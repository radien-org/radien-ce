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

package io.radien.webapp.doctypemanagement.propertydefinition;

import io.radien.exception.SystemException;
import io.radien.ms.doctypemanagement.client.entities.PropertyDefinition;
import io.radien.ms.doctypemanagement.client.services.PropertyDefinitionRESTServiceClient;
import io.radien.webapp.JSFUtil;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PropertyDefinitionManagerTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    @InjectMocks
    private PropertyDefinitionManager manager;
    @Mock
    private PropertyDefinitionRESTServiceClient serviceAccess;

    @Test
    public void testInit() {
        manager.init();
        assertNotNull(manager.getDataModel());
        assertNull(manager.getSelectedPropertyDefinition());
    }

    @Test
    public void testOnload() {
        manager.onload();
        assertNotNull(manager.getDataModel());
        assertNull(manager.getSelectedPropertyDefinition());
    }

    @Test
    public void testSave() throws SystemException {
        try(MockedStatic<JSFUtil> jsfUtil = mockStatic(JSFUtil.class)) {
            PropertyDefinition propertyDefinition = new PropertyDefinition();
            propertyDefinition.setName("name");
            when(serviceAccess.save(any()))
                    .thenReturn(true);
            manager.save(propertyDefinition);
            verify(serviceAccess).save(any(PropertyDefinition.class));
            jsfUtil.verify(() -> {
                JSFUtil.addSuccessMessage(null, "rd_save_success", "name");
            });
        }
    }

    @Test
    public void testSaveError() throws SystemException {
        try(MockedStatic<JSFUtil> jsfUtil = mockStatic(JSFUtil.class)) {
            PropertyDefinition propertyDefinition = new PropertyDefinition();
            propertyDefinition.setName("name");
            when(serviceAccess.save(any())).thenReturn(false);
            manager.save(propertyDefinition);
            verify(serviceAccess).save(any(PropertyDefinition.class));
            jsfUtil.verify(() -> {
                JSFUtil.addErrorMessage(null, "rd_save_error", propertyDefinition.getName());
            });
        }
    }

    @Test
    public void testSaveException() throws SystemException {
        try(MockedStatic<JSFUtil> jsfUtil = mockStatic(JSFUtil.class)) {
            PropertyDefinition propertyDefinition = new PropertyDefinition();
            propertyDefinition.setName("name");
            when(serviceAccess.save(any(PropertyDefinition.class)))
                    .thenThrow(new SystemException("error"));
            manager.save(propertyDefinition);
            verify(serviceAccess).save(any(PropertyDefinition.class));
            jsfUtil.verify(() -> {
                JSFUtil.addErrorMessage(null, "rd_save_error", propertyDefinition.getName());
            });
        }
    }

    @Test
    public void testDelete() throws SystemException {
        try(MockedStatic<JSFUtil> jsfUtil = mockStatic(JSFUtil.class)) {
            PropertyDefinition propertyDefinition = new PropertyDefinition();
            propertyDefinition.setName("name");
            propertyDefinition.setId(1L);
            when(serviceAccess.deletePropertyDefinition(anyLong()))
                    .thenReturn(true);
            manager.deletePropertyDefinition(propertyDefinition);
            verify(serviceAccess).deletePropertyDefinition(1L);
            jsfUtil.verify(() -> {
                JSFUtil.addSuccessMessage(null, "rd_delete_success", propertyDefinition.getName());
            });
        }
    }

    @Test
    public void testDeleteError() throws SystemException {
        try(MockedStatic<JSFUtil> jsfUtil = mockStatic(JSFUtil.class)) {
            PropertyDefinition propertyDefinition = new PropertyDefinition();
            propertyDefinition.setId(1L);
            propertyDefinition.setName("name");
            when(serviceAccess.deletePropertyDefinition(anyLong())).thenReturn(false);
            manager.deletePropertyDefinition(propertyDefinition);
            verify(serviceAccess).deletePropertyDefinition(anyLong());
            jsfUtil.verify(() -> {
                JSFUtil.addErrorMessage(null, "rd_delete_error", propertyDefinition.getName());
            });
        }
    }

    @Test
    public void testDeleteException() throws SystemException {
        try(MockedStatic<JSFUtil> jsfUtil = mockStatic(JSFUtil.class)) {
            PropertyDefinition propertyDefinition = new PropertyDefinition();
            propertyDefinition.setName("name");
            propertyDefinition.setId(1L);
            when(serviceAccess.deletePropertyDefinition(anyLong()))
                    .thenThrow(new SystemException("error"));
            manager.deletePropertyDefinition(propertyDefinition);
            verify(serviceAccess).deletePropertyDefinition(anyLong());
            jsfUtil.verify(() -> {
                JSFUtil.addErrorMessage(null, "rd_delete_error", propertyDefinition.getName());
            });
        }
    }

    @Test
    public void testCreatePropertyDefinitionRedirectURL() {
        assertEquals("pretty:propertyDefinitionCreate", manager.createPropertyDefinitionRedirectURL());
    }

    @Test
    public void testEditPropertyDefinitionRedirectURL() {
        manager.setSelectedPropertyDefinition(new PropertyDefinition());
        assertEquals("pretty:propertyDefinitionEdit", manager.editPropertyDefinitionRedirectURL());
    }

    @Test
    public void testEditPropertyDefinitionRedirectURLNoSelection() {
        manager.setSelectedPropertyDefinition(null);
        assertEquals("pretty:propertyDefinitions", manager.editPropertyDefinitionRedirectURL());
    }

    @Test
    public void testDeletePropertyDefinitionRedirectURL() {
        manager.setSelectedPropertyDefinition(new PropertyDefinition());
        assertEquals("pretty:propertyDefinitionDelete", manager.deletePropertyDefinitionRedirectURL());
    }

    @Test
    public void testDeletePropertyDefinitionRedirectURLNoSelection() {
        manager.setSelectedPropertyDefinition(null);
        assertEquals("pretty:propertyDefinitions", manager.deletePropertyDefinitionRedirectURL());
    }

    @Test
    public void testReturnToPropertyDefinitionsRedirectURL() {
        manager.setSelectedPropertyDefinition(new PropertyDefinition());
        assertEquals("pretty:propertyDefinitions", manager.returnToPropertyDefinitionsRedirectURL());
        assertNull(manager.getSelectedPropertyDefinition());
    }


}
