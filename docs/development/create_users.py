#!/usr/bin/env python3

import requests
import urllib3
import json
import sys
from requests.auth import HTTPBasicAuth

urllib3.disable_warnings()

url = 'https://localhost/usermgt/api/users'
basic = HTTPBasicAuth('gary', 'admin')
headers = {"Content-Type": "application/json; charset=UTF-8"}

def create_user(id):
    user = f'user{id}'
    mail = f'{user}@cloudogu.com'
    return {
	"username": user,
	"givenname": user,
	"surname": user,
	"displayName": user,
	"mail": mail,
        "password": "admin",
        "pwdReset": False,
        "memberOf": []
    }

user_count = 5
if len(sys.argv) > 1:
    user_count = int(sys.argv[1])
print(f'create {user_count} user(s)')
print("generating user data")
users = []
for id in range(user_count):
    users.append(create_user(id))

for user in users:
    print(f'create user: {json.dumps(user)}')
    response = requests.request(
        "POST",
        url,
        data = json.dumps(user),
        headers = headers,
        verify=False,
        auth=basic
    )
    if response.status_code != 201:
        print(f'error: {response.status_code}')
