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
package io.radien.webapp.user;

import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.tenantrole.TenantRoleRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleUserRESTServiceAccess;
import io.radien.api.service.user.UserRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.tenantmanagement.client.entities.TenantType;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.security.UserSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
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

import static io.radien.ms.tenantmanagement.client.services.TenantFactory.create;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Class that aggregates UnitTest cases for UserProfileManager
 * @author Newton Carvalho
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({JSFUtil.class, FacesContext.class, ExternalContext.class})
public class UserProfileManagerTest {

    @InjectMocks
    private UserProfileManager userProfileManager;

    @Mock
    private UserSession userSession;

    @Mock
    private UserRESTServiceAccess userRESTServiceAccess;

    @Mock
    private TenantRoleRESTServiceAccess tenantRoleRESTServiceAccess;

    @Mock
    private TenantRoleUserRESTServiceAccess tenantRoleUserRESTServiceAccess;

    private FacesContext facesContext;

    private SystemUser systemUser = new User();

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

        systemUser.setId(117L);
        systemUser.setFirstname("firstname");
        systemUser.setLastname("lastname");
    }

    /**
     * Auxiliary method that creates an list containing Tenants
     * @param tenantIds tenant identifiers that will be used to created a dummy tenant list(for test purposes)
     * @return list containing tenants
     */
    protected List<? extends SystemTenant> getMockedTenants(List<Long> tenantIds) {
        List<SystemTenant> tenants = new ArrayList<>();
        String description;
        for (Long tenantId:tenantIds) {
            description = "tenant-" + tenantId;
            Tenant tenant = create(description, description, TenantType.SUB, null,
                    null, null,null, null, null,
                    null, null, null, null, null);
            tenants.add(tenant);
        }
        return tenants;
    }


    /**
     * Test for method retrieveAssignedTenants
     * Expected (SUCCESS): Retrieve the Tenants currently associated with the session user
     * @throws SystemException states that an issue occurred during the communication with the endpoint
     */
    @Test
    public void testRetrieveAssignedTenants() throws SystemException {
        Long userId = 111L;
        when(userSession.getUserId()).then(i -> userId);

        // Expected tenants
        when(tenantRoleRESTServiceAccess.getTenants(userId, null)).then(i -> getMockedTenants(Arrays.asList(1L, 2L, 3L)));

        List<? extends SystemTenant> retrieved = this.userProfileManager.retrieveAssignedTenants();
        assertNotNull(retrieved);
        assertEquals(3, retrieved.size());
    }

    /**
     * Test for method retrieveAssignedTenants
     * Expected (FAIL): Could not retrieve tenants due error with communication with the endpoint
     * responsible for retrieve tenants (through TenantRole)
     * @throws SystemException states that an issue occurred during the communication with the endpoint
     */
    @Test
    public void testRetrieveAssignedTenantsWhenSystemExceptionOccurs() throws SystemException {
        String error = "error retrieving assigned tenants";
        Long userId = 111L;
        when(userSession.getUserId()).then(i -> userId);

        when(tenantRoleRESTServiceAccess.getTenants(userId, null)).thenThrow(new SystemException(error));

        List<? extends SystemTenant> retrieved = this.userProfileManager.retrieveAssignedTenants();
        assertNotNull(retrieved);
        assertTrue(retrieved.isEmpty());

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
        assertEquals("rd_retrieve_error", captured.getSummary());
        assertTrue(captured.getDetail().contains(error));
    }

    /**
     * Test for the (Listener) method used by bootsfaces DataTable component to select one Tenant
     */
    @Test
    public void testOnTenantSelect() {
        Tenant selectedOne = new Tenant(); selectedOne.setId(99999L);
        userProfileManager.onTenantSelect(selectedOne, "row", null);
        assertEquals(selectedOne, userProfileManager.getSelectedTenantToUnAssign());
    }

    /**
     * Test for the getter/setter methods related to the property assignedTenants.
     * This property corresponds to the tenants assigned to a currently logged user
     */
    @Test
    public void testGetterSetterForAssignedTenants() {
        List<SystemTenant> assigned = new ArrayList<>();
        userProfileManager.setAssignedTenants(assigned);
        assertEquals(assigned, userProfileManager.getAssignedTenants());
    }

    /**
     * Test for the getter/setter methods related to the property selectedTenantToUnAssign.
     * This property corresponds to the tenant which the user selects for perform
     * the self (un)assignment
     */
    @Test
    public void testGetterSetterForSelectedTenantToUnAssign() {
        Tenant selectedOne = new Tenant(); selectedOne.setId(9L);
        userProfileManager.setSelectedTenantToUnAssign(selectedOne);
        assertEquals(selectedOne, userProfileManager.getSelectedTenantToUnAssign());
    }

    /**
     * Test for getters and setter methods regarding tabIndex attribute
     */
    @Test
    public void testGetterSetterForTabIndex() {
        userProfileManager.setTabIndex(999L);
        assertEquals(new Long(999L), userProfileManager.getTabIndex());
    }

    /**
     * Test for the initialization callback method
     * Expected outcome (SUCCESS): Retrieve assigned tenants
     */
    @Test
    public void testInit() {
        Long userId = 89L;
        SystemUser clonedSystemUser = new User((User) systemUser);
        userProfileManager.setClonedLogInUser(clonedSystemUser);

        when(userSession.getUserId()).then(i -> userId);
        when(userSession.isActive()).then(isActive -> true);
        when(userSession.getUser()).then(user -> systemUser);

        // Expected tenants
        try {
            when(tenantRoleRESTServiceAccess.getTenants(userId, null)).
                    then(i -> getMockedTenants(Collections.singletonList(3L)));
        } catch (SystemException s) {
            fail("not expected");
        }

        this.userProfileManager.init();
        List<? extends SystemTenant> assignedTenantsForCurrentUser = this.userProfileManager.getAssignedTenants();
        assertNotNull(assignedTenantsForCurrentUser);
        assertEquals(1, assignedTenantsForCurrentUser.size());

        assertNotEquals(systemUser, userProfileManager.getClonedLogInUser());
        assertEquals(systemUser.getFirstname(), userProfileManager.getClonedLogInUser().getFirstname());
        assertNotNull(userProfileManager.getClonedLogInUser());
    }

    /**
     * Test for the initialization callback method
     * Expected outcome (SUCCESS): Empty list cause not Linked Authorizations for the current user
     */
    @Test
    public void testInitWhenNoAssignments() {
        Long userId = 99L;
        when(userSession.getUserId()).then(i -> userId);
        try {
            when(tenantRoleRESTServiceAccess.getTenants(userId, null)).
                    then(i -> new ArrayList<>());
        } catch (SystemException s) {
            fail("not expected");
        }
        this.userProfileManager.init();
        List<? extends SystemTenant> assignedTenantsForCurrentUser = this.userProfileManager.getAssignedTenants();
        assertNotNull(assignedTenantsForCurrentUser);
        assertEquals(0, assignedTenantsForCurrentUser.size());
    }

    /**
     * Test for the initialization callback method
     * Expected outcome (FAIL): An exception occurs when trying to retrieve the linked authorizations
     */
    @Test
    public void testInitWhenLinkedAuthorizationsRetrievalFail() {
        Long userId = 99L;
        String errorMsg = "error retrieving linked authorizations";
        when(userSession.getUserId()).then(i -> userId);
        try {
            when(tenantRoleRESTServiceAccess.getTenants(userId, null)).
                    thenThrow(new SystemException(errorMsg));
        } catch (SystemException s) {
            fail("not expected");
        }
        this.userProfileManager.init();
        List<? extends SystemTenant> assignedTenantsForCurrentUser = this.userProfileManager.getAssignedTenants();
        assertNotNull(assignedTenantsForCurrentUser);
        assertEquals(0, assignedTenantsForCurrentUser.size());

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
        assertEquals("rd_retrieve_error", captured.getSummary());
        assertTrue(captured.getDetail().contains(errorMsg));
    }

    /**
     * Test for the initialization callback method
     * Expected outcome (FAIL): An exception occurs when trying to retrieve the assigned tenants
     */
    @Test
    public void testInitWhenAssignedTenantsRetrievalFail() {
        Long userId = 88L;
        String errorMsg = "error retrieving tenant";
        when(userSession.getUserId()).then(i -> userId);

        // Expected LinkedAuthorization beans
        try {
            when(tenantRoleRESTServiceAccess.getTenants(userId, null)).
                    thenThrow(new SystemException(errorMsg));
        } catch (SystemException s) {
            fail("unexpected");
        }

        this.userProfileManager.init();
        List<? extends SystemTenant> assignedTenantsForCurrentUser = this.userProfileManager.getAssignedTenants();
        assertNotNull(assignedTenantsForCurrentUser);
        assertEquals(0, assignedTenantsForCurrentUser.size());

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
        assertEquals("rd_retrieve_error", captured.getSummary());
        assertTrue(captured.getDetail().contains(errorMsg));
    }

    /**
     * Test method - init()
     * Test case exception in clonedLoginUser
     * assert null login user
     */
    @Test
    public void testInitWithException() {
        doReturn(true).when(this.userSession).isActive();
        doThrow(new RuntimeException()).when(this.userSession).getUser();
        userProfileManager.init();

        assertNull(userProfileManager.getClonedLogInUser());
    }

    /**
     * Test method updateProfile()
     * Test case success
     * asserts equality of home HTML page
     */
    @Test
    public void testUpdateProfile() {
        systemUser.setFirstname("firstname-update");
        doReturn(true).when(userRESTServiceAccess).updateUser(systemUser);

        assertEquals("pretty:index", userProfileManager.updateProfile(systemUser));
    }

    /**
     * Test method updateProfile()
     * Test case exception
     * asserts equality of user profile HTML page
     */
    @Test
    public void testUpdateProfileWithException() {
        systemUser.setFirstname("firstname-update");
        doThrow(new RuntimeException()).when(userRESTServiceAccess).updateUser(systemUser);

        assertEquals("pretty:profile", userProfileManager.updateProfile(systemUser));
    }

    /**
     * Test for method dissociateUserTenant.
     * Expected outcome (FAIL): No tenant selected to perform this operation
     */
    @Test
    public void testDissociateTenantForUserWhenNoSelectedTenant() {
        userProfileManager.setSelectedTenantToUnAssign(null);
        String returnUrl = userProfileManager.dissociateUserTenant();

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
        assertEquals("rd_tenant_user_dissociation_error", captured.getSummary());
        assertTrue(captured.getDetail().contains("rd_tenant_not_selected"));

        assertEquals(new Long(1), userProfileManager.getTabIndex());
        assertEquals(userProfileManager.getLoggerUserGui(), returnUrl);
    }

    /**
     * Test for method dissociateUserTenant.
     * Expected outcome (FAIL): Error occurs during the dissociation process (Communication with endpoint fails)
     */
    @Test
    public void testDissociateTenantForUserWhenErrorOccurs() {
        Long userId = 99L;
        when(userSession.getUserId()).then(i -> userId);
        Tenant tenant = new Tenant(); tenant.setId(1111L);
        userProfileManager.setSelectedTenantToUnAssign(tenant);

        String errorMsg = "dissociation fail";

        try {
            when(tenantRoleUserRESTServiceAccess.unAssignUser(tenant.getId(), null, userId)).
                    thenThrow(new SystemException(errorMsg));
        } catch (SystemException s) {
            fail("unexpected");
        }

        String returnUrl = userProfileManager.dissociateUserTenant();

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
        assertEquals("rd_tenant_user_dissociation_error", captured.getSummary());
        assertTrue(captured.getDetail().contains(errorMsg));

        assertEquals(new Long(1), userProfileManager.getTabIndex());
        assertEquals(userProfileManager.getLoggerUserGui(), returnUrl);
    }

    /**
     * Test for method dissociateUserTenant.
     * Expected outcome (SUCCESS): Dissociation performed without issue
     */
    @Test
    public void testDissociateTenantForUser() {
        Long userId = 99L;
        when(userSession.getUserId()).then(i -> userId);
        Tenant tenant = new Tenant(); tenant.setId(1111L);
        userProfileManager.setSelectedTenantToUnAssign(tenant);
        try {
            when(tenantRoleUserRESTServiceAccess.unAssignUser(tenant.getId(), null, userId)).
                    thenReturn(Boolean.TRUE);
        } catch (SystemException s) {
            fail("unexpected");
        }

        String returnUrl = userProfileManager.dissociateUserTenant();

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_INFO, captured.getSeverity());
        assertEquals("rd_tenant_user_dissociation_success", captured.getSummary());

        assertEquals(new Long(0), userProfileManager.getTabIndex());
        assertEquals(userProfileManager.getHomeGui(), returnUrl);
    }
}
