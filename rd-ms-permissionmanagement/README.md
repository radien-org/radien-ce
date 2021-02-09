# Radien enterprise Permission management microservice
======

# - Pre-requirements:

### - ___Package:___
    mvn -P tomee package

### - ___Run:___
    java -jar target/rd-ms-permissionmanagement-service.jar

### - ___Local TomEE Configuration:___
    URL: http://localhost:8085/permissionmanagementservice/v1/permission
    HTTP Port: 8085
    HTTPS Port: 8447
    JMX Port: 1103
    Deployment: rd-ms-permissionmanagement:war exploded
    Application Context: /permissionmanagementservice

#
# - cUrls

* GET Data
  ------
### - ___Get all permissions:___
    curl --location --request GET 'http://localhost:8085/permissionmanagementservice/v1/permission'
### - ___Get permissions by ID:___
    curl --location --request GET 'http://localhost:8085/permissionmanagementservice/v1/permission/{id}'
### - ___Get permissions by Name:___
    curl --location --request GET 'http://localhost:8085/permissionmanagementservice/v1/permission/find?name=valueToBeSearched' --data-raw ''

### - ___Get all actions:___
    curl --location --request GET 'http://localhost:8085/permissionmanagementservice/v1/action'
### - ___Get actions by ID:___
    curl --location --request GET 'http://localhost:8085/permissionmanagementservice/v1/action/{id}'
### - ___Get actions by Name:___
    curl --location --request GET 'http://localhost:8085/permissionmanagementservice/v1/action/find?name=valueToBeSearched' --data-raw ''

#
* POST Data
  ------
### - ___Create permission:___
    curl --location --request POST 'http://localhost:8085/permissionmanagementservice/v1/permission' --header 'Content-Type: application/json' --data-raw '{"name":"permissionName"}'

### - ___Update permission:___
    curl --location --request POST 'http://localhost:8085/permissionmanagementservice/v1/permission' --header 'Content-Type: application/json' --data-raw '{"id":permissionIdOfRecordToBeUpdated, "name":"permissionNameToInsert"}'


### - ___Create action:___
    curl --location --request POST 'http://localhost:8085/permissionmanagementservice/v1/action' --header 'Content-Type: application/json' --data-raw '{"name":"actionName", "type":"READ"}'

### - ___Update action:___
    curl --location --request POST 'http://localhost:8085/permissionmanagementservice/v1/action' --header 'Content-Type: application/json' --data-raw '{"id":actionIdOfRecordToBeUpdated, "name":"actionNameToInsert", "type": "WRITE"}'


#
* DELETE Data
  ------
### - ___Delete permission by ID:___
    curl --location --request DELETE 'http://localhost:8085/permissionmanagementservice/v1/permission/{id}' --data-raw ''

### - ___Delete action by ID:___
    curl --location --request DELETE 'http://localhost:8085/permissionmanagementservice/v1/action/{id}' --data-raw ''


#
* ASSOCIATING Data (Permission with an Action)
### - ___Associating permission with action by IDs:
    curl --location --request POST 'http://localhost:8085/permissionmanagementservice/v1/permission/{permissionId}/association/{actionId}'

#
* DISSOCIATING Data (Permission with an Action)
### - ___Dissociating permission with action by IDs:
    curl --location --request POST 'http://localhost:8085/permissionmanagementservice/v1/permission/{permissionId}/dissociation'
