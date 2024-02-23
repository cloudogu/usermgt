#!/bin/bash
set -o errexit
set -o nounset
set -o pipefail

#
function determinePwdMinLength() {
  PWD_MIN_LENGTH=$(doguctl config -default 1 --global password-policy/min_length)
  NUM_REGEX='^[0-9]+$'

  echo $PWD_MIN_LENGTH

  if ! [[ $PWD_MIN_LENGTH =~ $NUM_REGEX ]]; then
     echo "Warning: Specified minimum length is not an integer; password minimum length is set to 1"
     PWD_MIN_LENGTH=1
  fi

  if [ "$PWD_MIN_LENGTH" -lt "1" ];
    then
      echo "Warning: Password minimum length is configured as less than 1; password minimum length is set to 1"
      PWD_MIN_LENGTH=1
  fi

  echo "The minimum password length is ${PWD_MIN_LENGTH}"
  export PWD_MIN_LENGTH
}