#!/bin/sh

# Copyright (c) 2021-present radien GmbH. All rights reserved.
# <p>
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
# <p>
# http://www.apache.org/licenses/LICENSE-2.0
# <p>
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.Ku

#to run this shell script you need jq (is a lightweight and flexible command-line JSON processor)
# you can find jq at https://github.com/stedolan/jq
# its available in package manager like brew (https://brew.sh/)
if ! command -v jq &> /dev/null
then
    echo "to run this shell script you need jq (is a lightweight and flexible command-line JSON processor)"
    echo "you can find jq at https://github.com/stedolan/jq"
    echo "its available in package manager like brew (https://brew.sh/)"
    echo "so if you have brew installed you can install jq with 'brew install jq'"
    exit
fi

echo "-----------------------------------------------------------------------------------------------------------------"
echo "---> I'm in the Role Developer Initializer Script and getting the authentication"

ACCESS_TOKEN=$(curl -L -X POST $KEYCLOAK_IDP_URL$REALMS_TOKEN_PATH \
-H 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode $SCRIPT_CLIENT_ID=$SCRIPT_CLIENT_ID_VALUE \
--data-urlencode $SCRIPT_CLIENT_SECRET=$SCRIPT_CLIENT_SECRET_VALUE \
--data-urlencode $SCRIPT_REDIRECT_URL=$SCRIPT_REDIRECT_URL_VALUE \
--data-urlencode $SCRIPT_GRANT_TYPE=$SCRIPT_GRANT_TYPE_VALUE \
--data-urlencode $SCRIPT_USERNAME=$SCRIPT_USERNAME_VALUE \
--data-urlencode $SCRIPT_PASSWORD=$SCRIPT_PASSWORD_VALUE | jq -r '.access_token')

echo "---> Going to create the admin role to be used and added to the user"

## Create a specific role
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/role' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "System Administrator", "description": "The BOSS!"}'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/role' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Permission Administrator", "description": "The Role for Permission Management Testing purposes!"}'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/role' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Role Administrator", "description": "The Role for Role Management Testing purposes!"}'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/role' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "User Administrator", "description": "The Role for User Management Testing purposes!"}'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/role' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Tenant Administrator", "description": "The Role for Tenant Management Testing purposes!"}'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/role' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Client Tenant Administrator", "description": "The Role for the Client Tenant administrators!"}'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/role' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Sub Tenant Administrator", "description": "The Role for the Sub Tenant administrators!"}'

curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/role' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Guest", "description": "The Role for Guests!"}'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/role' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Approver", "description": "The Role for users that only approve data!"'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/role' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Author", "description": "The Role for users that only create data!"}'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/role' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Publisher", "description": "The Role for the publishers!"}'


echo "-----------------------------------------------------------------------------------------------------------------"