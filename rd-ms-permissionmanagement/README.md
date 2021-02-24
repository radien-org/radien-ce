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
### - ___Get permissions by Action:___
    curl --location --request GET 'http://localhost:8085/permissionmanagementservice/v1/action/find?action=actionId' --data-raw ''
### - ___Get permissions by Resource:___
    curl --location --request GET 'http://localhost:8085/permissionmanagementservice/v1/action/find?resource=resourceId' --data-raw ''
### - ___Get permissions by Action and Resource:___
    curl --location --request GET 'http://localhost:8085/permissionmanagementservice/v1/action/find?action=actionId&resource=resourceId' --data-raw ''
### - ___Get permissions by Action or Resource:___
    curl --location --request GET 'http://localhost:8085/permissionmanagementservice/v1/action/find?action=actionId&resource=resourceId&isLogicalConjunction=false' --data-raw ''

### - ___Get all actions:___
    curl --location --request GET 'http://localhost:8085/permissionmanagementservice/v1/action'
### - ___Get actions by ID:___
    curl --location --request GET 'http://localhost:8085/permissionmanagementservice/v1/action/{id}'
### - ___Get actions by Name:___
    curl --location --request GET 'http://localhost:8085/permissionmanagementservice/v1/action/find?name=valueToBeSearched' --data-raw ''

### - ___Get all resources:___
    curl --location --request GET 'http://localhost:8085/permissionmanagementservice/v1/resource'
### - ___Get resources by ID:___
    curl --location --request GET 'http://localhost:8085/permissionmanagementservice/v1/resource/{id}'
### - ___Get resources by Name:___
    curl --location --request GET 'http://localhost:8085/permissionmanagementservice/v1/resource/find?name=valueToBeSearched' --data-raw ''

#
* POST Data
  ------
### - ___Create permission:___
    curl --location --request POST 'http://localhost:8085/permissionmanagementservice/v1/permission' --header 'Content-Type: application/json' --data-raw '{"name":"permissionName"}'

### - ___Update permission:___
    curl --location --request POST 'http://localhost:8085/permissionmanagementservice/v1/permission' --header 'Content-Type: application/json' --data-raw '{"id":permissionIdOfRecordToBeUpdated, "name":"permissionNameToInsert"}'

### - ___Create action:___
    curl --location --request POST 'http://localhost:8085/permissionmanagementservice/v1/action' --header 'Content-Type: application/json' --data-raw '{"name":"actionName"}'

### - ___Update action:___
    curl --location --request POST 'http://localhost:8085/permissionmanagementservice/v1/action' --header 'Content-Type: application/json' --data-raw '{"id":actionIdOfRecordToBeUpdated, "name":"actionNameToUpdate"}'

### - ___Create resource:___
    curl --location --request POST 'http://localhost:8085/permissionmanagementservice/v1/resource' --header 'Content-Type: application/json' --data-raw '{"name":"resourceName"}'

### - ___Update resource:___
    curl --location --request POST 'http://localhost:8085/permissionmanagementservice/v1/resource' --header 'Content-Type: application/json' --data-raw '{"id":resourceId, "name":"resourceNameToUpdate"}'


#
* DELETE Data
  ------
### - ___Delete permission by ID:___
    curl --location --request DELETE 'http://localhost:8085/permissionmanagementservice/v1/permission/{id}' --data-raw ''

### - ___Delete action by ID:___
    curl --location --request DELETE 'http://localhost:8085/permissionmanagementservice/v1/action/{id}' --data-raw ''

### - ___Delete resource by ID:___
    curl --location --request DELETE 'http://localhost:8085/permissionmanagementservice/v1/resource/{id}' --data-raw ''

### - ___Associating permission with action by IDs:___
    curl --location --request POST 'http://localhost:8085/permissionmanagementservice/v1/permission/{permissionId}/action/{actionId}'

### - ___Dissociating permission with action and resource by IDs:___
    curl --location --request POST 'http://localhost:8085/permissionmanagementservice/v1/permission/{permissionId}/dissociation'
