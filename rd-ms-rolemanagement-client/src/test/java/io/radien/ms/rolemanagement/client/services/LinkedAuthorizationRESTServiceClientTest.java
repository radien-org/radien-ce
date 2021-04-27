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
package io.radien.ms.rolemanagement.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.model.linked.authorization.SystemLinkedAuthorization;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.client.UserClient;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.rolemanagement.client.entities.LinkedAuthorization;
import io.radien.ms.rolemanagement.client.util.ClientServiceUtil;
import org.apache.cxf.bus.extension.ExtensionException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonWriter;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * @author Bruno Gama
 */
public class LinkedAuthorizationRESTServiceClientTest {

    @InjectMocks
    LinkedAuthorizationRESTServiceClient target;

    @Mock
    ClientServiceUtil linkedAuthorizationServiceUtil;

    @Mock
    OAFAccess oafAccess;

    @Mock
    AuthorizationChecker authorizationChecker;

    @Mock
    UserClient userClient;

    @Mock
    TokensPlaceHolder tokensPlaceHolder;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getLinkedAuthorizationByRoleId() throws MalformedURLException, SystemException {
        Long role = 2L;

        JsonArrayBuilder builder = Json.createArrayBuilder();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        LinkedAuthorizationResourceClient linkedAuthorizationClient = Mockito.mock(LinkedAuthorizationResourceClient.class);

        when(linkedAuthorizationClient.getSpecificAssociation(null, null, role, null,false)).thenReturn(response);

        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenReturn(linkedAuthorizationClient);

        List<? extends SystemLinkedAuthorization> list = new ArrayList<>();

        assertEquals(list,target.getLinkedAuthorizationByRoleId(2L));
    }

    @Test
    public void testLinkedAuthorizationByRoleIdException() throws Exception {
        boolean success = false;
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenThrow(new ExtensionException(new Exception()));
        try {
            target.getLinkedAuthorizationByRoleId(2L);
        }catch (SystemException se){
            success = true;
        }
        assertTrue(success);
    }

    private String getLinkedAuthorizationManagementUrl(){
        String url = "";
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT)).thenReturn(url);
        return url;
    }

    @Test
    public void create() throws MalformedURLException {
        LinkedAuthorizationResourceClient linkedAuthorizationResourceClient = Mockito.mock(LinkedAuthorizationResourceClient.class);
        when(linkedAuthorizationResourceClient.saveAssociation(any())).thenReturn(Response.ok().build());
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenReturn(linkedAuthorizationResourceClient);
        boolean success = false;
        try {
            assertTrue(target.create(new LinkedAuthorization()));
        } catch (SystemException e) {
            success = true;
        }
        assertFalse(success);
    }

    @Test
    public void testCreateMalformedException() throws MalformedURLException {
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenThrow(new MalformedURLException());
        boolean success = false;
        try {
            assertTrue(target.create(new LinkedAuthorization()));
        } catch(SystemException e) {
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void testCreateFail() throws MalformedURLException {
        LinkedAuthorizationResourceClient linkedAuthorization = Mockito.mock(LinkedAuthorizationResourceClient.class);
        when(linkedAuthorization.saveAssociation(any())).thenReturn(Response.serverError().entity("test error msg").build());
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenReturn(linkedAuthorization);
        boolean success = false;
        try {
            assertFalse(target.create(new LinkedAuthorization()));
        } catch (SystemException e) {
            success = true;
        }
        assertFalse(success);

    }

    @Test
    public void testCreateProcessingException() throws MalformedURLException {
        LinkedAuthorizationResourceClient linkedAuthorizationResourceClient = Mockito.mock(LinkedAuthorizationResourceClient.class);
        when(linkedAuthorizationResourceClient.saveAssociation(any())).thenThrow(new ProcessingException(""));
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenReturn(linkedAuthorizationResourceClient);
        boolean success = false;
        try {
            target.create(new LinkedAuthorization());
        }catch (ProcessingException | SystemException es){
            success = true;
        }
        assertTrue(success);

    }

    @Test(expected = SystemException.class)
    public void testGetAllTokenExpiration() throws Exception {
        LinkedAuthorizationResourceClient linkedAuthorizationResourceClient = Mockito.mock(LinkedAuthorizationResourceClient.class);

        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenReturn(linkedAuthorizationResourceClient);
        when(linkedAuthorizationResourceClient.getAllAssociations(anyInt(), anyInt())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getAll(1, 10);
    }

    @Test(expected = SystemException.class)
    public void testGetTotalRecordsCountTokenExpiration() throws Exception {
        LinkedAuthorizationResourceClient linkedAuthorizationResourceClient = Mockito.mock(LinkedAuthorizationResourceClient.class);

        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenReturn(linkedAuthorizationResourceClient);
        when(linkedAuthorizationResourceClient.getTotalRecordsCount()).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getTotalRecordsCount();
    }

    @Test(expected = SystemException.class)
    public void testGetLinkedAuthorizationByRoleIdTokenExpiration() throws Exception {
        LinkedAuthorizationResourceClient linkedAuthorizationResourceClient = Mockito.mock(LinkedAuthorizationResourceClient.class);

        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenReturn(linkedAuthorizationResourceClient);
        when(linkedAuthorizationResourceClient.getSpecificAssociation(any(), any(), anyLong(), any(), anyBoolean())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getLinkedAuthorizationByRoleId(2L);
    }

    @Test(expected = SystemException.class)
    public void testCreateTokenExpiration() throws Exception {
        LinkedAuthorizationResourceClient linkedAuthorizationResourceClient = Mockito.mock(LinkedAuthorizationResourceClient.class);

        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenReturn(linkedAuthorizationResourceClient);
        when(linkedAuthorizationResourceClient.saveAssociation(any())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        SystemLinkedAuthorization linkedAuthorization = new LinkedAuthorization();
        linkedAuthorization.setTenantId(2L);
        linkedAuthorization.setPermissionId(2L);
        linkedAuthorization.setRoleId(2L);
        target.create(linkedAuthorization);
    }

    @Test(expected = SystemException.class)
    public void testIsRoleExistentForUserTokenExpiration() throws Exception {
        LinkedAuthorizationResourceClient linkedAuthorizationResourceClient = Mockito.mock(LinkedAuthorizationResourceClient.class);

        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenReturn(linkedAuthorizationResourceClient);
        when(linkedAuthorizationResourceClient.isRoleExistentForUser(anyLong(), anyString(), anyLong())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.isRoleExistentForUser(2L, 2L, "roleName");
    }

    @Test(expected = SystemException.class)
    public void testIfLinkedAuthorizationsExistsTokenExpiration() throws Exception {
        LinkedAuthorizationResourceClient linkedAuthorizationResourceClient = Mockito.mock(LinkedAuthorizationResourceClient.class);

        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenReturn(linkedAuthorizationResourceClient);
        when(linkedAuthorizationResourceClient.existsSpecificAssociation(anyLong(), anyLong(), anyLong(), anyLong(), eq(true))).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.checkIfLinkedAuthorizationExists(2L,2L,2L,2L);
    }
}