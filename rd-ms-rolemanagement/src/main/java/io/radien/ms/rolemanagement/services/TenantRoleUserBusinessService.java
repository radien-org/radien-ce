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

import io.radien.api.SystemVariables;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.model.tenantrole.SystemTenantRoleUser;
import io.radien.api.service.tenant.ActiveTenantRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleUserServiceAccess;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.SystemException;
import io.radien.exception.TenantRoleException;
import io.radien.exception.TenantRoleIllegalArgumentException;
import io.radien.exception.TenantRoleNotFoundException;
import io.radien.exception.TenantRoleUserDuplicationException;
import io.radien.exception.UniquenessConstraintException;
import io.radien.ms.rolemanagement.entities.TenantRoleUserEntity;
import io.radien.ms.tenantmanagement.client.entities.ActiveTenant;
import io.radien.ms.tenantmanagement.client.services.ActiveTenantFactory;
import java.io.Serializable;
import java.util.Collection;
import javax.ejb.Stateless;
import javax.inject.Inject;

import static io.radien.exception.GenericErrorCodeMessage.TENANT_ROLE_NO_ASSOCIATION_FOUND_FOR_PARAMS;
import static io.radien.exception.GenericErrorCodeMessage.TENANT_ROLE_NO_TENANT_ROLE_FOUND;

/**
 * TenantRoleUserBusinessService bridge between REST Services and
 * Persistence layer of TenantRoleUserService
 *
 * @author Rajesh Gavvala
 */
@Stateless
public class TenantRoleUserBusinessService extends AbstractTenantRoleDomainBusinessService implements Serializable {
    private static final long serialVersionUID = -5658845596283229172L;

    @Inject
    private TenantRoleUserServiceAccess tenantRoleUserServiceAccess;

    @Inject
    private ActiveTenantRESTServiceAccess activeTenantRESTServiceAccess;

    /**
     * Assign/associate/add user to a Tenant (TenantRole domain)
     * The association will always be under a specific role
     * @param tru TenantRoleUser bean that contains information regarding user and Tenant role association
     * @throws TenantRoleException for the case of any inconsistency found
     * @throws UniquenessConstraintException in case of error during the insertion
     * @throws SystemException in case of any communication issue with the endpoint
     */
    public void assignUser(TenantRoleUserEntity tru) throws TenantRoleException,
            UniquenessConstraintException, SystemException {

        if (tru.getUserId() == null) {
            throw new TenantRoleIllegalArgumentException(GenericErrorCodeMessage.
                    TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.USER_ID.getLabel()));
        }

        SystemTenantRole tenantRole = getTenantRoleServiceAccess().get(tru.getTenantRoleId());
        if (tenantRole == null) {
            throw new TenantRoleNotFoundException(TENANT_ROLE_NO_TENANT_ROLE_FOUND.toString(String.valueOf(
                    tru.getTenantRoleId())));
        }
        if (this.tenantRoleUserServiceAccess.isAssociationAlreadyExistent(tru.getUserId(), tru.getTenantRoleId())) {
            throw new TenantRoleUserDuplicationException(GenericErrorCodeMessage.TENANT_ROLE_USER_IS_ALREADY_ASSOCIATED.
                    toString(String.valueOf(tenantRole.getTenantId()), String.valueOf(tenantRole.getRoleId())));
        }

        this.tenantRoleUserServiceAccess.create(tru);
//        if(!activeTenantRESTServiceAccess.isActiveTenantExistent(tru.getUserId(), tenantRole.getTenantId())) {
//            SystemTenant retrievedTenant = retrieveTenant(tenantRole.getTenantId());
//            ActiveTenant activeTenant = ActiveTenantFactory.create(tru.getUserId(), retrievedTenant.getId());
//            activeTenantRESTServiceAccess.create(activeTenant);
//        }
    }


    /**
     * Deletes a Tenant Role Permission association
     * @param id Tenant Role Id association Identifier
     * @return If the association was found and the delete process was successfully performed
     * @throws TenantRoleException If does not exist Tenant Role for the given id, or Tenant Role exists but
     * is linked with other Entities like Tenant Role Permission or Tenant Role User (so, it could not be removed)
     * @throws SystemException in case of any communication issue with Active Tenant Rest Service client
     */
    public boolean delete(Long id) throws TenantRoleException, SystemException {
        SystemTenantRoleUser systemTenantRoleUser = this.getTenantRoleUserServiceAccess().get(id);
        if (systemTenantRoleUser == null) {
            throw new TenantRoleNotFoundException(GenericErrorCodeMessage.TENANT_ROLE_NO_TENANT_ROLE_USER_FOUND.toString(id.toString()));
        }
        SystemTenantRole tenantRole = this.getTenantRoleServiceAccess().get(systemTenantRoleUser.getTenantRoleId());
        if (tenantRole == null) {
            throw new TenantRoleNotFoundException(TENANT_ROLE_NO_TENANT_ROLE_FOUND.toString(systemTenantRoleUser.
                    getTenantRoleId().toString()));
        }
        // First, remove TenantRoleUser
        boolean status = this.getTenantRoleUserServiceAccess().delete(id);
        // And then remove ActiveTenant
        deleteActiveTenant(systemTenantRoleUser.getUserId(), tenantRole.getTenantId());
        return status;
    }


    /**
     * (Un)Assign/Dissociate/remove user from a Tenant (TenantRole domain)
     * @param tenant Tenant identifier (Mandatory)
     * @param roles Roles identifiers
     * @param user User identifier (Always Mandatory)
     * @throws TenantRoleException for the case of any inconsistency found
     */
    public void unAssignUser(Long tenant, Collection<Long> roles, Long user) throws TenantRoleException, SystemException {
        if (user == null) {
            throw new TenantRoleIllegalArgumentException(GenericErrorCodeMessage.
                    TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.USER_ID.getLabel()));
        }
        if (tenant == null) {
            throw new TenantRoleIllegalArgumentException(GenericErrorCodeMessage.
                    TENANT_ROLE_FIELD_MANDATORY.toString(SystemVariables.TENANT_ID.getLabel()));
        }

        Collection<Long> ids = tenantRoleUserServiceAccess.getTenantRoleUserIds(tenant, roles, user);
        if (ids.isEmpty()) {
            throw new TenantRoleNotFoundException (TENANT_ROLE_NO_ASSOCIATION_FOUND_FOR_PARAMS.
                    toString(String.valueOf(tenant), String.valueOf(roles), String.valueOf(user)));
        }
        tenantRoleUserServiceAccess.delete(ids);
        deleteActiveTenant(user, tenant);
    }

    /**
     * After remove/delete/dissociate a user (TenantRoleUser) is necessary to handle
     * the Active Tenant (remove them as well)
     * @param user user identifier
     * @param tenant tenant identifier
     */
    protected void deleteActiveTenant(Long user, Long tenant) throws SystemException {
        if (!tenantRoleUserServiceAccess.isAssociatedWithTenant(user, tenant)) {
            // If user is no longer associated with the informed tenant, lets remove active tenant as well
            activeTenantRESTServiceAccess.deleteByTenantAndUser(tenant, user);
        }
    }

    /**
     * Getter for the property {@link TenantRoleUserBusinessService#activeTenantRESTServiceAccess}
     * @return instance of ActiveTenantRESTServiceAccess
     */
    public ActiveTenantRESTServiceAccess getActiveTenantRESTServiceAccess() {
        return activeTenantRESTServiceAccess;
    }

    /**
     * Setter for the property {@link TenantRoleUserBusinessService#activeTenantRESTServiceAccess}
     * @param activeTenantRESTServiceAccess instance of ActiveTenantRESTServiceAccess to be set
     */
    public void setActiveTenantRESTServiceAccess(ActiveTenantRESTServiceAccess activeTenantRESTServiceAccess) {
        this.activeTenantRESTServiceAccess = activeTenantRESTServiceAccess;
    }

    /**
     * Getter for the property {@link TenantRoleUserBusinessService#tenantRoleUserServiceAccess}
     * @return instance of {@link TenantRoleUserServiceAccess}
     */
    public TenantRoleUserServiceAccess getTenantRoleUserServiceAccess() {
        return tenantRoleUserServiceAccess;
    }

    /**
     * Getter for the property {@link TenantRoleUserBusinessService#tenantRoleUserServiceAccess}
     * @param tenantRoleUserServiceAccess  instance of {@link TenantRoleUserServiceAccess}
     */
    public void setTenantRoleUserServiceAccess(TenantRoleUserServiceAccess tenantRoleUserServiceAccess) {
        this.tenantRoleUserServiceAccess = tenantRoleUserServiceAccess;
    }

}
