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
package ${package}.ms.client.services;

import ${package}.api.OAFAccess;
import ${package}.api.OAFProperties;
import ${package}.api.entity.Page;
import ${package}.api.model.System${entityResourceName};
import ${package}.ms.client.entities.${entityResourceName};
import ${package}.ms.client.util.ClientServiceUtil;
import ${package}.ms.client.util.${entityResourceName}ModelMapper;

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

public class ${entityResourceName}RESTServiceClientTest {
    @InjectMocks
    ${entityResourceName}RESTServiceClient ${entityResourceName.toLowerCase()}RESTServiceClient;

    @Mock
    ClientServiceUtil clientServiceUtil;
    @Mock
    OAFAccess oafAccess;

    ${entityResourceName} ${entityResourceName.toLowerCase()} = new ${entityResourceName}();
    private ${entityResourceName} ${entityResourceName.toLowerCase()}1 = new ${entityResourceName}();
    ${entityResourceName}ResourceClient resourceClient = Mockito.mock(${entityResourceName}ResourceClient.class);

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
        ${entityResourceName.toLowerCase()}1.setId(1L);
        ${entityResourceName.toLowerCase()}1.setName("name-1");
    }

    private String get${entityResourceName}ManagementUrl(){
        String url = "http://${entityResourceName.toLowerCase()}Management";
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_${entityResourceName.toUpperCase()}MANAGEMENT)).thenReturn(url);
        return url;
    }

    @Test
    public void getOAF_test() {
        assertEquals(oafAccess,${entityResourceName.toLowerCase()}RESTServiceClient.getOAF());
    }

    @Test
    public void save_test() throws MalformedURLException {
        when(resourceClient.save(${entityResourceName.toLowerCase()})).thenReturn(Response.ok().build());
        when(clientServiceUtil.get${entityResourceName}ResourceClient(get${entityResourceName}ManagementUrl())).thenReturn(resourceClient);
        boolean success = false;
        try {
            assertTrue(${entityResourceName.toLowerCase()}RESTServiceClient.save(${entityResourceName.toLowerCase()}));
        } catch (Exception e) {
            success = true;
        }
        assertFalse(success);
    }

    @Test
    public void save_fail_test() throws MalformedURLException {
        when(resourceClient.save(${entityResourceName.toLowerCase()})).thenReturn(Response.serverError().entity("error msg").build());
        when(clientServiceUtil.get${entityResourceName}ResourceClient(any())).thenReturn(resourceClient);

        boolean success = false;
        try {
            assertFalse(${entityResourceName.toLowerCase()}RESTServiceClient.save(${entityResourceName.toLowerCase()}));
        } catch (Exception e) {
            success = true;
        }
        assertFalse(success);
    }

    @Test
    public void save_exception_test() throws MalformedURLException {
        when(resourceClient.save(${entityResourceName.toLowerCase()}1)).thenReturn(Response.serverError().entity("error msg").build());
        when(clientServiceUtil.get${entityResourceName}ResourceClient(any())).thenThrow(MalformedURLException.class);
        ${entityResourceName.toLowerCase()}RESTServiceClient.save(new ${entityResourceName}());
    }

    @Test
    public void getAll_test() throws MalformedURLException {
        List<${entityResourceName}> list = new ArrayList<>();
        list.add(${entityResourceName}Factory.create("name-1"));
        Page<System${entityResourceName}> page = new Page<>(list, 1, 1, 1);

        JsonObjectBuilder objectBuilder = Json.createObjectBuilder()
                .add("currentPage", page.getCurrentPage())
                .add("totalResults", page.getTotalResults())
                .add("totalPages", page.getTotalPages())
                .add("results", ${entityResourceName}ModelMapper.map(list));

        JsonObject jsonObject = objectBuilder.build();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonObject.toString().getBytes());
        Response expectedResponse = Response.ok(inputStream).build();

        when(clientServiceUtil.get${entityResourceName}ResourceClient(get${entityResourceName}ManagementUrl())).thenReturn(resourceClient);
        when(resourceClient.getAll(any(), anyInt(), anyInt(), any(), anyBoolean()))
                .thenReturn(expectedResponse);

        Page<? extends System${entityResourceName}> receivedPage = ${entityResourceName.toLowerCase()}RESTServiceClient.getAll(null, 1, 10, null, false);

        assertEquals(1, receivedPage.getTotalPages());
        assertEquals(1, receivedPage.getCurrentPage());
        assertEquals(1, receivedPage.getTotalResults());
    }

    @Test
    public void getAll_exception_test() throws MalformedURLException {
        List<${entityResourceName}> list = new ArrayList<>();
        list.add(${entityResourceName}Factory.create("name-1"));
        Page<System${entityResourceName}> page = new Page<>(list, 100, 100, 1000);

        JsonObjectBuilder objectBuilder = Json.createObjectBuilder()
                .add("currentPage", page.getCurrentPage())
                .add("totalResults", page.getTotalResults())
                .add("totalPages", page.getTotalPages())
                .add("results", ${entityResourceName}ModelMapper.map(list));

        JsonObject jsonObject = objectBuilder.build();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonObject.toString().getBytes());
        Response expectedResponse = Response.ok(inputStream).build();

        when(resourceClient.getAll(any(), anyInt(), anyInt(), any(), anyBoolean())).thenReturn(Response.serverError().entity("error msg").build());
        when(clientServiceUtil.get${entityResourceName}ResourceClient(any())).thenThrow(MalformedURLException.class);

        Page<? extends System${entityResourceName}> receivedPage = ${entityResourceName.toLowerCase()}RESTServiceClient.getAll(null, 1, 10, null, false);

    }

    @Test
    public void delete${entityResourceName}_test() throws MalformedURLException {
        when(resourceClient.delete(${entityResourceName.toLowerCase()}1.getId())).thenReturn(Response.ok().build());
        when(clientServiceUtil.get${entityResourceName}ResourceClient(get${entityResourceName}ManagementUrl())).thenReturn(resourceClient);

        boolean success = false;
        try {
            assertTrue(${entityResourceName.toLowerCase()}RESTServiceClient.delete${entityResourceName}(1L));
        } catch (Exception e) {
            success = true;
        }
        assertFalse(success);
    }

    @Test
    public void delete${entityResourceName}_fail_test() throws MalformedURLException {
        when(resourceClient.delete(${entityResourceName.toLowerCase()}1.getId())).thenReturn(Response.serverError().entity("error msg").build());
        when(clientServiceUtil.get${entityResourceName}ResourceClient(any())).thenReturn(resourceClient);

        boolean success = false;
        try {
            assertFalse(${entityResourceName.toLowerCase()}RESTServiceClient.delete${entityResourceName}(${entityResourceName.toLowerCase()}1.getId()));
        } catch (Exception e) {
            success = true;
        }
        assertFalse(success);
    }

    @Test
    public void delete${entityResourceName}_exception_test() throws MalformedURLException {
        when(resourceClient.delete(${entityResourceName.toLowerCase()}1.getId())).thenReturn(Response.serverError().entity("error msg").build());
        when(clientServiceUtil.get${entityResourceName}ResourceClient(any())).thenThrow(MalformedURLException.class);
        ${entityResourceName.toLowerCase()}RESTServiceClient.delete${entityResourceName}(5L);
    }

}
