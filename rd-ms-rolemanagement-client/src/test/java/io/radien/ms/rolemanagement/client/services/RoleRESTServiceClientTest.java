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
import io.radien.api.model.role.SystemRole;
import io.radien.api.model.tenant.SystemContract;
import io.radien.api.util.FactoryUtilService;
import io.radien.exception.SystemException;
import io.radien.ms.rolemanagement.client.entities.Role;
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

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * @author Bruno Gama
 */
public class RoleRESTServiceClientTest {

    @InjectMocks
    RoleRESTServiceClient target;

    @Mock
    ClientServiceUtil roleServiceUtil;

    @Mock
    OAFAccess oafAccess;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
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

        RoleResourceClient roleResourceClient = Mockito.mock(RoleResourceClient.class);

        when(roleResourceClient.getAll(null,1, 10,  null, false)).thenReturn(response);

        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenReturn(roleResourceClient);

        List<? extends SystemRole> list = new ArrayList<>();

        List<? extends SystemRole> returnedList = target.getAll(null, 1, 10, null, false).getResults();

        assertEquals(list, returnedList);
    }

    @Test
    public void testGetAllException() throws Exception {
        boolean success = false;
        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenThrow(new MalformedURLException());
        try {
            target.getAll(null,1, 10, null, false);
        }catch (SystemException se){
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void testGetRolesByDescription() throws MalformedURLException, SystemException {
        String a = "a";

        JsonArrayBuilder builder = Json.createArrayBuilder();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JsonWriter jsonWriter = Json.createWriter(baos);
        jsonWriter.writeArray(builder.build());
        jsonWriter.close();

        InputStream is = new ByteArrayInputStream(baos.toByteArray());

        Response response = Response.ok(is).build();

        RoleResourceClient roleResourceClient = Mockito.mock(RoleResourceClient.class);

        when(roleResourceClient.getSpecificRoles(any(), any(), anyBoolean(), anyBoolean())).thenReturn(response);

        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenReturn(roleResourceClient);

        List<? extends SystemRole> list = new ArrayList<>();

        assertEquals(list, target.getRolesByDescription(a));
    }

    @Test
    public void testGetRolesByDescriptionException() throws Exception {
        boolean success = false;
        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenThrow(new ExtensionException(new Exception()));
        try {
            target.getRolesByDescription("A");
        }catch (SystemException se){
            if (se.getMessage().contains(ExtensionException.class.getName())) {
                success = true;
            }
        }
        assertTrue(success);
    }

    private String getPermissionManagementUrl(){
        String url = "";
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ROLEMANAGEMENT)).thenReturn(url);
        return url;
    }

    @Test
    public void testCreate() throws MalformedURLException {
        RoleResourceClient resourceClient = Mockito.mock(RoleResourceClient.class);
        when(resourceClient.save(any())).thenReturn(Response.ok().build());
        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenReturn(resourceClient);
        boolean success = false;
        try {
			assertTrue(target.create(new Role()));
		} catch (SystemException e) {
			success = true;
        }
        assertFalse(success);
    }

    @Test
    public void testCreateMalformedException() throws MalformedURLException {
        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenThrow(new MalformedURLException());
        boolean success = false;
        try {
            assertTrue(target.create(new Role()));
        } catch(SystemException e) {
            success = true;
        }
        assertTrue(success);
    }

    @Test
    public void testCreateFail() throws MalformedURLException {
        RoleResourceClient roleResourceClient = Mockito.mock(RoleResourceClient.class);
        when(roleResourceClient.save(any())).thenReturn(Response.serverError().entity("test error msg").build());
        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenReturn(roleResourceClient);
        boolean success = false;
        try {
            assertFalse(target.create(new Role()));
        } catch (SystemException e) {
            success = true;
        }
        assertFalse(success);
    }

    @Test
    public void testCreateProcessingException() throws MalformedURLException {
        RoleResourceClient roleResourceClient = Mockito.mock(RoleResourceClient.class);
        when(roleResourceClient.save(any())).thenThrow(new ProcessingException(""));
        when(roleServiceUtil.getRoleResourceClient(getPermissionManagementUrl())).thenReturn(roleResourceClient);
        boolean success = false;
        try {
            target.create(new Role());
        }catch (ProcessingException | SystemException es){
            success = true;
        }
        assertTrue(success);

    }
}