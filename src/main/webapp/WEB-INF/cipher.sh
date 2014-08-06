#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
java -cp "$DIR/lib/*:$DIR/classes" de.triology.universeadm.Cipher $@
