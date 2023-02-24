#!/usr/bin/env python3

import requests
import urllib3
import json
import sys
from requests.auth import HTTPBasicAuth

urllib3.disable_warnings()

url = 'https://localhost/usermgt/api/groups'
basic = HTTPBasicAuth('gary', 'admin')
headers = {"Content-Type": "application/json; charset=UTF-8"}

def create_group(id):
    group = f'group{id}'
    return {
	"name": group,
	"description": f'this is group {group}',
    }

count = 5
if len(sys.argv) > 1:
    count = int(sys.argv[1])
print(f'create {count} groups(s)')
print("generating group data")
groups = []
for id in range(count):
    groups.append(create_group(id))

for group in groups:
    print(f'create group: {json.dumps(group)}')
    response = requests.request(
        "POST",
        url,
        data = json.dumps(group),
        headers = headers,
        verify=False,
        auth=basic
    )
    if response.status_code != 201:
        print(f'error: {response.status_code}')
