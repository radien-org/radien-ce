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

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.json.JsonObjectBuilder;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonWriter;
import javax.json.JsonObject;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
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

    @Test(expected = SystemException.class)
    public void testLinkedAuthorizationByRoleIdException() throws Exception {
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenThrow(new ExtensionException(new Exception()));
        target.getLinkedAuthorizationByRoleId(2L);
    }

    private String getLinkedAuthorizationManagementUrl(){
        String url = "";
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT)).thenReturn(url);
        return url;
    }

    @Test
    public void create() throws MalformedURLException, SystemException {
        LinkedAuthorizationResourceClient linkedAuthorizationResourceClient = Mockito.mock(LinkedAuthorizationResourceClient.class);
        when(linkedAuthorizationResourceClient.saveAssociation(any())).thenReturn(Response.ok().build());
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenReturn(linkedAuthorizationResourceClient);
        assertTrue(target.create(new LinkedAuthorization()));
    }

    @Test(expected = SystemException.class)
    public void testCreateMalformedException() throws MalformedURLException, SystemException {
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenThrow(new MalformedURLException());
        target.create(new LinkedAuthorization());
    }

    @Test
    public void testCreateFail() throws MalformedURLException, SystemException {
        LinkedAuthorizationResourceClient linkedAuthorization = Mockito.mock(LinkedAuthorizationResourceClient.class);
        when(linkedAuthorization.saveAssociation(any())).thenReturn(Response.serverError().entity("test error msg").build());
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenReturn(linkedAuthorization);
        assertFalse(target.create(new LinkedAuthorization()));
    }

    @Test(expected = SystemException.class)
    public void testCreateProcessingException() throws SystemException, MalformedURLException {
        LinkedAuthorizationResourceClient linkedAuthorizationResourceClient = Mockito.mock(LinkedAuthorizationResourceClient.class);
        when(linkedAuthorizationResourceClient.saveAssociation(any())).thenThrow(new ProcessingException(""));
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenReturn(linkedAuthorizationResourceClient);
        target.create(new LinkedAuthorization());
    }

    /**
     * Test scenario to delete linked authorization by passing
     * SystemLinked Authorization object Id
     * @throws MalformedURLException if incorrect url
     * @throws SystemException if any error
     */
    @Test
    public void delete() throws MalformedURLException, SystemException {
        Long linkedAuthorizationId = 1L;
        LinkedAuthorizationResourceClient linkedAuthorizationResourceClient = Mockito.mock(LinkedAuthorizationResourceClient.class);
        when(linkedAuthorizationResourceClient.deleteAssociation(anyLong())).thenReturn(Response.ok().build());
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenReturn(linkedAuthorizationResourceClient);
        assertTrue(target.delete(linkedAuthorizationId));
    }

    /**
     * Test scenario to get systemException error while deletion of linked authorization
     * if method throw MalformedURLException
     * @throws MalformedURLException if incorrect url
     * @throws SystemException if any error
     */
    @Test(expected = SystemException.class)
    public void testDeleteMalformedException() throws MalformedURLException, SystemException {
        Long linkedAuthorizationId = 2L;
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenThrow(new MalformedURLException());
        target.delete(linkedAuthorizationId);
    }

    /**
     * Test scenario to get server exception while deletion of linked authorization
     * @throws MalformedURLException if incorrect url
     * @throws SystemException if any error
     */
    @Test
    public void testDeleteFail() throws MalformedURLException, SystemException {
        Long linkedAuthorizationId = 3L;
        LinkedAuthorizationResourceClient linkedAuthorization = Mockito.mock(LinkedAuthorizationResourceClient.class);
        when(linkedAuthorization.deleteAssociation(anyLong())).thenReturn(Response.serverError().entity("test error msg").build());
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenReturn(linkedAuthorization);
        assertFalse(target.delete(linkedAuthorizationId));
    }

    /**
     * Test scenario to get systemException error while deletion of linked authorization
     * if method throw ProcessingException
     * @throws SystemException if any error
     * @throws MalformedURLException if incorrect url
     */
    @Test(expected = SystemException.class)
    public void testDeleteProcessingException() throws SystemException, MalformedURLException {
        Long linkedAuthorizationId = 4L;
        LinkedAuthorizationResourceClient linkedAuthorizationResourceClient = Mockito.mock(LinkedAuthorizationResourceClient.class);
        when(linkedAuthorizationResourceClient.deleteAssociation(anyLong())).thenThrow(new ProcessingException(""));
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenReturn(linkedAuthorizationResourceClient);
        target.delete(linkedAuthorizationId);
    }

    @Test(expected = SystemException.class)
    public void testDeleteTokenExpiration() throws Exception {
        Long linkedAuthorizationId = 5L;
        LinkedAuthorizationResourceClient linkedAuthorizationResourceClient = Mockito.mock(LinkedAuthorizationResourceClient.class);

        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenReturn(linkedAuthorizationResourceClient);
        when(linkedAuthorizationResourceClient.deleteAssociation(anyLong())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());
        target.delete(linkedAuthorizationId);
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

    @Test(expected = WebApplicationException.class)
    public void testIsLinkedAuthorizationExistentWebApplicationException() throws Exception {
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenThrow(new WebApplicationException());
        target.checkIfLinkedAuthorizationExists(2L, 2L, 2L, 2L);
    }

    @Test(expected = SystemException.class)
    public void testIsLinkedAuthorizationExistentException() throws Exception {
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenThrow(new ProcessingException("teste"));
        target.checkIfLinkedAuthorizationExists(2L, 2L, 2L, 2L);
    }

    @Test(expected = SystemException.class)
    public void testGetAll() throws MalformedURLException, SystemException {
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

        target.getAll(1, 100);
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

    @Test(expected = SystemException.class)
    public void isRoleExistentForUserException() throws MalformedURLException, SystemException {
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenThrow(new MalformedURLException());
        target.isRoleExistentForUser(2L, 2L, "role");
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

    @Test(expected = SystemException.class)
    public void testSpecificAssociationByUserIdException() throws Exception {
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenThrow(new ExtensionException(new Exception()));
        target.getSpecificAssociationByUserId(2L);
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
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void dissociateTenantUser() throws MalformedURLException, SystemException {
        LinkedAuthorizationResourceClient linkedAuthorizationResourceClient = Mockito.mock(LinkedAuthorizationResourceClient.class);
        when(linkedAuthorizationResourceClient.deleteAssociations(1L, 1L)).thenReturn(Response.ok().build());
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(
                getLinkedAuthorizationManagementUrl())).thenReturn(linkedAuthorizationResourceClient);
        assertTrue(target.deleteAssociations(1L, 1L));
    }

    /**
     * Test for method dissociateTenantUser(Long tenant, Long user)
     * Expected outcome: Fail. Communication fail due MalformedURLException
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testDissociateTenantUserMalformedException() throws SystemException, MalformedURLException {
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(
                getLinkedAuthorizationManagementUrl())).thenThrow(new MalformedURLException());
        target.deleteAssociations(1L, 1L);
    }

    /**
     * Test for method dissociateTenantUser(Long tenant, Long user)
     * Expected outcome: Fail due response server error
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testDissociateTenantUserFail() throws MalformedURLException, SystemException {
        LinkedAuthorizationResourceClient linkedAuthorization = Mockito.mock(LinkedAuthorizationResourceClient.class);
        when(linkedAuthorization.deleteAssociations(1L, 1L)).thenReturn(
                Response.serverError().entity("test error msg").build());
        when(linkedAuthorizationServiceUtil.
                getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).
                thenReturn(linkedAuthorization);
        assertFalse(target.deleteAssociations(1L, 1L));
    }

    /**
     * Test for method dissociateTenantUser(Long tenant, Long user)
     * Expected outcome: Fail due Processing exception
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testDissociateTenantUserProcessingException() throws SystemException, MalformedURLException {
        LinkedAuthorizationResourceClient linkedAuthorizationResourceClient = Mockito.mock(LinkedAuthorizationResourceClient.class);
        when(linkedAuthorizationResourceClient.deleteAssociations(1L, 1L)).
                thenThrow(new ProcessingException(""));
        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(
                getLinkedAuthorizationManagementUrl())).thenReturn(linkedAuthorizationResourceClient);
        target.deleteAssociations(1L, 1L);
    }

    /**
     * Test for method dissociateTenantUser(Long tenant, Long user)
     * Expected outcome: Fail due JWT expiration
     */
    @Test(expected = SystemException.class)
    public void testDissociateTenantUserTokenExpiredException() throws SystemException, MalformedURLException {
        LinkedAuthorizationResourceClient linkedAuthorizationResourceClient = Mockito.mock(LinkedAuthorizationResourceClient.class);

        when(linkedAuthorizationServiceUtil.getLinkedAuthorizationResourceClient(getLinkedAuthorizationManagementUrl())).thenReturn(linkedAuthorizationResourceClient);
        when(linkedAuthorizationResourceClient.deleteAssociations(anyLong(), anyLong())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.deleteAssociations(1L, 2L);
    }
}