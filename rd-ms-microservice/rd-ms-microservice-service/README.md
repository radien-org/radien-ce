radien Microservice Management Microservice
======

#
# - Pre requirements:

    URL: http://localhost:9081/microservicemanagementservice/v1/microservice
    HTTP Port: 9081
    HTTPS Port: 9444
    JMX Port: 1200
    Deployment: rd-ms-microservice-service:war exploded
    Application Context: /microservicemanagementservice

#
# - cUrls

#
* POST Data
  ------

    curl -H "Content-Type: application/json" -X POST \
      --data-raw '{
        "name": "name-1",
      }' http://localhost:9081/microservicemanagementservice/v1/microservice

#
* UPDATE Data
  ------

    curl -H "Content-Type: application/json" -X POST \
      --data-raw '{
        "name": "name-1-update",
      }' http://localhost:9081/microservicemanagementservice/v1/microservice/{id}


* GET Data
  ------
    curl -H -X GET 'http://localhost:9081/microservicemanagementservice/v1/microservice'
    
#
* DELETE Data
  ------
    curl --location --request DELETE 'http://localhost:9081/microservicemanagementservice/v1/microservice/{id}'
