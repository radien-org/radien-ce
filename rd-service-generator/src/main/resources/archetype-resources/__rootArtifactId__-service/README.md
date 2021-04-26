radien ${entityResourceName}  Management Microservice
======

#
# - Pre requirements:

    URL: https://localhost:9081/${entityResourceName.toLowerCase()}managementservice/v1/${entityResourceName.toLowerCase()}
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
      }' https://localhost:9081/${entityResourceName.toLowerCase()}managementservice/v1/${entityResourceName.toLowerCase()}

#
* UPDATE Data
  ------

    curl -H "Content-Type: application/json" -X POST \
      --data-raw '{
        "name": "name-1-update",
      }' https://localhost:9081/${entityResourceName.toLowerCase()}managementservice/v1/${entityResourceName.toLowerCase()}/{id}


* GET Data
  ------
    curl -H -X GET 'https://localhost:9081/${entityResourceName.toLowerCase()}managementservice/v1/${entityResourceName.toLowerCase()}'
    
#
* DELETE Data
  ------
    curl --location --request DELETE 'https://localhost:9081/${entityResourceName.toLowerCase()}managementservice/v1/${entityResourceName.toLowerCase()}/{id}'
