#!/usr/bin/env bash
# Set up a temporary Mailpit instance at /mailhog in a Kubernetes CES.
# Usage: ./resource/setup_mailpit_multinode.sh <namespace> [https://ces-host]
# Optional environment variables: MAILPIT_IMAGE, KUBECTL_CONTEXT
set -euo pipefail

usage() {
    echo "Usage: $0 <ces-namespace> [https://ces-host]"
    echo "Example: $0 ecosystem https://ces.example.org"
}

if [[ ${1:-} == '-h' || ${1:-} == '--help' ]]; then
    usage
    exit 0
fi
if [[ $# -lt 1 || $# -gt 2 ]]; then
    usage >&2
    exit 1
fi

ces_namespace=$1
ces_url=${2:-}
mailpit_image=${MAILPIT_IMAGE:-axllent/mailpit:v1.27.4}
kubectl_cmd=(kubectl)
if [[ -n ${KUBECTL_CONTEXT:-} ]]; then
    kubectl_cmd+=(--context "$KUBECTL_CONTEXT")
fi
command -v kubectl >/dev/null || { echo 'kubectl is required.' >&2; exit 1; }
if [[ -n $ces_url ]]; then
    command -v curl >/dev/null || { echo 'curl is required for the external API check.' >&2; exit 1; }
    [[ $ces_url == https://* ]] || { echo 'The CES URL must start with https://.' >&2; exit 1; }
    ces_url=${ces_url%/}
fi

"${kubectl_cmd[@]}" get namespace "$ces_namespace" >/dev/null
"${kubectl_cmd[@]}" get crd expositions.k8s.cloudogu.com >/dev/null
# Avoid replacing an existing MailHog installation or another service.
for resource in deployment/mailhog service/mailhog exposition/mailhog; do
    owner=$("${kubectl_cmd[@]}" -n "$ces_namespace" get "$resource" --ignore-not-found \
        -o 'jsonpath={.metadata.labels.setup-mailpit-owner}')
    exists=$("${kubectl_cmd[@]}" -n "$ces_namespace" get "$resource" --ignore-not-found -o name)
    if [[ -n $exists && $owner != setup-mailpit-multinode ]]; then
        echo "Refusing to overwrite existing $resource without this script's ownership label." >&2
        exit 1
    fi
done

"${kubectl_cmd[@]}" -n "$ces_namespace" apply -f - <<YAML
apiVersion: apps/v1
kind: Deployment
metadata:
  name: mailhog
  labels:
    setup-mailpit-owner: setup-mailpit-multinode
spec:
  replicas: 1
  strategy:
    type: Recreate
  selector:
    matchLabels:
      app: mailhog
      setup-mailpit-owner: setup-mailpit-multinode
  template:
    metadata:
      labels:
        app: mailhog
        setup-mailpit-owner: setup-mailpit-multinode
    spec:
      containers:
        - name: mailpit
          image: "$mailpit_image"
          env:
            - name: MP_WEBROOT
              value: mailhog
          ports:
            - name: http
              containerPort: 8025
            - name: smtp
              containerPort: 1025
          readinessProbe:
            tcpSocket:
              port: http
            initialDelaySeconds: 2
            periodSeconds: 5
          resources:
            requests:
              cpu: 50m
              memory: 64Mi
            limits:
              memory: 256Mi
---
apiVersion: v1
kind: Service
metadata:
  name: mailhog
  labels:
    setup-mailpit-owner: setup-mailpit-multinode
spec:
  selector:
    app: mailhog
    setup-mailpit-owner: setup-mailpit-multinode
  ports:
    - name: http
      port: 8025
      targetPort: http
    - name: smtp
      port: 1025
      targetPort: smtp
---
apiVersion: k8s.cloudogu.com/v1
kind: Exposition
metadata:
  name: mailhog
  labels:
    setup-mailpit-owner: setup-mailpit-multinode
spec:
  http:
    - name: mailhog
      service: mailhog
      port: 8025
      path: /mailhog
YAML

"${kubectl_cmd[@]}" -n "$ces_namespace" rollout status deployment/mailhog --timeout=120s
"${kubectl_cmd[@]}" -n "$ces_namespace" get deployment/mailhog service/mailhog exposition/mailhog

if [[ -n $ces_url ]]; then
    echo "Checking $ces_url/mailhog/api/v1/messages (GET only; no emails are deleted)..."
    curl -k --fail-with-body --silent --show-error --connect-timeout 10 --max-time 15 \
        --retry 6 --retry-delay 5 --retry-all-errors \
        "$ces_url/mailhog/api/v1/messages"
    echo
fi

cat <<INFO
Mailpit is ready. Emails are temporary and are lost when the pod is replaced.
SMTP destination: mailhog.$ces_namespace.svc.cluster.local:1025
Configure your CES mail sender/Postfix relay to use this destination.
This script does not change the existing Postfix configuration or NetworkPolicies.
If NetworkPolicies restrict traffic, allow the CES proxy to port 8025 and the
mail sender to port 1025 on the Mailpit pod.
Cypress: MAILPIT_URL: \`\${config.baseUrl}/mailhog\`
INFO
