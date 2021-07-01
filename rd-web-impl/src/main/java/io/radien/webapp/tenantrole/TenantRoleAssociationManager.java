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
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.role.SystemRole;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.model.tenantrole.SystemTenantRoleUser;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.role.RoleRESTServiceAccess;
import io.radien.api.service.role.SystemRolesEnum;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleRESTServiceAccess;
import io.radien.api.service.user.UserRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.permissionmanagement.client.entities.Permission;
import io.radien.ms.rolemanagement.client.entities.Role;
import io.radien.ms.rolemanagement.client.entities.TenantRole;
import io.radien.ms.rolemanagement.client.entities.TenantRoleUser;
import io.radien.ms.rolemanagement.client.services.TenantRoleFactory;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.authz.WebAuthorizationChecker;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.SelectEvent;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Inject
    private UserRESTServiceAccess userRESTServiceAccess;

    private SystemTenant tenant = new Tenant();
    private SystemRole role = new Role();
    private SystemPermission permission = new Permission();
    private SystemTenantRole tenantRole = new TenantRole();
    private SystemUser user = new User();

    private SystemPermission selectedPermissionToUnAssign = new Permission();
    private SystemPermission previousSelectedPermissionToUnAssign = new Permission();

    private SystemTenantRoleUser selectedUserToUnAssign = new TenantRoleUser();
    private SystemTenantRoleUser previousSelectedUserToUnAssign = new TenantRoleUser();

    private List<? extends SystemPermission> assignedPermissions = new ArrayList<>();
    private LazyTenantRoleUserDataModel lazyModel;

    private Boolean tenantRoleAssociationCreated = Boolean.FALSE;

    private Long tabIndex = 0L;

    public static final String K_TENANT_ROLE_SCREEN = "tenantrole";

    /**
     * This method is effectively invoke to create Tenant role association
     * @return mapping id that refers the tenant role creation/edition gui
     */
    public String associateTenantRole() {
        try {
            tenantRole.setTenantId(tenant.getId());
            tenantRole.setRoleId(role.getId());
            tenantRoleRESTServiceAccess.save(tenantRole);
            if (tenantRole.getId() == null) {
                tenantRoleAssociationCreated = true;
            }
            this.prepareUserDataTable();
            handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage("rd_save_success"),
                    JSFUtil.getMessage("tenant_role_association"));
        }
        catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_save_error"),
                    JSFUtil.getMessage("tenant_role_association"));
        }
        return K_TENANT_ROLE_SCREEN;
    }

    /**
     * This method prepares the frontend gui to expose the information
     * regarding the TenantRole to be create (Set some flags to the necessary initial values)
     * return "pretty:tenantrole";
     */
    public String prepareToCreateTenantRole() {
        this.tenantRole = new TenantRole();
        this.assignedPermissions = new ArrayList<>();
        this.previousSelectedPermissionToUnAssign = new Permission();
        this.previousSelectedUserToUnAssign = new TenantRoleUser();
        this.role = new Role();
        this.tenant = new Tenant();
        this.tenantRoleAssociationCreated = Boolean.FALSE;
        this.tabIndex = 0L;
        return K_TENANT_ROLE_SCREEN;
    }

    /**
     * This method prepares the frontend gui to expose the information
     * regarding the TenantRole to be edited
     * @param tenantRole Tenant role to be edited
     * return "pretty:tenantrole";
     */
    public String edit(SystemTenantRole tenantRole) {
        this.tenantRole = tenantRole;
        this.tabIndex = 0L;
        this.permission = new Permission();
        this.user = new User();
        this.previousSelectedPermissionToUnAssign = new Permission();
        this.selectedPermissionToUnAssign = new Permission();
        this.previousSelectedUserToUnAssign = new TenantRoleUser();
        this.selectedUserToUnAssign = new TenantRoleUser();
        try {
            this.role = this.roleRESTServiceAccess.getRoleById(this.tenantRole.getRoleId()).
                    orElseThrow(() -> new SystemException(MessageFormat.format(JSFUtil.getMessage(
                            "rd_role_not_found"), this.tenantRole.getRoleId())));

            this.tenant = this.tenantRESTServiceAccess.getTenantById(this.tenantRole.getTenantId()).
                    orElseThrow(() -> new SystemException(MessageFormat.format(JSFUtil.getMessage(
                            "rd_tenant_not_found"), this.tenantRole.getTenantId())));
            this.calculatePermissions();
            this.prepareUserDataTable();
        }
        catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_edit_error"),
                    JSFUtil.getMessage("rd_tenant_role_association"));
        }
        return K_TENANT_ROLE_SCREEN;
    }

    /**
     * Back method that helps to infer if a TenantRole association was already
     * created. It can be used by the GUI to able/disable some view parts
     * @return true if exists, false otherwise
     */
    public boolean isExistsTenantRoleCreated() {
        return (this.tenantRole != null && tenantRole.getId() != null) ||
                this.tenantRoleAssociationCreated;
    }

    /**
     * Retrieve permissions for tenant role combination
     * @return list containing permissions
     */
    public List<? extends SystemPermission> calculatePermissions() {
        try {
            this.assignedPermissions = tenantRoleRESTServiceAccess.
                    getPermissions(tenant.getId(), role.getId(), null);
            return assignedPermissions;
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_retrieve_error"),
                    JSFUtil.getMessage("rd_permissions"));
            return new ArrayList<>();
        }
    }

    /**
     * Given a tenant and a role, retrieves the id that exists for that
     * relationship
     * @return Id (key) that must exist for a Tenant Role association
     */
    protected Long getTenantRoleId() {
        if (tenant == null || tenant.getId() == null || role == null || role.getId() == null) {
            return null;
        }
        try {
            List<? extends SystemTenantRole> tenantRoles = tenantRoleRESTServiceAccess.
                    getTenantRoles(tenant.getId(), role.getId(), true);
            return !tenantRoles.isEmpty() ? tenantRoles.get(0).getId() : null;
        }
        catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_retrieve_error"),
                    JSFUtil.getMessage("tenant_role_association_id"));
            return null;
        }
    }

    /**
     * Retrieve users for tenant role combination
     */
    public void prepareUserDataTable() {
        if (this.lazyModel == null) {
            this.lazyModel = new LazyTenantRoleUserDataModel(tenantRoleRESTServiceAccess,
                    userRESTServiceAccess);
        }
        this.lazyModel.setTenantRoleId(getTenantRoleId());
    }

    /**
     * Perform the association process between permission, tenant and role (Tenant and Role are required
     * and must be previously selected from a GUI)
     */
    public String assignPermission() {
        this.tabIndex = 1L;
        try {
            if (permission == null || permission.getId() == null) {
                throw new IllegalArgumentException(JSFUtil.
                        getMessage("rd_tenant_role_permission_association_no_permission_select"));
            }
            this.tenantRoleRESTServiceAccess.assignPermission(tenant.getId(), role.getId(), permission.getId());
            this.calculatePermissions();
            handleMessage(FacesMessage.SEVERITY_INFO,
                    JSFUtil.getMessage("rd_tenant_role_permission_association_success"));
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_tenant_role_permission_association_error"));
        }
        return K_TENANT_ROLE_SCREEN;
    }

    /**
     * Perform the association process between permission, tenant and role (Tenant and Role are required
     * and must be previously selected from a GUI)
     */
    public String unAssignPermission() {
        this.tabIndex = 1L;
        try {
            if (selectedPermissionToUnAssign == null || selectedPermissionToUnAssign.getId() == null) {
                throw new IllegalArgumentException(JSFUtil.
                        getMessage("rd_tenant_role_permission_dissociation_no_permission_select"));
            }
            this.tenantRoleRESTServiceAccess.unassignPermission(tenant.getId(), role.getId(),
                    selectedPermissionToUnAssign.getId());
            this.calculatePermissions();
            handleMessage(FacesMessage.SEVERITY_INFO,
                    JSFUtil.getMessage("rd_tenant_role_permission_dissociation_success"));
            this.selectedPermissionToUnAssign = new Permission();
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_tenant_role_permission_dissociation_error"));
        }
        return K_TENANT_ROLE_SCREEN;
    }

    /**
     * Perform the association process between user, tenant and role (Tenant and Role are required
     * and must be previously selected from a GUI)
     * @param userId User identifier
     */
    public String associateUser(Long userId) {
        try {
            if (!tenantRoleRESTServiceAccess.exists(tenant.getId(), role.getId())) {
                SystemTenantRole tr = TenantRoleFactory.create(tenant.getId(), role.getId(),
                        webAuthorizationChecker.getCurrentUserId());
                tenantRoleRESTServiceAccess.save(tr);
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
     * Perform the association process between user, tenant and role (Tenant and Role are required
     * and must be previously selected from a GUI)
     */
    public String assignUser() {
        this.tabIndex = 2L;
        try {
            if (user == null || StringUtils.isBlank(user.getLogon())) {
                throw new IllegalArgumentException(JSFUtil.
                        getMessage("rd_tenant_role_user_association_no_user_select"));
            }
            user = userRESTServiceAccess.getUserByLogon(user.getLogon()).orElseThrow
                        (() -> new SystemException(MessageFormat.format(JSFUtil.getMessage(
                        "rd_user_not_found"), this.user.getLogon())));

            this.tenantRoleRESTServiceAccess.assignUser(tenant.getId(), role.getId(), user.getId());
            this.prepareUserDataTable();
            handleMessage(FacesMessage.SEVERITY_INFO,
                    JSFUtil.getMessage("rd_tenant_role_user_association_success"));
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_tenant_role_user_association_error"));
        }
        return K_TENANT_ROLE_SCREEN;
    }

    /**
     * Perform the association process between user, tenant and role (Tenant and Role are required
     * and must be previously selected from a GUI)
     */
    public String unAssignUser() {
        this.tabIndex = 2L;
        try {
            if (selectedUserToUnAssign == null || selectedUserToUnAssign.getUserId() == null) {
                throw new IllegalArgumentException(JSFUtil.
                        getMessage("rd_tenant_role_user_dissociation_no_user_select"));
            }
            this.tenantRoleRESTServiceAccess.unassignUser(tenant.getId(), role.getId(),
                    selectedUserToUnAssign.getUserId());
            this.prepareUserDataTable();
            this.selectedUserToUnAssign = new TenantRoleUser();
            handleMessage(FacesMessage.SEVERITY_INFO,
                    JSFUtil.getMessage("rd_tenant_role_user_dissociation_success"));
            this.selectedPermissionToUnAssign = new Permission();
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_tenant_role_user_dissociation_error"));
        }
        return K_TENANT_ROLE_SCREEN;
    }

    /**
     * Getter for the property that corresponds to the user for
     * whom the association is going to be done
     * @return User for which will be performed the association
     */
    public SystemUser getUser() {
        return user;
    }

    /**
     * Setter for the property that corresponds to the user for
     * whom the association is going to be done
     * @param user User for which will be performed the association
     */
    public void setUser(SystemUser user) {
        this.user = user;
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
     * Getter for the property that corresponds to the Permission selected for
     * doing the association
     * @return Permission for which will be performed the association
     */
    public SystemPermission getPermission() {
        return permission;
    }

    /**
     * Setter for the property that corresponds to the Permission selected for
     * doing the association
     * @param permission Permission for which will be performed the association
     */
    public void setPermission(SystemPermission permission) {
        this.permission = permission;
    }

    /**
     * Getter for the Initial entity to be persisted
     * @return tenant role to be persisted
     */
    public SystemTenantRole getTenantRole() {
        return tenantRole;
    }

    /**
     * Getter for the Initial entity to be persisted
     * @return tenant role to be persisted
     */
    public void setTenantRole(SystemTenantRole tenantRole) {
        this.tenantRole = tenantRole;
    }

    /**
     * Getter for the assigned permissions
     * @return List containing permissions
     */
    public List<? extends SystemPermission> getAssignedPermissions() {
        return assignedPermissions;
    }

    /**
     * Setter for the assigned permission
     * @param assignedPermissions list of the assigned permission
     */
    public void setAssignedPermissions(List<SystemPermission> assignedPermissions) {
        this.assignedPermissions = assignedPermissions;
    }

    /**
     * Getter for property that indicates which tab will be shown active
     * @return Long value that corresponds to the tab index
     */
    public Long getTabIndex() {
        return tabIndex;
    }

    /**
     * Setter for property that indicates which tab will be shown active
     * @param tabIndex Long value that corresponds to the tab index
     */
    public void setTabIndex(Long tabIndex) {
        this.tabIndex = tabIndex;
    }

    /**
     * Getter for the property that corresponds to the Permission selected for
     * doing the dissociation
     * @return Permission for which will be performed the dissociation
     */
    public SystemPermission getSelectedPermissionToUnAssign() {
        return selectedPermissionToUnAssign;
    }

    /**
     * Setter for the property that corresponds to the Permission selected for
     * doing the dissociation
     * @param selectedPermissionToUnAssign Permission for which will be performed the dissociation
     */
    public void setSelectedPermissionToUnAssign(SystemPermission selectedPermissionToUnAssign) {
        this.selectedPermissionToUnAssign = selectedPermissionToUnAssign;
    }

    /**
     * Getter for the property that corresponds to the Permission Previously selected for
     * doing the dissociation
     * @return Previous Permission for which the dissociation was performed
     */
    public SystemPermission getPreviousSelectedPermissionToUnAssign() {
        return previousSelectedPermissionToUnAssign;
    }

    /**
     * Setter for the property that corresponds to the Permission Previously selected for
     * doing the dissociation
     * @param previousSelectedPermissionToUnAssign Previous Permission for which the dissociation was performed
     */
    public void setPreviousSelectedPermissionToUnAssign(SystemPermission previousSelectedPermissionToUnAssign) {
        this.previousSelectedPermissionToUnAssign = previousSelectedPermissionToUnAssign;
    }

    /**
     * Stores the information regarding a selected permission
     * @param event that will contain which permission has been selected
     */
    public void onPermissionSelect(SelectEvent<SystemPermission> event) {
        if (previousSelectedPermissionToUnAssign != null && event.getObject().getId().
                equals(previousSelectedPermissionToUnAssign.getId())) {
            // remove selection
            previousSelectedPermissionToUnAssign = new Permission();
            selectedPermissionToUnAssign = new Permission();
        } else {
            // select
            previousSelectedPermissionToUnAssign =event.getObject();
        }
        this.tabIndex = 1L;
    }

    /**
     * Stores the information regarding a selected user
     * @param event that will contain which user has been selected
     */
    public void onUserSelect(SelectEvent<SystemTenantRoleUser> event) {
        if (previousSelectedUserToUnAssign != null && event.getObject().getId().
                equals(previousSelectedUserToUnAssign.getId())) {
            // remove selection
            previousSelectedUserToUnAssign = new TenantRoleUser();
            selectedUserToUnAssign = new TenantRoleUser();
        } else {
            // select
            previousSelectedUserToUnAssign =event.getObject();
        }
        this.tabIndex = 2L;
    }

    /**
     * Return a list containing Pre-Defined roles (Not administrative ones)
     * that can be use to do the association between (user - tenant - role)
     * @return list containing Roles
     */
    public List<SystemRole> getInitialRolesAllowedForAssociation() {
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
    public List<SystemRole> getRoles() throws SystemException {
        try {
            Page pagedInformation =
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
    public List<SystemTenant> getTenants() throws SystemException {
        try {
            Page pagedInformation =
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

    /**
     * This getter method retrieves the current instance for
     * LazyTenantRoleUserDataModel component
     * @return instance for LazyTenantRoleUserDataModel
     */
    public LazyTenantRoleUserDataModel getLazyModel() {
        return lazyModel;
    }

    /**
     * Getter for the property that corresponds to an user selected on
     * the data grid to perform an (un)assigment/dissociation
     * @return instance of SystemTenantRoleUser
     */
    public SystemTenantRoleUser getSelectedUserToUnAssign() {
        return selectedUserToUnAssign;
    }

    /**
     * Setter for the property that corresponds to an user selected on
     * the data grid to perform an (un)assigment/dissociation
     * @param selectedUserToUnAssign  instance of SystemTenantRoleUser
     */
    public void setSelectedUserToUnAssign(SystemTenantRoleUser selectedUserToUnAssign) {
        this.selectedUserToUnAssign = selectedUserToUnAssign;
    }

    /**
     * Getter for the property that corresponds to the User Previously selected for
     * doing the dissociation
     * @return Previous User for which the dissociation was performed
     */
    public SystemTenantRoleUser getPreviousSelectedUserToUnAssign() {
        return previousSelectedUserToUnAssign;
    }

    /**
     * Setter for the property that corresponds to the User Previously selected for
     * doing the dissociation
     * @param previousSelectedPermissionToUnAssign Previous User for which the dissociation was performed
     */
    public void setPreviousSelectedUserToUnAssign(SystemTenantRoleUser previousSelectedPermissionToUnAssign) {
        this.previousSelectedUserToUnAssign = previousSelectedPermissionToUnAssign;
    }

}
