# Set these to the desired values
ARTIFACT_ID=usermgt
VERSION=1.20.0-5
# overwrite ADDITIONAL_LDFLAGS to disable static compilation
# this should fix https://github.com/golang/go/issues/13470
ADDITIONAL_LDFLAGS=""
NPM_REGISTRY_RELEASE=https://ecosystem.cloudogu.com/nexus/repository/npm-releases/
NPM_REGISTRY_RC=https://ecosystem.cloudogu.com/nexus/repository/npm-releasecandidates/
UI_SRC=app/src/main/ui
MAKEFILES_VERSION=9.9.0
.DEFAULT_GOAL:=default

include build/make/variables.mk
include build/make/self-update.mk
include build/make/release.mk
include build/make/prerelease.mk
include build/make/bats.mk
include build/make/k8s-dogu.mk

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
