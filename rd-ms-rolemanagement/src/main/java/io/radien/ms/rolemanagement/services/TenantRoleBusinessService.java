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
package io.radien.ms.rolemanagement.services;

import io.radien.api.entity.Page;
import io.radien.api.model.role.SystemRole;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.model.tenantrole.SystemTenantRoleSearchFilter;
import io.radien.api.service.tenantrole.TenantRoleUserServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.RoleNotFoundException;
import io.radien.exception.SystemException;
import io.radien.exception.TenantRoleException;
import io.radien.exception.TenantRoleNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.RoleSearchFilter;
import io.radien.ms.rolemanagement.client.entities.TenantRoleSearchFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component that orchestrates the using of diverse Service Access components
 * to handle TenantRole domain business rules
 * @author Newton Carvalho
 */
@Stateless
public class TenantRoleBusinessService extends AbstractTenantRoleDomainBusinessService {

    protected Logger log = LoggerFactory.getLogger(TenantRoleBusinessService.class);

    @Inject
    private TenantRoleUserServiceAccess tenantRoleUserServiceAccess;

    /**
     * Retrieves TenantRole association using pagination approach
     * @param pageNumber page number
     * @param pageSize page size
     * @return Page containing TenantRole associations (Chunk/Portion compatible
     * with parameter Page number and Page size)
     */
    public Page<SystemTenantRole> getAll(Long tenantId, Long roleId, int pageNumber, int pageSize,
                                         List<String> sortBy,
                                         boolean isAscending) {
        return this.getTenantRoleServiceAccess().getAll(tenantId, roleId, pageNumber, pageSize, sortBy, isAscending);
    }

    /**
     * Retrieves TenantRole associations that met the following parameter
     * @param tenantId Tenant identifier
     * @param roleId Role identifier
     * @param isLogicalConjunction specifies if the parameters will be unified by AND (true) or OR (false)
     * @return Collection containing TenantRole associations.
     */
    public List<? extends SystemTenantRole> getSpecific(Long tenantId,
                                                        Long roleId,
                                                        boolean isLogicalConjunction) {
        SystemTenantRoleSearchFilter filter = new TenantRoleSearchFilter(tenantId, roleId,
                true, isLogicalConjunction);
        return this.getTenantRoleServiceAccess().get(filter);
    }

    /**
     * Gets the System Tenant Role association searched by the PK (id).
     * @param id to be searched.
     * @return the system Tenant Role Association requested to be found.
     * @throws TenantRoleNotFoundException if Tenant Role association could not be found
     */
    public SystemTenantRole getById(Long id) throws TenantRoleNotFoundException {
        SystemTenantRole systemTenantRole = this.getTenantRoleServiceAccess().get(id);
        if (systemTenantRole == null) {
            throw new TenantRoleNotFoundException(GenericErrorCodeMessage.TENANT_ROLE_NO_TENANT_ROLE_FOUND.toString(id.toString()));
        }
        return systemTenantRole;
    }

    /**
     * Deletes a Tenant Role association
     * @param id Tenant Role association Identifier
     * @return If the association was found and the delete process was successfully performed
     * @throws TenantRoleException If does not exist Tenant Role for the given id, or Tenant Role exists but
     * is linked with other Entities like Tenant Role Permission or Tenant Role User (so, it could not be removed)
     */
    public boolean delete(Long id) throws TenantRoleException {
        SystemTenantRole systemTenantRole = this.getTenantRoleServiceAccess().get(id);
        if (systemTenantRole == null) {
            throw new TenantRoleNotFoundException(GenericErrorCodeMessage.TENANT_ROLE_NO_TENANT_ROLE_FOUND.toString(id.toString()));
        }
        return this.getTenantRoleServiceAccess().delete(id);
    }

    /**
     * Create a TenantRole association
     * @param systemTenantRole bean that corresponds to TenantRole association
     * @throws UniquenessConstraintException In case of repeated information regarding tenant and role
     * @throws TenantRoleException for the case of inconsistencies found
     * @throws SystemException for the case of issues regarding REST Client communication
     */
    public void create(SystemTenantRole systemTenantRole) throws UniquenessConstraintException, TenantRoleException, SystemException {
        checkIfMandatoryParametersWereInformed(systemTenantRole);
        checkIfParamsExists(systemTenantRole.getTenantId(), systemTenantRole.getRoleId(), null);
        this.getTenantRoleServiceAccess().create(systemTenantRole);
    }

    /**
     * Create a TenantRole association
     * @param systemTenantRole bean that corresponds to TenantRole association
     * @throws UniquenessConstraintException In case of repeated information regarding tenant and role
     * @throws TenantRoleException for the case of inconsistencies found
     * @throws SystemException for the case of issues regarding REST Client communication
     */
    public void update(SystemTenantRole systemTenantRole) throws UniquenessConstraintException, TenantRoleException, SystemException {
        checkIfMandatoryParametersWereInformed(systemTenantRole);
        checkIfParamsExists(systemTenantRole.getTenantId(), systemTenantRole.getRoleId(), null);
        this.getTenantRoleServiceAccess().update(systemTenantRole);
    }

    /**
     * Check if a Tenant role association exists
     * @param tenantId Tenant Identifier
     * @param roleId Role identifier
     * @return true if exists, otherwise false
     */
    public boolean existsAssociation(Long tenantId, Long roleId) {
        checkIfMandatoryParametersWereInformed(tenantId, roleId);
        return this.getTenantRoleServiceAccess().isAssociationAlreadyExistent(roleId, tenantId);
    }

    /**
     * Retrieves the existent Roles for a User of a specific Tenant
     * @param userId User identifier
     * @param tenantId Tenant identifier
     * @return List containing roles
     */
    public List<? extends SystemRole> getRolesForUserTenant(Long userId, Long tenantId) throws RoleNotFoundException {
        checkIfMandatoryParametersWereInformed(userId);
        String msg = String.format("Get Roles for User:%d Tenant:%d",userId,tenantId);
        log.info(msg);
        List<Long> ids = this.getTenantRoleServiceAccess().getRoleIdsForUserTenant(userId, tenantId);
        if(ids == null || ids.isEmpty()){
            return new ArrayList<>();
        }
        return getRoleServiceAccess().getSpecificRoles(new RoleSearchFilter(null,
                null, ids, true,true));
    }

    /**
     * Check if Role exists for a User (Optionally for a Tenant)
     * @param userId User identifier
     * @param roleName Role name identifier
     * @param tenantId Tenant identifier (Optional)
     * @return true if role is associated to an User, otherwise false
     */
    public boolean isRoleExistentForUser(Long userId, String roleName, Long tenantId) {
        checkIfMandatoryParametersWereInformed(userId, roleName);
        return this.getTenantRoleServiceAccess().hasAnyRole(userId, Collections.singletonList(roleName), tenantId);
    }

    /**
     * Check if some of the specifies Role exists for a User (Optionally for a Tenant)
     * @param userId User identifier
     * @param roleNames Roles name identifier
     * @param tenantId Tenant identifier (Optional)
     * @return true if role is associated to an User, otherwise false
     */
    public boolean isAnyRoleExistentForUser(Long userId, List<String> roleNames, Long tenantId) {
        checkIfMandatoryParametersWereInformed(userId);
        if (roleNames == null || roleNames.isEmpty()) {
            throw new IllegalArgumentException(GenericErrorCodeMessage.TENANT_ROLE_FIELD_MANDATORY.toString("role names"));
        }
        return this.getTenantRoleServiceAccess().hasAnyRole(userId, roleNames, tenantId);
    }

    /**
     * Check if Permission exists for a User (Optionally for a Tenant)
     * @param userId User identifier
     * @param permissionId Permission identifier
     * @param tenantId Tenant identifier (Optional)
     * @return true if role is associated to an User, otherwise false
     */
    public boolean isPermissionExistentForUser(Long userId, Long permissionId, Long tenantId) {
        checkIfMandatoryParametersWereInformed(userId, permissionId);
        return this.getTenantRoleServiceAccess().hasPermission(userId, permissionId, tenantId);
    }

    public TenantRoleUserServiceAccess getTenantRoleUserServiceAccess() {
        return tenantRoleUserServiceAccess;
    }

    public void setTenantRoleUserServiceAccess(TenantRoleUserServiceAccess tenantRoleUserServiceAccess) {
        this.tenantRoleUserServiceAccess = tenantRoleUserServiceAccess;
    }

    public long count(){
        return getTenantRoleServiceAccess().count();
    }
}
