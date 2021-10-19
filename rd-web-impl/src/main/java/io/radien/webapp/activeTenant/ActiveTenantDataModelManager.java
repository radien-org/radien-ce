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
package io.radien.webapp.activeTenant;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import com.ocpsoft.pretty.PrettyContext;

import io.radien.api.model.tenant.SystemActiveTenant;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.tenant.ActiveTenantRESTServiceAccess;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.tenantmanagement.client.entities.ActiveTenant;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.DataModelEnum;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.security.UserSession;

/**
 * Active Tenant Model Manager controller and web page controller
 * @author Bruno Gama
 **/
@Model
@RequestScoped
public class ActiveTenantDataModelManager extends AbstractManager implements Serializable {
    private static final long serialVersionUID = -7725350210007205007L;

    @Inject
    private UserSession userSession;

    @Inject
    private ActiveTenantRESTServiceAccess activeTenantRESTServiceAccess;
    
    @Inject
    private TenantRoleRESTServiceAccess tenantRoleRESTServiceAccess;
    
    @Inject
    private TenantRESTServiceAccess tenantRESTServiceAccess;
    
    private List<? extends SystemTenant> userAvailableTenants;

    private SystemActiveTenant activeTenant;
    
    private String activeTenantTenantId;
    private String activeTenantTenantName;

    /**
     * Constructor and validator method to see which tenants are activated for the current active user
     * @throws SystemException in case of token expiration or any other exception
     */
    @PostConstruct
    public void init() throws SystemException {
        try {
            //get all the active tenants
            List<? extends SystemActiveTenant> userActiveTenants = activeTenantRESTServiceAccess.getActiveTenantByFilter(userSession.getUser().getId(), null);
            
            //choose the already selected active tenant
            if(!userActiveTenants.isEmpty()){
                SystemActiveTenant actTenant = userActiveTenants.get(0);
                activeTenant = actTenant;
                activeTenantTenantId = actTenant.getTenantId().toString();
                Optional<SystemTenant> nameValue = tenantRESTServiceAccess.getTenantById(actTenant.getTenantId());
                if(nameValue.isPresent()) {
                    activeTenantTenantName = nameValue.get().getName();
                }
                if(userActiveTenants.size() > 1) {
                    log.error("User has more than 1 active tenant!");
                }
            }
            userAvailableTenants = getUserTenants();
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.GENERIC_ERROR_MESSAGE.getValue()));
        }
    }

    /**
     * Method to convert the objects and show only the tenant names to the user
     * @return a list of active tenant names that are able to be chosen by the user
     * @throws SystemException 
     */
    public List<? extends SystemTenant> getUserTenants() throws SystemException {
        SystemUser user = userSession.getUser();
        Long userId = user.getId();
        return tenantRoleRESTServiceAccess.getTenants(userId, null);
    }

    /**
     * If a new tenant is selected then we are going to deactivate the previous one and activate the new tenant for
     * the current user
     * @param e the selected value properties
     */
    public void tenantChanged(ValueChangeEvent e) {
        tenantChangedValidationMethod(e.getNewValue().toString());
    }

    /**
     * Method to validate if a new tenant is selected then we are going to deactivate the previous one and activate the new tenant for
     * @param valueChange the selected value properties
     */
    public void tenantChangedValidationMethod(String valueChange) {
        try {
            if(valueChange.equals(JSFUtil.getMessage(DataModelEnum.NO_ACTIVE_TENANT_MESSAGE.getValue())) || valueChange.equals("???"+JSFUtil.getMessage(DataModelEnum.NO_ACTIVE_TENANT_MESSAGE.getValue())+"???")) {
                if(activeTenant != null) {
                    //if he had another then set the value to deactivate before changing
                    deactivateTenant(activeTenant);
                    redirectToHomePage();
                }
                activeTenant = null;
                activeTenantTenantId = null;
                activeTenantTenantName = null;
            } else {
                if(activeTenant != null && Long.parseLong(valueChange) == activeTenant.getTenantId()) {
                    return;
                }
                //if there was null
                if(activeTenant != null) {
                    //if user had another tenant active then we need to deactivate it first
                    deactivateTenant(activeTenant);
                }
                //then we check if it still exists the association for the user if so then...
                validateCorrectTenantAndActivateItToUser(valueChange);
                //check if we are at users listing screen
                if (isOnUsersListingScreen()) {
                    redirectToPage(DataModelEnum.USERS_DISPATCH_PATH.getValue());
                } else {
                    redirectToHomePage();
                }
                handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage(DataModelEnum.ACTIVE_TENANT_CHANGED_VALUE.getValue()), activeTenantTenantName);
            }
        } catch (Exception exception) {
            handleError(exception, JSFUtil.getMessage(DataModelEnum.GENERIC_ERROR_MESSAGE.getValue()));
        }
    }

    /**
     * Before redirect to home page, this method allows to know if the current view
     * corresponds to the users listing screen.
     * If does, redirection to HOME page should not happen, the navigation should
     * be redirect to the USERS LISTING page.
     *
     * @return true if view corresponds to the users listing screen, otherwise false
     */
    protected boolean isOnUsersListingScreen() {
        String viewId = "pretty:" + PrettyContext.getCurrentInstance().getCurrentMapping().getId();
        return viewId.equals(DataModelEnum.USERS_PATH.getValue());
    }

    /**
     * We will get the selected tenant and activate it for the user to use
     * @param valueChange the selected value properties
     * @throws SystemException in case of token expiration or any other exception
     */
    private void validateCorrectTenantAndActivateItToUser(String valueChange) throws SystemException {
        if(userAvailableTenants != null) {
            for(SystemTenant st : userAvailableTenants) {
                if(st.getId().toString().equals(valueChange)) {
                    //activate the tenant
                    SystemActiveTenant selectedActiveTenant = activateTenant(st);
                    activeTenant = selectedActiveTenant;
                    activeTenantTenantId = st.getId().toString();
                    activeTenantTenantName = st.getName();
                    break;
                }
            }
        }
    }

    /**
     * Method to deactivate the given active tenant
     * @param tenantToDeactivate active tenant to be deactivated
     * @throws SystemException in case of token expiration or any other exception
     */
    private void deactivateTenant(SystemActiveTenant tenantToDeactivate) throws SystemException {
        activeTenantRESTServiceAccess.delete(tenantToDeactivate.getId());
        activeTenant = null;
        activeTenantTenantId = null;
        activeTenantTenantName = null;
    }

    /**
     * Method to activate the given active tenant
     * @param tenantToActivate active tenant to be activated to the current user
     * @return the active tenant for further purposes
     * @throws SystemException in case of token expiration or any other exception
     */
    private SystemActiveTenant activateTenant(SystemTenant tenantToActivate) throws SystemException {
        SystemActiveTenant newActiveTenant = new ActiveTenant();
        newActiveTenant.setTenantId(tenantToActivate.getId());
        newActiveTenant.setUserId(userSession.getUserId());
        activeTenantRESTServiceAccess.create(newActiveTenant);
        return newActiveTenant;
    }

    /**
     * Validates if the requested user is active or not
     * @return true in case the user is indeed with the tenant active
     */
    public boolean isTenantActive() {
        return activeTenant != null;
    }

    /**
     * User session getter
     * @return the user in session
     */
    public UserSession getUserSession() {
        return userSession;
    }

    /**
     * User session setter
     * @param userSession to be set
     */
    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    /**
     * Active tenant object getter
     * @return the current active tenant for the user
     */
    public SystemActiveTenant getActiveTenant() {
        return activeTenant;
    }

    /**
     * Active tenant object setter
     * @param activeTenant to be set
     */
    public void setActiveTenant(SystemActiveTenant activeTenant) {
        this.activeTenant = activeTenant;
    }

    /**
     * Active tenant name getter
     * @return the active tenant name
     */
    public String getActiveTenantValue() {
        return activeTenantTenantId;
    }

    /**
     * active tenant name setter
     * @param activeTenantValue to be set
     */
    public void setActiveTenantValue(String activeTenantValue) {
        this.activeTenantTenantId = activeTenantValue;
    }

    /**
     * Active Tenant REST Service Access getter
     * @return the request and response for the active tenant rest service access
     */
    public ActiveTenantRESTServiceAccess getActiveTenantRESTServiceAccess() {
        return activeTenantRESTServiceAccess;
    }

    /**
     * Active Tenant REST Service Access setter
     * @param activeTenantRESTServiceAccess to be set
     */
    public void setActiveTenantRESTServiceAccess(ActiveTenantRESTServiceAccess activeTenantRESTServiceAccess) {
        this.activeTenantRESTServiceAccess = activeTenantRESTServiceAccess;
    }

    /**
     * Active Tenant list for the user getter
     * @return a list of all the possible active tenants for the user
     */
    public List<? extends SystemTenant> getUserAvailableTenants() {
        return userAvailableTenants;
    }

    /**
     * Active Tenant list for the user setter
     * @param userActiveTenants to be set
     */
    public void setUserActiveTenants(List<? extends SystemTenant> userActiveTenants) {
        this.userAvailableTenants = userActiveTenants;
    }
}
