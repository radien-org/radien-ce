# radien Enterprise Content Management Microservice

@andres sousa: documentation goes here

#prerequisite: 
mongo db has to run under
http://link_to_mongo_instance

## package: 
mvn package

## run: 
java -jar target/rd-ms-usermanagement-service.jar

## curls

#GET data

#microprofile:
curl http://localhost:8080/usermagagement/models

#local:
curl http://localhost:8080/rd-ms-usermanagement/usermanagement/user


#POST 
#microprofile
curl -H "Content-Type: application/json" -X POST -d '{"id": "1", "message": "this is the model"}' http://localhost:8080/template/models

#local
curl -H "Content-Type: application/json" -X POST -d '{"id": "1", "message": "this is the model"}' http://localhost:8080/rd-ms-template/template/models

#PUT data

#DELETE data