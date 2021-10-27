/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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

import java.io.File;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigSource;

import javax.xml.bind.DatatypeConverter;

/**
 * Console application that uses REST Clients to consume the respective endpoints responsible
 * for creating (initialize) the following entities into the Radien database: Action, Resource, Permission,
 * Role, Tenant, TenantRole and TenantPermission
 */
public class Initializer {

    /**
     * Method that retrieves a config property
     * @param propertyKey
     * @return
     */
    public static String getConfigProperty(String propertyKey) {
        Config config = ConfigProvider.getConfig();
        return config.getValue(propertyKey, String.class);
    }

    public static Optional<List<String>> getConfigProperties(String propertyKey) {
        Config config = ConfigProvider.getConfig();
        return config.getOptionalValues(propertyKey, String.class);
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
    public static List<String> getTokens() {
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
        HashMap<String,Object> body = response.getBody();
        String accessToken = (String)body.get("access_token");
        String refreshToken = (String)body.get("refresh_token");
        
        return Arrays.asList(accessToken,refreshToken);
    }

    /**
     * Main method that orchestrates all the steps to create Action, Resource, Permission, Tenant, etc
     * @param args console argument
     */
    public static void main(String[] args) {

        System.out.println("Configuration sources end");
        Optional<List<String>> initializingMicroServices = getConfigProperties("initializer.modules");
        List<String> tokens = getTokens();
        String accessToken = tokens.get(0);
        if(initializingMicroServices.isPresent()){
            List<String> modules = initializingMicroServices.get();
            if(modules.contains("configInfo")){
                getConfigInfo();
            }
            if(modules.contains("tenant")){
                tenantCreation(accessToken);
            }
            if(modules.contains("permission")){
                actionCreation(accessToken);
                resourceCreation(accessToken);
                permissionCreation(accessToken);
            }
            if(modules.contains("role")){
                roleCreation(accessToken);
                tenantRoleCreation(accessToken);
            }
        } else {
            tenantCreation(accessToken);

            actionCreation(accessToken);
            resourceCreation(accessToken);
            permissionCreation(accessToken);

            roleCreation(accessToken);
            tenantRoleCreation(accessToken);
        }
    }

    private static void getConfigInfo() {
        Iterator<ConfigSource> iterator = ConfigProvider.getConfig().getConfigSources().iterator();
        System.out.println("Configuration sources begin");
        while (iterator.hasNext()){
            ConfigSource current = iterator.next();
            System.out.println(current.getName());
            for (String property : current.getProperties().keySet()) {
                System.out.println("\t" + property);
            }
        }

        File f = new File(".");
        System.out.println("Current execution directory is:");
        System.out.println(f.getAbsolutePath());
    }

    /**
     * Given a generated Access Token, consumes TenantRole Endpoint to create the first basic
     * TenantRoles associations.
     * @param accessToken Access Token generated via KeyCloak
     */
    private static void tenantRoleCreation(String accessToken) {
        String tenantRoleUrl = getRoleManagementBaseURL() + "/tenantrole";
        String location = getConfigProperty("initializer.tenantRole.file.location");
        System.out.println(getMd5(location));
        List<String> postBodies = getPostBodies(location);
        executePostForBodies(accessToken, postBodies, tenantRoleUrl, "tenantRole");

        String tenantRolePermissionUrl = getRoleManagementBaseURL() + "/tenantrolepermission";
        location = getConfigProperty("initializer.tenantRolePermission.file.location");
        System.out.println(getMd5(location));
        postBodies = getPostBodies(location);
        executePostForBodies(accessToken, postBodies, tenantRolePermissionUrl, "tenantRolePermission");

        String tenantRoleUserUrl = getRoleManagementBaseURL() + "/tenantroleuser";
        location = getConfigProperty("initializer.tenantRoleUser.file.location");
        System.out.println(getMd5(location));
        postBodies = getPostBodies(location);
        executePostForBodies(accessToken, postBodies, tenantRoleUserUrl, "tenantRoleUser");
    }

    /**
     * Given a generated Access Token, consumes Role Endpoint to create the first basic Roles.
     * @param accessToken Access Token generated via KeyCloak
     */
    private static void roleCreation(String accessToken) {
        System.out.println("Starting roles...");
        String roleUrl= getRoleManagementBaseURL() + "/role";

        String location = getConfigProperty("initializer.role.file.location");
        System.out.println(getMd5(location));
        List<String> postBodies = getPostBodies(location);
        executePostForBodies(accessToken, postBodies, roleUrl, "role");
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

        String location = getConfigProperty("initializer.resource.file.location");
        System.out.println(getMd5(location));
        List<String> postBodies = getPostBodies(location);
        executePostForBodies(accessToken, postBodies, resourceUrl, "resource");
    }

    /**
     * Given a generated Access Token, consumes Permission Endpoint to create the first basic Permissions
     * @param accessToken Access Token generated via KeyCloak
     */
    private static void permissionCreation(String accessToken){
        System.out.println("Start permission...");
        String permissionUrl = getPermissionManagementBaseURL() + "/permission";

        String location = getConfigProperty("initializer.permission.file.location");
        System.out.println(getMd5(location));
        List<String> postBodies = getPostBodies(location);
        executePostForBodies(accessToken, postBodies, permissionUrl, "action");

    }

    /**
     * Given a generated Access Token, consumes Action Endpoint to create the first basic actions:
     * Create, Read, Update, Delete and All
     * @param accessToken Access Token generated via KeyCloak
     */
    private static void actionCreation(String accessToken) {
        System.out.println("Starting action...");
        String actionUrl= getPermissionManagementBaseURL() + "/action";

        String location = getConfigProperty("initializer.action.file.location");
        System.out.println(getMd5(location));
        List<String> postBodies = getPostBodies(location);
        executePostForBodies(accessToken, postBodies, actionUrl, "action");
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
            System.err.println(response.getBody());
        }
    }

    /**
     * Given a generated Access Token, consumes Tenant Endpoint to create the first basic Tenants
     * @param accessToken Access Token generated via KeyCloak
     */
    public static void tenantCreation(String accessToken){

        String location = getConfigProperty("initializer.tenant.file.location");
        System.out.println(getMd5(location));
        List<String> postBodies = getPostBodies(location);

        String tenantUrl= getTenantManagementBaseURL() + "/tenant";
        executePostForBodies(accessToken, postBodies, tenantUrl, "tenant");

    }

    private static void executePostForBodies(String accessToken, List<String> postBodies, String url, String identifier) {
        for (int i = 0; i < postBodies.size(); i++) {
            makePostRequest(url, identifier + i, accessToken, postBodies.get(i));
        }
    }

    private static List<String> getPostBodies(String location) {
        JSONParser parser = new JSONParser();
        List<String> postBodies = new ArrayList<>();
        try {
            JSONArray array = (JSONArray) parser.parse(new FileReader(location));
            for(int i=0;i<array.size();i++){
                JSONObject obj = (JSONObject) array.get(i);
                postBodies.add(obj.toJSONString());
            }
        } catch( Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(2);
        }
        return postBodies;
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
        System.out.println(url);
        HttpResponse<HashMap> response = Unirest.post(url)
                .header("Authorization", "Bearer "+accessToken)
                .header("Content-Type","application/json")
                .body(body).asObject(HashMap.class);


        System.out.println(identifier + " " + response.getStatus());
        checkResponse(response,identifier);
        return response.getBody();
    }

    public static String getMd5(String filePath){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(Files.readAllBytes(Paths.get(filePath)));
            byte[] digest = md.digest();
            return DatatypeConverter
                    .printHexBinary(digest).toUpperCase();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            System.exit(3);
        }
        return null;
    }
}
