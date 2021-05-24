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
package io.radien.webapp.linkedAuthorization;

import io.radien.api.model.linked.authorization.SystemLinkedAuthorization;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.linked.authorization.LinkedAuthorizationRESTServiceAccess;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.api.service.user.UserRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.security.UserSession;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Data Model to validate if and which the active user has selected in the active tenant
 *
 * @author Bruno Gama
 */
@Model
@SessionScoped
public class ActiveTenantDataModel extends AbstractManager implements Serializable {

    @Inject
    private LinkedAuthorizationRESTServiceAccess service;

    @Inject
    private TenantRESTServiceAccess tenantService;

    @Inject
    private UserRESTServiceAccess userService;

    @Inject
    private UserSession userSession;

    private SystemUser activeUserSession;

    private String activeTenant;

    private List<? extends SystemLinkedAuthorization> listOfLinkedAuthorizations;

    private HashMap<String, Long> tenantInformation = new HashMap<String, Long>();

    private List<String> listOfPossibleActiveTenants;

    @PostConstruct
    public void init() {
        try {
            userService.getUserBySub(userSession.getUserIdSubject()).ifPresent(value -> activeUserSession = value);

            if(activeUserSession.getActiveTenant() != null) {
                Optional<SystemTenant> tenant = tenantService.getTenantById(activeUserSession.getActiveTenant());
                if(tenant.isPresent()) {
                    if(service.checkIfLinkedAuthorizationExists(tenant.get().getId(),
                            null, null, activeUserSession.getId())) {
                        activeTenant = tenant.get().getName();
                    } else {
                        activeTenant = null;
                    }
                }
            } else {
                activeTenant = null;
            }
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_generic_error_message"));
        }
    }

    public List<String> getUserTenants() throws SystemException, IOException {
        try {
        List<? extends SystemLinkedAuthorization> tempList = service.getSpecificAssociationByUserId(activeUserSession.getId());

        if(tempList == null || listOfLinkedAuthorizations == null || tempList.size() != listOfLinkedAuthorizations.size()
                || !listEquals(tempList, listOfLinkedAuthorizations)) {
            listOfPossibleActiveTenants = new ArrayList<>();
            listOfLinkedAuthorizations = tempList;

            for (SystemLinkedAuthorization sl : listOfLinkedAuthorizations) {
                Optional<SystemTenant> tenant = tenantService.getTenantById(sl.getTenantId());
                if (tenant.isPresent()) {
                    tenantInformation.put(tenant.get().getName(), tenant.get().getId());
                }
            }

            Iterator it = tenantInformation.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                listOfPossibleActiveTenants.add((String) pair.getKey());
            }

            listOfPossibleActiveTenants.sort(Comparator.comparing(String::toString));
        }
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_generic_error_message"));
        }

        return listOfPossibleActiveTenants;
    }

    private static boolean listEquals(List<? extends SystemLinkedAuthorization> l1, List<? extends SystemLinkedAuthorization> l2) {
        // make a copy of the list so the original list is not changed, and remove() is supported
        List<SystemLinkedAuthorization> cp = new ArrayList<>();
        cp.addAll(l1);
        return cp.removeAll(l2);
    }

    /**
     * Listener to when the user decides to change the active tenant selection
     * @param e action performed and active tenant selected
     */
    public void tenantChanged(ValueChangeEvent e) {
        try {
            if(e.getNewValue().toString().equals(JSFUtil.getMessage("rd_no_active_tenant"))) {
                activeTenant = null;
                activeUserSession.setActiveTenant(null);
                userService.updateUser(activeUserSession);
                redirectToHomePage();
                handleMessage(FacesMessage.SEVERITY_WARN, JSFUtil.getMessage("rd_active_tenant_changed_value_to_null"));
            } else {
                activeTenant = e.getNewValue().toString();
                activeUserSession.setActiveTenant(tenantInformation.get(activeTenant));
                userService.updateUser(activeUserSession);
                redirectToHomePage();
                handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage("rd_active_tenant_changed_value"), activeTenant);
            }

        } catch (Exception exception) {
            handleError(exception, JSFUtil.getMessage("rd_generic_error_message"));
        }
    }

    /**
     * Redirects the user to the home page
     * @throws IOException in case of any issue while redirecting the user in the url or in the user session
     */
    private void redirectToHomePage() throws IOException {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        context.redirect(context.getRequestContextPath() + "/public/index");
    }

    public LinkedAuthorizationRESTServiceAccess getService() {
        return service;
    }

    public void setService(LinkedAuthorizationRESTServiceAccess service) {
        this.service = service;
    }

    public TenantRESTServiceAccess getTenantService() {
        return tenantService;
    }

    public void setTenantService(TenantRESTServiceAccess tenantService) {
        this.tenantService = tenantService;
    }

    public UserRESTServiceAccess getUserService() {
        return userService;
    }

    public void setUserService(UserRESTServiceAccess userService) {
        this.userService = userService;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public SystemUser getActiveUserSession() {
        return activeUserSession;
    }

    public void setActiveUserSession(SystemUser activeUserSession) {
        this.activeUserSession = activeUserSession;
    }

    public List<? extends SystemLinkedAuthorization> getListOfLinkedAuthorizations() {
        return listOfLinkedAuthorizations;
    }

    public void setListOfLinkedAuthorizations(List<? extends SystemLinkedAuthorization> listOfLinkedAuthorizations) {
        this.listOfLinkedAuthorizations = listOfLinkedAuthorizations;
    }

    public HashMap<String, Long> getTenantInformation() {
        return tenantInformation;
    }

    public void setTenantInformation(HashMap<String, Long> tenantInformation) {
        this.tenantInformation = tenantInformation;
    }

    public String getActiveTenant() {
        return activeTenant;
    }

    public void setActiveTenant(String activeTenant) {
        this.activeTenant = activeTenant;
    }
}
