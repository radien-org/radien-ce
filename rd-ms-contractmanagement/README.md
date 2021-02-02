radien Contract Management Microservice
======

#
# - Pre requirements:

### - ___Package:___
    mvn -P tomee package

### - ___Run:___
    java -jar target/contractmanagementservice-exec.jar

### - ___Local TomEE Configuration:___
    URL: https://localhost:8082/contractmanagement/v1/contract
    HTTP Port: 8082
    HTTPS Port: 8445
    JMX Port: 1101
    Deployment: rd-ms-contractmanagement:war exploded
    Application Context: /contractmanagementservice

#
# - cUrls

* GET Data
  ------
### - ___Get contract by ID:___
    curl -X GET 'http://localhost:8082/contractmanagementservice/v1/contract/{id}'
#
* POST Data
  ------
### - ___Create contract:___

    curl -H "Content-Type: application/json" -X POST -d '{"name": "AA","start": "2021-01-22T13:59:17.468","end": "2021-01-22T13:59:17.555"}' http://localhost:8082/contractmanagementservice/v1/contract

#
* PUT Data
  ------
### - ___Update contract:___
    curl --location --request PUT 'http://localhost:8082/contractmanagementservice/v1/contract/{id}' header 'Content-Type: application/json' data-raw '{"name": "AA","start": "2021-01-22T13:59:17.468","end": "2021-01-22T13:59:17.555"}'
#
* DELETE Data
  ------
### - ___Delete user by ID:___
    curl --location --request DELETE 'http://localhost:8082/contractmanagementservice/v1/contract/{id}'
