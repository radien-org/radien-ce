/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.radien.util;

import io.radien.api.KeycloakConfigs;
import io.radien.api.OAFProperties;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

/**
 * Console application that uses REST Clients to consume the respective endpoints responsible
 * for creating (initialize) the following entities into the Radien database: Action, Resource, Permission,
 * Role, Tenant, TenantRole and TenantPermission
 */
public class AccessTokenUtil {

    /**
     * Method that retrieves a config property
     * @param propertyKey
     * @return
     */
    public static String getConfigProperty(String propertyKey) {
        Config config = ConfigProvider.getConfig();
        return config.getValue(propertyKey, String.class);
    }

    /**
     * Retrieve the URL that corresponds to Role Management Endpoint
     * @return String that corresponds to the Role Management URL
     */
    public static String getRoleManagementBaseURL() {
        return getConfigProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT.propKey());
    }

    /**
     * Retrieve the URL that corresponds to Permission Management Endpoint
     * @return String that corresponds to the Permission Management URL
     */
    public static String getPermissionManagementBaseURL() {
        return getConfigProperty(OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT.propKey());
    }

    /**
     * Retrieve the URL that corresponds to Tenant Management Endpoint
     * @return String that corresponds to the Tenant Management URL
     */
    public static String getTenantManagementBaseURL() {
        return getConfigProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT.propKey());
    }

    /**
     * Method responsible for retrieving an access token generated via Keycloak.
     * The access token is a crucial parameter to invoke the endpoints (regarding
     * Tenant, Role, Permission, etc)
     * @return String that corresponds to the access token
     */
    public static String getAccessToken() {
        String url = getConfigProperty(KeycloakConfigs.IDP_URL.propKey()) +
                getConfigProperty(KeycloakConfigs.RADIEN_TOKEN_PATH.propKey());
        String clientId = getConfigProperty(KeycloakConfigs.RADIEN_CLIENT_ID.propKey());
        String clientSecret = getConfigProperty(KeycloakConfigs.RADIEN_SECRET.propKey());
        String username = getConfigProperty(KeycloakConfigs.RADIEN_USERNAME.propKey());
        String password = getConfigProperty(KeycloakConfigs.RADIEN_PASSWORD.propKey());

        HttpResponse<HashMap> response = Unirest.post(url)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .field("client_id", clientId)
                .field("client_secret",clientSecret)
                .field("grant_type", "password")
                .field("username", username)
                .field("password", password)
                .asObject(HashMap.class);
        String accessToken = (String)response.getBody().get("access_token");
        System.out.println(response.getBody().get("access_token"));
        return accessToken;
    }

    /**
     * Main method that orchestrates all the steps to create Action, Resource, Permission, Tenant, etc
     * @param args console argument
     */
    public static void main(String[] args) {
        String accessToken = getAccessToken();
        tenantCreation(accessToken);
        actionCreation(accessToken);
        resourceCreation(accessToken);
        permissionCreation(accessToken);
        roleCreation(accessToken);
        tenantRoleCreation(accessToken);
    }

    /**
     * Given a generated Access Token, consumes TenantRole Endpoint to create the first basic
     * TenantRoles associations.
     * @param accessToken Access Token generated via KeyCloak
     */
    private static void tenantRoleCreation(String accessToken) {
        String tenantRoleUrl= getRoleManagementBaseURL() + "/tenantrole";
        makePostRequest(tenantRoleUrl,"tenantRole1",accessToken,"{\"tenantId\":1, \"roleId\":1}");
        makePostRequest(tenantRoleUrl,"tenantRole2",accessToken,"{\"tenantId\":2, \"roleId\":1}");
        makePostRequest(tenantRoleUrl,"tenantRole1",accessToken,"{\"tenantId\":3, \"roleId\":1}");

        String tenantRolePermissionUrl= getRoleManagementBaseURL() + "/tenantrolepermission";
        makePostRequest(tenantRolePermissionUrl,"assignPermission1",accessToken,"{\"tenantRoleId\":1, \"permissionId\":1}");
        makePostRequest(tenantRolePermissionUrl,"assignPermission2",accessToken,"{\"tenantRoleId\":1, \"permissionId\":2}");
        makePostRequest(tenantRolePermissionUrl,"assignPermission3",accessToken,"{\"tenantRoleId\":1, \"permissionId\":3}");
        makePostRequest(tenantRolePermissionUrl,"assignPermission4",accessToken,"{\"tenantRoleId\":1, \"permissionId\":4}");

        makePostRequest(tenantRolePermissionUrl,"assignPermission5",accessToken,"{\"tenantRoleId\":2, \"permissionId\":4}");
        makePostRequest(tenantRolePermissionUrl,"assignPermission6",accessToken,"{\"tenantRoleId\":3, \"permissionId\":4}");

        makePostRequest(tenantRolePermissionUrl,"assignPermission8",accessToken,"{\"tenantRoleId\":2, \"permissionId\":1}");
        makePostRequest(tenantRolePermissionUrl,"assignPermission9",accessToken,"{\"tenantRoleId\":3, \"permissionId\":1}");
    }

    /**
     * Given a generated Access Token, consumes Role Endpoint to create the first basic Roles.
     * @param accessToken Access Token generated via KeyCloak
     */
    private static void roleCreation(String accessToken) {
        System.out.println("Starting roles...");
        String roleUrl= getRoleManagementBaseURL() + "/role";
        List<String> roles = Arrays.asList(
                "{ \"name\": \"System Administrator\", \"description\": \"The BOSS!\", \"terminationDate\": \"2030-12-12T00:00:00\" }",
                "{ \"name\": \"Permission Administrator\", \"description\": \"The Role for Permission Management Testing purposes!\", \"terminationDate\": \"2030-12-12T00:00:00\" }",
                "{ \"name\": \"Role Administrator\", \"description\": \"The Role for Role Management Testing purposes!\", \"terminationDate\": \"2030-12-12T00:00:00\" }",
                "{ \"name\": \"User Administrator\", \"description\": \"The Role for User Management Testing purposes!\", \"terminationDate\": \"2030-12-12T00:00:00\" }",
                "{ \"name\": \"Tenant Administrator\", \"description\": \"The Role for Tenant Management Testing purposes!\", \"terminationDate\": \"2030-12-12T00:00:00\" }",
                "{ \"name\": \"Client Tenant Administrator\", \"description\": \"The Role for the Client Tenant administrators!\", \"terminationDate\": \"2030-12-12T00:00:00\" }",
                "{ \"name\": \"Sub Tenant Administrator\", \"description\": \"The Role for the Sub Tenant administrators!\", \"terminationDate\": \"2030-12-12T00:00:00\" }",
                "{ \"name\": \"Guest\", \"description\": \"The Role for Guests!\", \"terminationDate\": \"2030-12-12T00:00:00\" }",
                "{ \"name\": \"Approver\", \"description\": \"The Role for users that only approve data!\", \"terminationDate\": \"2030-12-12T00:00:00\" }",
                "{ \"name\": \"Author\", \"description\": \"The Role for users that only create data!\", \"terminationDate\": \"2030-12-12T00:00:00\" }",
                "{ \"name\": \"Publisher\", \"description\": \"The Role for the publishers!\", \"terminationDate\": \"2030-12-12T00:00:00\" }"
        );
        for(int i =0;i<roles.size();i++){
            makePostRequest(roleUrl,"role"+i,accessToken,roles.get(i));
        }
    }

    /**
     * Given a generated Access Token, consumes Resource Endpoint to create the first basic Resources:
     * User, Roles, Permission, Resource, Action, Tenant, TenantRole, TenantRolePermission and
     * TenantRoleUser.
     * @param accessToken Access Token generated via KeyCloak
     */
    private static void resourceCreation(String accessToken) {
        System.out.println("Starting resource...");
        String resourceUrl= getPermissionManagementBaseURL() + "/resource";

        String user = "{ \"name\": \"User\" }";
        String roles = "{ \"name\": \"Roles\" }";
        String permission = "{ \"name\": \"Permission\" }";
        String resource = "{ \"name\": \"Resource\" }";
        String action = "{ \"name\": \"Action\" }";
        String tenant = "{ \"name\": \"Tenant\" }";
        String tenantRole = "{ \"name\": \"Tenant Role\" }";
        String tenantRolePermission = "{ \"name\": \"Tenant Role Permission\" }";
        String tenantRoleUser = "{ \"name\": \"Tenant Role User\" }";

        makePostRequest(resourceUrl,"resource user",accessToken,user);
        makePostRequest(resourceUrl,"resource roles",accessToken,roles);
        makePostRequest(resourceUrl,"resource permission",accessToken,permission);
        makePostRequest(resourceUrl,"resource resource",accessToken,resource);
        makePostRequest(resourceUrl,"resource action",accessToken,action);
        makePostRequest(resourceUrl,"resource tenant",accessToken,tenant);
        makePostRequest(resourceUrl,"resource tenant role",accessToken,tenantRole);
        makePostRequest(resourceUrl,"resource tenant role permission",accessToken,tenantRolePermission);
        makePostRequest(resourceUrl,"resource tenant role user",accessToken,tenantRoleUser);
    }

    /**
     * Given a generated Access Token, consumes Permission Endpoint to create the first basic Permissions
     * @param accessToken Access Token generated via KeyCloak
     */
    private static void permissionCreation(String accessToken){
        System.out.println("Start permission...");
        String permissionUrl = getPermissionManagementBaseURL() + "/permission";

        // Permissions regarding User
        String permission1 = "{ \"name\": \"User Management - Create\", \"actionId\": 1, \"resourceId\": 1 }";
        String permission2 = "{ \"name\": \"User Management - Read\", \"actionId\": 2, \"resourceId\": 1 }";
        String permission3 = "{ \"name\": \"User Management - Update\", \"actionId\": 3, \"resourceId\": 1 }";
        String permission4 = "{ \"name\": \"User Management - Delete\", \"actionId\": 4, \"resourceId\": 1 }";
        String permission5 = "{ \"name\": \"User Management - All\", \"actionId\": 5, \"resourceId\": 1 }";

        makePostRequest(permissionUrl,"permission1",accessToken,permission1);
        makePostRequest(permissionUrl,"permission2",accessToken,permission2);
        makePostRequest(permissionUrl,"permission3",accessToken,permission3);
        makePostRequest(permissionUrl,"permission4",accessToken,permission4);
        makePostRequest(permissionUrl,"permission5",accessToken,permission5);

        // Permissions regarding Roles
        String permission6 = "{ \"name\": \"Roles Management - Create\", \"actionId\": 1, \"resourceId\": 2 }";
        String permission7 = "{ \"name\": \"Roles Management - Read\", \"actionId\": 2, \"resourceId\": 2 }";
        String permission8 = "{ \"name\": \"Roles Management - Update\", \"actionId\": 3, \"resourceId\": 2 }";
        String permission9 = "{ \"name\": \"Roles Management - Delete\", \"actionId\": 4, \"resourceId\": 2 }";
        String permission10 = "{ \"name\": \"Roles Management - All\", \"actionId\": 5, \"resourceId\": 2 }";

        makePostRequest(permissionUrl,"permission6",accessToken,permission6);
        makePostRequest(permissionUrl,"permission7",accessToken,permission7);
        makePostRequest(permissionUrl,"permission8",accessToken,permission8);
        makePostRequest(permissionUrl,"permission9",accessToken,permission9);
        makePostRequest(permissionUrl,"permission10",accessToken,permission10);

        // Permissions regarding Permission
        String permission11 = "{ \"name\": \"Permission Management - Create\", \"actionId\": 1, \"resourceId\": 3 }";
        String permission12 = "{ \"name\": \"Permission Management - Read\", \"actionId\": 2, \"resourceId\": 3 }";
        String permission13 = "{ \"name\": \"Permission Management - Update\", \"actionId\": 3, \"resourceId\": 3 }";
        String permission14 = "{ \"name\": \"Permission Management - Delete\", \"actionId\": 4, \"resourceId\": 3 }";
        String permission15 = "{ \"name\": \"Permission Management - All\", \"actionId\": 5, \"resourceId\": 3 }";

        makePostRequest(permissionUrl,"permission11",accessToken,permission11);
        makePostRequest(permissionUrl,"permission12",accessToken,permission12);
        makePostRequest(permissionUrl,"permission13",accessToken,permission13);
        makePostRequest(permissionUrl,"permission14",accessToken,permission14);
        makePostRequest(permissionUrl,"permission15",accessToken,permission15);

        // Permissions regarding Resource
        String permission16 = "{ \"name\": \"Resource Management - Create\", \"actionId\": 1, \"resourceId\": 4 }";
        String permission17 = "{ \"name\": \"Resource Management - Read\", \"actionId\": 2, \"resourceId\": 4 }";
        String permission18 = "{ \"name\": \"Resource Management - Update\", \"actionId\": 3, \"resourceId\": 4 }";
        String permission19 = "{ \"name\": \"Resource Management - Delete\", \"actionId\": 4, \"resourceId\": 4 }";
        String permission20 = "{ \"name\": \"Resource Management - All\", \"actionId\": 5, \"resourceId\": 4 }";

        makePostRequest(permissionUrl,"permission16",accessToken,permission16);
        makePostRequest(permissionUrl,"permission17",accessToken,permission17);
        makePostRequest(permissionUrl,"permission18",accessToken,permission18);
        makePostRequest(permissionUrl,"permission19",accessToken,permission19);
        makePostRequest(permissionUrl,"permission20",accessToken,permission20);

        // Permissions regarding Action
        String permission21 = "{ \"name\": \"Action Management - Create\", \"actionId\": 1, \"resourceId\": 5 }";
        String permission22 = "{ \"name\": \"Action Management - Read\", \"actionId\": 2, \"resourceId\": 5 }";
        String permission23 = "{ \"name\": \"Action Management - Update\", \"actionId\": 3, \"resourceId\": 5 }";
        String permission24 = "{ \"name\": \"Action Management - Delete\", \"actionId\": 4, \"resourceId\": 5 }";
        String permission25 = "{ \"name\": \"Action Management - All\", \"actionId\": 5, \"resourceId\": 5 }";

        makePostRequest(permissionUrl,"permission21",accessToken,permission21);
        makePostRequest(permissionUrl,"permission22",accessToken,permission22);
        makePostRequest(permissionUrl,"permission23",accessToken,permission23);
        makePostRequest(permissionUrl,"permission24",accessToken,permission24);
        makePostRequest(permissionUrl,"permission25",accessToken,permission25);

        // Permissions regarding Tenant
        String permission26 = "{ \"name\": \"Tenant Management - Create\", \"actionId\": 1, \"resourceId\": 6 }";
        String permission27 = "{ \"name\": \"Tenant Management - Read\", \"actionId\": 2, \"resourceId\": 6 }";
        String permission28 = "{ \"name\": \"Tenant Management - Update\", \"actionId\": 3, \"resourceId\": 6 }";
        String permission29 = "{ \"name\": \"Tenant Management - Delete\", \"actionId\": 4, \"resourceId\": 6 }";
        String permission30 = "{ \"name\": \"Tenant Management - All\", \"actionId\": 5, \"resourceId\": 6 }";

        makePostRequest(permissionUrl,"permission26",accessToken,permission26);
        makePostRequest(permissionUrl,"permission27",accessToken,permission27);
        makePostRequest(permissionUrl,"permission28",accessToken,permission28);
        makePostRequest(permissionUrl,"permission29",accessToken,permission29);
        makePostRequest(permissionUrl,"permission30",accessToken,permission30);

        // Permissions regarding Tenant Role
        String permission31 = "{ \"name\": \"Tenant Role Management - Create\", \"actionId\": 1, \"resourceId\": 7 }";
        String permission32 = "{ \"name\": \"Tenant Role Management - Read\", \"actionId\": 2, \"resourceId\": 7 }";
        String permission33 = "{ \"name\": \"Tenant Role Management - Update\", \"actionId\": 3, \"resourceId\": 7 }";
        String permission34 = "{ \"name\": \"Tenant Role Management - Delete\", \"actionId\": 4, \"resourceId\": 7 }";
        String permission35 = "{ \"name\": \"Tenant Role Management - All\", \"actionId\": 5, \"resourceId\": 7 }";

        makePostRequest(permissionUrl,"permission31",accessToken,permission31);
        makePostRequest(permissionUrl,"permission32",accessToken,permission32);
        makePostRequest(permissionUrl,"permission33",accessToken,permission33);
        makePostRequest(permissionUrl,"permission34",accessToken,permission34);
        makePostRequest(permissionUrl,"permission35",accessToken,permission35);

        // Permissions regarding Tenant Role Permission
        String permission36 = "{ \"name\": \"Tenant Role Permission Management - Create\", \"actionId\": 1, \"resourceId\": 8 }";
        String permission37 = "{ \"name\": \"Tenant Role Permission Management - Read\", \"actionId\": 2, \"resourceId\": 8 }";
        String permission38 = "{ \"name\": \"Tenant Role Permission Management - Update\", \"actionId\": 3, \"resourceId\": 8 }";
        String permission39 = "{ \"name\": \"Tenant Role Permission Management - Delete\", \"actionId\": 4, \"resourceId\": 8 }";
        String permission40 = "{ \"name\": \"Tenant Role Permission Management - All\", \"actionId\": 5, \"resourceId\": 8 }";

        makePostRequest(permissionUrl,"permission36",accessToken,permission36);
        makePostRequest(permissionUrl,"permission37",accessToken,permission37);
        makePostRequest(permissionUrl,"permission38",accessToken,permission38);
        makePostRequest(permissionUrl,"permission39",accessToken,permission39);
        makePostRequest(permissionUrl,"permission40",accessToken,permission40);

        // Permissions regarding Tenant Role User
        String permission41 = "{ \"name\": \"Tenant Role User Management - Create\", \"actionId\": 1, \"resourceId\": 9 }";
        String permission42 = "{ \"name\": \"Tenant Role User Management - Read\", \"actionId\": 2, \"resourceId\": 9 }";
        String permission43 = "{ \"name\": \"Tenant Role User Management - Update\", \"actionId\": 3, \"resourceId\": 9 }";
        String permission44 = "{ \"name\": \"Tenant Role User Management - Delete\", \"actionId\": 4, \"resourceId\": 9 }";
        String permission45 = "{ \"name\": \"Tenant Role User Management - All\", \"actionId\": 5, \"resourceId\": 9 }";

        makePostRequest(permissionUrl,"permission41",accessToken,permission41);
        makePostRequest(permissionUrl,"permission42",accessToken,permission42);
        makePostRequest(permissionUrl,"permission43",accessToken,permission43);
        makePostRequest(permissionUrl,"permission44",accessToken,permission44);
        makePostRequest(permissionUrl,"permission45",accessToken,permission45);
    }

    /**
     * Given a generated Access Token, consumes Action Endpoint to create the first basic actions:
     * Create, Read, Update, Delete and All
     * @param accessToken Access Token generated via KeyCloak
     */
    private static void actionCreation(String accessToken) {
        System.out.println("Starting action...");
        String actionUrl= getPermissionManagementBaseURL() + "/action";
        String action1 ="{ \"name\": \"Create\" }";
        String action2 ="{ \"name\": \"Read\" }";
        String action3 ="{ \"name\": \"Update\" }";
        String action4 ="{ \"name\": \"Delete\" }";
        String action5 ="{ \"name\": \"All\" }";

        makePostRequest(actionUrl,"action1",accessToken,action1);
        makePostRequest(actionUrl,"action2",accessToken,action2);
        makePostRequest(actionUrl,"action3",accessToken,action3);
        makePostRequest(actionUrl,"action4",accessToken,action4);
        makePostRequest(actionUrl,"action5",accessToken,action5);
    }

    /**
     * Method that checks response and print status
     * @param response response to be checked
     * @param msg status to be print
     */
    public static void checkResponse(HttpResponse response,String msg){
        if(!response.isSuccess()){
            System.exit(1);
            System.err.println(msg);
        }
    }

    /**
     * Given a generated Access Token, consumes Tenant Endpoint to create the first basic Tenants
     * @param accessToken Access Token generated via KeyCloak
     */
    public static void tenantCreation(String accessToken){
        String tenant1 ="{\"tenantKey\": \"EVCorp\", \"name\": \"Root Tenant\", \"tenantType\": \"Root\", \"tenantStart\": \"2030-01-22\", \"tenantEnd\": \"2040-01-22\"}";
        String tenant2 ="{\"tenantKey\": \"EVCorp\", \"name\": \"Client Tenant\", \"tenantType\": \"Client\", \"tenantStart\": \"2030-01-22\", \"tenantEnd\": \"2040-01-22\", \"clientAddress\": \"Sophiestrasse 33\", \"clientZipCode\": \"38118\", \"clientCity\":\"Braunschweig\", \"clientCountry\":\"Germany\", \"clientPhoneNumber\":933876547, \"clientEmail\":\"email@email.com\", \"parentId\":1, \"clientId\":1}";
        String tenant3 ="{\"tenantKey\": \"EVCorp\", \"name\": \"Sub Tenant\", \"tenantType\": \"Sub\", \"tenantStart\": \"2030-01-22\", \"tenantEnd\": \"2040-01-22\", \"parentId\":2, \"clientId\":2}";

        String tenantUrl= getTenantManagementBaseURL() + "/tenant";

        makePostRequest(tenantUrl,"tenant1",accessToken,tenant1);
        makePostRequest(tenantUrl,"tenant2",accessToken,tenant2);
        makePostRequest(tenantUrl,"tenant3",accessToken,tenant3);

    }

    /**
     * Core method that invokes a endpoint. It uses a REst client to do that.
     * @param url endpoint url
     * @param identifier String that identifies a request
     * @param accessToken access token generated via KeyCloak
     * @param body request body
     * @return HashMap that corresponds to the obtained response
     */
    public static HashMap makePostRequest(String url,String identifier, String accessToken, String body){
        HttpResponse<HashMap> response = Unirest.post(url)
                .header("Authorization", "Bearer "+accessToken)
                .header("Content-Type","application/json")
                .body(body).asObject(HashMap.class);

        //checkResponse(response,identifier);
        System.out.println(identifier + " " + response.getStatus());
        return response.getBody();
    }
}
