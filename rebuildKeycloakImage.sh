docker stop rkc
docker rm rkc
docker rmi radien-keycloak
docker build -t radien-keycloak -f keycloak/local/Dockerfile .
