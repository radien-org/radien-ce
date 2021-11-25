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
package io.radien.ms.usermanagement.service;

import io.radien.exception.GenericErrorCodeMessage;
import io.radien.ms.usermanagement.client.exceptions.RemoteResourceException;


import io.radien.ms.usermanagement.config.KeycloakEmailActions;
import kong.unirest.Headers;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;


import kong.unirest.UnirestParsingException;
import org.keycloak.representations.idm.UserRepresentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Keycloak client side configuration constructor class
 *
 * @author Nuno Santana
 */
public class KeycloakClient {
    private static final String REFRESH_TOKEN="refresh_token";
    private static final String CLIENT_ID="client_id";
    private static final String GRANT_TYPE="grant_type";
    private static final String CLIENT_SECRET="client_secret";
    private static final String PASSWORD = "password";
    private static final String USERNAME = "username";
    private static final String JSON_BODY_AS_STRING = "{\"value\": \"%s\",\"type\": \"password\"}";
    private static final Logger log = LoggerFactory.getLogger(KeycloakClient.class);

    private HashMap<String, String> result;
    private String idpUrl;
    private String clientId;
    private String clientSecret;
    private String tokenPath;
    private String userPath;
    private String radienClientId;
    private String radienSecret;
    private String radienTokenPath;

    /**
     * Keycloak client empty constructor
     */
    public KeycloakClient() {
        log.info("keycloakClient starting");
    }

    /**
     * Keycloak idp url setter with getter
     * @param idpUrl to be set and updated
     * @return the current idp url
     */
    public KeycloakClient idpUrl(String idpUrl) {
        this.idpUrl = idpUrl;
        return this;
    }

    /**
     * Keycloak token path setter with getter
     * @param tokenPath to be set and/or updated
     * @return the current token path
     */
    public KeycloakClient tokenPath(String tokenPath) {
        this.tokenPath = tokenPath;
        return this;
    }

    /**
     * Keycloak client id setter with getter
     * @param clientId to be set and/or updated
     * @return the current client id
     */
    public KeycloakClient clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    /**
     * Keycloak client secret setter with getter
     * @param clientSecret to be set and/or updated
     * @return the current client username
     */
    public KeycloakClient clientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    /**
     * Keycloak user path setter with getter
     * @param userPath to be set and/or updated
     * @return the current client user path
     */
    public KeycloakClient userPath(String userPath) {
        this.userPath = userPath;
        return this;
    }

    /**
     * Keycloak radien client id setter with getter
     * @param radienClientId to be set and/or updated
     * @return the current client radien client id
     */
    public KeycloakClient radienClientId(String radienClientId) {
        this.radienClientId = radienClientId;
        return this;
    }

    /**
     * Keycloak radien secret setter with getter
     * @param radienSecret to be set and/or updated
     * @return the current client radien secret
     */
    public KeycloakClient radienSecret(String radienSecret) {
        this.radienSecret = radienSecret;
        return this;
    }

    /**
     * Keycloak radien token path setter with getter
     * @param radienTokenPath to be set and/or updated
     * @return the current client radien token path
     */
    public KeycloakClient radienTokenPath(String radienTokenPath) {
        this.radienTokenPath = radienTokenPath;
        return this;
    }

    /**
     * Keycloak access token getter
     * @return the current active and request access token
     */
    public String getAccessToken() {
        return result.get("access_token");
    }

    /**
     * Keycloak refresh token getter
     * @return the current active and request refresh token
     */
    public String getRefreshToken() {
        return result.get(REFRESH_TOKEN);
    }

    /**
     * Keycloak login process and field filler
     * @return http response hash map with all the login fields
     * @throws RemoteResourceException exceptions that may occur during the execution of a remote method call.
     */
    public Map<String, String> login() throws RemoteResourceException {
        HttpResponse<?> response = Unirest.post(idpUrl + tokenPath)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
                .field(CLIENT_ID, clientId)
                //.field("redirect_uri", "https://localhost:8443/web/login")
                .field(GRANT_TYPE, "client_credentials")
                .field(CLIENT_SECRET, clientSecret)
                .asObject(HashMap.class);
        if (response.isSuccess()) {
            result = (HashMap<String, String>) response.getBody();
            return result;
        } else {
            //TODO: improve Error handling
            throw new RemoteResourceException("Error on login");
        }
    }

    /**
     * Validate credentials against KeyCloak
     * @param username user logon
     * @param password user pass
     * @return true if the combination matches, otherwise return false
     */
    public boolean validateCredentials(String username, String password) {
        String url = this.idpUrl + this.radienTokenPath;
        String clientIdValue = this.radienClientId;
        String clientSecretValue = this.radienSecret;
        HttpResponse<?> response = Unirest.post(url)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .field(CLIENT_ID, clientIdValue)
                .field(CLIENT_SECRET,clientSecretValue)
                .field(GRANT_TYPE, PASSWORD)
                .field(USERNAME, username)
                .field(PASSWORD, password)
                .asObject(HashMap.class);
        if (response.isSuccess()) {
            return true;
        }

        Optional<UnirestParsingException> parsingError = response.getParsingError();
        parsingError.ifPresent(e -> log.error("Error Message: {}", e.getMessage()));
        if (response.getBody() != null) {
            log.error("Error body {}", response.getBody());
        }
        return false;
    }

    /**
     * Change user password on keycloak
     * @param subject User identifier on KeyCloak
     * @param newPassword New password value
     * @throws RemoteResourceException in case of any issue regarding KeyCloak communication
     */
    public void changePassword(String subject, String newPassword) throws RemoteResourceException {
        String url = this.idpUrl + this.userPath + subject + "/reset-password";
        String body = String.format(JSON_BODY_AS_STRING, newPassword);

        HttpResponse<String> response = Unirest.put(url)
                .header(HttpHeaders.AUTHORIZATION, getAuthorization())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(body)
                .asObject(String.class);

        if (!response.isSuccess()) {
            throw new RemoteResourceException(GenericErrorCodeMessage.ERROR_CHANGE_PASSWORD.
                    toString(response.getStatusText(), response.getBody()));
        }
    }

    /**
     * Keycloak user creation process
     * @param userRepresentation to be created
     * @return the created user subject
     * @throws RemoteResourceException exceptions that may occur during the execution of a remote method call.
     */
    public String createUser(UserRepresentation userRepresentation) throws RemoteResourceException {
        HttpResponse<String> response = Unirest.post(idpUrl + userPath)
                .header(HttpHeaders.AUTHORIZATION, getAuthorization())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(userRepresentation)
                .asObject(String.class);


        if (response.isSuccess()) {
            Headers headers = response.getHeaders();
            if (headers != null && !headers.get(HttpHeaders.LOCATION).isEmpty() && headers.get(HttpHeaders.LOCATION).get(0) != null) {
                List<String> locations = headers.get(HttpHeaders.LOCATION);
                    String location = locations.get(0);
                        int lastIndexOf = location.lastIndexOf("/");
                        if (lastIndexOf > 0 && lastIndexOf + 1 <= location.length()) {
                            return location.substring(lastIndexOf + 1);
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

    /**
     * Method to send a new password via email to the requested user that will be found via his subject
     * @param sub of the user to be found
     * @throws RemoteResourceException exceptions that may occur during the execution of a remote method call.
     */
    public void sendUpdatePasswordEmail(String sub) throws RemoteResourceException {
        HttpResponse<String> response = Unirest.put(idpUrl + userPath + "/" + sub + "/execute-actions-email")
                .header(HttpHeaders.AUTHORIZATION, getAuthorization())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body("[\"" + KeycloakEmailActions.UPDATE_PASSWORD + "\"]")
                .asObject(String.class);
        if (!response.isSuccess()) {
            log.error("status {},body {}",response.getStatus(),response.getBody());
            throw new RemoteResourceException(GenericErrorCodeMessage.ERROR_SEND_UPDATE_PASSWORD_EMAIL.toString());
        }
    }

    /**
     * Keycloak method to delete requested user
     * @param sub subject of the user to be deleted
     * @throws RemoteResourceException exceptions that may occur during the execution of a remote method call.
     */
    public void deleteUser(String sub) throws RemoteResourceException {
        HttpResponse<HashMap> response = Unirest.delete(idpUrl + userPath + "/" + sub)
                .header(HttpHeaders.AUTHORIZATION, getAuthorization())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .asObject(HashMap.class);
        if (!response.isSuccess()) {

            if(response.getStatus()==404 && "User not found".equalsIgnoreCase((String)response.getBody().get("error")) ){
                log.error("User not found on keycloak with subject id. Delete continues");
            }else {
                log.error(response.getBody().toString());
                throw new RemoteResourceException("Unable to delete User");
            }
        }
    }

    /**
     * Keycloak refresh active access token
     * @throws RemoteResourceException exceptions that may occur during the execution of a remote method call.
     */
    public void refreshToken() throws RemoteResourceException {
        HttpResponse<?> response = Unirest.post(idpUrl + tokenPath)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
                .field(CLIENT_ID, clientId)
                .field(GRANT_TYPE, REFRESH_TOKEN)
                .field(REFRESH_TOKEN, getRefreshToken())
                .asObject(HashMap.class);
        if (response.isSuccess()) {
            result = (HashMap<String, String>) response.getBody();
        } else {
            //TODO: improve Error handling
            throw new RemoteResourceException("Unable to refresh token");
        }
    }

    /**
     * Keycloak refresh active access token
     * @param refreshToken to be updated and refreshed
     * @return new access token
     * @throws RemoteResourceException exceptions that may occur during the execution of a remote method call.
     */
    public String refreshToken(String refreshToken) throws RemoteResourceException {
        HttpResponse<?> response = Unirest.post(idpUrl + radienTokenPath)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
                .field(CLIENT_ID, radienClientId)
                .field(CLIENT_SECRET, radienSecret)
                .field(GRANT_TYPE, REFRESH_TOKEN)
                .field(REFRESH_TOKEN, refreshToken)
                .asObject(HashMap.class);
        if (response.isSuccess()) {
            result = (HashMap<String, String>) response.getBody();
            return result.get("access_token");
        } else {
            //TODO: improve Error handling
            //  error_description=Token is not active, error=invalid_grant
            if(response.getBody()!= null) {
                String msg = response.getBody().toString();
                log.error(msg);
                log.error(refreshToken);
            }
            throw new RemoteResourceException("Unable to refresh token");
        }
    }

    /**
     * Keycloak update specific and requested user with the given user representation
     * @param sub of the user information to be updated
     * @param userRepresentation information to be added or updated
     * @throws RemoteResourceException exceptions that may occur during the execution of a remote method call.
     */
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

    /**
     * Keycloak updates specific and requested user email and emailVerified object
     * @param sub of the user information to be updated
     * @param userRepresentation information to be added or updated
     * @throws RemoteResourceException exceptions that may occur during the execution of a remote method call.
     */
    public void updateEmailAndExecuteActionEmailVerify(String sub, UserRepresentation userRepresentation) throws RemoteResourceException {
        HttpResponse<String> response = Unirest.put(idpUrl + userPath + "/" + sub)
                .header(HttpHeaders.AUTHORIZATION, getAuthorization())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body(userRepresentation)
                .asString();
        if(!response.isSuccess()){
            throw new RemoteResourceException(GenericErrorCodeMessage.ERROR_SEND_UPDATE_EMAIL_VERIFY.toString());
        }
    }

    /**
     * Method to send a new updated email for the verification of the requested user that will be found via his subject
     * @param sub of the user to be found
     * @throws RemoteResourceException exceptions that may occur during the execution of a remote method call.
     */
    public void sendUpdatedEmailToVerify(String sub) throws RemoteResourceException {
        HttpResponse<String> response = Unirest.put(idpUrl + userPath + "/" + sub + "/execute-actions-email")
                .header(HttpHeaders.AUTHORIZATION, getAuthorization())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body("[\"" + KeycloakEmailActions.VERIFY_EMAIL+ "\"]")
                .asObject(String.class);
        if (!response.isSuccess()) {
            log.error( "status {},body {}", response.getStatus(), response.getBody() );
            throw new RemoteResourceException(GenericErrorCodeMessage.ERROR_SEND_EXECUTE_ACTION_EMAIL_VERIFY.toString());
        }
    }

    /**
     * Keycloak method to get user
     * @param email of the user sub we want to retrieve
     * @throws RemoteResourceException exceptions that may occur during the execution of a remote method call.
     */
    public Optional<String> getSubFromEmail(String email) throws RemoteResourceException {
        HttpResponse<ArrayList> response = Unirest.get(idpUrl + userPath + "?email=" + email)
                .header(HttpHeaders.AUTHORIZATION, getAuthorization())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .asObject(ArrayList.class);
        if (response.isSuccess()) {
            List<Map<String, Object>> results = (List<Map<String, Object>>) response.getBody();
            if(results.isEmpty()){
                return Optional.empty();
            }
            if(results.size()>1){
                log.error("Invalid number of results for Sub");
                return Optional.empty();
            }
            return Optional.ofNullable((String)results.get(0).get("id"));
        }else {
            if(response.getBody()!= null) {
                String msg = response.getBody().toString();
                log.error(msg);
            }
            throw new RemoteResourceException("Unable to get Sub From Email");
        }
    }

    /**
     * Method to get the access token/authorization token
     * @return the authorization token in use
     */
    private String getAuthorization() {
        return "Bearer " + getAccessToken();
    }
}
