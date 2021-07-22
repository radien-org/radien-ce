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
package io.radien.webapp.activeTenant;

import com.ocpsoft.pretty.PrettyContext;
import io.radien.api.model.tenant.SystemActiveTenant;
import io.radien.api.service.tenant.ActiveTenantRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.DataModelEnum;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.security.UserSession;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Active Tenant Model Manager controller and web page controller
 * @author Bruno Gama
 **/
@Model
@RequestScoped
public class ActiveTenantDataModelManager extends AbstractManager implements Serializable {

    @Inject
    private UserSession userSession;

    @Inject
    private ActiveTenantRESTServiceAccess activeTenantRESTServiceAccess;

    private List<? extends SystemActiveTenant> userActiveTenants;

    private SystemActiveTenant activeTenant;
    private String activeTenantValue;

    /**
     * Constructor and validator method to see which tenants are activated for the current active user
     * @throws SystemException in case of token expiration or any other exception
     */
    @PostConstruct
    public void init() throws SystemException {
        try {
            //get all the active tenants
            userActiveTenants = activeTenantRESTServiceAccess.getActiveTenantByFilter(userSession.getUser().getId(), null, null, false);

            //choose the already selected active tenant
            for (SystemActiveTenant actTenant : userActiveTenants) {
                if(userActiveTenants.size()==1) {
                    if(!actTenant.getIsTenantActive()) {
                        actTenant.setIsTenantActive(true);
                        activeTenantRESTServiceAccess.update(actTenant);
                    }
                    activeTenant = actTenant;
                    activeTenantValue = actTenant.getTenantName();
                } else if (actTenant.getIsTenantActive()) {
                    activeTenant = actTenant;
                    activeTenantValue = actTenant.getTenantName();
                }
            }
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.GENERIC_ERROR_MESSAGE.getValue()));
        }
    }

    /**
     * Method to convert the objects and show only the tenant names to the user
     * @return a list of active tenant names that are able to be chosen by the user
     */
    public List<String> getUserTenants() {
        List<String> actTenantNames = new ArrayList<>();
        for(SystemActiveTenant actTenant : userActiveTenants) {
            actTenantNames.add(actTenant.getTenantName());
        }

        actTenantNames.sort(Comparator.comparing(String::toString));

        return actTenantNames;
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
            if(valueChange.equals(JSFUtil.getMessage(DataModelEnum.NO_ACTIVE_TENANT_MESSAGE.getValue()))) {
                if(activeTenant != null) {
                    //if he had another then set the value to deactivate before changing
                    deactivateTenant(activeTenant);
                    redirectToHomePage();
                }
                activeTenant = null;
                activeTenantValue=null;
            } else {
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
                handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage(DataModelEnum.ACTIVE_TENANT_CHANGED_VALUE.getValue()), activeTenantValue);
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
        List<? extends SystemActiveTenant> newActiveTenantForUser =
                activeTenantRESTServiceAccess.getActiveTenantByFilter(userSession.getUser().getId(), null, valueChange, false);

        if (!newActiveTenantForUser.isEmpty()) {
            for (SystemActiveTenant systemActiveTenant : newActiveTenantForUser) {
                if (systemActiveTenant.getTenantName().equals(valueChange)) {
                    //activate the tenant
                    SystemActiveTenant selectedActiveTenant = activateTenant(systemActiveTenant);

                    //add it to the user
                    activeTenant = selectedActiveTenant;
                    activeTenantValue = selectedActiveTenant.getTenantName();
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
        tenantToDeactivate.setIsTenantActive(false);
        activeTenantRESTServiceAccess.update(tenantToDeactivate);
    }

    /**
     * Method to activate the given active tenant
     * @param tenantToActivate active tenant to be activated to the current user
     * @return the active tenant for further purposes
     * @throws SystemException in case of token expiration or any other exception
     */
    private SystemActiveTenant activateTenant(SystemActiveTenant tenantToActivate) throws SystemException {
        tenantToActivate.setIsTenantActive(true);
        activeTenantRESTServiceAccess.update(tenantToActivate);

        return tenantToActivate;
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
        return activeTenantValue;
    }

    /**
     * active tenant name setter
     * @param activeTenantValue to be set
     */
    public void setActiveTenantValue(String activeTenantValue) {
        this.activeTenantValue = activeTenantValue;
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
    public List<? extends SystemActiveTenant> getUserActiveTenants() {
        return userActiveTenants;
    }

    /**
     * Active Tenant list for the user setter
     * @param userActiveTenants to be set
     */
    public void setUserActiveTenants(List<? extends SystemActiveTenant> userActiveTenants) {
        this.userActiveTenants = userActiveTenants;
    }
}
