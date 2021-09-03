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
package io.radien.webapp.user.tenant.role;

import io.radien.api.model.tenant.SystemActiveTenant;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenant.SystemTenantType;
import io.radien.api.service.tenant.TenantRESTServiceAccess;

import io.radien.exception.SystemException;

import io.radien.ms.tenantmanagement.client.entities.ActiveTenant;
import io.radien.ms.tenantmanagement.client.entities.Tenant;

import io.radien.ms.tenantmanagement.client.entities.TenantType;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.JSFUtilAndFaceContextMessagesTest;
import io.radien.webapp.activeTenant.ActiveTenantDataModelManager;
import io.radien.webapp.authz.WebAuthorizationChecker;

import java.util.Optional;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
/**
 * Class that aggregates UnitTest cases for UserTenantGlobalRolesManager
 *
 * @author Rajesh Gavvala
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({JSFUtil.class, FacesContext.class, ExternalContext.class})
public class UserTenantGlobalRolesManagerTest extends JSFUtilAndFaceContextMessagesTest {

    @InjectMocks
    private UserTenantGlobalRolesManager userTenantGlobalRolesManager;

    @Mock
    private WebAuthorizationChecker webAuthorizationChecker;

    @Mock
    private ActiveTenantDataModelManager activeTenantDataModelManager;

    @Mock
    private TenantRESTServiceAccess tenantRESTServiceAccess;

    private SystemActiveTenant systemActiveTenant;
    private Optional<SystemTenant> optionalSystemTenant;

    /**
     * Prepares require objects when requires to invoke
     */
    @Before
    public void before() throws SystemException {
        MockitoAnnotations.initMocks(this);

        handleJSFUtilAndFaceContextMessages();

        systemActiveTenant = new ActiveTenant();
        systemActiveTenant.setId(1L);
        systemActiveTenant.setTenantName("Root Tenant");

        SystemTenantType systemTenantType = TenantType.ROOT_TENANT;
        SystemTenant systemTenant = new Tenant();
        systemTenant.setId(1L);
        systemTenant.setName("Root Tenant");
        systemTenant.setTenantType(systemTenantType);

        optionalSystemTenant = Optional.of(systemTenant);

        doReturn(true).when(activeTenantDataModelManager).isTenantActive();
        doReturn(systemActiveTenant).when(activeTenantDataModelManager).getActiveTenant();
    }

    /**
     * Test method isRootTenantUserRoleAdministratorAccess()
     * Asserts boolean values
     */
    @Test
    public void testIsRootTenantUserRoleAdministratorAccess() throws SystemException {
        userTenantGlobalRolesManager.setRoleAdministratorInRootContext(false);
        assertFalse(userTenantGlobalRolesManager.isRoleAdministratorInRootContext());

        doReturn(optionalSystemTenant).when(tenantRESTServiceAccess).getTenantById(anyLong());
        doReturn(true).when(webAuthorizationChecker).hasGrant(anyLong(), anyString());

        boolean isRootTenantUserRoleAdministratorAccess = userTenantGlobalRolesManager.isRoleAdministratorInRootContext();
        assertTrue(isRootTenantUserRoleAdministratorAccess);

        userTenantGlobalRolesManager.setSystemActiveTenant(systemActiveTenant);
        assertNotNull(userTenantGlobalRolesManager.getSystemActiveTenant());
    }

    /**
     * Test method isRootTenantUserRoleAdministratorAccess()
     * Asserts Exception
     * @throws SystemException if any error occurs
     */
    @Test(expected = Exception.class)
    public void testIsRootTenantUserRoleAdministratorAccessException() throws SystemException {
        doThrow(SystemException.class).when(tenantRESTServiceAccess).getTenantById(anyLong());

        userTenantGlobalRolesManager.isRoleAdministratorInRootContext();
    }
}