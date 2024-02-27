# Set these to the desired values
ARTIFACT_ID=usermgt
VERSION=1.12.1-1
# overwrite ADDITIONAL_LDFLAGS to disable static compilation
# this should fix https://github.com/golang/go/issues/13470
ADDITIONAL_LDFLAGS=""
NPM_REGISTRY_RELEASE=https://ecosystem.cloudogu.com/nexus/repository/npm-releases/
NPM_REGISTRY_RC=https://ecosystem.cloudogu.com/nexus/repository/npm-releasecandidates/
UI_SRC=app/src/main/ui
MAKEFILES_VERSION=9.0.1
.DEFAULT_GOAL:=default

include build/make/variables.mk
include build/make/self-update.mk
include build/make/release.mk
include build/make/bats.mk

default: dogu-release

