mvn clean install -DskipTests
docker stop radien-tomee1
docker rm radien-tomee1
docker rmi radien-tomee
docker build -t radien-tomee -f tomee/local/Dockerfile .
