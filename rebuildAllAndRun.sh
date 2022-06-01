./rebuildKeycloakImage.sh
./rebuildTomeeImage.sh
docker run -d -p 8180:8080 -p 8343:8443 -e KEYCLOAK_USER=raiden -e KEYCLOAK_PASSWORD=brutality -v $(pwd)/keycloak/local:/tmp --name rkc radien-keycloak
docker run -d -p 1025:1025 -p 8025:8025 --name dev-mailhog mailhog/mailhog
docker run -d -p 8080:8080 -p 8443:8443 -p 8000:8000 -v $(pwd)/tomee/local/remoteTomee:/usr/local/tomee/logs --name radien-tomee1 radien-tomee
mvn -f initializer/pom.xml package
sleep 60
java -jar initializer/target/initializer-jar-with-dependencies.jar &
cd ../..

