#!/bin/bash

case "$1" in
  run)
    docker run -p 1389:389 -d --name ldap scmmu/ldap
    docker run -p 8443:8443 -d --name cas --link ldap:ldap scmmu/cas
    ;;
  rm)
    docker rm -f ldap
    docker rm -f cas
    ;;
  start)
    docker start ldap
    docker start cas
    ;;
  stop)
    docker stop ldap
    docker stop cas
    ;;
  *)
    echo "Usage: $0 {run|rm|start|stop}"
		exit 1
		;;
esac

exit 0
