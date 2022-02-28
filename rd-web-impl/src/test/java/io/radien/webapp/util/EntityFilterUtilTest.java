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
package io.radien.webapp.util;

import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.role.SystemRole;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.service.permission.PermissionRESTServiceAccess;
import io.radien.api.service.role.RoleRESTServiceAccess;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.permissionmanagement.client.entities.Permission;
import io.radien.ms.rolemanagement.client.entities.Role;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.webapp.JSFUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

/**
 * Class that aggregates UnitTest cases for EntityFilterUtil manager
 * @author Newton Carvalho
 */
public class EntityFilterUtilTest {

    @InjectMocks
    private EntityFilterUtil target;

    @Mock
    private RoleRESTServiceAccess roleRESTServiceAccess;

    @Mock
    private TenantRESTServiceAccess tenantRESTServiceAccess;

    @Mock
    private PermissionRESTServiceAccess permissionRESTServiceAccess;

    FacesContext facesContext;

    private static MockedStatic<FacesContext> facesContextMockedStatic;
    private static MockedStatic<JSFUtil> jsfUtilMockedStatic;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @BeforeClass
    public static void beforeClass(){
        facesContextMockedStatic = Mockito.mockStatic(FacesContext.class);
        jsfUtilMockedStatic = Mockito.mockStatic(JSFUtil.class);
    }
    @AfterClass
    public static final void destroy(){
        if(facesContextMockedStatic!=null) {
            facesContextMockedStatic.close();
        }
        if(jsfUtilMockedStatic!=null) {
            jsfUtilMockedStatic.close();
        }
    }

    @Before
    public void before(){

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
    }

    /**
     * Retrieve tenants doing a filtering process taking in consideration the name property.
     * This must me be used as backend method to sustain an autocomplete or any whatever component
     * that needs to retrieve tenants by name
     */
    @Test
    public void testFilterTenantsByName() throws SystemException {
        List expectedResult = new ArrayList<>();
        expectedResult.add(new Tenant());

        Page<? extends SystemTenant> page = new Page<>();
        page.setResults(expectedResult);

        String filter = "sub%";

        when(tenantRESTServiceAccess.getAll(filter,
                1, 100, null, false)).then(i -> page);

        List<? extends SystemTenant> output = target.filterTenantsByName("sub");
        assertNotNull(output);
        assertEquals(output, expectedResult);
    }

    /**
     * Retrieve tenants doing a filtering process taking in consideration the name property.
     * This must me be used as backend method to sustain an autocomplete or any whatever component
     * that needs to retrieve tenants by name.
     *
     * Test a scenario/case in which an exception occurs during the retrieval process
     */
    @Test
    public void testFilterTenantsByNameWithException() throws SystemException {
        String filterName = "sub%";
        Exception e = new RuntimeException("Error retrieving tenants");
        when(tenantRESTServiceAccess.getAll(filterName, 1, 100, null,
                false)).thenThrow(e);

        List<? extends SystemTenant> output = target.filterTenantsByName(filterName);
        assertNotNull(output);
        assertTrue(output.isEmpty());

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
        assertEquals("rd_retrieve_error", captured.getSummary());
        assertEquals(e.getMessage(), captured.getDetail());
    }

    /**
     * Retrieve tenants doing a filtering process taking in consideration the name property.
     * This must me be used as backend method to sustain an autocomplete or any whatever component
     * that needs to retrieve roles by name
     */
    @Test
    public void testFilterRolesByName() throws SystemException {
        List expectedResult = new ArrayList<>();
        expectedResult.add(new Role());

        Page page = new Page<>();
        page.setResults(expectedResult);

        String filterName = "admin%";

        when(roleRESTServiceAccess.getAll(filterName,
                1, 10, null, false)).thenReturn(page);

        List<? extends SystemRole> output = target.filterRolesByName(filterName);
        assertNotNull(output);
        assertEquals(output, expectedResult);
    }

    /**
     * Retrieve tenants doing a filtering process taking in consideration the name property.
     * This must me be used as backend method to sustain an autocomplete or any whatever component
     * that needs to retrieve roles by name.
     *
     * Test a scenario/case in which an exception occurs during the retrieval process
     */
    @Test
    public void testFilterRolesByNameWithException() throws SystemException {
        String filter = "sub%";
        Exception e = new RuntimeException("Error retrieving roles");
        when(roleRESTServiceAccess.getAll(filter, 1, 10, null,
                false)).thenThrow(e);

        List<? extends SystemRole> output = target.filterRolesByName("sub");
        assertNotNull(output);
        assertTrue(output.isEmpty());

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
        assertEquals("rd_retrieve_error", captured.getSummary());
        assertEquals(e.getMessage(), captured.getDetail());
    }


    /**
     * Retrieve tenants doing a filtering process taking in consideration the name property.
     * This must me be used as backend method to sustain an autocomplete or any whatever component
     * that needs to retrieve permissions by name
     */
    @Test
    public void testFilterPermissionsByName() throws SystemException, MalformedURLException {
        List expectedResult = new ArrayList<>();
        expectedResult.add(new Permission());

        Page page = new Page<>();
        page.setResults(expectedResult);

        String filterName = "create%";

        when(permissionRESTServiceAccess.getAll(filterName,
                1, 10, null, false)).thenReturn(page);

        List<? extends SystemPermission> output = target.filterPermissionsByName(filterName);
        assertNotNull(output);
        assertEquals(output, expectedResult);
    }

    /**
     * Retrieve tenants doing a filtering process taking in consideration the name property.
     * This must me be used as backend method to sustain an autocomplete or any whatever component
     * that needs to retrieve roles by name.
     *
     * Test a scenario/case in which an exception occurs during the retrieval process
     */
    @Test
    public void testFilterPermissionsByNameWithException() throws SystemException, MalformedURLException {
        String filter = "create%";
        Exception e = new RuntimeException("Error retrieving permissions");
        when(permissionRESTServiceAccess.getAll(filter, 1, 10, null,
                false)).thenThrow(e);

        List<? extends SystemPermission> output = target.filterPermissionsByName(filter);
        assertNotNull(output);
        assertTrue(output.isEmpty());

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
        assertEquals("rd_retrieve_error", captured.getSummary());
        assertEquals(e.getMessage(), captured.getDetail());
    }
}
