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

package io.radien.webapp.activeTenant;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import io.radien.api.model.tenant.SystemActiveTenant;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.user.SystemUser;

import io.radien.api.service.tenant.ActiveTenantRESTServiceAccess;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleUserRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.tenantmanagement.client.entities.ActiveTenant;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.tenantmanagement.client.entities.TenantType;
import io.radien.ms.tenantmanagement.client.services.ActiveTenantFactory;
import io.radien.ms.tenantmanagement.client.services.TenantFactory;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.webapp.DataModelEnum;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.security.UserSession;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;


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
import static org.mockito.Mockito.doThrow;

/**
 * Active Tenant Data Model Manager Test
 * {@link io.radien.webapp.activeTenant.ActiveTenantDataModelManager}
 *
 * @author Bruno Gama
 **/
public class ActiveTenantDataModelManagerTest {

    @InjectMocks
    private ActiveTenantDataModelManager activeTenantDataModelManager;

    @Mock
    private UserSession userSession;

    @Mock
    private ActiveTenantRESTServiceAccess activeTenantRESTServiceAccess;
    
    @Mock
    private TenantRESTServiceAccess tenantRESTServiceAccess;
    
    @Mock
    private TenantRoleUserRESTServiceAccess tenantRoleUserRESTServiceAccess;

    private FacesContext facesContext;

    private static MockedStatic<FacesContext> facesContextMockedStatic;
    private static MockedStatic<JSFUtil> jsfUtilMockedStatic;
    private static MockedStatic<PrettyContext> prettyContextMockedStatic;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @BeforeClass
    public static void beforeClass(){
        facesContextMockedStatic = Mockito.mockStatic(FacesContext.class);
        jsfUtilMockedStatic = Mockito.mockStatic(JSFUtil.class);
        prettyContextMockedStatic = Mockito.mockStatic(PrettyContext.class);
    }
    @AfterClass
    public static final void destroy(){
        if(facesContextMockedStatic!=null) {
            facesContextMockedStatic.close();
        }
        if(jsfUtilMockedStatic!=null) {
            jsfUtilMockedStatic.close();
        }
        if(prettyContextMockedStatic!=null) {
            prettyContextMockedStatic.close();
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

        SystemActiveTenant activeTenant = new ActiveTenant(2L, 2L, 2L);
        activeTenantDataModelManager.setActiveTenant(activeTenant);
        activeTenantDataModelManager.setActiveTenantValue("test");
    }

    @Test
    public void testInit() throws SystemException {
        SystemUser user = new User();
        when(userSession.getUser()).thenReturn(user);

        List<SystemActiveTenant> listOfSystemActiveTenants = new ArrayList();
        listOfSystemActiveTenants.add(ActiveTenantFactory.create(10L, 10L));

        doReturn(listOfSystemActiveTenants).when(activeTenantRESTServiceAccess).getActiveTenantByFilter(any(), any());

        activeTenantDataModelManager.init();

        SystemActiveTenant newActiveActiveTenant = ActiveTenantFactory.create(10L, 10L);

        assertEquals(newActiveActiveTenant.getUserId(), activeTenantDataModelManager.getActiveTenant().getUserId());
    }

    @Test(expected = Exception.class)
    public void testInitException() throws SystemException {
        SystemUser user = new User();
        when(userSession.getUser()).thenReturn(user);

        doThrow(new Exception()).when(activeTenantRESTServiceAccess).getActiveTenantByFilter(any(), any());

        activeTenantDataModelManager.init();
    }

    @Test
    public void testGetUserTenants() throws SystemException {
        SystemUser user = new User();
        when(userSession.getUser()).thenReturn(user);

        List<? extends SystemTenant> userTenantNames = activeTenantDataModelManager.getUserTenants();

        assertNotNull(userTenantNames);
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

        PrettyContext prettyContext = mock(PrettyContext.class);
        UrlMapping urlMapping = mock(UrlMapping.class);

        String id = "users";
        when(urlMapping.getId()).thenReturn(id);
        when(prettyContext.getCurrentMapping()).thenReturn(urlMapping);
        when(PrettyContext.getCurrentInstance()).thenReturn(prettyContext);

        String mockedContextPath = "int-env-url";
        String expectedRedirectionPath = mockedContextPath + DataModelEnum.USERS_DISPATCH_PATH.getValue();
        final List<?> urlRedirection = new ArrayList();

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
        activeTenantDataModelManager.tenantChangedValidationMethod("0");
        assertNull(activeTenantDataModelManager.getActiveTenant());
    }
    
    @Test
    public void testDeactivateTenant() {
        SystemUser user = new User();
        when(userSession.getUser()).thenReturn(user);
        
        List<SystemTenant> listOfAvailableTenants = new ArrayList<SystemTenant>();
        
        Tenant t = TenantFactory.create("name","tenantKey", TenantType.ROOT, null, null, null, null, null, null, null, null, null, null, null);
        t.setId(1L);
        listOfAvailableTenants.add(t);
        t = TenantFactory.create("name","tenantKey2", TenantType.CLIENT, null, null, null, null, null, null, null, null, null, null, null);
        t.setId(2L);
        listOfAvailableTenants.add(t);
        
        activeTenantDataModelManager.setUserActiveTenants(listOfAvailableTenants);
        
        activeTenantDataModelManager.tenantChangedValidationMethod(JSFUtil.getMessage(DataModelEnum.NO_ACTIVE_TENANT_MESSAGE.getValue()));
        
        assertNull(activeTenantDataModelManager.getActiveTenant());
    }

    @Test
    public void testTenantChangedValidationMethodToOther() throws SystemException {
        SystemUser user = new User();
        when(userSession.getUser()).thenReturn(user);
        
        List<SystemTenant> listOfAvailableTenants = new ArrayList<SystemTenant>();
        
        Tenant t = TenantFactory.create("name","tenantKey", TenantType.ROOT, null, null, null, null, null, null, null, null, null, null, null);
        t.setId(1L);
        listOfAvailableTenants.add(t);
        t = TenantFactory.create("name","tenantKey2", TenantType.CLIENT, null, null, null, null, null, null, null, null, null, null, null);
        t.setId(2L);
        listOfAvailableTenants.add(t);
        
        activeTenantDataModelManager.setUserActiveTenants(listOfAvailableTenants);
        
        activeTenantDataModelManager.tenantChangedValidationMethod("1");
        
        assertNotNull(activeTenantDataModelManager.getActiveTenant());
    }
    
    @Test
    public void testTenantChangedToSameValue() throws SystemException {
        SystemUser user = new User();
        when(userSession.getUser()).thenReturn(user);
        
        List<SystemTenant> listOfAvailableTenants = new ArrayList<SystemTenant>();
        
        Tenant t = TenantFactory.create("name","tenantKey", TenantType.ROOT, null, null, null, null, null, null, null, null, null, null, null);
        t.setId(1L);
        listOfAvailableTenants.add(t);
        t = TenantFactory.create("name","tenantKey2", TenantType.CLIENT, null, null, null, null, null, null, null, null, null, null, null);
        t.setId(2L);
        listOfAvailableTenants.add(t);
        
        activeTenantDataModelManager.setUserActiveTenants(listOfAvailableTenants);
        
        activeTenantDataModelManager.tenantChangedValidationMethod("2");
        
        assertNotNull(activeTenantDataModelManager.getActiveTenant());
    }
    
    @Test
    public void testTenantChangedTwice() throws SystemException {
        SystemUser user = new User();
        when(userSession.getUser()).thenReturn(user);
        
        List<SystemTenant> listOfAvailableTenants = new ArrayList<SystemTenant>();
        
        Tenant t = TenantFactory.create("name","tenantKey", TenantType.ROOT, null, null, null, null, null, null, null, null, null, null, null);
        t.setId(1L);
        listOfAvailableTenants.add(t);
        t = TenantFactory.create("name","tenantKey2", TenantType.CLIENT, null, null, null, null, null, null, null, null, null, null, null);
        t.setId(2L);
        listOfAvailableTenants.add(t);
        
        activeTenantDataModelManager.setUserActiveTenants(listOfAvailableTenants);
        
        activeTenantDataModelManager.tenantChangedValidationMethod("2");
        activeTenantDataModelManager.tenantChangedValidationMethod("1");
        
        assertNotNull(activeTenantDataModelManager.getActiveTenant());
    }

    @Test(expected = Exception.class)
    public void testTenantChangedValidationException() throws SystemException {
        SystemUser user = new User();
        when(userSession.getUser()).thenReturn(user);

        doThrow(new Exception()).when(activeTenantRESTServiceAccess).getActiveTenantByFilter(any(), any());

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
        SystemActiveTenant activeTenant = new ActiveTenant(4L, 4L, 4L);
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
        SystemActiveTenant activeTenant = new ActiveTenant(5L, 5L, 5L);
        userActiveTenantsTest.add(activeTenant);

        activeTenantDataModelManager.setActiveTenant(activeTenant);
        SystemActiveTenant activeTenants = activeTenantDataModelManager.getActiveTenant();
        assertNotNull(activeTenants);
    }
    
    @Test
    public void testGetUserAvailableTenants() {
        SystemUser user = new User();
        when(userSession.getUser()).thenReturn(user);
        
        List<SystemTenant> listOfAvailableTenants = new ArrayList<SystemTenant>();
        
        Tenant t = TenantFactory.create("name","tenantKey", TenantType.ROOT, null, null, null, null, null, null, null, null, null, null, null);
        t.setId(1L);
        listOfAvailableTenants.add(t);
        
        activeTenantDataModelManager.setUserActiveTenants(listOfAvailableTenants);
        
        List<? extends SystemTenant> activeTenants = activeTenantDataModelManager.getUserAvailableTenants();
        assertNotNull(activeTenants);
    }

    @Test
    public void testSetUserActiveTenants() {
        List<SystemActiveTenant> userActiveTenantsTest = new ArrayList<>();
        SystemActiveTenant activeTenant = new ActiveTenant(6L, 6L, 6L);
        SystemActiveTenant activeTenant2 = new ActiveTenant(7L, 7L, 7L);
        userActiveTenantsTest.add(activeTenant);
        userActiveTenantsTest.add(activeTenant2);

        activeTenantDataModelManager.setActiveTenant(activeTenant);
        activeTenantDataModelManager.setActiveTenant(activeTenant2);
        SystemActiveTenant activeTenants = activeTenantDataModelManager.getActiveTenant();
        assertNotNull(activeTenants);
    }
}