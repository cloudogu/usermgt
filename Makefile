# Set these to the desired values
ARTIFACT_ID=usermgt
VERSION=1.8.1-1
# overwrite ADDITIONAL_LDFLAGS to disable static compilation
# this should fix https://github.com/golang/go/issues/13470
ADDITIONAL_LDFLAGS=""
MAKEFILES_VERSION=4.2.0
.DEFAULT_GOAL:=default

include build/make/variables.mk
include build/make/self-update.mk
include build/make/release.mk
include bats.mk

default: dogu-release
