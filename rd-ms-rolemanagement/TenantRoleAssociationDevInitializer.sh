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

ACCESS_TOKEN=$(curl -L -X POST 'https://idp-int.radien.io/auth/realms/radien/protocol/openid-connect/token' \
-H 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'client_id=radien' \
--data-urlencode 'client_secret=d8f67579-1a33-47be-ad6f-aef38269ed12' \
--data-urlencode 'redirect_uri=https://localhost:8443/web/login' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'username=n.santana-username' \
--data-urlencode 'password=batata' | jq -r '.access_token')

echo "---> Going to create association between Tenant Root and Role Admin"
curl -L -X POST 'http://localhost:8083/rolemanagementservice/v1/tenantrole' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json' --data-raw '{"tenantId":1, "roleId":1}'

echo "---> Going to associate the already created 4 permissions"
curl -L -X POST 'http://localhost:8083/rolemanagementservice/v1/tenantrole/assign/permission/1/tenant/1/role/1' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json'
curl -L -X POST 'http://localhost:8083/rolemanagementservice/v1/tenantrole/assign/permission/2/tenant/1/role/1' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json'
curl -L -X POST 'http://localhost:8083/rolemanagementservice/v1/tenantrole/assign/permission/3/tenant/1/role/1' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json'
curl -L -X POST 'http://localhost:8083/rolemanagementservice/v1/tenantrole/assign/permission/4/tenant/1/role/1' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json'

echo "---> Going to associate the first user"
curl -L -X POST 'http://localhost:8083/rolemanagementservice/v1/tenantrole/assign/user/1/tenant/1/role/1' -H 'Authorization: Bearer '$ACCESS_TOKEN -H 'Content-Type: application/json'

echo "-----------------------------------------------------------------------------------------------------------------"