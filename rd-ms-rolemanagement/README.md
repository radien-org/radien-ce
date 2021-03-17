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
### - ___Get all roles with pagination:___
    curl --location --request GET 'http://localhost:8083/rolemanagementservice/v1/role?{fieldName}={fieldValue}' --data-raw ''
 - Possible field names
    - "pageNo" Long
    - "pageSize" Long

### - ___Get all Linked authorization association:___
    curl --location --request GET 'http://localhost:8083/rolemanagementservice/v1/linkedauthorization'
#
### - ___Get roles by ID:___
    curl --location --request GET 'http://localhost:8083/rolemanagementservice/v1/role/{id}'
### - ___Get Linked authorization association by ID:___
    curl --location --request GET 'http://localhost:8083/rolemanagementservice/v1/linkedauthorization/{id}'
#
### - ___Get roles by a specific field:___
    curl --location --request GET 'http://localhost:8083/rolemanagementservice/v1/role/find?{fieldName}={fieldValue}' --data-raw ''
  - Possible field names
    - "name" String
    - "description" String
    - "isExact" boolean
    - "isLogicalConjunction" boolean

### - ___Get specific Linked authorization association by searched field:___
    curl --location --request GET 'http://localhost:8083/rolemanagementservice/v1/linkedauthorization/find?{fieldName}={fieldValue}' --data-raw ''
  - Possible field names
    - "tenantId" Long
    - "permissionId" Long
    - "roleId" Long
    - "userId" Long
    - "isLogicalConjunction" boolean

#
### - ___Exists specific association:___
    curl --location --request GET 'http://localhost:8083/rolemanagementservice/v1/linkedauthorization/exists?{fieldName}={fieldValue}' --data-raw ''
  - Possible field names
    - "tenantId" Long
    - "permissionId" Long
    - "roleId" Long
    - "userId" Long
    - "isLogicalConjunction" boolean
 
#
### - ___Get a count of all the existent records:___
    curl --location --request GET 'http://localhost:8083/rolemanagementservice/v1/role/countRecords'

#
#
* POST Data
  ------
### - ___Create role:___
    curl --location --request POST 'http://localhost:8083/rolemanagementservice/v1/role' --header 'Content-Type: application/json' --data-raw '{"name":"roleName","description":"roleDescription"}'
### - ___Create Linked authorization association:___
    curl --location --request POST 'http://localhost:8083/rolemanagementservice/v1/linkedauthorization' --header 'Content-Type: application/json' --data-raw '{"tenantId":3,"permissionId":3, "roleId":3}'

#
### - ___Update role:___
    curl --location --request POST 'http://localhost:8083/rolemanagementservice/v1/role' --header 'Content-Type: application/json' --data-raw '{"id":roleIdOfRecordToBeUpdated, "name":"roleNameToInsert","description":"roleDescriptionToInsert"}'
### - ___Update Linked authorization association:___
    curl --location --request POST 'http://localhost:8083/rolemanagementservice/v1/linkedauthorization' --header 'Content-Type: application/json' --data-raw '{"id":2, "tenantId":3,"permissionId":3, "roleId":3}'

#
* DELETE Data
  ------
### - ___Delete role by ID:___
    curl --location --request DELETE 'http://localhost:8083/rolemanagementservice/v1/role/{id}' --data-raw ''
### - ___Delete Linked authorization association by ID:___
    curl --location --request DELETE 'http://localhost:8083/rolemanagementservice/v1/linkedauthorization/1' --data-raw ''
