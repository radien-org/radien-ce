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
package io.radien.ms.ecm.resource;

import io.radien.ms.ecm.client.entities.I18NProperty;
import io.radien.ms.ecm.client.entities.LabelTypeEnum;
import io.radien.ms.ecm.client.services.SystemI18NPropertyService;
import io.radien.ms.ecm.factory.I18NPropertyEntityFactory;
import io.radien.ms.ecm.util.I18NPropertyEntityMapper;
import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class I18NPropertyResourceTest extends TestCase {

    @InjectMocks
    private I18NPropertyResource propertyResource;
    @Mock
    private SystemI18NPropertyService propertyService;

    private String TEST_MESSAGE = "Test message";
    private I18NProperty mockProperty;
    private List<I18NProperty> mockPropertyList;

    public I18NPropertyResourceTest() {
        MockitoAnnotations.initMocks(this);
        mockPropertyList = new ArrayList<>();
        mockProperty = I18NPropertyEntityMapper.mapToDTO(
                I18NPropertyEntityFactory.createWithDefaults("test", LabelTypeEnum.MESSAGE, "en", "test message")
        );
        mockPropertyList.add(mockProperty);
        mockPropertyList.add(I18NPropertyEntityMapper.mapToDTO(
                I18NPropertyEntityFactory.createWithDefaults("test_1", LabelTypeEnum.MESSAGE, "en", "test message_1")
        ));
    }

    @Test
    public void testGetMessage() {
        when(propertyService.getLocalizedMessage(anyString())).thenReturn(TEST_MESSAGE);
        Response response = propertyResource.getMessage("message");

        assertEquals(200, response.getStatus());
        assertEquals(TEST_MESSAGE, response.getEntity());
    }

    @Test
    public void testGetProperty() {
        when(propertyService.getByKey(anyString())).thenReturn(mockProperty);
        Response response = propertyResource.getProperty("message");

        assertEquals(200, response.getStatus());
        assertEquals(mockProperty, response.getEntity());
    }

    @Test
    public void testAdd() {
        when(propertyService.save(any(I18NProperty.class))).thenReturn(mockProperty);
        Response response = propertyResource.add(mockProperty);

        assertEquals(201, response.getStatus());
        assertEquals("test", response.getMetadata().getFirst("Location").toString());
    }

    @Test
    public void testAddAll() {
        when(propertyService.save(any(List.class))).thenReturn(mockPropertyList);
        Response response = propertyResource.addAll(mockPropertyList);

        assertEquals(201, response.getStatus());
        assertEquals("test;test_1", response.getMetadata().getFirst("Location").toString());
    }

    @Test
    public void testGetKeys() {
        when(propertyService.getAll()).thenReturn(mockPropertyList);
        Response response = propertyResource.getKeys();

        assertEquals(200, response.getStatus());
        assertEquals(((List<String>)response.getEntity()).size(), mockPropertyList.size());
    }

    @Test
    public void testGetProperties() {
        when(propertyService.getAll()).thenReturn(mockPropertyList);
        Response response = propertyResource.getProperties();

        assertEquals(200, response.getStatus());
        assertEquals(((List<I18NProperty>)response.getEntity()).size(), mockPropertyList.size());
    }

}
