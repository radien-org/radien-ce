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

package io.radien.webapp.tenant;

import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenant.SystemTenantType;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.tenantmanagement.client.entities.TenantType;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.JSFUtil;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * @author Bruno Gama
 */
@Model
@ApplicationScoped
public class TenantDataModel extends AbstractManager implements Serializable {

    @Inject
    private TenantRESTServiceAccess service;

    private LazyDataModel<? extends SystemTenant> lazyModel;

    private SystemTenant selectedTenant;

    private SystemTenant tenant = new Tenant();

    @PostConstruct
    public void init() {
        lazyModel = new LazyTenantDataModel(service);
    }

    public void onload() {
        init();
    }

    public String save(SystemTenant r) {
        try {
            validateMandatoryFields(r);

            if (r.getId() == null) {
                this.service.create(r);
            } else {
                this.service.update(r);
            }
            handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage("rd_save_success"),
                    JSFUtil.getMessage("rd_tenant"));
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_save_error"), JSFUtil.getMessage("rd_tenant"));
            return null;
        }
        tenant = new Tenant();
        return "tenants";
    }

    public String editRecords() {
        if(selectedTenant != null) {
            return "tenantDetails";
        }
        return "tenants";
    }

    public void deleteTenantHierarchy(){
        try {
            if (selectedTenant != null) {
                service.deleteTenantHierarchy(selectedTenant.getId());
                handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage("rd_delete_success"),
                        JSFUtil.getMessage("rd_user"));
            }
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_delete_error"), JSFUtil.getMessage("rd_user"));
        }
    }

    public String returnHome() {
        tenant = new Tenant();
        selectedTenant=null;
        return "tenants";
    }

    private void validateMandatoryFields(SystemTenant r) throws Exception {
        if(r != null && r.getTenantType().equals(TenantType.CLIENT_TENANT)) {
            if(r.getClientAddress() == null || r.getClientAddress().equals("")) {
                sendErrorMessage(JSFUtil.getMessage("rd_tenant_client_address_is_mandatory"));
            }

            if(r.getClientZipCode() == null || r.getClientZipCode().equals("")) {
                sendErrorMessage(JSFUtil.getMessage("rd_tenant_client_zip_code_is_mandatory"));
            }

            if(r.getClientCity() == null || r.getClientCity().equals("")) {
                sendErrorMessage(JSFUtil.getMessage("rd_tenant_client_city_is_mandatory"));
            }

            if(r.getClientCountry() == null || r.getClientCountry().equals("")) {
                sendErrorMessage(JSFUtil.getMessage("rd_tenant_client_country_is_mandatory"));
            }

            if(r.getClientPhoneNumber() == null || r.getClientPhoneNumber().equals("")) {
                sendErrorMessage(JSFUtil.getMessage("rd_tenant_client_phone_is_mandatory"));
            }

            if(r.getClientEmail() == null || r.getClientEmail().equals("")) {
                sendErrorMessage(JSFUtil.getMessage("rd_tenant_client_email_is_mandatory"));
            }
        }
    }

    private void sendErrorMessage(String message) throws Exception {
        handleMessage(FacesMessage.SEVERITY_ERROR, message, JSFUtil.getMessage("rd_tenant"));
        throw new Exception();
    }

    public String edit(SystemTenant t) {
        try {
            this.service.update(t);
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_edit_error"), JSFUtil.getMessage("rd_user"));
        }
        return "tenants";
    }

    public SystemTenant getTenant() {
        return tenant;
    }

    public void setTenant(SystemTenant tenant) {
        this.tenant = tenant;
    }

    public SystemTenantType[] getTenantTypes() {
        return TenantType.values();
    }

    public List<? extends SystemTenant> getParents() throws SystemException {
        return service.getAll(null, 1, 3, null, false).getResults();
    }

    public LazyDataModel<? extends SystemTenant> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<? extends SystemTenant> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public SystemTenant getSelectedTenant() {
        return selectedTenant;
    }

    public void setSelectedTenant(SystemTenant selectedTenant) {
        this.selectedTenant = selectedTenant;
    }

    public TenantRESTServiceAccess getService() {
        return service;
    }

    public void setService(TenantRESTServiceAccess service) {
        this.service = service;
    }

    public void onRowSelect(SelectEvent<SystemTenant> event) {
        this.selectedTenant= event.getObject();
        FacesMessage msg = new FacesMessage("Tenant Selected", String.valueOf(event.getObject().getId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
