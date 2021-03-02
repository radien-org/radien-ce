#!/bin/sh
ACCESS_TOKEN=$(curl -L -X POST 'https://idp-int.radien.io/auth/realms/radien/protocol/openid-connect/token' \                                                                                                                                                                                                                                               17:42:27
-H 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'client_id=radien' \
--data-urlencode 'client_secret=d8f67579-1a33-47be-ad6f-aef38269ed12' \
--data-urlencode 'redirect_uri=https://localhost:8443/web/login' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'username=n.santana-username' \
--data-urlencode 'password=batata' | jq -r '.access_token')

curl -L -X GET 'http://localhost:8081/usermanagementservice/v1/user' -H "Authorization:Bearer "$ACCESS_TOKEN

