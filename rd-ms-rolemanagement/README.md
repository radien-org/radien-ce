# Radien enterprise Role management microservice
======

# - Pre-requirements:

### - ___Package:___
    mvn -P tomee package

### - ___Run:___
    java -jar target/rd-ms-rolemanagement-service.jar

### - ___Local TomEE Configuration:___
    URL: https://localhost:9080/rolemanagementservice/v1/role
    HTTP Port: 8083
    HTTPS Port: 8445
    JMX Port: 1101
    Deployment: rd-ms-rolemanagement:war exploded
    Application Context: /rolemanagementservice

#
# - cUrls

* GET Data
  ------
### - ___Get all roles:___
    curl --location --request GET 'http://localhost:8083/rolemanagementservice/v1/role'
### - ___Get roles by ID:___
    curl --location --request GET 'http://localhost:8083/rolemanagementservice/v1/role/{id}'
### - ___Get roles by Documentation:___
    curl --location --request GET 'http://localhost:8083/rolemanagementservice/v1/role/find?documentation=valueToBeSearched' --data-raw ''
#
* POST Data
  ------
### - ___Create role:___
    curl --location --request POST 'http://localhost:8083/rolemanagementservice/v1/role' --header 'Content-Type: application/json' --data-raw '{"name":"roleName","description":"roleDescription"}'

### - ___Update role:___
    curl --location --request POST 'http://localhost:8083/rolemanagementservice/v1/role' --header 'Content-Type: application/json' --data-raw '{"id":roleIdOfRecordToBeUpdated, "name":"roleNameToInsert","description":"roleDescriptionToInsert"}'
#
* DELETE Data
  ------
### - ___Delete role by ID:___
    curl --location --request DELETE 'http://localhost:8083/rolemanagementservice/v1/role/{id}' --data-raw ''

