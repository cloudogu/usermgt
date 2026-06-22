# Set these to the desired values
ARTIFACT_ID=usermgt

VERSION=1.21.0-4
# overwrite ADDITIONAL_LDFLAGS to disable static compilation
# this should fix https://github.com/golang/go/issues/13470
ADDITIONAL_LDFLAGS=""
NPM_REGISTRY_RELEASE=https://ecosystem.cloudogu.com/nexus/repository/npm-releases/
NPM_REGISTRY_RC=https://ecosystem.cloudogu.com/nexus/repository/npm-releasecandidates/
UI_SRC=app/src/main/ui
MAKEFILES_VERSION=10.7.1
.DEFAULT_GOAL:=default

BINARY_HELM_VERSION?=v3.20.0
HELM_SOURCE_DIR=k8s/helm

K8S_COMPONENT_SOURCE_VALUES = ${HELM_SOURCE_DIR}/values.yaml
K8S_COMPONENT_TARGET_VALUES = ${HELM_TARGET_DIR}/values.yaml
HELM_PRE_GENERATE_TARGETS = helm-values-update-image-version helm-copy-dogu-spec
HELM_POST_GENERATE_TARGETS = helm-values-replace-image-repo template-image-pull-policy
IMAGE_IMPORT_TARGET=image-import

include build/make/variables.mk
include build/make/self-update.mk
include build/make/release.mk
include build/make/prerelease.mk
include build/make/bats.mk
include build/make/k8s-dogu.mk
include build/make/k8s-component.mk
include build/make/k8s.mk

BATS_TAG=1.13.0

default: dogu-release

.PHONY info:
info:
	@echo Generating .npmrc file
	@echo This will overwrite the existing file including the previosly generated credentials

.PHONY gen-npmrc-release:
gen-npmrc-release: info
	@rm -f ${UI_SRC}/.npmrc
	@echo "email=jenkins@cloudogu.com" >> ${UI_SRC}/.npmrc
	@echo "always-auth=true" >> ${UI_SRC}/.npmrc
	@echo "_auth=$(shell bash -c 'read -p "Username: " usrname;read -s -p "Password: " pwd;echo -n "$$usrname:$$pwd" | openssl base64')" >> ${UI_SRC}/.npmrc
	@echo "@cloudogu:registry=${NPM_REGISTRY_RELEASE}" >> ${UI_SRC}/.npmrc

.PHONY gen-npmrc-prerelease:
gen-npmrc-prerelease: info
	@rm -f ${UI_SRC}/.npmrc
	@echo "email=jenkins@cloudogu.com" >> ${UI_SRC}/.npmrc
	@echo "always-auth=true" >> ${UI_SRC}/.npmrc
	@echo "_auth=$(shell bash -c 'read -p "Username: " usrname;read -s -p "Password: " pwd;echo -n "$$usrname:$$pwd" | openssl base64')" >> ${UI_SRC}/.npmrc
	@echo "@cloudogu:registry=${NPM_REGISTRY_RC}" >> ${UI_SRC}/.npmrc

.PHONY: helm-values-update-image-version
helm-values-update-image-version: $(BINARY_YQ)
	@echo "Updating the image version in source values.yaml to ${VERSION}..."
	@$(BINARY_YQ) -i e ".image.tag = \"${VERSION}\"" ${K8S_COMPONENT_SOURCE_VALUES}

.PHONY: helm-copy-dogu-spec
helm-copy-dogu-spec:
	@echo "Copy dogu.json to ${HELM_SOURCE_DIR}..."
	@cp dogu.json ${HELM_SOURCE_DIR}

.PHONY: helm-values-replace-image-repo
helm-values-replace-image-repo: $(BINARY_YQ)
	@if [[ ${STAGE} == "development" ]]; then \
      		echo "Setting dev image repo in target values.yaml!" ;\
    		$(BINARY_YQ) -i e ".image.registry=\"$(shell echo '${IMAGE_DEV}' | sed 's/\([^\/]*\)\/\(.*\)/\1/')\"" ${K8S_COMPONENT_TARGET_VALUES} ;\
    		$(BINARY_YQ) -i e ".image.repository=\"$(shell echo '${IMAGE_DEV}' | sed 's/\([^\/]*\)\/\(.*\)/\2/')\"" ${K8S_COMPONENT_TARGET_VALUES} ;\
    fi

.PHONY: template-image-pull-policy
template-image-pull-policy: $(BINARY_YQ)
	@if [[ "${STAGE}" == "development" ]]; then \
          echo "Setting pull policy to always!" ; \
          $(BINARY_YQ) -i e ".imagePullPolicy=\"Always\"" "${K8S_COMPONENT_TARGET_VALUES}" ; \
    fi


.PHONY: kill-pod
kill-pod:
	@echo "Restarting ${ARTIFACT_ID} Dogu!"
	@kubectl -n ${NAMESPACE} delete pods -l "dogu.name=${ARTIFACT_ID}"
