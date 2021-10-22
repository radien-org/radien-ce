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
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/role' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "System Administrator", "description": "The BOSS!", "terminationDate": "2030-12-12T00:00:00" }'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/role' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Permission Administrator", "description": "The Role for Permission Management Testing purposes!", "terminationDate": "2030-12-12T00:00:00" }'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/role' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Role Administrator", "description": "The Role for Role Management Testing purposes!", "terminationDate": "2030-12-12T00:00:00" }'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/role' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "User Administrator", "description": "The Role for User Management Testing purposes!", "terminationDate": "2030-12-12T00:00:00" }'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/role' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Tenant Administrator", "description": "The Role for Tenant Management Testing purposes!", "terminationDate": "2030-12-12T00:00:00" }'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/role' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Client Tenant Administrator", "description": "The Role for the Client Tenant administrators!", "terminationDate": "2030-12-12T00:00:00" }'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/role' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Sub Tenant Administrator", "description": "The Role for the Sub Tenant administrators!", "terminationDate": "2030-12-12T00:00:00" }'

curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/role' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Guest", "description": "The Role for Guests!", "terminationDate": "2030-12-12T00:00:00" }'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/role' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Approver", "description": "The Role for users that only approve data!", "terminationDate": "2030-12-12T00:00:00" }'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/role' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Author", "description": "The Role for users that only create data!", "terminationDate": "2030-12-12T00:00:00" }'
curl -L -X POST 'http://localhost:8080/rolemanagementservice/v1/role' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Publisher", "description": "The Role for the publishers!", "terminationDate": "2030-12-12T00:00:00" }'


echo "-----------------------------------------------------------------------------------------------------------------"