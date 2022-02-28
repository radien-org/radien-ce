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

package io.radien.ms.ecm.controller;

import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.api.service.i18n.I18NServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.ecm.client.entities.i18n.DeletePropertyFilter;
import io.radien.ms.ecm.client.entities.i18n.I18NProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class I18NResourceClientTest {
    @InjectMocks
    private I18NResourceClient client;

    @Mock
    private I18NServiceAccess serviceAccess;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetMessage() throws SystemException {
        when(serviceAccess.getTranslation("key", "en", "app"))
                .thenReturn("value");
        Response result = client.getMessage("key", "app", "en");
        assertEquals(Response.Status.OK.getStatusCode(), result.getStatusInfo().getStatusCode());
        assertEquals("value", result.readEntity(String.class));
    }

    @Test
    public void testFindByKeyAndApplication() throws SystemException {
        SystemI18NProperty property = new I18NProperty();
        property.setKey("key");
        property.setApplication("app");
        when(serviceAccess.findByKeyAndApplication("key", "app"))
                .thenReturn(property);
        Response result = client.findByKeyAndApplication("key", "app");
        assertEquals(Response.Status.OK.getStatusCode(), result.getStatusInfo().getStatusCode());
        assertEquals(property, result.readEntity(I18NProperty.class));
    }

    @Test
    public void testAllByApplication() throws SystemException {
        List<SystemI18NProperty> list = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            list.add(new I18NProperty());
        }
        when(serviceAccess.findAllByApplication("app"))
                .thenReturn(list);
        Response result = client.findAllByApplication("app");
        assertEquals(Response.Status.OK.getStatusCode(), result.getStatusInfo().getStatusCode());
        assertEquals(list, result.readEntity(List.class));
    }

    @Test
    public void testDeleteProperty() {
        SystemI18NProperty property = new I18NProperty();
        property.setKey("key");
        property.setApplication("app");
        DeletePropertyFilter filter = new DeletePropertyFilter(Collections.singletonList(property));
        Response result = client.deleteProperties(filter);
        assertEquals(Response.Status.OK.getStatusCode(), result.getStatusInfo().getStatusCode());
    }

    @Test
    public void testDeleteApplicationProperties() {
        DeletePropertyFilter filter = new DeletePropertyFilter("app");
        Response result = client.deleteProperties(filter);
        assertEquals(Response.Status.OK.getStatusCode(), result.getStatusInfo().getStatusCode());
    }

    @Test
    public void testInvalidRequest() {
        DeletePropertyFilter filter = new DeletePropertyFilter();
        Response result = client.deleteProperties(filter);
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), result.getStatusInfo().getStatusCode());
    }
    @Test
    public void testSaveProperty() {
        SystemI18NProperty property = new I18NProperty();
        property.setKey("key");
        property.setApplication("app");
        Response result = client.saveProperty(property);
        assertEquals(Response.Status.OK.getStatusCode(), result.getStatusInfo().getStatusCode());
    }

}
