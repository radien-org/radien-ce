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
package io.radien.webapp.tenantrole;

import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.role.SystemRole;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.service.role.RoleRESTServiceAccess;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.permissionmanagement.client.entities.Permission;
import io.radien.ms.rolemanagement.client.entities.Role;
import io.radien.ms.rolemanagement.client.entities.TenantRole;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.tenantmanagement.client.entities.TenantType;
import io.radien.webapp.DataModelEnum;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.authz.WebAuthorizationChecker;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

import static io.radien.webapp.tenantrole.TenantRoleAssociationManager.K_TENANT_ROLE_SCREEN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Class that aggregates UnitTest cases for TenantRoleAssociationManager
 * @author Newton Carvalho
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({JSFUtil.class, FacesContext.class, ExternalContext.class})
public class TenantRoleAssociationManagerTest {

    @InjectMocks
    private TenantRoleAssociationManager tenantRoleAssociationManager;

    @Mock
    private WebAuthorizationChecker webAuthorizationChecker;

    @Mock
    private TenantRoleRESTServiceAccess tenantRoleRESTServiceAccess;

    @Mock
    private RoleRESTServiceAccess roleRESTServiceAccess;

    @Mock
    private TenantRESTServiceAccess tenantRESTServiceAccess;

    private FacesContext facesContext;

    /**
     * Method variables preparation
     */
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
     * Test for method associateUser(Long userId, String urlMappingReturn).
     * Success case
     */
    @Test
    public void testAssociateUser() throws SystemException {
        Long userId = 111L;
        SystemTenant tenant = new Tenant(); tenant.setId(1L);
        SystemRole role = new Role(); role.setId(2L);
        tenantRoleAssociationManager.setRole(role);
        tenantRoleAssociationManager.setTenant(tenant);

        String expectedUrlMappingForRedirection = "pretty:users";

        doReturn(Boolean.FALSE).when(tenantRoleRESTServiceAccess).
                exists(tenant.getId(), role.getId());
        doReturn(Boolean.TRUE).when(tenantRoleRESTServiceAccess).save(any());
        doReturn(Boolean.TRUE).when(tenantRoleRESTServiceAccess).assignUser(tenant.getId(),
                role.getId(), userId);

        String urlMapping = tenantRoleAssociationManager.associateUser(userId);

        assertNotNull(urlMapping);
        assertEquals(urlMapping, expectedUrlMappingForRedirection);
    }

    /**
     * Test for method associateTenantRole
     * This method crates the first association predicted in the graph (The association
     * between a Tenant and a Role). If the process finishes successfully
     * is expected a rd_save_success FacesMessage.
     */
    @Test
    public void testAssociateTenantRole() throws Exception {
        SystemTenant tenant = new Tenant(); tenant.setId(1L);
        SystemRole role = new Role(); role.setId(2L);
        SystemTenantRole tenantRole = new TenantRole();

        tenantRoleAssociationManager.setRole(role);
        tenantRoleAssociationManager.setTenant(tenant);
        tenantRoleAssociationManager.setTenantRole(tenantRole);

        when(tenantRoleRESTServiceAccess.save(tenantRole)).thenReturn(Boolean.TRUE);
        tenantRoleAssociationManager.associateTenantRole();

        assertTrue(tenantRoleAssociationManager.isExistsTenantRoleCreated());

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_INFO, captured.getSeverity());
        assertEquals(DataModelEnum.SAVE_SUCCESS_MESSAGE.getValue(), captured.getSummary());
        assertEquals(tenantRoleAssociationManager.getTabIndex(), new Long(0L));
    }

    /**
     * Test for method associateTenantRole, but for this case
     * an exception occurs during the saving process of
     * TenantRole association. Is expected a rd_save_error FacesMessage.
     */
    @Test
    public void testAssociateTenantRoleWithExceptionOccurring() throws Exception {
        tenantRoleAssociationManager.setRole(new Role());
        tenantRoleAssociationManager.setTenant(new Tenant());
        tenantRoleAssociationManager.setTenantRole(new TenantRole());
        when(tenantRoleRESTServiceAccess.save(any(TenantRole.class))).
                    thenThrow(new RuntimeException("Error during save process"));
        tenantRoleAssociationManager.associateTenantRole();

        assertFalse(tenantRoleAssociationManager.isExistsTenantRoleCreated());
        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
        assertEquals(DataModelEnum.SAVE_ERROR_MESSAGE.getValue(), captured.getSummary());
        assertEquals(tenantRoleAssociationManager.getTabIndex(), new Long(0L));
    }

    /**
     * Test for method associateUser(Long userId, String urlMappingReturn).
     * Failure case
     */
    @Test
    public void testAssociateUserFailureCase() throws SystemException {
        Long userId = 111L;
        SystemTenant tenant = new Tenant(); tenant.setId(1L);
        SystemRole role = new Role(); role.setId(2L);

        tenantRoleAssociationManager.setRole(role);
        tenantRoleAssociationManager.setTenant(tenant);

        doThrow(new RuntimeException("Error checking exists")).when(tenantRoleRESTServiceAccess).
                exists(tenant.getId(), role.getId());

        doReturn(Boolean.TRUE).when(tenantRoleRESTServiceAccess).save(any());
        doReturn(Boolean.TRUE).when(tenantRoleRESTServiceAccess).assignUser(tenant.getId(),
                role.getId(), userId);

        String urlMapping = tenantRoleAssociationManager.associateUser(userId);

        assertNotNull(urlMapping);
        assertEquals("pretty:userTenantAssociation", urlMapping);
    }

    /**
     * Test for getters and setter methods regarding Tenant attribute
     */
    @Test
    public void testGetterSetterForTenant() {
        SystemTenant tenant = mock(SystemTenant.class);
        tenantRoleAssociationManager.setTenant(tenant);
        assertEquals(tenantRoleAssociationManager.getTenant(), tenant);
    }

    /**
     * Test for getters and setter methods regarding Role attribute
     */
    @Test
    public void testGetterSetterForRole() {
        SystemRole role = mock(SystemRole.class);
        tenantRoleAssociationManager.setRole(role);
        assertEquals(tenantRoleAssociationManager.getRole(), role);
    }

    /**
     * Test for getters and setter methods regarding Permission attribute
     */
    @Test
    public void testGetterSetterForPermission() {
        SystemPermission permission = mock(SystemPermission.class);
        tenantRoleAssociationManager.setPermission(permission);
        assertEquals(tenantRoleAssociationManager.getPermission(), permission);
    }

    /**
     * Test for getters and setter methods regarding tabIndex attribute
     */
    @Test
    public void testGetterSetterForTabIndex() {
        tenantRoleAssociationManager.setTabIndex(999L);
        assertEquals(tenantRoleAssociationManager.getTabIndex(), new Long(999L));
    }

    /**
     * Test for getters and setter methods regarding TenantRole attribute
     */
    @Test
    public void testGetterSetterTenantRole() {
        SystemTenantRole tenantRole = mock(SystemTenantRole.class);
        tenantRoleAssociationManager.setTenantRole(tenantRole);
        assertEquals(tenantRoleAssociationManager.getTenantRole(), tenantRole);
    }
    /**
     * Test for getters and setter methods regarding assignedPermissions attribute
     */
    @Test
    public void testGetterSetterAssignedPermission() {
        List<SystemPermission> assignedPermissions = new ArrayList<>();
        tenantRoleAssociationManager.setAssignedPermissions(assignedPermissions);
        assertEquals(tenantRoleAssociationManager.getAssignedPermissions(),
                assignedPermissions);
    }

    /**
     * Test for method getInitialRolesAllowedForAssociation().
     * The original method returns a list containing Pre-Defined roles (Not administrative ones)
     * that can be used to do the association between (user - tenant - role).
     * Success Case
     */
    @Test
    public void testGetInitialRolesAllowedForAssociation() throws Exception {
        doReturn(Optional.of(new Role())).when(roleRESTServiceAccess).getRoleByName(any());
        List<? extends SystemRole> initialRoles =
                tenantRoleAssociationManager.getInitialRolesAllowedForAssociation();
        assertNotNull(initialRoles);
        assertFalse(initialRoles.isEmpty());
    }

    /**
     * Test for method getInitialRolesAllowedForAssociation().
     * Failure Case
     */
    @Test
    public void testGetInitialRolesAllowedForAssociationExceptionCase() throws Exception {
        doThrow(new SystemException("error")).when(roleRESTServiceAccess).getRoleByName(any());
        List<? extends SystemRole> initialRoles =
                tenantRoleAssociationManager.getInitialRolesAllowedForAssociation();
        assertNotNull(initialRoles);
        assertTrue(initialRoles.isEmpty());
    }

    /**
     * Test for the method getTenantsFromCurrentUser().
     * It returns a list containing Tenants for which the current user has Administrative roles.
     * Success Case.
     */
    @Test
    public void testGetTenantsFromCurrentUser() throws SystemException {
        Long currentUserId = 1L;
        List<SystemTenant> expectedTenants = new ArrayList<>();

        SystemTenant tenant = new Tenant(); tenant.setId(1L); tenant.setTenantType(TenantType.ROOT_TENANT);
        expectedTenants.add(tenant);
        tenant = new Tenant(); tenant.setId(2L); tenant.setTenantType(TenantType.CLIENT_TENANT);
        expectedTenants.add(tenant);

        doReturn(currentUserId).when(this.webAuthorizationChecker).getCurrentUserId();
        doReturn(expectedTenants).when(this.tenantRoleRESTServiceAccess).getTenants(currentUserId, null);

        List<? extends SystemTenant> outcome = this.tenantRoleAssociationManager.getTenantsFromCurrentUser();
        assertEquals(expectedTenants, outcome);
    }

    /**
     * Test for the method getTenantsFromCurrentUser().
     * Failure Case.
     */
    @Test
    public void testGetTenantsFromCurrentUserFailureCase() throws SystemException {
        Long currentUserId = 1L;

        doThrow(new SystemException("error obtaining user id")).
                doReturn(currentUserId).when(this.webAuthorizationChecker).getCurrentUserId();

        doThrow(new SystemException("error retrieving tenant")).
                when(this.tenantRoleRESTServiceAccess).getTenants(currentUserId, null);

        List<? extends SystemTenant> outcome = this.tenantRoleAssociationManager.getTenantsFromCurrentUser();
        assertNotNull(outcome);
        assertTrue(outcome.isEmpty());

        outcome = this.tenantRoleAssociationManager.getTenantsFromCurrentUser();
        assertNotNull(outcome);
        assertTrue(outcome.isEmpty());
    }

    /**
     * Test for method prepareToCreateTenantRole
     * This method prepares the frontend gui to expose the information
     * regarding the TenantRole to be create (Set some flags to the necessary with initial values)
     * return uri mapping id "tenantrole";
     */
    @Test
    public void testPrepareToCreateTenantRole() {
        SystemTenant dirtyTenantValue = new Tenant(); dirtyTenantValue.setId(111L);
        tenantRoleAssociationManager.setTenant(dirtyTenantValue);
        SystemRole dirtyRoleValue = new Role(); dirtyRoleValue.setId(100L);
        tenantRoleAssociationManager.setRole(dirtyRoleValue);
        SystemTenantRole dirtyTenantRole = new TenantRole();
        dirtyTenantRole.setId(1L);
        dirtyTenantRole.setRoleId(dirtyRoleValue.getId());
        dirtyTenantRole.setTenantId(dirtyTenantValue.getId());
        tenantRoleAssociationManager.setTenantRole(dirtyTenantRole);

        String returnUri = this.tenantRoleAssociationManager.prepareToCreateTenantRole();
        assertEquals(tenantRoleAssociationManager.getTabIndex(), new Long(0L));
        assertNotEquals(dirtyTenantRole, tenantRoleAssociationManager.getTenantRole());
        assertNull(tenantRoleAssociationManager.getTenantRole().getId());
        assertNotEquals(dirtyRoleValue, tenantRoleAssociationManager.getRole());
        assertNull(tenantRoleAssociationManager.getRole().getId());
        assertNotEquals(dirtyTenantValue, tenantRoleAssociationManager.getTenant());
        assertNull(tenantRoleAssociationManager.getTenant().getId());

        assertFalse(tenantRoleAssociationManager.isExistsTenantRoleCreated());
        assertEquals(K_TENANT_ROLE_SCREEN, returnUri);
    }

    /**
     * Test for method edit(SystemTenantRole), which prepares the frontend gui
     * to expose the information related to the TenantRole that wil be edited.
     * Return uri mapping referring the value tenantrole
     * @throws SystemException in case of any rest client communication issue
     * @Exception still thrown by role rest client
     */
    @Test
    public void testEditTenantRole() throws Exception {
        SystemTenantRole tenantRoleToBeEdited = new TenantRole();
        tenantRoleToBeEdited.setTenantId(1L);
        tenantRoleToBeEdited.setRoleId(2L);
        tenantRoleToBeEdited.setId(3L);

        SystemRole expectedRole = new Role();
        expectedRole.setId(2L);

        SystemTenant expectedTenant = new Tenant();
        expectedTenant.setId(1L);

        List<? extends SystemPermission> expectedAssociatedPermissions = new ArrayList<>();

        when(roleRESTServiceAccess.getRoleById(tenantRoleToBeEdited.getRoleId())).
                thenReturn(Optional.of(expectedRole));
        when(tenantRESTServiceAccess.getTenantById(tenantRoleToBeEdited.getTenantId())).
                thenReturn(Optional.of(expectedTenant));
        when(tenantRoleRESTServiceAccess.getPermissions(expectedTenant.getId(),
                expectedRole.getId(), null)).then(i -> expectedAssociatedPermissions);

        String returnUriMappingId = this.tenantRoleAssociationManager.
                edit(tenantRoleToBeEdited);

        assertEquals(tenantRoleAssociationManager.getRole(), expectedRole);
        assertEquals(tenantRoleAssociationManager.getTenant(), expectedTenant);
        assertEquals(tenantRoleAssociationManager.getAssignedPermissions(),
                expectedAssociatedPermissions);
        assertEquals(K_TENANT_ROLE_SCREEN, returnUriMappingId);
    }

    /**
     * Test for method edit(SystemTenantRole), which prepares the frontend gui
     * to expose the information related to the TenantRole that wil be edited.
     *
     * This particular case will simulate an exception occurs while
     * retrieving role.
     *
     * Expected Return uri mapping referring the value tenantrole
     * @throws Exception thrown by role rest client
     */
    @Test
    public void testEditTenantRoleWhenExceptionOccursForRoleRetrieval() throws Exception {
        SystemTenantRole tenantRoleToBeEdited = new TenantRole();
        tenantRoleToBeEdited.setTenantId(1L);
        tenantRoleToBeEdited.setRoleId(2L);
        tenantRoleToBeEdited.setId(3L);

        when(roleRESTServiceAccess.getRoleById(tenantRoleToBeEdited.getRoleId())).
                thenThrow(new RuntimeException("Error retrieving role"));

        String returnUriMappingId = this.tenantRoleAssociationManager.
                edit(tenantRoleToBeEdited);
        
        assertEquals(K_TENANT_ROLE_SCREEN, returnUriMappingId);

        assertTrue(tenantRoleAssociationManager.isExistsTenantRoleCreated());
        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
        assertEquals(DataModelEnum.EDIT_ERROR_MESSAGE.getValue(), captured.getSummary());
    }

    /**
     * Test for method edit(SystemTenantRole), which prepares the frontend gui
     * to expose the information related to the TenantRole that wil be edited.
     *
     * This particular case will simulate an exception occurs while
     * retrieving tenant.
     *
     * Expected Return uri mapping referring the value tenantrole
     * @throws SystemException in case of any rest client communication issue
     * @throws Exception thrown by role rest client
     */
    @Test
    public void testEditTenantRoleWhenExceptionOccursForTenantRetrieval() throws SystemException, Exception {
        SystemTenantRole tenantRoleToBeEdited = new TenantRole();
        tenantRoleToBeEdited.setTenantId(1L);
        tenantRoleToBeEdited.setRoleId(2L);
        tenantRoleToBeEdited.setId(3L);

        SystemRole role = new Role(); role.setId(tenantRoleToBeEdited.getRoleId());
        when(roleRESTServiceAccess.getRoleById(tenantRoleToBeEdited.getRoleId())).
                then(i -> Optional.of(role));
        when(tenantRESTServiceAccess.getTenantById(tenantRoleToBeEdited.getTenantId())).
                thenThrow(new RuntimeException("Error retrieving tenant"));

        String returnUriMappingId = this.tenantRoleAssociationManager.
                edit(tenantRoleToBeEdited);

        assertEquals(K_TENANT_ROLE_SCREEN, returnUriMappingId);

        assertTrue(tenantRoleAssociationManager.isExistsTenantRoleCreated());
        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
        assertEquals(DataModelEnum.EDIT_ERROR_MESSAGE.getValue(), captured.getSummary());
    }

    /**
     * Test for method edit(SystemTenantRole), which prepares the frontend gui
     * to expose the information related to the TenantRole that wil be edited.
     *
     * This particular case will simulate an exception occurs while
     * retrieving the assigned permissions.
     *
     * Expected Return uri mapping referring the value tenantrole
     * @throws SystemException in case of any rest client communication issue
     * @throws Exception thrown by role rest client
     */
    @Test
    public void testEditTenantRoleExceptionOccursWhenCalculateAssignedPermissions() throws SystemException, Exception {
        SystemTenantRole tenantRoleToBeEdited = new TenantRole();
        tenantRoleToBeEdited.setTenantId(1L);
        tenantRoleToBeEdited.setRoleId(2L);
        tenantRoleToBeEdited.setId(3L);

        SystemRole expectedRole = new Role();
        expectedRole.setId(2L);

        SystemTenant expectedTenant = new Tenant();
        expectedTenant.setId(1L);
        when(roleRESTServiceAccess.getRoleById(tenantRoleToBeEdited.getRoleId())).
                thenReturn(Optional.of(expectedRole));
        when(tenantRESTServiceAccess.getTenantById(tenantRoleToBeEdited.getTenantId())).
                thenReturn(Optional.of(expectedTenant));

        Exception e = new RuntimeException("Error retrieving assigned Permissions");
        when(tenantRoleRESTServiceAccess.getPermissions(expectedTenant.getId(),
                expectedRole.getId(), null)).
                thenThrow(e);

        String returnUriMappingId = this.tenantRoleAssociationManager.
                edit(tenantRoleToBeEdited);

        assertEquals(tenantRoleAssociationManager.getRole(), expectedRole);
        assertEquals(tenantRoleAssociationManager.getTenant(), expectedTenant);
        assertTrue(tenantRoleAssociationManager.getAssignedPermissions().isEmpty());
        assertEquals(K_TENANT_ROLE_SCREEN, returnUriMappingId);

        assertTrue(tenantRoleAssociationManager.isExistsTenantRoleCreated());
        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
        assertEquals(DataModelEnum.RETRIEVE_ERROR_MESSAGE.getValue(), captured.getSummary());
        assertEquals(e.getMessage(), captured.getDetail());
    }

    /**
     * Test the method assignPermission(): the one which does/perform permission assignment
     * Perform the association between permission, tenant and role (Tenant and Role are required
     * and must be previously selected from a GUI)
     */
    @Test
    public void testAssignPermission() throws SystemException {
        SystemRole role = new Role(); role.setId(2L);
        SystemTenant tenant = new Tenant(); tenant.setId(1L);
        SystemPermission permission = new Permission(); permission.setId(3L);
        tenantRoleAssociationManager.setPermission(permission);
        tenantRoleAssociationManager.setRole(role);
        tenantRoleAssociationManager.setTenant(tenant);

        List<SystemPermission> expectedAssociatedPermissions = new ArrayList<>();
        expectedAssociatedPermissions.add(permission);
        when(tenantRoleRESTServiceAccess.assignPermission(tenant.getId(), role.getId(), null)).
                then(i -> Boolean.TRUE);
        when(tenantRoleRESTServiceAccess.getPermissions(tenant.getId(),
                role.getId(), null)).then(i -> expectedAssociatedPermissions);

        String returnUriMappingId = tenantRoleAssociationManager.assignPermission();

        assertEquals(tenantRoleAssociationManager.getAssignedPermissions(), expectedAssociatedPermissions);
        assertEquals(K_TENANT_ROLE_SCREEN, returnUriMappingId);

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_INFO, captured.getSeverity());
        assertEquals(DataModelEnum.TRP_ASSOCIATION_SUCCESS_MESSAGE.getValue(),
                captured.getSummary());
        assertEquals(new Long(1L), tenantRoleAssociationManager.getTabIndex());
    }

    /**
     * Test the method assignPermission(): the one which does/perform permission assignment
     * Perform the association between permission, tenant and role (Tenant and Role are required
     * and must be previously selected from a GUI)
     *
     * Corresponds to scenario/case in which a Permission was not selected
     */
    @Test
    public void testAssignPermissionWithNoPermissionSelected() {
        SystemRole role = new Role(); role.setId(2L);
        SystemTenant tenant = new Tenant(); tenant.setId(1L);
        SystemPermission permission = new Permission();
        tenantRoleAssociationManager.setPermission(permission);
        tenantRoleAssociationManager.setRole(role);
        tenantRoleAssociationManager.setTenant(tenant);

        String returnUriMappingId = tenantRoleAssociationManager.assignPermission();

        assertEquals(K_TENANT_ROLE_SCREEN, returnUriMappingId);

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
        assertEquals(DataModelEnum.TRP_ASSOCIATION_ERROR_MESSAGE.getValue(),
                captured.getSummary());
        assertEquals(DataModelEnum.TRP_ASSOCIATION_NO_PERMISSION_SELECT_MESSAGE.getValue(), captured.getDetail());
        assertEquals(tenantRoleAssociationManager.getTabIndex(), new Long(1L));
    }

    /**
     * Test the method assignPermission(): the one which does/perform permission assignment
     * Perform the association between permission, tenant and role (Tenant and Role are required
     * and must be previously selected from a GUI)
     *
     * Corresponds to scenario/case in which a Exception occurs during assigment process
     * @throws SystemException in case of any rest client communication issue
     */
    @Test
    public void testAssignPermissionWithException() throws SystemException {
        SystemRole role = new Role(); role.setId(2L);
        SystemTenant tenant = new Tenant(); tenant.setId(1L);
        SystemPermission permission = new Permission(); permission.setId(3L);
        tenantRoleAssociationManager.setPermission(permission);
        tenantRoleAssociationManager.setRole(role);
        tenantRoleAssociationManager.setTenant(tenant);

        Exception e = new RuntimeException("Error assigning permission");
        when(tenantRoleRESTServiceAccess.assignPermission(tenant.getId(), role.getId(), permission.getId())).
                thenThrow(e);

        String returnUriMappingId = tenantRoleAssociationManager.assignPermission();

        assertEquals(K_TENANT_ROLE_SCREEN, returnUriMappingId);

        ArgumentCaptor<FacesMessage> facesMessageCaptor = ArgumentCaptor.forClass(FacesMessage.class);
        verify(facesContext).addMessage(nullable(String.class), facesMessageCaptor.capture());

        FacesMessage captured = facesMessageCaptor.getValue();
        assertEquals(FacesMessage.SEVERITY_ERROR, captured.getSeverity());
        assertEquals(DataModelEnum.TRP_ASSOCIATION_ERROR_MESSAGE.getValue(),
                captured.getSummary());
        assertEquals(e.getMessage(), captured.getDetail());
        assertEquals(new Long(1L), tenantRoleAssociationManager.getTabIndex());
    }

}
