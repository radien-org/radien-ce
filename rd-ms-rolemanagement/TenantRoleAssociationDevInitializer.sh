#!/bin/sh
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
echo "---> I'm in the Tenant Role Associations Developer Initializer Script and getting the authentication"

ACCESS_TOKEN=$(curl -L -X POST $KEYCLOAK_IDP_URL$REALMS_TOKEN_PATH \
-H 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode $SCRIPT_CLIENT_ID=$SCRIPT_CLIENT_ID_VALUE \
--data-urlencode $SCRIPT_CLIENT_SECRET=$SCRIPT_CLIENT_SECRET_VALUE \
--data-urlencode $SCRIPT_REDIRECT_URL=$SCRIPT_REDIRECT_URL_VALUE \
--data-urlencode $SCRIPT_GRANT_TYPE=$SCRIPT_GRANT_TYPE_VALUE \
--data-urlencode $SCRIPT_USERNAME=$SCRIPT_USERNAME_VALUE \
--data-urlencode $SCRIPT_PASSWORD=$SCRIPT_PASSWORD_VALUE | jq -r '.access_token')

echo "---> Going to create association between Tenant Root and Role Admin"
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/tenantrole' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{"tenantId":1, "roleId":1}'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/tenantrole' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{"tenantId":2, "roleId":1}'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/tenantrole' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{"tenantId":3, "roleId":1}'

echo "---> Going to associate the already created 4 permissions"
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/tenantrole/assign/permission/1/tenant/1/role/1' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/tenantrole/assign/permission/2/tenant/1/role/1' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/tenantrole/assign/permission/3/tenant/1/role/1' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/tenantrole/assign/permission/4/tenant/1/role/1' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json'

curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/tenantrole/assign/permission/4/tenant/2/role/1' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/tenantrole/assign/permission/4/tenant/3/role/1' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json'

echo "---> Going to associate the first user"
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/tenantrole/assign/user/1/tenant/1/role/1' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/tenantrole/assign/user/1/tenant/2/role/1' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/tenantrole/assign/user/1/tenant/3/role/1' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json'

echo "-----------------------------------------------------------------------------------------------------------------"