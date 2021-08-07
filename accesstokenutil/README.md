Helper util to fetch access token from Keycloak
Instructions for Build and Execute:

1 - for build
mvn package

2 - for execute
java -jar accessTokenUtil-jar-with-dependencies.jar <keycloak-token-endpoint-url> <client-id> <client-secret> <username> <password>

Where:
<keycloak-token-endpoint-url> => https://idp-int.radien.io/auth/realms/radien/protocol/openid-connect/token
<client-id> => radien
<client-secret> => keycloak client secret
<username> => keycloak username
<password> => keycloak password

example:
java -jar accessTokenUtil-jar-with-dependencies.jar https://idp-int.radien.io/auth/realms/radien/protocol/openid-connect/token radien XXXXX-YYYY-ZZZZ-WWWW-ZSE123 username password