# Radien Permission Management Microservice
Microservice that provides REST APIs to manage domain objects regarding **Permission**, **Action** and **Resource**.

For each of these entities there will be one particular endpoint providing CRUD operations. 

![Permission domain model](doc/Permission-domain-model.png)

# Pre requirements:

## Running as executable Jar file 

### - ___Package:___
    mvn -P tomee package

### - ___Run:___ 
    java -jar target/rd-ms-permissionmanagement-service.jar

### - ___Local TomEE Configuration:___
    URL: http://localhost:8080/permissionmanagementservice/v1/permission
    HTTP Port: 8085
    HTTPS Port: 8447
    JMX Port: 1103
    Deployment: rd-ms-permissionmanagement:war exploded
    Application Context: /permissionmanagementservice

#
Let's check the operations that exists for each endpoint and how to perform them via cURL

#
# Action Endpoint:

- Get Data:
    - <span style="color:blue">**Get Action by ID**:</span> Returns the Action found for an informed Id.    
       - Params:
           - Id: Action Identifier           
       - Return (status):
           - 200: If exists an action for the Id:
           - 404: If no action could be found
           - 401: In case of not informing a JWT token within AUTHORIZATION header
       - Pattern:
       
        ```
          curl -X GET 'http://localhost:8080/permissionmanagementservice/v1/action/{id}'
        ```
      
        - Example:
        ```
            curl --location --request GET 'http://localhost:8080/permissionmanagementservice/v1/action/1' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
        ```    
          
    - <span style="color:blue">**Get All**:</span> Retrieve all the actions using a pagination approach
        - Params:
            - search: criteria to be found, applied to the action name (Optional).
            - pageNo: page number to show the first records (Optional).
            - pageSize: max number of pages of results (Optional).
            - sortBy: criteria field to be sorted (Optional).
            - asc: boolean value to show the values ascending or descending way (Optional).
        - Return (status):
            - 200: In case of search operation concluded without issue
            - 401: In case of not informing a JWT token within AUTHORIZATION header
        - Pattern:
            ```
              curl --location --request GET 'http://localhost:8080/permissionmanagementservice/v1/action?pageNo={pageNumber}&pageSize={pageSize}&sortBy={fieldName}&asc={true|false}&search={searchParam}'
            ```
        - Example:
            ```
            curl --location --request GET 'http://localhost:8080/permissionmanagementservice/v1/action?pageNo=1&pageSize=10&sortBy=name&asc=true&search=c%25' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    
          
    - <span style="color:blue">**Find:**</span> Retrieves multiple actions (List) basing on a search filter that uses the following parameters:
        - Params:
            - name: action name (Optional).
            - ids: ids to be found (Optional).
            - isExact: isExact should the search fields be exact or not as given (Optional).
            - isLogicalConjunction: isLogicalConjunction should the query use a and or a or criteria (Optional).            
            
        - Return (status):
            - 200: In case of search operation concluded without issue.
            - 401: In case of not informing a JWT token within AUTHORIZATION header
        - Pattern:
            ```
            curl --location --request GET 'http://localhost:8080/permissionmanagementservice/v1/action/find?name={value}&ids={ids for action}&isExact={true|false}&isLogicalConjunction={true|false}'
            ```
        - Example: Searching using name parameter (Value do not need to be exact)
            ```
            curl --location --request GET 'http://localhost:8080/permissionmanagementservice/v1/action/find?name=3&isExact=false' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    
        - Example 2: Searching using an ID List
            ```
            curl --location --request GET 'http://localhost:8080/permissionmanagementservice/v1/action/find?ids=1&ids=2&ids=3' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```              

- Post Data:
    - <span style="color:blue">**Create action:**</span> Creates a new action into the radien database  
       - Params:
           - action: Json message that corresponds to the action that must be created  
       - Return (status):
           - 200: in case of success operation.
           - 400: in case of invalid parameters (ex: Repeated values already inserted)
           - 401: In case of not informing a JWT token within AUTHORIZATION header.      
           
       - Pattern:
       
        ```
          curl --location --request POST 'http://localhost:8080//permissionmanagementservice/v1/action' \
          --data-raw '{
              "name": [ACTION NAME VALUE]
          }'
        ```
      
        - Example:
        ```
            curl --location --request POST 'http://localhost:8080//permissionmanagementservice/v1/action' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]' \
            --header 'Content-Type: application/json' \
            --data-raw '{
                "name": "Create"
            }'
        ```    
  
- Put Data:
    - <span style="color:blue">**Update action:**</span> Updates a action into the radien database 
       - Params:
           - Id: Path param that corresponds to the action identifier  
           - action: Json message containing all action information (to be updated)
       - Return (status):
           - 200: in case of success operation.
           - 400: in case of invalid parameters (ex: Repeated values already inserted)
           - 401: In case of not informing a JWT token within AUTHORIZATION header.
           - 404: If no action can be found for Id parameter         
           
       - Pattern:
       
        ```
          curl --location --request PUT 'http://localhost:8080//permissionmanagementservice/v1/action/{id}' \
          --data-raw '{
              "name": [ACTION NAME VALUE]
          }'
        ```
      
        - Example:
        ```
            curl --location --request PUT 'http://localhost:8080//permissionmanagementservice/v1/action/1' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]' \
            --header 'Content-Type: application/json' \
            --data-raw '{
                "name": "[ACTION NAME VALUE]"
            }'
        ```        
    
- Delete Data:
    - <span style="color:blue">**Delete action:**</span> Deletes a action from radien database
       - Params:
           - Id: Path param that corresponds to the action identifier  
       - Return (status):
           - 200: in case of success operation.
           - 401: In case of not informing a JWT token within AUTHORIZATION header.
           - 404: If no action can be found for Id parameter                
           
       - Pattern:
       
        ```
          curl --location --request DELETE 'http://localhost:8080//permissionmanagementservice/v1/action/{id}' \
          --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
        ```
      
        - Example:
        ```
            curl --location --request DELETE 'http://localhost:8080/permissionmanagementservice/v1/action/222' \
            --header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJJTEtqRFdzbzhUU3NnT1ZuVEZBUlJsWDIxTXBfN29zYUpNTnRwTWVsNV9VIn0.eyJleHAiOjE2MzUyNDYxNzgsImlhdCI6MTYzNTI0MjU3OCwianRpIjoiZmFmMjJiMGEtNmZhNy00YjI5LThlNGUtMmQxMTM3MTNmNmNiIiwiaXNzIjoiaHR0cHM6Ly9pZHAtaW50LnJhZGllbi5pby9hdXRoL3JlYWxtcy9yYWRpZW4iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNWEwODNiYTgtNDdhMS00NjE3LWI2NTktMzg2MTkyYTQ0M2M1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicmFkaWVuIiwic2Vzc2lvbl9zdGF0ZSI6ImIwMjhiNWNkLWUxZjItNDI1Mi05NjkxLWQ1ZjQ3Y2M5MGVlNSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cHM6Ly9sb2NhbGhvc3Q6ODQ0MyJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoia2FyY2gga2lyYWx5IiwicHJlZmVycmVkX3VzZXJuYW1lIjoia2FyY2gua2lyYWx5LXVzZXJuYW1lIiwiZ2l2ZW5fbmFtZSI6ImthcmNoIiwiZmFtaWx5X25hbWUiOiJraXJhbHkiLCJlbWFpbCI6ImthcmNoLmtpcmFseUB5b3BtYWlsLmNvbSJ9.XHuSZgWqGRDS1LHnYacCkH-0mHr4Oaeetm1UzBrjIppv-jPYGroaSEZFSMVdQ4e7sLDPHD4kYyImtDVYKN4_2gvubl3IVEQtL8rBAfjd8YYG7KYxNVDFVbW8mw-KuqfIBN5YQiXetPNbUj15NU_Fz0mu7XPn3UtL7lAZTq6NHVUtxi4Q8IsVqzvAnNvkqRwSUbuHqykk-X4gymCoyGjcuPk0ex4_7Umm0uKMhrKjMYM2ATLEvAjqo4ODkMnSe31y0CBhhcq_Ou2HE7nMTn3ffXjikH7rYNfQhW4JhbDySXLh0e-iHzuuHke35TZhq5gGnnZjd9xaZWO47im9b-l5xw' 
        ```        

#
# Resource Endpoint:

- Get Data:
    - <span style="color:blue">**Get Resource by ID**:</span> Returns the Resource found for an informed Id.    
       - Params:
           - Id: Resource Identifier           
       - Return (status):
           - 200: If exists an resource for the Id:
           - 404: If no resource could be found
           - 401: In case of not informing a JWT token within AUTHORIZATION header
       - Pattern:
       
        ```
          curl -X GET 'http://localhost:8080/permissionmanagementservice/v1/resource/{id}'
        ```
      
        - Example:
        ```
            curl --location --request GET 'http://localhost:8080/permissionmanagementservice/v1/resource/1' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
        ```    
          
    - <span style="color:blue">**Get All**:</span> Retrieve all the resources using a pagination approach
        - Params:
            - search: criteria to be found, applied to the resource name (Optional).
            - pageNo: page number to show the first records (Optional).
            - pageSize: max number of pages of results (Optional).
            - sortBy: criteria field to be sorted (Optional).
            - asc: boolean value to show the values ascending or descending way (Optional).
        - Return (status):
            - 200: In case of search operation concluded without issue
            - 401: In case of not informing a JWT token within AUTHORIZATION header
        - Pattern:
            ```
              curl --location --request GET 'http://localhost:8080/permissionmanagementservice/v1/resource?pageNo={pageNumber}&pageSize={pageSize}&sortBy={fieldName}&asc={true|false}&search={searchParam}'
            ```
        - Example:
            ```
            curl --location --request GET 'http://localhost:8080/permissionmanagementservice/v1/resource?pageNo=1&pageSize=10&sortBy=name&asc=true&search=c%25' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    
          
    - <span style="color:blue">**Find:**</span> Retrieves multiple resources (List) basing on a search filter that uses the following parameters:
        - Params:
            - name: resource name (Optional).
            - ids: ids to be found (Optional).
            - isExact: isExact should the search fields be exact or not as given (Optional).
            - isLogicalConjunction: isLogicalConjunction should the query use a and or a or criteria (Optional).            
            
        - Return (status):
            - 200: In case of search operation concluded without issue.
            - 401: In case of not informing a JWT token within AUTHORIZATION header
        - Pattern:
            ```
            curl --location --request GET 'http://localhost:8080/permissionmanagementservice/v1/resource/find?name={value}&ids={ids for resource}&isExact={true|false}&isLogicalConjunction={true|false}'
            ```
        - Example: Searching using name parameter (Value do not need to be exact)
            ```
            curl --location --request GET 'http://localhost:8080/permissionmanagementservice/v1/resource/find?name=3&isExact=false' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    
        - Example 2: Searching using an ID List
            ```
            curl --location --request GET 'http://localhost:8080/permissionmanagementservice/v1/resource/find?ids=1&ids=2&ids=3' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```              

- Post Data:
    - <span style="color:blue">**Create resource:**</span> Creates a new resource into the radien database  
       - Params:
           - resource: Json message that corresponds to the resource that must be created  
       - Return (status):
           - 200: in case of success operation.
           - 400: in case of invalid parameters (ex: Repeated values already inserted)
           - 401: In case of not informing a JWT token within AUTHORIZATION header.      
           
       - Pattern:
       
        ```
          curl --location --request POST 'http://localhost:8080//permissionmanagementservice/v1/resource' \
          --data-raw '{
              "name": [ACTION NAME VALUE]
          }'
        ```
      
        - Example:
        ```
            curl --location --request POST 'http://localhost:8080//permissionmanagementservice/v1/resource' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]' \
            --header 'Content-Type: application/json' \
            --data-raw '{
                "name": "Create"
            }'
        ```    
  
- Put Data:
    - <span style="color:blue">**Update resource:**</span> Updates a resource into the radien database 
       - Params:
           - Id: Path param that corresponds to the resource identifier  
           - resource: Json message containing all resource information (to be updated)
       - Return (status):
           - 200: in case of success operation.
           - 400: in case of invalid parameters (ex: Repeated values already inserted)
           - 401: In case of not informing a JWT token within AUTHORIZATION header.
           - 404: If no resource can be found for Id parameter         
           
       - Pattern:
       
        ```
          curl --location --request PUT 'http://localhost:8080//permissionmanagementservice/v1/resource/{id}' \
          --data-raw '{
              "name": [ACTION NAME VALUE]
          }'
        ```
      
        - Example:
        ```
            curl --location --request PUT 'http://localhost:8080//permissionmanagementservice/v1/resource/1' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]' \
            --header 'Content-Type: application/json' \
            --data-raw '{
                "name": "[ACTION NAME VALUE]"
            }'
        ```        
    
- Delete Data:
    - <span style="color:blue">**Delete resource:**</span> Deletes a resource from radien database
       - Params:
           - Id: Path param that corresponds to the resource identifier  
       - Return (status):
           - 200: in case of success operation.
           - 401: In case of not informing a JWT token within AUTHORIZATION header.
           - 404: If no resource can be found for Id parameter                
           
       - Pattern:
       
        ```
          curl --location --request DELETE 'http://localhost:8080//permissionmanagementservice/v1/resource/{id}' \
          --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
        ```
      
        - Example:
        ```
            curl --location --request DELETE 'http://localhost:8080/permissionmanagementservice/v1/resource/222' \
            --header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJJTEtqRFdzbzhUU3NnT1ZuVEZBUlJsWDIxTXBfN29zYUpNTnRwTWVsNV9VIn0.eyJleHAiOjE2MzUyNDYxNzgsImlhdCI6MTYzNTI0MjU3OCwianRpIjoiZmFmMjJiMGEtNmZhNy00YjI5LThlNGUtMmQxMTM3MTNmNmNiIiwiaXNzIjoiaHR0cHM6Ly9pZHAtaW50LnJhZGllbi5pby9hdXRoL3JlYWxtcy9yYWRpZW4iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNWEwODNiYTgtNDdhMS00NjE3LWI2NTktMzg2MTkyYTQ0M2M1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicmFkaWVuIiwic2Vzc2lvbl9zdGF0ZSI6ImIwMjhiNWNkLWUxZjItNDI1Mi05NjkxLWQ1ZjQ3Y2M5MGVlNSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cHM6Ly9sb2NhbGhvc3Q6ODQ0MyJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoia2FyY2gga2lyYWx5IiwicHJlZmVycmVkX3VzZXJuYW1lIjoia2FyY2gua2lyYWx5LXVzZXJuYW1lIiwiZ2l2ZW5fbmFtZSI6ImthcmNoIiwiZmFtaWx5X25hbWUiOiJraXJhbHkiLCJlbWFpbCI6ImthcmNoLmtpcmFseUB5b3BtYWlsLmNvbSJ9.XHuSZgWqGRDS1LHnYacCkH-0mHr4Oaeetm1UzBrjIppv-jPYGroaSEZFSMVdQ4e7sLDPHD4kYyImtDVYKN4_2gvubl3IVEQtL8rBAfjd8YYG7KYxNVDFVbW8mw-KuqfIBN5YQiXetPNbUj15NU_Fz0mu7XPn3UtL7lAZTq6NHVUtxi4Q8IsVqzvAnNvkqRwSUbuHqykk-X4gymCoyGjcuPk0ex4_7Umm0uKMhrKjMYM2ATLEvAjqo4ODkMnSe31y0CBhhcq_Ou2HE7nMTn3ffXjikH7rYNfQhW4JhbDySXLh0e-iHzuuHke35TZhq5gGnnZjd9xaZWO47im9b-l5xw' 
        ```        

#
# Permission Endpoint:

- Get Data:
    - <span style="color:blue">**Get Permission by ID**:</span> Returns the Permission found for an informed Id.    
       - Params:
           - Id: Permission Identifier           
       - Return (status):
           - 200: If exists an permission for the Id:
           - 404: If no permission could be found
           - 401: In case of not informing a JWT token within AUTHORIZATION header
       - Pattern:
       
        ```
          curl -X GET 'http://localhost:8080/permissionmanagementservice/v1/permission/{id}'
        ```
      
        - Example:
        ```
            curl --location --request GET 'http://localhost:8080/permissionmanagementservice/v1/permission/1' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
        ```    
          
    - <span style="color:blue">**Get All**:</span> Retrieve all the permissions using a pagination approach
        - Params:
            - search: criteria to be found, applied to the permission name (Optional).
            - pageNo: page number to show the first records (Optional).
            - pageSize: max number of pages of results (Optional).
            - sortBy: criteria field to be sorted (Optional).
            - asc: boolean value to show the values ascending or descending way (Optional).
        - Return (status):
            - 200: In case of search operation concluded without issue
            - 401: In case of not informing a JWT token within AUTHORIZATION header
        - Pattern:
            ```
              curl --location --request GET 'http://localhost:8080/permissionmanagementservice/v1/permission?pageNo={pageNumber}&pageSize={pageSize}&sortBy={fieldName}&asc={true|false}&search={searchParam}'
            ```
        - Example:
            ```
            curl --location --request GET 'http://localhost:8080/permissionmanagementservice/v1/permission?pageNo=1&pageSize=10&sortBy=name&asc=true&search=c%25' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    
          
    - <span style="color:blue">**Find:**</span> Retrieves multiple permissions (List) basing on a search filter that uses the following parameters:
        - Params:
            - name: permission name (Optional).
            - action: action id (Optional).
            - resource: resource id (Optional)
            - ids: ids to be found (Optional).
            - isExact: isExact should the search fields be exact or not as given (Optional).
            - isLogicalConjunction: isLogicalConjunction should the query use a and or a or criteria (Optional).            
            
        - Return (status):
            - 200: In case of search operation concluded without issue.
            - 401: In case of not informing a JWT token within AUTHORIZATION header
        - Pattern:
            ```
            curl --location --request GET 'http://localhost:8080/permissionmanagementservice/v1/permission/find?name={value}&ids={ids for permission}&action={action id}&resource={resource id}&isExact={true|false}&isLogicalConjunction={true|false}'
            ```
        - Example: Searching using name, action, and resource parameters (Using **isExact=false** value name don't need to be exact)
            ```
            curl --location --request GET 'http://localhost:8080/permissionmanagementservice/v1/permission/find?name=create&action=1&resource=2&isLogicalConjuntion=false&isExact=false' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    
        - Example 2: Searching using an ID List
            ```
            curl --location --request GET 'http://localhost:8080/permissionmanagementservice/v1/permission/find?ids=1&ids=5&ids=6&ids=8' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```              
          
    - <span style="color:blue">**Get Id by Resource and Action:**</span> Retrieve the permission Id using the combination of resource and action as parameters
        - Params:
            - resource: resource name (Mandatory)
            - action: action name (Mandatory)           
            
        - Return (status):
            - 200: If exists a permission for the informed parameters (id will be returned with the response). 
            - 400: In case of insufficient params (resource or action not informed)
            - 401: In case of not informing a JWT token within AUTHORIZATION header
            - 404: If there is no permission for the informed parameters
        
        - Example: Trying to retrieve Id informing (names for) resource="User" and action="Update"
            ```
            curl --location --request GET 'http://localhost:8080/permissionmanagementservice/v1/permission/id?resource=User&action=Update' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    
          
    - <span style="color:blue">**Check exists:**</span> Validates if Permission exists for a referred Id (or taking in account permission name).
        - Params:
            - id: Permission Identifier to guide the search (Primary parameter)
            - name: Permission name, an alternative parameter to be used (When Id is omitted)           
            
        - Return (status):
            - 204: If Permission exists 
            - 400: None expected parameter informed
            - 401: In case of not informing a JWT token within AUTHORIZATION header
            - 404: If Permission does not exist
            
        - Example: Checking existence using id as parameter
            ```          
            curl --location --head 'http://localhost:8080/permissionmanagementservice/v1/permission?id=1' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    
            
        - Example: Checking existence using name as parameter
            ```
            curl --location --head 'http://localhost:8080/permissionmanagementservice/v1/permission?name=User%20Management%20-%20Create' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```    

    - <span style="color:blue">**Count records:**</span> Calculate how many records are existent in the db.
        - Params:
            - id: Permission Identifier to guide the search (Primary parameter)
            - name: Permission name, an alternative parameter to be used (When Id is omitted)           
            
        - Return (status):
            - 200: With response encapsulating the amount of records 
           
        - Pattern / Example:
            ```
            curl --location --request GET 'http://localhost:8080/permissionmanagementservice/v1/permission/countRecords' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
            ```

- Post Data:
    - <span style="color:blue">**Create permission:**</span> Creates a new permission into the radien database  
       - Params:
           - permission: Json message that corresponds to the permission that must be created  
       - Return (status):
           - 200: in case of success operation.
           - 401: In case of not informing a JWT token within AUTHORIZATION header.      
           
       - Pattern:
       
        ```
          curl --location --request POST 'http://localhost:8080//permissionmanagementservice/v1/permission' \
          --data-raw '{
              "name": [ACTION NAME VALUE]
          }'
        ```
      
        - Example:
        ```
            curl --location --request POST 'http://localhost:8080//permissionmanagementservice/v1/permission' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]' \
            --header 'Content-Type: application/json' \
            --data-raw '{
                "name": "Create"
            }'
        ```    
  
- Put Data:
    - <span style="color:blue">**Update permission:**</span> Updates a permission into the radien database 
       - Params:
           - Id: Path param that corresponds to the permission identifier  
           - permission: Json message containing all permission information (to be updated)
       - Return (status):
           - 200: in case of success operation.
           - 400: in case of invalid parameters (ex: Repeated values already inserted)
           - 401: In case of not informing a JWT token within AUTHORIZATION header.
           - 404: If no permission can be found for Id parameter         
           
       - Pattern:
       
        ```
          curl --location --request PUT 'http://localhost:8080//permissionmanagementservice/v1/permission/{id}' \
          --data-raw '{
              "name": [ACTION NAME VALUE]
          }'
        ```
      
        - Example:
        ```
            curl --location --request PUT 'http://localhost:8080//permissionmanagementservice/v1/permission/1' \
            --header 'Authorization: Bearer [JWT ACCESS TOKEN]' \
            --header 'Content-Type: application/json' \
            --data-raw '{
                "name": "[ACTION NAME VALUE]"
            }'
        ```        
    
- Delete Data:
    - <span style="color:blue">**Delete permission:**</span> Deletes a permission from radien database
       - Params:
           - Id: Path param that corresponds to the permission identifier  
       - Return (status):
           - 200: in case of success operation.
           - 401: In case of not informing a JWT token within AUTHORIZATION header.
           - 404: If no permission can be found for Id parameter                
           
       - Pattern:
       
        ```
          curl --location --request DELETE 'http://localhost:8080//permissionmanagementservice/v1/permission/{id}' \
          --header 'Authorization: Bearer [JWT ACCESS TOKEN]'
        ```
      
        - Example:
        ```
            curl --location --request DELETE 'http://localhost:8080/permissionmanagementservice/v1/permission/222' \
            --header 'Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJJTEtqRFdzbzhUU3NnT1ZuVEZBUlJsWDIxTXBfN29zYUpNTnRwTWVsNV9VIn0.eyJleHAiOjE2MzUyNDYxNzgsImlhdCI6MTYzNTI0MjU3OCwianRpIjoiZmFmMjJiMGEtNmZhNy00YjI5LThlNGUtMmQxMTM3MTNmNmNiIiwiaXNzIjoiaHR0cHM6Ly9pZHAtaW50LnJhZGllbi5pby9hdXRoL3JlYWxtcy9yYWRpZW4iLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNWEwODNiYTgtNDdhMS00NjE3LWI2NTktMzg2MTkyYTQ0M2M1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicmFkaWVuIiwic2Vzc2lvbl9zdGF0ZSI6ImIwMjhiNWNkLWUxZjItNDI1Mi05NjkxLWQ1ZjQ3Y2M5MGVlNSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cHM6Ly9sb2NhbGhvc3Q6ODQ0MyJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJuYW1lIjoia2FyY2gga2lyYWx5IiwicHJlZmVycmVkX3VzZXJuYW1lIjoia2FyY2gua2lyYWx5LXVzZXJuYW1lIiwiZ2l2ZW5fbmFtZSI6ImthcmNoIiwiZmFtaWx5X25hbWUiOiJraXJhbHkiLCJlbWFpbCI6ImthcmNoLmtpcmFseUB5b3BtYWlsLmNvbSJ9.XHuSZgWqGRDS1LHnYacCkH-0mHr4Oaeetm1UzBrjIppv-jPYGroaSEZFSMVdQ4e7sLDPHD4kYyImtDVYKN4_2gvubl3IVEQtL8rBAfjd8YYG7KYxNVDFVbW8mw-KuqfIBN5YQiXetPNbUj15NU_Fz0mu7XPn3UtL7lAZTq6NHVUtxi4Q8IsVqzvAnNvkqRwSUbuHqykk-X4gymCoyGjcuPk0ex4_7Umm0uKMhrKjMYM2ATLEvAjqo4ODkMnSe31y0CBhhcq_Ou2HE7nMTn3ffXjikH7rYNfQhW4JhbDySXLh0e-iHzuuHke35TZhq5gGnnZjd9xaZWO47im9b-l5xw' 
        ```        


