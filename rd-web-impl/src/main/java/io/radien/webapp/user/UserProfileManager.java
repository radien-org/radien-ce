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

import io.radien.api.SystemVariables;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.ticket.SystemTicket;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.notification.email.EmailNotificationRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleUserRESTServiceAccess;
import io.radien.api.service.ticket.TicketRESTServiceAccess;
import io.radien.api.service.user.UserRESTServiceAccess;
import io.radien.exception.ProcessingException;
import io.radien.exception.SystemException;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.ticketmanagement.client.entities.TicketType;
import io.radien.ms.ticketmanagement.client.services.TicketFactory;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.entities.UserPasswordChanging;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.DataModelEnum;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.security.UserSession;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.event.ComponentSystemEvent;
import javax.inject.Inject;

import static io.radien.exception.GenericErrorCodeMessage.ERROR_INVALID_CREDENTIALS;

/**
 * Class Responsible to update/save logged in user profile
 *
 * @author Rajesh Gavvala
 * @author Newton Carvalho
 */
@Model
@SessionScoped
public class UserProfileManager extends AbstractManager {

    private static final long serialVersionUID = 3238655265543535112L;

    @Inject
    private UserRESTServiceAccess userService;

    @Inject
    private UserSession userSession;

    @Inject
    private EmailNotificationRESTServiceAccess mailService;

    @Inject
    private TicketRESTServiceAccess ticketService;

    @Inject
    private TenantRoleUserRESTServiceAccess tenantRoleUserRESTServiceAccess;

    private List<SystemTenant> assignedTenants;

    private SystemTenant selectedTenantToUnAssign;
    private Long tabIndex = 0L;

    private SystemUser clonedLogInUser;

    private UserPasswordChanging changing = new UserPasswordChanging();

    private boolean emailControlEnabled = false;
    private String confirmationInfo;

    /**
     * This method initializes and constructs
     * user to edit profile
     * occurs and shows corresponding handle message
     */
    @PostConstruct
    public void init() {
        this.assignedTenants = retrieveAssignedTenants();
        try{
            if(userSession.isActive()){
                clonedLogInUser = new User((User) userSession.getUser());
            }
        } catch (Exception e){
            handleError(e, JSFUtil.getMessage("rd_retrieving_logged_user_error"));
        }
        setEmailControlEnabled(false);
    }

    /**
     * Gets the property of user from clone
     * login user
     * @return cloned SystemUser object
     */
    public SystemUser getClonedLogInUser() {
        return clonedLogInUser;
    }

    /**
     * Setter for the SystemUser cloned object
     * @param clonedLogInUser cloned object of login user
     */
    public void setClonedLogInUser(SystemUser clonedLogInUser) {
        this.clonedLogInUser = clonedLogInUser;
    }

    /**
     * Method updateProfile save/update of the logged in user
     * profile information that have been edited
     * @param updateUserProfile user profile updated information
     * to be saved
     * @return home HTML page if success otherwise user profile
     */


    public String updateProfile(SystemUser updateUserProfile, boolean checkForProcessingLocked) {
        if (!checkForProcessingLocked || !clonedLogInUser.isProcessingLocked())
        {
            try{
                if(updateUserProfile != null && updateUserProfile.getId() != null) {
                    sendEmailModificationConfirmation();
                    updateUserProfile.setUserEmail(userSession.getUser().getUserEmail());
                    boolean isUpdated = userService.updateUser(updateUserProfile);

                    if(isUpdated){
                        handleMessage(
                                FacesMessage.SEVERITY_INFO,
                                JSFUtil.getMessage("rd_user_profile_save_success"),
                                JSFUtil.getMessage("rd_user"),
                                updateUserProfile.getLogon());

                        userSession.setUser(updateUserProfile);
                    }
                }
            } catch(Exception e) {
                handleError(e,
                        JSFUtil.getMessage("rd_user_profile_save_error"),
                        JSFUtil.getMessage("rd_user"),
                        updateUserProfile.getLogon());

                return DataModelEnum.PRETTY_PROFILE.getValue();
            }
            return DataModelEnum.PRETTY_INDEX.getValue();
        } else {
            JSFUtil.addErrorMessage(DataModelEnum.PROCESSINGLOCKED_BLOCKS_ACTION.getValue());
            return DataModelEnum.PRETTY_PROFILE.getValue();
        }
    }

    /**
     * Listener method used by bootsfaces DataTable component to select
     * one Tenant
     * @param tenant Selected tenant on DataTable
     * @param typeOfSelection one of those predicted types on DataTable.
     *                        This is either row, column or item
     * @param indexes tells the JSF bean which rows, columns or cells have been selected.
     *     Note that in the first two cases this is either an individual number or - if multiple items have been selected - a
     *     comma separated list
     */
    public void onTenantSelect(Tenant tenant, String typeOfSelection, String indexes) {
        if (typeOfSelection.equals("row") && tenant != null) {
            this.selectedTenantToUnAssign = tenant;
        }
    }

    /**
     * Retrieve the tenants assigned to / associated with the current logged user
     * @return List containing instances of SystemTenant
     */
    protected List<SystemTenant> retrieveAssignedTenants() {
        List<SystemTenant> assignedOnes = new ArrayList<>();
        try {
            assignedOnes.addAll(tenantRoleUserRESTServiceAccess.getTenants(userSession.getUserId(), null));
            this.tabIndex = 1L;
        }
        catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_retrieve_error"),
                    JSFUtil.getMessage("rd_roles"));
        }
        return assignedOnes;
    }

    /**
     * Getter for the property that corresponds to the tenants assigned/associated
     * with the current logged user
     * @return list containing tenants
     */
    public List<SystemTenant> getAssignedTenants() {
        return assignedTenants;
    }

    /**
     * Setter for the property that corresponds to the tenants assigned/associated
     * with the current logged user
     * @param list list containing the tenants assigned to the current user
     */
    public void setAssignedTenants(List<SystemTenant> list) {
        this.assignedTenants = list;
    }

    /**
     * Perform Tenant dissociation. In other words, dissociate
     * the current logged user from a Tenant
     */
    public String dissociateUserTenant() {
        if (!clonedLogInUser.isProcessingLocked()){
            try {
                if (selectedTenantToUnAssign == null || selectedTenantToUnAssign.getId() == null) {
                    throw new ProcessingException(JSFUtil.getMessage("rd_tenant_not_selected"));
                }
                // Invoking backend method to delete tenant associations (once its approved and merged)
                tenantRoleUserRESTServiceAccess.unAssignUser(selectedTenantToUnAssign.getId(), null,
                        userSession.getUserId());
                selectedTenantToUnAssign = null;
                this.assignedTenants = retrieveAssignedTenants();
                setTabIndex(0L);
                handleMessage(FacesMessage.SEVERITY_INFO,
                        JSFUtil.getMessage("rd_tenant_user_dissociation_success"));
            }
            catch (Exception e) {
                setTabIndex(1L);
                handleError(e, JSFUtil.getMessage("rd_tenant_user_dissociation_error"));
                return DataModelEnum.PRETTY_PROFILE.getValue();
            }
        } else {
            JSFUtil.addErrorMessage(DataModelEnum.PROCESSINGLOCKED_BLOCKS_ACTION.getValue());
            return DataModelEnum.PRETTY_PROFILE.getValue();
        }
        return DataModelEnum.PRETTY_INDEX.getValue();
    }

    public void sendEmailModificationConfirmation(){
        setEmailControlEnabled(false);
        String currentEmail = userSession.getUser().getUserEmail();
        String newEmail = clonedLogInUser.getUserEmail();
        if(newEmail.equalsIgnoreCase(currentEmail)){
            return;
        }
        try {
            String url = JSFUtil.getBaseUrl().orElse("#").concat("/confirmData?ticket=").concat(createTicket(TicketType.EMAIL_CHANGE, newEmail));
            Map<String, String> args = new HashMap<>();
            args.put("currentEmail", currentEmail);
            args.put("newEmail", newEmail);
            args.put("confirmationURL", url);
            mailService.notify(newEmail, "email-3", userSession.getLanguage(), args);
        } catch (Exception e) {
            log.error("An exception has occurred when attempting to send an email change request. Stack trace: {}", e.toString());
        }
    }

    public void sendDataRequestOptIn(){
        try {
            String ticketUuid = createDataRequestTicket();

            String referenceUrl = MessageFormat.format("{0}/confirmData?ticket={1}", JSFUtil.getBaseUrl().orElse("#"), ticketUuid);
            String emailViewId = "email-7";
            Map<String, String> argumentsMap = new HashMap<>();
            argumentsMap.put("firstName", clonedLogInUser.getFirstname());
            argumentsMap.put("lastName", clonedLogInUser.getLastname());
            argumentsMap.put("portalUrl", "radien");
            argumentsMap.put("targetUrl", referenceUrl);

            mailService.notify(clonedLogInUser.getUserEmail(), emailViewId, userSession.getLanguage(), argumentsMap);
        } catch (SystemException e) {
            log.error("A exception occured while trying to request data for the user {}. The exception message is: {}", userSession.getUserId(), e.getMessage());
            handleMessage(FacesMessage.SEVERITY_ERROR,
                    JSFUtil.getMessage("request_data_error"));
        }

    }

    private String createDataRequestTicket() throws SystemException {
        return createTicket(TicketType.GDPR_DATA_REQUEST, "");
    }

    private String createTicket(TicketType type, String data) throws SystemException{
        UUID uuid = UUID.randomUUID();
        SystemTicket ticket = TicketFactory.create(clonedLogInUser.getId(), uuid.toString(), type.getId(), data, clonedLogInUser.getId());
        ticketService.create(ticket);
        return uuid.toString();
    }

    /**
     * Getter for the property that corresponds to the Tenant selected for
     * doing the dissociation
     * @return Permission for which will be performed the dissociation
     */
    public SystemTenant getSelectedTenantToUnAssign() {
        return selectedTenantToUnAssign;
    }

    /**
     * Setter for the property that corresponds to the Tenant selected for
     * doing the dissociation
     * @param selectedTenantToUnAssign Tenant for which will be performed the dissociation
     */
    public void setSelectedTenantToUnAssign(SystemTenant selectedTenantToUnAssign) {
        this.selectedTenantToUnAssign = selectedTenantToUnAssign;
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
     * Return the JSF URL mapping that refers the Logged User Profile GUI/Screen
     * @return String value that corresponds to an JSF mapping URL
     */
    public String getLoggerUserGui() {
        return DataModelEnum.PRETTY_PROFILE.getValue();
    }

    /**
     * Return the JSF URL mapping that refers the index home screen/area
     * @return String value that corresponds to an JSF mapping URL
     */
    public String getHomeGui() {
        return DataModelEnum.PRETTY_INDEX.getValue();
    }

    /**
     * Getter method for the {@link UserProfileManager#changing} attribute
     * @return instance of {@link UserPasswordChanging}
     */
    public UserPasswordChanging getChanging() { return changing; }

    /**
     * Setter method for the {@link UserProfileManager#changing} attribute
     * @param changing instance of {@link UserPasswordChanging}
     */
    public void setChanging(UserPasswordChanging changing) { this.changing = changing; }

    /**
     * Getter method for the {@link UserProfileManager#confirmationInfo} attribute
     * @return String value for confirmationInfo
     */
    public String getConfirmationInfo() {
        return confirmationInfo;
    }

    /**
     * Setter method for the {@link UserProfileManager#confirmationInfo} attribute
     * @param confirmationInfo String value for confirmationInfo
     */
    public void setConfirmationInfo(String confirmationInfo) {
        this.confirmationInfo = confirmationInfo;
    }

    /**
     * Getter method used by the frontend to verify whether the email field is 'locked'.
     * @return boolean value for emailControlEnabled
     */
    public boolean isEmailControlEnabled(){
        return emailControlEnabled;
    }

    /**
     * Setter method used to declare whether the email field in the frontend is open for editing.
     * @param enabled boolean value for emailControlEnabled
     */
    public void setEmailControlEnabled(boolean enabled){
        emailControlEnabled = enabled;
    }

    /**
     * Toggles whether the email field in the frontend is open for editing.
     */
    public void emailControlToggle(){
        emailControlEnabled = !emailControlEnabled;
    }

    /**
     * Listener that invokes REST API responsible to change the the password
     */
    public void changePasswordListener() {
        try {
            String username = this.userSession.getUser().getLogon();
            String sub = this.userSession.getUserIdSubject();
            this.changing.setLogin(username);
            if(!this.changing.validatePassword()) {
                for(String error : this.changing.getValidationErrors()) {
                    JSFUtil.addErrorMessage(error);
                }
                return;
            }
            userService.updatePassword(sub, this.changing);
            setTabIndex(0L);
            this.changing.clear();
            this.confirmationInfo = "";
            JSFUtil.addSuccessMessage(DataModelEnum.USER_CHANGE_PASSWORD_SUCCESS.getValue());
        }
        catch(Exception e) {
            Map<String, String> map = (Map<String, String>) this.getWrappedErrorDescriptor(e);
            if (map != null) {
                String code = map.get(SystemVariables.GENERIC_ERROR_MESSAGE_CODE.getFieldName());
                if (code != null && code.equals(ERROR_INVALID_CREDENTIALS.getCode())) {
                    JSFUtil.addErrorMessage(DataModelEnum.USER_CHANGE_PASSWORD_ACTUAL_PASSWORD_INVALID.getValue());
                    return;
                }
            }
            JSFUtil.addErrorMessage(DataModelEnum.USER_CHANGE_PASSWORD_UNSUCCESSFUL.getValue());
        }
    }

    public void deleteUserListener() {
        if (!clonedLogInUser.isProcessingLocked())
        {
            Long id = userSession.getUserId();
            try {
                log.info("Starting to delete user {}", id);

                if(userService.deleteUser(id)){
                   userSession.logout();
                   log.info("User {} succesfully deleted", id);

                    log.info("Starting to unassign all tenants from user {}", id);
                    for(SystemTenant tenant: assignedTenants) {
                        tenantRoleUserRESTServiceAccess.unAssignUser(tenant.getId(), null, userSession.getUserId());
                    }
                    log.info("Succesfully unasigned all tenants from user {}", id);

                    handleMessage(
                            FacesMessage.SEVERITY_INFO,
                            JSFUtil.getMessage("rd_user_profile_delete_success"));
               }else {
                    log.info("Failed to delete user: {}", id);
                    handleMessage(FacesMessage.SEVERITY_ERROR,
                            JSFUtil.getMessage("rd_user_profile_delete_error"));
               }
            } catch (Exception e) {
                log.info("A exception occured while trying to delete user: {}", id);
                handleError(e,
                        JSFUtil.getMessage("rd_user_profile_delete_error"));
            }
        } else {
            JSFUtil.addErrorMessage(DataModelEnum.PROCESSINGLOCKED_BLOCKS_ACTION.getValue());
        }
    }

    /**
     * Gets the user from the current session and sets processingLocked for it to true
     * @return site to which the user should be redirect as a string
     */
    public String lockUserProcessing(){
        clonedLogInUser.setProcessingLocked(true);
        String result = updateProfile(clonedLogInUser, false);
        if (!result.equals(DataModelEnum.PRETTY_INDEX.getValue())) {
            clonedLogInUser.setProcessingLocked(false);
        }
        return result;
    }


    /**
     * Listener/Validator to check if password and confirmation password matches
     * @param event validation event
     */
    public void validateComparePasswords(ComponentSystemEvent event) {
        setTabIndex(0L);
        UIComponent components = event.getComponent();
        UIInput uiNewPassword = (UIInput) components.findComponent(SystemVariables.NEW_PASSWORD.getFieldName());
        String pass = uiNewPassword.getValue() == null ? "" : uiNewPassword.getValue().toString();

        UIInput uiConfirmPassword = (UIInput) components.findComponent(SystemVariables.CONFIRM_NEW_PASSWORD.getFieldName());
        String confirm = uiConfirmPassword.getValue() == null ? "" : uiConfirmPassword.getValue().toString();

        if (pass.isEmpty() || confirm.isEmpty()) {
            return;
        }
        if (!pass.equals(confirm)) {
            JSFUtil.addErrorMessage(DataModelEnum.USER_CHANGE_PASSWORD_NO_MATCHES.getValue());
            JSFUtil.getFacesContext().renderResponse();
        }
    }
}
