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

package io.radien.webapp.activeTenant;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import io.radien.api.model.tenant.SystemActiveTenant;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.tenant.ActiveTenantRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.tenantmanagement.client.entities.ActiveTenant;
import io.radien.ms.tenantmanagement.client.services.ActiveTenantFactory;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.webapp.DataModelEnum;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.security.UserSession;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.doThrow;

/**
 * Active Tenant Data Model Manager Test
 * {@link io.radien.webapp.activeTenant.ActiveTenantDataModelManager}
 *
 * @author Bruno Gama
 **/
@RunWith(PowerMockRunner.class)
@PrepareForTest({JSFUtil.class, FacesContext.class, ExternalContext.class, PrettyContext.class})
public class ActiveTenantDataModelManagerTest {

    @InjectMocks
    private ActiveTenantDataModelManager activeTenantDataModelManager;

    @Mock
    private UserSession userSession;

    @Mock
    private ActiveTenantRESTServiceAccess activeTenantRESTServiceAccess;

    private FacesContext facesContext;

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

        SystemActiveTenant activeTenant = new ActiveTenant(2L, 2L, 2L, "test", false);
        activeTenantDataModelManager.setActiveTenant(activeTenant);
        activeTenantDataModelManager.setActiveTenantValue("test");
    }

    @Test
    public void testInit() throws SystemException {
        SystemUser user = new User();
        when(userSession.getUser()).thenReturn(user);

        List<SystemActiveTenant> listOfSystemActiveTenants = new ArrayList();
        listOfSystemActiveTenants.add(ActiveTenantFactory.create(10L, 10L, "test", true));

        doReturn(listOfSystemActiveTenants).when(activeTenantRESTServiceAccess).getActiveTenantByFilter(any(), any(), any(), anyBoolean());

        activeTenantDataModelManager.init();

        SystemActiveTenant newActiveActiveTenant = ActiveTenantFactory.create(10L, 10L, "test", true);

        assertEquals(newActiveActiveTenant.getUserId(), activeTenantDataModelManager.getActiveTenant().getUserId());
        assertEquals(newActiveActiveTenant.getIsTenantActive(), activeTenantDataModelManager.getActiveTenant().getIsTenantActive());
    }

    @Test
    public void testInitMultipleRecords() throws SystemException {
        SystemUser user = new User();
        when(userSession.getUser()).thenReturn(user);

        List<SystemActiveTenant> listOfSystemActiveTenants = new ArrayList();
        listOfSystemActiveTenants.add(ActiveTenantFactory.create(10L, 10L, "test", false));
        listOfSystemActiveTenants.add(ActiveTenantFactory.create(11L, 11L, "test", false));
        listOfSystemActiveTenants.add(ActiveTenantFactory.create(12L, 12L, "test", true));

        doReturn(listOfSystemActiveTenants).when(activeTenantRESTServiceAccess).getActiveTenantByFilter(any(), any(), any(), anyBoolean());

        activeTenantDataModelManager.setActiveTenantValue(null);
        activeTenantDataModelManager.init();

        SystemActiveTenant newActiveActiveTenant = ActiveTenantFactory.create(12L, 12L, "test", true);

        assertEquals(newActiveActiveTenant.getUserId(), activeTenantDataModelManager.getActiveTenant().getUserId());
        assertEquals(newActiveActiveTenant.getTenantId(), activeTenantDataModelManager.getActiveTenant().getTenantId());
        assertEquals(newActiveActiveTenant.getIsTenantActive(), activeTenantDataModelManager.getActiveTenant().getIsTenantActive());
    }

    @Test(expected = Exception.class)
    public void testInitException() throws SystemException {
        SystemUser user = new User();
        when(userSession.getUser()).thenReturn(user);

        doThrow(new Exception()).when(activeTenantRESTServiceAccess).getActiveTenantByFilter(any(), any(), any(), anyBoolean());

        activeTenantDataModelManager.init();
    }

    @Test
    public void testGetUserTenants() {
        List<SystemActiveTenant> userActiveTenantsTest = new ArrayList<>();
        SystemActiveTenant activeTenant = new ActiveTenant(3L, 3L, 3L, "test", false);
        userActiveTenantsTest.add(activeTenant);

        activeTenantDataModelManager.setUserActiveTenants(userActiveTenantsTest);
        List<String> activeTenantNames = activeTenantDataModelManager.getUserTenants();
        List<String> fixedList = new ArrayList<>();
        fixedList.add(activeTenant.getTenantName());

        assertNotNull(activeTenantNames);
        assertEquals(fixedList, activeTenantNames);
    }

    /**
     * Test for method {@link ActiveTenantDataModelManager#isTenantActive()}
     */
    @Test
    public void testIsTenantActive() {
        activeTenantDataModelManager.setActiveTenant(null);
        assertFalse(activeTenantDataModelManager.isTenantActive());
        activeTenantDataModelManager.setActiveTenant(mock(ActiveTenant.class));
        assertTrue(activeTenantDataModelManager.isTenantActive());
    }

    /**
     * Test for method {@link ActiveTenantDataModelManager#isOnUsersListingScreen()}
     * Scenario where the user is navigating through Users Management Data Table Screen
     */
    @Test
    public void testIsOnUsersListingScreenOK() {
        PowerMockito.mockStatic(PrettyContext.class);
        PrettyContext prettyContext = mock(PrettyContext.class);
        UrlMapping urlMapping = mock(UrlMapping.class);

        String id = "users";
        when(urlMapping.getId()).thenReturn(id);
        when(prettyContext.getCurrentMapping()).thenReturn(urlMapping);
        when(PrettyContext.getCurrentInstance()).thenReturn(prettyContext);

        assertTrue(activeTenantDataModelManager.isOnUsersListingScreen());
    }

    /**
     * Test for method {@link ActiveTenantDataModelManager#isOnUsersListingScreen()}
     * Scenario where the user is navigating through others screens
     */
    @Test
    public void testIsOnUsersListingScreenNOK() {
        PowerMockito.mockStatic(PrettyContext.class);
        PrettyContext prettyContext = mock(PrettyContext.class);
        UrlMapping urlMapping = mock(UrlMapping.class);

        String id = "tenants";
        when(urlMapping.getId()).thenReturn(id);
        when(prettyContext.getCurrentMapping()).thenReturn(urlMapping);
        when(PrettyContext.getCurrentInstance()).thenReturn(prettyContext);

        assertFalse(activeTenantDataModelManager.isOnUsersListingScreen());
    }

    /**
     * Test the flow when coming from users screen
     * @throws IOException
     */
    @Test
    public void testingFlowWhenComingFromUsersScreen() throws IOException, SystemException {
        PowerMockito.mockStatic(PrettyContext.class);
        PrettyContext prettyContext = mock(PrettyContext.class);
        UrlMapping urlMapping = mock(UrlMapping.class);

        String id = "users";
        when(urlMapping.getId()).thenReturn(id);
        when(prettyContext.getCurrentMapping()).thenReturn(urlMapping);
        when(PrettyContext.getCurrentInstance()).thenReturn(prettyContext);

        String mockedContextPath = "int-env-url";
        String expectedRedirectionPath = mockedContextPath + DataModelEnum.USERS_DISPATCH_PATH.getValue();
        final List<?> urlRedirection = new ArrayList();

        PowerMockito.mockStatic(FacesContext.class);
        FacesContext fc = mock(FacesContext.class);
        ExternalContext ec = mock(ExternalContext.class);
        Flash flash = mock(Flash.class);

        when(FacesContext.getCurrentInstance()).thenReturn(fc);
        when(fc.getExternalContext()).thenReturn(ec);
        when(ec.getFlash()).thenReturn(flash);
        when(ec.getRequestContextPath()).thenReturn(mockedContextPath);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                urlRedirection.add(invocation.getArgument(0));
                return null;
            }
        }).when(ec).redirect(anyString());

        testTenantChangedValidationMethodToOther();

        assertEquals(expectedRedirectionPath, urlRedirection.get(0));
    }


    @Test
    public void testTenantChangedValidationMethodToNull() {
        assertNotNull(activeTenantDataModelManager.getActiveTenant());
        activeTenantDataModelManager.tenantChangedValidationMethod(JSFUtil.getMessage(DataModelEnum.NO_ACTIVE_TENANT_MESSAGE.getValue()));
        assertNull(activeTenantDataModelManager.getActiveTenant());
    }

    @Test
    public void testTenantChangedValidationMethodToOther() throws SystemException {
        assertNotNull(activeTenantDataModelManager.getActiveTenant());
        assertFalse(activeTenantDataModelManager.getActiveTenant().getIsTenantActive());

        SystemUser user = new User();
        when(userSession.getUser()).thenReturn(user);

        List<SystemActiveTenant> listOfSystemActiveTenants = new ArrayList();
        listOfSystemActiveTenants.add(ActiveTenantFactory.create(10L, 10L, "test", false));
        listOfSystemActiveTenants.add(ActiveTenantFactory.create(11L, 11L, "test1", false));
        listOfSystemActiveTenants.add(ActiveTenantFactory.create(12L, 12L, "test2", false));

        doReturn(listOfSystemActiveTenants).when(activeTenantRESTServiceAccess).getActiveTenantByFilter(any(), any(), anyString(), anyBoolean());

        activeTenantDataModelManager.tenantChangedValidationMethod("test");
        assertTrue(activeTenantDataModelManager.getActiveTenant().getIsTenantActive());
    }

    @Test(expected = Exception.class)
    public void testTenantChangedValidationException() throws SystemException {
        SystemUser user = new User();
        when(userSession.getUser()).thenReturn(user);

        doThrow(new Exception()).when(activeTenantRESTServiceAccess).getActiveTenantByFilter(any(), any(), anyString(), anyBoolean());

        activeTenantDataModelManager.tenantChangedValidationMethod("test");
    }

    @Test
    public void testGetUserSession() {
         UserSession session = activeTenantDataModelManager.getUserSession();
         assertNotNull(session);
    }

    @Test
    public void testSetUserSession() {
        activeTenantDataModelManager.setUserSession(userSession);
        UserSession session = activeTenantDataModelManager.getUserSession();
        assertNotNull(session);
    }

    @Test
    public void testGetActiveTenant() {
        SystemActiveTenant systemActiveTenant = activeTenantDataModelManager.getActiveTenant();
        assertEquals((Long) 2L, systemActiveTenant.getId());
        assertEquals((Long) 2L, systemActiveTenant.getTenantId());
        assertEquals((Long) 2L, systemActiveTenant.getUserId());
    }

    @Test
    public void testSetActiveTenant() {
        SystemActiveTenant activeTenant = new ActiveTenant(4L, 4L, 4L, "test", false);
        activeTenantDataModelManager.setActiveTenant(activeTenant);
        SystemActiveTenant systemActiveTenant = activeTenantDataModelManager.getActiveTenant();
        assertEquals((Long) 4L, systemActiveTenant.getId());
        assertEquals((Long) 4L, systemActiveTenant.getTenantId());
        assertEquals((Long) 4L, systemActiveTenant.getUserId());
    }

    @Test
    public void testGetActiveTenantValue() {
        String value = activeTenantDataModelManager.getActiveTenantValue();
        assertEquals("test", value);
    }

    @Test
    public void testSetActiveTenantValue() {
activeTenantDataModelManager.setActiveTenantValue("test2");
        String value = activeTenantDataModelManager.getActiveTenantValue();
        assertEquals("test2", value);
    }

    @Test
    public void testGetActiveTenantRESTServiceAccess() {
        ActiveTenantRESTServiceAccess restServiceAccess = activeTenantDataModelManager.getActiveTenantRESTServiceAccess();
        assertNotNull(restServiceAccess);
    }

    @Test
    public void testSetActiveTenantRESTServiceAccess() {
        activeTenantDataModelManager.setActiveTenantRESTServiceAccess(activeTenantRESTServiceAccess);
        ActiveTenantRESTServiceAccess restServiceAccess = activeTenantDataModelManager.getActiveTenantRESTServiceAccess();
        assertNotNull(restServiceAccess);
    }

    @Test
    public void testGetUserActiveTenants() {
        List<SystemActiveTenant> userActiveTenantsTest = new ArrayList<>();
        SystemActiveTenant activeTenant = new ActiveTenant(5L, 5L, 5L, "test", false);
        userActiveTenantsTest.add(activeTenant);

        activeTenantDataModelManager.setUserActiveTenants(userActiveTenantsTest);
        List<? extends SystemActiveTenant> activeTenants = activeTenantDataModelManager.getUserActiveTenants();
        assertNotNull(activeTenants);
        assertEquals(1, activeTenants.size());
    }

    @Test
    public void testSetUserActiveTenants() {
        List<SystemActiveTenant> userActiveTenantsTest = new ArrayList<>();
        SystemActiveTenant activeTenant = new ActiveTenant(6L, 6L, 6L, "test", false);
        SystemActiveTenant activeTenant2 = new ActiveTenant(7L, 7L, 7L, "test", false);
        userActiveTenantsTest.add(activeTenant);
        userActiveTenantsTest.add(activeTenant2);

        activeTenantDataModelManager.setUserActiveTenants(userActiveTenantsTest);
        List<? extends SystemActiveTenant> activeTenants = activeTenantDataModelManager.getUserActiveTenants();
        assertNotNull(activeTenants);
        assertEquals(2, activeTenants.size());
    }
}