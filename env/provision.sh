#!/bin/bash

# exit when a command fails
set -e
set -o pipefail

# docker
echo Installing Docker...
wget -qO- https://get.docker.io/gpg | apt-key add -
echo deb http://get.docker.io/ubuntu docker main > /etc/apt/sources.list.d/docker.list
apt-get update
apt-get install linux-image-extra-$(uname -r) lxc-docker -y -q
echo Docker installed...

# ldap
echo build and run ldap container...
docker build -t scmmu/ldap /vagrant/env/docker/ldap
docker run -p 1389:389 -d --name ldap scmmu/ldap
echo ldap container is ready

# cas
echo build cas container...
docker build -t scmmu/cas /vagrant/env/docker/cas
docker run -p 8443:8443 -d --name cas --link ldap:ldap scmmu/cas
echo cas container is ready

# upstart
cp /vagrant/env/upstart/*.conf /etc/init
# echo starting containers
#initctl ldap start
#initctl cas start
