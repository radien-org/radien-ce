/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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

import io.radien.api.KeycloakConfigs;

import io.radien.ms.usermanagement.client.exceptions.RemoteResourceException;
import io.radien.ms.usermanagement.config.KeycloakEmailActions;

import java.lang.reflect.Field;
import java.util.HashMap;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;

import kong.unirest.RequestBodyEntity;
import kong.unirest.Unirest;

import org.keycloak.representations.idm.UserRepresentation;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;

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

    String inputBody_EmailVerify = "[\"" + KeycloakEmailActions.VERIFY_EMAIL + "\"]";
    String inputBody_UpdatePassword = "[\"" + KeycloakEmailActions.UPDATE_PASSWORD + "\"]";

    @Mock
    HttpRequestWithBody httpRequestWithBody;
    @Mock
    RequestBodyEntity requestBodyEntity;

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
                .username("username")
                .password("password")
                .idpUrl("url")
                .tokenPath("tokenPath")
                .radienClientId("clientId")
                .radienSecret("clientSecret")
                .userPath("userPath");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("access_token", "TOKEN");

        Field field = PowerMockito.field(KeycloakClient.class, "result");
        field.set(keycloakClient, hashMap);

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
     * Test for method {@link KeycloakClient#sendUpdatedEmailVerify(String)}
     * @throws Exception if any error occurs
     */
    @Test
    public void testSendUpdatedEmailVerify() throws Exception {
        prepareMockObjectsForUpdatePasswordAndEmailVerify(true, inputBody_EmailVerify);
        keycloakClient.sendUpdatedEmailVerify("test");
        assertEquals("TOKEN", keycloakClient.getAccessToken());
    }

    /**
     * Test for method {@link KeycloakClient#sendUpdatedEmailVerify(String)}
     * Asserts an expected exception
     * @throws Exception if any error occurs
     */
    @Test(expected = RemoteResourceException.class)
    public void testSendUpdatedEmailVerifyException() throws Exception {
        prepareMockObjectsForUpdatePasswordAndEmailVerify(false, inputBody_EmailVerify);
        keycloakClient.sendUpdatedEmailVerify("test");
    }

    @Test(expected = RemoteResourceException.class)
    public void testUpdateUser() throws Exception {
        UserRepresentation userRepresentation = mock(UserRepresentation.class);
        prepareMockObjectsForUpdateUser(userRepresentation);
        when(Unirest.put(anyString())).thenReturn(httpRequestWithBody);
        keycloakClient.updateUser("test", userRepresentation);
    }


    @Test(expected = RemoteResourceException.class)
    public void testUpdateKeyCloakEmailVerifiedAttribute() throws Exception {
        HttpResponse<String> httpResponse = PowerMockito.mock(HttpResponse.class);
        httpResponse.isSuccess();

        when(httpRequestWithBody.header(HttpHeaders.AUTHORIZATION, "Bearer TOKEN")).thenReturn(httpRequestWithBody);
        when(httpRequestWithBody.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)).thenReturn(httpRequestWithBody);
        when(httpRequestWithBody.body("{}")).thenReturn(requestBodyEntity);
        when(requestBodyEntity.asString()).thenReturn(httpResponse);

        when(Unirest.put(anyString())).thenReturn(httpRequestWithBody);
        keycloakClient.updateUserEmailAndEmailVerifiedAttribute("test", "{}");
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

    private void prepareMockObjectsForUpdateUser(UserRepresentation userRepresentation){
        userRepresentation.setEmailVerified(false);
        HttpResponse<String> httpResponse = PowerMockito.mock(HttpResponse.class);
        httpResponse.isSuccess();

        when(httpRequestWithBody.header(HttpHeaders.AUTHORIZATION, "Bearer TOKEN")).thenReturn(httpRequestWithBody);
        when(httpRequestWithBody.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)).thenReturn(httpRequestWithBody);
        when(httpRequestWithBody.body(userRepresentation)).thenReturn(requestBodyEntity);
        when(requestBodyEntity.asString()).thenReturn(httpResponse);
    }

}