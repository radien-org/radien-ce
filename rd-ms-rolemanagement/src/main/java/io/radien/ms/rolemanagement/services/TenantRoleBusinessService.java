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
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRolePermissionServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleUserServiceAccess;
import io.radien.api.service.user.UserRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.exception.TenantRoleException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.client.entities.TenantRoleSearchFilter;
import io.radien.ms.rolemanagement.entities.TenantRolePermission;
import io.radien.ms.rolemanagement.entities.TenantRoleUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.*;

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

//    @Inject
//    private UserRESTServiceAccess userRESTServiceAccess;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public Page<SystemTenantRole> getAll(int pageNumber, int pageSize) {
        return this.tenantRoleServiceAccess.getAll(pageNumber, pageSize);
    }

    public List<? extends SystemTenantRole> getSpecific(Long tenantId,
                                                        Long roleId,
                                                        boolean isLogicalConjunction) {
        SystemTenantRoleSearchFilter filter = new TenantRoleSearchFilter(tenantId, roleId,
                true, isLogicalConjunction);
        return this.tenantRoleServiceAccess.get(filter);
    }

    public SystemTenantRole getById(Long id) {
        return this.tenantRoleServiceAccess.get(id);
    }

    public boolean delete(Long id) throws TenantRoleException {
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
        checkIfParamsExists(systemTenantRole.getTenantId(), systemTenantRole.getRoleId(), null, null);
        this.tenantRoleServiceAccess.save(systemTenantRole);
    }

    /**
     * Check if a Tenant role association exists
     * @param tenantId Tenant Identifier
     * @param roleId Role identifier
     * @return true if exists, otherwise false
     */
    public boolean existsAssociation(Long tenantId, Long roleId) {
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
        // TODO: Necessary to improve this retrieval process (informing one array of Ids)
        for (Long id:ids) {
            Optional<SystemPermission> opt = this.permissionRESTServiceAccess.getPermissionById(id);
            if (opt.isPresent()) {
                list.add(opt.get());
            }
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
        // TODO: Necessary to improve this retrieval process (informing one array of Ids)
        for (Long id:ids) {
            Optional<SystemTenant> opt = this.tenantRESTServiceAccess.getTenantById(id);
            if (opt.isPresent()) {
                list.add(opt.get());
            }
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
     * Assign/associate/add user to a Tenant (TenantRole domain)
     * The association will always be under a specific role
     * @param tenant Tenant identifier (Mandatory)
     * @param role Role identifier (Mandatory)
     * @param user User identifier (Mandatory)
     * @throws TenantRoleException for the case of any inconsistency found
     * @throws UniquenessConstraintException in case of error during the insertion
     * @throws SystemException in case of communication issue regarding any REST client
     */
    public void assignUser(Long tenant, Long role, Long user) throws TenantRoleException,
            UniquenessConstraintException, SystemException {
        checkIfMandatoryParametersWereInformed(tenant, role, user);
        checkIfParamsExists(null, null, null, user);
        Long tenantRoleId = this.tenantRoleServiceAccess.getTenantRoleId(tenant, role);
        if (tenantRoleId == null) {
            throwInformingInconsistencyFound("There is no association between tenant %d and role %d", tenant, role);
        }
        if (this.tenantRoleUserServiceAccess.isAssociationAlreadyExistent(user, tenantRoleId)) {
            throwInformingInconsistencyFound("User is already associated with tenant %d and role %d", tenant, role);
        }
        TenantRoleUser tru = new TenantRoleUser();
        tru.setTenantRoleId(tenantRoleId);
        tru.setUserId(user);
        tru.setCreateDate(new Date());
        this.tenantRoleUserServiceAccess.create(tru);
    }

    /**
     * (Un)Assign/Dissociate/remove user from a Tenant (TenantRole domain)
     * @param tenant Tenant identifier (Mandatory)
     * @param role Role identifier (Mandatory)
     * @param user User identifier (Mandatory)
     * @throws TenantRoleException for the case of any inconsistency found
     */
    public void unassignUser(Long tenant, Long role, Long user) throws TenantRoleException {
        checkIfMandatoryParametersWereInformed(tenant, role, user);
        Long tenantRoleId = this.tenantRoleServiceAccess.getTenantRoleId(tenant, role);
        if (tenantRoleId == null) {
            throwInformingInconsistencyFound("There is no association between tenant %d and role %d", tenant, role);
        }
        Long tenantRoleUserId = this.tenantRoleUserServiceAccess.getTenantRoleUserId(tenantRoleId, user);
        if (tenantRoleUserId == null) {
            throwInformingInconsistencyFound("No association found for user %d", user);
        }
        this.tenantRoleUserServiceAccess.delete(tenantRoleUserId);
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
        checkIfParamsExists(null, null, permission, null);
        Long tenantRoleId = this.tenantRoleServiceAccess.getTenantRoleId(tenant, role);
        if (tenantRoleId == null) {
            throwInformingInconsistencyFound("There is no association between tenant %d and role %d", tenant, role);
        }
        if (this.tenantRolePermissionService.isAssociationAlreadyExistent(permission, tenantRoleId)) {
            throwInformingInconsistencyFound("User is already associated with tenant %d and role %d", tenant, role);
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
        Long tenantRoleId = this.tenantRoleServiceAccess.getTenantRoleId(tenant, role);
        if (tenantRoleId == null) {
            throwInformingInconsistencyFound("There is no association between tenant %d and role %d", tenant, role);
        }
        Long tenantRoleUserId = this.tenantRolePermissionService.getTenantRolePermissionId(tenantRoleId, permission);
        if (tenantRoleUserId == null) {
            throwInformingInconsistencyFound("No association found for user %d", permission);
        }
        this.tenantRoleUserServiceAccess.delete(tenantRoleUserId);
    }

    /**
     * Check if all the informed Params (Tenant, Role, etc) exist
     * @param tenantId Tenant Identifier
     * @param roleId Role identifier
     * @param permissionId Permission identifier
     * @param userId User identifier
     * @throws SystemException in case of error during communication process using REST Clients
     * @throws TenantRoleException in case of insconsistency found
     */
    protected void checkIfParamsExists(Long tenantId, Long roleId, Long permissionId, Long userId)
            throws SystemException, TenantRoleException {
        String standardMsgError = "Param %s not found for id %d";
        if (tenantId != null) {
            try {
                if (!tenantRESTServiceAccess.isTenantExistent(tenantId)) {
                    throwInformingInconsistencyFound(standardMsgError, "Tenant", tenantId);
                }
            } catch (SystemException systemException) {
                log.error("Error checking tenant existence", systemException);
                throw systemException;
            }
        }
        if (permissionId != null) {
            try {
                if (!permissionRESTServiceAccess.isPermissionExistent(permissionId, null)) {
                    throwInformingInconsistencyFound(standardMsgError, "Permission", permissionId);
                }
            } catch (SystemException systemException) {
                log.error("Error checking permission existence", systemException);
                throw systemException;
            }
        }
        if (roleId != null && !this.roleServiceAccess.checkIfRolesExist(roleId, null) ) {
            throwInformingInconsistencyFound(standardMsgError, "Role", roleId);
        }
//        if (userId != null) {
//            try {
//                if (!this.userRESTServiceAccess.getUserById(userId).isPresent()) {
//                    throwInformingInconsistencyFound(standardMsgError, "User", userId);
//                }
//            } catch(SystemException systemException) {
//                log.error("Error checking user existence", systemException);
//                throw systemException;
//            }
//        }
    }

    /**
     * Check if all specified parameters were informed
     * @param params vargs contains variables that correspond method parameters
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
     * @throws TenantRoleException
     */
    protected void throwInformingInconsistencyFound(String rawMsg, Object ... params) throws TenantRoleException {
        String formattedMsg = params != null ? String.format(rawMsg, params) : rawMsg;
        log.error(formattedMsg);
        throw new TenantRoleException(formattedMsg);
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

//    public UserRESTServiceAccess getUserRESTServiceAccess() {
//        return userRESTServiceAccess;
//    }
//
//    public void setUserRESTServiceAccess(UserRESTServiceAccess userRESTServiceAccess) {
//        this.userRESTServiceAccess = userRESTServiceAccess;
//    }
}
