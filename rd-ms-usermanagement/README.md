radien Enterprise Content Management Microservice
======

@andres sousa: documentation goes here
#
# - Pre requirements:
 Mongo DB has to run under:
 * http://link_to_mongo_instance

### - ___Package:___
    mvn package

### - ___Run:___ 
    java -jar target/rd-ms-usermanagement-service.jar

#
#
# - cUrls

* GET Data
  ------

### - ___Microprofile:___
    curl http://localhost:8080/usermagagement/models

### - ___Local:___
    curl http://localhost:8080/rd-ms-usermanagement/usermanagement/user

### - ___Get user by ID:___
    curl --location --request GET 'http://localhost:8080/usermanagement/usermanagement/v1/user/{id}'
#
* POST Data
  ------

### - ___Microprofile:___
    curl -H "Content-Type: application/json" -X POST -d '{"id": "1", "message": "this is the model"}' http://localhost:8080/template/models

### - ___Local:___
    curl -H "Content-Type: application/json" -X POST -d '{"id": "1", "message": "this is the model"}' http://localhost:8080/rd-ms-template/template/models
### - ___Create user:___
    curl --location --request POST 'http://localhost:8080/usermanagement/usermanagement/v1/user' --header 'Content-Type: application/json' --data-raw '{"logon":"logon1","userEmail":"useremail1@useremail1.pt", "firstName":"NameFirst", "lastName":"NameLast", "createUser":1,"lastUpdateUser":1}'
#
* PUT Data
  ------

#
* DELETE Data
  ------
### - ___Delete user by ID:___
    curl --location --request DELETE 'http://localhost:8080/usermanagement/usermanagement/v1/user/{id}'
