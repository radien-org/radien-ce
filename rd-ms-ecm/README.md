# radien Enterprise Content Management Microservice

@andres sousa: documentation goes here

#prerequisite: 
mongo db has to run under
http://link_to_mongo_instance

## package: 
mvn package

## run: 
java -jar target/rd-ms-ecm-service.jar

## curls

#retrieve
curl http://localhost:9081/rd-ms-ecm/content
#tomee:
curl http://localhost:8080/rd-ms-ecm/content

#add a model
curl -H "Content-Type: application/json" -X POST -d '{"viewId": "test-content", "content": "<h1>Enterprise Content</h1><p>this is the first HTML content</p>"}' http://localhost:9081/rd-ms-ecm/content