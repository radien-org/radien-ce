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

package io.radien.webapp.i18n;

import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.api.model.i18n.SystemI18NTranslation;
import io.radien.api.service.i18n.I18NRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.ecm.client.entities.i18n.I18NProperty;
import io.radien.ms.ecm.client.entities.i18n.I18NTranslation;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.primefaces.behavior.ajax.AjaxBehavior;
import org.primefaces.component.accordionpanel.AccordionPanel;
import org.primefaces.event.ToggleEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class I18NPropertyManagerTest {
    @InjectMocks
    private I18NPropertyManager manager;

    @Mock
    private I18NRESTServiceAccess i18NRESTServiceAccess;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInit() {
        manager.init();
        assertNotNull(manager.getDataModel());
        assertNull(manager.getSelectedProperty());
    }

    @Test
    public void testInitNewProperty() {
        manager.initNewProperty();
        assertNotNull(manager.getNewProperty());
    }

    @Test
    public void testUpdateProperty() throws SystemException {
        when(i18NRESTServiceAccess.save(any(SystemI18NProperty.class)))
                .thenReturn(true);
        manager.updateProperty(new I18NProperty());
        verify(i18NRESTServiceAccess).save(any(SystemI18NProperty.class));
    }

    @Test
    public void testUpdatePropertyError() throws SystemException {
        when(i18NRESTServiceAccess.save(any(SystemI18NProperty.class)))
                .thenReturn(false);
        manager.updateProperty(new I18NProperty());
        verify(i18NRESTServiceAccess).save(any(SystemI18NProperty.class));
    }

    @Test
    public void testUpdatePropertyException() throws SystemException {
        when(i18NRESTServiceAccess.save(any(SystemI18NProperty.class)))
                .thenThrow(new SystemException("error"));
        manager.updateProperty(new I18NProperty());
        verify(i18NRESTServiceAccess).save(any(SystemI18NProperty.class));
    }

    @Test
    public void addNewTranslationRow() {
        SystemI18NProperty property = new I18NProperty();
        property.setTranslations(new ArrayList<>());
        manager.addNewTranslationRow(property);
        assertEquals(1, property.getTranslations().size());
    }

    @Test
    public void testNewTranslationRowNull() {
        SystemI18NProperty property = new I18NProperty();
        manager.addNewTranslationRow(property);
        assertNotNull(property.getTranslations());
        assertEquals(1, property.getTranslations().size());
    }

    @Test
    public void testDelete() throws SystemException {
        when(i18NRESTServiceAccess.deleteProperties(anyList()))
                .thenReturn(true);
        manager.deleteProperty(new I18NProperty());
        verify(i18NRESTServiceAccess).deleteProperties(anyList());
    }

    @Test
    public void testDeleteError() throws SystemException {
        when(i18NRESTServiceAccess.deleteProperties(anyList()))
                .thenReturn(false);
        manager.deleteProperty(new I18NProperty());
        verify(i18NRESTServiceAccess).deleteProperties(anyList());
    }

    @Test
    public void testDeleteErrorException() throws SystemException {
        when(i18NRESTServiceAccess.deleteProperties(anyList()))
                .thenThrow(new SystemException("erroe"));
        manager.deleteProperty(new I18NProperty());
        verify(i18NRESTServiceAccess).deleteProperties(anyList());
    }

    @Test
    public void testRemoveTranslation() {
        SystemI18NProperty property = new I18NProperty();
        SystemI18NTranslation translation = new I18NTranslation();
        translation.setLanguage("en");
        translation.setValue("value");
        property.setTranslations(new ArrayList<>(Collections.singletonList(translation)));
        manager.removeTranslation(property, translation);
        assertTrue(property.getTranslations().isEmpty());
    }

    @Test
    public void testRemoveTranslationNotFound() {
        SystemI18NProperty property = new I18NProperty();
        SystemI18NTranslation translation = new I18NTranslation();
        translation.setLanguage("en");
        translation.setValue("value");
        property.setTranslations(new ArrayList<>(Collections.singletonList(translation)));
        SystemI18NTranslation otherTranslation = new I18NTranslation();
        otherTranslation.setLanguage("en");
        otherTranslation.setValue("value");
        manager.removeTranslation(property, otherTranslation);
        assertFalse(property.getTranslations().isEmpty());
    }

    @Test
    public void testOnRowExpand() {
        SystemI18NProperty property = new I18NProperty();
        ToggleEvent event = new ToggleEvent(new AccordionPanel(), new AjaxBehavior(),
                null, property);
        manager.onRowExpand(event);
        assertEquals(property, manager.getSelectedProperty());
    }

    @Test
    public void testSetSelectedProperty() {
        assertNull(manager.getSelectedProperty());
        manager.setSelectedProperty(new I18NProperty());
        assertNotNull(manager.getSelectedProperty());
    }

    @Test
    public void testSetNewProperty() {
        assertNull(manager.getNewProperty());
        manager.setNewProperty(new I18NProperty());
        assertNotNull(manager.getNewProperty());
    }
}
