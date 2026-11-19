#!/usr/bin/env bash
# Set up a temporary Mailpit instance at /mailhog in a Kubernetes CES.
# Usage: ./resources/setup_mailpit_multinode.sh <namespace> [https://ces-host]
# Optional environment variables: MAILPIT_IMAGE, KUBECTL_CONTEXT
set -euo pipefail

usage() {
    echo "Usage: $0 <ces-namespace> [https://ces-host]"
    echo "Example: $0 ecosystem https://ces.example.org"
    echo "Requires kubectl and Python 3 with PyYAML. Routes Postfix mail to Mailpit."
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
smtp_destination=mailhog.$ces_namespace.svc.cluster.local:1025
mailpit_image=${MAILPIT_IMAGE:-axllent/mailpit:v1.27.4}
kubectl_cmd=(kubectl)
if [[ -n ${KUBECTL_CONTEXT:-} ]]; then
    kubectl_cmd+=(--context "$KUBECTL_CONTEXT")
fi
command -v kubectl >/dev/null || { echo 'kubectl is required.' >&2; exit 1; }
python3 -c 'import yaml' 2>/dev/null || { echo 'Python 3 with PyYAML is required.' >&2; exit 1; }
if [[ -n $ces_url ]]; then
    command -v curl >/dev/null || { echo 'curl is required for the external API check.' >&2; exit 1; }
    [[ $ces_url == https://* ]] || { echo 'The CES URL must start with https://.' >&2; exit 1; }
    ces_url=${ces_url%/}
fi

"${kubectl_cmd[@]}" get namespace "$ces_namespace" >/dev/null
"${kubectl_cmd[@]}" get crd expositions.k8s.cloudogu.com >/dev/null
"${kubectl_cmd[@]}" -n "$ces_namespace" get deployment/postfix configmap/postfix-config >/dev/null
# Avoid replacing an existing MailHog installation or another service.
for resource in deployment/mailhog service/mailhog exposition/mailhog \
    networkpolicy/mailpit-allow-smtp-from-postfix; do
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
---
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: mailpit-allow-smtp-from-postfix
  labels:
    setup-mailpit-owner: setup-mailpit-multinode
spec:
  podSelector:
    matchLabels:
      app: mailhog
      setup-mailpit-owner: setup-mailpit-multinode
  policyTypes:
    - Ingress
  ingress:
    - from:
        - podSelector:
            matchLabels:
              dogu.name: postfix
      ports:
        - protocol: TCP
          port: 1025
YAML

"${kubectl_cmd[@]}" -n "$ces_namespace" rollout status deployment/mailhog --timeout=120s
"${kubectl_cmd[@]}" -n "$ces_namespace" get deployment/mailhog service/mailhog exposition/mailhog \
    networkpolicy/mailpit-allow-smtp-from-postfix

# The Dogu reads relayhost from config.yaml on startup.
postfix_config=$("${kubectl_cmd[@]}" -n "$ces_namespace" get configmap/postfix-config -o json)
relay_patch=$(python3 -c '
import json
import sys
import yaml

configmap = json.load(sys.stdin)
config = yaml.safe_load(configmap.get("data", {}).get("config.yaml", ""))
if config is None:
    config = {}
if not isinstance(config, dict):
    sys.exit("Expected a mapping in postfix-config config.yaml.")
if config.get("relayhost") != sys.argv[1]:
    config["relayhost"] = sys.argv[1]
    # Reject concurrent writes to the ConfigMap instead of replacing their changes.
    print(json.dumps({
        "metadata": {"resourceVersion": configmap["metadata"]["resourceVersion"]},
        "data": {"config.yaml": yaml.safe_dump(config, sort_keys=False)},
    }))
' "$smtp_destination" <<< "$postfix_config")
if [[ -n $relay_patch ]]; then
    "${kubectl_cmd[@]}" -n "$ces_namespace" patch configmap postfix-config \
        --type merge --patch-file /dev/stdin <<< "$relay_patch"
fi

# Apply the relay to the running Postfix without replacing its pod.
"${kubectl_cmd[@]}" -n "$ces_namespace" rollout status deployment/postfix --timeout=120s
active_relay=$("${kubectl_cmd[@]}" -n "$ces_namespace" exec deployment/postfix -- postconf -h relayhost)
if [[ $active_relay != "$smtp_destination" ]]; then
    "${kubectl_cmd[@]}" -n "$ces_namespace" exec deployment/postfix -- \
        postconf -e "relayhost=$smtp_destination"
fi
"${kubectl_cmd[@]}" -n "$ces_namespace" exec deployment/postfix -- postfix reload

if [[ -n $ces_url ]]; then
    echo "Checking $ces_url/mailhog/api/v1/messages (GET only; no emails are deleted)..."
    curl -k --fail-with-body --silent --show-error --connect-timeout 10 --max-time 15 \
        --retry 6 --retry-delay 5 --retry-all-errors \
        "$ces_url/mailhog/api/v1/messages"
    echo
fi

cat <<INFO
Mailpit is ready. Emails are temporary and are lost when the pod is replaced.
Postfix relay: $smtp_destination
The NetworkPolicy allows Postfix to reach Mailpit on TCP port 1025.
If NetworkPolicies restrict web access, allow the CES proxy to reach Mailpit on port 8025.
Cypress: MAILPIT_URL: \`\${config.baseUrl}/mailhog\`
INFO
