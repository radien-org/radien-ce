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
package io.radien.webapp.user;

import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.service.linked.authorization.LinkedAuthorizationRESTServiceAccess;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.rolemanagement.client.entities.LinkedAuthorization;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.tenantmanagement.client.entities.TenantType;
import io.radien.webapp.JSFUtil;
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

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;

import static io.radien.ms.rolemanagement.client.services.LinkedAuthorizationFactory.create;
import static io.radien.ms.tenantmanagement.client.services.TenantFactory.create;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.nullable;

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
    private TenantRESTServiceAccess tenantRESTServiceAccess;

    @Mock
    private LinkedAuthorizationRESTServiceAccess linkedAuthorizationRESTServiceAccess;

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
    }

    /**
     * Auxiliary method that creates an Optional containing a Tenant
     * @param tenantId tenant identifier that will be used to created a dummy tenant (for test purposes)
     * @return Optional containing a tenant
     */
    protected Optional<Tenant> getTenantOptional(Long tenantId) {
        String description = "tenant-" + tenantId;
        Tenant tenant = create(description, description, TenantType.SUB_TENANT, null,
                null, null,null, null, null,
                null, null, null, null, null);
        return Optional.of(tenant);
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

        List<LinkedAuthorization> linkedAuthorizations = new ArrayList<>();

        // Expected LinkedAuthorization beans
        linkedAuthorizations.add(create(1L, 1L, 1L, userId, null));
        linkedAuthorizations.add(create(1L, 1L, 2L, userId, null));
        linkedAuthorizations.add(create(2L, 1L, 3L, userId, null));
        linkedAuthorizations.add(create(3L, 1L, 1L, userId, null));
        linkedAuthorizations.add(create(3L, 1L, 1L, userId, null));

        when(linkedAuthorizationRESTServiceAccess.getSpecificAssociationByUserId(userId)).
                then(i -> linkedAuthorizations);

        // Expected tenants
        when(tenantRESTServiceAccess.getTenantById(1L)).then(i -> getTenantOptional(1L));
        when(tenantRESTServiceAccess.getTenantById(2L)).then(i -> getTenantOptional(2L));
        when(tenantRESTServiceAccess.getTenantById(3L)).then(i -> getTenantOptional(3L));

        List<? extends SystemTenant> retrieved = this.userProfileManager.retrieveAssignedTenants();
        assertNotNull(retrieved);
        assertEquals(3, retrieved.size());
    }

    /**
     * Test for method retrieveAssignedTenants
     * Expected (FAIL): Could not retrieve tenants due error with communication with the endpoint
     * responsible for retrieve linked authorizations
     * @throws SystemException states that an issue occurred during the communication with the endpoint
     */
    @Test
    public void testRetrieveAssignedTenantsWhenSystemExceptionOccurs() throws SystemException {
        String error = "error retrieving linked authorizations";
        Long userId = 111L;
        when(userSession.getUserId()).then(i -> userId);

        when(linkedAuthorizationRESTServiceAccess.getSpecificAssociationByUserId(userId)).
                thenThrow(new SystemException(error));

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
        List<? extends SystemTenant> assigned = new ArrayList<>();
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
        when(userSession.getUserId()).then(i -> userId);

        List<LinkedAuthorization> linkedAuthorizations = new ArrayList<>();

        // Expected LinkedAuthorization beans
        linkedAuthorizations.add(create(1000L, 1L, 1L, userId, null));
        try {
            when(linkedAuthorizationRESTServiceAccess.getSpecificAssociationByUserId(userId)).
                    then(i -> linkedAuthorizations);
        } catch (SystemException s) {
            fail("not expected");
        }

        // Expected tenants
        try {
            when(tenantRESTServiceAccess.getTenantById(1000L)).then(i -> getTenantOptional(1000L));
        } catch (SystemException s) {
            fail("not expected");
        }

        this.userProfileManager.init();
        List<? extends SystemTenant> assignedTenantsForCurrentUser = this.userProfileManager.getAssignedTenants();
        assertNotNull(assignedTenantsForCurrentUser);
        assertEquals(1, assignedTenantsForCurrentUser.size());
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
            when(linkedAuthorizationRESTServiceAccess.getSpecificAssociationByUserId(userId)).
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
            when(linkedAuthorizationRESTServiceAccess.getSpecificAssociationByUserId(userId)).
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
        Long tenantId = 1L;
        String errorMsg = "error retrieving tenant";
        when(userSession.getUserId()).then(i -> userId);

        // Expected LinkedAuthorization beans
        List<LinkedAuthorization> linkedAuthorizations = new ArrayList<>();
        linkedAuthorizations.add(create(tenantId, 1L, 1L, userId, null));
        try {
            when(linkedAuthorizationRESTServiceAccess.getSpecificAssociationByUserId(userId)).
                    then(i -> linkedAuthorizations);
        } catch (SystemException s) {
            fail("unexpected");
        }

        try {
            when(tenantRESTServiceAccess.getTenantById(tenantId)).
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
            when(linkedAuthorizationRESTServiceAccess.deleteAssociations(tenant.getId(), userId)).
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
            when(linkedAuthorizationRESTServiceAccess.deleteAssociations(tenant.getId(), userId)).
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
