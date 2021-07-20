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

package io.radien.webapp.tenant;

import io.radien.api.model.tenant.SystemActiveTenant;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenant.SystemTenantType;
import io.radien.api.service.tenant.ActiveTenantRESTServiceAccess;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.tenantmanagement.client.entities.ActiveTenant;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.tenantmanagement.client.entities.TenantType;
import io.radien.webapp.DataModelEnum;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.activeTenant.ActiveTenantDataModelManager;
import io.radien.webapp.security.UserSession;
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
import org.primefaces.model.LazyDataModel;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Active Tenant Data Model Manager Test
 * {@link ActiveTenantDataModelManager}
 *
 * @author Bruno Gama
 **/
@RunWith(PowerMockRunner.class)
@PrepareForTest({JSFUtil.class, FacesContext.class, ExternalContext.class})
public class TenantDataModelTest {

    @InjectMocks
    private TenantDataModel tenantDataModel;

    @Mock
    private TenantRESTServiceAccess service;

    @Mock
    private ActiveTenantRESTServiceAccess activeTenantRESTServiceAccess;

    @Mock
    private LazyDataModel<? extends SystemTenant> lazyModel;

    @Mock
    private UserSession userSession;

    FacesContext facesContext;

    Tenant tenant;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(FacesContext.class);
        PowerMockito.mockStatic(JSFUtil.class);

        facesContext = mock(FacesContext.class);
        when(FacesContext.getCurrentInstance()).thenReturn(facesContext);

        ExternalContext externalContext = mock(ExternalContext.class);
        when(facesContext.getExternalContext())
                .thenReturn(externalContext);

        Flash flash = mock(Flash.class);
        when(externalContext.getFlash()).thenReturn(flash);

        when(JSFUtil.getFacesContext()).thenReturn(facesContext);
        when(JSFUtil.getExternalContext()).thenReturn(externalContext);
        when(JSFUtil.getMessage(anyString())).thenAnswer(i -> i.getArguments()[0]);

        tenant = new Tenant();
        tenant.setName("name");
        tenant.setTenantKey("key");
        tenant.setTenantType(TenantType.ROOT_TENANT);
        tenantDataModel.setTenantStartDate(new Date());
        tenantDataModel.setTenantEndDate(new Date());

        tenantDataModel.setTenant(tenant);
        tenantDataModel.setSelectedTenant(tenant);
    }

    @Test
    public void testInit() {
        boolean success;
        try {
            tenantDataModel.init();
            success = true;
        } catch (Exception e) {
            success = false;
        }

        assertTrue(success);
    }

    @Test
    public void testOnLoad() {
        boolean success;
        try {
            tenantDataModel.onload();
            success = true;
        } catch (Exception e) {
            success = false;
        }

        assertTrue(success);
    }

    @Test
    public void testSave() throws SystemException {
        doReturn(true).when(service).create(any());
        tenantDataModel.save(tenant);

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_INFO, captured.getSeverity());
        assertEquals(DataModelEnum.SAVE_SUCCESS_MESSAGE.getValue(), captured.getSummary());
    }

    @Test
    public void testSaveWithId() throws SystemException {
        tenant.setId(2L);
        doReturn(true).when(service).update(any());
        tenantDataModel.save(tenant);

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_INFO, captured.getSeverity());
        assertEquals(DataModelEnum.SAVE_SUCCESS_MESSAGE.getValue(), captured.getSummary());
    }

    @Test
    public void testSaveException() {
        tenantDataModel.save(null);

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
        assertEquals(DataModelEnum.SAVE_ERROR_MESSAGE.getValue(), captured.getSummary());
    }

    @Test
    public void testSaveClientTenant() throws SystemException {
        Tenant clientTenant = new Tenant();
        clientTenant.setName("name1");
        clientTenant.setTenantKey("key1");
        clientTenant.setTenantType(TenantType.CLIENT_TENANT);
        clientTenant.setClientCity("city1");
        clientTenant.setClientAddress("address1");
        clientTenant.setClientCountry("country1");
        clientTenant.setClientEmail("email1");
        clientTenant.setClientPhoneNumber(123L);
        clientTenant.setClientZipCode("zipCode1");

        ActiveTenant activeTenant = new ActiveTenant();
        activeTenant.setIsTenantActive(true);
        activeTenant.setTenantId(3L);

        List<SystemActiveTenant> activeTenants = new ArrayList<>();
        activeTenants.add(activeTenant);
        doReturn(activeTenants).when(activeTenantRESTServiceAccess).getActiveTenantByFilter(anyLong(), any(), any(), anyBoolean());

        tenantDataModel.save(clientTenant);

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_INFO, captured.getSeverity());
        assertEquals(DataModelEnum.SAVE_SUCCESS_MESSAGE.getValue(), captured.getSummary());
    }

    @Test
    public void testSaveClientTenantExceptionNoZipCode() throws SystemException {
        Tenant clientTenant = new Tenant();
        clientTenant.setName("name1");
        clientTenant.setTenantKey("key1");
        clientTenant.setTenantType(TenantType.CLIENT_TENANT);
        clientTenant.setClientCity("city1");
        clientTenant.setClientAddress("address1");
        clientTenant.setClientCountry("country1");
        clientTenant.setClientEmail("email1");
        clientTenant.setClientPhoneNumber(123L);

        validateClientTenantCreation(clientTenant);
    }

    @Test
    public void testSaveClientTenantExceptionNoCity() throws SystemException {
        Tenant clientTenant = new Tenant();
        clientTenant.setName("name1");
        clientTenant.setTenantKey("key1");
        clientTenant.setTenantType(TenantType.CLIENT_TENANT);
        clientTenant.setClientAddress("address1");
        clientTenant.setClientCountry("country1");
        clientTenant.setClientEmail("email1");
        clientTenant.setClientPhoneNumber(123L);
        clientTenant.setClientZipCode("zipCode1");

        validateClientTenantCreation(clientTenant);
    }

    private void validateClientTenantCreation(Tenant clientTenant) throws SystemException {
        ActiveTenant activeTenant = new ActiveTenant();
        activeTenant.setIsTenantActive(true);
        activeTenant.setTenantId(3L);

        List<SystemActiveTenant> activeTenants = new ArrayList<>();
        activeTenants.add(activeTenant);
        doReturn(activeTenants).when(activeTenantRESTServiceAccess).getActiveTenantByFilter(anyLong(), any(), any(), anyBoolean());

        assertEquals(DataModelEnum.TENANT_CREATION_PAGE.getValue(), tenantDataModel.save(clientTenant));
    }

    @Test
    public void testSaveClientTenantExceptionNoCountry() throws SystemException {
        Tenant clientTenant = new Tenant();
        clientTenant.setName("name1");
        clientTenant.setTenantKey("key1");
        clientTenant.setTenantType(TenantType.CLIENT_TENANT);
        clientTenant.setClientCity("city1");
        clientTenant.setClientAddress("address1");
        clientTenant.setClientEmail("email1");
        clientTenant.setClientPhoneNumber(123L);
        clientTenant.setClientZipCode("zipCode1");

        validateClientTenantCreation(clientTenant);
    }

    @Test
    public void testSaveClientTenantExceptionNoPhoneNumber() throws SystemException {
        Tenant clientTenant = new Tenant();
        clientTenant.setName("name1");
        clientTenant.setTenantKey("key1");
        clientTenant.setTenantType(TenantType.CLIENT_TENANT);
        clientTenant.setClientCity("city1");
        clientTenant.setClientAddress("address1");
        clientTenant.setClientCountry("country1");
        clientTenant.setClientEmail("email1");
        clientTenant.setClientZipCode("zipCode1");

        validateClientTenantCreation(clientTenant);
    }

    @Test
    public void testSaveClientTenantExceptionNoEmail() throws SystemException {
        Tenant clientTenant = new Tenant();
        clientTenant.setName("name1");
        clientTenant.setTenantKey("key1");
        clientTenant.setTenantType(TenantType.CLIENT_TENANT);
        clientTenant.setClientCity("city1");
        clientTenant.setClientAddress("address1");
        clientTenant.setClientCountry("country1");
        clientTenant.setClientPhoneNumber(123L);
        clientTenant.setClientZipCode("zipCode1");

        validateClientTenantCreation(clientTenant);
    }

    @Test
    public void testSaveClientTenantExceptionNoAddress() throws SystemException {
        Tenant clientTenant = new Tenant();
        clientTenant.setName("name1");
        clientTenant.setTenantKey("key1");
        clientTenant.setTenantType(TenantType.CLIENT_TENANT);
        clientTenant.setClientCity("city1");
        clientTenant.setClientCountry("country1");
        clientTenant.setClientEmail("email1");
        clientTenant.setClientPhoneNumber(123L);
        clientTenant.setClientZipCode("zipCode1");

        validateClientTenantCreation(clientTenant);
    }

    @Test
    public void testSaveSubTenant() throws SystemException {
        Tenant clientTenant = new Tenant();
        clientTenant.setName("name");
        clientTenant.setTenantKey("key");
        clientTenant.setTenantType(TenantType.SUB_TENANT);

        ActiveTenant activeTenant = new ActiveTenant();
        activeTenant.setIsTenantActive(true);
        activeTenant.setTenantId(2L);

        List<SystemActiveTenant> activeTenants = new ArrayList<>();
        activeTenants.add(activeTenant);
        doReturn(activeTenants).when(activeTenantRESTServiceAccess).getActiveTenantByFilter(anyLong(), any(), any(), anyBoolean());

        assertEquals(DataModelEnum.TENANT_MAIN_PAGE.getValue(), tenantDataModel.save(clientTenant));
    }

    @Test
    public void testSaveSubTenantException() throws SystemException {
        Tenant clientTenant = new Tenant();
        clientTenant.setName("name");
        clientTenant.setTenantKey("key");
        clientTenant.setTenantType(TenantType.SUB_TENANT);

        ActiveTenant activeTenant = new ActiveTenant();
        activeTenant.setIsTenantActive(true);
        activeTenant.setTenantId(2L);

        doThrow(new SystemException()).when(activeTenantRESTServiceAccess).
                getActiveTenantByFilter(anyLong(), any(), any(), anyBoolean());

        assertEquals(DataModelEnum.TENANT_CREATION_PAGE.getValue(), tenantDataModel.save(clientTenant));
    }

    @Test
    public void testSaveSubTenant2() throws SystemException {
        Tenant clientTenant = new Tenant();
        clientTenant.setName("name");
        clientTenant.setTenantKey("key");
        clientTenant.setTenantType(TenantType.SUB_TENANT);

        ActiveTenant activeTenant = new ActiveTenant();
        activeTenant.setIsTenantActive(true);
        activeTenant.setTenantId(2L);

        List<SystemActiveTenant> activeTenants = new ArrayList<>();
        activeTenants.add(activeTenant);
        doReturn(activeTenants).when(activeTenantRESTServiceAccess).getActiveTenantByFilter(anyLong(), any(), any(), anyBoolean());

        assertEquals(DataModelEnum.TENANT_MAIN_PAGE.getValue(), tenantDataModel.save(clientTenant));
    }


    @Test
    public void testEditRecordsEmptySelectedRecord() throws IOException {
        assertEquals(DataModelEnum.TENANT_DETAIL_PAGE.getValue(), tenantDataModel.editRecords());
    }

    @Test
    public void testEditRecords() throws IOException {
        tenantDataModel.setSelectedTenant(null);
        assertEquals(DataModelEnum.TENANT_MAIN_PAGE.getValue(), tenantDataModel.editRecords());
    }

    @Test
    public void testDeleteTenantHierarchy() throws SystemException {
        Tenant clientTenant = new Tenant();
        clientTenant.setName("name");
        clientTenant.setTenantKey("key");
        clientTenant.setTenantType(TenantType.CLIENT_TENANT);
        clientTenant.setClientCity("city");
        clientTenant.setClientAddress("address");
        clientTenant.setClientCountry("country");
        clientTenant.setClientEmail("email");
        clientTenant.setClientPhoneNumber(123L);
        clientTenant.setClientZipCode("zipCode");
        clientTenant.setId(2L);

        tenantDataModel.setSelectedTenant(clientTenant);

        doReturn(true).when(service).deleteTenantHierarchy(anyLong());

        tenantDataModel.deleteTenantHierarchy();

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_INFO, captured.getSeverity());
        assertEquals(DataModelEnum.DELETE_SUCCESS.getValue(), captured.getSummary());
    }

    @Test
    public void testDeleteTenantHierarchyException() throws SystemException {
        Tenant clientTenant = new Tenant();
        clientTenant.setName("name");
        clientTenant.setTenantKey("key");
        clientTenant.setTenantType(TenantType.CLIENT_TENANT);
        clientTenant.setClientCity("city");
        clientTenant.setClientAddress("address");
        clientTenant.setClientCountry("country");
        clientTenant.setClientEmail("email");
        clientTenant.setClientPhoneNumber(123L);
        clientTenant.setClientZipCode("zipCode");
        clientTenant.setId(2L);

        tenantDataModel.setSelectedTenant(clientTenant);

        doThrow(new SystemException()).when(service).deleteTenantHierarchy(anyLong());

        tenantDataModel.deleteTenantHierarchy();

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
        assertEquals(DataModelEnum.DELETE_ERROR.getValue(), captured.getSummary());
    }

    @Test
    public void testReturnHome() {
        assertEquals(DataModelEnum.TENANT_MAIN_PAGE.getValue(), tenantDataModel.returnToDataTableRecords());
    }

    @Test
    public void testEdit() throws SystemException, IOException {
        Tenant clientTenant = new Tenant();
        clientTenant.setName("name");
        clientTenant.setTenantKey("key");
        clientTenant.setTenantType(TenantType.CLIENT_TENANT);
        clientTenant.setClientCity("city");
        clientTenant.setClientAddress("address");
        clientTenant.setClientCountry("country");
        clientTenant.setClientEmail("email");
        clientTenant.setClientPhoneNumber(123L);
        clientTenant.setClientZipCode("zipCode");
        clientTenant.setId(2L);

        doReturn(true).when(service).update(any());

        assertEquals(DataModelEnum.TENANT_MAIN_PAGE.getValue(), tenantDataModel.edit(clientTenant));
    }

    @Test
    public void testGetTenant() {
        SystemTenant tenantReceived = tenantDataModel.getTenant();
        assertEquals(tenant.getName(), tenantReceived.getName());
        assertEquals(tenant.getTenantKey(), tenantReceived.getTenantKey());
    }

    @Test
    public void testGetTenantType() {
        SystemTenantType[] tenantTypes = tenantDataModel.getTenantTypes();
        assertEquals(3L, tenantTypes.length);
    }

    @Test
    public void testGetLazyModel() {
        assertEquals(lazyModel, tenantDataModel.getLazyModel());
    }

    @Test
    public void testGetSelectedTenant() {
        SystemTenant tenantReceived = tenantDataModel.getSelectedTenant();
        assertEquals(tenant.getName(), tenantReceived.getName());
        assertEquals(tenant.getTenantKey(), tenantReceived.getTenantKey());
    }

    @Test
    public void testGetService() {
        assertEquals(service, tenantDataModel.getService());
    }

    @Test
    public void testGetStartDate() {
        Date now = new Date();
        tenantDataModel.setTenantStartDate(now);
        assertEquals(now, tenantDataModel.getTenantStartDate());
    }

    @Test
    public void testGetEndDate() {
        Date now = new Date();
        tenantDataModel.setTenantEndDate(now);
        assertEquals(now, tenantDataModel.getTenantEndDate());
    }
}