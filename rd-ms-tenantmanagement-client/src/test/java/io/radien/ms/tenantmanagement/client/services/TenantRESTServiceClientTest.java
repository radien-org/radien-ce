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
package io.radien.ms.tenantmanagement.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.api.util.FactoryUtilService;
import io.radien.exception.SystemException;
import io.radien.exception.TokenExpiredException;
import io.radien.ms.authz.client.UserClient;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.tenantmanagement.client.entities.TenantType;
import io.radien.ms.tenantmanagement.client.util.ClientServiceUtil;
import io.radien.ms.tenantmanagement.client.util.TenantModelMapper;
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
import javax.json.JsonArray;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

/**
 * @author Bruno Gama
 */
public class TenantRESTServiceClientTest {

    @InjectMocks
    TenantRESTServiceClient target;

    @Mock
    ClientServiceUtil tenantServiceUtil;

    @Mock
    AuthorizationChecker authorizationChecker;

    @Mock
    UserClient userClient;

    @Mock
    TokensPlaceHolder tokensPlaceHolder;

    @Mock
    OAFAccess oafAccess;

    private final Tenant dummyTenant = new Tenant();

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);

        dummyTenant.setId(2L);
        dummyTenant.setTenantType(TenantType.ROOT_TENANT.getName());
        dummyTenant.setTenantKey("tenantKey");
        dummyTenant.setName("name");
    }

    private String getTenantManagementUrl(){
        String url = "";
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT)).thenReturn(url);
        return url;
    }

    @Test
    public void testGetTenantById() throws MalformedURLException, SystemException {
        InputStream is = new ByteArrayInputStream(TenantModelMapper.map(dummyTenant).toString().getBytes());
        Response response = Response.ok(is).build();
        TenantResourceClient resourceClient = Mockito.mock(TenantResourceClient.class);
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getById(anyLong())).
                thenReturn(response);
        Optional<SystemTenant> opt = target.getTenantById(dummyTenant.getId());
        assertNotNull(opt);
        assertTrue(opt.isPresent());
        assertEquals(opt.get().getId(), dummyTenant.getId());
    }

    @Test
    public void testGetTenantByIdException() throws Exception {
        boolean success = false;
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenThrow(new MalformedURLException());
        try {
            target.getTenantById(3L);
        }catch (SystemException se){
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void testGetTenantByName() throws SystemException, MalformedURLException {
        String a = "name";

        JsonArrayBuilder builder = Json.createArrayBuilder();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        TenantResourceClient tenantResourceClient = Mockito.mock(TenantResourceClient.class);

        when(tenantResourceClient.get(any(),any(),any(),anyBoolean(),anyBoolean()))
                .thenReturn(response);

        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(tenantResourceClient);

        List<? extends SystemTenant> list = new ArrayList<>();

        assertEquals(list, target.getTenantByName(a));
    }

    @Test
    public void testGetTenantByNameException() throws Exception {
        boolean success = false;
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenThrow(new MalformedURLException());
        try {
            target.getTenantByName("testException");
        }catch (SystemException se){
            success = true;
        }
        assertTrue(success);
    }

    /**
     * Test for method getTenantById(id)
     * It corresponds to the unsuccessful situation where JWT expiration occurs and
     * is not possible to recover from that
     * Expected result (FAIL): SystemException thrown due JWT expiration during the reattempt
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetTenantByIdWithTokenException() throws MalformedURLException, SystemException {
        TenantResourceClient resourceClient = Mockito.mock(TenantResourceClient.class);
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getById(anyLong())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getTenantById(1L);
    }

    /**
     * Test for method getTenantsByIds(List ids)
     * It corresponds to the successful situation where tenants are found and retrieved for
     * the informed ids.
     * Expected result (SUCCESS): Tenants retrieved
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetTenantsByIds() throws MalformedURLException, SystemException {
        Tenant t1 = new Tenant();
        t1.setId(1L);
        t1.setTenantType(TenantType.ROOT_TENANT.getName());

        Tenant t2 = new Tenant();
        t2.setId(2L);
        t2.setTenantType(TenantType.SUB_TENANT.getName());

        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        jsonArrayBuilder.add(TenantModelMapper.map(t1));
        jsonArrayBuilder.add(TenantModelMapper.map(t2));
        JsonArray jsonArray = jsonArrayBuilder.build();

        List<Long> ids = Arrays.asList(t1.getId(), t2.getId());

        InputStream is = new ByteArrayInputStream(jsonArray.toString().getBytes());
        Response response = Response.ok(is).build();

        TenantResourceClient resourceClient = Mockito.mock(TenantResourceClient.class);
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.get(null, null, ids, true, true)).thenReturn(response);

        List<? extends SystemTenant> outcome = target.getTenantsByIds(ids);
        assertNotNull(outcome);
        assertFalse(outcome.isEmpty());
        assertEquals(2, outcome.size());
    }

    /**
     * Test for method getTenantsByIds(List ids)
     * It corresponds to the unsuccessful situation where exception occurs
     * Expected result (FAIL): SystemException thrown
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetTenantsByIdsException() throws MalformedURLException, SystemException {
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenThrow(new MalformedURLException());
        target.getTenantsByIds(Arrays.asList(3L, 4L));
    }

    /**
     * Test for method getTenantsByIds(List ids)
     * It corresponds to the unsuccessful situation where JWT expiration occurs and
     * is not possible to recover from that
     * Expected result (FAIL): SystemException thrown
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetTenantsByIdsTokenExpiration() throws MalformedURLException, SystemException {
        List<Long> ids = Arrays.asList(1L, 2L);
        TenantResourceClient resourceClient = Mockito.mock(TenantResourceClient.class);
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.get(null, null, ids, true, true)).
                thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getTenantsByIds(ids);
    }

    /**
     * Test for method getTenantsByIds(List ids)
     * It corresponds to the the case where is not possible to find Tenants
     * Expected result (SUCCESS): Empty list. No Tenants found for the informed ids
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test
    public void testGetTenantsByIdsNoResults() throws MalformedURLException, SystemException {
        List<Long> ids = Arrays.asList(1L, 2L);
        InputStream is = new ByteArrayInputStream("[]".getBytes());
        Response response = Response.ok(is).build();

        TenantResourceClient resourceClient = Mockito.mock(TenantResourceClient.class);
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.get(null, null, ids, true, true)).thenReturn(response);

        List<? extends SystemTenant> outcome = target.getTenantsByIds(ids);
        assertNotNull(outcome);
        assertTrue(outcome.isEmpty());
    }


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

        TenantResourceClient tenantResourceClient = Mockito.mock(TenantResourceClient.class);

        when(tenantResourceClient.getAll(null,1, 10, null, false)).thenReturn(response);

        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(tenantResourceClient);

        List<? extends SystemTenant> list = new ArrayList<>();

        List<? extends SystemTenant> returnedList = target.getAll(null,1, 10, null, false).getResults();

        assertEquals(list, returnedList);
    }

    @Test
    public void testGetAllException() throws Exception {
        boolean success = false;
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenThrow(new MalformedURLException());
        try {
            target.getAll(null,1, 10, null, false);
        }catch (SystemException se){
            success = true;
        }
        assertTrue(success);
    }

    /**
     * Test for method getAll(search, page, size, sortBy, boolean)
     * It corresponds to the unsuccessful situation where JWT expiration occurs and
     * is not possible to recover from that
     * Expected result (FAIL): SystemException thrown
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testGetAllWithTokenException() throws MalformedURLException, SystemException {
        TenantResourceClient resourceClient = Mockito.mock(TenantResourceClient.class);
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getAll(anyString(), anyInt(),anyInt(),anyList(), anyBoolean())).
                thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getAll("toyota", 1, 10, new ArrayList<>(), true);
    }

    @Test
    public void testCreate() throws MalformedURLException, SystemException {
        TenantResourceClient tenantResourceClient = Mockito.mock(TenantResourceClient.class);
        when(tenantResourceClient.create(any())).thenReturn(Response.ok().build());
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(tenantResourceClient);
        assertTrue(target.create(new Tenant()));
    }

    @Test
    public void testCreateFalse() throws MalformedURLException, SystemException {
        TenantResourceClient tenantResourceClient = Mockito.mock(TenantResourceClient.class);
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(tenantResourceClient);
        when(tenantResourceClient.create(any())).thenReturn(Response.notModified().entity("teste").build());
        boolean sucess = target.create(new Tenant());
        assertFalse(sucess);
    }

    @Test
    public void testCreateException() throws Exception {
        boolean success = false;
        TenantResourceClient tenantResourceClient = Mockito.mock(TenantResourceClient.class);
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(tenantResourceClient);
        when(tenantResourceClient.create(any())).thenThrow(new ProcessingException("teste"));
        try {
            target.create(new Tenant());
        }catch (SystemException se){
            success = true;
        }
        assertTrue(success);
    }

    /**
     * Test for method create(tenant)
     * It corresponds to the unsuccessful situation where JWT expiration occurs and
     * is not possible to recover from that
     * Expected result (FAIL): SystemException thrown
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testCreateWithTokenException() throws MalformedURLException, SystemException {
        Tenant toBeCreated = mock(Tenant.class);
        TenantResourceClient resourceClient = Mockito.mock(TenantResourceClient.class);
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.create(toBeCreated)).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.create(toBeCreated);
    }

    @Test
    public void testDelete() throws MalformedURLException {
        TenantResourceClient tenantResourceClient = Mockito.mock(TenantResourceClient.class);
        when(tenantResourceClient.delete(dummyTenant.getId())).thenReturn(Response.ok().build());
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(tenantResourceClient);

        boolean success = false;
        try {
            assertTrue(target.delete(2L));
        } catch (Exception e) {
            success = true;
        }
        assertFalse(success);
    }

    @Test
    public void testDeleteReturnFalse() throws MalformedURLException, SystemException {
        TenantResourceClient tenantResourceClient = Mockito.mock(TenantResourceClient.class);
        when(tenantResourceClient.delete(2L)).thenReturn(Response.serverError().entity("teste").build());
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(tenantResourceClient);

        assertFalse(target.delete(2L));
    }

    @Test
    public void testDeleteException() throws Exception {
        boolean success = false;
        TenantResourceClient tenantResourceClient = Mockito.mock(TenantResourceClient.class);
        when(tenantResourceClient.delete(dummyTenant.getId())).thenThrow(new ProcessingException("teste"));
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(tenantResourceClient);
        try {
            target.delete(2L);
        }catch (SystemException se){
            success = true;
        }
        assertTrue(success);
    }

    /**
     * Test for method delete(tenantId)
     * It corresponds to the unsuccessful situation where JWT expiration occurs and
     * is not possible to recover from that
     * Expected result (FAIL): SystemException thrown
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testDeleteWithTokenException() throws MalformedURLException, SystemException {
        TenantResourceClient resourceClient = Mockito.mock(TenantResourceClient.class);
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.delete(anyLong())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.delete(1L);
    }

    @Test
    public void testDeleteTenantHierarchy() throws MalformedURLException {
        TenantResourceClient tenantResourceClient = Mockito.mock(TenantResourceClient.class);
        when(tenantResourceClient.deleteTenantHierarchy(dummyTenant.getId())).thenReturn(Response.ok().build());
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(tenantResourceClient);

        boolean success = false;
        try {
            assertTrue(target.deleteTenantHierarchy(2L));
        } catch (Exception e) {
            success = true;
        }
        assertFalse(success);
    }

    @Test
    public void testDeleteTenantHierarchyReturnFalse() throws MalformedURLException, SystemException {
        TenantResourceClient tenantResourceClient = Mockito.mock(TenantResourceClient.class);
        when(tenantResourceClient.deleteTenantHierarchy(3L)).thenReturn(Response.serverError().entity("teste").build());
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(tenantResourceClient);

        assertFalse(target.deleteTenantHierarchy(3L));
    }

    @Test
    public void testDeleteTenantHierarchyException() throws Exception {
        boolean success = false;
        TenantResourceClient tenantResourceClient = Mockito.mock(TenantResourceClient.class);
        when(tenantResourceClient.deleteTenantHierarchy(dummyTenant.getId())).thenThrow(new ProcessingException("teste"));
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(tenantResourceClient);
        try {
            target.deleteTenantHierarchy(2L);
        }catch (SystemException se){
            success = true;
        }
        assertTrue(success);
    }

    /**
     * Test for method deleteTenantHierarchy(tenantId)
     * It corresponds to the unsuccessful situation where JWT expiration occurs and
     * is not possible to recover from that
     * Expected result (FAIL): SystemException thrown
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testDeleteHierarchyWithTokenException() throws MalformedURLException, SystemException {
        TenantResourceClient resourceClient = Mockito.mock(TenantResourceClient.class);
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.deleteTenantHierarchy(anyLong())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.deleteTenantHierarchy(1L);
    }

    @Test
    public void testUpdate() throws MalformedURLException, SystemException {
        TenantResourceClient tenantResourceClient = Mockito.mock(TenantResourceClient.class);
        when(tenantResourceClient.update(2L, dummyTenant)).thenReturn(Response.ok().build());
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(tenantResourceClient);
        dummyTenant.setName("name-update");

        assertTrue(target.update(dummyTenant));
    }

    @Test
    public void testUpdateReturnFalse() throws MalformedURLException, SystemException {
        TenantResourceClient tenantResourceClient = Mockito.mock(TenantResourceClient.class);
        when(tenantResourceClient.update(2L, dummyTenant)).thenReturn(Response.serverError().entity("teste").build());
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(tenantResourceClient);

        assertFalse(target.update(dummyTenant));
    }

    @Test
    public void testUpdateException() throws Exception {
        boolean success = false;
        TenantResourceClient tenantResourceClient = Mockito.mock(TenantResourceClient.class);
        when(tenantResourceClient.update(2L, dummyTenant)).thenThrow(new ProcessingException("teste"));
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(tenantResourceClient);
        try {
            target.update(dummyTenant);
        }catch (SystemException se){
            success = true;
        }
        assertTrue(success);
    }

    /**
     * Test for method update(tenant)
     * It corresponds to the unsuccessful situation where JWT expiration occurs and
     * is not possible to recover from that
     * Expected result (FAIL): SystemException thrown
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testUpdateWithTokenException() throws MalformedURLException, SystemException {
        long id = 1L;
        Tenant toBeUpdated = new Tenant();
        toBeUpdated.setId(id);
        TenantResourceClient resourceClient = Mockito.mock(TenantResourceClient.class);
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.update(id, toBeUpdated)).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.update(toBeUpdated);
    }

    @Test
    public void testIsTenantExistent() throws MalformedURLException, SystemException {
        TenantResourceClient tenantResourceClient = Mockito.mock(TenantResourceClient.class);
        when(tenantResourceClient.exists(any())).thenReturn(Response.ok(Boolean.TRUE).build());
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(tenantResourceClient);
        dummyTenant.setName("name-update");

        assertTrue(target.isTenantExistent(dummyTenant.getId()));
    }

    @Test
    public void testIsTenantExistentReturnFalseResponse() throws MalformedURLException, SystemException {
        TenantResourceClient tenantResourceClient = Mockito.mock(TenantResourceClient.class);
        when(tenantResourceClient.exists(any())).thenReturn(Response.ok().entity(Boolean.FALSE).build());
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(tenantResourceClient);
        dummyTenant.setName("name-update");

        assertFalse(target.isTenantExistent(dummyTenant.getId()));
    }

    @Test
    public void testIsTenantExistentMalformedException() throws MalformedURLException {
        boolean success = false;
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenThrow(new MalformedURLException());
        try {
            target.isTenantExistent(500L);
        }catch (SystemException se){
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void testIsTenantExistentProcessingException() throws MalformedURLException {
        boolean success = false;
        TenantResourceClient tenantResourceClient = Mockito.mock(TenantResourceClient.class);
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(tenantResourceClient);
        when(tenantResourceClient.exists(any())).thenThrow(new ProcessingException("test"));
        try {
            target.isTenantExistent(500L);
        }catch ( SystemException se){
            success = true;
        }
        assertTrue(success);
    }

    /**
     * Test for method update(tenant)
     * It corresponds to the unsuccessful situation where JWT expiration occurs and
     * is not possible to recover from that
     * Expected result (FAIL): SystemException thrown
     * @throws MalformedURLException for url informed incorrectly
     * @throws SystemException in case of any communication issue
     */
    @Test(expected = SystemException.class)
    public void testIsTenantExistentWithTokenException() throws MalformedURLException, SystemException {
        Tenant toBeUpdated = mock(Tenant.class);
        toBeUpdated.setId(1L);
        TenantResourceClient resourceClient = Mockito.mock(TenantResourceClient.class);
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.exists(1L)).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.isTenantExistent(1L);
    }

    @Test(expected = SystemException.class)
    public void testGetTenantByIdTokenExpiration() throws Exception {
        TenantResourceClient tenantResourceClient = Mockito.mock(TenantResourceClient.class);

        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(tenantResourceClient);
        when(tenantResourceClient.getById(anyLong())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getTenantById(2L);
    }

    @Test(expected = SystemException.class)
    public void testGetTenantByNameTokenExpiration() throws Exception {
        TenantResourceClient tenantResourceClient = Mockito.mock(TenantResourceClient.class);

        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(tenantResourceClient);
        when(tenantResourceClient.get(anyString(), any(), any(), anyBoolean(), anyBoolean())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getTenantByName("name");
    }

    @Test(expected = SystemException.class)
    public void testGetAllTokenExpiration() throws Exception {
        TenantResourceClient tenantResourceClient = Mockito.mock(TenantResourceClient.class);

        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(tenantResourceClient);
        when(tenantResourceClient.getAll(anyString(), anyInt(), anyInt(), any(), anyBoolean())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.getAll("name", 1, 10, null, false);
    }

    @Test(expected = SystemException.class)
    public void testCreateTokenExpiration() throws Exception {
        TenantResourceClient tenantResourceClient = Mockito.mock(TenantResourceClient.class);

        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(tenantResourceClient);
        when(tenantResourceClient.create(any())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.create(new Tenant());
    }

    @Test(expected = SystemException.class)
    public void testDeleteTokenExpiration() throws Exception {
        TenantResourceClient tenantResourceClient = Mockito.mock(TenantResourceClient.class);

        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(tenantResourceClient);
        when(tenantResourceClient.delete(anyLong())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.delete(2L);
    }

    @Test(expected = SystemException.class)
    public void testDeleteHierarchyTokenExpiration() throws Exception {
        TenantResourceClient tenantResourceClient = Mockito.mock(TenantResourceClient.class);

        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(tenantResourceClient);
        when(tenantResourceClient.deleteTenantHierarchy(anyLong())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.deleteTenantHierarchy(2L);
    }

    @Test(expected = SystemException.class)
    public void testUpdateTokenExpiration() throws Exception {
        TenantResourceClient tenantResourceClient = Mockito.mock(TenantResourceClient.class);

        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(tenantResourceClient);
        when(tenantResourceClient.update(anyLong(), any())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        Tenant tenant = new Tenant();
        tenant.setId(2L);

        target.update(tenant);
    }

    @Test(expected = SystemException.class)
    public void testExistsTokenExpiration() throws Exception {
        TenantResourceClient tenantResourceClient = Mockito.mock(TenantResourceClient.class);

        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(tenantResourceClient);
        when(tenantResourceClient.exists(anyLong())).thenThrow(new TokenExpiredException("test"));

        when(authorizationChecker.getUserClient()).thenReturn(userClient);
        when(tokensPlaceHolder.getRefreshToken()).thenReturn("test");
        when(userClient.refreshToken(anyString())).thenReturn(Response.ok().entity("test").build());

        target.isTenantExistent(2L);
    }
}