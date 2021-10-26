/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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
package io.rd.microservice.ms.client.services;

import io.rd.microservice.api.OAFAccess;
import io.rd.microservice.api.OAFProperties;
import io.rd.microservice.api.entity.Page;
import io.rd.microservice.api.model.SystemMicroservice;
import io.rd.microservice.ms.client.entities.Microservice;
import io.rd.microservice.ms.client.util.ClientServiceUtil;
import io.rd.microservice.ms.client.util.MicroserviceModelMapper;

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

public class MicroserviceRESTServiceClientTest {
    @InjectMocks
    MicroserviceRESTServiceClient microserviceRESTServiceClient;

    @Mock
    ClientServiceUtil clientServiceUtil;
    @Mock
    OAFAccess oafAccess;

    Microservice microservice = new Microservice();
    private Microservice microservice1 = new Microservice();
    MicroserviceResourceClient resourceClient = Mockito.mock(MicroserviceResourceClient.class);

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
        microservice1.setId(1L);
        microservice1.setName("name-1");
    }

    private String getMicroserviceManagementUrl(){
        String url = "http://microserviceManagement";
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_MICROSERVICEMANAGEMENT)).thenReturn(url);
        return url;
    }

    @Test
    public void getOAF_test() {
        assertEquals(oafAccess,microserviceRESTServiceClient.getOAF());
    }

    @Test
    public void save_test() throws MalformedURLException {
        when(resourceClient.save(microservice)).thenReturn(Response.ok().build());
        when(clientServiceUtil.getMicroserviceResourceClient(getMicroserviceManagementUrl())).thenReturn(resourceClient);
        boolean success = false;
        try {
            assertTrue(microserviceRESTServiceClient.save(microservice));
        } catch (Exception e) {
            success = true;
        }
        assertFalse(success);
    }

    @Test
    public void save_fail_test() throws MalformedURLException {
        when(resourceClient.save(microservice)).thenReturn(Response.serverError().entity("error msg").build());
        when(clientServiceUtil.getMicroserviceResourceClient(any())).thenReturn(resourceClient);

        boolean success = false;
        try {
            assertFalse(microserviceRESTServiceClient.save(microservice));
        } catch (Exception e) {
            success = true;
        }
        assertFalse(success);
    }

    @Test
    public void save_exception_test() throws MalformedURLException {
        when(resourceClient.save(microservice1)).thenReturn(Response.serverError().entity("error msg").build());
        when(clientServiceUtil.getMicroserviceResourceClient(any())).thenThrow(MalformedURLException.class);
        microserviceRESTServiceClient.save(new Microservice());
    }

    @Test
    public void getAll_test() throws MalformedURLException {
        List<Microservice> list = new ArrayList<>();
        list.add(MicroserviceFactory.create("name-1"));
        Page<SystemMicroservice> page = new Page<>(list, 1, 1, 1);

        JsonObjectBuilder objectBuilder = Json.createObjectBuilder()
                .add("currentPage", page.getCurrentPage())
                .add("totalResults", page.getTotalResults())
                .add("totalPages", page.getTotalPages())
                .add("results", MicroserviceModelMapper.map(list));

        JsonObject jsonObject = objectBuilder.build();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonObject.toString().getBytes());
        Response expectedResponse = Response.ok(inputStream).build();

        when(clientServiceUtil.getMicroserviceResourceClient(getMicroserviceManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getAll(any(), anyInt(), anyInt(), any(), anyBoolean()))
                .thenReturn(expectedResponse);

        Page<? extends SystemMicroservice> receivedPage = microserviceRESTServiceClient.getAll(null, 1, 10, null, false);

        assertEquals(1, receivedPage.getTotalPages());
        assertEquals(1, receivedPage.getCurrentPage());
        assertEquals(1, receivedPage.getTotalResults());
    }

    @Test
    public void getAll_exception_test() throws MalformedURLException {
        List<Microservice> list = new ArrayList<>();
        list.add(MicroserviceFactory.create("name-1"));
        Page<SystemMicroservice> page = new Page<>(list, 100, 100, 1000);

        JsonObjectBuilder objectBuilder = Json.createObjectBuilder()
                .add("currentPage", page.getCurrentPage())
                .add("totalResults", page.getTotalResults())
                .add("totalPages", page.getTotalPages())
                .add("results", MicroserviceModelMapper.map(list));

        JsonObject jsonObject = objectBuilder.build();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonObject.toString().getBytes());
        Response expectedResponse = Response.ok(inputStream).build();

        when(resourceClient.getAll(any(), anyInt(), anyInt(), any(), anyBoolean())).thenReturn(Response.serverError().entity("error msg").build());
        when(clientServiceUtil.getMicroserviceResourceClient(any())).thenThrow(MalformedURLException.class);

        Page<? extends SystemMicroservice> receivedPage = microserviceRESTServiceClient.getAll(null, 1, 10, null, false);

    }

    @Test
    public void deleteMicroservice_test() throws MalformedURLException {
        when(resourceClient.delete(microservice1.getId())).thenReturn(Response.ok().build());
        when(clientServiceUtil.getMicroserviceResourceClient(getMicroserviceManagementUrl())).thenReturn(resourceClient);

        boolean success = false;
        try {
            assertTrue(microserviceRESTServiceClient.deleteMicroservice(1L));
        } catch (Exception e) {
            success = true;
        }
        assertFalse(success);
    }

    @Test
    public void deleteMicroservice_fail_test() throws MalformedURLException {
        when(resourceClient.delete(microservice1.getId())).thenReturn(Response.serverError().entity("error msg").build());
        when(clientServiceUtil.getMicroserviceResourceClient(any())).thenReturn(resourceClient);

        boolean success = false;
        try {
            assertFalse(microserviceRESTServiceClient.deleteMicroservice(microservice1.getId()));
        } catch (Exception e) {
            success = true;
        }
        assertFalse(success);
    }

    @Test
    public void deleteMicroservice_exception_test() throws MalformedURLException {
        when(resourceClient.delete(microservice1.getId())).thenReturn(Response.serverError().entity("error msg").build());
        when(clientServiceUtil.getMicroserviceResourceClient(any())).thenThrow(MalformedURLException.class);
        microserviceRESTServiceClient.deleteMicroservice(5L);
    }

}
