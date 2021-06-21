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
import io.radien.api.service.linked.authorization.LinkedAuthorizationRESTServiceAccess;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.api.service.user.UserRESTServiceAccess;

import io.radien.exception.SystemException;

import io.radien.ms.permissionmanagement.client.entities.Permission;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.security.UserSession;
import org.primefaces.event.SelectEvent;

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
    private LinkedAuthorizationRESTServiceAccess linkedAuthorizationRESTServiceAccess;

    private List<? extends SystemTenant> assignedTenants;

    private static final String URL_MAPPING_ID_LOGGED_USER_PROFILE = "pretty:profile";
    private static final String URL_MAPPING_ID_HOME = "pretty:index";

    private SystemTenant selectedTenantToUnAssign;
    private Long tabIndex = 0L;

    /**
     * This method initializes and constructs
     * user to edit profile
     * occurs and shows corresponding handle message
     */
    @PostConstruct
    public void init() {
        this.assignedTenants = retrieveAssignedTenants();
    }

    /**
     * Redirects user to the home page revert changes if
     * logged in user profile edited
     * @return Returns to the HTML(home) page
     */
    public String redirectToHomePage() throws SystemException {
        return URL_MAPPING_ID_HOME;
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
    protected List<? extends SystemTenant> retrieveAssignedTenants() {
        List<SystemTenant> assignedOnes = new ArrayList<>();
        try {
            // Retrieve tenant ids
            List<Long> tenantIds = linkedAuthorizationRESTServiceAccess.
                    getSpecificAssociationByUserId(userSession.getUserId()).
                    stream().map(SystemLinkedAuthorization::getTenantId).distinct().collect(Collectors.toList());
            // Retrieve tenant
            for (Long tId: tenantIds) {
                tenantRESTServiceAccess.getTenantById(tId).ifPresent(i -> assignedOnes.add(i));
            }
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
    public List<? extends SystemTenant> getAssignedTenants() {
        return assignedTenants;
    }

    /**
     * Setter for the property that corresponds to the tenants assigned/associated
     * with the current logged user
     * @param list list containing the tenants assigned to the current user
     */
    public void setAssignedTenants(List<? extends SystemTenant> list) {
        this.assignedTenants = list;
    }

    /**
     * Perform Tenant dissociation. In other words, dissociate
     * the current logged user from a Tenant
     */
    public String dissociateUserTenant() {
        try {
            this.tabIndex = 1L;
            if (selectedTenantToUnAssign == null || selectedTenantToUnAssign.getId() == null) {
                throw new Exception(JSFUtil.getMessage("rd_tenant_not_selected"));
            }
            // TODO: Call backend method to delete tenant associations (once its approved and merged)
            selectedTenantToUnAssign = null;
            handleMessage(FacesMessage.SEVERITY_INFO,
                    JSFUtil.getMessage("rd_tenant_user_dissociation_success"));
        }
        catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_tenant_user_dissociation_error"));
            return URL_MAPPING_ID_LOGGED_USER_PROFILE;
        }
        return URL_MAPPING_ID_HOME;
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
}
