#!/bin/bash
set -o errexit
set -o nounset
set -o pipefail

#
function determinePwdMinLength() {
  PWD_MIN_LENGTH=$(doguctl config -default 1 --global password-policy/min_length)
  NUM_REGEX='^[0-9]+$'
  if ! [[ PWD_MIN_LENGTH =~ $NUM_REGEX ]]; then
     echo "Warning: Specified minimum length is not an integer; password minimum length is set to 1"
     PWD_MIN_LENGTH=1
  fi

  export PWD_MIN_LENGTH
}