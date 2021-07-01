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
package io.radien.ms.tenantmanagement.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.model.tenant.SystemActiveTenant;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.api.util.FactoryUtilService;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.client.UserClient;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.tenantmanagement.client.entities.ActiveTenant;
import io.radien.ms.tenantmanagement.client.util.ActiveTenantModelMapper;
import io.radien.ms.tenantmanagement.client.util.ClientServiceUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

/**
 * Active Tenant REST Service Client Test
 * {@link io.radien.ms.tenantmanagement.client.services.ActiveTenantRESTServiceClient}
 * @author Bruno Gama
 */
public class ActiveTenantRESTServiceClientTest {

    @InjectMocks
    ActiveTenantRESTServiceClient target;

    @Mock
    ClientServiceUtil clientServiceUtil;

    @Mock
    AuthorizationChecker authorizationChecker;

    @Mock
    UserClient userClient;

    @Mock
    TokensPlaceHolder tokensPlaceHolder;

    @Mock
    OAFAccess oafAccess;

    private ActiveTenant dummyActiveTenant = new ActiveTenant();

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);

        dummyActiveTenant.setId(2L);
        dummyActiveTenant.setUserId(2L);
        dummyActiveTenant.setTenantId(2L);
    }

    private String getActiveTenantManagementUrl(){
        String url = "";
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT)).thenReturn(url);
        return url;
    }

    /**
     * Tests the access into the db and retrieval of a active tenant by id
     * @throws MalformedURLException in case of issue connecting to the client
     * @throws SystemException in case of token expiration or any other issue for the system
     */
    @Test
    public void testGetActiveTenantById() throws MalformedURLException, SystemException {
        InputStream is = new ByteArrayInputStream(ActiveTenantModelMapper.map(dummyActiveTenant).toString().getBytes());
        Response response = Response.ok(is).build();
        ActiveTenantResourceClient resourceClient = Mockito.mock(ActiveTenantResourceClient.class);
        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getById(anyLong())).
                thenReturn(response);
        Optional<SystemActiveTenant> opt = target.getActiveTenantById(dummyActiveTenant.getId());
        assertNotNull(opt);
        assertTrue(opt.isPresent());
        assertEquals(opt.get().getId(), dummyActiveTenant.getId());
    }

    /**
     * Test to get active tenant by id with token exception being throw
     * @throws Exception in case o token exception
     */
    @Test
    public void testGetActiveTenantByIdTokenExpiration() throws Exception {
        ActiveTenantResourceClient client = Mockito.mock(ActiveTenantResourceClient.class);

        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenReturn(client);
        when(client.getById(1L)).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        assertThrows(SystemException.class, () -> target.getActiveTenantById(1L));
    }

    /**
     * Tests the access into the db and retrieval of a active tenant by id but with the throw of an exception
     * @throws Exception in case of any issue
     */
    @Test(expected = SystemException.class)
    public void testGetActiveTenantByIdException() throws Exception {
        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenThrow(new MalformedURLException());
        target.getActiveTenantById(3L);
    }

    /**
     * Tests the access into the db and retrieval of all the active tenants
     * @throws MalformedURLException in case of issue connecting to the client
     * @throws SystemException in case of token expiration or any other issue for the system
     */
    @Test
    public void testGetAll() throws MalformedURLException, SystemException {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValueInt(builder, "currentPage", 1);
        FactoryUtilService.addValueInt(builder, "totalPages", 1);
        FactoryUtilService.addValueInt(builder, "totalResults", 1);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeObject(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        ActiveTenantResourceClient activeTenantResourceClient = Mockito.mock(ActiveTenantResourceClient.class);

        when(activeTenantResourceClient.getAll(null,1, 10, null, false)).thenReturn(response);

        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenReturn(activeTenantResourceClient);

        List<? extends SystemActiveTenant> list = new ArrayList<>();

        List<? extends SystemActiveTenant> returnedList = target.getAll(null,1, 10, null, false).getResults();

        assertEquals(list, returnedList);
    }

    /**
     * Tests the access into the db and retrieval of all the active tenants but in this case with the throw of an
     * exception
     * @throws Exception in case of any issue
     */
    @Test(expected = SystemException.class)
    public void testGetAllException() throws Exception {
        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenThrow(new MalformedURLException());
        target.getAll(null,1, 10, null, false);
    }

    /**
     * Test to get all the active tenant with token exception being throw
     * @throws Exception in case o token exception
     */
    @Test(expected = SystemException.class)
    public void testGetAllActiveTenantsTokenExpiration() throws Exception {
        ActiveTenantResourceClient client = Mockito.mock(ActiveTenantResourceClient.class);

        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenReturn(client);
        when(client.getAll(anyString(), anyInt(), anyInt(), any(), anyBoolean())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getAll("", 1, 10, null, true);
    }

    /**
     * Tests the access into the db and creation of a active tenant
     * @throws MalformedURLException in case of issue connecting to the client
     * @throws SystemException in case of token expiration or any other issue for the system
     */
    @Test
    public void testCreate() throws MalformedURLException, SystemException {
        ActiveTenantResourceClient activeTenantResourceClient = Mockito.mock(ActiveTenantResourceClient.class);
        when(activeTenantResourceClient.create(any())).thenReturn(Response.ok().build());
        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenReturn(activeTenantResourceClient);
        assertTrue(target.create(new ActiveTenant()));
    }

    /**
     * Tests the access into the db and creation of a active tenant but withou success
     * @throws MalformedURLException in case of issue connecting to the client
     * @throws SystemException in case of token expiration or any other issue for the system
     */
    @Test
    public void testCreateFalse() throws MalformedURLException, SystemException {
        ActiveTenantResourceClient activeTenantResourceClient = Mockito.mock(ActiveTenantResourceClient.class);
        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenReturn(activeTenantResourceClient);
        when(activeTenantResourceClient.create(any())).thenReturn(Response.notModified().entity("teste").build());
        boolean success = target.create(new ActiveTenant());
        assertFalse(success);
    }

    /**
     * Tests the access into the db and creation of a active tenant but this with an issue so that
     * we can validate the exception capture
     * @throws Exception in case of issue
     */
    @Test(expected = SystemException.class)
    public void testCreateException() throws Exception {
        ActiveTenantResourceClient activeTenantResourceClient = Mockito.mock(ActiveTenantResourceClient.class);
        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenReturn(activeTenantResourceClient);
        when(activeTenantResourceClient.create(any())).thenThrow(new ProcessingException("teste"));
        target.create(new ActiveTenant());
    }

    /**
     * Tests the access into the db and creation of a active tenant but this with an issue so that
     * we can validate the exception capture of the malformed URL exception
     * @throws Exception in case of issue
     */
    @Test(expected = SystemException.class)
    public void testCreateMalformedUrlException() throws Exception {
        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenThrow(new MalformedURLException());
        target.create(new ActiveTenant());
    }

    /**
     * Test to create the active tenant with token exception being throw
     * @throws Exception in case o token exception
     */
    @Test(expected = SystemException.class)
    public void testCreateActiveTenantsTokenExpiration() throws Exception {
        ActiveTenantResourceClient client = Mockito.mock(ActiveTenantResourceClient.class);

        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenReturn(client);
        when(client.create(any())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        SystemActiveTenant systemActiveTenant = new ActiveTenant();
        target.create(systemActiveTenant);
    }

    /**
     * Tests the access into the db and deletion of a active tenant
     * @throws MalformedURLException in case of issue connecting to the client
     */
    @Test
    public void testDelete() throws MalformedURLException {
        ActiveTenantResourceClient activeTenantResourceClient = Mockito.mock(ActiveTenantResourceClient.class);
        when(activeTenantResourceClient.delete(dummyActiveTenant.getId())).thenReturn(Response.ok().build());
        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenReturn(activeTenantResourceClient);

        boolean success = false;
        try {
            assertTrue(target.delete(2L));
        } catch (Exception e) {
            success = true;
        }
        assertFalse(success);
    }

    /**
     * Tests the access into the db and deletion of a active tenant but without success
     * @throws MalformedURLException in case of issue connecting to the client
     */
    @Test
    public void testDeleteReturnFalse() throws MalformedURLException, SystemException {
        ActiveTenantResourceClient activeTenantResourceClient = Mockito.mock(ActiveTenantResourceClient.class);
        when(activeTenantResourceClient.delete(2L)).thenReturn(Response.serverError().entity("teste").build());
        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenReturn(activeTenantResourceClient);

        assertFalse(target.delete(2L));
    }

    /**
     * Tests the access into the db and deletion of a active tenant but this with an issue so that
     * we can validate the exception capture
     * @throws Exception in case of issue
     */
    @Test(expected = SystemException.class)
    public void testDeleteException() throws Exception {
        ActiveTenantResourceClient activeTenantResourceClient = Mockito.mock(ActiveTenantResourceClient.class);
        when(activeTenantResourceClient.delete(dummyActiveTenant.getId())).thenThrow(new ProcessingException("teste"));
        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenReturn(activeTenantResourceClient);
        target.delete(2L);
    }

    /**
     * Tests the access into the db and deletion of a active tenant but this with an issue so that
     * we can validate the exception capture of the malformed URL exception
     * @throws Exception in case of issue
     */
    @Test(expected = SystemException.class)
    public void testDeletionMalformedUrlException() throws Exception {
        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenThrow(new MalformedURLException());
        target.delete(2L);
    }

    /**
     * Test to delete the active tenant with token exception being throw
     * @throws Exception in case o token exception
     */
    @Test(expected = SystemException.class)
    public void testDeleteActiveTenantsTokenExpiration() throws Exception {
        ActiveTenantResourceClient client = Mockito.mock(ActiveTenantResourceClient.class);

        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenReturn(client);
        when(client.delete(anyLong())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.delete(2L);
    }

    /**
     * Tests the access into the db and update of a active tenant
     * @throws MalformedURLException in case of issue connecting to the client
     * @throws SystemException in case of token expiration or any other issue for the system
     */
    @Test
    public void testUpdate() throws MalformedURLException, SystemException {
        ActiveTenantResourceClient activeTenantResourceClient = Mockito.mock(ActiveTenantResourceClient.class);
        when(activeTenantResourceClient.update(2L, dummyActiveTenant)).thenReturn(Response.ok().build());
        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenReturn(activeTenantResourceClient);
        dummyActiveTenant.setTenantId(3L);

        assertTrue(target.update(dummyActiveTenant));
    }

    /**
     * Tests the access into the db and update of a active tenant but without success
     * @throws MalformedURLException in case of issue connecting to the client
     * @throws SystemException in case of token expiration or any other issue for the system
     */
    @Test
    public void testUpdateReturnFalse() throws MalformedURLException, SystemException {
        ActiveTenantResourceClient activeTenantResourceClient = Mockito.mock(ActiveTenantResourceClient.class);
        when(activeTenantResourceClient.update(2L, dummyActiveTenant)).thenReturn(Response.serverError().entity("teste").build());
        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenReturn(activeTenantResourceClient);

        assertFalse(target.update(dummyActiveTenant));
    }

    /**
     * Tests the access into the db and update of a active tenant but with an issue so that we can test
     * the exception being capture
     * @throws Exception in case of issue
     */
    @Test
    public void testUpdateException() throws Exception {
        boolean success = false;
        ActiveTenantResourceClient activeTenantResourceClient = Mockito.mock(ActiveTenantResourceClient.class);
        when(activeTenantResourceClient.update(2L, dummyActiveTenant)).thenThrow(new ProcessingException("teste"));
        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenReturn(activeTenantResourceClient);
        try {
            target.update(dummyActiveTenant);
        }catch (SystemException se){
            success = true;
        }
        assertTrue(success);
    }

    /**
     * Tests the access into the db and update of a active tenant but this with an issue so that
     * we can validate the exception capture of the malformed URL exception
     * @throws Exception in case of issue
     */
    @Test
    public void testUpdateMalformedUrlException() throws Exception {
        boolean success = false;
        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenThrow(new MalformedURLException());
        try {
            target.update(new ActiveTenant());
        }catch (SystemException se){
            success = true;
        }
        assertTrue(success);
    }

    /**
     * Test to update the active tenant with token exception being throw
     * @throws Exception in case o token exception
     */
    @Test(expected = SystemException.class)
    public void testUpdateActiveTenantsTokenExpiration() throws Exception {
        ActiveTenantResourceClient client = Mockito.mock(ActiveTenantResourceClient.class);

        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenReturn(client);
        when(client.update(anyLong(), any())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        SystemActiveTenant systemActiveTenant = new ActiveTenant(2L, 2L, 2L, "teste", true);
        target.update(systemActiveTenant);
    }

    /**
     * Tests the access into the db and validates if a specific active tenant is existent
     * @throws MalformedURLException in case of issue connecting to the client
     * @throws SystemException in case of token expiration or any other issue for the system
     */
    @Test
    public void testIsActiveTenantExistent() throws MalformedURLException, SystemException {
        ActiveTenantResourceClient activeTenantResourceClient = Mockito.mock(ActiveTenantResourceClient.class);
        when(activeTenantResourceClient.exists(anyLong(), anyLong())).thenReturn(Response.ok(Boolean.TRUE).build());
        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenReturn(activeTenantResourceClient);
        dummyActiveTenant.setUserId(2L);

        assertTrue(target.isActiveTenantExistent(dummyActiveTenant.getUserId(), dummyActiveTenant.getTenantId()));
    }

    /**
     * Tests the access into the db and validates if a specific active tenant is existent but it is not
     * @throws MalformedURLException in case of issue connecting to the client
     * @throws SystemException in case of token expiration or any other issue for the system
     */
    @Test
    public void testIsActiveTenantExistentReturnFalseResponse() throws MalformedURLException, SystemException {
        ActiveTenantResourceClient activeTenantResourceClient = Mockito.mock(ActiveTenantResourceClient.class);
        when(activeTenantResourceClient.exists(anyLong(), anyLong())).thenReturn(Response.ok(Boolean.FALSE).build());
        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenReturn(activeTenantResourceClient);
        dummyActiveTenant.setUserId(2L);

        assertFalse(target.isActiveTenantExistent(dummyActiveTenant.getUserId(), dummyActiveTenant.getTenantId()));
    }

    /**
     * Tests the access into the db and validates if a specific active tenant is existent but with malformed
     * url exception being throw so that we can validate its capture
     * @throws MalformedURLException in case of issue connecting to the client
     */
    @Test
    public void testIsActiveTenantExistentMalformedException() throws MalformedURLException {
        boolean success = false;
        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenThrow(new MalformedURLException());
        try {
            target.isActiveTenantExistent(500L, 500L);
        }catch (SystemException se){
            success = true;
        }
        assertTrue(success);
    }

    /**
     * Tests the access into the db and validates if a specific active tenant is existent but with processing exception
     * being throw so that we can validate its capture
     * @throws MalformedURLException in case of issue connecting to the client
     */
    @Test
    public void testIsActiveTenantExistentProcessingException() throws MalformedURLException {
        boolean success = false;
        ActiveTenantResourceClient activeTenantResourceClient = Mockito.mock(ActiveTenantResourceClient.class);
        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenReturn(activeTenantResourceClient);
        when(activeTenantResourceClient.exists(anyLong(), anyLong())).thenThrow(new ProcessingException("test"));
        try {
            target.isActiveTenantExistent(500L, 500L);
        }catch (SystemException se){
            success = true;
        }
        assertTrue(success);
    }

    /**
     * Test to check if a certain active tenant exists with token exception being throw
     * @throws Exception in case o token exception
     */
    @Test(expected = SystemException.class)
    public void testIsActiveTenantExistentActiveTenantsTokenExpiration() throws Exception {
        ActiveTenantResourceClient client = Mockito.mock(ActiveTenantResourceClient.class);

        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenReturn(client);
        when(client.exists(anyLong(), anyLong())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.isActiveTenantExistent(2L, 2L);
    }

    /**
     * Tests the access into the db and retrieval of a active tenant by searching only by the user id and tenant id
     * @throws MalformedURLException in case of issue connecting to the client
     * @throws SystemException in case of token expiration or any other issue for the system
     */
    @Test
    public void testGetActiveTenantByUserAndTenant() throws MalformedURLException, SystemException {
        JsonArrayBuilder builder = Json.createArrayBuilder();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        Response response = Response.ok(is).build();
        ActiveTenantResourceClient resourceClient = Mockito.mock(ActiveTenantResourceClient.class);
        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getByUserAndTenant(anyLong(), anyLong())).thenReturn(response);
        List<? extends SystemActiveTenant> list = new ArrayList<>();

        assertEquals(list,target.getActiveTenantByUserAndTenant(100L, 100L));
    }

    /**
     * Tests the access into the db and retrieval of a active tenant by searching only by the user id and tenant id
     * with token exception being throw
     * @throws Exception in case o token exception
     */
    @Test(expected = SystemException.class)
    public void testGetActiveTenantByUserAndTenantTokenExpiration() throws Exception {
        ActiveTenantResourceClient resourceClient = Mockito.mock(ActiveTenantResourceClient.class);

        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getByUserAndTenant(anyLong(), anyLong())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getActiveTenantByUserAndTenant(2L, 2L);
    }

    /**
     * Tests the access into the db and retrieval of a active tenant by searching only by the user id and tenant id
     * with exception being throw
     * @throws Exception in case o token exception
     */
    @Test(expected = Exception.class)
    public void testGetActiveTenantByUserAndTenantException() throws Exception {
        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenThrow(new ProcessingException("test"));
        target.getActiveTenantByUserAndTenant(2L, 2L);
    }

    /**
     * Tests the access into the db and retrieval of a active tenant by searching only by the user id and tenant id
     * @throws MalformedURLException in case of issue connecting to the client
     * @throws SystemException in case of token expiration or any other issue for the system
     */
    @Test
    public void testGetActiveTenantByFilter() throws MalformedURLException, SystemException {
        JsonArrayBuilder builder = Json.createArrayBuilder();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        Response response = Response.ok(is).build();
        ActiveTenantResourceClient resourceClient = Mockito.mock(ActiveTenantResourceClient.class);
        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.get(anyLong(), anyLong(), anyString(), anyBoolean(), anyBoolean())).thenReturn(response);
        List<? extends SystemActiveTenant> list = new ArrayList<>();

        assertEquals(list,target.getActiveTenantByFilter(100L, 100L, "test", false));
    }

    /**
     * Tests the access into the db and retrieval of a active tenant by searching only by the user id and tenant id
     * with token exception being throw
     * @throws Exception in case o token exception
     */
    @Test(expected = SystemException.class)
    public void testGetActiveTenantByFilterTokenExpiration() throws Exception {
        ActiveTenantResourceClient resourceClient = Mockito.mock(ActiveTenantResourceClient.class);

        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.get(anyLong(), anyLong(), anyString(), anyBoolean(), anyBoolean())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getActiveTenantByFilter(2L, 2L, "test", false);
    }

    /**
     * Tests the access into the db and retrieval of a active tenant by searching only by the user id and tenant id
     * with exception being throw
     * @throws Exception in case o token exception
     */
    @Test(expected = Exception.class)
    public void testGetActiveTenantByFilterException() throws Exception {
        when(clientServiceUtil.getActiveTenantResourceClient(getActiveTenantManagementUrl())).thenThrow(new ProcessingException("test"));
        target.getActiveTenantByFilter(2L, 2L, "test", false);
    }
}