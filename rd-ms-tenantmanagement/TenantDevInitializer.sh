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

ACCESS_TOKEN=$(curl -L -X POST 'https://idp-int.radien.io/auth/realms/radien/protocol/openid-connect/token' \
-H 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'client_id=radien' \
--data-urlencode 'client_secret=d8f67579-1a33-47be-ad6f-aef38269ed12' \
--data-urlencode 'redirect_uri=https://localhost:8443/web/login' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'username=n.santana-username' \
--data-urlencode 'password=batata' | jq -r '.access_token')

echo "---> Going to create a Root Tenant"

## Create ROOT Tenant
curl -L -X POST 'http://localhost:8080/tenantmanagementservice/v1/tenant' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{"tenantKey": "EVCorp", "name": "Root Tenant", "tenantType": "Root", "tenantStart": "2030-01-22", "tenantEnd": "2040-01-22"}'
curl -L -X POST 'http://localhost:8080/tenantmanagementservice/v1/tenant' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{"tenantKey": "EVCorp", "name": "Client Tenant", "tenantType": "Client", "tenantStart": "2030-01-22", "tenantEnd": "2040-01-22", "clientAddress": "Sophiestrasse 33", "clientZipCode": "38118", "clientCity":"Braunschweig", "clientCountry":"Germany", "clientPhoneNumber":933876547, "clientEmail":"email@email.com", "parentId":1, "clientId":1}'
curl -L -X POST 'http://localhost:8080/tenantmanagementservice/v1/tenant' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{"tenantKey": "EVCorp", "name": "Sub Tenant", "tenantType": "Sub", "tenantStart": "2030-01-22", "tenantEnd": "2040-01-22", "parentId":2, "clientId":2}'

echo "-----------------------------------------------------------------------------------------------------------------"