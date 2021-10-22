# radien Enterprise Content Management Microservice

#prerequisite: 
mongo db has to run under
http://link_to_mongo_instance

## package: 
mvn package

## run: 
java -jar target/rd-ms-ecm-service.jar

## curls

#GET data

#microprofile:
http://localhost:8085/ecm/models

#local:
curl http://localhost:8085/rd-ms-ecm/ecm/models
#byViewId
curl http://localhost:8085/rd-ms-ecm/ecm/content/default-content


#POST 
#microprofile
curl -H "Content-Type: application/json" -X POST -d '{"id": "test-content", "message": "<h1>Enterprise Content</h1><p>this is the first HTML content</p>"}' http://localhost:8085/ecm/models

#local
curl -H "Content-Type: application/json" -X POST -d '{"viewId": "test-content", "htmlContent": "<h1>Enterprise Content</h1><p>this is the first HTML content</p>"}' http://localhost:8085/rd-ms-ecm/ecm/content
curl http://localhost:8085/rd-ms-ecm/ecm/content/test-content

#PUT data

#DELETE data