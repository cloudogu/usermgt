#!/bin/bash
# start ldap
docker run -p 1389:389 -t -i scmmu/ldap
