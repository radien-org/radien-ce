/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.webapp.permission;

import io.radien.api.model.permission.SystemPermission;
import io.radien.api.service.permission.ActionRESTServiceAccess;
import io.radien.api.service.permission.PermissionRESTServiceAccess;
import io.radien.api.service.permission.ResourceRESTServiceAccess;
import io.radien.ms.permissionmanagement.client.entities.Permission;
import io.radien.webapp.AbstractBaseJsfTester;
import io.radien.webapp.DataModelEnum;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.activeTenant.ActiveTenantDataModelManager;
import javax.faces.application.FacesMessage;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Class that aggregates UnitTest cases for {@link PermissionDataModel}
 * @author Newton Carvalho
 */
public class PermissionDataModelTest extends AbstractBaseJsfTester {

    @Mock
    private ActiveTenantDataModelManager activeTenantDataModelManager;
    
    @InjectMocks
    private PermissionDataModel target;

    /**
     * Test for callback method {@link PermissionDataModel#init()}
     */
    @Test
    public void testInit() {
        when(activeTenantDataModelManager.isTenantActive()).thenReturn(true);
        target.init();
        assertNotNull(target.getLazyModel());
    }

    /**
     * Test for callback method {@link PermissionDataModel#init()}, in a scenario
     * where no active tenant is available
     */
    @Test
    public void testInitWhenNoActiveTenantAvailable() {
        when(activeTenantDataModelManager.isTenantActive()).thenReturn(false);
        target.init();
        assertNull(target.getLazyModel());
    }

    /**
     * Test for method {@link PermissionDataModel#onload()}
     */
    @Test
    public void testOnLoad() {
        when(activeTenantDataModelManager.isTenantActive()).thenReturn(true);
        target.onload();
        assertNotNull(target.getLazyModel());
    }

    /**
     * Test for setter method {@link PermissionDataModel#setLazyModel(LazyDataModel)}
     */
    @Test
    public void testSetterLazyModel() {
        LazyPermissionDataModel lazyDataModel = mock(LazyPermissionDataModel.class);
        target.setLazyModel(lazyDataModel);
        assertEquals(lazyDataModel, target.getLazyModel());
    }

    /**
     * Test for setter method {@link PermissionDataModel#setActionRESTServiceAccess(ActionRESTServiceAccess)}
     */
    @Test
    public void testSetterForRoleRESTServiceAccess() {
        ActionRESTServiceAccess client = mock(ActionRESTServiceAccess.class);
        target.setActionRESTServiceAccess(client);
        assertEquals(client, target.getActionRESTServiceAccess());
    }

    /**
     * Test for setter method {@link PermissionDataModel#setResourceRESTServiceAccess(ResourceRESTServiceAccess)}
     */
    @Test
    public void testSetterForTenantRESTServiceAccess() {
        ResourceRESTServiceAccess client = mock(ResourceRESTServiceAccess.class);
        target.setResourceRESTServiceAccess(client);
        assertEquals(client, target.getResourceRESTServiceAccess());
    }

    /**
     * Test for setter method {@link PermissionDataModel#setService(PermissionRESTServiceAccess)}
     */
    @Test
    public void testSetterForTenantRoleRESTServiceAccess() {
        PermissionRESTServiceAccess client = mock(PermissionRESTServiceAccess.class);
        target.setService(client);
        assertEquals(client, target.getService());
    }

    /**
     * Test for setter method {@link PermissionDataModel#setSelectedPermission(SystemPermission)}
     */
    @Test
    public void testSetterForSelectedPermission() {
        SystemPermission systemPermission = mock(SystemPermission.class);
        target.setSelectedPermission(systemPermission);
        assertEquals(systemPermission, target.getSelectedPermission());
    }

    /**
     * Test for setter method {@link PermissionDataModel#setPreviousSelectedPermission(SystemPermission)}
     */
    @Test
    public void testSetterForPreviousSelectedPermission() {
        SystemPermission systemPermission = mock(SystemPermission.class);
        target.setPreviousSelectedPermission(systemPermission);
        assertEquals(systemPermission, target.getPreviousSelectedPermission());
    }

    /**
     * Test for setter method {@link PermissionDataModel#setPermissionManager(PermissionManager)}
     */
    @Test
    public void testSetterForPermissionManager() {
        PermissionManager permissionManager = mock(PermissionManager.class);
        target.setPermissionManager(permissionManager);
        assertEquals(permissionManager, target.getPermissionManager());
    }

    /**
     * Test for method {@link PermissionDataModel#onRowSelect(SelectEvent)}}
     */
    @Test
    public void testOnRowSelect() {
        SystemPermission permission = mock(SystemPermission.class);
        when(permission.getId()).thenReturn(222L);

        target.setPreviousSelectedPermission(permission);

        SelectEvent<SystemPermission> selectEvent = mock(SelectEvent.class);
        when(selectEvent.getObject()).thenReturn(permission);

        target.onRowSelect(selectEvent);

        assertNull(target.getSelectedPermission().getId());
        assertNull(target.getPreviousSelectedPermission().getId());

        target.setPreviousSelectedPermission(null);
        target.onRowSelect(selectEvent);
        assertEquals(permission, target.getPreviousSelectedPermission());

        target.setPreviousSelectedPermission(mock(SystemPermission.class));
        target.onRowSelect(selectEvent);
        assertEquals(permission, target.getPreviousSelectedPermission());
    }

    /**
     * Test for method {@link PermissionDataModel#editRecords()}, method
     * that checks if edit process is allowed to proceed, but within in scenario
     * where no permission was selected from DataGrid
     */
    @Test
    public void testEditRecordsWhenNoPermissionWasSelected() {
        target.setSelectedPermission(null);

        // Call core method
        String returnMappedURI = target.editRecords();

        assertEquals(DataModelEnum.PERMISSION_MAIN_PAGE.getValue(), returnMappedURI);

        // Test the obtained message
        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());
        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_WARN, captured.getSeverity());
    }

    /**
     * Test for method {@link PermissionDataModel#editRecords()}, method
     * that checks if edit process is allowed to proceed, but within in scenario
     * where the permission was "unselected" from the DataGrid
     */
    @Test
    public void testEditRecordsWithEmptySelectedPermission() {
        target.setSelectedPermission(new Permission());

        // Call core method
        String returnMappedURI = target.editRecords();

        assertEquals(DataModelEnum.PERMISSION_MAIN_PAGE.getValue(), returnMappedURI);

        // Test the obtained message
        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());
        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_WARN, captured.getSeverity());
    }


    /**
     * Test for method {@link PermissionDataModel#editRecords()}, method
     * that checks if edit process is allowed to proceed.
     */
    @Test
    public void testEditRecords() {
        Permission permission = new Permission(); permission.setId(1111L);
        target.setSelectedPermission(permission);

        PermissionManager permissionManager = mock(PermissionManager.class);
        when(permissionManager.edit(permission)).thenReturn(DataModelEnum.
                PERMISSION_DETAIL_PAGE.getValue());

        target.setPermissionManager(permissionManager);

        // Call core method
        String returnMappedURI = target.editRecords();

        assertEquals(DataModelEnum.PERMISSION_DETAIL_PAGE.getValue(), returnMappedURI);
    }
}
