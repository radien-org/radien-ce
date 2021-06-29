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
package io.radien.ms.permissionmanagement.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.api.util.FactoryUtilService;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.client.UserClient;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.permissionmanagement.client.entities.Permission;
import io.radien.ms.permissionmanagement.client.util.ClientServiceUtil;

import io.radien.ms.permissionmanagement.client.util.PermissionModelMapper;
import org.apache.cxf.bus.extension.ExtensionException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.json.*;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * Permission REST Service Client requests and responses test
 * {@link io.radien.ms.permissionmanagement.client.services.PermissionRESTServiceClient}
 *
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class PermissionRESTServiceClientTest {

    @InjectMocks
    PermissionRESTServiceClient target;

    @Mock
    ClientServiceUtil clientServiceUtil;

    @Mock
    OAFAccess oafAccess;

    @Mock
    AuthorizationChecker authorizationChecker;

    @Mock
    UserClient userClient;

    @Mock
    TokensPlaceHolder tokensPlaceHolder;

    private static final String testValue = "test";

    /**
     * Method for test preparation
     */
    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test to try to get permissions by a given name
     * @throws Exception in case of permission not found or type of fields are incorrect
     */
    @Test
    public void testGetPermissionByName() throws Exception {
        String a = "a";

        JsonArrayBuilder builder = Json.createArrayBuilder();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);

        when(resourceClient.getPermissions(a,null,null,true,true)).thenReturn(response);

        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);

        assertEquals(Optional.empty(),target.getPermissionByName(a));
    }

    /**
     * Method to retrieve the permission endpoint url
     * @return permission url endpoint
     */
    private String getPermissionManagementUrl(){
        String url = "";
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT)).thenReturn(url);
        return url;
    }

    /**
     * Test to get permissions by name but without any results to be found
     * @throws Exception in case of permission not found or type of fields are incorrect
     */
    @Test
    public void testGetPermissionByNameWithResults() throws Exception {
        String name = "a";
        Permission permission = PermissionFactory.create(name, null,null, null);

        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(PermissionFactory.convertToJsonObject(permission));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(resourceClient.getPermissions(name,null,null, true,true))
                .thenReturn(response);
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);

        assertTrue(target.getPermissionByName(name).isPresent());
    }

    /**
     * Test to get permissions by id with results
     * @throws Exception in case of permission not found or type of fields are incorrect
     */
    @Test
    public void testGetPermissionByIdWithResults() throws Exception {
        Permission p = PermissionFactory.create("permission-a", 1L, null,2L);
        p.setId(11L);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeObject(PermissionFactory.convertToJsonObject(p));
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getById(anyLong())).
                thenReturn(response);

        Optional<SystemPermission> opt = target.getPermissionById(p.getId());
        assertNotNull(opt);
        assertTrue(opt.isPresent());
        assertEquals(opt.get().getId(), p.getId());
    }

    /**
     * Test to get permissions by non unique names
     * @throws Exception in case of permission not found or type of fields are incorrect
     */
    @Test(expected = Exception.class)
    public void testGetPermissionByNameNonUnique() throws Exception {
        String a = "a";
        Page<Permission> page = new Page<>(new ArrayList<>(),1,2,0);

        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValueInt(builder, "currentPage", page.getCurrentPage());
        FactoryUtilService.addValueInt(builder, "totalPages", page.getTotalPages());
        FactoryUtilService.addValueInt(builder, "totalResults", page.getTotalResults());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeObject(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(resourceClient.getPermissions(a,null,null,true,true))
                .thenReturn(response);
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);
        target.getPermissionByName(a);
    }

    /**
     * Test to get permission by name but facing an extension exception
     * @throws Exception in case of permission not found or type of fields are incorrect
     */
    @Test(expected = SystemException.class)
    public void testGetPermissionByNameExtensionException() throws Exception {
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenThrow(new ExtensionException(new Exception()));
        target.getPermissionByName("a");
    }

    /**
     * Test to get permissions by searching for its name but returning a processing exception
     * @throws Exception in case of permission not found or type of fields are incorrect
     */
    @Test(expected = SystemException.class)
    public void testGetPermissionByNameProcessingException() throws Exception {
        String a = "a";
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(resourceClient.getPermissions(a,null,null,true,true))
                .thenThrow(new ProcessingException(testValue));
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);

        target.getPermissionByName(a);
    }

    /**
     * Test to create permission
     * @throws SystemException in case of token expiration
     * @throws MalformedURLException in case of malformed URL in the permission endpoint
     */
    @Test
    public void testCreate() throws SystemException, MalformedURLException {
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(resourceClient.save(any())).thenReturn(Response.ok().build());
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);
        assertTrue(target.create(new Permission()));
    }

    /**
     * Test to create permission but without success
     * @throws SystemException in case of token expiration
     * @throws MalformedURLException in case of malformed URL in the permission endpoint
     */
    @Test
    public void testCreateFail() throws MalformedURLException, SystemException {
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(resourceClient.save(any())).thenReturn(Response.serverError().entity("test error msg").build());
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);
        assertFalse(target.create(new Permission()));
    }

    /**
     * Test to create permission but returned with a processing exception
     * @throws MalformedURLException in case of malformed URL in the permission endpoint
     * @throws SystemException in case of token expiration
     */
    @Test(expected = SystemException.class)
    public void testCreateProcessingException() throws MalformedURLException, SystemException {
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(resourceClient.save(any())).thenThrow(new ProcessingException(""));
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);
        target.create(new Permission());
    }

    /**
     * Test to try to communicate with the permission endpoint but withou success
     * @throws MalformedURLException in case of malformed URL in the permission endpoint
     */
    @Test
    public void testCommunicationFail() throws MalformedURLException {
        MalformedURLException malformedURLException =
                new MalformedURLException("Error accessing permission microservice");
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).
                thenThrow(malformedURLException);
        SystemException se = assertThrows(SystemException.class, () -> target.create(new Permission()));
        assertNotNull(se);
        assertTrue(se.getMessage().contains(malformedURLException.getMessage()));
    }

    /**
     * Test to attempt to access the requested OAF
     */
    @Test
    public void testAccessingOAF() {
        OAFAccess oafAccess = target.getOAF();
        assertNotNull(oafAccess);
    }

    /**
     * Test to get the permission by a requested id but with a extension exception being throw
     * @throws Exception to be throw
     */
    @Test(expected = SystemException.class)
    public void testGetPermissionByIdExtensionException() throws Exception {
        when(clientServiceUtil.getPermissionResourceClient(
                getPermissionManagementUrl())).thenThrow(new ExtensionException(new Exception()));
        target.getPermissionById(1L);
    }

    /**
     * Test to get the permission by a requested id but with a processing exception being throw
     * @throws Exception to be throw
     */
    @Test(expected = SystemException.class)
    public void testGetPermissionByIdProcessingException() throws Exception {
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(resourceClient.getById(anyLong()))
                .thenThrow(new ProcessingException(testValue));
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);
        target.getPermissionById(1L);
    }

    /**
     * Test to get permission by a requested action and resource with results to be found
     * @throws Exception in case of any issue
     */
    @Test
    public void testGetPermissionByActionAndResourceWithResults() throws Exception {
        java.util.List<Permission> inputList = new java.util.ArrayList<>();
        inputList.add(PermissionFactory.create("permission-a", 1L, 2L, 2L));
        inputList.add(PermissionFactory.create("permission-b", 1L, 2L, 3L));

        JsonArray jsonArray = PermissionModelMapper.map(inputList);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonArray.toString().getBytes());
        Response expectedResponse = Response.ok(inputStream).build();

        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getPermissions(null, 1L, 2L, true, true)).
                thenReturn(expectedResponse);

        List<? extends SystemPermission> result =
                target.getPermissionByActionAndResource(1L, 2L);
        assertNotNull(result);
        assertFalse(result.isEmpty());

        assertEquals(2, result.size());
    }

    /**
     * Test to get permission by a requested action and resource but without communication to the permission endpoint
     * @throws Exception to be throw
     */
    @Test
    public void testGetPermissionByActionAndResourceWithCommunicationBreakdown() throws Exception {
        MalformedURLException malformedURLException =
                new MalformedURLException("Error accessing permission microservice");
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).
                thenThrow(malformedURLException);
        SystemException se = assertThrows(SystemException.class,
                () -> target.getPermissionByActionAndResource(1L, 2L));
        assertNotNull(se);
        assertTrue(se.getMessage().contains(malformedURLException.getMessage()));
    }

    /**
     * Test to get permission by action and resource but with processing exception being throw
     * @throws Exception to be throw
     */
    @Test
    public void testGetPermissionByActionAndResourceWithProcessingException() throws Exception {
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getPermissions(null, 1L, 2L, true, true))
                .thenThrow(new ProcessingException(new Exception()));
        SystemException se = assertThrows(SystemException.class,
                () -> target.getPermissionByActionAndResource(1L, 2L));
        assertNotNull(se);
        assertTrue(se.getMessage().contains(ProcessingException.class.getName()));
    }

    /**
     * Test to validate if specific permission does exist
     * @throws MalformedURLException in case of malformed permission endpoint url
     */
    @Test
    public void testPermissionExists() throws MalformedURLException {
        Long permissionId = 1111L;
        String permissionName = "add-tenant";
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).
                thenReturn(resourceClient);
        when(resourceClient.exists(permissionId, permissionName)).then(i -> Response.ok().build());
        try {
            assertTrue(target.isPermissionExistent(permissionId, permissionName));
        } catch (SystemException systemException) {
            fail("unexpected exception");
        }
    }

    /**
     * Test to validate if permission does exist but will not be there
     * @throws MalformedURLException in case of malformed permission endpoint url
     */
    @Test
    public void testPermissionExistsFalseCase() throws MalformedURLException {
        Long permissionId = 1111L;
        String permissionName = "add-tenant";
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).
                thenReturn(resourceClient);

        // Case 1: Permission not exists (Status 404)
        when(resourceClient.exists(permissionId, permissionName)).
                then(i -> Response.status(Response.Status.NOT_FOUND).build());
        try {
            assertFalse(target.isPermissionExistent(permissionId, permissionName));
        } catch (SystemException systemException) {
            fail("unexpected exception");
        }

        // Case 2: Error 500 happened
        when(resourceClient.exists(permissionId, permissionName)).
                then(i -> Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
        try {
            assertFalse(target.isPermissionExistent(permissionId, permissionName));
        } catch (SystemException systemException) {
            fail("unexpected exception");
        }
    }

    /**
     * Test to validate if permission does exist but will throw  processing exception
     * @throws MalformedURLException in case of malformed permission endpoint url
     * @throws SystemException in case of token expiration or error in the system
     */
    @Test(expected = SystemException.class)
    public void testPermissionExistsWithProcessingException() throws MalformedURLException, SystemException {
        Long permissionId = 1111L;
        String permissionName = "add-tenant";
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).
                thenReturn(resourceClient);
        when(resourceClient.exists(permissionId, permissionName)).
                thenThrow(new ProcessingException("Error invoking endpoint"));
        target.isPermissionExistent(permissionId, permissionName);
    }

    /**
     * Test to attempt to get all the permissions
     * @throws MalformedURLException in case of malformed permission endpoint url
     * @throws SystemException in case of token expiration or error in the system
     */
    @Test
    public void testGetAll() throws MalformedURLException, SystemException {
        List<Permission> list = new ArrayList<>();
        list.add(PermissionFactory.create("add contract", 1L, 2L, 3L));
        list.add(PermissionFactory.create("delete contract", 2L, 2L, 3L));
        list.add(PermissionFactory.create("update contract", 3L, 2L, 3L));
        Page<SystemPermission> page = new Page<>(list, 1, 3, 1);

        JsonObjectBuilder objectBuilder = Json.createObjectBuilder()
                .add("currentPage", page.getCurrentPage())
                .add("totalResults", page.getTotalResults())
                .add("totalPages", page.getTotalPages())
                .add("results", PermissionModelMapper.map(list));

        JsonObject jsonObject = objectBuilder.build();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonObject.toString().getBytes());
        Response expectedResponse = Response.ok(inputStream).build();

        List<String> sortBy = new ArrayList<>();
        String filter = "%contract%";
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).
                then(i -> resourceClient);
        when(resourceClient.getAll(filter, 1, 100, sortBy, true)).
                then(i -> expectedResponse);

        Page resultPage = null;
        try {
            resultPage = target.getAll(filter, 1, 100, sortBy, true);
        }
        catch (Exception e) {
            fail("should not happen here...");
        }

        assertNotNull(resultPage);
        assertFalse(page.getResults().isEmpty());
        assertEquals(3, page.getResults().size());
    }

    /**
     * Test to attempt to get all the permissions but without success
     * @throws MalformedURLException in case of malformed permission endpoint url
     * @throws SystemException in case of token expiration or error in the system
     */
    @Test(expected = SystemException.class)
    public void testGetAllWithFailure() throws MalformedURLException, SystemException {
        List<Permission> list = new ArrayList<>();
        list.add(PermissionFactory.create("add contract", 1L, 2L, 3L));
        Page<SystemPermission> page = new Page<>(list, 1, 1, 1);

        JsonObjectBuilder objectBuilder = Json.createObjectBuilder()
                .add("currentPage", page.getCurrentPage())
                .add("totalResults", page.getTotalResults())
                .add("totalPages", page.getTotalPages())
                .add("results", PermissionModelMapper.map(list));

        JsonObject jsonObject = objectBuilder.build();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonObject.toString().getBytes());
        Response expectedResponse = Response.ok(inputStream).build();

        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).
                thenThrow(new ProcessingException("error"));
        when(resourceClient.getAll(null, 1, 100, null, true)).
                then(i -> expectedResponse);

        target.getAll(null, 1, 100, null, true);
    }

    /**
     * Test to get permissions via paginated mode
     * @throws MalformedURLException in case of malformed permission endpoint url
     */
    @Test
    public void testGetPermissions() throws MalformedURLException {
        List<Permission> list = new ArrayList<>();
        list.add(PermissionFactory.create("add contract", 1L, 2L, 3L));
        list.add(PermissionFactory.create("delete contract", 2L, 2L, 3L));
        list.add(PermissionFactory.create("update contract", 3L, 2L, 3L));
        Page<SystemPermission> page = new Page<>(list, 1, 3, 1);

        JsonObjectBuilder objectBuilder = Json.createObjectBuilder()
                .add("currentPage", page.getCurrentPage())
                .add("totalResults", page.getTotalResults())
                .add("totalPages", page.getTotalPages())
                .add("results", PermissionModelMapper.map(list));

        JsonObject jsonObject = objectBuilder.build();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonObject.toString().getBytes());
        Response expectedResponse = Response.ok(inputStream).build();

        List<String> sortBy = new ArrayList<>();

        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).
                then(i -> resourceClient);
        when(resourceClient.getAll("contract", 1, 100, sortBy, true)).
                then(i -> expectedResponse);

        List<? extends SystemPermission> list1 = null;
        try {
            list1 = target.getPermissions("contract", 1, 100, sortBy, true);
        }
        catch (Exception e) {
            fail("should not happen here...");
        }
        assertNotNull(list1);
        assertFalse(list1.isEmpty());
    }

    /**
     * Test to get permissions via paginated mode but without success
     * @throws MalformedURLException in case of malformed permission endpoint url
     * @throws SystemException in case of token expiration or error in the system
     */
    @Test(expected = SystemException.class)
    public void testGetPermissionsWhenFailure() throws MalformedURLException, SystemException {
        List<String> sortBy = new ArrayList<>();
        Response errorResponse = Response.status(500).build();
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).
                thenThrow(new ProcessingException("error"));
        when(resourceClient.getAll("contract", 1, 100, sortBy, true)).
                then(i -> errorResponse);
        target.getPermissions("contract", 1, 100, sortBy, true);
    }

    /**
     * Test to attempt to delete a permission
     * @throws MalformedURLException in case of malformed permission endpoint url
     * @throws SystemException in case of token expiration or error in the system
     */
    @Test
    public void testDelete() throws MalformedURLException, SystemException {
        long idOK = 1L;
        long idNOK = 2L;
        Response OK = Response.status(200).build();
        Response NOK = Response.status(500).build();

        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).
                then(i -> resourceClient);
        when(resourceClient.delete(idNOK)).then(i -> NOK);
        when(resourceClient.delete(idOK)).then(i -> OK);

        assertTrue(target.delete(idOK));
        assertFalse(target.delete(idNOK));
    }

    /**
     * Test to attempt to delete permission but without success
     * @throws MalformedURLException in case of malformed permission endpoint url
     * @throws SystemException in case of token expiration or error in the system
     */
    @Test(expected = SystemException.class)
    public void testDeleteWithFailure() throws MalformedURLException, SystemException {
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).
                then(i -> resourceClient);
        when(resourceClient.delete(1L)).thenThrow(new ProcessingException("error during delete process"));
        target.delete(1L);
    }

    /**
     * Test to count all the permission records
     * @throws MalformedURLException in case of malformed permission endpoint url
     * @throws SystemException in case of token expiration or error in the system
     */
    @Test(expected = SystemException.class)
    public void testGetTotalRecordsCount() throws MalformedURLException, SystemException {
        Permission permission = PermissionFactory.create(testValue, null,null, null);

        JsonArrayBuilder builder = Json.createArrayBuilder();
        builder.add(PermissionFactory.convertToJsonObject(permission));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);

        when(resourceClient.getTotalRecordsCount()).thenReturn(response);

        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);

        target.getTotalRecordsCount();
    }

    /**
     * Test to attempt to get all the permissions but with the token expired
     * @throws MalformedURLException in case of malformed permission endpoint url
     * @throws SystemException in case of token expiration or error in the system
     */
    @Test(expected = SystemException.class)
    public void testGetAllTokenExpiration() throws MalformedURLException, SystemException {
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);

        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getAll(anyString(), anyInt(), anyInt(), anyList(), anyBoolean())).thenThrow(new TokenExpiredException(testValue));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn(testValue);
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity(testValue).build());

        List<String> sortBy = new ArrayList<>();
        target.getAll("search", 1, 10, sortBy, true);
    }

    /**
     * Test to attempt to get the permissions but with the token expired
     * @throws MalformedURLException in case of malformed permission endpoint url
     * @throws SystemException in case of token expiration or error in the system
     */
    @Test(expected = SystemException.class)
    public void testGetPermissionsTokenExpiration() throws MalformedURLException, SystemException {
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);

        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getAll(anyString(), anyInt(), anyInt(), anyList(), anyBoolean())).thenThrow(new TokenExpiredException(testValue));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn(testValue);
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity(testValue).build());

        List<String> sortBy = new ArrayList<>();
        target.getPermissions("search", 1, 10, sortBy, true);
    }

    /**
     * Test to attempt to get the permissions by id but with the token expired
     * @throws MalformedURLException in case of malformed permission endpoint url
     * @throws SystemException in case of token expiration or error in the system
     */
    @Test(expected = SystemException.class)
    public void testGetPermissionsByIdTokenExpiration() throws MalformedURLException, SystemException {
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);

        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getById(anyLong())).thenThrow(new TokenExpiredException(testValue));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn(testValue);
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity(testValue).build());

        target.getPermissionById(2L);
    }

    /**
     * Test to attempt to get the permissions by action and resource but with the token expired
     * @throws MalformedURLException in case of malformed permission endpoint url
     * @throws SystemException in case of token expiration or error in the system
     */
    @Test(expected = SystemException.class)
    public void testGetPermissionsByActionAndResourceTokenExpiration() throws MalformedURLException, SystemException {
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);

        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getPermissions(any(), anyLong(), anyLong(), anyBoolean(), anyBoolean())).thenThrow(new TokenExpiredException(testValue));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn(testValue);
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity(testValue).build());

        target.getPermissionByActionAndResource(2L,2L);
    }

    /**
     * Test to attempt to get the permissions by the name but with the token expired
     * @throws MalformedURLException in case of malformed permission endpoint url
     * @throws SystemException in case of token expiration or error in the system
     */
    @Test(expected = SystemException.class)
    public void testGetPermissionsByNameTokenExpiration() throws MalformedURLException, SystemException {
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);

        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getPermissions(anyString(), any(), any(), anyBoolean(), anyBoolean())).thenThrow(new TokenExpiredException(testValue));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn(testValue);
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity(testValue).build());

        target.getPermissionByName("name");
    }

    /**
     * Test to attempt to delete the permissions but with the token expired
     * @throws MalformedURLException in case of malformed permission endpoint url
     * @throws SystemException in case of token expiration or error in the system
     */
    @Test(expected = SystemException.class)
    public void testDeleteTokenExpiration() throws MalformedURLException, SystemException {
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);

        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.delete(anyLong())).thenThrow(new TokenExpiredException(testValue));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn(testValue);
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity(testValue).build());

        target.delete(2L);
    }

    /**
     * Test to attempt to create permissions but with the token expired
     * @throws MalformedURLException in case of malformed permission endpoint url
     * @throws SystemException in case of token expiration or error in the system
     */
    @Test(expected = SystemException.class)
    public void testCreateTokenExpiration() throws MalformedURLException, SystemException {
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);

        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.save(any())).thenThrow(new TokenExpiredException(testValue));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn(testValue);
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity(testValue).build());

        SystemPermission systemPermission = new PermissionFactory().create("name", 2L, 2L, 2L);
        target.create(systemPermission);
    }

    /**
     * Test to attempt to validate if the permissions exist but with the token expired
     * @throws MalformedURLException in case of malformed permission endpoint url
     * @throws SystemException in case of token expiration or error in the system
     */
    @Test(expected = SystemException.class)
    public void testIsPermissionExistentTokenExpiration() throws MalformedURLException, SystemException {
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);

        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.exists(anyLong(), anyString())).thenThrow(new TokenExpiredException(testValue));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn(testValue);
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity(testValue).build());

        target.isPermissionExistent(2L,"name");
    }

    /**
     * Test to attempt to get a count of all the permissions but with the token expired
     * @throws MalformedURLException in case of malformed permission endpoint url
     * @throws SystemException in case of token expiration or error in the system
     */
    @Test(expected = SystemException.class)
    public void testGetTotalRecordsCountTokenExpiration() throws MalformedURLException, SystemException {
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);

        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getTotalRecordsCount()).thenThrow(new TokenExpiredException(testValue));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn(testValue);
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity(testValue).build());

        target.getTotalRecordsCount();
    }
}