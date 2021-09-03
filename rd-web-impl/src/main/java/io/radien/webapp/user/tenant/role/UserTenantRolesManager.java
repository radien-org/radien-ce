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

import io.radien.api.model.role.SystemRole;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.service.tenantrole.TenantRoleRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleUserRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.rolemanagement.client.entities.TenantRole;
import io.radien.ms.rolemanagement.client.entities.TenantRoleUser;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.DataModelEnum;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.user.UserDataModel;
import io.radien.webapp.util.TenantRoleUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.event.ValueChangeEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
/**
 * JSF manager bean that handle the assign/unassigned
 * role(s) for a selected user the tenant
 *
 * @author Rajesh Gavvala
 */
@Model
@ViewScoped
public class UserTenantRolesManager extends AbstractManager implements Serializable {
    private static final long serialVersionUID = 6413335452953713180L;

    @Inject
    private TenantRoleRESTServiceAccess tenantRoleRESTServiceAccess;

    @Inject
    private TenantRoleUserRESTServiceAccess tenantRoleUserRESTServiceAccess;

    @Inject
    private TenantRoleUtil tenantRoleUtil;

    @Inject
    private UserDataModel userDataModel;

    private SystemTenant tenant = new Tenant();

    private Map<Long, Boolean> isRoleAssigned = new HashMap<>();

    private List<SystemTenant> userTenants;
    private List<SystemRole> assignedRolesForUserTenant = new ArrayList<>();

    private Set<Long> assignableUserTenantRoles = new HashSet<>();
    private Set<Long> unassignedUserTenantRoles = new HashSet<>();

    private boolean isNewRoleObjectAssigned;
    private boolean isOldRoleObjectUnassigned;

    /**
     * This method initializes and constructs
     * the User Tenant(s) and SystemRoles
     * Retrieves list of Tenants associated with the User
     */
    @PostConstruct
    public void init() {
        try{
            userTenants = Collections.unmodifiableList(tenantRoleRESTServiceAccess.getTenants(userDataModel.getSelectedUser().getId(),null));
        } catch(Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.GENERIC_ERROR_MESSAGE.getValue()), JSFUtil.getMessage(DataModelEnum.TENANT_RD_TENANT.getValue()));
        }
    }

    /**
     * Persists change of Tenant object from the Client and constructs default assigned
     * roles for the Tenant User
     * @param systemTenant the selected value object
     */
    public void selectedChangeTenant (ValueChangeEvent systemTenant) throws SystemException {
        if(systemTenant.getNewValue() != null){
            clearDefaultRolesAssignedMap();
            loadUserTenantRoles((SystemTenant) systemTenant.getNewValue());
        }
    }

    /**
     * This method retrieves the assigned Roles for an User
     * that corresponds to the selected Tenant
     */
    public void loadUserTenantRoles(SystemTenant systemTenant) {
        try{
            assignedRolesForUserTenant = Collections.unmodifiableList(tenantRoleRESTServiceAccess
                    .getRolesForUserTenant(userDataModel.getSelectedUser().getId(), systemTenant.getId()));

            if(!assignedRolesForUserTenant.isEmpty()){
                for(SystemRole systemRole : assignedRolesForUserTenant){
                    isRoleAssigned.put(systemRole.getId(), true);
                }
            }
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.GENERIC_ERROR_MESSAGE.getValue()), JSFUtil.getMessage(DataModelEnum.USER_RD_TENANT_ROLE.getValue()));
        }
    }

    /**
     * Persists the change of User Tenant Role from the UI that to be is assignable or unassigned
     * @param isAssignableOrUnassignedRole state of boolean value for the assignable(newValue) or
     *                                     unassigned(oldValue)
     */
    public void isAssignableOrUnassignedRole(ValueChangeEvent isAssignableOrUnassignedRole) {
        if(isAssignableOrUnassignedRole.getOldValue() != null){
            isOldRoleObjectUnassigned = (boolean) isAssignableOrUnassignedRole.getOldValue();
        }
        if(isAssignableOrUnassignedRole.getNewValue() != null){
            isNewRoleObjectAssigned = (boolean) isAssignableOrUnassignedRole.getNewValue();
        }
    }

    /**
     * Prepares set of ids for assignable or unassigned role(s) for the
     * User Tenant
     * @param systemRole persists from the client
     */
    public void isAssignableOrUnassignedRoleType(SystemRole systemRole){
        if(isOldRoleObjectUnassigned){
            unassignedUserTenantRoles.add(systemRole.getId());
            if(!assignableUserTenantRoles.isEmpty()){
                assignableUserTenantRoles.remove(systemRole.getId());
            }
        }

        if(isNewRoleObjectAssigned){
            assignableUserTenantRoles.add(systemRole.getId());
            if(!unassignedUserTenantRoles.isEmpty()){
                unassignedUserTenantRoles.remove(systemRole.getId());
            }
        }
    }

    /**
     * This Method gathers unique set of assignable/unassailable roles
     * for an User of Tenant. Validates & creates Tenant Role association
     * and performs assignable/unassailable association among User Tenant Role(s)
     */
    public void assignOrUnassignedRolesToUserTenant() {
        if(assignableUserTenantRoles != null && !assignableUserTenantRoles.isEmpty()) {
            doAssignedRolesForUserTenant();
            handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage(DataModelEnum.USER_RD_TENANT_ROLE_ASSIGNED_SUCCESS.getValue()));
        }

        if(unassignedUserTenantRoles != null && !unassignedUserTenantRoles.isEmpty()) {
            doUnassignedRolesForUserTenant();
        }
        clearAssignableOrUnAssignedRoles();
    }

    /**
     * This method validates association between
     * the tenant role and performs action
     * assign role for the tenant user
     */
    private void doAssignedRolesForUserTenant() {
        try {
            for(Long systemRoleId : assignableUserTenantRoles) {

                boolean isTenantRoleAssociationExists = tenantRoleRESTServiceAccess.exists(tenant.getId(), systemRoleId);

                boolean isTenantRoleSaved = false;
                if(!isTenantRoleAssociationExists){
                    SystemTenantRole tenantRole = new TenantRole();
                    tenantRole.setTenantId(tenant.getId());
                    tenantRole.setRoleId(systemRoleId);
                    isTenantRoleSaved = tenantRoleRESTServiceAccess.save(tenantRole);
                }

                if(isTenantRoleAssociationExists || isTenantRoleSaved){
                    TenantRoleUser tenantRoleUser = new TenantRoleUser();
                    tenantRoleUser.setTenantRoleId(tenantRoleUtil.getTenantRoleId(tenant.getId(), systemRoleId));
                    tenantRoleUser.setUserId(userDataModel.getSelectedUser().getId());
                    tenantRoleUserRESTServiceAccess.assignUser(tenantRoleUser);
                }
            }
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.USER_RD_TENANT_ROLE_ASSIGNED_ERROR.getValue()));
        }
    }

    /**
     * This method validates association between
     * the tenant role and performs action
     * un assign role for the tenant user
     */
    private void doUnassignedRolesForUserTenant() {
        try {
            boolean isUnassignedRolesForUserTenant = tenantRoleUserRESTServiceAccess
                    .unAssignUser(tenant.getId(), unassignedUserTenantRoles, userDataModel.getSelectedUser().getId());

            if(isUnassignedRolesForUserTenant){
                handleMessage(FacesMessage.SEVERITY_INFO,
                        JSFUtil.getMessage(DataModelEnum.USER_RD_TENANT_ROLE_UNASSIGNED_SUCCESS.getValue()));
            }
        }
        catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.USER_RD_TENANT_ROLE_UNASSIGNED_ERROR.getValue()));
        }
    }

    /**
     * Getter for the Tenant info object
     * @return SystemTenant object
     */
    public SystemTenant getTenant() {
        return tenant;
    }

    /**
     * Sets Tenant info object
     * @param tenant SystemTenant object
     */
    public void setTenant(SystemTenant tenant) {
        this.tenant = tenant;
    }

    /**
     * Getter for the User Tenant(s)
     * @return list of user SystemTenant object(s)
     */
    public List<SystemTenant> getUserTenants() {
        return userTenants;
    }

    /**
     * Setter for the list of user SystemTenant object(s)
     * @param userTenants list User Tenant(s)
     */
    public void setUserTenants(List<SystemTenant> userTenants) {
        this.userTenants = userTenants;
    }

    /**
     * Getter for the default assigned Role(s) for the User Tenant
     * @return list of SystemRole(s) for an User Tenant
     */
    public List<SystemRole> getAssignedRolesForUserTenant() {
        return assignedRolesForUserTenant;
    }

    /**
     * Setter for the list of SystemRole(s) for an User Tenant
     * @param assignedRolesForUserTenant list of assigned Role(s) for an User Tenant
     */
    public void setAssignedRolesForUserTenant(List<SystemRole> assignedRolesForUserTenant) {
        this.assignedRolesForUserTenant = assignedRolesForUserTenant;
    }

    /**
     * Gets assigned mapped Role(s) with boolean flag as true
     * @return map default assigned Role(s)
     */
    public Map<Long, Boolean> getIsRoleAssigned() {
        return isRoleAssigned;
    }

    /**
     * Setter for map default assigned Role(s)
     * @param isRoleAssigned map values of Role(s) Id as key and corresponding value boolean flag of true
     */
    public void setIsRoleAssigned(Map<Long, Boolean> isRoleAssigned) {
        this.isRoleAssigned = isRoleAssigned;
    }

    /**
     * Getter for Role object assigned
     * @return boolean value true if Role is assigned
     */
    public boolean isNewRoleObjectAssigned() {
        return isNewRoleObjectAssigned;
    }

    /**
     * Setter for Role object assigned
     * @param newRoleObjectAssigned Role object assigned true else false
     */
    public void setNewRoleObjectAssigned(boolean newRoleObjectAssigned) {
        isNewRoleObjectAssigned = newRoleObjectAssigned;
    }

    /**
     * Getter for Role object unassigned
     * @return boolean value true if Role is unassigned
     */
    public boolean isOldRoleObjectUnassigned() {
        return isOldRoleObjectUnassigned;
    }

    /**
     * Setter for Role object unassigned
     * @param oldRoleObjectUnassigned Role object unassigned true else false
     */
    public void setOldRoleObjectUnassigned(boolean oldRoleObjectUnassigned) {
        isOldRoleObjectUnassigned = oldRoleObjectUnassigned;
    }

    /**
     * Getter for the assignable Role(s)
     * @return set of assignable Role(s) id(s) for an User Tenant
     */
    public Set<Long> getAssignableUserTenantRoles() {
        return assignableUserTenantRoles;
    }

    /**
     * Setter for set of assignable Role(s) id(s) for an User Tenant
     * @param assignableUserTenantRoles info
     */
    public void setAssignableUserTenantRoles(Set<Long> assignableUserTenantRoles) {
        this.assignableUserTenantRoles = assignableUserTenantRoles;
    }

    /**
     * Getter for the unassigned Role(s)
     * @return set of unassigned Role(s) id(s) for an User Tenant
     */
    public Set<Long> getUnassignedUserTenantRoles() {
        return unassignedUserTenantRoles;
    }

    /**
     * Setter for set of unassigned Role(s) id(s) for an User Tenant
     * @param unassignedUserTenantRoles info
     */
    public void setUnassignedUserTenantRoles(Set<Long> unassignedUserTenantRoles) {
        this.unassignedUserTenantRoles = unassignedUserTenantRoles;
    }

    /**
     * This method invoked when assignable or
     * Unassigned Role(s) set to be clear
     */
    public void clearAssignableOrUnAssignedRoles(){
        if(!assignableUserTenantRoles.isEmpty()){
            assignableUserTenantRoles.clear();
        }
        if(!unassignedUserTenantRoles.isEmpty()){
            unassignedUserTenantRoles.clear();
        }
    }

    /**
     * This method invoked when default Role(s)
     * assigned set to be clear
     */
    public void clearDefaultRolesAssignedMap(){
        if(!isRoleAssigned.isEmpty()){
            isRoleAssigned.clear();
        }
    }

    /**
     * Redirects user to the home page
     * @return a new HTML page
     */
    public String returnBackToUsersTable() {
        try {
            tenant = null;
            userDataModel.setSelectedUser(null);
            clearAssignableOrUnAssignedRoles();
            clearDefaultRolesAssignedMap();
        } catch(Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.GENERIC_ERROR_MESSAGE.getValue()), JSFUtil.getMessage(DataModelEnum.USERS_MESSAGE.getValue()));
            return DataModelEnum.USERS_ROLES_PATH.getValue();
        }
        return DataModelEnum.USERS_PATH.getValue();
    }
}
