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

import io.radien.api.entity.Page;
import io.radien.api.model.role.SystemRole;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.service.role.RoleRESTServiceAccess;
import io.radien.api.service.role.SystemRolesEnum;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.rolemanagement.client.entities.Role;
import io.radien.ms.rolemanagement.client.entities.TenantRole;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.authz.WebAuthorizationChecker;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * JSF manager bean that will handle all associations regarding TenantRole domain
 * @author Newton Carvalho
 */
@Model
@SessionScoped
public class TenantRoleAssociationManager extends AbstractManager {

    @Inject
    private WebAuthorizationChecker webAuthorizationChecker;

    @Inject
    private TenantRoleRESTServiceAccess tenantRoleRESTServiceAccess;

    @Inject
    private RoleRESTServiceAccess roleRESTServiceAccess;

    @Inject
    private TenantRESTServiceAccess tenantRESTServiceAccess;

    private SystemTenant tenant = new Tenant();

    private SystemRole role = new Role();

    /**
     * Perform the association process between user, tenant and role (Tenant and Role are required
     * and must be previously selected from a GUI)
     * @param userId User identifier
     */
    public String associateUser(Long userId) {
        try {
            if (!tenantRoleRESTServiceAccess.exists(tenant.getId(), role.getId())) {
                SystemTenantRole tenantRole = new TenantRole();
                tenantRole.setTenantId(tenant.getId());
                tenantRole.setRoleId(role.getId());
                tenantRoleRESTServiceAccess.save(tenantRole);
            }
            tenantRoleRESTServiceAccess.assignUser(tenant.getId(), role.getId(), userId);
            handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage("rd_tenant_association_creation_success"));
            return "pretty:users";
        }
        catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_tenant_association_creation_error"));
            return "pretty:userTenantAssociation";
        }
    }

    /**
     * Getter for the property that corresponds to the tenant selected for
     * doing the association
     * @return Tenant for which will be performed the association
     */
    public SystemTenant getTenant() { return tenant; }

    /**
     * Setter for the property that corresponds to the tenant selected for
     * doing the association
     * @param tenant  Tenant for which will be performed the association
     */
    public void setTenant(SystemTenant tenant) {
        this.tenant = tenant;
    }

    /**
     * Getter for the property that corresponds to the role selected for
     * doing the association
     * @return Role for which will be performed the association
     */
    public SystemRole getRole() { return role; }

    /**
     * Setter for the property that corresponds to the role selected for
     * doing the association
     * @param role  Role for which will be performed the association
     */
    public void setRole(SystemRole role) {
        this.role = role;
    }

    /**
     * Return a list containing Pre-Defined roles (Not administrative ones)
     * that can be use to do the association between (user - tenant - role)
     * @return list containing Roles
     */
    public List<? extends SystemRole> getInitialRolesAllowedForAssociation() {
        try {
            List<SystemRole> roles = new ArrayList<>();
            roles.add(roleRESTServiceAccess.getRoleByName(SystemRolesEnum.GUEST.getRoleName()).get());
            roles.add(roleRESTServiceAccess.getRoleByName(SystemRolesEnum.APPROVER.getRoleName()).get());
            roles.add(roleRESTServiceAccess.getRoleByName(SystemRolesEnum.AUTHOR.getRoleName()).get());
            roles.add(roleRESTServiceAccess.getRoleByName(SystemRolesEnum.PUBLISHER.getRoleName()).get());
            return roles;
        }
        catch(Exception e) {
            handleError(e, JSFUtil.getMessage("rd_retrieve_error"),
                    JSFUtil.getMessage("rd_roles"));
            return new ArrayList<>();
        }
    }

    /**
     * Retrieve all registered roles
     * @return List containing roles
     * @throws SystemException in case of any error
     */
    public List<? extends SystemRole> getRoles() throws SystemException {
        try {
            Page<? extends SystemRole> pagedInformation =
                    roleRESTServiceAccess.getAll(null, 1, 30, null, true);
            return pagedInformation.getResults();
        }
        catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_retrieve_error"), JSFUtil.getMessage("rd_roles"));
            return new ArrayList<>();
        }
    }


    /**
     * Retrieve all registered tenants
     * @return List containing tenants
     * @throws SystemException in case of any error
     */
    public List<? extends SystemTenant> getTenants() throws SystemException {
        try {
            Page<? extends SystemTenant> pagedInformation =
                    tenantRESTServiceAccess.getAll(null, 1, 30, null, true);
            return pagedInformation.getResults();
        }
        catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_retrieve_error"), JSFUtil.getMessage("rd_roles"));
            return new ArrayList<>();
        }
    }

    /**
     * Return a list containing Tenants for which the current user has
     * has Administrative roles
     * @return list containing tenants
     */
    public List<? extends SystemTenant> getTenantsFromCurrentUser() throws SystemException {
        try {
            return this.tenantRoleRESTServiceAccess.getTenants(this.webAuthorizationChecker.
                    getCurrentUserId(), null);
        }
        catch(Exception e) {
            handleError(e, JSFUtil.getMessage("rd_retrieve_error"),
                    JSFUtil.getMessage("rd_tenants_from_user"));
            return new ArrayList<>();
        }
    }

}
