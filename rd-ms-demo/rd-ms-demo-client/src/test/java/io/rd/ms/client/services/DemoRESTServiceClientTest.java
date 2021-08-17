/*
 * Copyright (c) 2016-present radien.io & its legal owners. All rights reserved.
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
package io.rd.ms.client.services;

import io.rd.api.OAFAccess;
import io.rd.api.OAFProperties;
import io.rd.api.entity.Page;
import io.rd.api.model.SystemDemo;
import io.rd.exception.SystemException;
import io.rd.ms.client.entities.Demo;
import io.rd.ms.client.util.ClientServiceUtil;
import io.rd.ms.client.util.DemoModelMapper;

import javax.ejb.EJBException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Response;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class DemoRESTServiceClientTest {
    @InjectMocks
    DemoRESTServiceClient demoRESTServiceClient;

    @Mock
    ClientServiceUtil clientServiceUtil;
    @Mock
    OAFAccess oafAccess;

    Demo demo = new Demo();
    private Demo demo1 = new Demo();
    DemoResourceClient resourceClient = Mockito.mock(DemoResourceClient.class);

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
        demo1.setId(1L);
        demo1.setName("name-1");
    }

    private String getDemoManagementUrl(){
        String url = "http://demoManagement";
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_DEMOMANAGEMENT)).thenReturn(url);
        return url;
    }

    @Test
    public void getOAF_test() {
        assertEquals(oafAccess,demoRESTServiceClient.getOAF());
    }

    @Test
    public void save_test() throws MalformedURLException {
        when(resourceClient.save(demo)).thenReturn(Response.ok().build());
        when(clientServiceUtil.getDemoResourceClient(getDemoManagementUrl())).thenReturn(resourceClient);
        boolean success = false;
        try {
            assertTrue(demoRESTServiceClient.save(demo));
        } catch (Exception e) {
            success = true;
        }
        assertFalse(success);
    }

    @Test
    public void save_fail_test() throws MalformedURLException {
        when(resourceClient.save(demo)).thenReturn(Response.serverError().entity("error msg").build());
        when(clientServiceUtil.getDemoResourceClient(any())).thenReturn(resourceClient);

        boolean success = false;
        try {
            assertFalse(demoRESTServiceClient.save(demo));
        } catch (Exception e) {
            success = true;
        }
        assertFalse(success);
    }

    @Test
    public void save_exception_test() throws MalformedURLException {
        when(resourceClient.save(demo1)).thenReturn(Response.serverError().entity("error msg").build());
        when(clientServiceUtil.getDemoResourceClient(any())).thenThrow(MalformedURLException.class);
        assertFalse(demoRESTServiceClient.save(new Demo()));
    }

    @Test
    public void getAll_test() throws MalformedURLException, SystemException {
        List<Demo> list = new ArrayList<>();
        list.add(DemoFactory.create("name-1"));
        Page<SystemDemo> page = new Page<>(list, 1, 1, 1);

        JsonObjectBuilder objectBuilder = Json.createObjectBuilder()
                .add("currentPage", page.getCurrentPage())
                .add("totalResults", page.getTotalResults())
                .add("totalPages", page.getTotalPages())
                .add("results", DemoModelMapper.map(list));

        JsonObject jsonObject = objectBuilder.build();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonObject.toString().getBytes());
        Response expectedResponse = Response.ok(inputStream).build();

        when(clientServiceUtil.getDemoResourceClient(getDemoManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getAll(any(), anyInt(), anyInt(), any(), anyBoolean()))
                .thenReturn(expectedResponse);

        Page<? extends SystemDemo> receivedPage = demoRESTServiceClient.getAll(null, 1, 10, null, false);

        assertEquals(1, receivedPage.getTotalPages());
        assertEquals(1, receivedPage.getCurrentPage());
        assertEquals(1, receivedPage.getTotalResults());
    }

    @Test
    public void getAll_exception_test() throws MalformedURLException, SystemException {
        List<Demo> list = new ArrayList<>();
        list.add(DemoFactory.create("name-1"));
        Page<SystemDemo> page = new Page<>(list, 100, 100, 1000);

        JsonObjectBuilder objectBuilder = Json.createObjectBuilder()
                .add("currentPage", page.getCurrentPage())
                .add("totalResults", page.getTotalResults())
                .add("totalPages", page.getTotalPages())
                .add("results", DemoModelMapper.map(list));

        JsonObject jsonObject = objectBuilder.build();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonObject.toString().getBytes());
        Response expectedResponse = Response.ok(inputStream).build();

        when(resourceClient.getAll(any(), anyInt(), anyInt(), any(), anyBoolean())).thenReturn(Response.serverError().entity("error msg").build());
        when(clientServiceUtil.getDemoResourceClient(any())).thenThrow(MalformedURLException.class);

        assertNotNull(demoRESTServiceClient.getAll(null, 1, 10, null, false));

    }

    @Test
    public void deleteDemo_test() throws MalformedURLException {
        when(resourceClient.delete(demo1.getId())).thenReturn(Response.ok().build());
        when(clientServiceUtil.getDemoResourceClient(getDemoManagementUrl())).thenReturn(resourceClient);

        boolean success = false;
        try {
            assertTrue(demoRESTServiceClient.deleteDemo(1L));
        } catch (Exception e) {
            success = true;
        }
        assertFalse(success);
    }

    @Test
    public void deleteDemo_fail_test() throws MalformedURLException {
        when(resourceClient.delete(demo1.getId())).thenReturn(Response.serverError().entity("error msg").build());
        when(clientServiceUtil.getDemoResourceClient(any())).thenReturn(resourceClient);

        boolean success = false;
        try {
            assertFalse(demoRESTServiceClient.deleteDemo(demo1.getId()));
        } catch (Exception e) {
            success = true;
        }
        assertFalse(success);
    }

    @Test
    public void deleteDemo_exception_test() throws MalformedURLException {
        when(resourceClient.delete(demo1.getId())).thenReturn(Response.serverError().entity("error msg").build());
        when(clientServiceUtil.getDemoResourceClient(any())).thenThrow(MalformedURLException.class);
        assertFalse(demoRESTServiceClient.deleteDemo(5L));
    }

}
