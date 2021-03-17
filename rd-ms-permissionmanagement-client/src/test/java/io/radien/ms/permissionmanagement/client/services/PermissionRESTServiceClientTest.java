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
import io.radien.api.util.FactoryUtilService;
import io.radien.exception.SystemException;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
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

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

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

    private String getPermissionManagementUrl(){
        String url = "";
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT)).thenReturn(url);
        return url;
    }

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

    @Test
    public void testGetPermissionByIdWithResults() throws Exception {
        Permission p = PermissionFactory.create("permission-a", 1L, null,2l);
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

    @Test
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
        boolean success = false;
        try {
            target.getPermissionByName(a);
        } catch (Exception e){
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void testGetPermissionByNameExtensionException() throws Exception {
        boolean success = false;
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenThrow(new ExtensionException(new Exception()));
        try {
            target.getPermissionByName("a");
        }catch (SystemException se){
        	if (se.getMessage().contains(ExtensionException.class.getName())) {
        		success = true;
        	}
        }
        assertTrue(success);
    }

    @Test
    public void testGetPermissionByNameProcessingException() throws Exception {
        boolean success = false;
        String a = "a";
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(resourceClient.getPermissions(a,null,null,true,true))
                .thenThrow(new ProcessingException("test"));
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);

        try {
            target.getPermissionByName(a);
        }
        catch (SystemException se){
        	if (se.getMessage().contains(ProcessingException.class.getName())) {
        		success = true;
        	}
        }
        assertTrue(success);
    }
    @Test
    public void testCreate() throws SystemException, MalformedURLException {
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(resourceClient.save(any())).thenReturn(Response.ok().build());
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);
        boolean success = false;
        assertTrue(target.create(new Permission()));
        assertFalse(success);
    }
    @Test
    public void testCreateFail() throws MalformedURLException, SystemException {
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(resourceClient.save(any())).thenReturn(Response.serverError().entity("test error msg").build());
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);
        boolean success = false;
        assertFalse(target.create(new Permission()));
        assertFalse(success);
    }

    @Test
    public void testCreateProcessingException() throws MalformedURLException {
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(resourceClient.save(any())).thenThrow(new ProcessingException(""));
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);
        boolean success = false;
        try {
            target.create(new Permission());
        }catch (ProcessingException | SystemException es){
            success = true;
        }
        assertTrue(success);

    }

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

    @Test
    public void testAccessingOAF() {
        OAFAccess oafAccess = target.getOAF();
        assertNotNull(oafAccess);
    }


    @Test
    public void testGetPermissionByIdExtensionException() throws Exception {
        boolean success = false;
        when(clientServiceUtil.getPermissionResourceClient(
                getPermissionManagementUrl())).thenThrow(new ExtensionException(new Exception()));
        try {
            target.getPermissionById(1L);
        }catch (SystemException se){
            if (se.getMessage().contains(ExtensionException.class.getName())) {
                success = true;
            }
        }
        assertTrue(success);
    }

    @Test
    public void testGetPermissionByIdProcessingException() throws Exception {
        boolean success = false;
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(resourceClient.getById(anyLong()))
                .thenThrow(new ProcessingException("test"));
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);

        try {
            target.getPermissionById(1L);
        }
        catch (SystemException se){
            if (se.getMessage().contains(ProcessingException.class.getName())) {
                success = true;
            }
        }
        assertTrue(success);
    }

    @Test
    public void testGetPermissionByActionAndResourceWithResults() throws Exception {
        java.util.List<Permission> inputList = new java.util.ArrayList<>();
        inputList.add(PermissionFactory.create("permission-a", 1L, 2l, 2l));
        inputList.add(PermissionFactory.create("permission-b", 1L, 2l, 3l));

        JsonArray jsonArray = PermissionModelMapper.map(inputList);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonArray.toString().getBytes());
        Response expectedResponse = Response.ok(inputStream).build();

        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getPermissions(null, 1L, 2l, true, true)).
                thenReturn(expectedResponse);

        List<? extends SystemPermission> result =
                target.getPermissionByActionAndResource(1L, 2l);
        assertNotNull(result);
        assertTrue(!result.isEmpty());

        assertEquals(result.size(), 2);
    }

    @Test
    public void testGetPermissionByActionAndResourceWithCommunicationBreakdown() throws Exception {
        MalformedURLException malformedURLException =
                new MalformedURLException("Error accessing permission microservice");
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).
                thenThrow(malformedURLException);
        SystemException se = assertThrows(SystemException.class,
                () -> target.getPermissionByActionAndResource(1l, 2l));
        assertNotNull(se);
        assertTrue(se.getMessage().contains(malformedURLException.getMessage()));
    }

    @Test
    public void testGetPermissionByActionAndResourceWithProcessingException() throws Exception {
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getPermissions(null, 1L, 2l, true, true))
                .thenThrow(new ProcessingException(new Exception()));
        SystemException se = assertThrows(SystemException.class,
                () -> target.getPermissionByActionAndResource(1l, 2l));
        assertNotNull(se);
        assertTrue(se.getMessage().contains(ProcessingException.class.getName()));
    }

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

    @Test
    public void testPermissionExistsWithProcessingException() throws MalformedURLException {
        Long permissionId = 1111L;
        String permissionName = "add-tenant";
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).
                thenReturn(resourceClient);
        when(resourceClient.exists(permissionId, permissionName)).
                thenThrow(new ProcessingException("Error invoking endpoint"));
        assertThrows(SystemException.class,
                () -> target.isPermissionExistent(permissionId, permissionName));
    }

    /** Testing regarding "hasPermission(...)" possible scenarios  */

    /** Testing that were missing  */

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
        assertEquals(page.getResults().size(), 3);
    }

    @Test
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

        assertThrows(SystemException.class, () -> target.getAll(null, 1, 100, null, true));
    }

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

    @Test
    public void testGetPermissionsWhenFailure() throws MalformedURLException {
        List<String> sortBy = new ArrayList<>();
        Response errorResponse = Response.status(500).build();
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).
                thenThrow(new ProcessingException("error"));
        when(resourceClient.getAll("contract", 1, 100, sortBy, true)).
                then(i -> errorResponse);
        assertThrows(SystemException.class, () -> target.getPermissions("contract", 1, 100, sortBy, true));
    }

    @Test
    public void testDelete() throws MalformedURLException, SystemException {
        Long idOK = 1L;
        Long idNOK = 2L;
        Response OK = Response.status(200).build();
        Response NOK = Response.status(500).build();

        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).
                then(i -> resourceClient);
        when(resourceClient.delete(idNOK)).then(i -> NOK);
        when(resourceClient.delete(idOK)).then(i -> OK);

        assertEquals(target.delete(idOK), true);
        assertEquals(target.delete(idNOK), false);
    }

    @Test
    public void testDeleteWithFailure() throws MalformedURLException, SystemException {
        PermissionResourceClient resourceClient = Mockito.mock(PermissionResourceClient.class);
        when(clientServiceUtil.getPermissionResourceClient(getPermissionManagementUrl())).
                then(i -> resourceClient);
        when(resourceClient.delete(1L)).thenThrow(new ProcessingException("error during delete process"));
        assertThrows(SystemException.class, () -> target.delete(1L));
    }

    @Test
    public void testGetTotalRecordsCount() throws MalformedURLException {
        Permission permission = PermissionFactory.create("test", null,null, null);

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

        assertThrows(SystemException.class, () -> target.getTotalRecordsCount());
    }
}