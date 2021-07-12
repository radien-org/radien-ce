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

import io.radien.api.model.linked.authorization.SystemLinkedAuthorization;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.linked.authorization.LinkedAuthorizationRESTServiceAccess;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleRESTServiceAccess;
import io.radien.api.service.user.UserRESTServiceAccess;

import io.radien.exception.ProcessingException;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.DataModelEnum;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.security.UserSession;

import javax.annotation.PostConstruct;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;

import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private TenantRESTServiceAccess tenantRESTServiceAccess;

    @Inject
    private TenantRoleRESTServiceAccess tenantRoleRESTServiceAccess;

    private List<SystemTenant> assignedTenants;

    private SystemTenant selectedTenantToUnAssign;
    private Long tabIndex = 0L;

    private SystemUser clonedLogInUser;

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
    public String updateProfile(SystemUser updateUserProfile) {
        try{
            if(updateUserProfile != null && updateUserProfile.getId() != null) {
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
            assignedOnes.addAll(tenantRoleRESTServiceAccess.getTenants(userSession.getUserId(), null));
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
        try {
            if (selectedTenantToUnAssign == null || selectedTenantToUnAssign.getId() == null) {
                throw new ProcessingException(JSFUtil.getMessage("rd_tenant_not_selected"));
            }
            // Invoking backend method to delete tenant associations (once its approved and merged)
//            linkedAuthorizationRESTServiceAccess.deleteAssociations(
//                    selectedTenantToUnAssign.getId(), userSession.getUserId());
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
        return DataModelEnum.PRETTY_INDEX.getValue();
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
}
