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
import io.radien.api.model.docmanagement.mixindefinition.SystemMixinDefinition;
import io.radien.exception.RestResponseException;
import io.radien.exception.SystemException;
import io.radien.ms.doctypemanagement.client.entities.MixinDefinitionDTO;
import io.radien.ms.doctypemanagement.client.util.ClientServiceUtil;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MixinDefinitionRESTServiceClientTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    @InjectMocks
    private MixinDefinitionRESTServiceClient serviceClient;
    @Mock
    private OAFAccess oaf;
    @Mock
    private ClientServiceUtil clientServiceUtil;

    private MixinDefinitionResourceClient client;

    @Before
    public void setUp() throws Exception {
        when(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_DOCTYPEMANAGEMENT))
                .thenReturn("http://a.valid.url");
        client = mock(MixinDefinitionResourceClient.class);
        when(clientServiceUtil.getMixinDefinitionClient(anyString()))
                .thenReturn(client);
    }

    @Test
    public void testGetAll() throws JsonProcessingException, SystemException {
        Page<? extends SystemMixinDefinition<Long>> page = new Page<>();
        page.setTotalPages(1);
        page.setTotalResults(1);
        page.setCurrentPage(1);
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = new ByteArrayInputStream(mapper.writeValueAsString(page).getBytes(StandardCharsets.UTF_8));
        Response response = Response.ok().entity(is).build();
        when(client.getAll(anyString(), anyInt(), anyInt(), anyList(), anyBoolean()))
                .thenReturn(response);
        Page<? extends SystemMixinDefinition<Long>> result = serviceClient.getAll("", 1, 1, new ArrayList<>(), false);
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getTotalResults());
        assertEquals(1, result.getCurrentPage());
    }

    @Test(expected = RestResponseException.class)
    public void testGetAllError() throws SystemException {
        Response response = Response.status(Response.Status.BAD_REQUEST).entity("error").build();
        when(client.getAll(anyString(), anyInt(), anyInt(), anyList(), anyBoolean()))
                .thenReturn(response);
        serviceClient.getAll("", 1, 1, new ArrayList<>(), false);
    }

    @Test(expected = SystemException.class)
    public void testGetAllException() throws SystemException, MalformedURLException {
        when(clientServiceUtil.getMixinDefinitionClient(anyString()))
                .thenThrow(new MalformedURLException());
        Response response = Response.status(Response.Status.BAD_REQUEST).entity("error").build();
        when(client.getAll(anyString(), anyInt(), anyInt(), anyList(), anyBoolean()))
                .thenReturn(response);
        serviceClient.getAll("", 1, 1, new ArrayList<>(), false);
    }

    @Test
    public void testGetMixinDefinitionById() throws JsonProcessingException, SystemException {
        SystemMixinDefinition<Long> definition = new MixinDefinitionDTO();
        definition.setName("name");
        definition.setPropertyDefinitions(new ArrayList<>());
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = new ByteArrayInputStream(mapper.writeValueAsString(definition).getBytes(StandardCharsets.UTF_8));
        Response response = Response.ok().entity(is).build();
        when(client.getById(anyLong()))
                .thenReturn(response);
        Optional<SystemMixinDefinition<Long>> result = serviceClient.getMixinDefinitionById(1L);
        assertTrue(result.isPresent());
        assertEquals("name", result.get().getName());
    }

    @Test
    public void testGetMixinDefinitionByIdNotFound() throws SystemException {
        Response response = Response.status(Response.Status.NOT_FOUND).entity("not found").build();
        when(client.getById(anyLong()))
                .thenReturn(response);
        Optional<SystemMixinDefinition<Long>> result = serviceClient.getMixinDefinitionById(1L);
        assertFalse(result.isPresent());
    }

    @Test(expected = SystemException.class)
    public void testGetMixinDefinitionByIdException() throws SystemException, MalformedURLException {
        when(clientServiceUtil.getMixinDefinitionClient(anyString()))
                .thenThrow(new MalformedURLException());
        Response response = Response.status(Response.Status.BAD_REQUEST).entity("error").build();
        when(client.getById(anyLong()))
                .thenReturn(response);
        serviceClient.getMixinDefinitionById(1L);
    }

    @Test
    public void testSave() throws JsonProcessingException, SystemException {
        SystemMixinDefinition<Long> definition = new MixinDefinitionDTO();
        definition.setName("name");
        Response response = Response.ok().build();
        when(client.save(any()))
                .thenReturn(response);
        assertTrue(serviceClient.save(definition));
    }

    @Test
    public void testSaveError() throws SystemException {
        SystemMixinDefinition<Long> definition = new MixinDefinitionDTO();
        Response response = Response.status(Response.Status.BAD_REQUEST).entity("bad request").build();
        when(client.save(any()))
                .thenReturn(response);
        assertFalse(serviceClient.save(definition));
    }

    @Test(expected = SystemException.class)
    public void testSaveException() throws SystemException, MalformedURLException {
        when(clientServiceUtil.getMixinDefinitionClient(anyString()))
                .thenThrow(new MalformedURLException());
        serviceClient.save(null);
    }

    @Test
    public void testDeleteMixinDefinition() throws SystemException {
        Response response = Response.ok().build();
        when(client.delete(anyLong()))
                .thenReturn(response);
        assertTrue(serviceClient.deleteMixinDefinition(1L));
    }

    @Test
    public void testDeleteMixinDefinitionError() throws SystemException {
        Response response = Response.status(Response.Status.NOT_FOUND).entity("not found").build();
        when(client.delete(anyLong()))
                .thenReturn(response);
        assertFalse(serviceClient.deleteMixinDefinition(1L));
    }

    @Test(expected = SystemException.class)
    public void testDeleteMixinDefinitionException() throws SystemException, MalformedURLException {
        when(clientServiceUtil.getMixinDefinitionClient(anyString()))
                .thenThrow(new MalformedURLException());
        serviceClient.deleteMixinDefinition(1L);
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
        when(clientServiceUtil.getMixinDefinitionClient(anyString()))
                .thenThrow(new MalformedURLException());
        serviceClient.getTotalRecordsCount();
    }
}