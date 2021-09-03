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
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.radien.webapp.tenantrole;

import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.service.role.RoleRESTServiceAccess;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleRESTServiceAccess;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.activeTenant.ActiveTenantDataModelManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JSFUtil.class, FacesContext.class, ExternalContext.class})
public class TenantRoleAssociationDataModelTest {

    @InjectMocks
    private TenantRoleAssociationDataModel tenantRoleAssociationDataModel;

    @Mock
    private TenantRoleRESTServiceAccess service;

    @Mock
    private RoleRESTServiceAccess roleRESTServiceAccess;

    @Mock
    private ActiveTenantDataModelManager activeTenantDataModelManager;

    @Mock
    private TenantRESTServiceAccess tenantRESTServiceAccess;

    private FacesContext facesContext;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(FacesContext.class);
        PowerMockito.mockStatic(JSFUtil.class);

        facesContext = mock(FacesContext.class);
        when(FacesContext.getCurrentInstance()).thenReturn(facesContext);

        ExternalContext externalContext = mock(ExternalContext.class);
        when(facesContext.getExternalContext()).thenReturn(externalContext);

        Flash flash = mock(Flash.class);
        when(externalContext.getFlash()).thenReturn(flash);

        when(JSFUtil.getFacesContext()).thenReturn(facesContext);
        when(JSFUtil.getExternalContext()).thenReturn(externalContext);
        when(JSFUtil.getMessage(anyString())).thenAnswer(i -> i.getArguments()[0]);
    }

    /**
     * Test for callback method {@link TenantRoleAssociationDataModel#init()}
     */
    @Test
    public void testInit() {
        when(activeTenantDataModelManager.isTenantActive()).thenReturn(true);
        tenantRoleAssociationDataModel.init();
        assertNotNull(tenantRoleAssociationDataModel.getLazyModel());
    }

    /**
     * Test for method {@link TenantRoleAssociationDataModel#onload()}
     */
    @Test
    public void testOnLoad() {
        when(activeTenantDataModelManager.isTenantActive()).thenReturn(true);
        tenantRoleAssociationDataModel.onload();
        assertNotNull(tenantRoleAssociationDataModel.getLazyModel());
    }

    /**
     * Test for setter method {@link TenantRoleAssociationDataModel#setLazyModel(LazyDataModel)}
     */
    @Test
    public void testSetterLazyModel() {
        LazyTenantRoleAssociationDataModel lazyDataModel = mock(LazyTenantRoleAssociationDataModel.class);
        tenantRoleAssociationDataModel.setLazyModel(lazyDataModel);
        assertEquals(lazyDataModel, tenantRoleAssociationDataModel.getLazyModel());
    }

    /**
     * Test for setter method {@link TenantRoleAssociationDataModel#setRoleRESTServiceAccess(RoleRESTServiceAccess)}
     */
    @Test
    public void testSetterForRoleRESTServiceAccess() {
        RoleRESTServiceAccess client = mock(RoleRESTServiceAccess.class);
        tenantRoleAssociationDataModel.setRoleRESTServiceAccess(client);
        assertEquals(client, tenantRoleAssociationDataModel.getRoleRESTServiceAccess());
    }

    /**
     * Test for setter method {@link TenantRoleAssociationDataModel#setRoleRESTServiceAccess(RoleRESTServiceAccess)}
     */
    @Test
    public void testSetterForTenantRESTServiceAccess() {
        TenantRESTServiceAccess client = mock(TenantRESTServiceAccess.class);
        tenantRoleAssociationDataModel.setTenantRESTServiceAccess(client);
        assertEquals(client, tenantRoleAssociationDataModel.getTenantRESTServiceAccess());
    }

    /**
     * Test for setter method {@link TenantRoleAssociationDataModel#setService(TenantRoleRESTServiceAccess)}
     */
    @Test
    public void testSetterForTenantRoleRESTServiceAccess() {
        TenantRoleRESTServiceAccess client = mock(TenantRoleRESTServiceAccess.class);
        tenantRoleAssociationDataModel.setService(client);
        assertEquals(client, tenantRoleAssociationDataModel.getService());
    }

    /**
     * Test for setter method {@link TenantRoleAssociationDataModel#setSelectedAssociation(SystemTenantRole)}
     */
    @Test
    public void testSetterForSelectedAssociation() {
        SystemTenantRole systemTenantRole = mock(SystemTenantRole.class);
        tenantRoleAssociationDataModel.setSelectedAssociation(systemTenantRole);
        assertEquals(systemTenantRole, tenantRoleAssociationDataModel.getSelectedAssociation());
    }

    /**
     * Test for method {@link TenantRoleAssociationDataModel#onRowSelect(SelectEvent)}}
     */
    @Test
    public void testOnRowSelect() {
        SystemTenantRole tenantRole = mock(SystemTenantRole.class);
        when(tenantRole.getId()).thenReturn(1L);
        SelectEvent<SystemTenantRole> selectEvent = mock(SelectEvent.class);
        when(selectEvent.getObject()).thenReturn(tenantRole);

        TenantRoleAssociationDataModel dataModel = new TenantRoleAssociationDataModel();
        dataModel.onRowSelect(selectEvent);

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_INFO, captured.getSeverity());
        assertEquals("rowSelected", captured.getSummary());
    }
}
