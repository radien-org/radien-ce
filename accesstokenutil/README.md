Helper util to fetch access token from Keycloak
Instructions for Build and Execute:

mvn package
java -jar accessTokenUtil-jar-with-dependencies.jar https://idp-int.radien.io/auth/realms/radien/protocol/openid-connect/token radien XXXXX-YYYY-ZZZZ-WWWW-ZSE123 username password