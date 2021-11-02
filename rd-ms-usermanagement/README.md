# Radien User Management Microservice
Microservice that provides the necessary REST API to manage User domain entities.
In other words, provide CRUD specific operations to create, update, delete and retrieve Users. 

# Pre requirements:

## Running as executable Jar file 

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

## Running from IDE (ex: Intellij)

<img src="doc/config-umms-intellij-part1.png" width="400" height="400"/>

<img src="doc/config-umms-intellij-part2.png" width="400" height="400"/>

#
# cURLs: 
Let's check the operations and how to perform them via cURL (using http port 8080)

- Get Data:
    - <span style="color:blue">**Get User by ID**:</span> Returns the User found for an informed Id.    
       - Params:
           - Id: User Identifier           
       - Return (status):
           - 200: If exists a user for the Id
           - 404: If no user could be found
           - 401: In case of not informing a JWT token within AUTHORIZATION header
       - Pattern:
       
        ```
          curl -X GET 'http://localhost:8080/usermanagementservice/v1/user/{id}'
        ```
      
        - Example:
        ```
            curl --location --request GET 'http://localhost:8080/usermanagementservice/v1/user/1' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
        ```    
    - <span style="color:blue">**Get User ID by SUB**:</span> Retrieve the user id for a given keycloak user sub
        - Params:
            - Sub: User subject           
        - Return (status):
            - 200: If exists a user for the informed subject
            - 404: If no user could be found for the subject
            - 401: In case of not informing a JWT token within AUTHORIZATION header
        - Pattern:
            ```
              curl -X GET 'http://localhost:8080/usermanagementservice/v1/user/sub/{sub}'
            ```
        - Example:
            ```
                curl --location --request GET 'http://localhost:8080/usermanagementservice/v1/user/sub/f90b4a92-7fd4-4ff3-8e29-4c27411609bb' \
                --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    
          
    - <span style="color:blue">**Get All**:</span> Retrieve all the users using a pagination approach
        - Params:
            - search: criteria to be found, can be applied to Logon or e-mail fields in this case (Optional).
            - pageNo: page number to show the first records (Optional).
            - pageSize: max number of pages of results (Optional).
            - sortBy: criteria field to be sorted (Optional).
            - asc: boolean value to show the values ascending or descending way (Optional).
        - Return (status):
            - 200: In case of search operation concluded without issue.
            - 401: In case of not informing a JWT token within AUTHORIZATION header
            - 403: In case of forbidden access (Current logged user has no permission or roles to see other users)
        - Pattern:
            ```
              curl --location --request GET 'http://localhost:8080/usermanagementservice/v1/user?pageNo={pageNumber}&pageSize={pageSize}&sortBy={fieldName}&asc={true|false}&search={searchParam}'
            ```
        - Example:
            ```
            curl --location --request GET 'http://localhost:8080/usermanagementservice/v1/user?pageNo=1&pageSize=10&sortBy=firstname&asc=true&search=karch%25' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    
          
    - <span style="color:blue">**Find:**</span> Retrieves multiple users (List) basing on a search filter that uses the following parameters:
        - Params:
            - sub: sub to be found (Optional).
            - email: email to be found (Optional).
            - logon: logon to be found (Optional).
            - ids: ids to be found (Optional).
            - isExact: isExact should the search fields be exact or not as given (Optional).
            - isLogicalConjunction: isLogicalConjunction should the query use a and or a or criteria (Optional).            
            
        - Return (status):
            - 200: In case of search operation concluded without issue.
            - 401: In case of not informing a JWT token within AUTHORIZATION header
        - Pattern:
            ```
            curl --location --request GET 'http://localhost:8080/usermanagementservice/v1/user/find?sub={sub}&email={email}&logon={logon}&isExact={true|false}&isLogicalConjunction={true|false}'
            ```
        - Example: Searching using sub, e-mail and logon (Values do not need to be exact and will be performed as an "OR" search operation)
            ```
            curl --location --request GET 'http://localhost:8080/usermanagementservice/v1/user/find?sub=5a083ba8&email=karch&logon=karch&isExact=false&isLogicalConjunction=false' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    
        - Example 2: Searching using an ID List
            ```
            curl --location --request GET 'http://localhost:8080/usermanagementservice/v1/user/find?ids=1&ids=2&ids=3' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```              

- Post Data:
    - <span style="color:blue">**Create user:**</span> Creates a new User into the radien database (and optionally into the keycloak repository)  
       - Params:
           - User: Json message that corresponds to the user that must be created  
       - Keycloak Operation: Insertion into keycloak repository will be performed if attribute **enable** is set with true          
       - Return (status):
           - 200: in case of success operation.
           - 400: in case of invalid parameters (ex: Repeated values already inserted)
           - 401: In case of not informing a JWT token within AUTHORIZATION header.
           - 403: If the current logged user has no permission to create a new user         
           
       - Pattern:
       
        ```
          curl --location --request POST 'http://localhost:8080//usermanagementservice/v1/user' \
          --data-raw '{
              "firstname": [FIRST NAME VALUE],
              "lastname": [LAST NAME VALUE],
              "logon": [LOGON VALUE],
              "userEmail": [E-MAIL VALUE],
              "enabled": [true | false]
          }'
        ```
      
        - Example:
        ```
            curl --location --request POST 'http://localhost:8080//usermanagementservice/v1/user' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]' \
            --header 'Content-Type: application/json' \
            --data-raw '{
                "firstname": "newton",
                "lastname": "reis",
                "logon": "newton.reis",
                "userEmail": "newton.reis@yopmail.com",
                "enabled": true
            }'
        ```    
  
    - <span style="color:blue">**Create multiple users (Batch request):**  Creates a set of users like in batch mode (only insertion on Radien Database)</span>
       - Params:
           - Users: Array of Json messages which correspond to the users that must be created
       - Return (status):
           - 200: in case of success operation.
           - 400: in case of invalid parameters (ex: Repeated values contained within Json message)
           - 401: In case of not informing a JWT token within AUTHORIZATION header           
           
       - Pattern:
       
        ```
          curl --location --request POST 'http://localhost:8080/usermanagementservice/v1/user/multipleCreation' \
          --data-raw '[{
              "firstname": [FIRST NAME VALUE],
              "lastname": [LAST NAME VALUE],
              "logon": [LOGON VALUE],
              "userEmail": [E-MAIL VALUE],
              "enabled": [true | false]
          }]'
        ```
      
        - Example:
        ```
            curl --location --request POST 'http://localhost:8080/usermanagementservice/v1/user/multipleCreation' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]' \
            --header 'Content-Type: application/json' \
            --data-raw '[{
                "firstname": "newton",
                "lastname": "reis",
                "logon": "newton.reis",
                "userEmail": "newton.reis@yopmail.com",
                "enabled": true
            }]'
        ```    

    - <span style="color:blue">**Send Update password e-mail (Keycloak):**</span> Will send the updated password via email to the user
       - Params:
           - Id: Path parameter that represents the user Id             
       - Return (status):
           - 204: in case of success operation.
           - 401: In case of not informing a JWT token within AUTHORIZATION header           
           - 404: In case of informing a not existent user via id parameter            
           
       - Pattern:
       
        ```
          curl --location --request POST 'http://localhost:8080/usermanagementservice/v1/user/{id}/sendUpdatePasswordEmail'
        ```
      
        - Example:
        
        ```
          curl --location --request POST 'http://localhost:8080/usermanagementservice/v1/user/333/sendUpdatePasswordEmail' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]' \
            --header 'Content-Type: application/json'
        ```    
    
    - <span style="color:blue">**Refresh Token:**</span> Given an access token, this operation will update It based on the refresh token value.

       - Params:
           - Refresh token: Informed via body              
       - Return (status):
           - 200: in case of success operation.
           - 401: In case of not informing a JWT token within AUTHORIZATION header    
           
       - Pattern:
       
        ```
          curl --location --request POST 'http://localhost:8080/usermanagementservice/v1/user/refresh' \
          --header 'Authorization: Bearer [JWT ACCESS TOKEN]' \
          --header 'Content-Type: application/json' \
          --data-raw '[JWT REFRESH TOKEN]'
        ```
      
- Patch Data:
    - <span style="color:blue">**Update E-mail And ExecuteAction Email Verify:**</span>Will updated email and, optionally, will request to the user to verify the new e-mail
       - Params:
           - id: Path param that represents the user identifier
           - user: Json message that contains the information to be updated (User pojo containing the e-mail value)
           - emailVerify: Query parameter with boolean value to trigger keycloak to send verification message to the user (By default set to true if omitted)           
       - Return (status):
           - 200: in case of success operation.
           - 401: In case of not informing a JWT token within AUTHORIZATION header              
           
       - Pattern:
       
        ```
          curl --location --request PATCH 'http://localhost:8080/usermanagementservice/v1/user/{id}?emailVerify={true|false}' \       
          --header 'Authorization: Bearer [JWT ACCESS TOKEN]' \
          --header 'Content-Type: application/json' \
          --data-raw '{
              "userEmail": "[NEW EMAIL VALUE]"
          }'        
      
          curl --location --request PATCH 'http://localhost:8080/usermanagementservice/v1/user/{id}' \
          --header 'Authorization: Bearer [JWT ACCESS TOKEN]' \
          --header 'Content-Type: application/json' \
          --data-raw '{
              "userEmail": "[NEW EMAIL VALUE]"
          }'
        ```

       - Example:
       
       ```
          curl --location --request PATCH 'http://localhost:8080/usermanagementservice/v1/user/2?emailVerify=true' \
          --header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJJTEtqRFdzbzhUU3NnT1ZuVEZBUlJsWDIxTXBfN29zYUpNTnRwTWVsNV9VIn0.eyJleHAiOjE2MzUyNDQyMzAsImlhdCI6MTYzNTI0MDYzMCwianRpIjoiZjVkYTAyNGYtZmMzOS00NTBhLTkzMzctZmI3ZjA2YjA0ZjU0IiwiaXNzIjoiaHR0cHM6Ly9pZHAtaW50LnJhZGllbi5pby9hdXRoL3JlYWxtcy9yYWRpZW4iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNWEwODNiYTgtNDdhMS00NjE3LWI2NTktMzg2MTkyYTQ0M2M1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicmFkaWVuIiwic2Vzc2lvbl9zdGF0ZSI6Ijk0MDE4MDI2LTM5M2QtNGFiMy04ZTI0LTUyMjlhN2JjNTQ4NyIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cHM6Ly9sb2NhbGhvc3Q6ODQ0MyJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoia2FyY2gga2lyYWx5IiwicHJlZmVycmVkX3VzZXJuYW1lIjoia2FyY2gua2lyYWx5LXVzZXJuYW1lIiwiZ2l2ZW5fbmFtZSI6ImthcmNoIiwiZmFtaWx5X25hbWUiOiJraXJhbHkiLCJlbWFpbCI6ImthcmNoLmtpcmFseUB5b3BtYWlsLmNvbSJ9.LPqJ6XMQ2OaDLGhnHrPqYeyiukirIJBE2yjjupQA9x2mmUP6qyzf5uO_Zpld5MHTrY0IMJ4TXTOUbZNAWxpzxEh2Lk9BZtZC5x7th4NtGW_iUHszKPzrah-sWp7C-xZEpO3auJ2j-UtPxU9548PYcro34Wy5ke7VYHw5Y3hoSUReN73ZFgD4X1rCSgGJc3UlHdU5cY0L0a5CweHGgXZpZwbcHdjxlZyRCkZgZNAczsGgqfuMIqibsld3ogU4rjReKAG4kY18R-WLV5GfCagqRY4Q7-yFe57TcUJrdgVkXkoOhTkn-k9FG8G8xthPh6RMCOAv8G9xmX-VEeG2sW-_oQ' \
          --header 'Content-Type: application/json' \
          --header 'Cookie: JSESSIONID=5A5B2D72CA621634A7AB4E2D04B229AA' \
          --data-raw '{
              "userEmail": "gustavo.lopes.franco@yopmail.com"
          }'
       ```

- Put Data:
    - <span style="color:blue">**Update user:**</span> Updates a user into the radien database (and optionally into the keycloak repository) 
       - Params:
           - Id: Path param that corresponds to the user identifier  
           - User: Json message containing all user information (to be updated)
       - Keycloak Operation: Updating into keycloak repository will be performed if attribute **enable** is set with true                       
       - Return (status):
           - 200: in case of success operation.
           - 400: in case of invalid parameters (ex: Repeated values already inserted)
           - 401: In case of not informing a JWT token within AUTHORIZATION header.
           - 403: If the current logged user has no permission to update a new user
           - 404: If no user can be found for Id parameter         
           
       - Pattern:
       
        ```
          curl --location --request PUT 'http://localhost:8080//usermanagementservice/v1/user/{id}' \
          --data-raw '{
              "firstname": [FIRST NAME VALUE],
              "lastname": [LAST NAME VALUE],
              "logon": [LOGON VALUE],
              "userEmail": [E-MAIL VALUE],
              "enabled": [true | false]
          }'
        ```
      
        - Example:
        ```
            curl --location --request PUT 'http://localhost:8080//usermanagementservice/v1/user/1' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]' \
            --header 'Content-Type: application/json' \
            --data-raw '{
                "firstname": "newton",
                "lastname": "reis",
                "logon": "newton.reis",
                "userEmail": "newton.reis@yopmail.com",
                "enabled": true
            }'
        ```        
    
- Delete Data:
    - <span style="color:blue">**Delete user:**</span> Deletes a User from radien database and keycloak 
       - Params:
           - Id: Path param that corresponds to the user identifier  
       - Return (status):
           - 200: in case of success operation.
           - 401: In case of not informing a JWT token within AUTHORIZATION header.
           - 403: If the current logged user has no sufficient ROLES to delete user
           - 404: If no user can be found for Id parameter                
           
       - Pattern:
       
        ```
          curl --location --request DELETE 'http://localhost:8080//usermanagementservice/v1/user/{id}' \
          --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
        ```
      
        - Example:
        ```
            curl --location --request DELETE 'http://localhost:8080/usermanagementservice/v1/user/222' \
            --header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJJTEtqRFdzbzhUU3NnT1ZuVEZBUlJsWDIxTXBfN29zYUpNTnRwTWVsNV9VIn0.eyJleHAiOjE2MzUyNDYxNzgsImlhdCI6MTYzNTI0MjU3OCwianRpIjoiZmFmMjJiMGEtNmZhNy00YjI5LThlNGUtMmQxMTM3MTNmNmNiIiwiaXNzIjoiaHR0cHM6Ly9pZHAtaW50LnJhZGllbi5pby9hdXRoL3JlYWxtcy9yYWRpZW4iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNWEwODNiYTgtNDdhMS00NjE3LWI2NTktMzg2MTkyYTQ0M2M1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicmFkaWVuIiwic2Vzc2lvbl9zdGF0ZSI6ImIwMjhiNWNkLWUxZjItNDI1Mi05NjkxLWQ1ZjQ3Y2M5MGVlNSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cHM6Ly9sb2NhbGhvc3Q6ODQ0MyJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoia2FyY2gga2lyYWx5IiwicHJlZmVycmVkX3VzZXJuYW1lIjoia2FyY2gua2lyYWx5LXVzZXJuYW1lIiwiZ2l2ZW5fbmFtZSI6ImthcmNoIiwiZmFtaWx5X25hbWUiOiJraXJhbHkiLCJlbWFpbCI6ImthcmNoLmtpcmFseUB5b3BtYWlsLmNvbSJ9.XHuSZgWqGRDS1LHnYacCkH-0mHr4Oaeetm1UzBrjIppv-jPYGroaSEZFSMVdQ4e7sLDPHD4kYyImtDVYKN4_2gvubl3IVEQtL8rBAfjd8YYG7KYxNVDFVbW8mw-KuqfIBN5YQiXetPNbUj15NU_Fz0mu7XPn3UtL7lAZTq6NHVUtxi4Q8IsVqzvAnNvkqRwSUbuHqykk-X4gymCoyGjcuPk0ex4_7Umm0uKMhrKjMYM2ATLEvAjqo4ODkMnSe31y0CBhhcq_Ou2HE7nMTn3ffXjikH7rYNfQhW4JhbDySXLh0e-iHzuuHke35TZhq5gGnnZjd9xaZWO47im9b-l5xw' 
        ```        
