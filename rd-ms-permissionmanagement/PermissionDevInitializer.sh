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
echo "---> I'm in the Permission Developer Initializer Script and getting the authentication"

ACCESS_TOKEN=$(curl -L -X POST $KEYCLOAK_IDP_URL$REALMS_TOKEN_PATH \
-H 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode $SCRIPT_CLIENT_ID=$SCRIPT_CLIENT_ID_VALUE \
--data-urlencode $SCRIPT_CLIENT_SECRET=$SCRIPT_CLIENT_SECRET_VALUE \
--data-urlencode $SCRIPT_REDIRECT_URL=$SCRIPT_REDIRECT_URL_VALUE \
--data-urlencode $SCRIPT_GRANT_TYPE=$SCRIPT_GRANT_TYPE_VALUE \
--data-urlencode $SCRIPT_USERNAME=$SCRIPT_USERNAME_VALUE \
--data-urlencode $SCRIPT_PASSWORD=$SCRIPT_PASSWORD_VALUE | jq -r '.access_token')

echo "---> Going to create 4 Permissions for the User Management Service"

## Permissions regarding User
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "User Management - Create", "actionId": 1, "resourceId": 1 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "User Management - Read", "actionId": 2, "resourceId": 1 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "User Management - Update", "actionId": 3, "resourceId": 1 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "User Management - Delete", "actionId": 4, "resourceId": 1 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "User Management - All", "actionId": 5, "resourceId": 1 }'

## Permissions regarding Roles
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Roles Management - Create", "actionId": 1, "resourceId": 2 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Roles Management - Read", "actionId": 2, "resourceId": 2 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Roles Management - Update", "actionId": 3, "resourceId": 2 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Roles Management - Delete", "actionId": 4, "resourceId": 2 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Roles Management - All", "actionId": 5, "resourceId": 2 }'

## Permissions regarding Permission
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Permission Management - Create", "actionId": 1, "resourceId": 3 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Permission Management - Read", "actionId": 2, "resourceId": 3 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Permission Management - Update", "actionId": 3, "resourceId": 3 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Permission Management - Delete", "actionId": 4, "resourceId": 3 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Permission Management - All", "actionId": 5, "resourceId": 3 }'

## Permissions regarding Resource
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Resource Management - Create", "actionId": 1, "resourceId": 4 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Resource Management - Read", "actionId": 2, "resourceId": 4 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Resource Management - Update", "actionId": 3, "resourceId": 4 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Resource Management - Delete", "actionId": 4, "resourceId": 4 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Resource Management - All", "actionId": 5, "resourceId": 4 }'

## Permissions regarding Action
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Action Management - Create", "actionId": 1, "resourceId": 5 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Action Management - Read", "actionId": 2, "resourceId": 5 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Action Management - Update", "actionId": 3, "resourceId": 5 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Action Management - Delete", "actionId": 4, "resourceId": 5 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Action Management - All", "actionId": 5, "resourceId": 5 }'

## Permissions regarding Tenant
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Tenant Management - Create", "actionId": 1, "resourceId": 6 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Tenant Management - Read", "actionId": 2, "resourceId": 6 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Tenant Management - Update", "actionId": 3, "resourceId": 6 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Tenant Management - Delete", "actionId": 4, "resourceId": 6 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Tenant Management - All", "actionId": 5, "resourceId": 6 }'

## Permissions regarding Tenant Role
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Tenant Role Management - Create", "actionId": 1, "resourceId": 7 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Tenant Role Management - Read", "actionId": 2, "resourceId": 7 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Tenant Role Management - Update", "actionId": 3, "resourceId": 7 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Tenant Role Management - Delete", "actionId": 4, "resourceId": 7 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Tenant Role Management - All", "actionId": 5, "resourceId": 7 }'

## Permissions regarding Tenant Role Permission
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Tenant Role Permission Management - Create", "actionId": 1, "resourceId": 8 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Tenant Role Permission Management - Read", "actionId": 2, "resourceId": 8 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Tenant Role Permission Management - Update", "actionId": 3, "resourceId": 8 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Tenant Role Permission Management - Delete", "actionId": 4, "resourceId": 8 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Tenant Role Permission Management - All", "actionId": 5, "resourceId": 8 }'

## Permissions regarding Tenant Role User
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Tenant Role User Management - Create", "actionId": 1, "resourceId": 9 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Tenant Role User Management - Read", "actionId": 2, "resourceId": 9 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Tenant Role User Management - Update", "actionId": 3, "resourceId": 9 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Tenant Role User Management - Delete", "actionId": 4, "resourceId": 9 }'
curl -L -X POST 'http://localhost:8080/permissionmanagementservice/v1/permission' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{ "name": "Tenant Role User Management - All", "actionId": 5, "resourceId": 9 }'

echo "-----------------------------------------------------------------------------------------------------------------"