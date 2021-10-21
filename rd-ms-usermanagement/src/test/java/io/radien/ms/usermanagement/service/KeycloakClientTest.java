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
package io.radien.ms.usermanagement.service;

import io.radien.ms.usermanagement.client.exceptions.RemoteResourceException;
import io.radien.ms.usermanagement.config.KeycloakEmailActions;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import kong.unirest.GetRequest;
import kong.unirest.Headers;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import kong.unirest.RequestBodyEntity;
import kong.unirest.Unirest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.keycloak.representations.idm.UserRepresentation;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Class that aggregates UnitTest cases for KeycloakClient
 * @author Rajesh Gavvala
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.net.ssl.*" })
@PrepareForTest({ Unirest.class})
public class KeycloakClientTest {
    @InjectMocks
    private KeycloakClient keycloakClient;

    @Mock
    private GetRequest getRequest;

    @Mock
    HttpRequestWithBody httpRequestWithBody;
    @Mock
    RequestBodyEntity requestBodyEntity;


    String inputBody_EmailVerify = "[\"" + KeycloakEmailActions.VERIFY_EMAIL + "\"]";
    String inputBody_UpdatePassword = "[\"" + KeycloakEmailActions.UPDATE_PASSWORD + "\"]";

    /**
     * Prepares required mock objects
     */
    @Before
    public void setUp() throws IllegalAccessException {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(Unirest.class);

        httpRequestWithBody = PowerMockito.mock(HttpRequestWithBody.class);
        requestBodyEntity = PowerMockito.mock(RequestBodyEntity.class);

        keycloakClient = new KeycloakClient()
                .clientId("adminClientId")
                .clientSecret("secret")
                .idpUrl("url")
                .tokenPath("tokenPath")
                .radienClientId("clientId")
                .radienSecret("clientSecret")
                .userPath("userPath");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("access_token", "TOKEN");
        hashMap.put("refresh_token", "REFRESH TOKEN");
        hashMap.put("grant_type", "REFRESH TOKEN");

        Field field = PowerMockito.field(KeycloakClient.class, "result");
        field.set(keycloakClient, hashMap);
    }

    /**
     * Test method {@link KeycloakClient#radienSecret(String)}
     */
    @Test
    public void testRadienSecret(){
        assertNotNull(keycloakClient.radienSecret("RADIEN SECRET"));
    }

    /**
     * Test method {@link KeycloakClient#radienTokenPath(String)}
     */
    @Test
    public void testRadienTokenPath(){
        assertNotNull(keycloakClient.radienTokenPath("RADIEN TOKEN PATH"));
    }

    /**
     * Test method {@link KeycloakClient#getRefreshToken()}
     */
    @Test
    public void testGetRefreshToken(){
        assertNotNull(keycloakClient.getRefreshToken());
    }

    /**
     * Test method {@link KeycloakClient#createUser(UserRepresentation)}
     * @throws RemoteResourceException if Unable to create User in keycloak
     */
    @Test
    public void testCreateUser() throws RemoteResourceException {
        UserRepresentation userRepresentation = mock(UserRepresentation.class);
        HttpResponse<String> httpResponse = PowerMockito.mock(HttpResponse.class);
        Headers headers = new Headers();
        headers.add(HttpHeaders.LOCATION, "Location/ ");
        when(httpResponse.getHeaders()).thenReturn(headers);

        prepareMockObjectsForCreateUser(httpResponse, userRepresentation, true);
        assertNotNull(keycloakClient.createUser(userRepresentation));
    }


    /**
     * Test method {@link KeycloakClient#createUser(UserRepresentation)}
     * @throws RemoteResourceException if Unable to create User in keycloak
     */
    @Test(expected = RemoteResourceException.class)
    public void testCreateUserException() throws RemoteResourceException {
        UserRepresentation userRepresentation = mock(UserRepresentation.class);
        HttpResponse<String> httpResponse = PowerMockito.mock(HttpResponse.class);
        Headers headers = new Headers();
        when(httpResponse.getHeaders()).thenReturn(headers);

        prepareMockObjectsForCreateUser(httpResponse, userRepresentation, true);
        keycloakClient.createUser(userRepresentation);
    }

    /**
     * Test method {@link KeycloakClient#createUser(UserRepresentation)}
     * @throws RemoteResourceException if Unable to create User in keycloak
     */
    @Test(expected = RemoteResourceException.class)
    public void testCreateUserExceptionAndHeaderIsEmpty() throws RemoteResourceException {
        UserRepresentation userRepresentation = mock(UserRepresentation.class);
        HttpResponse<String> httpResponse = PowerMockito.mock(HttpResponse.class);
        Headers headers = new Headers();
        headers.add("hello", "hello");
        headers.add(HttpHeaders.LOCATION, "");
        when(httpResponse.getHeaders()).thenReturn(headers);

        prepareMockObjectsForCreateUser(httpResponse, userRepresentation, true);
        keycloakClient.createUser(userRepresentation);
    }

    /**
     * Test method {@link KeycloakClient#createUser(UserRepresentation)}
     * @throws RemoteResourceException if Unable to create User in keycloak
     */
    @Test(expected = RemoteResourceException.class)
    public void testCreateUserExceptionConflict() throws RemoteResourceException {
        UserRepresentation userRepresentation = mock(UserRepresentation.class);
        HttpResponse<String> httpResponse = PowerMockito.mock(HttpResponse.class);
        when(httpResponse.getStatus()).thenReturn(409);
        prepareMockObjectsForCreateUser(httpResponse, userRepresentation, false);

        keycloakClient.createUser(userRepresentation);
    }

    /**
     * Test method {@link KeycloakClient#createUser(UserRepresentation)}
     * @throws RemoteResourceException if Unable to create User in keycloak
     */
    @Test(expected = RemoteResourceException.class)
    public void testCreateUserExceptionConflictDoesNotMatch() throws RemoteResourceException {
        UserRepresentation userRepresentation = mock(UserRepresentation.class);
        HttpResponse<String> httpResponse = PowerMockito.mock(HttpResponse.class);
        when(httpResponse.getStatus()).thenReturn(410);
        prepareMockObjectsForCreateUser(httpResponse, userRepresentation, false);

        keycloakClient.createUser(userRepresentation);
    }


    /**
     * Test method {@link KeycloakClient#deleteUser(String)}
     * @throws RemoteResourceException if resource not able to delete
     */
    @Test(expected = Test.None.class)
    public void testDeleteUser() throws RemoteResourceException {
        HttpResponse<HashMap> httpResponse = PowerMockito.mock(HttpResponse.class);
        prepareMockObjectForDeleteUser(httpResponse, true);
        keycloakClient.deleteUser("userSub");
    }

    /**
     * Test method {@link KeycloakClient#deleteUser(String)}
     * @throws RemoteResourceException if resource not able to delete
     */
    @Test(expected = Test.None.class)
    public void testDeleteUser404Status() throws RemoteResourceException {
        HttpResponse<HashMap> httpResponse = PowerMockito.mock(HttpResponse.class);

        prepareMockObjectForDeleteUser(httpResponse, false);
        Map<String, String> map = new HashMap<>();
        map.put("error", "User not found");
        when(httpResponse.getStatus()).thenReturn(404);
        when(httpResponse.getBody()).thenReturn( (HashMap) map );
        keycloakClient.deleteUser("delete user");
    }

    /**
     * Test method {@link KeycloakClient#deleteUser(String)}
     * @throws RemoteResourceException if resource not able to delete
     */
    @Test(expected = RemoteResourceException.class)
    public void testDeleteUserException() throws RemoteResourceException {
        HttpResponse<HashMap> httpResponse = PowerMockito.mock(HttpResponse.class);

        prepareMockObjectForDeleteUser(httpResponse, false);
        when(httpResponse.getStatus()).thenReturn(404);
        Map<String, String> map = new HashMap<>();
        map.put("delete", "delete");
        when(httpResponse.getBody()).thenReturn( (HashMap) map );
        keycloakClient.deleteUser("delete user");
    }

    /**
     * Test for method {@link KeycloakClient#sendUpdatePasswordEmail(String)}
     * @throws Exception if any error occurs
     */
    @Test
    public void testSendUpdatePasswordEmail() throws Exception {
        prepareMockObjectsForUpdatePasswordAndEmailVerify(true, inputBody_UpdatePassword);
        keycloakClient.sendUpdatePasswordEmail("test");
        assertEquals("TOKEN", keycloakClient.getAccessToken());
    }

    /**
     * Test for method {@link KeycloakClient#sendUpdatePasswordEmail(String)}
     * Asserts an expected exception
     * @throws Exception if any error occurs
     */
    @Test(expected = RemoteResourceException.class)
    public void testSendUpdatePasswordEmailException() throws Exception {
        prepareMockObjectsForUpdatePasswordAndEmailVerify(false, inputBody_UpdatePassword);
        keycloakClient.sendUpdatePasswordEmail("test");
    }

    /**
     * Test for method {@link KeycloakClient#sendUpdatedEmailToVerify(String)}
     * @throws Exception if any error occurs
     */
    @Test
    public void testSendUpdatedEmailVerify() throws Exception {
        prepareMockObjectsForUpdatePasswordAndEmailVerify(true, inputBody_EmailVerify);
        keycloakClient.sendUpdatedEmailToVerify("test");
        assertEquals("TOKEN", keycloakClient.getAccessToken());
    }

    /**
     * Test for method {@link KeycloakClient#sendUpdatedEmailToVerify(String)}
     * Asserts an expected exception
     * @throws Exception if any error occurs
     */
    @Test(expected = RemoteResourceException.class)
    public void testSendUpdatedEmailVerifyException() throws Exception {
        prepareMockObjectsForUpdatePasswordAndEmailVerify(false, inputBody_EmailVerify);
        keycloakClient.sendUpdatedEmailToVerify("test");
    }

    /**
     * Test for method {@link KeycloakClient#updateUser(String, UserRepresentation)}
     */
    @Test
    public void testUpdateUser() throws Exception {
        UserRepresentation userRepresentation = mock(UserRepresentation.class);
        prepareMockObjectsForUpdateUser(userRepresentation, true);
        when(Unirest.put(anyString())).thenReturn(httpRequestWithBody);
        keycloakClient.updateUser("test", userRepresentation);
        assertEquals("TOKEN", keycloakClient.getAccessToken());
    }

    /**
     * Test for method {@link KeycloakClient#updateUser(String, UserRepresentation)}
     */
    @Test(expected = RemoteResourceException.class)
    public void testUpdateUserException() throws Exception {
        UserRepresentation userRepresentation = mock(UserRepresentation.class);

        prepareMockObjectsForUpdateUser(userRepresentation, false);
        when(Unirest.put(anyString())).thenReturn(httpRequestWithBody);
        keycloakClient.updateUser("test", userRepresentation);
    }

    /**
     * Test for method {@link KeycloakClient#updateEmailAndExecuteActionEmailVerify(String, UserRepresentation)}
     * @throws Exception if any error occurs
     */
    @Test
    public void testUpdateKeyCloakEmailVerifiedAttribute() throws Exception {
        UserRepresentation userRepresentation = mock(UserRepresentation.class);

        prepareMockObjectsForUpdateEmailAndExecuteActionEmailVerify(userRepresentation, true);

        keycloakClient.updateEmailAndExecuteActionEmailVerify("test", userRepresentation);
        assertEquals("TOKEN", keycloakClient.getAccessToken());
    }

    /**
     * Test for method {@link KeycloakClient#updateEmailAndExecuteActionEmailVerify(String, UserRepresentation)}
     * @throws Exception if any error occurs
     */
    @Test(expected = Exception.class)
    public void testUpdateKeyCloakEmailVerifiedAttributeException() throws Exception {
        UserRepresentation userRepresentation = mock(UserRepresentation.class);

        prepareMockObjectsForUpdateEmailAndExecuteActionEmailVerify(userRepresentation, false);

        keycloakClient.updateEmailAndExecuteActionEmailVerify("test", userRepresentation);
    }

    /**
     * Test method {@link KeycloakClient#getSubFromEmail(String)}
     * Asserts response fails and response body not null
     * @throws RemoteResourceException if any exception to a remote method call
     */
    @Test(expected = RemoteResourceException.class)
    public void testGetSubFromEmailResponseSuccessFailGetBodyNull() throws RemoteResourceException {
        HttpResponse<ArrayList> httpResponse = PowerMockito.mock(HttpResponse.class);
        prepareMockObjectForGetSubFromEmail(httpResponse, false);
        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<>();
        map.put("userEmail", "email@email.com");
        mapList.add(map);
        when(httpResponse.getBody()).thenReturn( (ArrayList) mapList );

        keycloakClient.getSubFromEmail("email@email.com");
    }

    /**
     * Test method {@link KeycloakClient#getSubFromEmail(String)}
     * Asserts response as success
     * @throws RemoteResourceException if any exception to a remote method call
     */
    @Test(expected = Test.None.class)
    public void testGetSubFromEmailResponseSuccess() throws RemoteResourceException {
        HttpResponse<ArrayList> httpResponse = PowerMockito.mock(HttpResponse.class);
        prepareMockObjectForGetSubFromEmail(httpResponse, true);
        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<>();
        map.put("userEmail", "email@email.com");
        mapList.add(map);
        when(httpResponse.getBody()).thenReturn( (ArrayList) mapList );

        keycloakClient.getSubFromEmail("email@email.com");
    }

    /**
     * Test method {@link KeycloakClient#getSubFromEmail(String)}
     * Asserts response and its response body results more than one
     * @throws RemoteResourceException if any exception to a remote method call
     */
    @Test(expected = Test.None.class)
    public void testGetSubFromEmailResponseSuccessResultsSizeMoreThanOne() throws RemoteResourceException {
        HttpResponse<ArrayList> httpResponse = PowerMockito.mock(HttpResponse.class);
        prepareMockObjectForGetSubFromEmail(httpResponse, true);
        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<>();
        map.put("userEmail", "email@email.com");
        Map<String, Object> map1 = new HashMap<>();
        map.put("userEmail", "email@email.com");
        mapList.add(map);
        mapList.add(map1);
        when(httpResponse.getBody()).thenReturn( (ArrayList) mapList );

        keycloakClient.getSubFromEmail("email");
    }

    /**
     * Test method {@link KeycloakClient#getSubFromEmail(String)}
     * @throws RemoteResourceException if any exception to a remote method call
     */
    @Test(expected = Test.None.class)
    public void testGetSubFromEmailResponseSuccessResultsEmpty() throws RemoteResourceException {
        HttpResponse<ArrayList> httpResponse = PowerMockito.mock(HttpResponse.class);
        prepareMockObjectForGetSubFromEmail(httpResponse, true);
        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        when(httpResponse.getBody()).thenReturn( (ArrayList) mapList );

        keycloakClient.getSubFromEmail("email");
    }

    /**
     * Test method {@link KeycloakClient#getSubFromEmail(String)}
     * @throws RemoteResourceException if any exception to a remote method call
     */
    @Test(expected = RemoteResourceException.class)
    public void testGetSubFromEmailResponseFail() throws RemoteResourceException {
        HttpResponse<ArrayList> httpResponse = PowerMockito.mock(HttpResponse.class);
        prepareMockObjectForGetSubFromEmail(httpResponse, true);
        prepareMockObjectForGetSubFromEmail(httpResponse, false);

        keycloakClient.getSubFromEmail( "email@email.com" );
    }


    /**
     * Prepares mock objects for the invoke methods
     * @param response to be set either true/false
     * @param inputBody to be set
     * @throws IllegalAccessException If field object is either inaccessible or final
     */
    private void prepareMockObjectsForUpdatePasswordAndEmailVerify(boolean response, String inputBody) throws IllegalAccessException {
        HttpResponse<String> httpResponse = PowerMockito.mock(HttpResponse.class);

        if(response){
            when(httpResponse.isSuccess()).thenReturn(Boolean.TRUE);
        } else {
            when(httpResponse.isSuccess()).thenReturn(Boolean.FALSE);
        }
        when(httpRequestWithBody.header(HttpHeaders.AUTHORIZATION, "Bearer TOKEN")).thenReturn(httpRequestWithBody);
        when(httpRequestWithBody.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)).thenReturn(httpRequestWithBody);
        when(httpRequestWithBody.body(inputBody)).thenReturn(requestBodyEntity);
        when(requestBodyEntity.asObject(String.class)).thenReturn(httpResponse);
        when(Unirest.put(anyString())).thenReturn(httpRequestWithBody);
    }

    /**
     * Prepares mock objects for the invoke methods
     * @param userRepresentation object information
     */
    private void prepareMockObjectsForCreateUser(HttpResponse<String> httpResponse, UserRepresentation userRepresentation, boolean response) {
        if(response){
            when(httpResponse.isSuccess()).thenReturn(Boolean.TRUE);
        } else {
            when(httpResponse.isSuccess()).thenReturn(Boolean.FALSE);
        }

        when(httpRequestWithBody.header(HttpHeaders.AUTHORIZATION, "Bearer TOKEN")).thenReturn(httpRequestWithBody);
        when(httpRequestWithBody.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)).thenReturn(httpRequestWithBody);
        when(httpRequestWithBody.body(userRepresentation)).thenReturn(requestBodyEntity);
        when(Unirest.post(anyString())).thenReturn( httpRequestWithBody );
        when(requestBodyEntity.asObject(String.class)).thenReturn(httpResponse);

    }

    /**
     * Prepares mock objects for the invoke methods
     * @param userRepresentation object information
     */
    private void prepareMockObjectsForUpdateUser(UserRepresentation userRepresentation, boolean response){
        userRepresentation.setEmailVerified(false);
        HttpResponse<String> httpResponse = PowerMockito.mock(HttpResponse.class);

        if(response){
            when(httpResponse.isSuccess()).thenReturn(Boolean.TRUE);
        } else {
            when(httpResponse.isSuccess()).thenReturn(Boolean.FALSE);
        }

        when(httpRequestWithBody.header(HttpHeaders.AUTHORIZATION, "Bearer TOKEN")).thenReturn(httpRequestWithBody);
        when(httpRequestWithBody.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)).thenReturn(httpRequestWithBody);
        when(httpRequestWithBody.body(userRepresentation)).thenReturn(requestBodyEntity);
        when(requestBodyEntity.asString()).thenReturn(httpResponse);
    }

    /**
     * Prepares mock objects for the invoke methods
     * @param userRepresentation object information
     * @param response requested response as boolean flag
     */
    private void prepareMockObjectsForUpdateEmailAndExecuteActionEmailVerify(UserRepresentation userRepresentation, boolean response){
        HttpResponse<String> httpResponse = PowerMockito.mock(HttpResponse.class);

        if(response){
            when(httpResponse.isSuccess()).thenReturn(Boolean.TRUE);
        } else {
            when(httpResponse.isSuccess()).thenReturn(Boolean.FALSE);
        }

        userRepresentation.setEmail("email@email.com");
        userRepresentation.setEmailVerified(false);

        when(httpRequestWithBody.header(HttpHeaders.AUTHORIZATION, "Bearer TOKEN")).thenReturn(httpRequestWithBody);
        when(httpRequestWithBody.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)).thenReturn(httpRequestWithBody);
        when(httpRequestWithBody.body(userRepresentation)).thenReturn(requestBodyEntity);
        when(requestBodyEntity.asString()).thenReturn(httpResponse);

        when(Unirest.put(anyString())).thenReturn(httpRequestWithBody);
    }

    /**
     * Prepares mock objects for the invoke methods for getSubFromEmail()
     * @param httpResponse mock object
     * @param response boolean flag
     */
    private void prepareMockObjectForGetSubFromEmail(HttpResponse<ArrayList> httpResponse, boolean response) {
        if(response){
            when(httpResponse.isSuccess()).thenReturn(Boolean.TRUE);
        } else {
            when(httpResponse.isSuccess()).thenReturn(Boolean.FALSE);
        }
        when(getRequest.header(HttpHeaders.AUTHORIZATION, "Bearer TOKEN")).thenReturn(getRequest);
        when(getRequest.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)).thenReturn(getRequest);
        when(Unirest.get(anyString())).thenReturn(getRequest);
        when(getRequest.asObject(ArrayList.class)).thenReturn(httpResponse);
    }

    /**
     * Prepares mock objects for the invoke methods for deleteUser()
     * @param httpResponse mock object
     * @param response boolean flag
     */
    private void prepareMockObjectForDeleteUser(HttpResponse<HashMap> httpResponse, boolean response) {

        if(response){
            when(httpResponse.isSuccess()).thenReturn(Boolean.TRUE);
        } else {
            when(httpResponse.isSuccess()).thenReturn(Boolean.FALSE);
        }
        when(httpRequestWithBody.header(HttpHeaders.AUTHORIZATION, "Bearer TOKEN")).thenReturn(httpRequestWithBody);
        when(httpRequestWithBody.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)).thenReturn(httpRequestWithBody);
        when(Unirest.delete(anyString())).thenReturn( httpRequestWithBody );
        when(httpRequestWithBody.asObject(HashMap.class)).thenReturn(httpResponse);
    }
}