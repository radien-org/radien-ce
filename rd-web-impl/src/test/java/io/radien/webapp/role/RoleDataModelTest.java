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
package io.radien.webapp.role;

import io.radien.exception.SystemException;

import io.radien.api.model.role.SystemRole;
import io.radien.api.service.role.RoleRESTServiceAccess;

import io.radien.ms.rolemanagement.client.entities.Role;

import io.radien.webapp.DataModelEnum;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.JSFUtilAndFaceContextMessagesTest;
import io.radien.webapp.activeTenant.ActiveTenantDataModelManager;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.primefaces.event.SelectEvent;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
/**
 * Class that aggregates UnitTest cases for RoleDataModel
 *
 * @author Rajesh Gavvala
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({JSFUtil.class, FacesContext.class, ExternalContext.class})
public class RoleDataModelTest extends JSFUtilAndFaceContextMessagesTest {
    @InjectMocks
    private RoleDataModel roleDataModel;

    @Mock
    private RoleRESTServiceAccess service;

    @Mock
    private ActiveTenantDataModelManager activeTenantDataModelManager;

    SystemRole systemRole;

    /**
     * Constructs mock object
     */
    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
        handleJSFUtilAndFaceContextMessages();

        systemRole = new Role();
        systemRole.setId(1L);
        systemRole.setName("testRole");
    }

    /**
     * Test method inti()
     * Asserts service and model objects
     */
    @Test
    public void testInit(){
        doReturn(true).when(activeTenantDataModelManager).isTenantActive();
        roleDataModel.init();
        assertEquals(service, roleDataModel.getService());
        assertNotNull(roleDataModel.getLazyModel());

        doReturn(false).when(activeTenantDataModelManager).isTenantActive();
        roleDataModel.onload();
    }

    /**
     * Test method save()
     * @throws SystemException if any error
     */
    @Test
    public void testSave() throws SystemException {
        doReturn(true).when(service).create(systemRole);
        String expected = roleDataModel.save(systemRole);
        assertEquals(expected, DataModelEnum.ROLE_MAIN_PAGE.getValue());
    }

    /**
     * Test method save()
     * Asserts exception
     * @throws SystemException if any error
     */
    @Test
    public void testSaveException() throws SystemException {
        SystemRole roleToBeCreated = new Role();
        doThrow(NullPointerException.class).when(service).create(roleToBeCreated);
        String expected = roleDataModel.save(roleToBeCreated);
        assertEquals(expected, DataModelEnum.ROLE_CREATION_PAGE.getValue());
    }

    /**
     * Test method update()
     * @throws SystemException if any error
     */
    @Test
    public void testUpdate() throws SystemException {
        systemRole.setId(1L);
        doReturn(true).when(service).update(systemRole);
        String expected = roleDataModel.save(systemRole);
        assertEquals(expected, DataModelEnum.ROLE_MAIN_PAGE.getValue());
    }

    /**
     * Test method update()
     * Asserts exception
     * @throws SystemException if any error
     */
    @Test
    public void testUpdateException() throws SystemException {
        systemRole.setId(1L);
        doThrow(NullPointerException.class).when(service).update(systemRole);
        String expected = roleDataModel.save(systemRole);
        assertEquals(expected, DataModelEnum.ROLE_CREATION_PAGE.getValue());
    }

    /**
     * Test method editRecords()
     */
    @Test
    public void testEditRecords() {
        roleDataModel.setSelectedRole(null);
        String expectedMainPage = roleDataModel.editRecords();
        assertEquals(expectedMainPage, DataModelEnum.ROLE_MAIN_PAGE.getValue());

        roleDataModel.setSelectedRole(systemRole);
        String expectedDetailPage = roleDataModel.editRecords();
        assertEquals(expectedDetailPage, DataModelEnum.ROLE_DETAIL_PAGE.getValue());
    }

    /**
     * Test method delete()
     * @throws SystemException if any error
     */
    @Test
    public void testDelete() throws SystemException {
        roleDataModel.setSelectedRole(systemRole);
        doReturn(true).when(service).delete(systemRole.getId());
        roleDataModel.delete();
        assertNull(roleDataModel.getSelectedRole());

        roleDataModel.delete();
    }

    /**
     * Test method delete()
     * Asserts exception
     * @throws SystemException if any error
     */
    @Test(expected = Exception.class)
    public void testDeleteException() throws SystemException {
        roleDataModel.setSelectedRole(systemRole);
        doThrow(Exception.class).when(service).delete(systemRole.getId());
        roleDataModel.delete();
    }

    /**
     * Test method edit()
     * @throws SystemException if any system exception
     */
    @Test
    public void testEdit() throws SystemException {
        String expectedMainPage = roleDataModel.edit(null);
        assertEquals(expectedMainPage, DataModelEnum.ROLE_DETAIL_PAGE.getValue());

        systemRole.setName("testUpdate");
        roleDataModel.setSelectedRole(systemRole);
        doReturn(true).when(service).create(systemRole);
        String expected = roleDataModel.edit(systemRole);
        assertEquals(expected, DataModelEnum.ROLE_MAIN_PAGE.getValue());
    }

    /**
     * Test method edit()
     * Asserts exception
     * @throws SystemException if any system exception
     */
    @Test
    public void testEditException() throws SystemException {
        roleDataModel.setSelectedRole(systemRole);
        doThrow(NullPointerException.class).when(service).create(systemRole);
        String expected = roleDataModel.edit(systemRole);
        assertEquals(expected, DataModelEnum.ROLE_DETAIL_PAGE.getValue());
    }

    /**
     * Asserts getRole()
     */
    @Test
    public void testGetActionAndSetAction(){
        roleDataModel.setRole(systemRole);
        assertEquals(systemRole, roleDataModel.getRole());
    }

    /**
     * Asserts returnToDataTableRecords()
     */
    @Test
    public void testReturnToDataTableRecords(){
        String expected = roleDataModel.returnToDataTableRecords();
        assertEquals(expected, DataModelEnum.ROLE_MAIN_PAGE.getValue());
    }

    /**
     * Asserts OnRowSelect()
     */
    @Test
    public void testOnRowSelect() {
        SelectEvent<SystemRole> event = mock(SelectEvent.class);
        when(event.getObject()).thenReturn(systemRole);
        roleDataModel.onRowSelect(event);
        assertEquals(systemRole, roleDataModel.getSelectedRole());
    }

}