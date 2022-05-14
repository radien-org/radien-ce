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

package io.radien.ms.doctypemanagement.client.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.api.model.docmanagement.propertydefinition.SystemPropertyDefinition;
import io.radien.exception.RestResponseException;
import io.radien.exception.SystemException;
import io.radien.ms.doctypemanagement.client.entities.PropertyDefinition;
import io.radien.ms.doctypemanagement.client.util.ClientServiceUtil;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PropertyDefinitionRESTServiceClientTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    private PropertyDefinitionRESTServiceClient serviceClient;

    @Mock private OAFAccess oaf;
    @Mock private ClientServiceUtil clientServiceUtil;

    private PropertyDefinitionResourceClient client;

    @Before
    public void init() throws MalformedURLException {
        when(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_DOCTYPEMANAGEMENT))
                .thenReturn("http://a.valid.url");
        client = mock(PropertyDefinitionResourceClient.class);
        when(clientServiceUtil.getPropertyDefinitionClient(anyString()))
                .thenReturn(client);
    }

    @Test
    public void testGetAll() throws JsonProcessingException, SystemException {
        Page<? extends SystemPropertyDefinition> page = new Page<>();
        page.setTotalPages(1);
        page.setTotalResults(1);
        page.setCurrentPage(1);
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = new ByteArrayInputStream(mapper.writeValueAsString(page).getBytes(StandardCharsets.UTF_8));
        Response response = Response.ok().entity(is).build();
        when(client.getAll(anyString(), anyInt(), anyInt(), anyList(), anyBoolean()))
                .thenReturn(response);
        Page<? extends SystemPropertyDefinition> result = serviceClient.getAll("", 1, 1, new ArrayList<>(), false);
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getTotalResults());
        assertEquals(1, result.getCurrentPage());
    }

    @Test(expected = RestResponseException.class)
    public void testGetAllError() throws SystemException {
        Response response = Response.status(Response.Status.BAD_REQUEST).entity("error").build();
        when(client.getAll(anyString(), anyInt(), anyInt(), anyList(), anyBoolean()))
                .thenReturn(response);
        Page<? extends SystemPropertyDefinition> result = serviceClient.getAll("", 1, 1, new ArrayList<>(), false);
    }

    @Test(expected = SystemException.class)
    public void testGetAllException() throws SystemException, MalformedURLException {
        when(clientServiceUtil.getPropertyDefinitionClient(anyString()))
                .thenThrow(new MalformedURLException());
        Response response = Response.status(Response.Status.BAD_REQUEST).entity("error").build();
        when(client.getAll(anyString(), anyInt(), anyInt(), anyList(), anyBoolean()))
                .thenReturn(response);
        serviceClient.getAll("", 1, 1, new ArrayList<>(), false);
    }

    @Test
    public void testGetPropertyDefinitionById() throws JsonProcessingException, SystemException {
        SystemPropertyDefinition definition = new PropertyDefinition();
        definition.setName("name");
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = new ByteArrayInputStream(mapper.writeValueAsString(definition).getBytes(StandardCharsets.UTF_8));
        Response response = Response.ok().entity(is).build();
        when(client.getById(anyLong()))
                .thenReturn(response);
        Optional<SystemPropertyDefinition> result = serviceClient.getPropertyDefinitionById(1L);
        assertTrue(result.isPresent());
        assertEquals("name", result.get().getName());
    }

    @Test
    public void testGetPropertyDefinitionByIdNotFound() throws SystemException {
        Response response = Response.status(Response.Status.NOT_FOUND).entity("not found").build();
        when(client.getById(anyLong()))
                .thenReturn(response);
        Optional<SystemPropertyDefinition> result = serviceClient.getPropertyDefinitionById(1L);
        assertFalse(result.isPresent());
    }

    @Test(expected = SystemException.class)
    public void testGetPropertyDefinitionByIdException() throws SystemException, MalformedURLException {
        when(clientServiceUtil.getPropertyDefinitionClient(anyString()))
                .thenThrow(new MalformedURLException());
        Response response = Response.status(Response.Status.BAD_REQUEST).entity("error").build();
        when(client.getById(anyLong()))
                .thenReturn(response);
        serviceClient.getPropertyDefinitionById(1L);
    }

    @Test
    public void testGetPropertyDefinitionNamesByIds() throws SystemException {
        Response response = Response.status(200)
                        .entity("Property Name, Property Name 2").build();
        when(client.getNameListByIds(anyList()))
                .thenReturn(response);

        String result = serviceClient.getPropertyDefinitionNamesByIds(Collections.singletonList(1L));
        assertEquals("Property Name, Property Name 2", result);
    }

    @Test(expected = RestResponseException.class)
    public void testGetPropertyDefinitionNamesByIdsNotFound() throws SystemException {
        Response response = Response.status(Response.Status.NOT_FOUND)
                        .entity("ID not found").build();
        when(client.getNameListByIds(anyList()))
                .thenReturn(response);

        serviceClient.getPropertyDefinitionNamesByIds(Collections.singletonList(1L));
    }

    @Test(expected = SystemException.class)
    public void testGetPropertyDefinitionNamesByIdsException() throws SystemException, MalformedURLException {
        when(clientServiceUtil.getPropertyDefinitionClient(anyString()))
                .thenThrow(new MalformedURLException());
        Response response = Response.status(Response.Status.BAD_REQUEST).entity("error").build();
        when(client.getNameListByIds(anyList()))
                .thenReturn(response);
        serviceClient.getPropertyDefinitionNamesByIds(Collections.singletonList(1L));
    }

    @Test
    public void testSave() throws JsonProcessingException, SystemException {
        SystemPropertyDefinition definition = new PropertyDefinition();
        definition.setName("name");
        Response response = Response.ok().build();
        when(client.save(any()))
                .thenReturn(response);
        assertTrue(serviceClient.save(definition));
    }

    @Test
    public void testSaveError() throws SystemException {
        SystemPropertyDefinition definition = new PropertyDefinition();
        Response response = Response.status(Response.Status.BAD_REQUEST).entity("bad request").build();
        when(client.save(any()))
                .thenReturn(response);
        assertFalse(serviceClient.save(definition));
    }

    @Test(expected = SystemException.class)
    public void testSaveException() throws SystemException, MalformedURLException {
        when(clientServiceUtil.getPropertyDefinitionClient(anyString()))
                .thenThrow(new MalformedURLException());
        serviceClient.save(null);
    }

    @Test
    public void testDeletePropertyDefinition() throws SystemException {
        Response response = Response.ok().build();
        when(client.delete(anyLong()))
                .thenReturn(response);
        assertTrue(serviceClient.deletePropertyDefinition(1L));
    }

    @Test
    public void testDeletePropertyDefinitionError() throws SystemException {
        Response response = Response.status(Response.Status.NOT_FOUND).entity("not found").build();
        when(client.delete(anyLong()))
                .thenReturn(response);
        assertFalse(serviceClient.deletePropertyDefinition(1L));
    }

    @Test(expected = SystemException.class)
    public void testDeletePropertyDefinitionException() throws SystemException, MalformedURLException {
        when(clientServiceUtil.getPropertyDefinitionClient(anyString()))
                .thenThrow(new MalformedURLException());
        serviceClient.deletePropertyDefinition(1L);
    }

    @Test
    public void testGetTotalRecordsCount() throws SystemException {
        Response response = Response.ok().entity("10").build();
        when(client.getTotalRecordsCount())
                .thenReturn(response);
        assertEquals(Long.valueOf("10"), serviceClient.getTotalRecordsCount());
    }

    @Test(expected = RestResponseException.class)
    public void testGetTotalRecordsCountError() throws SystemException {
        Response response = Response.status(Response.Status.NOT_FOUND).entity("not found").build();
        when(client.getTotalRecordsCount())
                .thenReturn(response);
        serviceClient.getTotalRecordsCount();
    }

    @Test(expected = SystemException.class)
    public void testGetTotalRecordsCountException() throws SystemException, MalformedURLException {
        when(clientServiceUtil.getPropertyDefinitionClient(anyString()))
                .thenThrow(new MalformedURLException());
        serviceClient.getTotalRecordsCount();
    }
}
