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
echo "---> I'm in the Tenant Developer Initializer Script and getting the authentication"

ACCESS_TOKEN=$(curl -L -X POST $KEYCLOAK_IDP_URL$REALMS_TOKEN_PATH \
-H 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode $SCRIPT_CLIENT_ID=$SCRIPT_CLIENT_ID_VALUE \
--data-urlencode $SCRIPT_CLIENT_SECRET=$SCRIPT_CLIENT_SECRET_VALUE \
--data-urlencode $SCRIPT_REDIRECT_URL=$SCRIPT_REDIRECT_URL_VALUE \
--data-urlencode $SCRIPT_GRANT_TYPE=$SCRIPT_GRANT_TYPE_VALUE \
--data-urlencode $SCRIPT_USERNAME=$SCRIPT_USERNAME_VALUE \
--data-urlencode $SCRIPT_PASSWORD=$SCRIPT_PASSWORD_VALUE | jq -r '.access_token')

echo "---> Going to create a Root Tenant"

## Create ROOT Tenant
curl -L -X POST 'http://localhost:8082/tenantmanagementservice/v1/tenant' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{"tenantKey": "EVCorp", "name": "Root Tenant", "tenantType": "Root", "tenantStart": "2030-01-22", "tenantEnd": "2040-01-22"}'
curl -L -X POST 'http://localhost:8082/tenantmanagementservice/v1/tenant' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{"tenantKey": "EVCorp", "name": "Client Tenant", "tenantType": "Client", "tenantStart": "2030-01-22", "tenantEnd": "2040-01-22", "clientAddress": "Sophiestrasse 33", "clientZipCode": "38118", "clientCity":"Braunschweig", "clientCountry":"Germany", "clientPhoneNumber":933876547, "clientEmail":"email@email.com", "parentId":1, "clientId":1}'
curl -L -X POST 'http://localhost:8082/tenantmanagementservice/v1/tenant' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{"tenantKey": "EVCorp", "name": "Sub Tenant", "tenantType": "Sub", "tenantStart": "2030-01-22", "tenantEnd": "2040-01-22", "parentId":2, "clientId":2}'

echo "-----------------------------------------------------------------------------------------------------------------"