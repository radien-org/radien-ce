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
}