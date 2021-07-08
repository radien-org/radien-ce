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
echo "---> I'm in the Resource Developer Initializer Script and getting the authentication"

ACCESS_TOKEN=$(curl -L -X POST $KEYCLOAK_IDP_URL$REALMS_TOKEN_PATH \
-H 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode $SCRIPT_CLIENT_ID=$SCRIPT_CLIENT_ID_VALUE \
--data-urlencode $SCRIPT_CLIENT_SECRET=$SCRIPT_CLIENT_SECRET_VALUE \
--data-urlencode $SCRIPT_REDIRECT_URL=$SCRIPT_REDIRECT_URL_VALUE \
--data-urlencode $SCRIPT_GRANT_TYPE=$SCRIPT_GRANT_TYPE_VALUE \
--data-urlencode $SCRIPT_USERNAME=$SCRIPT_USERNAME_VALUE \
--data-urlencode $SCRIPT_PASSWORD=$SCRIPT_PASSWORD_VALUE | jq -r '.access_token')

echo "---> Going to create 4 resources, one for each service"

## Create the 4 basic resources
curl -L -X POST 'http://localhost:8085/permissionmanagementservice/v1/resource' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "User Management" }'
curl -L -X POST 'http://localhost:8085/permissionmanagementservice/v1/resource' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Permission Management" }'
curl -L -X POST 'http://localhost:8085/permissionmanagementservice/v1/resource' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Role Management" }'
curl -L -X POST 'http://localhost:8085/permissionmanagementservice/v1/resource' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Tenant Management" }'

echo "-----------------------------------------------------------------------------------------------------------------"