# radien

## package: 
mvn package

## run: 
java -jar target/rd-microservice-template-service.jar

## curls

#retrieve
curl http://localhost:9080/rd-microservice-template/models
#tomee:
curl http://localhost:8080/rd-microservice-template/models

#add a model
curl -H "Content-Type: application/json" -X POST -d '{"id": "1", "message": "welcome to the microprofile"}' http://localhost:9080/rd-microservice-template/models