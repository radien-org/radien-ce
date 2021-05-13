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
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.util.FactoryUtilService;
import io.radien.exception.SystemException;
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
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * @author Bruno Gama
 */
public class TenantRESTServiceClientTest {

    @InjectMocks
    TenantRESTServiceClient target;

    @Mock
    ClientServiceUtil tenantServiceUtil;

    @Mock
    OAFAccess oafAccess;

    private Tenant dummyTenant = new Tenant();

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);

        dummyTenant.setId(2L);
        dummyTenant.setTenantType(TenantType.ROOT_TENANT);
        dummyTenant.setTenantKey("tenantKey");
        dummyTenant.setName("name");
    }

    private String getTenantManagementUrl(){
        String url = "";
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_TENANTMANAGEMENT)).thenReturn(url);
        return url;
    }

    @Test
    public void testGetTenantById() throws MalformedURLException, ParseException, SystemException {
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

        when(tenantResourceClient.get(any(),any(),anyBoolean(),anyBoolean()))
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


    @Test
    public void testIsTenantExistent() throws MalformedURLException, SystemException {
        TenantResourceClient tenantResourceClient = Mockito.mock(TenantResourceClient.class);
        when(tenantResourceClient.exists(any())).thenReturn(Response.ok().build());
        when(tenantServiceUtil.getTenantResourceClient(getTenantManagementUrl())).thenReturn(tenantResourceClient);
        dummyTenant.setName("name-update");

        assertTrue(target.isTenantExistent(dummyTenant.getId()));
    }

    @Test
    public void testIsTenantExistentReturnFalseResponse() throws MalformedURLException, SystemException {
        TenantResourceClient tenantResourceClient = Mockito.mock(TenantResourceClient.class);
        when(tenantResourceClient.exists(any())).thenReturn(Response.serverError().build());
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
}