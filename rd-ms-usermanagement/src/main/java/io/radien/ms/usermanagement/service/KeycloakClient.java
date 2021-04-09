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
package io.radien.ms.usermanagement.service;


import io.radien.exception.SystemException;
import io.radien.ms.usermanagement.client.exceptions.RemoteResourceException;
import io.radien.ms.usermanagement.config.KeycloakEmailActions;
import kong.unirest.Headers;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * @author Nuno Santana
 */
public class KeycloakClient {
    private static final Logger log = LoggerFactory.getLogger(KeycloakClient.class);

    private HashMap<String, String> result;
    private String idpUrl;
    private String clientId;
    private String username;
    private String password;
    private String tokenPath;
    private String userPath;
    private String radienClientId;
    private String radienSecret;
    private String radienTokenPath;

    public KeycloakClient() {
    }

    public KeycloakClient idpUrl(String idpUrl) {
        this.idpUrl = idpUrl;
        return this;
    }

    public KeycloakClient tokenPath(String tokenPath) {
        this.tokenPath = tokenPath;
        return this;
    }

    public KeycloakClient clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public KeycloakClient username(String username) {
        this.username = username;
        return this;
    }

    public KeycloakClient password(String password) {
        this.password = password;
        return this;
    }

    public KeycloakClient userPath(String userPath) {
        this.userPath = userPath;
        return this;
    }

    public KeycloakClient radienClientId(String radienClientId) {
        this.radienClientId = radienClientId;
        return this;
    }

    public KeycloakClient radienSecret(String radienSecret) {
        this.radienSecret = radienSecret;
        return this;
    }
    public KeycloakClient radienTokenPath(String radienTokenPath) {
        this.radienTokenPath = radienTokenPath;
        return this;
    }

    public String getAccessToken() {
        return result.get("access_token");
    }

    public String getRefreshToken() {
        return result.get("refresh_token");
    }

    public HashMap login() throws RemoteResourceException {
        HttpResponse<HashMap> response = Unirest.post(idpUrl + tokenPath)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
                .field("client_id", clientId)
                //.field("redirect_uri", "https://localhost:8443/web/login")
                .field("grant_type", "password")
                .field("username", username)
                .field("password", password)
                .asObject(HashMap.class);
        if (response.isSuccess()) {
            result = response.getBody();
            return result;
        } else {
            //TODO: improve Error handling
            throw new RemoteResourceException("Error on login");
        }
    }

    public String createUser(UserRepresentation userRepresentation) throws RemoteResourceException {
        HttpResponse<String> response = Unirest.post(idpUrl + userPath)
                .header(HttpHeaders.AUTHORIZATION, getAuthorization())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(userRepresentation)
                .asObject(String.class);
        if (response.isSuccess()) {
            Headers headers = response.getHeaders();
            if (headers != null) {
                List<String> locations = headers.get(HttpHeaders.LOCATION);
                if (!locations.isEmpty()) {
                    String location = locations.get(0);
                    if (location != null) {
                        int lastIndexOf = location.lastIndexOf("/");
                        if (lastIndexOf > 0 && lastIndexOf + 1 <= location.length()) {
                            String sub = location.substring(lastIndexOf + 1);
                            return sub;
                        }
                    }
                }
            }
        } else {
            log.error("Status:{}, Body:{}", response.getStatus(), response.getBody());
            if(Response.Status.CONFLICT.getStatusCode() == response.getStatus ()){
                String message = "User may already exist in Keycloak";
                throw new RemoteResourceException(message);
            }
        }
        throw new RemoteResourceException("Unable to create User in keycloak");
    }

    public void sendUpdatePasswordEmail(String sub) throws RemoteResourceException {
        HttpResponse<String> response = Unirest.put(idpUrl + userPath + "/" + sub + "/execute-actions-email")
                .header(HttpHeaders.AUTHORIZATION, getAuthorization())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body("[\"" + KeycloakEmailActions.UPDATE_PASSWORD + "\"]")
                .asObject(String.class);
        if (!response.isSuccess()) {
            log.error("status {},body {}",response.getStatus(),response.getBody());
            throw new RemoteResourceException("Unable to send update password email");
        }
    }

    public void deleteUser(String sub) throws RemoteResourceException {
        HttpResponse<String> response = Unirest.delete(idpUrl + userPath + "/" + sub)
                .header(HttpHeaders.AUTHORIZATION, getAuthorization())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .asObject(String.class);
        if (!response.isSuccess()) {
            log.error(response.getBody());
            throw new RemoteResourceException("Unable to delete User");
        }
    }

    public void refreshToken() throws RemoteResourceException {
        HttpResponse<HashMap> response = Unirest.post(idpUrl + tokenPath)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
                .field("client_id", clientId)
                .field("grant_type", "refresh_token")
                .field("refresh_token", getRefreshToken())
                .asObject(HashMap.class);
        if (response.isSuccess()) {
            result = response.getBody();
        } else {
            //TODO: improve Error handling
            throw new RemoteResourceException("Unable to refresh token");
        }
    }

    public String refreshToken(String refreshToken) throws RemoteResourceException {
        HttpResponse<HashMap> response = Unirest.post(idpUrl + radienTokenPath)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
                .field("client_id", radienClientId)
                .field("client_secret", radienSecret)
                .field("grant_type", "refresh_token")
                .field("refresh_token", refreshToken)
                .asObject(HashMap.class);
        if (response.isSuccess()) {
            result = response.getBody();
            return result.get("access_token");
        } else {
            //TODO: improve Error handling
            // {error_description=Token is not active, error=invalid_grant}
            if(response.getBody()!= null) {
                log.error(response.getBody().toString());
            }
            throw new RemoteResourceException("Unable to refresh token");
        }
    }

    public void updateUser(String sub,UserRepresentation userRepresentation) throws RemoteResourceException {
        HttpResponse<String> response = Unirest.put(idpUrl + userPath + "/" + sub)
                .header(HttpHeaders.AUTHORIZATION, getAuthorization())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(userRepresentation)
                .asString();
        if(!response.isSuccess()){
            throw new RemoteResourceException("Unable to update user");
        }
    }

    private String getAuthorization() {
        return "Bearer " + getAccessToken();
    }
}
