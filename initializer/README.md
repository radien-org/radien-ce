#Introduction

Utilitarian Console program that: 
 - Fetches access token from Keycloak
 - Invokes endpoints to create the following initial entities: 
   - Tenant
   - Role
   - Permission
   - Action
   - Resources, etc
     
#Instructions for Build and Execute:

1 - For build  
<pre><code>mvn package</code></pre>
  
2 - For execute  
<pre><code>java -jar <b>initializer-jar-with-dependencies.jar</b></code></pre>
  
#Parameters
 - system.ms.endpoint.rolemanagement: URL that refers the (endpoint) for Role Management Microservice 
 - system.ms.endpoint.permissionmanagement: URL that refers the (endpoint) for Permission Management Microservice
 - system.ms.endpoint.tenantmanagement: URL that refers the (endpoint) for Tenant Management Microservice
 - KEYCLOAK_IDP_URL: Keycloak Identity provider URL  
 - REALMS_TOKEN_PATH: Keycloak endpoint path that allows obtaining access token.
 - SCRIPT_CLIENT_ID_VALUE: Keycloak client Id for Radien
 - SCRIPT_CLIENT_SECRET_VALUE: Client Secret (sub) for script user
 - SCRIPT_PASSWORD_VALUE: script user (login) that will be used to obtain Access token
 - SCRIPT_USERNAME_VALUE: Password regarding script user 

#Where they are defined

Like any other property managed by ConfigProvider, they can be defined via Environment variables, 
System Properties or microprofile-config.properties file.

#Main Methods
 - getAccessToken: Retrieve access token;
 - tenantCreation: Create tenants;
 - actionCreation: Create actions;
 - resourceCreation: Create resources;
 - permissionCreation: Create permission;
 - roleCreation: Create roles;
 - tenantRoleCreation: Create tenant role associations;
 - makePostRequest: Execute a request to a radien endpoint

#Examples

##How to create new Tenants

1. Go to tenantCreation method
2. Specify a body which corresponds to the new Tenant
3. Invoke makePostRequest

```
String newTenant ="{\"tenantKey\": \"EVCorp\", \"name\": \"Sub Tenant\", \"tenantType\": \"Sub\", \"tenantStart\": \"2030-01-22\", \"tenantEnd\": \"2040-01-22\", \"parentId\":2, \"clientId\":2}";
makePostRequest(tenantUrl,"newTenantToBeCreated",accessToken,newTenant);
```

##How to create new Resource
1. Go to resourceCreation method
2. Specify a body which corresponds to the new Resource
3. Invoke makePostRequest

```
String newResource = "{ \"name\": \"New Resource to be associated with a Permission\" }";
makePostRequest(resourceUrl,"newResource",accessToken,newResource);
```

##How to create new Action
1. Go to actionCreation method
2. Specify a body which corresponds to the new Action
3. Invoke makePostRequest

```
String newAction = ""{ \"name\": \"All\" }"";
makePostRequest(actionUrl,"newAction",accessToken,newAction);
```