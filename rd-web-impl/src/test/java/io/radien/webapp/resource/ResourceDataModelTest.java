/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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
 * See the License for the specific language governing actions and
 * limitations under the License.
 */
package io.radien.webapp.resource;

import io.radien.exception.SystemException;

import io.radien.api.model.permission.SystemResource;
import io.radien.api.service.permission.ResourceRESTServiceAccess;

import io.radien.ms.permissionmanagement.client.entities.Resource;

import io.radien.webapp.DataModelEnum;

import io.radien.webapp.JSFUtilAndFaceContextMessagesTest;
import io.radien.webapp.activeTenant.ActiveTenantDataModelManager;


import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;


import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.primefaces.event.SelectEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
/**
 * Class that aggregates UnitTest cases for ResourceDataModel
 *
 * @author Rajesh Gavvala
 */

public class ResourceDataModelTest extends JSFUtilAndFaceContextMessagesTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    @InjectMocks
    private ResourceDataModel resourceDataModel;

    @Mock
    private ResourceRESTServiceAccess service;

    @Mock
    private ActiveTenantDataModelManager activeTenantDataModelManager;

    SystemResource systemResource;
    @BeforeClass
    public static void beforeClass(){
        handleJSFUtilAndFaceContextMessages();
    }

    @AfterClass
    public static void afterClass(){
        destroy();
    }

    /**
     * Constructs mock object
     */
    @Before
    public void before(){
        systemResource = new Resource();
        systemResource.setId(1L);
        systemResource.setName("testResource");
    }

    @Test
    public void testInit(){
        doReturn(true).when(activeTenantDataModelManager).isTenantActive();
        resourceDataModel.init();
        assertEquals(service, resourceDataModel.getService());
        assertNotNull(resourceDataModel.getLazyModel());

        doReturn(false).when(activeTenantDataModelManager).isTenantActive();
        resourceDataModel.onload();
    }

    @Test
    public void testSave() throws SystemException {
        doReturn(true).when(service).create(systemResource);
        String expected = resourceDataModel.save(systemResource);
        assertEquals(expected, DataModelEnum.RESOURCE_DATA_TABLE_PAGE.getValue());
    }

    @Test
    public void testSaveException() throws SystemException {
        doThrow(NullPointerException.class).when(service).create(systemResource);
        String expected = resourceDataModel.save(systemResource);
        assertEquals(expected, DataModelEnum.RESOURCE_CREATION_PAGE.getValue());
    }

    @Test
    public void testEditRecords() {
        resourceDataModel.setSelectedResource(null);
        String expectedMainPage = resourceDataModel.editRecords();
        assertEquals(expectedMainPage, DataModelEnum.RESOURCE_DATA_TABLE_PAGE.getValue());

        resourceDataModel.setSelectedResource(systemResource);
        String expectedDetailPage = resourceDataModel.editRecords();
        assertEquals(expectedDetailPage, DataModelEnum.RESOURCE_DETAIL_PAGE.getValue());
    }

    @Test
    public void testDelete() throws SystemException {
        resourceDataModel.setSelectedResource(systemResource);
        doReturn(true).when(service).delete(systemResource.getId());
        resourceDataModel.delete();
        assertNull(resourceDataModel.getSelectedResource());

        resourceDataModel.delete();
    }

    @Test(expected = Exception.class)
    public void testDeleteException() throws SystemException {
        resourceDataModel.setSelectedResource(systemResource);
        doThrow(Exception.class).when(service).delete(systemResource.getId());
        resourceDataModel.delete();
    }

    @Test
    public void testEdit() throws SystemException {
        String expectedMainPage = resourceDataModel.edit(null);
        assertEquals(expectedMainPage, DataModelEnum.RESOURCE_DETAIL_PAGE.getValue());

        systemResource.setName("testUpdate");
        resourceDataModel.setSelectedResource(systemResource);
        doReturn(true).when(service).create(systemResource);
        String expected = resourceDataModel.edit(systemResource);
        assertEquals(expected, DataModelEnum.RESOURCE_DATA_TABLE_PAGE.getValue());
    }

    @Test
    public void testEditException() throws SystemException {
        resourceDataModel.setSelectedResource(systemResource);
        doThrow(NullPointerException.class).when(service).create(systemResource);
        String expected = resourceDataModel.edit(systemResource);
        assertEquals(expected, DataModelEnum.RESOURCE_DETAIL_PAGE.getValue());
    }

    @Test
    public void testGetResourceAndSetResource(){
        resourceDataModel.setResource(systemResource);
        assertEquals(systemResource, resourceDataModel.getResource());
    }

    @Test
    public void testReturnToDataTableRecords(){
        String expected = resourceDataModel.returnToDataTableRecords();
        assertEquals(expected, DataModelEnum.RESOURCE_DATA_TABLE_PAGE.getValue());
    }

    @Test
    public void testOnRowSelect() {
        SelectEvent<SystemResource> event = mock(SelectEvent.class);
        when(event.getObject()).thenReturn(systemResource);
        resourceDataModel.onRowSelect(event);
        assertEquals(systemResource, resourceDataModel.getSelectedResource());
    }
}