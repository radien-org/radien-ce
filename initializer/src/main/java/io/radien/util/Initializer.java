/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.radien.api.entity.Page;
import kong.unirest.UnirestParsingException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigSource;

import javax.xml.bind.DatatypeConverter;
import org.json.simple.parser.ParseException;

/**
 * Console application that uses REST Clients to consume the respective endpoints responsible
 * for creating (initialize) the following entities into the Radien database: Action, Resource, Permission,
 * Role, Tenant, TenantRole and TenantPermission
 */
public class Initializer {

    public static final String JSON_EXTENSION = ".json";
    public static final String CONTROL_FILE_NAME_INIT_PATTERN = "loaded-on-env-";
    public static final String RADIEN_ENV_PROPERTY = "RADIEN_ENV";
    public static final String INITIALIZER_CONFIG_DIR_PROPERTY = "initializer.configuration.location";
    private static Map<Object,Object> tenants;
    private static Map<Object, Object> roles;
    private static Map<Object,Object> actions;
    private static Map<Object, Object> resources;
    private static Map<Long, Map<Long,Object>> tenantRoles;
    private static Map<Long, Map<Long,Object>> permissions;

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
                initializeActionAndResourceMaps(accessToken);
                permissionCreation(accessToken);
                initializePermissionMap(accessToken);
            }
            if(modules.contains("role")){
                roleCreation(accessToken);
                initializeTenantAndRoleMaps(accessToken);

                tenantRoleCreation(accessToken);

            }
        } else {
            tenantCreation(accessToken);

            actionCreation(accessToken);
            resourceCreation(accessToken);
            initializeActionAndResourceMaps(accessToken);
            permissionCreation(accessToken);

            roleCreation(accessToken);
            initializeTenantAndRoleMaps(accessToken);
            initializeTenantRoleMap(accessToken);
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
     * Given a generated Access Token, consumes Tenant Endpoint to create the first basic Tenants
     * @param accessToken Access Token generated via KeyCloak
     */
    public static void tenantCreation(String accessToken){
        System.out.println("Starting tenant...");
        String location = getConfigProperty("initializer.tenant.directory.location");
        String tenantUrl= getTenantManagementBaseURL() + "/tenant";
        loadEntitiesFromDirectory(location, accessToken, tenantUrl, "tenant",null);
    }

    /**
     * Given a generated Access Token, consumes TenantRole Endpoint to create the first basic
     * TenantRoles associations.
     * @param accessToken Access Token generated via KeyCloak
     */
    private static void tenantRoleCreation(String accessToken) {
        System.out.println("Starting tenant role...");
        String tenantRoleUrl = getRoleManagementBaseURL() + "/tenantrole";
        String location = getConfigProperty("initializer.tenantRole.directory.location");
        loadEntitiesFromDirectory(location, accessToken, tenantRoleUrl, "tenantRole",Initializer::tenantRoleTranslator);

        initializeTenantRoleMap(accessToken);

        System.out.println("Starting tenant role permission...");
        String tenantRolePermissionUrl = getRoleManagementBaseURL() + "/tenantrolepermission";
        location = getConfigProperty("initializer.tenantRolePermission.directory.location");
        loadEntitiesFromDirectory(location, accessToken, tenantRolePermissionUrl, "tenantRolePermission",Initializer::tenantRolePermissionTranslator);

        loadTenantRoleUsers(accessToken);
    }

    private static void initializeTenantRoleMap(String accessToken) {
        if(tenantRoles == null) {
            String tenantUrl = getRoleManagementBaseURL() + "/tenantrole/all";
            tenantRoles = getDoubleMapFromPage(tenantUrl, "tenantId","roleId", "tenantRoleFinder", accessToken);
        }

    }

    private static void initializePermissionMap(String accessToken) {
        if(permissions == null) {
            String permissionUrl = getPermissionManagementBaseURL() + "/permission";
            permissions = getDoubleMapFromPage(permissionUrl, "resourceId","actionId", "permissionFinder", accessToken);
        }

    }

    private static void initializeTenantAndRoleMaps(String accessToken) {
        if(tenants == null) {
            String tenantUrl = getTenantManagementBaseURL() + "/tenant";
            tenants = getMapFromList(tenantUrl, "name", "tenantFinder", accessToken);
        }
        if(roles == null) {
            String roleUrl = getRoleManagementBaseURL() + "/role";
            roles = getMapFromPage(roleUrl, "name", "RoleFinder", accessToken);
        }
    }

    private static void initializeActionAndResourceMaps(String accessToken) {
        if(actions == null) {
            String actionUrl= getPermissionManagementBaseURL() + "/action";
            actions = getMapFromPage(actionUrl, "name", "actionFinder", accessToken);
        }
        if(resources == null) {
            String resourceUrl= getPermissionManagementBaseURL() + "/resource";
            resources = getMapFromPage(resourceUrl, "name", "ResourceFinder", accessToken);
        }
    }

    public static List<String> tenantRoleTranslator(List<String> list){
        List<String> results= new ArrayList<>();
        JSONParser parser = new JSONParser();
        for(String tenantRoleStr:list){
            try {
                JSONObject object = (JSONObject) parser.parse(tenantRoleStr);


                JSONObject tenantRole = new JSONObject();
                String tenantName = (String)object.get("tenantName");
                Map tenant =(Map)tenants.get(tenantName);
                if(tenant == null){
                    System.out.println("Tenant not found "+ tenantName);
                    System.exit(-9);
                }
                String roleName = (String)object.get("roleName");
                Map role =(Map)roles.get(roleName);
                if(role == null){
                    System.out.println("Role not found "+ roleName);
                    System.exit(-10);
                }
                tenantRole.put("tenantId",((Double)tenant.get("id")).longValue());
                tenantRole.put("roleId",((Double)role.get("id")).longValue());
                results.add(tenantRole.toJSONString());


            } catch (ParseException e) {
                e.printStackTrace();
                System.exit(-5);
            }
        }
        return results;
    }

    public static List<String> permissionTranslator(List<String> list){
        List<String> results= new ArrayList<>();
        JSONParser parser = new JSONParser();
        for(String permissionStr:list){
            try {
                JSONObject object = (JSONObject) parser.parse(permissionStr);


                JSONObject permission = new JSONObject();
                String actionName = (String)object.get("actionName");
                Map action =(Map)actions.get(actionName);
                if(action == null){
                    System.out.println("Action not found "+ actionName);
                    System.exit(-12);
                }
                String resourceName = (String)object.get("resourceName");
                Map resource =(Map)resources.get(resourceName);
                if(resource == null){
                    System.out.println("Resource not found "+ resourceName);
                    System.exit(-13);
                }
                permission.put("actionId",((Double)action.get("id")).longValue());
                permission.put("resourceId",((Double)resource.get("id")).longValue());
                permission.put("name",object.get("name"));
                results.add(permission.toJSONString());


            } catch (ParseException e) {
                e.printStackTrace();
                System.exit(-14);
            }
        }
        return results;
    }

    public static List<String> tenantRolePermissionTranslator(List<String> list){
        List<String> results= new ArrayList<>();
        JSONParser parser = new JSONParser();
        for(String tenantRoleStr:list){
            try {
                JSONObject object = (JSONObject) parser.parse(tenantRoleStr);


                JSONObject tenantRolePermission = new JSONObject();
                String tenantName = (String)object.get("tenantName");
                Map tenant =(Map)tenants.get(tenantName);
                if(tenant == null){
                    System.out.println("Tenant not found "+ tenantName);
                    System.exit(-9);
                }

                String roleName = (String)object.get("roleName");
                Map role =(Map)roles.get(roleName);
                if(role == null){
                    System.out.println("Role not found "+ roleName);
                    System.exit(-10);
                }

                Map tenantRole1 = tenantRoles.get(((Double)tenant.get("id")).longValue());
                if(tenantRole1 == null){
                    System.out.println("TenantRole not found with Tenant with name "+ tenantName);
                    System.exit(-11);
                }
                Map tenantRole2 = (Map) tenantRole1.get(((Double)role.get("id")).longValue());
                if(tenantRole2 == null){
                    System.out.println("TenantRole not found with Role with name "+ roleName);
                    System.exit(-12);
                }

                String actionName = (String)object.get("actionName");
                Map action =(Map)actions.get(actionName);
                if(action == null){
                    System.out.println("Action not found "+ actionName);
                    System.exit(-13);
                }

                String resourceName = (String)object.get("resourceName");
                Map resource =(Map)resources.get(resourceName);
                if(resource == null){
                    System.out.println("Resource not found "+ resourceName);
                    System.exit(-14);
                }

                Map permission1 = permissions.get(((Double)resource.get("id")).longValue());
                if(permission1 == null){
                    System.out.println("Permission not found with Tenant with name "+ tenantName);
                    System.exit(-15);
                }
                Map permission2 = (Map) permission1.get(((Double)action.get("id")).longValue());
                if(permission2 == null){
                    System.out.println("Permission not found with action with name"+ actionName);
                    System.exit(-16);
                }

                tenantRolePermission.put("tenantRoleId",((Double)tenantRole2.get("id")).longValue());
                tenantRolePermission.put("permissionId", ((Double)permission2.get("id")).longValue());
                results.add(tenantRolePermission.toJSONString());


            } catch (ParseException e) {
                e.printStackTrace();
                System.exit(-5);
            }
        }
        return results;
    }

    private static void loadTenantRoleUsers(String accessToken) {
        System.out.println("Starting tenant role user...");
        String tenantRoleUserUrl = getRoleManagementBaseURL() + "/tenantroleuser";
        String directory = getConfigProperty("initializer.tenantRoleUser.directory.location");
        String identifier = "tenantRoleUser";
        try {
            List<Path> paths = getFilesPaths(directory, identifier);
            Set<String> namesForFilesAlreadyLoaded = getFilesAlreadyLoaded(getConfigProperty(INITIALIZER_CONFIG_DIR_PROPERTY),
                    getConfigProperty(RADIEN_ENV_PROPERTY));
            for (Path path:paths) {
                String location = path.toString();
                if (!namesForFilesAlreadyLoaded.contains(path.getFileName().toString())) {
                    System.out.println(location + " - " + getMd5(location));
                    List<String> postBodies = getPostBodies(location);
                    createActiveTenants(accessToken, postBodies);
                    executePostForBodies(accessToken, postBodies, tenantRoleUserUrl, identifier);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(5);
        }
    }

    /**
     * Create active tenants references, taking in account the tenantRoleUser information
     * @param accessToken Access Token generated via KeyCloak
     * @param tenantRoleUserBodies
     */
    private static void createActiveTenants(String accessToken, List<String> tenantRoleUserBodies) {
        JSONParser parser = new JSONParser();
        for (String tenantRoleUserBody: tenantRoleUserBodies) {
            JSONObject json = null;
            try {
                json = (JSONObject) parser.parse(tenantRoleUserBody);
            } catch (ParseException e) {
                e.printStackTrace();
                System.exit(9);
            }
            Long tenantRoleId = (Long) json.get("tenantRoleId");
            Long userId = (Long) json.get("userId");

            String urlGetTenantRoleById = getRoleManagementBaseURL() + "/tenantrole/" + tenantRoleId;
            HashMap map = makeGetRequest(urlGetTenantRoleById, "retrievingTenantRole", accessToken);

            Long tenantId = ((Double) map.get("tenantId")).longValue();
            String urlGetTenantById = getTenantManagementBaseURL() + "/tenant/" + tenantId;
            map = makeGetRequest(urlGetTenantById, "retrievingTenant", accessToken);

            String tenantName = map.get("name").toString();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tenantId", tenantId);
            jsonObject.put("tenantName", tenantName);
            jsonObject.put("userId", userId);
            jsonObject.put("isTenantActive", "false");

            String activeTenantUrl = getTenantManagementBaseURL() + "/activeTenant";
            makePostRequest(activeTenantUrl, "activeTenant", accessToken, jsonObject.toJSONString());
        }
    }

    /**
     * Given a generated Access Token, consumes Role Endpoint to create the first basic Roles.
     * @param accessToken Access Token generated via KeyCloak
     */
    private static void roleCreation(String accessToken) {
        System.out.println("Starting roles...");
        String roleUrl= getRoleManagementBaseURL() + "/role";

        String location = getConfigProperty("initializer.role.directory.location");
        loadEntitiesFromDirectory(location, accessToken, roleUrl, "role",null);
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

        String location = getConfigProperty("initializer.resource.directory.location");
        loadEntitiesFromDirectory(location, accessToken, resourceUrl, "resource",null);
    }

    /**
     * Given a generated Access Token, consumes Permission Endpoint to create the first basic Permissions
     * @param accessToken Access Token generated via KeyCloak
     */
    private static void permissionCreation(String accessToken){
        System.out.println("Starting permission...");
        String permissionUrl = getPermissionManagementBaseURL() + "/permission";

        String location = getConfigProperty("initializer.permission.directory.location");
        loadEntitiesFromDirectory(location, accessToken, permissionUrl, "permission",Initializer::permissionTranslator);
    }

    /**
     * Given a generated Access Token, consumes Action Endpoint to create the first basic actions:
     * Create, Read, Update, Delete and All
     * @param accessToken Access Token generated via KeyCloak
     */
    private static void actionCreation(String accessToken) {
        System.out.println("Starting action...");
        String actionUrl= getPermissionManagementBaseURL() + "/action";

        String location = getConfigProperty("initializer.action.directory.location");
        loadEntitiesFromDirectory(location, accessToken, actionUrl, "action",null);
    }

    /**
     * Given a directory location, load the entities from json files identified by a discriminator param.
     * For example, given a discriminator named action, it will load entities from all files that matches the
     * pattern action-(index).json
     * @param directoryLocation String that defines a directory location
     * @param accessToken JWT token to be informed a header parameter for the endpoint responsible to perform the
     *                    loading process
     * @param url String that identifies the url for the endpoint responsible to perform the loading process
     * @param identifier discriminator that corresponds to the kind of entity described via json file
     */
    protected static void loadEntitiesFromDirectory(String directoryLocation, String accessToken,
                                                    String url, String identifier, Function<List<String>,List<String>> function) {
        try {
            List<Path> paths = getFilesPaths(directoryLocation, identifier);
            Set<String> namesForFilesAlreadyLoaded = getFilesAlreadyLoaded(getConfigProperty(INITIALIZER_CONFIG_DIR_PROPERTY),
                    getConfigProperty(RADIEN_ENV_PROPERTY));
            for (Path path:paths) {
                String location = path.toString();
                if (!namesForFilesAlreadyLoaded.contains(path.getFileName().toString())) {
                    System.out.println(location + " - " + getMd5(location));
                    List<String> postBodies = getPostBodies(location);
                    if(function!=null){
                        postBodies = function.apply(postBodies);
                    }
                    executePostForBodies(accessToken, postBodies, url, identifier);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(5);
        }
    }



    /**
     * Given a directory, load json files (Path) whose names matches a pattern (names starting with
     * some informed starting substring).
     *
     * These files will load and shown in a sequential order determined by an index contained in the name.
     *
     * @param directoryLocation String that describes a directory
     * @param nameStartingWith Pattern that describes the seeked json files
     * @return
     */
    protected static List<Path> getFilesPaths(String directoryLocation, String nameStartingWith) throws IOException {
        List<Path> paths = Files.list(Paths.get(directoryLocation)).
                filter(path -> {
                    String name = path.getFileName().toString();
                    String startingToken = nameStartingWith+"-";
                    return name.startsWith(startingToken) && name.endsWith(JSON_EXTENSION) && hasNumericIndex(name);
                }).sorted((path1, path2) ->  {
                    Integer indexForPath1 = Integer.valueOf(getIndex(path1.getFileName().toString()));
                    Integer indexForPath2 = Integer.valueOf(getIndex(path2.getFileName().toString()));
                    return indexForPath1 > indexForPath2 ? 1 : (indexForPath1 == indexForPath2 ? 0 : -1);
                }).collect(Collectors.toList());
        return paths;
    }

    /**
     * Check if a Json file name has an index descriptor
     * (i.e action-21.json contains 21 as index)
     * @param fileName name for a Json file
     * @return true if contains an index, otherwise false
     */
    public static boolean hasNumericIndex(String fileName) {
        String indexAsString = getIndex(fileName);
        try {
            Integer.parseInt(indexAsString);
        } catch(NumberFormatException n) {
            return false;
        }
        return true;
    }

    /**
     * Given a file name, extract the index that eventually exists before
     * the extension descriptor (i.e action-1111.json should return 111)
     * @param fileName name for a Json file
     * @return String that corresponds of a index
     */
    public static String getIndex(String fileName) {
        StringBuilder sb = new StringBuilder();

        // Regex to extract the string between expected two delimiters
        String regex = "\\-(.*?)\\.json";

        // Compile the Regex
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(fileName);
        while (m.find()) {
            sb.append(m.group(1));
        }
        return sb.toString();
    }

    /**
     * Given an execution directory reads the control file that contains
     * references for the json files already loaded.
     *
     * The control file name corresponds to the constant
     * loaded-on-env- + {ENV} environment description.
     *
     * @param locationDirectory String that describes the directory location
     *                          where the control file is seeked
     * @param env String that describes the environment
     * @return Set containing name of all already loaded files
     * @throws IOException thrown in case of any i/o error
     */
    public static Set<String> getFilesAlreadyLoaded(String locationDirectory,
                                                    String env) throws IOException {
        String fileName = CONTROL_FILE_NAME_INIT_PATTERN + env;
        Path path = Paths.get(locationDirectory + File.separator + fileName);
        return Files.lines(path).filter(line -> !line.startsWith("#")).collect(Collectors.toSet());
    }

    /**
     * Method that checks response and print status
     * @param response response to be checked
     * @param identifier to be print
     */
    public static void checkResponse(HttpResponse response,String identifier){
        if(!response.isSuccess()){
            System.out.println(identifier);
            if(response.getBody()!=null) {
                System.out.println(response.getBody().toString());
            }
            Optional<UnirestParsingException> parsingError = response.getParsingError();
            if(parsingError.isPresent()){

                System.out.println("originalBody:" + parsingError.get().getOriginalBody());
                System.out.println("Message:" + parsingError.get().getMessage());
            }
            System.exit(1);
        }
    }

    /**
     * Execute post requests for the following parameters
     * @param accessToken JWT token to be informed as header parameter for the endpoint
     * @param postBodies list containing string representation of json objects to be submitted
     * @param url url for endpoint that will handle the request
     * @param identifier discriminator/identifier for the entities that will be loaded
     */
    private static void executePostForBodies(String accessToken, List<String> postBodies, String url, String identifier) {
        for (int i = 0; i < postBodies.size(); i++) {
            makePostRequest(url, identifier + i, accessToken, postBodies.get(i));
        }
    }

    /**
     * Given a file described by its path (location), retrieves the json strings contained within it
     * @param location string that describes json file path
     * @return list containing strings (for json objects)
     */
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
            e.printStackTrace();
            System.exit(2);
        }
        return postBodies;
    }

    public static Map<Object,Object> getMapFromList(String url, String fieldForKey,String identifier, String accessToken){
        System.out.println(url);
        HttpResponse<ArrayList> response = Unirest.get(url)
                .header("Authorization", "Bearer "+accessToken)
                .header("Content-Type","application/json")
                .asObject(ArrayList.class);

        String msg =identifier + " " + response.getStatus();

        System.out.println(identifier + " Status:" + response.getStatus() + " Success:" + response.isSuccess());
        checkResponse(response,identifier);
        ArrayList<Map<Object,Object>> list = response.getBody();
        return list.stream().collect(Collectors.toMap(k->k.get(fieldForKey),v->v));
    }

    public static Map<Object,Object> getMapFromPage(String url, String fieldForKey,String identifier, String accessToken){
        System.out.println(url);
        HttpResponse<Map> response = Unirest.get(url)
                .header("Authorization", "Bearer "+accessToken)
                .header("Content-Type","application/json")
                .asObject(Map.class);

        String msg =identifier + " " + response.getStatus();

        System.out.println(identifier + " Status:" + response.getStatus() + " Success:" + response.isSuccess());
        checkResponse(response,identifier);
        String urlTemp= url+"?pageSize=" + ((Double)response.getBody().get("totalResults")).intValue();
        System.out.println(urlTemp);
        response = Unirest.get(urlTemp)
                .header("Authorization", "Bearer "+accessToken)
                .header("Content-Type","application/json")
                .asObject(Map.class);
        System.out.println(identifier+2 + " Status:" + response.getStatus() + " Success:" + response.isSuccess());
        checkResponse(response,identifier);

        List<Map> list = (List<Map>) response.getBody().get("results");
        return list.stream().collect(Collectors.toMap(k->((Map)k).get(fieldForKey),v->v));
    }

    public static Map<Long,Map<Long,Object>> getDoubleMapFromPage(String url, String fieldForKey1,String fieldForKey2,String identifier, String accessToken){
        System.out.println(url);
        HttpResponse<Map> response = Unirest.get(url)
                .header("Authorization", "Bearer "+accessToken)
                .header("Content-Type","application/json")
                .asObject(Map.class);

        System.out.println(identifier + " Status:" + response.getStatus() + " Success:" + response.isSuccess());
        checkResponse(response,identifier);
        String urlTemp= url+"?pageSize=" + ((Double)response.getBody().get("totalResults")).intValue();
        System.out.println(urlTemp);
        response = Unirest.get(urlTemp)
                .header("Authorization", "Bearer "+accessToken)
                .header("Content-Type","application/json")
                .asObject(Map.class);
        System.out.println(identifier+2 + " Status:" + response.getStatus() + " Success:" + response.isSuccess());
        checkResponse(response,identifier);

        List<Map> list = (List<Map>) response.getBody().get("results");
        Map<Long,Map<Long,Object>> result = new HashMap<>();
        for(Map map:list){
            Long valueForKey1 = ((Double) map.get(fieldForKey1)).longValue();
            Map<Long,Object> lvl1 = result.get(valueForKey1);
            if(lvl1 == null){
                lvl1 = new HashMap<>();
            }
            lvl1.put(((Double)map.get(fieldForKey2)).longValue(),map);
            result.put(valueForKey1,lvl1);
        }

        return result;
    }

    public static Map<Long,Map<Long,Object>> getDoubleMapFromList(String url, String fieldForKey1,String fieldForKey2,String identifier, String accessToken){
        System.out.println(url);
        HttpResponse<ArrayList> response = Unirest.get(url)
                .header("Authorization", "Bearer "+accessToken)
                .header("Content-Type","application/json")
                .asObject(ArrayList.class);

        System.out.println(identifier + " Status:" + response.getStatus() + " Success:" + response.isSuccess());
        checkResponse(response,identifier);

        List<Map> list = (List<Map>) response.getBody();
        Map<Long,Map<Long,Object>> result = new HashMap<>();
        for(Map map:list){
            Long valueForKey1 = (Long) map.get(fieldForKey1);
            Map<Long,Object> lvl1 = result.get(valueForKey1);
            if(lvl1 == null){
                lvl1 = new HashMap<>();
            }
            lvl1.put((Long)map.get(fieldForKey2),map);
            result.put(valueForKey1,lvl1);
        }

        return result;
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

    /**
     * Similar to {@link Initializer#makePostRequest(String, String, String, String)}
     * this invokes an Http Get method endpoint
     * @param url endpoint url
     * @param identifier String that identifies a request
     * @param accessToken access token generated via KeyCloak
     * @return HashMap that corresponds to the obtained response
     */
    public static HashMap makeGetRequest(String url,String identifier, String accessToken){
        System.out.println(url);
        HttpResponse<HashMap> response = Unirest.get(url)
                .header("Authorization", "Bearer "+accessToken)
                .asObject(HashMap.class);
        System.out.println(identifier + " " + response.getStatus());
        checkResponse(response,identifier);
        return response.getBody();
    }

    /**
     * Extract the md5 hash code value from a file
     * @param filePath path that describes a file
     * @return md5 hash code value
     */
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
