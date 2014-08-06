#!/bin/bash
EXPLODEDWAR="$1"
shift
if [ -d "$EXPLODEDWAR" ]; then
  java -cp "$EXPLODEDWAR/WEB-INF/lib/*:$EXPLODEDWAR/WEB-INF/classes" de.triology.universeadm.Cipher $@
fi
