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
import io.radien.api.model.permission.SystemResource;
import io.radien.api.util.FactoryUtilService;
import io.radien.exception.SystemException;
import io.radien.ms.permissionmanagement.client.entities.Resource;
import io.radien.ms.permissionmanagement.client.util.ClientServiceUtil;
import io.radien.ms.permissionmanagement.client.util.ResourceModelMapper;
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
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * @author Newton Carvalho
 */
public class ResourceRESTServiceClientTest {

    @InjectMocks
    ResourceRESTServiceClient target;

    @Mock
    ClientServiceUtil clientServiceUtil;

    @Mock
    OAFAccess oafAccess;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetResourceByName() throws Exception {
        String resourceName = "resource-a";
        InputStream is = new ByteArrayInputStream("[]".getBytes());

        Response response = Response.ok(is).build();
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(resourceClient.getResources(resourceName,true,true)).thenReturn(response);
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);
        assertEquals(Optional.empty(),target.getResourceByName(resourceName));
    }

    private String getResourceManagementUrl(){
        String url = "";
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_PERMISSIONMANAGEMENT)).thenReturn(url);
        return url;
    }

    @Test
    public void testGetResourceByNameWithResults() throws Exception {
        String resourceName = "resource-aaa";
        Resource resource = ResourceFactory.create(resourceName, null);

        InputStream is = new ByteArrayInputStream(ResourceModelMapper.
                map(Arrays.asList(resource)).toString().getBytes());

        Response response = Response.ok(is).build();

        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(resourceClient.getResources(resourceName,true,true))
                .thenReturn(response);
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).
                thenReturn(resourceClient);

        assertTrue(target.getResourceByName(resourceName).isPresent());
    }

    @Test
    public void testGetResourceByIdWithResults() throws Exception {
        Resource resource = ResourceFactory.create("resource-a", 2l);
        resource.setId(11L);
        InputStream is = new ByteArrayInputStream(ResourceModelMapper.
                map(resource).toString().getBytes());
        Response response = Response.ok(is).build();
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getById(anyLong())).
                thenReturn(response);
        Optional<SystemResource> opt = target.getResourceById(resource.getId());
        assertNotNull(opt);
        assertTrue(opt.isPresent());
        assertEquals(opt.get().getId(), resource.getId());
    }

    @Test
    public void testGetResourceByNameNonUnique() throws SystemException, MalformedURLException {
        String resourceName = "resource-x";
        Resource r1 = ResourceFactory.create("resource-x1", null);
        Resource r2 = ResourceFactory.create("resource-x2", null);
        Resource r3 = ResourceFactory.create("resource-x3", null);
        InputStream is = new ByteArrayInputStream(ResourceModelMapper.
                map(Arrays.asList(r1, r2, r3)).toString().getBytes());

        Response response = Response.ok(is).build();

        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(resourceClient.getResources(resourceName,true,true))
                .thenReturn(response);
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);
        Optional<SystemResource> result = target.getResourceByName(resourceName);
        assertNotNull(result);
        assertFalse(result.isPresent());
    }

    @Test
    public void testGetResourceByNameExtensionException() throws Exception {
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenThrow(new ExtensionException(new Exception()));
        Exception e = assertThrows(SystemException.class, () -> target.getResourceByName("resource"));
        assertTrue(e.getMessage().contains(ExtensionException.class.getName()));
    }

    @Test
    public void testGetResourceByNameProcessingException() throws Exception {
        String resourceName = "resource-1";
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(resourceClient.getResources(resourceName,true,true))
                .thenThrow(new ProcessingException("test"));
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).
                thenReturn(resourceClient);
        Exception e = assertThrows(SystemException.class, ()-> target.getResourceByName(resourceName));
        assertTrue(e.getMessage().contains(ProcessingException.class.getName()));
    }
    @Test
    public void testCreate() throws MalformedURLException {
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(resourceClient.save(any())).thenReturn(Response.ok().build());
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);
        try {
            assertTrue(target.create(new Resource()));
        } catch (SystemException se) {
            fail("unexpected");
        }
    }
    @Test
    public void testCreateFail() throws MalformedURLException {
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(resourceClient.save(any())).thenReturn(Response.serverError().entity("test error msg").build());
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);
        try {
            assertFalse(target.create(new Resource()));
        } catch (SystemException se) {
            fail("unexpected");
        }
    }

    @Test
    public void testCreateProcessingException() throws MalformedURLException {
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(resourceClient.save(any())).thenThrow(new ProcessingException(""));
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).thenReturn(resourceClient);
        Exception e = assertThrows(SystemException.class, ()->target.create(new Resource()));
        assertTrue(e.getMessage().contains(ProcessingException.class.getSimpleName()));
    }

    @Test
    public void testCommunicationFail() throws MalformedURLException {
        MalformedURLException malformedURLException =
                new MalformedURLException("Error accessing permission microservice");
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).
                thenThrow(malformedURLException);
        SystemException se = assertThrows(SystemException.class, () -> target.create(new Resource()));
        assertNotNull(se);
        assertTrue(se.getMessage().contains(malformedURLException.getMessage()));
    }

    @Test
    public void testAccessingOAF() {
        OAFAccess oafAccess = target.getOAF();
        assertNotNull(oafAccess);
    }

    @Test
    public void testGetResourceByIdExtensionException() throws Exception {
        when(clientServiceUtil.getResourceResourceClient(
                getResourceManagementUrl())).thenThrow(new ExtensionException(new Exception()));
        Exception e = assertThrows(SystemException.class, ()->target.getResourceById(1L));
        assertTrue(e.getMessage().contains(ExtensionException.class.getName()));
    }

    @Test
    public void testGetResourceByIdProcessingException() throws Exception {
        ResourceResourceClient resourceClient = Mockito.mock(ResourceResourceClient.class);
        when(resourceClient.getById(anyLong()))
                .thenThrow(new ProcessingException("test"));
        when(clientServiceUtil.getResourceResourceClient(getResourceManagementUrl())).
                thenReturn(resourceClient);
        Exception e = assertThrows(SystemException.class, ()->target.getResourceById(1L));
        assertTrue(e.getMessage().contains(ProcessingException.class.getName())) ;
    }
}