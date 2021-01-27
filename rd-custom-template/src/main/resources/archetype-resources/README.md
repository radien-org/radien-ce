# radien Custom Template Management of Microservice

#prerequisite: 
mvn clean install

## run: 
java -jar target/${package}-service.jar

#local:
curl http://localhost:8084/${package}/template/models

## curls
#POST 
curl -d '{"id":1,"message":"Hello-1"}' -H 'Content-Type:application/json' http://localhost:8084/${package}/template/models
#GET data
curl -v http://localhost:8084/rd_ms_my/template/models/1
#PUT data
curl -d '{"id":1,"message":"Hello-3"}' -H 'Content-Type:application/json' -X PUT http://localhost:8084/${package}/template/models/1
#DELETE data
curl -X DELETE http://localhost:8084/${package}/template/models/1