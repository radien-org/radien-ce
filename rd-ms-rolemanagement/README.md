# Radien Tenant Role Permission Management Microservice
Microservice that provides REST APIs to manage domain objects regarding **Role**, **TenantRole**, **TenantRoleTenant Role Permission** and **TenantRoleUser**.

For each of these entities there will be one particular endpoint providing CRUD operations. 

![Tenant Role Permission domain model](doc/Role-domain-model.png)

A small glimpses regarding the following entities:
   - TenantRole: Corresponds to an association between Tenant (handled by Tenant management microservice) and Role
   - TenantRoleTenant Role Permission: Corresponds to an association between Tenant, Role and Tenant Role Permission (handled by Tenant Role Permission management microservice)
   - TenantRoleUser: Corresponds to an association between Tenant, Role and User
   
# Pre requirements:

## Running as executable Jar file 

### - ___Package:___
    mvn -P tomee package

### - ___Run:___ 
    java -jar target/rd-ms-permissionmanagement-service.jar

### - ___Local TomEE Configuration:___
    URL: https://localhost:9080/rolemanagementservice/v1/role
    HTTP Port: 8083
    HTTPS Port: 8445
    JMX Port: 1101
    Deployment: rd-ms-rolemanagement:war exploded
    Application Context: /rolemanagementservice

## Running from IDE (ex: Intellij)

<img src="doc/config-rmms-intellij-part1.png" width="400" height="400"/>

<img src="doc/config-rmms-intellij-part2.png" width="400" height="400"/>

#
# cURLs: 
Let's check the operations that exists for each endpoint and how to perform them via cURL (using http port 8080)

#
# Role Endpoint:

- Get Data:
    - <span style="color:blue">**Get Role by ID**:</span> Returns the Role found for an informed Id.    
       - Params:
           - Id: Role Identifier           
       - Return (status):
           - 200: If exists a role for the Id:
           - 404: If no role could be found
           - 401: In case of not informing a JWT token within AUTHORIZATION header
       - Pattern:
       
        ```
          curl -X GET 'http://localhost:8080/rolemanagementservice/v1/role/{id}'
        ```
      
        - Example:
        ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/role/1' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
        ```    
          
    - <span style="color:blue">**Get All**:</span> Retrieve all the roles using a pagination approach
        - Params:
            - search: criteria to be found, applied to the role name or role description (Optional).
            - pageNo: page number to show the first records (Optional).
            - pageSize: max number of pages of results (Optional).
            - sortBy: criteria field to be sorted (Optional).
            - asc: boolean value to show the values ascending or descending way (Optional).
        - Return (status):
            - 200: In case of search operation concluded without issue
            - 401: In case of not informing a JWT token within AUTHORIZATION header
        - Pattern:
            ```
              curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/role?pageNo={pageNumber}&pageSize={pageSize}&sortBy={fieldName}&asc={true|false}&search={searchParam}'
            ```
        - Example:
            ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/role?pageNo=1&pageSize=10&sortBy=name&asc=true&search=c%25' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    
          
    - <span style="color:blue">**Find:**</span> Retrieves multiple roles (List) basing on a search filter that uses the following parameters:
        - Params:
            - name: role name (Optional).
            - description: role description (Optional)
            - ids: ids to be found (Optional).
            - isExact: isExact should the search fields be exact or not as given (Optional).
            - isLogicalConjunction: isLogicalConjunction should the query use a and or a or criteria (Optional).            
            
        - Return (status):
            - 200: In case of search operation concluded without issue.
            - 401: In case of not informing a JWT token within AUTHORIZATION header
        - Pattern:
            ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/role/find?name={value}&ids={ids for role}&isExact={true|false}&isLogicalConjunction={true|false}'
            ```
        - Example: Searching using name and description parameters (Value do not need to be exact)
            ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/role/find?name=Administrator&description=Admin&isExact=false&isLogicalConjunction=true' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    
        - Example 2: Searching using an ID List
            ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/role/find?ids=1&ids=2&ids=3' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```              

- Post Data:
    - <span style="color:blue">**Create Role:**</span> Creates a new role into the radien database  
       - Params:
           - role: Json message that corresponds to the role that must be created  
       - Return (status):
           - 200: in case of success operation.
           - 400: in case of invalid parameters (ex: Repeated values already inserted)
           - 401: In case of not informing a JWT token within AUTHORIZATION header.      
           
       - Pattern:
       
        ```
          curl --location --request POST 'http://localhost:8080//rolemanagementservice/v1/role' \
          --data-raw '{
              "name": [ACTION NAME VALUE],
              "description": [ROLE DESCRIPTION VALUE]
          }'
        ```
      
        - Example:
        ```
            curl --location --request POST 'http://localhost:8080//rolemanagementservice/v1/role'' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]' \
            --header 'Content-Type: application/json' \
            --data-raw '{
               "name": "Permission Administrator",
               "description": "The Role for Permission Management Testing purposes!"
            }'
        ```    
  
- Put Data:
    - <span style="color:blue">**Update role:**</span> Updates a role into the radien database 
       - Params:
           - Id: Path param that corresponds to the role identifier  
           - role: Json message containing all role information (to be updated)
       - Return (status):
           - 200: in case of success operation.
           - 400: in case of invalid parameters (ex: Repeated values already inserted)
           - 401: In case of not informing a JWT token within AUTHORIZATION header.
           - 404: If no role can be found for Id parameter         
           
       - Pattern:
       
        ```
          curl --location --request PUT 'http://localhost:8080//rolemanagementservice/v1/role/{id}' \
          --data-raw '{
              "name": [ACTION NAME VALUE],
              "description": [ROLE DESCRIPTION VALUE]          
          }'
        ```
      
        - Example:
        ```
            curl --location --request PUT 'http://localhost:8080//rolemanagementservice/v1/role/1' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]' \
            --header 'Content-Type: application/json' \
            --data-raw '{
               "name": "Permission Administrator",
               "description": "The Role for Permission Management Testing purposes!"
            }'
        ```        
    
- Delete Data:
    - <span style="color:blue">**Delete role:**</span> Deletes a role from radien database
       - Params:
           - Id: Path param that corresponds to the role identifier  
       - Return (status):
           - 200: in case of success operation.
           - 401: In case of not informing a JWT token within AUTHORIZATION header.
           - 404: If no role can be found for Id parameter                
           
       - Pattern:
       
        ```
          curl --location --request DELETE 'http://localhost:8080//rolemanagementservice/v1/role/{id}' \
          --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
        ```
      
        - Example:
        ```
            curl --location --request DELETE 'http://localhost:8080/rolemanagementservice/v1/role/222' \
            --header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJJTEtqRFdzbzhUU3NnT1ZuVEZBUlJsWDIxTXBfN29zYUpNTnRwTWVsNV9VIn0.eyJleHAiOjE2MzUyNDYxNzgsImlhdCI6MTYzNTI0MjU3OCwianRpIjoiZmFmMjJiMGEtNmZhNy00YjI5LThlNGUtMmQxMTM3MTNmNmNiIiwiaXNzIjoiaHR0cHM6Ly9pZHAtaW50LnJhZGllbi5pby9hdXRoL3JlYWxtcy9yYWRpZW4iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNWEwODNiYTgtNDdhMS00NjE3LWI2NTktMzg2MTkyYTQ0M2M1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicmFkaWVuIiwic2Vzc2lvbl9zdGF0ZSI6ImIwMjhiNWNkLWUxZjItNDI1Mi05NjkxLWQ1ZjQ3Y2M5MGVlNSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cHM6Ly9sb2NhbGhvc3Q6ODQ0MyJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoia2FyY2gga2lyYWx5IiwicHJlZmVycmVkX3VzZXJuYW1lIjoia2FyY2gua2lyYWx5LXVzZXJuYW1lIiwiZ2l2ZW5fbmFtZSI6ImthcmNoIiwiZmFtaWx5X25hbWUiOiJraXJhbHkiLCJlbWFpbCI6ImthcmNoLmtpcmFseUB5b3BtYWlsLmNvbSJ9.XHuSZgWqGRDS1LHnYacCkH-0mHr4Oaeetm1UzBrjIppv-jPYGroaSEZFSMVdQ4e7sLDPHD4kYyImtDVYKN4_2gvubl3IVEQtL8rBAfjd8YYG7KYxNVDFVbW8mw-KuqfIBN5YQiXetPNbUj15NU_Fz0mu7XPn3UtL7lAZTq6NHVUtxi4Q8IsVqzvAnNvkqRwSUbuHqykk-X4gymCoyGjcuPk0ex4_7Umm0uKMhrKjMYM2ATLEvAjqo4ODkMnSe31y0CBhhcq_Ou2HE7nMTn3ffXjikH7rYNfQhW4JhbDySXLh0e-iHzuuHke35TZhq5gGnnZjd9xaZWO47im9b-l5xw' 
        ```        

#
# Tenant Role Endpoint:

- Get Data:
    - <span style="color:blue">**Get Tenant Role by ID**:</span> Returns the Tenant Role found for an informed Id.    
       - Params:
           - Id: Tenant Role Identifier           
       - Return (status):
           - 200: If exists a Tenant Role for the Id:
           - 404: If no Tenant Role could be found
           - 401: In case of not informing a JWT token within AUTHORIZATION header
       - Pattern:
       
        ```
          curl -X GET 'http://localhost:8080/rolemanagementservice/v1/tenantrole/{id}'
        ```
      
        - Example:
        ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantrole/1' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
        ```    
      
    - <span style="color:blue">**Get Id By Tenant And Role**:</span> Retrieve the association Id using the combination of tenant (id) and role as parameters    
       - Params:
           - tenantId: Tenant Identifier (Mandatory)           
           - roleId: Role Identifier (Mandatory)          
       - Return (status):
           - 200: Response OK (200) with the retrieved id (if exists)
           - 400: In case of insufficient params (tenant or role not informed) It will return 400 status.
           - 404: If not exist will return 404 status.
           - 401: In case of not informing a JWT token within AUTHORIZATION header
       - Pattern:
       
        ```
          curl -X GET 'http://localhost:8080/rolemanagementservice/v1/tenantrole/id?tenantId={value}&roleId={value}'
        ```
      
        - Example:
        ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantrole/id?tenantId=1&roleId=12' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
        ```    
          
    - <span style="color:blue">**Get All**:</span> Retrieve all the Tenant Roles using a pagination approach
        - Params:
            - tenantId: tenant id (Optional).
            - roleId: roleId (Optional).
            - pageNo: page number to show the first records (Optional).
            - pageSize: max number of pages of results (Optional).
            - sortBy: criteria field to be sorted (Optional).
            - asc: boolean value to show the values ascending or descending way (Optional).
        - Return (status):
            - 200: In case of search operation concluded without issue
            - 401: In case of not informing a JWT token within AUTHORIZATION header
        - Pattern:
            ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantrole?tenantId={value}&roleId={value}&pageNo={pageNumber}&pageSize={pageSize}&sortBy={fieldName}&asc={true|false}'
            ```
        - Example: A Paginated filter using tenantId=1 as parameter
            ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantrole?tenantId=1&pageNo=1&pageSize=10&sortBy=id&asc=false' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    
          
    - <span style="color:blue">**Find:**</span> Retrieves multiple Tenant Roles (List) basing on a search filter that uses the following parameters:
        - Params:
            - tenantId: tenant id (Optional).
            - roleId: roleId (Optional).
            - isLogicalConjunction: isLogicalConjunction should the query use a and or a or criteria (Optional).            
            
        - Return (status):
            - 200: In case of search operation concluded without issue.
            - 401: In case of not informing a JWT token within AUTHORIZATION header
        - Pattern:
            ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantrole/find?tenantId={value}&roleId={value}&isLogicalConjunction={true|false}'
            ```
        - Example: Searching using tenantId=1 **and** roleId=2 (AND => isLogicalConjunction=true) 
            ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantrole/find?tenantId=1&roleId=2&isLogicalConjunction=true' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    
          
    - <span style="color:blue">**Check if Tenant Role combination exists:**</span> Check if a Tenant role association exists
        - Params:
            - tenantId: tenant identifier (Mandatory).
            - roleId: role identifier (Mandatory).            
            
        - Return (status):
            - 204: If association exists.
            - 401: In case of not informing a JWT token within AUTHORIZATION header.
            - 404: If association DO NOT exist.            
            
        - Pattern:
            ```
            curl --location --head 'http://localhost:8080/rolemanagementservice/v1/tenantrole/{tenantId}/{roleId}'
            ```
        - Example:  
            ```
            curl --location --head 'http://localhost:8080/rolemanagementservice/v1/tenantrole/2/1' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    
          
    - <span style="color:blue">**Check If User Has a Role:**</span> Check if one Role exists for a User (Optionally under a Tenant)
        - Params:
            - userId: User identifier (Mandatory).
            - roleName: Role name descriptor (Mandatory).   
            - tenantId: Tenant identifier (Optional)
            
        - Return (status):
            - 200: Response OK containing a boolean value (true if role is associated to a User, otherwise false).
            - 401: In case of not informing a JWT token within AUTHORIZATION header.
            - 404: In case of absence of parameter like user identifier or role name.
            - 500: In case of any error         
            
        - Pattern:
            ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantrole/exists/role?userId={value}&roleName={value}&tenantId={value}'
            ```
        - Example:  
            ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantrole/exists/role?userId=2&roleName=SYSTEM%20ADMINISTRATOR&tenantId=102' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    

    - <span style="color:blue">**Check If User Has Some Role:**</span>Given a list of roles, check if user has some of them (Optionally under a Tenant)
        - Params:
            - userId: User identifier (Mandatory).
            - roleNames: List of Role name descriptors (Mandatory).   
            - tenantId: Tenant identifier (Optional)
            
        - Return (status):
            - 200: Response OK containing a boolean value (true if role is associated to a User, otherwise false).
            - 401: In case of not informing a JWT token within AUTHORIZATION header.
            - 404: In case of absence of parameter like user identifier or role name.
            - 500: In case of any error         
            
        - Pattern:
            ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantrole/exists/roles?userId={value}&roleNames={value}&roleName={value2}&tenantId={value}'
            ```
        - Example:  
            ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantrole/exists/roles?userId=2&roleNames=SYSTEM%20ADMINISTRATOR&roleNames=USER%20ADMINISTRATOR&tenantId=102' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    

    - <span style="color:blue">**Check If User Has a Permission:**</span>Check if Permission exists for a User (Optionally under a Tenant)
        - Params:
            - userId: User identifier (Mandatory).
            - permissionId: Permission identifier (Mandatory).   
            - tenantId: Tenant identifier (Optional)
                
        - Return (status):
            - 200: Response OK containing a boolean value (true if User has the permission, otherwise false).
            - 401: In case of not informing a JWT token within AUTHORIZATION header.
            - 404: In case of absence of parameter like user identifier or permission id.
            - 500: In case of any error         
            
        - Pattern:
            ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantrole/exists/permission?userId={value}&permissionId={value}&tenantId={value}'
            ```
        - Example:  
            ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantrole/exists/permission?userId=2&permissionId=1&tenantId=102' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    

- Post Data:
    - <span style="color:blue">**Create Tenant Role:**</span> Creates a new Tenant Role into the radien database  
       - Params:
           - Tenant Role : Json message that corresponds to the Tenant Role that must be created  
       - Return (status):
           - 200: in case of success operation.
           - 400: in case of invalid parameters (ex: Repeated values already inserted)
           - 401: In case of not informing a JWT token within AUTHORIZATION header.      
           
       - Pattern:
       
        ```
          curl --location --request POST 'http://localhost:8080//rolemanagementservice/v1/tenantrole' \
          --data-raw '{
              "tenantId": [TENANT ID VALUE],
              "roleId": [ROLE ID VALUE]
          }'
        ```
      
        - Example:
        ```
            curl --location --request POST 'http://localhost:8080//rolemanagementservice/v1/tenantrole' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]' \
            --header 'Content-Type: application/json' \
            --data-raw '{
                 "tenantId": 44,
                 "roleId": 64
            }'
        ```    
  
- Put Data:
    - <span style="color:blue">**Update Tenant Role:**</span> Updates a Tenant Role into the radien database 
       - Params:
           - Id: Path param that corresponds to the tenant role identifier  
           - Tenant Role: Json message containing all tenant role information (to be updated)
       - Return (status):
           - 200: in case of success operation.
           - 400: in case of duplication (association already existing with the same parameter) or absence of information (tenant or role not existing)
           - 401: In case of not informing a JWT token within AUTHORIZATION header.
           - 404: If there is no TenantRole for the informed Id         
           
       - Pattern:
       
        ```
          curl --location --request PUT 'http://localhost:8080//rolemanagementservice/v1/tenantrole/{id}' \
          --data-raw '{
              "tenantId": [TENANT ID VALUE],
              "roleId": [ROLE ID VALUE]
          }'
        ```
      
        - Example:
        ```
            curl --location --request PUT 'http://localhost:8080//rolemanagementservice/v1/tenantrole/1' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]' \
            --header 'Content-Type: application/json' \
            --data-raw '{
                 "tenantId": 44,
                 "roleId": 64
            }'
        ```        
    
- Delete Data:
    - <span style="color:blue">**Delete Tenant Role:**</span> Deletes a Tenant Role from radien database
       - Params:
           - Id: Path param that corresponds to the Tenant Role identifier  
       - Return (status):
           - 200: in case of success operation.
           - 401: In case of not informing a JWT token within AUTHORIZATION header.
           - 400: 
               - if association could not be found, 
               - if the association is attached to other entities (i.e TenantRoleUser, TenantRolePermission, etc)                
           
       - Pattern:
       
        ```
          curl --location --request DELETE 'http://localhost:8080//rolemanagementservice/v1/tenantrole/{id}' \
          --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
        ```
      
        - Example:
        ```
            curl --location --request DELETE 'http://localhost:8080/rolemanagementservice/v1/tenantrole/222' \
            --header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJJTEtqRFdzbzhUU3NnT1ZuVEZBUlJsWDIxTXBfN29zYUpNTnRwTWVsNV9VIn0.eyJleHAiOjE2MzUyNDYxNzgsImlhdCI6MTYzNTI0MjU3OCwianRpIjoiZmFmMjJiMGEtNmZhNy00YjI5LThlNGUtMmQxMTM3MTNmNmNiIiwiaXNzIjoiaHR0cHM6Ly9pZHAtaW50LnJhZGllbi5pby9hdXRoL3JlYWxtcy9yYWRpZW4iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNWEwODNiYTgtNDdhMS00NjE3LWI2NTktMzg2MTkyYTQ0M2M1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicmFkaWVuIiwic2Vzc2lvbl9zdGF0ZSI6ImIwMjhiNWNkLWUxZjItNDI1Mi05NjkxLWQ1ZjQ3Y2M5MGVlNSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cHM6Ly9sb2NhbGhvc3Q6ODQ0MyJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoia2FyY2gga2lyYWx5IiwicHJlZmVycmVkX3VzZXJuYW1lIjoia2FyY2gua2lyYWx5LXVzZXJuYW1lIiwiZ2l2ZW5fbmFtZSI6ImthcmNoIiwiZmFtaWx5X25hbWUiOiJraXJhbHkiLCJlbWFpbCI6ImthcmNoLmtpcmFseUB5b3BtYWlsLmNvbSJ9.XHuSZgWqGRDS1LHnYacCkH-0mHr4Oaeetm1UzBrjIppv-jPYGroaSEZFSMVdQ4e7sLDPHD4kYyImtDVYKN4_2gvubl3IVEQtL8rBAfjd8YYG7KYxNVDFVbW8mw-KuqfIBN5YQiXetPNbUj15NU_Fz0mu7XPn3UtL7lAZTq6NHVUtxi4Q8IsVqzvAnNvkqRwSUbuHqykk-X4gymCoyGjcuPk0ex4_7Umm0uKMhrKjMYM2ATLEvAjqo4ODkMnSe31y0CBhhcq_Ou2HE7nMTn3ffXjikH7rYNfQhW4JhbDySXLh0e-iHzuuHke35TZhq5gGnnZjd9xaZWO47im9b-l5xw' 
        ```        

#
# Tenant Role Permission Endpoint:

- Get Data:
    - <span style="color:blue">**Get Tenant Role Permission by ID**:</span> Returns the Tenant Role Permission found for an informed Id.    
       - Params:
           - Id: Tenant Role Permission Identifier           
       - Return (status):
           - 200: If exists a Tenant Role Permission for the Id:
           - 404: If no Tenant Role Permission Tenant Role Permission  could be found
           - 401: In case of not informing a JWT token within AUTHORIZATION header
       - Pattern:
       
        ```
          curl -X GET 'http://localhost:8080/rolemanagementservice/v1/tenantrolepermission/{id}'
        ```
      
        - Example:
        ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantrolepermission/1' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
        ```    
          
    - <span style="color:blue">**Get All**:</span> Retrieve all the Tenant Role Permissions using a pagination approach
        - Params:
            - tenantRoleId: identifier for a TenantRole (Optional)
            - permissionId: identifier for a permission (Optional)
            - pageNo: page number to show the first records (Optional).
            - pageSize: max number of pages of results (Optional).
            - sortBy: criteria field to be sorted (Optional).
            - asc: boolean value to show the values ascending or descending way (Optional).
        - Return (status):
            - 200: In case of search operation concluded without issue
            - 401: In case of not informing a JWT token within AUTHORIZATION header
        - Pattern:
            ```
              curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantrolepermission?tenantRoleId={value}&permissionId={value}&pageNo={pageNumber}&pageSize={pageSize}&sortBy={fieldName}&asc={true|false}&search={searchParam}'
            ```
        - Example:
            ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantrolepermission?tenantId=1&permissionId=2pageNo=1&pageSize=10&sortBy=name&asc=true&search=c%25' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    
          
    - <span style="color:blue">**Find:**</span> Retrieves multiple permissions (List) basing on a search filter that uses the following parameters:
        - Params:
            - tenantRoleId: identifier for a TenantRole (Optional)
            - permissionId: identifier for a permission (Optional)
            - isLogicalConjunction: isLogicalConjunction should the query use a and or a or criteria (Optional).            
            
        - Return (status):
            - 200: In case of search operation concluded without issue.
            - 401: In case of not informing a JWT token within AUTHORIZATION header
        - Pattern:
            ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantrolepermission/find?tenantRoleId={value}&permissionId={value}&isExact={true|false}&isLogicalConjunction={true|false}'
            ```
        - Example: Searching using tenantRoleId, permission as parameters (Using **isLogicalConjunction=false** means that will be performed an "OR" operation)
            ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantrolepermission/find?tenantRoleId=1&permissionId=1&isLogicalConjunction=false' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    
          
    - <span style="color:blue">**Get Permissions:**</span> Retrieves the Permissions that exists for a Tenant Role Association (Optionally taking in account user)
        - Params:
            - tenantId: identifier for a Tenant (Mandatory)
            - roleId: identifier for a Role (Mandatory)
            - userId: identifier for a User (Optional).            
            
        - Return (status):
            - 200: In case of search operation concluded without issue (Response OK with List containing permissions).
            - 401: In case of not informing a JWT token within AUTHORIZATION header
        - Pattern:
            ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantrolepermission/permissions?tenantId={value}&roleId={value}&userId={value}'
            ```
        - Example:
            ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantrolepermission/permissions?tenantId=102&roleId=1&userId=3' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    

- Post Data:
    - <span style="color:blue">**Create a Tenant Role Permission:**</span> Creates a new Tenant Role Permission into the radien database  
       - Params:
           - Tenant Role Permission: Json message that corresponds to the Tenant Role Permission that must be created  
       - Return (status):
           - 200: in case of success operation.
           - 400: in case of invalid parameters (ex: Repeated values already inserted)
           - 401: In case of not informing a JWT token within AUTHORIZATION header.
           - 404: If no permission can be found for Id parameter        
           
       - Pattern:
       
        ```
          curl --location --request POST 'http://localhost:8080//rolemanagementservice/v1/tenantrolepermission' \
          --data-raw '{
              "tenantRoleId": [TENANT ROLE ID VALUE],
              "permissionId": [PERMISSION ID VALUE]
          }'
        ```
      
        - Example:
        ```
            curl --location --request POST 'http://localhost:8080//rolemanagementservice/v1/tenantrolepermission' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]' \
            --header 'Content-Type: application/json' \
            --data-raw '{
               "tenantRoleId": 1,
               "permissionId": 1001
            }'
        ```    
  
- Put Data:
    - <span style="color:blue">**Update Tenant Role Permission:**</span> Updates a Tenant Role Permission into the radien database 
       - Params:
           - Id: Path param that corresponds to the permission identifier  
           - Tenant Role Permission: Json message containing Tenant Role Permission information (to be updated)
       - Return (status):
           - 200: in case of success operation.
           - 400: in case of invalid parameters (ex: Repeated values already inserted)
           - 401: In case of not informing a JWT token within AUTHORIZATION header.
           - 404: If no permission can be found for Id parameter         
           
       - Pattern:
       
        ```
          curl --location --request PUT 'http://localhost:8080//rolemanagementservice/v1/tenantrolepermission/{id}' \
          --data-raw '{
              "tenantRoleId": [TENANT ROLE ID VALUE],
              "permissionId": [PERMISSION ID VALUE]
          }'
        ```
      
        - Example:
        ```
            curl --location --request PUT 'http://localhost:8080//rolemanagementservice/v1/tenantrolepermission/1' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]' \
            --header 'Content-Type: application/json' \
            --data-raw '{
               "tenantRoleId": 1,
               "permissionId": 1001
            }'
        ```        
    
- Delete Data:
    - <span style="color:blue">**Delete Tenant Role Permission:**</span> Deletes a tenant role permission from radien database
       - Params:
           - Id: Path param that corresponds to the tenant role permission identifier  
       - Return (status):
           - 200: in case of success operation.
           - 401: In case of not informing a JWT token within AUTHORIZATION header.
           - 400: If no permission can be found for Id parameter                
           
       - Pattern:
       
        ```
          curl --location --request DELETE 'http://localhost:8080//rolemanagementservice/v1/tenantrolepermission/{id}' \
          --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
        ```
      
        - Example:
        ```
            curl --location --request DELETE 'http://localhost:8080/rolemanagementservice/v1/tenantrolepermission/222' \
            --header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJJTEtqRFdzbzhUU3NnT1ZuVEZBUlJsWDIxTXBfN29zYUpNTnRwTWVsNV9VIn0.eyJleHAiOjE2MzUyNDYxNzgsImlhdCI6MTYzNTI0MjU3OCwianRpIjoiZmFmMjJiMGEtNmZhNy00YjI5LThlNGUtMmQxMTM3MTNmNmNiIiwiaXNzIjoiaHR0cHM6Ly9pZHAtaW50LnJhZGllbi5pby9hdXRoL3JlYWxtcy9yYWRpZW4iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNWEwODNiYTgtNDdhMS00NjE3LWI2NTktMzg2MTkyYTQ0M2M1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicmFkaWVuIiwic2Vzc2lvbl9zdGF0ZSI6ImIwMjhiNWNkLWUxZjItNDI1Mi05NjkxLWQ1ZjQ3Y2M5MGVlNSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cHM6Ly9sb2NhbGhvc3Q6ODQ0MyJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoia2FyY2gga2lyYWx5IiwicHJlZmVycmVkX3VzZXJuYW1lIjoia2FyY2gua2lyYWx5LXVzZXJuYW1lIiwiZ2l2ZW5fbmFtZSI6ImthcmNoIiwiZmFtaWx5X25hbWUiOiJraXJhbHkiLCJlbWFpbCI6ImthcmNoLmtpcmFseUB5b3BtYWlsLmNvbSJ9.XHuSZgWqGRDS1LHnYacCkH-0mHr4Oaeetm1UzBrjIppv-jPYGroaSEZFSMVdQ4e7sLDPHD4kYyImtDVYKN4_2gvubl3IVEQtL8rBAfjd8YYG7KYxNVDFVbW8mw-KuqfIBN5YQiXetPNbUj15NU_Fz0mu7XPn3UtL7lAZTq6NHVUtxi4Q8IsVqzvAnNvkqRwSUbuHqykk-X4gymCoyGjcuPk0ex4_7Umm0uKMhrKjMYM2ATLEvAjqo4ODkMnSe31y0CBhhcq_Ou2HE7nMTn3ffXjikH7rYNfQhW4JhbDySXLh0e-iHzuuHke35TZhq5gGnnZjd9xaZWO47im9b-l5xw' 
        ```
              
    - <span style="color:blue">**UnAssign Tenant Role Permission:**</span> Deletes a TenantRolePermission taking in consideration the following parameters:
       - Params:
           - tenantId Tenant identifier (Mandatory)
           - roleId Role identifier (Mandatory)
           - permissionId Permission identifier (Mandatory) 
       - Return (status):
           - 200: Response OK if operation concludes with success
           - 400: in case of inconsistencies or issues found (ex: Mandatory parameters not informed, or TenantRolePermission not existent for the informed parameters)
           - 401: In case of not informing a JWT token within AUTHORIZATION header.
           
       - Pattern:
       
        ```
          curl --location --request DELETE 'http://localhost:8080/rolemanagementservice/v1/tenantrolepermission?tenantId={value}&roleId={value}&permissionId={value}' \
          --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
        ```
      
        - Example:
        ```
            curl --location --request DELETE 'http://localhost:8080/rolemanagementservice/v1/tenantrolepermission?tenantId=1&roleId=1&permissionId=1111' \
            --header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJJTEtqRFdzbzhUU3NnT1ZuVEZBUlJsWDIxTXBfN29zYUpNTnRwTWVsNV9VIn0.eyJleHAiOjE2MzUyNDYxNzgsImlhdCI6MTYzNTI0MjU3OCwianRpIjoiZmFmMjJiMGEtNmZhNy00YjI5LThlNGUtMmQxMTM3MTNmNmNiIiwiaXNzIjoiaHR0cHM6Ly9pZHAtaW50LnJhZGllbi5pby9hdXRoL3JlYWxtcy9yYWRpZW4iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNWEwODNiYTgtNDdhMS00NjE3LWI2NTktMzg2MTkyYTQ0M2M1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicmFkaWVuIiwic2Vzc2lvbl9zdGF0ZSI6ImIwMjhiNWNkLWUxZjItNDI1Mi05NjkxLWQ1ZjQ3Y2M5MGVlNSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cHM6Ly9sb2NhbGhvc3Q6ODQ0MyJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoia2FyY2gga2lyYWx5IiwicHJlZmVycmVkX3VzZXJuYW1lIjoia2FyY2gua2lyYWx5LXVzZXJuYW1lIiwiZ2l2ZW5fbmFtZSI6ImthcmNoIiwiZmFtaWx5X25hbWUiOiJraXJhbHkiLCJlbWFpbCI6ImthcmNoLmtpcmFseUB5b3BtYWlsLmNvbSJ9.XHuSZgWqGRDS1LHnYacCkH-0mHr4Oaeetm1UzBrjIppv-jPYGroaSEZFSMVdQ4e7sLDPHD4kYyImtDVYKN4_2gvubl3IVEQtL8rBAfjd8YYG7KYxNVDFVbW8mw-KuqfIBN5YQiXetPNbUj15NU_Fz0mu7XPn3UtL7lAZTq6NHVUtxi4Q8IsVqzvAnNvkqRwSUbuHqykk-X4gymCoyGjcuPk0ex4_7Umm0uKMhrKjMYM2ATLEvAjqo4ODkMnSe31y0CBhhcq_Ou2HE7nMTn3ffXjikH7rYNfQhW4JhbDySXLh0e-iHzuuHke35TZhq5gGnnZjd9xaZWO47im9b-l5xw' 
        ```        

#
# Tenant Role User Endpoint:

- Get Data:
    - <span style="color:blue">**Get Tenant Role User by ID**:</span> Returns the Tenant Role User found for an informed Id.    
       - Params:
           - Id: Tenant Role User Identifier           
       - Return (status):
           - 200: If exists a Tenant Role User for the Id:
           - 404: If no Tenant Role User Tenant Role User  could be found
           - 401: In case of not informing a JWT token within AUTHORIZATION header
       - Pattern:
       
        ```
          curl -X GET 'http://localhost:8080/rolemanagementservice/v1/tenantroleuser/{id}'
        ```
      
        - Example:
        ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantroleuser/1' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
        ```    
          
    - <span style="color:blue">**Get All**:</span> Retrieve all the Tenant Role Users using a pagination approach
        - Params:
            - tenantRoleId: identifier for a TenantRole (Optional)
            - userId: identifier for a user (Optional)
            - pageNo: page number to show the first records (Optional).
            - pageSize: max number of pages of results (Optional).
            - sortBy: criteria field to be sorted (Optional).
            - asc: boolean value to show the values ascending or descending way (Optional).
        - Return (status):
            - 200: In case of search operation concluded without issue
            - 401: In case of not informing a JWT token within AUTHORIZATION header
        - Pattern:
            ```
              curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantroleuser?tenantRoleId={value}&userId={value}&pageNo={pageNumber}&pageSize={pageSize}&sortBy={fieldName}&asc={true|false}'
            ```
        - Example:
            ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantroleuser?tenantId=1&userId=2pageNo=1&pageSize=10&sortBy=name&asc=true' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    
          
    - <span style="color:blue">**Get All User Ids**:</span> Retrieves TenantRoleUser association (Ids) using pagination approach (in other words, retrieves the Users associations that exist for a TenantRole)
        - Params:
            - tenantId: identifier for a Tenant (Optional)
            - roleId: identifier for a Role (Optional)
            - pageNo: page number to show the first records (Optional).
            - pageSize: max number of pages of results (Optional).
            - sortBy: criteria field to be sorted (Optional).
            - asc: boolean value to show the values ascending or descending way (Optional).
        - Return (status):
            - 200: In case of successful operation returns OK (http status 200) and a Page containing TenantRole associations Ids (Chunk/Portion compatible with parameter Page number and Page size)
            - 401: In case of not informing a JWT token within AUTHORIZATION header
        - Pattern:
            ```
              curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantroleuser/userIds?tenantId={value}&roleId={value}&pageNo={pageNumber}&pageSize={pageSize}&sortBy={fieldName}&asc={true|false}'
            ```
        - Example:
            ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantroleuser?tenantId=1&userId=2pageNo=1&pageSize=10&sortBy=id&asc=true' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```
          
    - <span style="color:blue">**Find:**</span> Retrieves multiple tenant role users (List) basing on a search filter that uses the following parameters:
        - Params:
            - tenantRoleId: identifier for a TenantRole (Optional)
            - userId: identifier for a user (Optional)
            - isLogicalConjunction: isLogicalConjunction should the query use a and or a or criteria (Optional).            
            
        - Return (status):
            - 200: In case of search operation concluded without issue.
            - 401: In case of not informing a JWT token within AUTHORIZATION header
        - Pattern:
            ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantroleuser/find?tenantRoleId={value}&userId={value}&isExact={true|false}&isLogicalConjunction={true|false}'
            ```
        - Example: Searching using tenantRoleId, user as parameters (Using **isLogicalConjunction=false** means that will be performed an "OR" operation)
            ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantroleuser/find?tenantRoleId=1&userId=1&isLogicalConjunction=false' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    
          
    - <span style="color:blue">**Get Tenants:**</span> Retrieves the existent Tenants for a User (Optionally for a specific role)
        - Params:
            - userId: User identifier (Mandatory)
            - roleId: Role identifier (Optional)

        - Return (status):
            - 200: In case of search operation concluded without issue (Response OK with List containing tenants).
            - 400: In case of invalid request (lack of user information)
            - 401: In case of not informing a JWT token within AUTHORIZATION header
            - 500: In case of any other error

        - Pattern:
            ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantroleuser/tenants?userId={value}&roleId={value}'
            ```
        - Example:
            ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantroleuser/tenants?userId=1&roleId=1' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    
         
    - <span style="color:blue">**Get Roles:**</span> Retrieves the Roles for which a User is associated under a Tenant
        - Params:
            - userId: User identifier (MANDATORY)
            - roleId: Role identifier (MANDATORY)

        - Return (status):
            - 200: In case of search operation concluded without issue (Response OK with List containing roles).
            - 401: In case of not informing a JWT token within AUTHORIZATION header
            - 500: In case of any other error.

        - Pattern:
            ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantroleuser/roles?userId={value}&tenantId={value}'
            ```
        - Example:
            ```
            curl --location --request GET 'http://localhost:8080/rolemanagementservice/v1/tenantroleuser/roles?userId=1&tenantId=1' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    

- Post Data:
    - <span style="color:blue">**Create a Tenant Role User:**</span> Creates a new Tenant Role User into the radien database  
       - Params:
           - Tenant Role User: Json message that corresponds to the Tenant Role User that must be created  
       - Return (status):
           - 200: in case of success operation.
           - 400: in case of invalid parameters (ex: Repeated values already inserted)
           - 401: In case of not informing a JWT token within AUTHORIZATION header.
           - 404: If no user can be found for Id parameter        
           
       - Pattern:
       
        ```
          curl --location --request POST 'http://localhost:8080//rolemanagementservice/v1/tenantroleuser' \
          --data-raw '{
              "tenantRoleId": [TENANT ROLE ID VALUE],
              "userId": [PERMISSION ID VALUE]
          }'
        ```
      
        - Example:
        ```
            curl --location --request POST 'http://localhost:8080//rolemanagementservice/v1/tenantroleuser' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]' \
            --header 'Content-Type: application/json' \
            --data-raw '{
               "tenantRoleId": 1,
               "userId": 1001
            }'
        ```    
  
- Put Data:
    - <span style="color:blue">**Update Tenant Role User:**</span> Updates a Tenant Role User into the radien database 
       - Params:
           - Id: Path param that corresponds to the user identifier  
           - Tenant Role User: Json message containing Tenant Role User information (to be updated)
       - Return (status):
           - 200: in case of success operation.
           - 400: in case of invalid parameters (ex: Repeated values already inserted)
           - 401: In case of not informing a JWT token within AUTHORIZATION header.
           - 404: If no user can be found for Id parameter         
           
       - Pattern:
       
        ```
          curl --location --request PUT 'http://localhost:8080//rolemanagementservice/v1/tenantroleuser/{id}' \
          --data-raw '{
              "tenantRoleId": [TENANT ROLE ID VALUE],
              "userId": [PERMISSION ID VALUE]
          }'
        ```
      
        - Example:
        ```
            curl --location --request PUT 'http://localhost:8080//rolemanagementservice/v1/tenantroleuser/1' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]' \
            --header 'Content-Type: application/json' \
            --data-raw '{
               "tenantRoleId": 1,
               "userId": 1001
            }'
        ```        
    
- Delete Data:
    - <span style="color:blue">**Delete Tenant Role User:**</span> Deletes a tenant role user from radien database
       - Params:
           - Id: Path param that corresponds to the tenant role user identifier  
       - Return (status):
           - 200: in case of success operation.
           - 401: In case of not informing a JWT token within AUTHORIZATION header.
           - 400: If no user can be found for Id parameter                
           
       - Pattern:
       
        ```
          curl --location --request DELETE 'http://localhost:8080//rolemanagementservice/v1/tenantroleuser/{id}' \
          --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
        ```
      
        - Example:
        ```
            curl --location --request DELETE 'http://localhost:8080/rolemanagementservice/v1/tenantroleuser/222' \
            --header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJJTEtqRFdzbzhUU3NnT1ZuVEZBUlJsWDIxTXBfN29zYUpNTnRwTWVsNV9VIn0.eyJleHAiOjE2MzUyNDYxNzgsImlhdCI6MTYzNTI0MjU3OCwianRpIjoiZmFmMjJiMGEtNmZhNy00YjI5LThlNGUtMmQxMTM3MTNmNmNiIiwiaXNzIjoiaHR0cHM6Ly9pZHAtaW50LnJhZGllbi5pby9hdXRoL3JlYWxtcy9yYWRpZW4iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNWEwODNiYTgtNDdhMS00NjE3LWI2NTktMzg2MTkyYTQ0M2M1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicmFkaWVuIiwic2Vzc2lvbl9zdGF0ZSI6ImIwMjhiNWNkLWUxZjItNDI1Mi05NjkxLWQ1ZjQ3Y2M5MGVlNSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cHM6Ly9sb2NhbGhvc3Q6ODQ0MyJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoia2FyY2gga2lyYWx5IiwicHJlZmVycmVkX3VzZXJuYW1lIjoia2FyY2gua2lyYWx5LXVzZXJuYW1lIiwiZ2l2ZW5fbmFtZSI6ImthcmNoIiwiZmFtaWx5X25hbWUiOiJraXJhbHkiLCJlbWFpbCI6ImthcmNoLmtpcmFseUB5b3BtYWlsLmNvbSJ9.XHuSZgWqGRDS1LHnYacCkH-0mHr4Oaeetm1UzBrjIppv-jPYGroaSEZFSMVdQ4e7sLDPHD4kYyImtDVYKN4_2gvubl3IVEQtL8rBAfjd8YYG7KYxNVDFVbW8mw-KuqfIBN5YQiXetPNbUj15NU_Fz0mu7XPn3UtL7lAZTq6NHVUtxi4Q8IsVqzvAnNvkqRwSUbuHqykk-X4gymCoyGjcuPk0ex4_7Umm0uKMhrKjMYM2ATLEvAjqo4ODkMnSe31y0CBhhcq_Ou2HE7nMTn3ffXjikH7rYNfQhW4JhbDySXLh0e-iHzuuHke35TZhq5gGnnZjd9xaZWO47im9b-l5xw' 
        ```
              
    - <span style="color:blue">**UnAssign Tenant Role User:**</span> (Un)Assign/Dissociate/remove user from TenantRole domain (Filtered deleting)
       - Params:
           - tenantId: Tenant identifier (Mandatory)
           - roleIds: List of Role identifiers (Mandatory)
           - userId: User identifier (Mandatory) 
       - Return (status):
           - 200: Response OK if operation concludes with success
           - 400: in case of violations regarding business rules
           - 401: In case of not informing a JWT token within AUTHORIZATION header.
           
       - Pattern:
       
        ```
          curl --location --request DELETE 'http://localhost:8080/rolemanagementservice/v1/tenantroleuser?tenantId={value}&roleIds={value}&roleIds={value2}&roleIds={value3}&userId={value}' \
          --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
        ```
      
        - Example:
        ```
            curl --location --request DELETE 'http://localhost:8080/rolemanagementservice/v1/tenantroleuser?tenantId=1&roleIds=1&roleIds=2&userId=1111' \
            --header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJJTEtqRFdzbzhUU3NnT1ZuVEZBUlJsWDIxTXBfN29zYUpNTnRwTWVsNV9VIn0.eyJleHAiOjE2MzUyNDYxNzgsImlhdCI6MTYzNTI0MjU3OCwianRpIjoiZmFmMjJiMGEtNmZhNy00YjI5LThlNGUtMmQxMTM3MTNmNmNiIiwiaXNzIjoiaHR0cHM6Ly9pZHAtaW50LnJhZGllbi5pby9hdXRoL3JlYWxtcy9yYWRpZW4iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNWEwODNiYTgtNDdhMS00NjE3LWI2NTktMzg2MTkyYTQ0M2M1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicmFkaWVuIiwic2Vzc2lvbl9zdGF0ZSI6ImIwMjhiNWNkLWUxZjItNDI1Mi05NjkxLWQ1ZjQ3Y2M5MGVlNSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cHM6Ly9sb2NhbGhvc3Q6ODQ0MyJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoia2FyY2gga2lyYWx5IiwicHJlZmVycmVkX3VzZXJuYW1lIjoia2FyY2gua2lyYWx5LXVzZXJuYW1lIiwiZ2l2ZW5fbmFtZSI6ImthcmNoIiwiZmFtaWx5X25hbWUiOiJraXJhbHkiLCJlbWFpbCI6ImthcmNoLmtpcmFseUB5b3BtYWlsLmNvbSJ9.XHuSZgWqGRDS1LHnYacCkH-0mHr4Oaeetm1UzBrjIppv-jPYGroaSEZFSMVdQ4e7sLDPHD4kYyImtDVYKN4_2gvubl3IVEQtL8rBAfjd8YYG7KYxNVDFVbW8mw-KuqfIBN5YQiXetPNbUj15NU_Fz0mu7XPn3UtL7lAZTq6NHVUtxi4Q8IsVqzvAnNvkqRwSUbuHqykk-X4gymCoyGjcuPk0ex4_7Umm0uKMhrKjMYM2ATLEvAjqo4ODkMnSe31y0CBhhcq_Ou2HE7nMTn3ffXjikH7rYNfQhW4JhbDySXLh0e-iHzuuHke35TZhq5gGnnZjd9xaZWO47im9b-l5xw' 
        ```        




