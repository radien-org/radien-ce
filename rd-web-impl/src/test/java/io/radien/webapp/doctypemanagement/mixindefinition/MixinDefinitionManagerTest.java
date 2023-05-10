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

package io.radien.webapp.doctypemanagement.mixindefinition;


import io.radien.api.entity.Page;
import io.radien.api.model.docmanagement.mixindefinition.SystemMixinDefinition;
import io.radien.api.model.docmanagement.propertydefinition.SystemPropertyDefinition;
import io.radien.api.service.docmanagement.mixindefinition.MixinDefinitionRESTServiceAccess;
import io.radien.api.service.docmanagement.propertydefinition.PropertyDefinitionRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.doctypemanagement.client.entities.MixinDefinitionDTO;
import io.radien.ms.doctypemanagement.client.entities.PropertyDefinition;
import io.radien.webapp.JSFUtil;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.primefaces.model.DualListModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class MixinDefinitionManagerTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    private MixinDefinitionManager manager;
    @Mock
    private MixinDefinitionRESTServiceAccess mixinDefinitionServiceAccess;
    @Mock
    private PropertyDefinitionRESTServiceAccess propertyDefinitionServiceAccess;

    @Before
    public void init() {
        manager.init();
    }

    @Test
    public void testInit() {
        manager.init();
        assertNotNull(manager.getDataModel());
        assertNull(manager.getSelectedMixinDefinition());
    }

    @Test
    public void testOnLoad() {
        manager.onload();
        assertNotNull(manager.getDataModel());
        assertNull(manager.getSelectedMixinDefinition());
    }

    @Test
    public void testSaveSuccess() throws SystemException {
        try(MockedStatic<JSFUtil> jsfUtil = mockStatic(JSFUtil.class)) {
            SystemMixinDefinition<Long> mixinDefinition = new MixinDefinitionDTO();
            mixinDefinition.setName("name");
            List<SystemPropertyDefinition> selectedPropertyList = new ArrayList<>();
            SystemPropertyDefinition propertyDefinition = new PropertyDefinition();
            propertyDefinition.setId(1L);
            selectedPropertyList.add(propertyDefinition);
            DualListModel<SystemPropertyDefinition> pickList = new DualListModel<>();
            pickList.setTarget(selectedPropertyList);
            manager.setPickList(pickList);

            when(mixinDefinitionServiceAccess.save(any(SystemMixinDefinition.class)))
                    .thenReturn(true);
            manager.save(mixinDefinition);

            jsfUtil.verify(() -> {
                JSFUtil.addSuccessMessage(null, "rd_save_success", mixinDefinition.getName());
            });
        }
    }

    @Test
    public void testSaveError() throws SystemException {
        try(MockedStatic<JSFUtil> jsfUtil = mockStatic(JSFUtil.class)) {
            SystemMixinDefinition<Long> mixinDefinition = new MixinDefinitionDTO();
            mixinDefinition.setName("name");
            DualListModel<SystemPropertyDefinition> pickList = new DualListModel<>();
            pickList.setTarget(new ArrayList<>());
            manager.setPickList(pickList);

            when(mixinDefinitionServiceAccess.save(any(SystemMixinDefinition.class)))
                    .thenReturn(false);
            manager.save(mixinDefinition);

            jsfUtil.verify(() -> {
                JSFUtil.addErrorMessage(null, "rd_save_error", mixinDefinition.getName());
            });
        }
    }

    @Test
    public void testSaveException() throws SystemException {
        try(MockedStatic<JSFUtil> jsfUtil = mockStatic(JSFUtil.class)) {
            SystemMixinDefinition<Long> mixinDefinition = new MixinDefinitionDTO();
            mixinDefinition.setName("name");
            DualListModel<SystemPropertyDefinition> pickList = new DualListModel<>();
            pickList.setTarget(new ArrayList<>());
            manager.setPickList(pickList);

            when(mixinDefinitionServiceAccess.save(any(SystemMixinDefinition.class)))
                    .thenThrow(new SystemException());
            manager.save(mixinDefinition);

            jsfUtil.verify(() -> {
                JSFUtil.addErrorMessage(null, "rd_save_error", mixinDefinition.getName());
            });
        }
    }

    @Test
    public void testDeleteMixinDefinition() throws SystemException {
        try(MockedStatic<JSFUtil> jsfUtil = mockStatic(JSFUtil.class)) {
            SystemMixinDefinition<Long> mixinDefinition = new MixinDefinitionDTO();
            mixinDefinition.setId(1L);
            when(mixinDefinitionServiceAccess.deleteMixinDefinition(anyLong()))
                    .thenReturn(true);

            manager.deleteMixinDefinition(mixinDefinition);

            jsfUtil.verify(() -> {
                JSFUtil.addSuccessMessage(null, "rd_delete_success", mixinDefinition.getName());
            });
        }
    }

    @Test
    public void testDeleteMixinDefinitionError() throws SystemException {
        try(MockedStatic<JSFUtil> jsfUtil = mockStatic(JSFUtil.class)) {
            SystemMixinDefinition<Long> mixinDefinition = new MixinDefinitionDTO();
            mixinDefinition.setId(1L);
            when(mixinDefinitionServiceAccess.deleteMixinDefinition(anyLong()))
                    .thenReturn(false);

            manager.deleteMixinDefinition(mixinDefinition);

            jsfUtil.verify(() -> {
                JSFUtil.addErrorMessage(null, "rd_delete_error", mixinDefinition.getName());
            });
        }
    }

    @Test
    public void testDeleteMixinDefinitionException() throws SystemException {
        try(MockedStatic<JSFUtil> jsfUtil = mockStatic(JSFUtil.class)) {
            SystemMixinDefinition<Long> mixinDefinition = new MixinDefinitionDTO();
            mixinDefinition.setId(1L);
            when(mixinDefinitionServiceAccess.deleteMixinDefinition(anyLong()))
                    .thenThrow(new SystemException());

            manager.deleteMixinDefinition(mixinDefinition);

            jsfUtil.verify(() -> {
                JSFUtil.addErrorMessage(null, "rd_delete_error", mixinDefinition.getName());
            });
        }
    }

    @Test
    public void testGetPropertyDefinitionNamesAsString() throws SystemException {
        String resultString = "Property Name 1, Property Name 2";
        when(propertyDefinitionServiceAccess.getPropertyDefinitionNamesByIds(any()))
                .thenReturn(resultString);

        assertEquals(resultString, manager.getPropertyDefinitionNamesAsString(new MixinDefinitionDTO()));
    }

    @Test
    public void testGetPropertyDefinitionNamesAsStringException() throws SystemException {
        when(propertyDefinitionServiceAccess.getPropertyDefinitionNamesByIds(any()))
                .thenThrow(new SystemException());

        assertEquals("ERR", manager.getPropertyDefinitionNamesAsString(new MixinDefinitionDTO()));
    }

    @Test
    public void testCreateMixinDefinitionRedirectURL() throws SystemException {
        doReturn(new Page<>()).when(propertyDefinitionServiceAccess)
                .getAll(any(), anyInt(), anyInt(), any(), anyBoolean());
        assertEquals("pretty:mixinDefinitionCreate", manager.createMixinDefinitionRedirectURL());
    }

    @Test
    public void testCreateMixinDefinitionRedirectURLError() throws SystemException {
        try(MockedStatic<JSFUtil> jsfUtil = mockStatic(JSFUtil.class)) {
            doThrow(new SystemException()).when(propertyDefinitionServiceAccess)
                    .getAll(any(), anyInt(), anyInt(), any(), anyBoolean());
            assertEquals("pretty:mixinDefinitionCreate", manager.createMixinDefinitionRedirectURL());
            jsfUtil.verify(() -> {
                JSFUtil.addErrorMessage(null, "rd_propertyDefinition_error_retrieving_definitions");
            });
        }
    }

    @Test
    public void testEditMixinDefinitionRedirectURL() {
        manager.setSelectedMixinDefinition(new MixinDefinitionDTO());
        assertEquals("pretty:mixinDefinitionEdit", manager.editMixinDefinitionRedirectURL());
    }

    @Test
    public void testEditMixinDefinitionRedirectURLNoSelection() {
        manager.setSelectedMixinDefinition(null);
        assertEquals("pretty:mixinDefinitions", manager.editMixinDefinitionRedirectURL());
    }

    @Test
    public void testReturnToMixinDefinitionsRedirectURL() {
        assertEquals("pretty:mixinDefinitions", manager.returnToMixinDefinitionsRedirectURL());
        assertNotNull(manager.getNewMixinDefinition());
        assertNull(manager.getSelectedMixinDefinition());
    }

    private List<? extends SystemPropertyDefinition> setupGetAll() {
        List<SystemPropertyDefinition> results = new ArrayList<>();
        SystemPropertyDefinition def1 = new PropertyDefinition();
        def1.setId(1L);
        def1.setName("name1");
        SystemPropertyDefinition def2 = new PropertyDefinition();
        def2.setId(2L);
        def2.setName("name2");
        results.add(def1);
        results.add(def2);
        return results;
    }
}