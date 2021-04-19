radien Tenant Management Microservice
======

#
# - Pre requirements:

### - ___Package:___
    mvn -P tomee package

### - ___Run:___
    java -jar target/tenantmanagementservice-exec.jar

### - ___Local TomEE Configuration:___
    URL: https://localhost:8082/tenantmanagement/v1/contract
    HTTP Port: 8082
    HTTPS Port: 8445
    JMX Port: 1101
    Deployment: rd-ms-tenantmanagement:war exploded
    Application Context: /tenantmanagementservice

#
# - cUrls

* GET Data
  ------
### - ___Get all tenants with pagination:___
    curl --location --request GET 'http://localhost:8082/tenantmanagementservice/v1/contract?{fieldName}={fieldValue}' --data-raw ''
- Possible field names
  - "pageNo" Long
  - "pageSize" Long
#
### - ___Get contract by ID:___
    curl -X GET 'http://localhost:8082/tenantmanagementservice/v1/contract/{id}'
### - ___Get tenant by ID:___
    curl -X GET 'http://localhost:8082/tenantmanagementservice/v1/tenant/{id}'
#
### - ___Get list of contracts:___
    curl -X GET 'http://localhost:8082/tenantmanagementservice/v1/contract'
### - ___Get list of tenants:___
    curl -X GET 'http://localhost:8082/tenantmanagementservice/v1/tenant'
#
* POST Data
  ------
### - ___Create contract:___
    curl -H "Content-Type: application/json" -X POST \
      --data-raw '{
        "name": "AZ",
        "start": "2021-01-22T13:59:17.468",
        "end": "2021-01-22T13:59:17.555",
        "createUser": null,
        "lastUpdateUser": null,
        "createDate": null,
        "lastUpdate": null
      }' http://localhost:8082/tenantmanagementservice/v1/contract
### - ___Create tenant:___
    curl -H "Content-Type: application/json" -X POST \
      --data-raw '{
        "name": "AZ",
        "createUser": null,
        "lastUpdateUser": null,
        "createDate": null,
        "lastUpdate": null
      }' http://localhost:8082/tenantmanagementservice/v1/tenant
#
* PUT Data
  ------
### - ___Update contract:___
    curl -H "Content-Type: application/json" -X POST \
      --data-raw '{
        "name": "AZ",
        "start": "2021-01-22T13:59:17.468",
        "end": "2021-01-22T13:59:17.555",
        "createUser": null,
        "lastUpdateUser": null,
        "createDate": null,
        "lastUpdate": null
      }' http://localhost:8082/tenantmanagementservice/v1/contract/{id}
### - ___Update tenant:___
    curl -H "Content-Type: application/json" -X POST \
      --data-raw '{
        "name": "AZ",
        "createUser": null,
        "lastUpdateUser": null,
        "createDate": null,
        "lastUpdate": null
      }' http://localhost:8082/tenantmanagementservice/v1/tenant/{id}
#

* DELETE Data
  ------
### - ___Delete Contract by ID:___
    curl --location --request DELETE 'http://localhost:8082/tenantmanagementservice/v1/contract/{id}'
### - ___Delete Tenant by ID:___
    curl --location --request DELETE 'http://localhost:8082/tenantmanagementservice/v1/tenant/{id}'
### - ___Delete Tenant by ID:___
    curl --location --request DELETE 'http://localhost:8082/tenantmanagementservice/v1/tenant/deleteTenantHierarchy/{id}'
