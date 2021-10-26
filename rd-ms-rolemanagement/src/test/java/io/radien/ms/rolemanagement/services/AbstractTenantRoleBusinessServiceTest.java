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
package io.radien.ms.rolemanagement.services;

import io.radien.api.model.role.SystemRole;
import io.radien.api.model.role.SystemRoleSearchFilter;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.service.role.RoleServiceAccess;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRolePermissionServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleUserServiceAccess;
import io.radien.exception.RoleNotFoundException;
import io.radien.exception.SystemException;
import io.radien.exception.TenantRoleException;
import io.radien.exception.TenantRoleIllegalArgumentException;
import io.radien.exception.TenantRoleNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.RoleSearchFilter;
import io.radien.ms.rolemanagement.entities.RoleEntity;
import io.radien.ms.rolemanagement.entities.TenantRoleEntity;
import io.radien.ms.rolemanagement.entities.TenantRolePermissionEntity;
import io.radien.ms.rolemanagement.entities.TenantRoleUserEntity;
import java.util.List;
import java.util.Properties;
import javax.ejb.embeddable.EJBContainer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Base class that contains features to be used by Test classes regarding
 * {@link TenantRoleBusinessService} and {@link TenantRolePermissionBusinessService}
 *
 * @author Newton Carvalho
 */
public abstract class AbstractTenantRoleBusinessServiceTest {

    protected static Long user1Id = 1111L;
    protected static Long tenantId = 1000L;
    protected static Long tenantId2 = 2000L;
    protected static Long tenantId3 = 3000L;

    protected static String roleNameForTenantAdministrator = "Tenant Administrator";

    protected static Properties p;
    protected static TenantRoleBusinessService tenantRoleBusinessService;
    protected static TenantRolePermissionBusinessService tenantRolePermissionBusinessService;
    protected static TenantRoleUserBusinessService tenantRoleUserBusinessService;

    protected static TenantRoleServiceAccess tenantRoleServiceAccess;
    protected static TenantRolePermissionServiceAccess tenantRolePermissionServiceAccess;
    protected static TenantRoleUserServiceAccess tenantRoleUserServiceAccess;
    protected static RoleServiceAccess roleServiceAccess;
    protected static EJBContainer container;

    protected static String lookupString;

    /**
     * Method to get a specific role by specific given name
     * @param name to be found
     * @return the requested system role
     */
    protected SystemRole getRoleByName(String name) {
        SystemRoleSearchFilter filter = new RoleSearchFilter();
        filter.setName(name);
        List<? extends SystemRole> roles = roleServiceAccess.getSpecificRoles(filter);
        return roles.isEmpty() ? null : roles.get(0);
    }

    /**
     * Method to create a requested role
     * @param name of the role to be created
     * @return the newly created system role
     * @throws RoleNotFoundException in case the role could not be found
     * @throws UniquenessConstraintException in case of duplicated name
     */
    protected SystemRole createRole(String name) throws RoleNotFoundException, UniquenessConstraintException {
        SystemRole sr = getRoleByName(name);
        if (sr == null) {
            sr = new RoleEntity();
            sr.setName(name);
            roleServiceAccess.create(sr);
        }
        return sr;
    }

    /**
     * Method to create a specific tenant role
     * @param roleId to be associated
     * @param tenantId to be associated
     * @return the newly created system tenant role
     * @throws SystemException in case of token expiration
     * @throws UniquenessConstraintException in case of duplicated fields
     * @throws TenantRoleException in case of issue while creating the newly tenant role
     */
    protected SystemTenantRole createTenantRole(Long roleId, Long tenantId) throws
            SystemException, UniquenessConstraintException, TenantRoleException {

        List<? extends SystemTenantRole> tenantRoles = tenantRoleBusinessService.
                getSpecific(tenantId, roleId, true);

        SystemTenantRole tenantRole = tenantRoles.isEmpty() ? null : tenantRoles.get(0);

        if (tenantRole == null) {
            tenantRole = new TenantRoleEntity();
            tenantRole.setRoleId(roleId);
            tenantRole.setTenantId(tenantId);

            if (tenantRoleBusinessService.getTenantRESTServiceAccess() == null) {
                tenantRoleBusinessService.setTenantRESTServiceAccess(mock(TenantRESTServiceAccess.class));
            }

            when(tenantRoleBusinessService.getTenantRESTServiceAccess().isTenantExistent(tenantId)).
                    thenReturn(Boolean.TRUE);

            tenantRoleBusinessService.create(tenantRole);
        }

        return tenantRole;
    }


    /**
     * Utility method to assembly a TenantRolePermission
     * @param tenant tenant identifier
     * @param role role identifier
     * @param permission permission identifier
     * @return instance of TenantRolePermission
     * @throws TenantRoleNotFoundException if tenant role could not be found for the informed params
     * @throws TenantRoleIllegalArgumentException thrown when either tenant or role are not informed
     */
    protected TenantRolePermissionEntity assemblyTenantRolePermission(Long tenant,
                                                                      Long role, Long permission) throws TenantRoleNotFoundException, TenantRoleIllegalArgumentException {
        TenantRolePermissionEntity trp = new TenantRolePermissionEntity();
        trp.setTenantRoleId(tenantRolePermissionBusinessService.getTenantRoleId(tenant, role));
        trp.setPermissionId(permission);
        return trp;
    }

    /**
     * Utility method to assembly a TenantRoleUser
     * @param tenant tenant identifier
     * @param role role identifier
     * @param user user identifier
     * @return instance of TenantRolePermission
     * @throws TenantRoleNotFoundException if tenant role could not be found for the informed params
     * @throws TenantRoleIllegalArgumentException thrown when either tenant or role are not informed
     */
    protected TenantRoleUserEntity assemblyTenantRoleUser(Long tenant,
                                                          Long role, Long user) throws TenantRoleNotFoundException, TenantRoleIllegalArgumentException {
        TenantRoleUserEntity tru = new TenantRoleUserEntity();
        tru.setTenantRoleId(tenantRolePermissionBusinessService.getTenantRoleId(tenant, role));
        tru.setUserId(user);
        return tru;
    }

}