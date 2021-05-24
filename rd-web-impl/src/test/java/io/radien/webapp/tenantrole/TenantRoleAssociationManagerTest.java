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

import io.radien.api.model.role.SystemRole;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.service.role.RoleRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.rolemanagement.client.entities.Role;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.tenantmanagement.client.entities.TenantType;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.authz.WebAuthorizationChecker;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Class that aggregates UnitTest cases for TenantRoleAssociationManager
 * @author Newton Carvalho
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Logger.class, JSFUtil.class})
public class TenantRoleAssociationManagerTest {

    @InjectMocks
    private TenantRoleAssociationManager tenantRoleAssociationManager;

    @Mock
    private WebAuthorizationChecker webAuthorizationChecker;

    @Mock
    private TenantRoleRESTServiceAccess tenantRoleRESTServiceAccess;

    @Mock
    private RoleRESTServiceAccess roleRESTServiceAccess;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
        ExternalContext externalContext = Mockito.mock(ExternalContext.class);
        FacesContext facesContext = Mockito.mock(FacesContext.class);
        Mockito.when(facesContext.getExternalContext()).thenReturn(externalContext);
        try {
            Method setter = FacesContext.class.getDeclaredMethod("setCurrentInstance",
                    new Class[] { FacesContext.class });
            setter.setAccessible(true);
            setter.invoke(null, new Object[] { facesContext });
        } catch (Exception e) {
            logger.error("Error setting mocked FacesContext instance", e);
        }
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

        String expectedUrlMappingForRedirection = "test";

        doReturn(Boolean.FALSE).when(tenantRoleRESTServiceAccess).
                exists(tenant.getId(), role.getId());
        doReturn(Boolean.TRUE).when(tenantRoleRESTServiceAccess).save(any());
        doReturn(Boolean.TRUE).when(tenantRoleRESTServiceAccess).assignUser(tenant.getId(),
                role.getId(), userId);

        String urlMapping = tenantRoleAssociationManager.
                associateUser(userId, expectedUrlMappingForRedirection);

        assertNotNull(urlMapping);
        assertEquals(urlMapping, expectedUrlMappingForRedirection);
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

        String expectedUrlMappingForRedirection = "test";

        doThrow(new RuntimeException("Error checking exists")).when(tenantRoleRESTServiceAccess).
                exists(tenant.getId(), role.getId());

        doReturn(Boolean.TRUE).when(tenantRoleRESTServiceAccess).save(any());
        doReturn(Boolean.TRUE).when(tenantRoleRESTServiceAccess).assignUser(tenant.getId(),
                role.getId(), userId);

        String urlMapping = tenantRoleAssociationManager.
                associateUser(userId, expectedUrlMappingForRedirection);

        assertNull(urlMapping);
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
        assertNull(initialRoles);
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

        List outcome = this.tenantRoleAssociationManager.getTenantsFromCurrentUser();
        assertEquals(expectedTenants, outcome);
    }

    /**
     * Test for the method getTenantsFromCurrentUser().
     * Failure Case.
     */
    @Test
    public void testGetTenantsFromCurrentUserFailureCase() throws SystemException {
        Long currentUserId = 1L;
        List<SystemTenant> expectedTenants = new ArrayList<>();

        doThrow(new SystemException("error obtaining user id")).
                doReturn(currentUserId).when(this.webAuthorizationChecker).getCurrentUserId();

        doThrow(new SystemException("error retrieving tenant")).
                when(this.tenantRoleRESTServiceAccess).getTenants(currentUserId, null);

        List outcome = this.tenantRoleAssociationManager.getTenantsFromCurrentUser();
        assertNull(outcome);

        outcome = this.tenantRoleAssociationManager.getTenantsFromCurrentUser();
        assertNull(outcome);
    }
}
