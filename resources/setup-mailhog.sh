#!/bin/bash
set -o errexit
set -o nounset
set -o pipefail

etcdctl set dogu/mailhog/1.0.0-1 '{"Name": "official/mailhog","Version": "1.0.0-1","DisplayName": "Mailhog","Description": "Mailhog Dogu","Category": "Administration Apps","Tags": ["webapp","warp"],"Logo": "","URL": "https://github.com/cloudogu","Image": "registry.cloudogu.com/official/mailhog","Dependencies": ["nginx"],"OptionalDependencies": null}'
etcdctl set dogu/mailhog/current 1.0.0-1
sudo docker run -d -e MH_UI_WEB_PATH=mailhog -p 25:1025 -p 8025:8025 --name mailhog --net cesnet1 mailhog/mailhog
MAILHOG_IP=$(sudo docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' mailhog)
etcdctl set services/mailhog/registrator:mailhog:8025 "{\"name\":\"mailhog\",\"service\":\"${MAILHOG_IP}:8025\",\"port\":\"8025\",\"tags\":[\"webapp\"],\"healthStatus\":\"healthy\",\"attributes\":{}}"
etcdctl set /config/postfix/relayhost mailhog:1025
sudo docker restart postfix
