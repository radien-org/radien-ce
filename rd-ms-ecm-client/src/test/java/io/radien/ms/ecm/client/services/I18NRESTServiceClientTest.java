/*
 *
 *  * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
 *  * <p>
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  * <p>
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  * <p>
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.radien.ms.ecm.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.entity.Page;
import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.exception.SystemException;
import io.radien.ms.ecm.client.controller.I18NResource;
import io.radien.ms.ecm.client.entities.i18n.I18NProperty;
import io.radien.ms.ecm.client.util.ClientServiceUtil;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class I18NRESTServiceClientTest {
    @InjectMocks
    private I18NRESTServiceClient client;
    @Mock
    private ClientServiceUtil clientServiceUtil;
    @Mock
    private OAFAccess oaf;

    private I18NResource mockResource;

    @Before
    public void init() throws MalformedURLException {
        MockitoAnnotations.initMocks(this);
        when(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ECM))
                .thenReturn("localhost/ecm");
        mockResource = mock(I18NResource.class);
        when(clientServiceUtil.getI18NResourceClient(anyString()))
                .thenReturn(mockResource);
    }

    @Test
    public void testGetAll() throws SystemException {
        String page = "{\n" +
                "  \"results\": \n" +
                "  [\n" +
                "    {\n" +
                "      \"key\": \"key\",\n" +
                "      \"application\": \"radien\",\n" +
                "      \"translations\": \n" +
                "      [\n" +
                "        {\n" +
                "          \"value\": \"value\",\n" +
                "          \"language\": \"en\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"currentPage\": 1,\n" +
                "  \"totalResults\": 1,\n" +
                "  \"totalPages\": 1\n" +
                "}";
        InputStream stream = new ByteArrayInputStream(page.getBytes(StandardCharsets.UTF_8));
        Response response = Response.ok().entity(stream).build();
        when(mockResource.getAll(eq("radien"), eq(1), eq(10), anyList(), eq(true)))
                .thenReturn(response);
        Page<SystemI18NProperty> result = client.getAll("radien", 1, 10, new ArrayList<>(), true);
        assertEquals(1, result.getTotalResults());
        assertFalse(result.getResults().isEmpty());
    }

    @Test
    public void testGetAllInvalid() throws SystemException {
        Response response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("value").build();
        when(mockResource.getAll(eq("radien"), eq(1), eq(10), anyList(), eq(true)))
                .thenReturn(response);
        Page<SystemI18NProperty> result = client.getAll("radien", 1, 10, new ArrayList<>(), true);
        assertTrue(result.getResults().isEmpty());
    }

    @Test(expected = SystemException.class)
    public void testGetAllException() throws SystemException, MalformedURLException {
        when(clientServiceUtil.getI18NResourceClient(anyString()))
                .thenThrow(new MalformedURLException());
        client.getAll("radien", 1, 10, new ArrayList<>(), true);
    }

    @Test
    public void testGetTranslation() throws SystemException {
        Response response = Response.ok().entity("value").build();
        when(mockResource.getMessage("key", "radien", "en"))
                .thenReturn(response);
        assertEquals("value", client.getTranslation("key", "en", "radien"));
    }

    @Test
    public void testGetTranslationInvalid() throws SystemException {
        Response response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("value").build();
        when(mockResource.getMessage("key", "radien", "en"))
                .thenReturn(response);
        String translation = client.getTranslation("key", "en", "radien");
        assertNotNull(translation);
        assertEquals("key", translation);
    }

    @Test(expected = SystemException.class)
    public void testGetTranslationException() throws SystemException, MalformedURLException {
        when(clientServiceUtil.getI18NResourceClient(anyString()))
                .thenThrow(new MalformedURLException());
        client.getTranslation("key", "en", "radien");
    }

    @Test
    public void testSave() throws SystemException {
        Response response = Response.ok().build();
        when(mockResource.saveProperty(any(SystemI18NProperty.class)))
                .thenReturn(response);
        assertTrue(client.save(new I18NProperty()));
    }

    @Test
    public void testSaveInvalid() throws SystemException {
        Response response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("value").build();
        when(mockResource.saveProperty(any(SystemI18NProperty.class)))
                .thenReturn(response);
        assertFalse(client.save(new I18NProperty()));
    }

    @Test(expected = SystemException.class)
    public void testSaveException() throws SystemException, MalformedURLException {
        when(clientServiceUtil.getI18NResourceClient(anyString()))
                .thenThrow(new MalformedURLException());
        client.save(new I18NProperty());
    }

    @Test
    public void testDeleteProperties() throws SystemException {
        when(mockResource.deleteProperties(any()))
                .thenReturn(Response.ok().build());
        assertTrue(client.deleteProperties(Collections.singletonList(new I18NProperty())));
    }

    @Test(expected = SystemException.class)
    public void testDeletePropertiesException() throws SystemException, MalformedURLException {
        when(clientServiceUtil.getI18NResourceClient(anyString()))
                .thenThrow(new MalformedURLException());
        client.deleteProperties(Collections.singletonList(new I18NProperty()));
    }

    @Test
    public void testDeleteAllByApplicationProperties() throws SystemException {
        when(mockResource.deleteProperties(any()))
                .thenReturn(Response.ok().build());
        assertTrue(client.deleteAllByApplication("radien"));
    }

    @Test(expected = SystemException.class)
    public void testDeleteAllByApplicationPropertiesException() throws SystemException, MalformedURLException {
        when(clientServiceUtil.getI18NResourceClient(anyString()))
                .thenThrow(new MalformedURLException());
        client.deleteAllByApplication("radien");
    }

    @Test
    public void testdeleteError() throws SystemException {
        when(mockResource.deleteProperties(any()))
                .thenReturn(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("error").build());
        assertFalse(client.deleteAllByApplication("radien"));
    }

    @Test
    public void testfindByKeyAndApplication() throws SystemException {
        String entity = "{\"" +
                "key\":\"keyValue\"," +
                "\"application\": \"radien\"," +
                "\"translations\": [" +
                "{" +
                "\"language\": \"en\"," +
                "\"value\": \"value\"" +
                "}]}";
        InputStream in = new ByteArrayInputStream(entity.getBytes());
        Response response = Response.ok().entity(in).build();
        when(mockResource.findByKeyAndApplication("key", "radien"))
                .thenReturn(response);
        Optional<SystemI18NProperty> byKeyAndApplication = client.findByKeyAndApplication("key", "radien");
        assertTrue(byKeyAndApplication.isPresent());
        assertNotNull(byKeyAndApplication.get());
    }

    @Test
    public void testfindByKeyAndApplicationInvalid() throws SystemException {
        Response response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("value").build();
        when(mockResource.findByKeyAndApplication("key", "radien"))
                .thenReturn(response);
        assertFalse(client.findByKeyAndApplication("key", "radien").isPresent());
    }

    @Test(expected = SystemException.class)
    public void testfindByKeyAndApplicationException() throws SystemException, MalformedURLException {
        when(clientServiceUtil.getI18NResourceClient(anyString()))
                .thenThrow(new MalformedURLException());
        client.findByKeyAndApplication("key", "radien");
    }

    @Test
    public void testfindAllByApplication() throws SystemException {
        String entity = "[" +
                "{\"" +
                "key\":\"keyValue\"," +
                "\"application\": \"radien\"," +
                "\"translations\": [" +
                "{" +
                "\"language\": \"en\"," +
                "\"value\": \"value\"" +
                "}]" +
                "}," +
                "{\"" +
                "key\":\"keyValue1\"," +
                "\"application\": \"radien\"," +
                "\"translations\": [" +
                "{" +
                "\"language\": \"en\"," +
                "\"value\": \"value1\"" +
                "}]" +
                "}]";
        InputStream in = new ByteArrayInputStream(entity.getBytes());
        Response response = Response.ok().entity(in).build();
        when(mockResource.findAllByApplication("radien"))
                .thenReturn(response);
        List<SystemI18NProperty> byKeyAndApplication = client.findAllByApplication("radien");
        assertNotNull(byKeyAndApplication);
    }

    @Test
    public void testfindAllByApplicationInvalid() throws SystemException {
        Response response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("value").build();
        when(mockResource.findAllByApplication("radien"))
                .thenReturn(response);
        assertTrue(client.findAllByApplication("radien").isEmpty());
    }

    @Test(expected = SystemException.class)
    public void testfindAllByApplicationException() throws SystemException, MalformedURLException {
        when(clientServiceUtil.getI18NResourceClient(anyString()))
                .thenThrow(new MalformedURLException());
        client.findAllByApplication("radien");
    }
}
