radien Demo Management Microservice
======

#
# - Pre requirements:

    URL: http://localhost:9081/demomanagementservice/v1/demo
    HTTP Port: 9081
    HTTPS Port: 9444
    JMX Port: 1200
    Deployment: rd-ms-demo-service:war exploded
    Application Context: /demomanagementservice

#
# - cUrls

#
* POST Data
  ------

    curl -H "Content-Type: application/json" -X POST \
      --data-raw '{
        "name": "name-1",
      }' http://localhost:9081/demomanagementservice/v1/demo

#
* UPDATE Data
  ------

    curl -H "Content-Type: application/json" -X POST \
      --data-raw '{
        "name": "name-1-update",
      }' http://localhost:9081/demomanagementservice/v1/demo/{id}


* GET Data
  ------
    curl -H -X GET 'http://localhost:9081/demomanagementservice/v1/demo'
    
#
* DELETE Data
  ------
    curl --location --request DELETE 'http://localhost:9081/demomanagementservice/v1/demo/{id}'
