# Radien Tenant Management Microservice
Microservice that provides REST APIs to manage domain objects regarding **Tenant** and **ActiveTenant**.

For each of these entities there will be one particular endpoint providing CRUD operations. 

![Tenant domain model](doc/tenant-domain-model.png)

# Pre requirements:

## Running as executable Jar file 

### - ___Package:___
    mvn -P tomee package

### - ___Run:___ 
    java -jar target/rd-ms-tenantmanagement-service.jar

### - ___Local TomEE Configuration:___
    URL: http://localhost:8080/tenantmanagementservice/v1/tenant
    HTTP Port: 8085
    HTTPS Port: 8447
    JMX Port: 1103
    Deployment: rd-ms-tenantmanagement:war exploded
    Application Context: /tenantmanagementservice

#
Let's check the operations that exists for each endpoint and how to perform them via cURL

#
# Tenant Endpoint:

- Get Data:
    - <span style="color:blue">**Get Tenant by ID**:</span> Returns the Tenant found for an informed Id.    
       - Params:
           - Id: Tenant Identifier           
       - Return (status):
           - 200: If exists an tenant for the Id:
           - 404: If no tenant could be found
           - 401: In case of not informing a JWT token within AUTHORIZATION header
       - Pattern:
       
        ```
          curl -X GET 'http://localhost:8080/tenantmanagementservice/v1/tenant/{id}'
        ```
      
        - Example:
        ```
            curl --location --request GET 'http://localhost:8080/tenantmanagementservice/v1/tenant/1' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
        ```    
          
    - <span style="color:blue">**Get All**:</span> Retrieve all the tenants using a pagination approach
        - Params:
            - search: criteria to be found, applied to the tenant name (Optional).
            - pageNo: page number to show the first records (Optional).
            - pageSize: max number of pages of results (Optional).
            - sortBy: criteria field to be sorted (Optional).
            - asc: boolean value to show the values ascending or descending way (Optional).
        - Return (status):
            - 200: In case of search operation concluded without issue
            - 401: In case of not informing a JWT token within AUTHORIZATION header
        - Pattern:
            ```
              curl --location --request GET 'http://localhost:8080/tenantmanagementservice/v1/tenant?pageNo={pageNumber}&pageSize={pageSize}&sortBy={fieldName}&asc={true|false}&search={searchParam}'
            ```
        - Example:
            ```
            curl --location --request GET 'http://localhost:8080/tenantmanagementservice/v1/tenant?pageNo=1&pageSize=10&sortBy=name&asc=true&search=Client%25' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    
          
    - <span style="color:blue">**Find:**</span> Retrieves multiple tenants (List) basing on a search filter that uses the following parameters:
        - Params:
            - name: tenant name (Optional).
            - tenantType: tenant type description, ex: ROOT, etc (Optional).
            - ids: ids to be found (Optional).
            - isExact: isExact should the search fields be exact or not as given (Optional).
            - isLogicalConjunction: isLogicalConjunction should the query use a and or a or criteria (Optional).            
            
        - Return (status):
            - 200: In case of search operation concluded without issue.
            - 401: In case of not informing a JWT token within AUTHORIZATION header
        - Pattern:
            ```
            curl --location --request GET 'http://localhost:8080/tenantmanagementservice/v1/tenant/find?name={value}&tenantType={tenant type descr}&ids={ids for tenant}&isExact={true|false}&isLogicalConjunction={true|false}'
            ```
        - Example: Searching using name (Value do not need to be exact) and type parameters
            ```
            curl --location --request GET 'http://localhost:8080/tenantmanagementservice/v1/tenant/find?name=Cli&tenantType=client&isExact=false&isLogicalConjunction=true' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    
        - Example 2: Searching using an ID List
            ```
            curl --location --request GET 'http://localhost:8080/tenantmanagementservice/v1/tenant/find?ids=1&ids=2&ids=3' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```              

    - <span style="color:blue">**Check exists:**</span> Validates if Tenant exists for a referred Id
        - Params:
            - id: Tenant Identifier to guide the search

        - Return (status):
            - 204: If Tenant exists 
            - 401: In case of not informing a JWT token within AUTHORIZATION header
            - 404: If Tenant does not exist
            
        - Example: 
            ```          
            curl --location --head 'http://localhost:8080/tenantmanagementservice/v1/tenant/1' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    


- Post Data:
    - <span style="color:blue">**Create tenant:**</span> Creates a new tenant into the radien database  
       - Params:
           - tenant: Json message that corresponds to the tenant that must be created  
       - Return (status):
           - 200: in case of success operation.
           - 400: in case of invalid parameters (ex: Repeated values already inserted)
           - 401: In case of not informing a JWT token within AUTHORIZATION header.      
           
       - Pattern / Example:
       
           - For creating a Root Tenant:
           
       ```            
            curl --location --request POST 'http://localhost:8080//tenantmanagementservice/v1/tenant' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]' \
            --header 'Content-Type: application/json' \                        
            --data-raw '{
                "tenantKey": "EVCorp",
                "name": "Root Tenant",
                "tenantType": "Root",
                "tenantStart": "2030-01-22",
                "tenantEnd": "2040-01-22"
            }'
       ```
         
           - For creating a Client Tenant:
           
       ```
            curl --location --request POST 'http://localhost:8080//tenantmanagementservice/v1/tenant' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]' \
            --header 'Content-Type: application/json' \                        
            --data-raw '{
                "name": "Client Tenant",
                "tenantKey": "Rethink Client",     
                "tenantStart": "2021-05-13",
                "tenantType": "Client",       
                "clientAddress": "Sophiestrasse 33",
                "clientCity": "Braunschweig",
                "clientCountry": "Germany",
                "clientEmail": "email@email.com",
                "clientPhoneNumber": 933876547,
                "clientZipCode": "38118",
                "parentId": 1,
            }'
       ```

           - For creating a Sub Tenant:
           
       ```
            curl --location --request POST 'http://localhost:8080//tenantmanagementservice/v1/tenant' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]' \
            --header 'Content-Type: application/json' \                        
            --data-raw '{
                "tenantKey": "EVCorp",
                "name": "Sub Tenant",
                "tenantType": "Sub",
                "tenantStart": "2030-01-22",
                "tenantEnd": "2040-01-22",
                "parentId": 2,
                "clientId": 2
            }'
       ```           
  
- Put Data:
    - <span style="color:blue">**Update tenant:**</span> Updates a tenant into the radien database 
       - Params:
           - Id: Path param that corresponds to the tenant identifier  
           - tenant: Json message containing all tenant information (to be updated)
       - Return (status):
           - 200: in case of success operation.
           - 400: in case of invalid parameters (ex: Repeated values already inserted)
           - 401: In case of not informing a JWT token within AUTHORIZATION header.
           - 404: If no tenant can be found for Id parameter         
           
       - Pattern / Example:
       
           - For update a Root Tenant:
           
       ```            
            curl --location --request POST 'http://localhost:8080//tenantmanagementservice/v1/tenant' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]' \
            --header 'Content-Type: application/json' \                        
            --data-raw '{
                "tenantKey": "EVCorp",
                "name": "Root Tenant",
                "tenantType": "Root",
                "tenantStart": "2030-01-22",
                "tenantEnd": "2040-01-22"
            }'
       ```
         
           - For update a Client Tenant:
           
       ```
            curl --location --request POST 'http://localhost:8080//tenantmanagementservice/v1/tenant' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]' \
            --header 'Content-Type: application/json' \                        
            --data-raw '{
                "name": "Client Tenant",
                "tenantKey": "Rethink Client",     
                "tenantStart": "2021-05-13",
                "tenantType": "Client",       
                "clientAddress": "Sophiestrasse 33",
                "clientCity": "Braunschweig",
                "clientCountry": "Germany",
                "clientEmail": "email@email.com",
                "clientPhoneNumber": 933876547,
                "clientZipCode": "38118",
                "parentId": 1,
            }'
       ```

           - For update a Sub Tenant:
           
       ```
            curl --location --request POST 'http://localhost:8080//tenantmanagementservice/v1/tenant' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]' \
            --header 'Content-Type: application/json' \                        
            --data-raw '{
                "tenantKey": "EVCorp",
                "name": "Sub Tenant",
                "tenantType": "Sub",
                "tenantStart": "2030-01-22",
                "tenantEnd": "2040-01-22",
                "parentId": 2,
                "clientId": 2
            }        


      
- Delete Data:
    - <span style="color:blue">**Delete tenant:**</span> Deletes a tenant from radien database
       - Params:
           - Id: Path param that corresponds to the tenant identifier  
       - Return (status):
           - 200: in case of success operation.
           - 401: In case of not informing a JWT token within AUTHORIZATION header.
           - 404: If no tenant can be found for Id parameter                
           
       - Pattern:
       
        ```
          curl --location --request DELETE 'http://localhost:8080//tenantmanagementservice/v1/tenant/{id}' \
          --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
        ```
      
        - Example:
        ```
            curl --location --request DELETE 'http://localhost:8080/tenantmanagementservice/v1/tenant/222' \
            --header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJJTEtqRFdzbzhUU3NnT1ZuVEZBUlJsWDIxTXBfN29zYUpNTnRwTWVsNV9VIn0.eyJleHAiOjE2MzUyNDYxNzgsImlhdCI6MTYzNTI0MjU3OCwianRpIjoiZmFmMjJiMGEtNmZhNy00YjI5LThlNGUtMmQxMTM3MTNmNmNiIiwiaXNzIjoiaHR0cHM6Ly9pZHAtaW50LnJhZGllbi5pby9hdXRoL3JlYWxtcy9yYWRpZW4iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNWEwODNiYTgtNDdhMS00NjE3LWI2NTktMzg2MTkyYTQ0M2M1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicmFkaWVuIiwic2Vzc2lvbl9zdGF0ZSI6ImIwMjhiNWNkLWUxZjItNDI1Mi05NjkxLWQ1ZjQ3Y2M5MGVlNSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cHM6Ly9sb2NhbGhvc3Q6ODQ0MyJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoia2FyY2gga2lyYWx5IiwicHJlZmVycmVkX3VzZXJuYW1lIjoia2FyY2gua2lyYWx5LXVzZXJuYW1lIiwiZ2l2ZW5fbmFtZSI6ImthcmNoIiwiZmFtaWx5X25hbWUiOiJraXJhbHkiLCJlbWFpbCI6ImthcmNoLmtpcmFseUB5b3BtYWlsLmNvbSJ9.XHuSZgWqGRDS1LHnYacCkH-0mHr4Oaeetm1UzBrjIppv-jPYGroaSEZFSMVdQ4e7sLDPHD4kYyImtDVYKN4_2gvubl3IVEQtL8rBAfjd8YYG7KYxNVDFVbW8mw-KuqfIBN5YQiXetPNbUj15NU_Fz0mu7XPn3UtL7lAZTq6NHVUtxi4Q8IsVqzvAnNvkqRwSUbuHqykk-X4gymCoyGjcuPk0ex4_7Umm0uKMhrKjMYM2ATLEvAjqo4ODkMnSe31y0CBhhcq_Ou2HE7nMTn3ffXjikH7rYNfQhW4JhbDySXLh0e-iHzuuHke35TZhq5gGnnZjd9xaZWO47im9b-l5xw' 
        ```        
      
    - <span style="color:blue">**Delete Tenant Hierarchy:**</span> Deletes a tenant and all his children (Client Tenants and sub tenants linked)
       - Params:
           - Id: Path param that corresponds to the tenant identifier  
       - Return (status):
           - 200: in case of success operation.
           - 401: In case of not informing a JWT token within AUTHORIZATION header.
           - 500: In case of any issue                
           
       - Pattern:
       
        ```
          curl --location --request DELETE 'http://localhost:8080//tenantmanagementservice/v1/tenant/hierarchy/{id}' \
          --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
        ```
      
        - Example:
        ```
            curl --location --request DELETE 'http://localhost:8080/tenantmanagementservice/v1/tenant/hierarchy/222' \
            --header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJJTEtqRFdzbzhUU3NnT1ZuVEZBUlJsWDIxTXBfN29zYUpNTnRwTWVsNV9VIn0.eyJleHAiOjE2MzUyNDYxNzgsImlhdCI6MTYzNTI0MjU3OCwianRpIjoiZmFmMjJiMGEtNmZhNy00YjI5LThlNGUtMmQxMTM3MTNmNmNiIiwiaXNzIjoiaHR0cHM6Ly9pZHAtaW50LnJhZGllbi5pby9hdXRoL3JlYWxtcy9yYWRpZW4iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNWEwODNiYTgtNDdhMS00NjE3LWI2NTktMzg2MTkyYTQ0M2M1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicmFkaWVuIiwic2Vzc2lvbl9zdGF0ZSI6ImIwMjhiNWNkLWUxZjItNDI1Mi05NjkxLWQ1ZjQ3Y2M5MGVlNSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cHM6Ly9sb2NhbGhvc3Q6ODQ0MyJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoia2FyY2gga2lyYWx5IiwicHJlZmVycmVkX3VzZXJuYW1lIjoia2FyY2gua2lyYWx5LXVzZXJuYW1lIiwiZ2l2ZW5fbmFtZSI6ImthcmNoIiwiZmFtaWx5X25hbWUiOiJraXJhbHkiLCJlbWFpbCI6ImthcmNoLmtpcmFseUB5b3BtYWlsLmNvbSJ9.XHuSZgWqGRDS1LHnYacCkH-0mHr4Oaeetm1UzBrjIppv-jPYGroaSEZFSMVdQ4e7sLDPHD4kYyImtDVYKN4_2gvubl3IVEQtL8rBAfjd8YYG7KYxNVDFVbW8mw-KuqfIBN5YQiXetPNbUj15NU_Fz0mu7XPn3UtL7lAZTq6NHVUtxi4Q8IsVqzvAnNvkqRwSUbuHqykk-X4gymCoyGjcuPk0ex4_7Umm0uKMhrKjMYM2ATLEvAjqo4ODkMnSe31y0CBhhcq_Ou2HE7nMTn3ffXjikH7rYNfQhW4JhbDySXLh0e-iHzuuHke35TZhq5gGnnZjd9xaZWO47im9b-l5xw' 
        ```        

#
# Active Tenant Endpoint:

- Get Data:
    - <span style="color:blue">**Get Active Tenant by ID**:</span> Returns the Active Tenant found for an informed Id.    
       - Params:
           - Id: Active Tenant Identifier           
       - Return (status):
           - 200: If exists an active tenant for the Id:
           - 404: If no active tenant could be found
           - 401: In case of not informing a JWT token within AUTHORIZATION header
       - Pattern:
       
        ```
          curl -X GET 'http://localhost:8080/tenantmanagementservice/v1/activeTenant/{id}'
        ```
      
        - Example:
        ```
            curl --location --request GET 'http://localhost:8080/tenantmanagementservice/v1/activeTenant/1' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
        ```    
          
    - <span style="color:blue">**Get All**:</span> Retrieve all the active tenants using a pagination approach
        - Params:
            - search: criteria to be found, applied to the active tenant name (Optional).
            - pageNo: page number to show the first records (Optional).
            - pageSize: max number of pages of results (Optional).
            - sortBy: criteria field to be sorted (Optional).
            - asc: boolean value to show the values ascending or descending way (Optional).
        - Return (status):
            - 200: In case of search operation concluded without issue
            - 401: In case of not informing a JWT token within AUTHORIZATION header
        - Pattern:
            ```
              curl --location --request GET 'http://localhost:8080/tenantmanagementservice/v1/activeTenant?pageNo={pageNumber}&pageSize={pageSize}&sortBy={fieldName}&asc={true|false}&search={searchParam}'
            ```
        - Example:
            ```
            curl --location --request GET 'http://localhost:8080/tenantmanagementservice/v1/activeTenant?pageNo=1&pageSize=10&sortBy=name&asc=true&search=c%25' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    
          
    - <span style="color:blue">**Find:**</span> Retrieves multiple active tenants (List) basing on a search filter that uses the following parameters:
        - Params:
            - user id: user identifier (Optional).
            - tenant id: tenant identifier (Optional).
            - isLogicalConjunction: isLogicalConjunction should the query use a and or a or criteria (Optional).            
            
        - Return (status):
            - 200: In case of search operation concluded without issue.
            - 401: In case of not informing a JWT token within AUTHORIZATION header
        - Pattern:
            ```
            curl --location --request GET 'http://localhost:8080/tenantmanagementservice/v1/activeTenant/find?userId={value}&tenantId={value}&isLogicalConjunction={true|false}'
            ```
        - Example: 
            ```
            curl --location --request GET 'http://localhost:8080/tenantmanagementservice/v1/activeTenant/find?userId=1&tenantId=2&isLogicalConjunction=true' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```              

- Post Data:
    - <span style="color:blue">**Create active tenant:**</span> Creates a new active tenant into the radien database  
       - Params:
           - Active tenant: Json message that corresponds to the active tenant that must be created  
       - Return (status):
           - 200: in case of success operation.
           - 401: In case of not informing a JWT token within AUTHORIZATION header.      
           - 500: In case of any issue      
           
       - Pattern:
       
        ```
          curl --location --request POST 'http://localhost:8080//tenantmanagementservice/v1/activeTenant' \
          --data-raw '{
              "tenantId": [TENANT ID VALUE],
              "userId": [USER ID VALUE] 
          }'
        ```
      
        - Example:
        ```
            curl --location --request POST 'http://localhost:8080//tenantmanagementservice/v1/activeTenant' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]' \
            --header 'Content-Type: application/json' \
            --data-raw '{
                "tenantId": 1,
                "userId": 2 
            }'
        ```    
  
- Put Data:
    - <span style="color:blue">**Update active tenant:**</span> Updates a active tenant into the radien database 
       - Params:
           - Id: Path param that corresponds to the active tenant identifier  
           - Active tenant: Json message containing all active tenant information (to be updated)
       - Return (status):
           - 200: in case of success operation.
           - 401: In case of not informing a JWT token within AUTHORIZATION header.
           - 500: In case of any issue
           
       - Pattern:
       
        ```
          curl --location --request POST 'http://localhost:8080//tenantmanagementservice/v1/activeTenant/{id}' \
          --data-raw '{
              "tenantId": [TENANT ID VALUE],
              "userId": [USER ID VALUE] 
          }'
        ```
      
        - Example:
        ```
            curl --location --request POST 'http://localhost:8080//tenantmanagementservice/v1/activeTenant/11' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]' \
            --header 'Content-Type: application/json' \
            --data-raw '{
                "tenantId": 1,
                "userId": 2 
            }'
        ```    
      
    
- Delete Data:
    - <span style="color:blue">**Delete active tenant:**</span> Deletes a active tenant from radien database
       - Params:
           - Id: Path param that corresponds to the active tenant identifier  
       - Return (status):
           - 200: in case of success operation.
               - Will contain a boolean flag indicating if the row was found and removed
               - If the row was found the response will encapsulate a true value, otherwise false
           - 401: In case of not informing a JWT token within AUTHORIZATION header.
           - 500: In case of any issue                
           
       - Pattern:
       
        ```
          curl --location --request DELETE 'http://localhost:8080//tenantmanagementservice/v1/activeTenant/{id}' \
          --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
        ```
      
        - Example:
        ```
            curl --location --request DELETE 'http://localhost:8080/tenantmanagementservice/v1/activeTenant/222' \
            --header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJJTEtqRFdzbzhUU3NnT1ZuVEZBUlJsWDIxTXBfN29zYUpNTnRwTWVsNV9VIn0.eyJleHAiOjE2MzUyNDYxNzgsImlhdCI6MTYzNTI0MjU3OCwianRpIjoiZmFmMjJiMGEtNmZhNy00YjI5LThlNGUtMmQxMTM3MTNmNmNiIiwiaXNzIjoiaHR0cHM6Ly9pZHAtaW50LnJhZGllbi5pby9hdXRoL3JlYWxtcy9yYWRpZW4iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNWEwODNiYTgtNDdhMS00NjE3LWI2NTktMzg2MTkyYTQ0M2M1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicmFkaWVuIiwic2Vzc2lvbl9zdGF0ZSI6ImIwMjhiNWNkLWUxZjItNDI1Mi05NjkxLWQ1ZjQ3Y2M5MGVlNSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cHM6Ly9sb2NhbGhvc3Q6ODQ0MyJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoia2FyY2gga2lyYWx5IiwicHJlZmVycmVkX3VzZXJuYW1lIjoia2FyY2gua2lyYWx5LXVzZXJuYW1lIiwiZ2l2ZW5fbmFtZSI6ImthcmNoIiwiZmFtaWx5X25hbWUiOiJraXJhbHkiLCJlbWFpbCI6ImthcmNoLmtpcmFseUB5b3BtYWlsLmNvbSJ9.XHuSZgWqGRDS1LHnYacCkH-0mHr4Oaeetm1UzBrjIppv-jPYGroaSEZFSMVdQ4e7sLDPHD4kYyImtDVYKN4_2gvubl3IVEQtL8rBAfjd8YYG7KYxNVDFVbW8mw-KuqfIBN5YQiXetPNbUj15NU_Fz0mu7XPn3UtL7lAZTq6NHVUtxi4Q8IsVqzvAnNvkqRwSUbuHqykk-X4gymCoyGjcuPk0ex4_7Umm0uKMhrKjMYM2ATLEvAjqo4ODkMnSe31y0CBhhcq_Ou2HE7nMTn3ffXjikH7rYNfQhW4JhbDySXLh0e-iHzuuHke35TZhq5gGnnZjd9xaZWO47im9b-l5xw' 
        ```        

    - <span style="color:blue">**Delete Active Tenant By Tenant and User:**</span> Deletes a active tenant from radien database
       - Params:
           - Tenant Id: Path param that corresponds to the tenant identifier  
           - User Id: Path param that corresponds to the user identifier  
       - Return (status):
           - 200: in case of success operation.
               - Will contain a boolean flag indicating if the row was found and removed
               - If the row was found the response will encapsulate a true value, otherwise false
           - 401: In case of not informing a JWT token within AUTHORIZATION header.
           - 500: In case of any issue                
           
       - Pattern:
       
        ```
          curl --location --request DELETE 'http://localhost:8080//tenantmanagementservice/v1/activeTenant/{id}' \
          --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
        ```
      
        - Example:
        ```
            curl --location --request DELETE 'http://localhost:8080/tenantmanagementservice/v1/activeTenant/222' \
            --header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJJTEtqRFdzbzhUU3NnT1ZuVEZBUlJsWDIxTXBfN29zYUpNTnRwTWVsNV9VIn0.eyJleHAiOjE2MzUyNDYxNzgsImlhdCI6MTYzNTI0MjU3OCwianRpIjoiZmFmMjJiMGEtNmZhNy00YjI5LThlNGUtMmQxMTM3MTNmNmNiIiwiaXNzIjoiaHR0cHM6Ly9pZHAtaW50LnJhZGllbi5pby9hdXRoL3JlYWxtcy9yYWRpZW4iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNWEwODNiYTgtNDdhMS00NjE3LWI2NTktMzg2MTkyYTQ0M2M1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicmFkaWVuIiwic2Vzc2lvbl9zdGF0ZSI6ImIwMjhiNWNkLWUxZjItNDI1Mi05NjkxLWQ1ZjQ3Y2M5MGVlNSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cHM6Ly9sb2NhbGhvc3Q6ODQ0MyJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoia2FyY2gga2lyYWx5IiwicHJlZmVycmVkX3VzZXJuYW1lIjoia2FyY2gua2lyYWx5LXVzZXJuYW1lIiwiZ2l2ZW5fbmFtZSI6ImthcmNoIiwiZmFtaWx5X25hbWUiOiJraXJhbHkiLCJlbWFpbCI6ImthcmNoLmtpcmFseUB5b3BtYWlsLmNvbSJ9.XHuSZgWqGRDS1LHnYacCkH-0mHr4Oaeetm1UzBrjIppv-jPYGroaSEZFSMVdQ4e7sLDPHD4kYyImtDVYKN4_2gvubl3IVEQtL8rBAfjd8YYG7KYxNVDFVbW8mw-KuqfIBN5YQiXetPNbUj15NU_Fz0mu7XPn3UtL7lAZTq6NHVUtxi4Q8IsVqzvAnNvkqRwSUbuHqykk-X4gymCoyGjcuPk0ex4_7Umm0uKMhrKjMYM2ATLEvAjqo4ODkMnSe31y0CBhhcq_Ou2HE7nMTn3ffXjikH7rYNfQhW4JhbDySXLh0e-iHzuuHke35TZhq5gGnnZjd9xaZWO47im9b-l5xw' 
        ```    