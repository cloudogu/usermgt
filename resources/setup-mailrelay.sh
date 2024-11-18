#!/bin/bash
set -o errexit
set -o nounset
set -o pipefail

cd /dogu/resources
mkdir mailrelay
cd mailrelay
curl -L -o emailrelay.zip https://sourceforge.net/projects/emailrelay/files/emailrelay/2.6/emailrelay-2.6-src.zip/download
unzip emailrelay.zip
sudo ./configure && sudo make && sudo make install -y
echo "server plain ces-admin Ecosystem2016!" > secret.auth
sudo emailrelay -t --as-server --forward-on-disconnect --log --verbose --log-file mailrelay.log --log-time --port 587 --forward-to localhost:1025 --server-auth ./secret.auth
etcdctl set /config/postfix/relayhost 192.168.56.1:587
etcdctl set /config/postfix/sasl_username ces-admin
etcdctl set /config/postfix/sasl_password Ecosystem2016!
