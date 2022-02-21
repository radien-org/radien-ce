call rebuildKeycloakImage.bat
call rebuildTomeeImage.bat
docker run -d -p 8180:8080 -p 8343:8443 -e KEYCLOAK_USER=raiden -e KEYCLOAK_PASSWORD=brutality -v %CD%/keycloak/local:/tmp --name rkc radien-keycloak
docker run -d -p 8080:8080 -p 8443:8443 -p 8000:8000 -v %CD%/tomee/local/remoteTomee:/usr/local/tomee/logs --name radien-tomee1 radien-tomee
mvn -f initializer/pom.xml package
timeout /t 60
start /b java -jar initializer/target/initializer-jar-with-dependencies.jar
cd ../..