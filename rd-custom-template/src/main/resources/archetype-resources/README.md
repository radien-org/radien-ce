# ${artifact-name}

# run: 
java -jar target/${artifactId}-service.jar

#pre-requisite:
mvn clean install
1) rd-custom-template
2) rd-custom-template-client
3) Read documentation for further Info

# Local TomEE Configuration
    URL: http://localhost:8084/${resource-name-variable}managementservice/${application-path}/${resource-path}
    HTTP Port: 8084
    HTTPS Port: 8447
    JMX Port: 1103
    Deployment: rd-ms-${artifactId}:war exploded
    Application Context: /${resource-name-variable}managementservice

## curls
#POST 
curl -d '{"id":1,"message":"Hello-1"}' -H 'Content-Type:application/json' http://localhost:8084/${resource-name-variable}managementservice/${application-path}/${resource-path}

#GET data
curl -v http://localhost:8084/${resource-name-variable}managementservice/${application-path}/${resource-path}/{id}

#PUT data
curl -d '{"id":1,"message":"Hello-3"}' -H 'Content-Type:application/json' -X PUT http://localhost:8084/${resource-name-variable}managementservice/${application-path}/${resource-path}/{id}

#DELETE data
curl -X DELETE http://localhost:8084/${resource-name-variable}managementservice/${application-path}/${resource-path}/{id}