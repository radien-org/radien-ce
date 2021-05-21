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
package io.radien.webapp.user;

import io.radien.api.model.role.SystemRole;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.model.user.SystemUser;
import io.radien.api.security.UserSessionEnabled;
import io.radien.api.service.role.RoleRESTServiceAccess;
import io.radien.api.service.role.SystemRolesEnum;
import io.radien.api.service.tenantrole.TenantRoleRESTServiceAccess;
import io.radien.api.service.user.UserRESTServiceAccess;

import io.radien.exception.SystemException;
import io.radien.ms.rolemanagement.client.entities.Role;
import io.radien.ms.rolemanagement.client.entities.TenantRole;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.authz.WebAuthorizationChecker;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;

import javax.annotation.PostConstruct;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import javax.inject.Inject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rajesh Gavvala
 */
@Model
@SessionScoped
public class UserDataModel extends AbstractManager implements Serializable {
    private static final long serialVersionUID = -4406564138942194060L;

    @Inject
    private UserRESTServiceAccess service;

    @Inject
    private UserSessionEnabled userSessionEnabled;

    @Inject
    private WebAuthorizationChecker webAuthorizationChecker;

    @Inject
    private TenantRoleRESTServiceAccess tenantRoleRESTServiceAccess;

    @Inject
    private RoleRESTServiceAccess roleRESTServiceAccess;

    private LazyDataModel<? extends SystemUser> lazyUserDataModel;
    private SystemUser selectedUser;
    private SystemUser userForTenantAssociation;
    private SystemUser user = new User();

    private SystemTenant tenantForAssociation = new Tenant();
    private SystemRole roleForAssociation = new Role();

    private boolean hasUserAdministratorRoleAccess = false;
    private boolean hasTenantAdministratorRoleAccess = false;

    /**
     * Post construction method for user management page
     * @throws SystemException SystemException is thrown by the common language runtime when errors occur
     * that are nonfatal and recoverable by user programs.
     */
    @PostConstruct
    public void init() throws SystemException {
        try {
            if (!hasUserAdministratorRoleAccess) {
                hasUserAdministratorRoleAccess = webAuthorizationChecker.hasUserAdministratorRoleAccess();
            }
            if (!hasTenantAdministratorRoleAccess) {
                hasTenantAdministratorRoleAccess = webAuthorizationChecker.hasTenantAdministratorRoleAccess();
            }

            if (hasUserAdministratorRoleAccess) {
                lazyUserDataModel = new LazyUserDataModel(service);
            }
        } catch(Exception e) {
            handleError(e, JSFUtil.getMessage("rd_generic_error_message"), JSFUtil.getMessage("rd_users"));
        }
    }

    /**
     * When reloading the user management page
     * @throws SystemException SystemException is thrown by the common language runtime when errors occur
     * that are nonfatal and recoverable by user programs.
     */
    public void onload() throws SystemException {
        try {
            init();
        } catch(Exception e) {
            handleError(e, JSFUtil.getMessage("rd_generic_error_message"), JSFUtil.getMessage("rd_users"));
        }
    }

    /**
     * Loads in a lazy loading system all the records for the user management
     * @return a lazy loading list of system users
     */
    public LazyDataModel<? extends SystemUser> getLazyUserDataModel() {
        return lazyUserDataModel;
    }

    /**
     * Sets a Lazy Loading list with a given object
     * @param lazyUserDataModel to be set
     */
    public void setLazyUserDataModel(LazyDataModel<? extends SystemUser> lazyUserDataModel) {
        this.lazyUserDataModel = lazyUserDataModel;
    }

    /**
     * When selecting a user line will return user information
     * @return the selected system user information
     */
    public SystemUser getSelectedUser() {
        return selectedUser;
    }

    /**
     * Sets a new selected user information
     * @param selectedUser information to be set
     */
    public void setSelectedUser(SystemUser selectedUser) {
        this.selectedUser = selectedUser;
    }

    /**
     * Gets user information
     * @return a system user
     */
    public SystemUser getUser() { return user; }

    /**
     * Sets a user with a given user object information
     * @param user object to be set
     */
    public void setUser(SystemUser user) { this.user = user; }

    /**
     * Retrieves the active User Rest Service Access information
     * @return a UserRESTServiceAccess object
     */
    public UserRESTServiceAccess getService() {
        return service;
    }

    /**
     * Sets the active UserRESTServiceAccess as the current one that has been given
     * @param service to be set
     */
    public void setService(UserRESTServiceAccess service) {
        this.service = service;
    }

    /**
     * Method to update a given user information that have been edited
     * @param updateUser new user information to be saved
     */
    public void updateUser(SystemUser updateUser){
        try{
            if(updateUser != null){
                if (updateUser.getId() == null) {
                    updateUser.setCreateUser(userSessionEnabled.getUserId());
                } else {
                    updateUser.setLastUpdateUser(userSessionEnabled.getUserId());
                }
                service.updateUser(updateUser);
                handleMessage(FacesMessage.SEVERITY_INFO,
                        updateUser.getId() == null ? JSFUtil.getMessage("rd_save_success") :
                        JSFUtil.getMessage("rd_edit_success"), JSFUtil.getMessage("rd_user"));
            }
        }catch (Exception e){
            handleError(e, updateUser.getId() == null ? JSFUtil.getMessage("rd_save_error") :
                    JSFUtil.getMessage("rd_edit_error"), JSFUtil.getMessage("rd_user"));
        }
    }

    /**
     * Deletes the requested user and all his information
     */
    public void deleteUser(){
        try{
            if(selectedUser != null){
                service.deleteUser(selectedUser.getId());
                handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage("rd_delete_success"), JSFUtil.getMessage("rd_user"));
            }
        }catch (Exception e){
            handleError(e, JSFUtil.getMessage("rd_delete_error"), JSFUtil.getMessage("rd_user"));
        }
    }

    /**
     * Send the update password email to the correct active user
     */
    public void sendUpdatePasswordEmail(){
        try{
            if(selectedUser != null){
                service.sendUpdatePasswordEmail(selectedUser.getId());
                handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage("rd_send_update_password_email_success"),
                        JSFUtil.getMessage("rd_user"));
            }
        }catch (Exception e){
            handleError(e, JSFUtil.getMessage("rd_send_update_password_email_error"), JSFUtil.getMessage("rd_user"));
        }
    }

    /**
     * Redirects user to the page of editing the user
     * @return a new HTML page
     */
    public String editRecord() {
        try {
            if (selectedUser != null) {
                return "pretty:user";
            }
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_generic_error_message"), JSFUtil.getMessage("rd_users"));
        }
        return "pretty:users";
    }

    /**
     * Redirects user to the page of creation the user
     * @return a new HTML page
     */
    public String createRecord() {
        try {
            user = new User();
            user.setEnabled(true);
        } catch(Exception e) {
            handleError(e, JSFUtil.getMessage("rd_generic_error_message"), JSFUtil.getMessage("rd_users"));
        }
        return "pretty:user";
    }

    /**
     * Redirects user to the page of user profile information
     * @return a new HTML page
     */
    public String userProfile() {
        try {
            if(selectedUser != null) {
                return "pretty:userProfile";
            }
        } catch(Exception e) {
            handleError(e, JSFUtil.getMessage("rd_generic_error_message"), JSFUtil.getMessage("rd_users"));
        }
        return "pretty:users";
    }

    /**
     * Redirects user to the home page
     * @return a new HTML page
     */
    public String returnHome() {
        try {
            user = new User();
            selectedUser=null;
        } catch(Exception e) {
            handleError(e, JSFUtil.getMessage("rd_generic_error_message"), JSFUtil.getMessage("rd_users"));
        }
        return "pretty:users";
    }

    /**
     * Stores the information selected by the current user to be used for later
     * @param event that will contain which user has been selected
     */
    public void onRowSelect(SelectEvent<SystemUser> event) {
        this.selectedUser=event.getObject();
        FacesMessage msg = new FacesMessage("User Selected", String.valueOf(event.getObject().getLogon()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }


    /**
     * Check if the tenant association process is allowed to be started from
     * the following perspective:
     * <ul>
     *     <li>Current logged user has the right roles (the one who will start/trigger the process)</li>
     *     <li>There is a user available to be associated with a Tenant (i.e a newly created user
     *     or a previously selected one from the data grid)</li>
     * </ul>
     * @return true if the process can be handled/started, false otherwise
     */
    public boolean isTenantAssociationProcessAllowed() {
        if (!isHasTenantAdministratorRoleAccess()) {
            return false;
        }
        this.userForTenantAssociation = findUserToAssociate();
        return this.userForTenantAssociation != null;
    }

    /**
     * Find the user that may participate on the tenant association process
     * @return SystemUser instance, or null in case of not found any user
     */
    protected SystemUser findUserToAssociate() {
        // Corresponds to the user picked from the data grid (user selected to be update, etc)
        if (this.selectedUser != null && this.selectedUser.getId() != null) {
            return this.selectedUser;
        }
        // Corresponds to the user recently created
        if (this.user != null && !StringUtils.isEmpty(this.user.getLogon())) {
            return getSystemUserFromLogon(this.user.getLogon());
        }
        return null;
    }

    /**
     * Retrieve a SystemUser using logon as parameter
     * @param logon parameter that will guide the search process
     * @return User instance (if there is one available for the informed logon)
     */
    protected SystemUser getSystemUserFromLogon(String logon) {
        try {
            return service.getUserByLogon(logon).orElse(null);
        } catch(Exception e) {
            this.log.error("Error retrieving user from logon", e);
            return null;
        }
    }

    /**
     * This method do some preparing process (Pre-Processing) before redirecting
     * to the Tenant association screen
     * @return url mapping that refers Tenant association screen
     */
    public String prepareTenantAssociation() {
        return "pretty:userTenantAssociation";
    }

    /**
     * Validates if current user has System Administrator or User Administrator roles
     * @return true in case of so
     */
    public boolean getHasUserAdministratorRoleAccess() {
        return hasUserAdministratorRoleAccess;
    }

    /**
     * Sets if current user has System Administrator or User Administrator roles
     */
    public void setHasUserAdministratorRoleAccess(boolean hasUserAdministratorRoleAccess) {
        this.hasUserAdministratorRoleAccess = hasUserAdministratorRoleAccess;
    }

    /**
     * Flag indicating if current user has System Administrator or any other Tenant Administration role
     * @return true if such condition is affirmative, false otherwise
     */
    public boolean isHasTenantAdministratorRoleAccess() {
        return hasTenantAdministratorRoleAccess;
    }

    /**
     * Sets a flag indicating if the current user has System Administrator or
     * any other Tenant Administration role
     * @param hasTenantAdministratorRoleAccess boolean value to be set
     */
    public void setHasTenantAdministratorRoleAccess(boolean hasTenantAdministratorRoleAccess) {
        this.hasTenantAdministratorRoleAccess = hasTenantAdministratorRoleAccess;
    }

    /**
     * Getter that refers tne User for whom will be applied the association with a tenant and a role
     * @return user for whom will done the association
     */
    public SystemUser getUserForTenantAssociation() {
        return userForTenantAssociation;
    }

    /**
     * Setter that refers tne User for whom will be applied the association with a tenant and a role
     * @param userForTenantAssociation  user for whom will done the associaton
     */
    public void setUserForTenantAssociation(SystemUser userForTenantAssociation) {
        this.userForTenantAssociation = userForTenantAssociation;
    }
}
