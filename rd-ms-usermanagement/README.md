radien User Management Microservice
======

#
# - Pre requirements:

### - ___Package:___
    mvn -P tomee package

### - ___Run:___ 
    java -jar target/rd-ms-usermanagement-service.jar

### - ___Local TomEE Configuration:___
    URL: https://localhost:9080/rd-ms-usermanagement/usermanagement/v1/user
    HTTP Port: 8081
    HTTPS Port: 8444
    JMX Port: 1100
    Deployment: rd-ms-usermanagement:war exploded
    Application Context: /rd-ms-usermanagement

#
# - cUrls

* GET Data
  ------
### - ___Get user by ID:___
    curl -H -X GET 'http://localhost:8081/usermanagementservice/v1/user'
    curl -X GET 'http://localhost:8081/usermanagementservice/v1/user/{id}'
#
* POST Data
  ------
### - ___Save/Create user:___

    curl -H "Content-Type: application/json" -X POST \
      --data-raw '{
        "logon": "AZ",
        "userEmail": "user@radien.io",
        "firstName": "user",
        "lastName": "name",
        "createUser": 1,
        "lastUpdateUser": 1,
        "createDate": null,
        "lastUpdate": null
      }' http://localhost:8081/usermanagementservice/v1/user/{id}

#
* DELETE Data
  ------
### - ___Delete user by ID:___
    curl --location --request DELETE 'http://localhost:9080/rd-ms-usermanagement/usermanagement/v1/user/{id}'
