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
echo "---> I'm in the Linked Authorization Developer Initializer Script and getting the authentication"

ACCESS_TOKEN=$(curl -L -X POST $KEYCLOAK_IDP_URL$REALMS_TOKEN_PATH \
-H 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode $SCRIPT_CLIENT_ID=$SCRIPT_CLIENT_ID_VALUE \
--data-urlencode $SCRIPT_CLIENT_SECRET=$SCRIPT_CLIENT_SECRET_VALUE \
--data-urlencode $SCRIPT_REDIRECT_URL=$SCRIPT_REDIRECT_URL_VALUE \
--data-urlencode $SCRIPT_GRANT_TYPE=$SCRIPT_GRANT_TYPE_VALUE \
--data-urlencode $SCRIPT_USERNAME=$SCRIPT_USERNAME_VALUE \
--data-urlencode $SCRIPT_PASSWORD=$SCRIPT_PASSWORD_VALUE | jq -r '.access_token')

echo "---> Going to add to the user, the admin role with the already created 4 permissions"

## Create a association between the tenant, the role, the 4 permission and the user
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/linkedauthorization' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{"tenantId":1,"permissionId":1, "roleId":1, "userId":1}'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/linkedauthorization' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{"tenantId":1,"permissionId":2, "roleId":1, "userId":1}'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/linkedauthorization' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{"tenantId":1,"permissionId":3, "roleId":1, "userId":1}'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/linkedauthorization' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{"tenantId":1,"permissionId":4, "roleId":1, "userId":1}'

curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/linkedauthorization' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{"tenantId":2,"permissionId":4, "roleId":1, "userId":1}'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/linkedauthorization' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{"tenantId":3,"permissionId":4, "roleId":1, "userId":1}'

echo "-----------------------------------------------------------------------------------------------------------------"