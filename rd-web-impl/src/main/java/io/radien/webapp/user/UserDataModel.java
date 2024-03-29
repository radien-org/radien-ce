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
package io.radien.webapp.user;

import io.radien.api.model.tenant.SystemActiveTenant;
import io.radien.api.model.tenantrole.SystemTenantRoleUser;
import io.radien.api.model.user.SystemUser;
import io.radien.api.security.UserSessionEnabled;
import io.radien.api.service.permission.SystemActionsEnum;
import io.radien.api.service.permission.SystemResourcesEnum;
import io.radien.api.service.tenant.ActiveTenantRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleUserRESTServiceAccess;
import io.radien.api.service.user.UserRESTServiceAccess;

import io.radien.exception.SystemException;
import io.radien.ms.usermanagement.client.entities.User;

import io.radien.webapp.AbstractManager;
import io.radien.webapp.DataModelEnum;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.activeTenant.ActiveTenantDataModelManager;
import io.radien.webapp.activeTenant.ActiveTenantMandatory;
import io.radien.webapp.authz.WebAuthorizationChecker;
import io.radien.webapp.tenantrole.LazyTenantingUserDataModel;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
/**
 * Class manages Users data grid
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
    private TenantRoleUserRESTServiceAccess tenantRoleUserRESTServiceAccess;

    @Inject
    private ActiveTenantDataModelManager activeTenantDataModelManager;
    
    @Inject
    private ActiveTenantRESTServiceAccess activeTenantRESTServiceAccess;

    private LazyDataModel<? extends SystemUser> lazyUserDataModel;

    private SystemUser selectedUser;
    private SystemUser updateEmail = new User();
    private SystemUser userForTenantAssociation;
    private SystemUser user = new User();

    private boolean allowedReadUser = false;
    private boolean allowedUpdateUser = false;
    private boolean allowedDeleteUser = false;
    private boolean allowedCreateUser = false;

    private boolean allowedAssociateTenantAndUser = false;
    private boolean allowedToResetPassword = false;
    private boolean allowedToUpdateUserEmail = false;
    
    /**
     * Post construction method for user management page
     * that are nonfatal and recoverable by user programs.
     */
    @PostConstruct
    public void init() {
        try {
            if(activeTenantDataModelManager.isTenantActive()) {
                if (!allowedReadUser) {
                    allowedReadUser = webAuthorizationChecker.hasPermissionAccess(SystemResourcesEnum.USER.getResourceName(), SystemActionsEnum.ACTION_READ.getActionName(), null);
                }
                if (!allowedUpdateUser) {
                    allowedUpdateUser = webAuthorizationChecker.hasPermissionAccess(SystemResourcesEnum.USER.getResourceName(), SystemActionsEnum.ACTION_UPDATE.getActionName(), null);
                }
                if (!allowedDeleteUser) {
                    allowedDeleteUser = webAuthorizationChecker.hasPermissionAccess(SystemResourcesEnum.USER.getResourceName(), SystemActionsEnum.ACTION_DELETE.getActionName(), null);
                }
                if (!allowedCreateUser) {
                    allowedCreateUser = webAuthorizationChecker.hasPermissionAccess(SystemResourcesEnum.USER.getResourceName(), SystemActionsEnum.ACTION_CREATE.getActionName(), null);
                }
                if (!allowedAssociateTenantAndUser) {
                    allowedAssociateTenantAndUser = webAuthorizationChecker.hasPermissionAccess(SystemResourcesEnum.TENANT_ROLE_USER.getResourceName(),
                            SystemActionsEnum.ACTION_CREATE.getActionName(), null);
                }

                Long tenantId = getCurrentActiveTenantId();
                allowedToResetPassword = webAuthorizationChecker.hasPermissionToResetPassword(tenantId);

                allowedToUpdateUserEmail = webAuthorizationChecker.hasPermissionToUpdateUserEmail(tenantId);

                if (allowedReadUser) {
                    // Must retrieve user compatible with the Active Tenant
                    lazyUserDataModel = new LazyTenantingUserDataModel(tenantRoleUserRESTServiceAccess,
                            service);
                    ((LazyTenantingUserDataModel) lazyUserDataModel).setTenantId(tenantId);
                }
            } else {
                redirectToHomePage();
            }
        } catch(Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.GENERIC_ERROR_MESSAGE.getValue()),
                    JSFUtil.getMessage(DataModelEnum.USERS_MESSAGE.getValue()));
        }
    }

    /**
     * Retrieve the active tenant id for the current user
     * @return long value that corresponds to the tenant id
     */
    protected Long getCurrentActiveTenantId() {
        SystemActiveTenant activeTenant = activeTenantDataModelManager.getActiveTenant();
        return activeTenant != null ? activeTenant.getTenantId() : null;
    }

    /**
     * When reloading the user management page
     * @throws SystemException SystemException is thrown by the common language runtime when errors occur
     * that are nonfatal and recoverable by user programs.
     */
    @ActiveTenantMandatory
    public void onload() {
        init();
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
    public void setUser(SystemUser user) { this.user = user;}

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
     * Getter method for the flag property that indicates if the current user has grant
     * to reset a password or not
     * @return true for positive case, otherwise false
     */
    public boolean isAllowedToResetPassword() {
        return allowedToResetPassword;
    }

    /**
     * Setter method for the flag property that indicates if the current user has grant
     * to reset a password or not
     * @param allowedToResetPassword boolean value that if express the user has permission or not to reset password
     */
    public void setAllowedToResetPassword(boolean allowedToResetPassword) {
        this.allowedToResetPassword = allowedToResetPassword;
    }

    /**
     * Method to save (create or update) a given user information that have been edited
     * @param saveUser new user information to be saved (create or update)
     */
    @ActiveTenantMandatory
    public boolean saveUser(SystemUser saveUser, boolean checkForProcessingLocked){
        try{
            if(saveUser != null){
                if (!checkForProcessingLocked || !saveUser.isProcessingLocked()) {
                    if (saveUser.getId() == null) {
                        saveUser.setCreateUser(userSessionEnabled.getUserId());
                        service.create(saveUser, saveUser.isDelegatedCreation());
                    } else {
                        saveUser.setLastUpdateUser(userSessionEnabled.getUserId());
                        service.updateUser(saveUser);
                    }
                    handleMessage(FacesMessage.SEVERITY_INFO,
                            saveUser.getId() == null ? JSFUtil.getMessage(DataModelEnum.SAVE_SUCCESS_MESSAGE.getValue()) :
                                    JSFUtil.getMessage(DataModelEnum.EDIT_SUCCESS.getValue()), JSFUtil.getMessage(DataModelEnum.USER_MESSAGE.getValue()));
                    return true;
                } else {
                    
                    handleMessage(FacesMessage.SEVERITY_ERROR, JSFUtil.getMessage(DataModelEnum.PROCESSINGLOCKED_BLOCKS_ACTION.getValue()));
                }
            }
        }catch (Exception e){
            handleError(e, saveUser.getId() == null ? JSFUtil.getMessage(DataModelEnum.SAVE_ERROR_MESSAGE.getValue()) :
                    JSFUtil.getMessage(DataModelEnum.EDIT_ERROR_MESSAGE.getValue()), JSFUtil.getMessage(DataModelEnum.USER_MESSAGE.getValue()));
        }
        return false;
    }

    /**
     * Deletes the requested user and all his information
     */
    @ActiveTenantMandatory
    public void deleteUser(){
        try{
            if(selectedUser != null) {
                if (!selectedUser.isProcessingLocked()) {
                    Long userId = selectedUser.getId();
                    service.deleteUser(userId);

                    List<? extends SystemTenantRoleUser> tenantRolesUser = tenantRoleUserRESTServiceAccess.getTenantRoleUsers(null, userId, false);
                    for (SystemTenantRoleUser systemTenantRoleUser : tenantRolesUser) {
                        tenantRoleUserRESTServiceAccess.delete(systemTenantRoleUser.getTenantRoleId());
                    }

                    List<? extends SystemActiveTenant> activeTenants = activeTenantRESTServiceAccess.getActiveTenantByFilter(userId, null);
                    for (SystemActiveTenant systemActiveTenant : activeTenants) {
                        activeTenantRESTServiceAccess.delete(systemActiveTenant.getId());
                    }

                    handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage(DataModelEnum.DELETE_SUCCESS.getValue()), JSFUtil.getMessage(DataModelEnum.USER_MESSAGE.getValue()));
                }else {
                    handleMessage(FacesMessage.SEVERITY_ERROR, JSFUtil.getMessage(DataModelEnum.PROCESSINGLOCKED_BLOCKS_ACTION.getValue()), "");
                }
            }
        }catch (Exception e){
            handleError(e, JSFUtil.getMessage(DataModelEnum.DELETE_ERROR.getValue()), JSFUtil.getMessage(DataModelEnum.USER_MESSAGE.getValue()));
        }
    }

    public void changeProcessingLocked(SystemUser saveUser){
        saveUser.setProcessingLocked(!saveUser.isProcessingLocked());
        saveUser(saveUser, false);
    }

    /**
     * Send the update password email to the correct active user
     */
    public void sendUpdatePasswordEmail(){
        try{
            if(selectedUser != null){
                service.sendUpdatePasswordEmail(selectedUser.getId());
                handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage(DataModelEnum.SENT_UPDATE_PASSWORD_EMAIL_SUCCESS.getValue()),
                        JSFUtil.getMessage(DataModelEnum.USER_MESSAGE.getValue()));
            }
        }catch (Exception e){
            handleError(e, JSFUtil.getMessage(DataModelEnum.SENT_UPDATE_PASSWORD_EMAIL_ERROR.getValue()), JSFUtil.getMessage(DataModelEnum.USER_MESSAGE.getValue()));
        }
    }

    /**
     * Assemblies a message to be shown in the frontend when an administrator
     * requests to reset the password of a selected user
     * @return message to be shown
     */
    public String getResetPasswordMessage() {
        String userLogin = selectedUser != null? selectedUser.getLogon() : "";
        return MessageFormat.format(JSFUtil.getMessage(DataModelEnum.USER_RESET_PASSWORD_CONFIRM_MESSAGE.getValue()),
                userLogin);
    }

    /**
     * Redirects user to the page of editing the user
     * @return a new HTML page
     */
    @ActiveTenantMandatory
    public String editRecord() {
        if (selectedUser != null) {
            return DataModelEnum.USER_PATH.getValue();
        }
        return DataModelEnum.USERS_PATH.getValue();
    }

    /**
     * Redirects user to the page of delete the user
     * @return a new HTML page
     */
    @ActiveTenantMandatory
    public String deleteRecord() {
        if (checkProcessingLocked()) {
            handleMessage(FacesMessage.SEVERITY_ERROR, JSFUtil.getMessage(DataModelEnum.PROCESSINGLOCKED_BLOCKS_ACTION.getValue()));
        } else if (selectedUser != null) {
            return DataModelEnum.USER_DELETE_PATH.getValue();
        }
        return DataModelEnum.USERS_PATH.getValue();
    }

    /**
     * Redirects user to the page of creation the user
     * @return a new HTML page
     */
    @ActiveTenantMandatory
    public String createRecord() {
        user = new User();
        user.setEnabled(true);
        return DataModelEnum.USER_PATH.getValue();
    }

    /**
     * Redirects user to the page of user profile information
     * @return a new HTML page
     */
    @ActiveTenantMandatory
    public String userProfile() {
        if (checkProcessingLocked()) {
            handleMessage(FacesMessage.SEVERITY_ERROR, JSFUtil.getMessage(DataModelEnum.PROCESSINGLOCKED_BLOCKS_ACTION.getValue()));
        } else if(selectedUser != null) {
            return DataModelEnum.USERS_PROFILE_PATH.getValue();
        }
        return DataModelEnum.USERS_PATH.getValue();
    }

    /**
     * Redirects to user roles page
     * when it invoke
     * @return users HTML page
     */
    public String userRoles() {

        if (checkProcessingLocked()) {
            handleMessage(FacesMessage.SEVERITY_ERROR, JSFUtil.getMessage(DataModelEnum.PROCESSINGLOCKED_BLOCKS_ACTION.getValue()));
        } else if (selectedUser != null) {
            return DataModelEnum.USERS_ROLES_PATH.getValue();

        }
        return DataModelEnum.USERS_PATH.getValue();
    }

    /**
     * Redirects to user unAssign page
     * when it invoke
     * @return users HTML page
     */
    public String userUnAssign() {
        if (checkProcessingLocked()) {
            handleMessage(FacesMessage.SEVERITY_ERROR, JSFUtil.getMessage(DataModelEnum.PROCESSINGLOCKED_BLOCKS_ACTION.getValue()));
        } else if (selectedUser != null) {
            return DataModelEnum.USER_UN_ASSIGN_PATH.getValue();
        }
        return DataModelEnum.USERS_PATH.getValue();
    }

    /**
     * Redirects user to the home page
     * @return a new HTML page
     */
    public String returnToDataTableRecords() {
        user = new User();
        selectedUser = null;
        return DataModelEnum.USERS_PATH.getValue();
    }

    /**
     * Stores the information selected by the current user to be used for later
     * @param event that will contain which user has been selected
     */
    public void onRowSelect(SelectEvent<SystemUser> event) {
        this.selectedUser=event.getObject();
        FacesMessage msg = new FacesMessage(JSFUtil.getMessage(DataModelEnum.USERS_SELECTED_MESSAGE.getValue()),
                String.valueOf(selectedUser.getLogon()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }


    /**
     * Check if the tenant association process is allowed to be started from
     * the following perspective:
     * <ul>
     *     <li>Current logged user has the right permission (ex: to associate an user to one tenant)</li>
     *     <li>There is a user available to be associated with a Tenant (i.e a newly created user
     *     or a previously selected one from the data grid)</li>
     * </ul>
     * @return true if the process can be handled/started, false otherwise
     */
    @ActiveTenantMandatory
    public boolean isTenantAssociationProcessAllowed() {
        try {
            if (!allowedAssociateTenantAndUser) {
                return false;
            }
            this.userForTenantAssociation = findUserToAssociate();
            return this.userForTenantAssociation != null;
        }
        catch(Exception e) {
            this.handleError(e, JSFUtil.getMessage(DataModelEnum.TR_ERROR_RETRIEVING_USER.getValue()));
            return false;
        }
    }

    /**
     * Find the user that may participate on the tenant association process
     * @return SystemUser instance, or null in case of not found any user
     * @throws SystemException in case of any error during the attempt to retrieve SystemUser
     */
    @ActiveTenantMandatory
    protected SystemUser findUserToAssociate() throws SystemException{
        // Corresponds to the user picked from the data grid (user selected to be update, etc)
        if (this.selectedUser != null && this.selectedUser.getId() != null) {
            return this.selectedUser;
        }
        // Corresponds to the user recently created
        if (this.user != null && !StringUtils.isEmpty(this.user.getLogon())) {
            // Retrieve a SystemUser using logon as parameter
            return service.getUserByLogon(this.user.getLogon()).orElse(null);
        }
        return null;
    }

    /**
     * This method do some preparing process (Pre-Processing) before redirecting
     * to the Tenant association screen
     * @return url mapping that refers Tenant association screen
     */
    public String prepareTenantAssociation() {
        if (checkProcessingLocked()) {
            handleMessage(FacesMessage.SEVERITY_ERROR, JSFUtil.getMessage(DataModelEnum.PROCESSINGLOCKED_BLOCKS_ACTION.getValue()));
            JSFUtil.addErrorMessage(DataModelEnum.PROCESSINGLOCKED_BLOCKS_ACTION.getValue());
            return DataModelEnum.USER_PATH.getValue();
        }  else {
            return DataModelEnum.USER_ASSIGNING_TENANT_ASSOCIATION_PATH.getValue();
        }
    }

    public boolean isAllowedReadUser() {
        return allowedReadUser;
    }

    public void setAllowedReadUser(boolean allowedReadUser) {
        this.allowedReadUser = allowedReadUser;
    }

    public boolean isAllowedUpdateUser() {
        return allowedUpdateUser;
    }

    public void setAllowedUpdateUser(boolean allowedUpdateUser) {
        this.allowedUpdateUser = allowedUpdateUser;
    }

    public boolean isAllowedDeleteUser() {
        return allowedDeleteUser;
    }

    public void setAllowedDeleteUser(boolean allowedDeleteUser) {
        this.allowedDeleteUser = allowedDeleteUser;
    }

    public boolean isAllowedCreateUser() {
        return allowedCreateUser;
    }

    public void setAllowedCreateUser(boolean allowedCreateUser) {
        this.allowedCreateUser = allowedCreateUser;
    }

    /**
     * Flag indicating if current user has permission to associate another user to one tenant
     * @return true if such condition is affirmative, false otherwise
     */
    public boolean isAllowedAssociateTenantAndUser() { return allowedAssociateTenantAndUser; }

    /**
     * Sets a flag indicating if the current user has permission to associate another user to one tenant
     * @param allowedAssociateTenantAndUser boolean value to be set
     */
    public void setAllowedAssociateTenantAndUser(boolean allowedAssociateTenantAndUser) {
        this.allowedAssociateTenantAndUser = allowedAssociateTenantAndUser;
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

    /**
     * Redirects to user userEmailUpdate
     * page when it invokes
     * @return users page
     */
    public String userEmailUpdate() {
        if (selectedUser != null) {
            return DataModelEnum.USER_EMAIL_UPDATE.getValue();
        }
        return DataModelEnum.USERS_PATH.getValue();
    }

    /**
     * Updates email and sends verification email for a user
     */
    public String updateUserEmailAndExecuteActionEmailVerify(){
        try{
            if(selectedUser.getId() != null && updateEmail.getUserEmail() != null){
                service.updateEmailAndExecuteActionEmailVerify(selectedUser.getId(), updateEmail, true);
                handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage(DataModelEnum.SENT_UPDATED_EMAIL_VERIFY_SUCCESS.getValue()),
                        JSFUtil.getMessage(DataModelEnum.USER_MESSAGE.getValue()));
                onload();
                returnToDataTableRecords();
            }
        }catch (Exception e){
            handleError(e, JSFUtil.getMessage(DataModelEnum.SENT_UPDATED_EMAIL_VERIFY_ERROR.getValue()), JSFUtil.getMessage(DataModelEnum.USER_MESSAGE.getValue()));
        }
        return DataModelEnum.USERS_PATH.getValue();
    }

    /**
     * Getter method for the flag property that indicates if the current user
     * has grant to update email
     * @return true for positive case, otherwise false
     */
    public boolean isAllowedToUpdateUserEmail() {
        return allowedToUpdateUserEmail;
    }

    /**
     * Setter method for the flag property that indicates if the current user has grant
     * to reset a update email or not
     * @param allowedToUpdateUserEmail boolean value that if express the user has permission or not to reset password
     */
    public void setAllowedToUpdateUserEmail(boolean allowedToUpdateUserEmail) {
        this.allowedToUpdateUserEmail = allowedToUpdateUserEmail;
    }

    /**
     * Getter method for the user which has email attribute
     * @return User containing update email info
     */
    public SystemUser getUpdateEmail() {
        return updateEmail;
    }

    /**
     * Setter method for the user which has only emailed attribute
     * @param updateEmail info
     */
    public void setUpdateEmail(SystemUser updateEmail) {
        this.updateEmail = updateEmail;
    }

    public boolean checkProcessingLocked() {
        return (selectedUser != null && selectedUser.isProcessingLocked() || selectedUser == null && user.isProcessingLocked());
    }

}
