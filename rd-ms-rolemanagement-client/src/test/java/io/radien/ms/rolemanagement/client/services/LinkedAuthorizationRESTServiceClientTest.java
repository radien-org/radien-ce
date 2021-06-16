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
import io.radien.api.entity.Page;
import io.radien.api.model.linked.authorization.SystemLinkedAuthorization;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.client.UserClient;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.rolemanagement.client.entities.LinkedAuthorization;
import io.radien.ms.rolemanagement.client.util.ClientServiceUtil;
import io.radien.ms.rolemanagement.client.util.LinkedAuthorizationModelMapper;
import org.apache.cxf.bus.extension.ExtensionException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.json.*;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    public void testIsLinkedAuthorizationExistent() throws MalformedURLException, SystemException {
        LinkedAuthorizationResourceClient linkedAuthorizationClient = Mockito.mock(LinkedAuthorizationResourceClient.class);

        Response response = Response.ok().build();

        when(linkedAuthorizationClient.existsSpecificAssociation(anyLong(), anyLong(), anyLong(), anyLong(), anyBoolean())).thenReturn(response);
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenReturn(linkedAuthorizationClient);

        assertTrue(target.checkIfLinkedAuthorizationExists(2L, 2L, 2L, 2L));
    }

    @Test
    public void testIsLinkedAuthorizationExistentReturnFalse() throws MalformedURLException, SystemException {
        LinkedAuthorizationResourceClient linkedAuthorizationClient = Mockito.mock(LinkedAuthorizationResourceClient.class);

        Response response = Response.notModified().build();

        when(linkedAuthorizationClient.existsSpecificAssociation(anyLong(), anyLong(), anyLong(), anyLong(), anyBoolean())).thenReturn(response);
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenReturn(linkedAuthorizationClient);

        assertFalse(target.checkIfLinkedAuthorizationExists(2L, 2L, 2L, 2L));
    }

    @Test
    public void testIsLinkedAuthorizationExistentWebApplicationException() throws Exception {
        boolean success = false;
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenThrow(new WebApplicationException());
        try {
            target.checkIfLinkedAuthorizationExists(2L, 2L, 2L, 2L);
        }catch (WebApplicationException se){
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void testIsLinkedAuthorizationExistentException() throws Exception {
        boolean success = false;
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenThrow(new ProcessingException("teste"));
        try {
            target.checkIfLinkedAuthorizationExists(2L, 2L, 2L, 2L);
        }catch (Exception se){
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void testGetAll() throws MalformedURLException {
        List<LinkedAuthorization> list = new ArrayList<>();
        list.add(LinkedAuthorizationFactory.create(2L, 2L, 2L, 2L, 2L));
        list.add(LinkedAuthorizationFactory.create(3L, 3L, 3L, 3L, 3L));
        list.add(LinkedAuthorizationFactory.create(4L, 4L, 4L, 4L, 4L));
        Page<SystemLinkedAuthorization> page = new Page<>(list, 1, 3, 1);

        JsonObjectBuilder objectBuilder = Json.createObjectBuilder()
                .add("currentPage", page.getCurrentPage())
                .add("totalResults", page.getTotalResults())
                .add("totalPages", page.getTotalPages())
                .add("results", LinkedAuthorizationModelMapper.map(list));

        JsonObject jsonObject = objectBuilder.build();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonObject.toString().getBytes());
        Response expectedResponse = Response.ok(inputStream).build();

        LinkedAuthorizationResourceClient linkedAuthorizationClient = Mockito.mock(LinkedAuthorizationResourceClient.class);
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).
                then(i -> linkedAuthorizationClient);
        when(linkedAuthorizationClient.getAllAssociations(1, 100)).
                then(i -> expectedResponse);

        assertThrows(Exception.class, () -> target.getAll(1, 100));
    }

    @Test
    public void testGetTotalRecordsCountException() throws MalformedURLException, SystemException {
        LinkedAuthorization linkedAuthorization = LinkedAuthorizationFactory.create(2L, 2L, 2L, 2L, 2L);

        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(LinkedAuthorizationFactory.convertToJsonObject(linkedAuthorization));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).entity(1L).build();

        LinkedAuthorizationResourceClient linkedAuthorizationClient = Mockito.mock(LinkedAuthorizationResourceClient.class);

        when(linkedAuthorizationClient.getTotalRecordsCount()).thenReturn(response);
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenReturn(linkedAuthorizationClient);

        assertThrows(SystemException.class, () -> target.getTotalRecordsCount());
    }

    @Test
    public void isRoleExistentForUser() throws MalformedURLException, SystemException {
        LinkedAuthorizationResourceClient linkedAuthorizationResourceClient = Mockito.mock(LinkedAuthorizationResourceClient.class);
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenReturn(linkedAuthorizationResourceClient);

        when(linkedAuthorizationResourceClient.isRoleExistentForUser(anyLong(), anyString(), anyLong())).thenReturn(Response.ok().entity(true).build());

        boolean value = target.isRoleExistentForUser(2L, 2L, "role");

        assertTrue(value);
    }

    @Test
    public void isRoleExistentForUserException() throws MalformedURLException, SystemException {
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenThrow(new MalformedURLException());

        boolean success=false;
        try {
            target.isRoleExistentForUser(2L, 2L, "role");
        }catch (Exception e) {
            success=true;
        }
        assertTrue(success);
    }

    @Test
    public void getSpecificAssociationByUserId() throws MalformedURLException, SystemException {
        Long user = 2L;

        JsonArrayBuilder builder = Json.createArrayBuilder();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        LinkedAuthorizationResourceClient linkedAuthorizationClient = Mockito.mock(LinkedAuthorizationResourceClient.class);

        when(linkedAuthorizationClient.getSpecificAssociation(null, null, null, user,false)).thenReturn(response);

        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenReturn(linkedAuthorizationClient);

        List<? extends SystemLinkedAuthorization> list = new ArrayList<>();

        assertEquals(list,target.getSpecificAssociationByUserId(2L));
    }

    @Test
    public void testSpecificAssociationByUserIdException() throws Exception {
        boolean success = false;
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenThrow(new ExtensionException(new Exception()));
        try {
            target.getSpecificAssociationByUserId(2L);
        }catch (SystemException se){
            success = true;
        }
        assertTrue(success);
    }

    @Test(expected = SystemException.class)
    public void testSpecificAssociationByUserIdTokenExpiration() throws Exception {
        LinkedAuthorizationResourceClient linkedAuthorizationResourceClient = Mockito.mock(LinkedAuthorizationResourceClient.class);

        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenReturn(linkedAuthorizationResourceClient);
        when(linkedAuthorizationResourceClient.getSpecificAssociation(any(), any(), any(), anyLong(), anyBoolean())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getSpecificAssociationByUserId(2L);
    }

    /**
     * Test for method dissociateTenantUser(Long tenant, Long user)
     * Expected outcome: Success. Communication with endpoint performing well. Receive the expected response
     */
    @Test
    public void dissociateTenantUser()  {
        LinkedAuthorizationResourceClient linkedAuthorizationResourceClient = Mockito.mock(LinkedAuthorizationResourceClient.class);
        when(linkedAuthorizationResourceClient.deleteAssociations(1L, 1l,1l,1L)).thenReturn(Response.ok().build());
        assertDoesNotThrow(() -> when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(
                getLinkedAuthorizationManagementUrl())).thenReturn(linkedAuthorizationResourceClient));
        assertTrue(assertDoesNotThrow(() -> target.deleteAssociations(1L, 1L, 1L,1L)));
    }

    /**
     * Test for method dissociateTenantUser(Long tenant, Long user)
     * Expected outcome: Fail. Communication fail due MalformedURLException
     */
    @Test
    public void testDissociateTenantUserMalformedException() throws MalformedURLException {
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(
                getLinkedAuthorizationManagementUrl())).thenThrow(new MalformedURLException());
        assertThrows(SystemException.class, () -> target.deleteAssociations(1L, 1l,1l,1L));
    }

    /**
     * Test for method dissociateTenantUser(Long tenant, Long user)
     * Expected outcome: Fail due response server error
     */
    @Test
    public void testDissociateTenantUserFail() {
        LinkedAuthorizationResourceClient linkedAuthorization = Mockito.mock(LinkedAuthorizationResourceClient.class);
        when(linkedAuthorization.deleteAssociations(1L, 1L, 1L,1L)).thenReturn(
                Response.serverError().entity("test error msg").build());
        assertDoesNotThrow(() -> when(linkedAuthorizationServiceUtil.
                getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).
                thenReturn(linkedAuthorization));
        assertFalse(assertDoesNotThrow(() -> target.deleteAssociations(1L, 1L, 1L,1L)));
    }

    /**
     * Test for method dissociateTenantUser(Long tenant, Long user)
     * Expected outcome: Fail due Processing exception
     */
    @Test
    public void testDissociateTenantUserProcessingException() {
        LinkedAuthorizationResourceClient linkedAuthorizationResourceClient = Mockito.mock(LinkedAuthorizationResourceClient.class);
        when(linkedAuthorizationResourceClient.deleteAssociations(1L, 1L, 1L,1L)).
                thenThrow(new ProcessingException(""));
        assertDoesNotThrow(() -> when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(
                getLinkedAuthorizationManagementUrl())).thenReturn(linkedAuthorizationResourceClient));
        assertThrows(SystemException.class, () -> target.deleteAssociations(1L, 1L, 1L,1L));
    }
}