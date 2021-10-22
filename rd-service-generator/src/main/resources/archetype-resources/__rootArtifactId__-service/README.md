radien ${entityResourceName} Management Microservice
======

#
# - Pre requirements:

    URL: http://localhost:9081/${entityResourceName.toLowerCase()}managementservice/v1/${entityResourceName.toLowerCase()}
    HTTP Port: 9081
    HTTPS Port: 9444
    JMX Port: 1200
    Deployment: ${rootArtifactId}-service:war exploded
    Application Context: /${entityResourceName.toLowerCase()}managementservice

#
# - cUrls

#
* POST Data
  ------

    curl -H "Content-Type: application/json" -X POST \
      --data-raw '{
        "name": "name-1",
      }' http://localhost:9081/${entityResourceName.toLowerCase()}managementservice/v1/${entityResourceName.toLowerCase()}

#
* UPDATE Data
  ------

    curl -H "Content-Type: application/json" -X POST \
      --data-raw '{
        "name": "name-1-update",
      }' http://localhost:9081/${entityResourceName.toLowerCase()}managementservice/v1/${entityResourceName.toLowerCase()}/{id}


* GET Data
  ------
    curl -H -X GET 'http://localhost:9081/${entityResourceName.toLowerCase()}managementservice/v1/${entityResourceName.toLowerCase()}'
    
#
* DELETE Data
  ------
    curl --location --request DELETE 'http://localhost:9081/${entityResourceName.toLowerCase()}managementservice/v1/${entityResourceName.toLowerCase()}/{id}'
