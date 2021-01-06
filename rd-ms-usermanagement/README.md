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
### - ___Create user:___

curl -H "Content-Type: application/json" -X POST -d '{"logon":"logon1","userEmail":"useremail1@useremail1.pt", "firstName":"NameFirst", "lastName":"NameLast", "createUser":1,"lastUpdateUser":1}' http://localhost:9080/rd-ms-usermanagement/usermanagement/v1/user

#
* PUT Data
  ------
### - ___Update user:___
    curl --location --request PUT 'http://localhost:9080/rd-ms-usermanagement/usermanagement/v1/user/2' header 'Content-Type: application/json' data-raw '{"logon":"newLogon","userEmail":"newuseremail@newuseremail.pt", "firstName":"newFirstName", "lastName":"newLastName", "createUser":1,"lastUpdateUser":1}'
#
* DELETE Data
  ------
### - ___Delete user by ID:___
    curl --location --request DELETE 'http://localhost:9080/rd-ms-usermanagement/usermanagement/v1/user/{id}'
