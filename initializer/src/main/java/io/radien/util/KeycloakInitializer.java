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

import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static io.radien.util.Headers.APPLICATION_JSON;
import static io.radien.util.Headers.CONTENT_TYPE;
import static io.radien.util.RequestUtils.checkResponse;
import static io.radien.util.RequestUtils.logProgress;

public class KeycloakInitializer {
    private static final Logger log
            = LoggerFactory.getLogger(KeycloakInitializer.class);

    private final String workDir;
    private String masterRealmUser;
    private String masterRealmUserPassword;
    private String idpUrl;
    private static final String MASTER_TOKEN_PATH = "/auth/realms/master/protocol/openid-connect/token";
    private static final String REALMS = "/auth/admin/realms/";
    private static final String MASTER_CLIENTS = "/auth/admin/realms/master/clients";
    private static final String MASTER_USERS = "/auth/admin/realms/master/users";
    private static final String RADIEN_USERS = "/auth/admin/realms/radien/users";

    public KeycloakInitializer(String masterRealmUser, String masterRealmUserPassword,String idpUrl,String workDir) {
        this.masterRealmUser = masterRealmUser;
        this.masterRealmUserPassword = masterRealmUserPassword;
        this.idpUrl = idpUrl;
        this.workDir = workDir;
    }

    public void createRealm(String masterAccessToken){
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(workDir + File.separator +"radien.json"));
        } catch (IOException e) {
            String msg = Paths.get(".").toAbsolutePath().toString();
            log.error("location expected {}", msg);
            log.error("Unable to access realm file.",e);
            System.exit(51);
        }

        HttpResponse<HashMap<String,Object>> response = Unirest.post(idpUrl+REALMS)
                .body(String.join("\n",lines))
                .header(Headers.AUTHORIZATION,Headers.BEARER+masterAccessToken)
                .header(CONTENT_TYPE,APPLICATION_JSON)
                .asObject(new GenericType<HashMap<String,Object>>(){});
        logProgress("Create Realm",response);
    }

    public void createServiceClient(String masterAccessToken){

        List<String> clientContent = null;
        try {
            clientContent = Files.readAllLines(Paths.get(workDir + File.separator +"ServiceAccountsClient.json"));
        } catch (IOException e) {
            String msg = Paths.get(".").toAbsolutePath().toString();
            log.error("location expected {}", msg);
            log.error("Unable to Service Client file.",e);
            System.exit(52);
        }

        HttpResponse<HashMap<String,Object>> responseClient = Unirest.post(idpUrl+MASTER_CLIENTS)
                .body(String.join("\n",clientContent))
                .header(Headers.AUTHORIZATION,Headers.BEARER+masterAccessToken)
                .header(CONTENT_TYPE,APPLICATION_JSON)
                .asObject(new GenericType<HashMap<String,Object>>(){});
        logProgress("Create Service Client",responseClient);
    }

    public void createServiceClientUser(String masterAccessToken) {
        String serviceClientId = "/e0778735-a0a3-4ee7-908f-6dc13dee0d4a";

        HttpResponse<HashMap<String,Object>> responseClient = Unirest.get(idpUrl + MASTER_CLIENTS + serviceClientId + "/service-account-user")
                .header(Headers.AUTHORIZATION,Headers.BEARER+masterAccessToken)
                .header(CONTENT_TYPE,APPLICATION_JSON)
                .asObject(new GenericType<HashMap<String,Object>>(){});
        logProgress("Get Service Account User", responseClient);
        String serviceAccountUserId = responseClient.getBody().get("id").toString();
        log.info("Service Account User Id {}", serviceAccountUserId);

        HttpResponse<ArrayList<HashMap<String,Object>>> masterRadienClient = Unirest.get(idpUrl + MASTER_CLIENTS + "?clientId=radien-realm")
                .header(Headers.AUTHORIZATION,Headers.BEARER+masterAccessToken)
                .header(CONTENT_TYPE,APPLICATION_JSON)
                .asObject(new GenericType<ArrayList<HashMap<String,Object>>>(){});
        logProgress("Get Radien Client Id", masterRadienClient);
        String radienClientId = masterRadienClient.getBody().get(0).get("id").toString();
        log.info("Radien Client Id {}", radienClientId);

        HttpResponse<ArrayList<HashMap<String,Object>>> radienClientRoles = Unirest.get(idpUrl + MASTER_CLIENTS + "/" + radienClientId + "/roles?search=manage-users")
                .header(Headers.AUTHORIZATION,Headers.BEARER+masterAccessToken)
                .header(CONTENT_TYPE,APPLICATION_JSON)
                .asObject(new GenericType<ArrayList<HashMap<String,Object>>>(){});
        logProgress("Get Radien Client Manage Users Role", radienClientRoles);
        String roleId = radienClientRoles.getBody().get(0).get("id").toString();
        log.info("Radien Client Manage Users Role Id {}", roleId);

        HttpResponse<HashMap<String,Object>> roleAssignment = Unirest.post(idpUrl + MASTER_USERS + "/" + serviceAccountUserId + "/role-mappings/clients/" + radienClientId)
                .body("[{\"id\":\"" + roleId + "\",\n\"name\":\"manage-users\"}]")
                .header(Headers.AUTHORIZATION,Headers.BEARER+masterAccessToken)
                .header(CONTENT_TYPE,APPLICATION_JSON)
                .asObject(new GenericType<HashMap<String,Object>>(){});
        logProgress("Assign Service Client User Role", roleAssignment);
    }

    public List<String> getTokens(){
        Unirest.config().verifySsl(false);
        HttpResponse<HashMap<String,Object>> response = Unirest.post(idpUrl+MASTER_TOKEN_PATH)
                .header(CONTENT_TYPE, "application/x-www-form-urlencoded")
                .field("client_id", "admin-cli")
                .field("grant_type", "password")
                .field("username", masterRealmUser)
                .field("password", masterRealmUserPassword)
                .asObject(new GenericType<HashMap<String,Object>>(){});
        log.info(idpUrl+MASTER_TOKEN_PATH);
        logProgress("AdminRealm login",response);
        checkResponse(response,"AdminRealm login");

        HashMap<String, Object> body = response.getBody();
        String accessToken = (String) body.get("access_token");
        String refreshToken = (String) body.get("refresh_token");
        return Arrays.asList(accessToken, refreshToken);
    }

    public void createUser(String masterAccessToken) {
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(workDir + File.separator +"user.json"));
        } catch (IOException e) {
            String msg = Paths.get(".").toAbsolutePath().toString();
            log.error("location expected {}", msg);
            log.error("Unable to Service Client file.",e);
            System.exit(53);
        }

        HttpResponse<HashMap<String,Object>> response = Unirest.post(idpUrl+RADIEN_USERS)
                .body(String.join("\n",lines))
                .header(Headers.AUTHORIZATION,Headers.BEARER+masterAccessToken)
                .header(CONTENT_TYPE,APPLICATION_JSON)
                .asObject(new GenericType<HashMap<String,Object>>(){});
        logProgress("Create User",response);

    }
}
