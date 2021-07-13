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
package io.radien.ms.rolemanagement.services;

import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.model.tenantrole.SystemTenantRoleSearchFilter;
import io.radien.api.service.permission.PermissionRESTServiceAccess;
import io.radien.api.service.role.RoleServiceAccess;
import io.radien.api.service.tenant.ActiveTenantRESTServiceAccess;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRolePermissionServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleUserServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.NotFoundException;
import io.radien.exception.SystemException;
import io.radien.exception.TenantRoleException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.TenantRoleSearchFilter;
import io.radien.ms.rolemanagement.entities.TenantRolePermission;
import io.radien.ms.rolemanagement.entities.TenantRoleUser;
import io.radien.ms.tenantmanagement.client.entities.ActiveTenant;
import io.radien.ms.tenantmanagement.client.exceptions.InternalServerErrorException;
import io.radien.ms.tenantmanagement.client.services.ActiveTenantFactory;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;

import static io.radien.exception.GenericErrorCodeMessage.TENANT_ROLE_ASSOCIATION_TENANT_ROLE;
import static io.radien.exception.GenericErrorCodeMessage.TENANT_ROLE_NO_ASSOCIATION_FOUND_FOR;
import static io.radien.exception.GenericErrorCodeMessage.TENANT_ROLE_NO_ASSOCIATION_FOR_PERMISSION;
import static io.radien.exception.GenericErrorCodeMessage.TENANT_ROLE_PERMISSION_EXISTENT_FOR_TENANT_ROLE;

/**
 * Component that orchestrates the using of diverse Service Access components
 * to handle TenantRole domain business rules
 * @author Newton Carvalho
 */
@Stateless
public class TenantRoleBusinessService implements Serializable {

    @Inject
    private TenantRoleServiceAccess tenantRoleServiceAccess;

    @Inject
    private TenantRoleUserServiceAccess tenantRoleUserServiceAccess;

    @Inject
    private TenantRolePermissionServiceAccess tenantRolePermissionService;

    @Inject
    private TenantRESTServiceAccess tenantRESTServiceAccess;

    @Inject
    private PermissionRESTServiceAccess permissionRESTServiceAccess;

    @Inject
    private RoleServiceAccess roleServiceAccess;

    @Inject
    private ActiveTenantRESTServiceAccess activeTenantRESTServiceAccess;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * Retrieves TenantRole association using pagination approach
     * @param pageNumber page number
     * @param pageSize page size
     * @return Page containing TenantRole associations (Chunk/Portion compatible
     * with parameter Page number and Page size)
     */
    public Page<SystemTenantRole> getAll(int pageNumber, int pageSize) {
        return this.tenantRoleServiceAccess.getAll(pageNumber, pageSize);
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
        return this.tenantRoleServiceAccess.get(filter);
    }

    /**
     * Gets the System Tenant Role association searched by the PK (id).
     * @param id to be searched.
     * @return the system Tenant Role Association requested to be found.
     * @throws TenantRoleException if Tenant Role association could not be found
     */
    public SystemTenantRole getById(Long id) throws TenantRoleException {
        SystemTenantRole systemTenantRole = this.tenantRoleServiceAccess.get(id);
        if (systemTenantRole == null) {
            throw new TenantRoleException(GenericErrorCodeMessage.TENANT_ROLE_NO_TENANT_ROLE_FOUND.toString(id.toString()));
        }
        return systemTenantRole;
    }

    /**
     * Deletes a Tenant Role association
     * @param id Tenant Role association Identifier
     * @return If the association was found and the delete process was successfully performed
     * @throws TenantRoleException If the Tenant Role association is linked
     * with other Entities like Tenant Role Permission or Tenant Role User
     */
    public boolean delete(Long id) throws TenantRoleException {
        SystemTenantRole systemTenantRole = this.tenantRoleServiceAccess.get(id);
        if (systemTenantRole == null) {
            throw new TenantRoleException(GenericErrorCodeMessage.TENANT_ROLE_NO_TENANT_ROLE_FOUND.toString(id.toString()));
        }
        return this.tenantRoleServiceAccess.delete(id);
    }

    /**
     * Create a TenantRole association
     * @param systemTenantRole bean that corresponds to TenantRole association
     * @throws UniquenessConstraintException In case of repeated information regarding tenant and role
     * @throws TenantRoleException for the case of inconsistencies found
     * @throws SystemException for the case of issues regarding REST Client communication
     */
    public void save(SystemTenantRole systemTenantRole) throws UniquenessConstraintException, TenantRoleException, SystemException {
        checkIfMandatoryParametersWereInformed(systemTenantRole);
        checkIfParamsExists(systemTenantRole.getTenantId(), systemTenantRole.getRoleId(), null);
        this.tenantRoleServiceAccess.save(systemTenantRole);
    }

    /**
     * Check if a Tenant role association exists
     * @param tenantId Tenant Identifier
     * @param roleId Role identifier
     * @return true if exists, otherwise false
     */
    public boolean existsAssociation(Long tenantId, Long roleId) {
        checkIfMandatoryParametersWereInformed(tenantId, roleId);
        return this.tenantRoleServiceAccess.isAssociationAlreadyExistent(roleId, tenantId);
    }

    /**
     * Retrieves the Permissions that exists for a Tenant Role Association (Optionally taking in account user)
     * @param tenantId Tenant identifier (Mandatory)
     * @param roleId Role identifier (Mandatory)
     * @param userId User identifier (Optional)
     * @return List containing permissions
     */
    public List<SystemPermission> getPermissions(Long tenantId, Long roleId, Long userId) throws SystemException {
        checkIfMandatoryParametersWereInformed(tenantId, roleId);
        List<SystemPermission> list = new ArrayList<>();
        List<Long> ids = this.tenantRoleServiceAccess.getPermissions(tenantId, roleId, userId);
        for (Long id:ids) {
            Optional<SystemPermission> opt = getPermissionRESTServiceAccess().getPermissionById(id);
            opt.ifPresent(list::add);
        }
        return list;
    }

    /**
     * Retrieves the existent Tenants for a User (Optionally for a specific role)
     * @param userId User identifier
     * @param roleId Role identifier (Optional)
     * @return List containing tenants
     */
    public List<SystemTenant> getTenants(Long userId, Long roleId) throws SystemException {
        checkIfMandatoryParametersWereInformed(userId);
        List<SystemTenant> list = new ArrayList<>();
        List<Long> ids = this.tenantRoleServiceAccess.getTenants(userId, roleId);
        for (Long id:ids) {
            Optional<SystemTenant> opt = getTenantRESTServiceAccess().getTenantById(id);
            opt.ifPresent(list::add);
        }
        return list;
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
        return this.tenantRoleServiceAccess.hasAnyRole(userId, Arrays.asList(roleName), tenantId);
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
        return this.tenantRoleServiceAccess.hasAnyRole(userId, roleNames, tenantId);
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
        return this.tenantRoleServiceAccess.hasPermission(userId, permissionId, tenantId);
    }

    /**
     * Utility method to retrieve tenant and reduce cognitive complexity
     * @param tenantId tenant identifier
     * @return instance of SystemTenant
     * @throws SystemException in case of communication issues with tenant rest client
     * @throws TenantRoleException if tenant could not be found for the informed id
     */
    protected SystemTenant retrieveTenant(Long tenantId) throws SystemException, TenantRoleException {
        Optional<SystemTenant> optionalSystemTenant;
        try {
            optionalSystemTenant = tenantRESTServiceAccess.getTenantById(tenantId);
        }
        catch(NotFoundException n) {
            optionalSystemTenant = Optional.empty();
        }
        catch (InternalServerErrorException | SystemException s) {
            throw new SystemException(s);
        }
        return optionalSystemTenant.orElseThrow(() -> new TenantRoleException(GenericErrorCodeMessage.
                TENANT_ROLE_NO_TENANT_FOUND.toString(String.valueOf(tenantId))));
    }

    /**
     * Assign/associate/add user to a Tenant (TenantRole domain)
     * The association will always be under a specific role
     * @param tenant Tenant identifier (Mandatory)
     * @param role Role identifier (Mandatory)
     * @param user User identifier (Mandatory)
     * @throws TenantRoleException for the case of any inconsistency found
     * @throws UniquenessConstraintException in case of error during the insertion
     */
    public void assignUser(Long tenant, Long role, Long user) throws TenantRoleException,
            UniquenessConstraintException, SystemException {
        checkIfMandatoryParametersWereInformed(tenant, role, user);
        Long tenantRoleId = getTenantRoleId(tenant, role);
        if (this.tenantRoleUserServiceAccess.isAssociationAlreadyExistent(user, tenantRoleId)) {
            throw new TenantRoleException(GenericErrorCodeMessage.TENANT_ROLE_USER_IS_ALREADY_ASSOCIATED.
                    toString(tenant.toString(), role.toString()));
        }
        TenantRoleUser tru = new TenantRoleUser();
        tru.setTenantRoleId(tenantRoleId);
        tru.setUserId(user);
        tru.setCreateDate(new Date());
        this.tenantRoleUserServiceAccess.create(tru);
        if(!activeTenantRESTServiceAccess.isActiveTenantExistent(user, tenant)) {
            SystemTenant retrievedTenant = retrieveTenant(tenant);
            ActiveTenant activeTenant = new ActiveTenantFactory().create(user,
                    tenant, retrievedTenant.getName(), false);
            activeTenantRESTServiceAccess.create(activeTenant);
        }
    }

    /**
     * Given a tenant and a role, retrieves the existent id for such association
     * @param tenant tenant identifier (id)
     * @param role role identifier (id)
     * @return the association id (if exists), otherwise throws a exception
     * @throws TenantRoleException thrown if association does not exists
     */
    private Long getTenantRoleId(Long tenant, Long role) throws TenantRoleException{
        return this.tenantRoleServiceAccess.getTenantRoleId(tenant, role).
                orElseThrow(() -> new TenantRoleException(TENANT_ROLE_ASSOCIATION_TENANT_ROLE.toString(
                        tenant.toString(), role.toString())));
    }

    /**
     * (Un)Assign/Dissociate/remove user from a Tenant (TenantRole domain)
     * @param tenant Tenant identifier (Mandatory)
     * @param role Role identifier
     * @param user User identifier (Mandatory)
     * @throws TenantRoleException for the case of any inconsistency found
     */
    public void unassignUser(Long tenant, Long role, Long user) throws TenantRoleException, SystemException {
        if (tenant == null) {
            throw new TenantRoleException(GenericErrorCodeMessage.
                    TENANT_ROLE_FIELD_MANDATORY.toString("tenant id"));
        }
        if (user == null) {
            throw new TenantRoleException(GenericErrorCodeMessage.
                    TENANT_ROLE_FIELD_MANDATORY.toString("user id"));
        }
        Collection<Long> ids = tenantRoleUserServiceAccess.getTenantRoleUserIds(tenant, role, user);
        if (ids.isEmpty()) {
            throw new TenantRoleException (TENANT_ROLE_NO_ASSOCIATION_FOUND_FOR.
                    toString(String.valueOf(tenant), String.valueOf(role), String.valueOf(user)));
        }
        tenantRoleUserServiceAccess.delete(ids);
        activeTenantRESTServiceAccess.deleteByTenantAndUser(tenant, user);
    }

    /**
     * Assign/associate/add permission to a Tenant (TenantRole domain)
     * The association will always be under a specific role
     * @param tenant Tenant identifier (Mandatory)
     * @param role Role identifier (Mandatory)
     * @param permission Permission identifier (Mandatory)
     * @throws TenantRoleException for the case of any inconsistency found
     * @throws UniquenessConstraintException in case of error during the insertion
     * @throws SystemException in case of communication issues with REST client
     */
    public void assignPermission(Long tenant, Long role, Long permission) throws TenantRoleException,
            UniquenessConstraintException, SystemException {

        checkIfMandatoryParametersWereInformed(tenant, role, permission);
        checkIfParamsExists(null, null, permission);
        Long tenantRoleId = getTenantRoleId(tenant, role);
        if (this.tenantRolePermissionService.isAssociationAlreadyExistent(permission, tenantRoleId)) {
            throw new TenantRoleException(TENANT_ROLE_PERMISSION_EXISTENT_FOR_TENANT_ROLE.
                    toString(tenant.toString(), role.toString()));
        }
        TenantRolePermission trp = new TenantRolePermission();
        trp.setTenantRoleId(tenantRoleId);
        trp.setPermissionId(permission);
        trp.setCreateDate(new Date());
        this.tenantRolePermissionService.create(trp);
    }

    /**
     * (Un)Assign/Dissociate/remove permission from a Tenant (TenantRole domain)
     * @param tenant Tenant identifier (Mandatory)
     * @param role Role identifier (Mandatory)
     * @param permission User identifier (Mandatory)
     * @throws TenantRoleException for the case of any inconsistency found
     */
    public void unassignPermission(Long tenant, Long role, Long permission) throws TenantRoleException{
        checkIfMandatoryParametersWereInformed(tenant, role, permission);
        Long tenantRoleId = getTenantRoleId(tenant, role);
        Long tenantRolePermissionId = this.tenantRolePermissionService.
                getTenantRolePermissionId(tenantRoleId, permission).orElseThrow(() -> new TenantRoleException(
                        TENANT_ROLE_NO_ASSOCIATION_FOR_PERMISSION.toString(permission.toString())));
        this.tenantRolePermissionService.delete(tenantRolePermissionId);
    }

    /**
     * Check if all the informed Params (Tenant, Role, etc) exist
     * @param tenantId Tenant Identifier
     * @param roleId Role identifier
     * @param permissionId Permission identifier
     * @throws SystemException in case of error during communication process using REST Clients
     * @throws TenantRoleException in case of inconsistency found
     */
    protected void checkIfParamsExists(Long tenantId, Long roleId, Long permissionId)
            throws TenantRoleException {
        String standardMsgError = "Param %s not found for id %d";
        if (tenantId != null) {
            try {
                if (!tenantRESTServiceAccess.isTenantExistent(tenantId)) {
                    throwInformingInconsistencyFound(standardMsgError, "Tenant", tenantId);
                }
            } catch (SystemException systemException) {
                log.error("Error checking tenant existence", systemException);
            }
        }
        if (permissionId != null) {
            try {
                permissionRESTServiceAccess.isPermissionExistent(permissionId, null);
            } catch (SystemException systemException) {
                if (systemException.getMessage().contains("HTTP 404 Not Found")) {
                    throwInformingInconsistencyFound(standardMsgError, "Permission", permissionId);
                }
                log.error("Error checking permission existence", systemException);
            }
        }
        if (roleId != null && !this.roleServiceAccess.checkIfRolesExist(roleId, null) ) {
            throwInformingInconsistencyFound(standardMsgError, "Role", roleId);
        }
    }

    /**
     * Check if all specified parameters were informed
     * @param params vars contains variables that correspond method parameters
     */
    protected void checkIfMandatoryParametersWereInformed(Object...params) {
        for (Object o:params) {
            if (o == null)
                throw new IllegalArgumentException("One mandatory parameter not informed");
        }
    }

    /**
     * This method just throw a TenantRoleException to inform that an inconsistency was found
     * @param rawMsg Informative message describing the inconsistency
     * @param params Parameters to be aggregated into the message
     * @throws TenantRoleException in case of any inconsistency found
     */
    protected void throwInformingInconsistencyFound(String rawMsg, Object ... params) throws TenantRoleException {
        String formattedMsg = params != null ? String.format(rawMsg, params) : rawMsg;
        log.error(formattedMsg);
        throw new TenantRoleException(formattedMsg);
    }

    public TenantRESTServiceAccess getTenantRESTServiceAccess() {
        return tenantRESTServiceAccess;
    }

    public void setTenantRESTServiceAccess(TenantRESTServiceAccess tenantRESTServiceAccess) {
        this.tenantRESTServiceAccess = tenantRESTServiceAccess;
    }

    public PermissionRESTServiceAccess getPermissionRESTServiceAccess() {
        return permissionRESTServiceAccess;
    }

    public void setPermissionRESTServiceAccess(PermissionRESTServiceAccess permissionRESTServiceAccess) {
        this.permissionRESTServiceAccess = permissionRESTServiceAccess;
    }

    public TenantRoleServiceAccess getTenantRoleServiceAccess() {
        return tenantRoleServiceAccess;
    }

    public void setTenantRoleServiceAccess(TenantRoleServiceAccess tenantRoleServiceAccess) {
        this.tenantRoleServiceAccess = tenantRoleServiceAccess;
    }

    public TenantRoleUserServiceAccess getTenantRoleUserServiceAccess() {
        return tenantRoleUserServiceAccess;
    }

    public void setTenantRoleUserServiceAccess(TenantRoleUserServiceAccess tenantRoleUserServiceAccess) {
        this.tenantRoleUserServiceAccess = tenantRoleUserServiceAccess;
    }

    public TenantRolePermissionServiceAccess getTenantRolePermissionService() {
        return tenantRolePermissionService;
    }

    public void setTenantRolePermissionService(TenantRolePermissionServiceAccess tenantRolePermissionService) {
        this.tenantRolePermissionService = tenantRolePermissionService;
    }

    public RoleServiceAccess getRoleServiceAccess() {
        return roleServiceAccess;
    }

    public void setRoleServiceAccess(RoleServiceAccess roleServiceAccess) {
        this.roleServiceAccess = roleServiceAccess;
    }

    public ActiveTenantRESTServiceAccess getActiveTenantRESTServiceAccess() {
        return activeTenantRESTServiceAccess;
    }

    public void setActiveTenantRESTServiceAccess(ActiveTenantRESTServiceAccess activeTenantRESTServiceAccess) {
        this.activeTenantRESTServiceAccess = activeTenantRESTServiceAccess;
    }
}
