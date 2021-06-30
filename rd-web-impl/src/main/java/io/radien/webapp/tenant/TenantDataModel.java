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
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Tenant Interface Data Model. Class responsible for managing and maintaining
 * the data between the front end and backend for the tenant tables.
 *
 * @author Bruno Gama
 */
@Model
@SessionScoped
public class TenantDataModel extends AbstractManager implements Serializable {

    @Inject
    private TenantRESTServiceAccess service;

    private LazyDataModel<? extends SystemTenant> lazyModel;

    private SystemTenant selectedTenant;

    private SystemTenant tenant = new Tenant();

    protected Date tenantStartDate;
    protected Date tenantEndDate;


    /**
     * Initialization of the tenant data tables and models
     */
    @PostConstruct
    public void init() {
        try {
            lazyModel = new LazyTenantDataModel(service);
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_generic_error_message"), JSFUtil.getMessage("rd_tenants"));
        }
    }

    /**
     * Data reload method
     */
    public void onload() {
        try {
            init();
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_generic_error_message"), JSFUtil.getMessage("rd_tenants"));
        }
    }

    /**
     * Tenant creation or update save method that will validate all the mandatory fields and convert the
     * given dates from text to date format
     * @param r system tenant to be saved
     * @return a string value to redirect the user into the correct page either send him to the table in case of success,
     * or into the edit menu in case of error
     */
    public String save(SystemTenant r) {
        try {
            validateMandatoryFields(r);
            convertAndSetDates(r, this.tenantStartDate, this.tenantEndDate);
            if (r.getId() == null) {
                this.service.create(r);
            } else {
                this.service.update(r);
            }
            handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage("rd_save_success"),
                    JSFUtil.getMessage("rd_tenant"));
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_save_error"), JSFUtil.getMessage("rd_tenant"));
            return "tenant";
        }
        tenant = new Tenant();
        return "tenants";
    }

    /**
     * Tenant update edit record method
     * @return the correct page to where the user should be redirected
     */
    public String editRecords() {
        try {
            if (selectedTenant != null) {
                return "tenantDetails";
            } else {
                handleMessage(FacesMessage.SEVERITY_WARN, JSFUtil.getMessage("rd_select_record_first"), JSFUtil.getMessage("rd_tenants"));
            }
            return "tenants";
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_generic_error_message"), JSFUtil.getMessage("rd_tenants"));
            return "tenantDetails";
        }
    }

    /**
     * Tenant deletion in cascade method
     */
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

        tenant = new Tenant();
        selectedTenant=null;
    }

    /**
     * Redirect user into tenant table page
     */
    public String returnHome() {
        tenant = new Tenant();
        selectedTenant=null;
        return "tenants";
    }

    /**
     * Method to validate all the requested fields in the given system tenant
     * @param r to be validated
     * @throws Exception in case of incoherence in the data
     */
    private void validateMandatoryFields(SystemTenant r) throws Exception {
        if(r != null && r.getTenantType().equals(TenantType.CLIENT_TENANT)) {
            clientAddressValidation(r);
            clientZipCodeValidation(r);
            clientCityValidation(r);
            clientCountryValidation(r);
            clientPhoneNumberValidation(r);
            clientEmailValidation(r);
        }
    }

    /**
     * Check if in the given client tenant the client email is filled
     * @param r system tenant to be validated
     * @throws Exception to be throw in case of client email is not filled
     */
    private void clientEmailValidation(SystemTenant r) throws Exception {
        if(r.getClientEmail() == null) {
            sendErrorMessage(JSFUtil.getMessage("rd_tenant_client_email_is_mandatory"));
        }
    }

    /**
     * Check if in the given client tenant the client phone number is filled
     * @param r system tenant to be validated
     * @throws Exception to be throw in case of client phone number is not filled
     */
    private void clientPhoneNumberValidation(SystemTenant r) throws Exception {
        if(r.getClientPhoneNumber() == null) {
            sendErrorMessage(JSFUtil.getMessage("rd_tenant_client_phone_is_mandatory"));
        }
    }

    /**
     * Check if in the given client tenant the client country is filled
     * @param r system tenant to be validated
     * @throws Exception to be throw in case of client country is not filled
     */
    private void clientCountryValidation(SystemTenant r) throws Exception {
        if(r.getClientCountry() == null || r.getClientCountry().equals("")) {
            sendErrorMessage(JSFUtil.getMessage("rd_tenant_client_country_is_mandatory"));
        }
    }

    /**
     * Check if in the given client tenant the client city is filled
     * @param r system tenant to be validated
     * @throws Exception to be throw in case of client city is not filled
     */
    private void clientCityValidation(SystemTenant r) throws Exception {
        if(r.getClientCity() == null || r.getClientCity().equals("")) {
            sendErrorMessage(JSFUtil.getMessage("rd_tenant_client_city_is_mandatory"));
        }
    }

    /**
     * Check if in the given client tenant the client zip code is filled
     * @param r system tenant to be validated
     * @throws Exception to be throw in case of client zip code is not filled
     */
    private void clientZipCodeValidation(SystemTenant r) throws Exception {
        if(r.getClientZipCode() == null || r.getClientZipCode().equals("")) {
            sendErrorMessage(JSFUtil.getMessage("rd_tenant_client_zip_code_is_mandatory"));
        }
    }

    /**
     * Check if in the given client tenant the client address is filled
     * @param r system tenant to be validated
     * @throws Exception to be throw in case of client address is not filled
     */
    private void clientAddressValidation(SystemTenant r) throws Exception {
        if(r.getClientAddress() == null || r.getClientAddress().equals("")) {
            sendErrorMessage(JSFUtil.getMessage("rd_tenant_client_address_is_mandatory"));
        }
    }

    /**
     * Send the requested message to the user
     * @param message to be throw to the user
     * @throws Exception to be throw
     */
    private void sendErrorMessage(String message) throws Exception {
        handleMessage(FacesMessage.SEVERITY_ERROR, message, JSFUtil.getMessage("rd_tenant"));
        throw new Exception();
    }

    /**
     * Method to update and edit the given system tenant
     * @param t to be edited and updated
     * @return a string value to where the user should be redirected
     */
    public String edit(SystemTenant t) {
        try {
            this.service.update(t);
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_edit_error"), JSFUtil.getMessage("rd_user"));
        }
        return "tenants";
    }

    /**
     * Conversion method to map string values into dates
     * @param tenant to be converted and stored the new information
     * @param tenantStartDate to be converted
     * @param tenantEndDate to be converted
     */
    protected void convertAndSetDates(SystemTenant tenant, Date tenantStartDate, Date tenantEndDate) {
        if (tenantStartDate != null) {
            LocalDate startDate = this.tenantStartDate.toInstant().atZone(ZoneId.systemDefault()).
                    toLocalDate();
            tenant.setTenantStart(startDate);
        }
        if (tenantEndDate != null) {
            LocalDate endDate = this.tenantEndDate.toInstant().atZone(ZoneId.systemDefault()).
                    toLocalDate();
            tenant.setTenantEnd(endDate);
        }
    }

    /**
     * Tenant getter method
     * @return the tenant in use
     */
    public SystemTenant getTenant() {
        return tenant;
    }

    /**
     * Tenant setter method
     * @param tenant to be used or updated
     */
    public void setTenant(SystemTenant tenant) {
        this.tenant = tenant;
    }

    /**
     * List of the tenant types
     * @return a list of all the tenant types [Root, Client, Sub]
     */
    public SystemTenantType[] getTenantTypes() {
        return TenantType.values();
    }

    /**
     * Method to retrieve a list of all the parent tenants
     * @return a list of system tenants
     * @throws SystemException in case of issues in the url or retrieving the data
     */
    public List<? extends SystemTenant> getParents() throws SystemException {
        return service.getAll(null, 1, 3, null, false).getResults();
    }

    /**
     * Lazy Model Data Table getter method
     * @return the lazy data model
     */
    public LazyDataModel<? extends SystemTenant> getLazyModel() {
        return lazyModel;
    }

    /**
     * Lazy data model setter
     * @param lazyModel to be set
     */
    public void setLazyModel(LazyDataModel<? extends SystemTenant> lazyModel) {
        this.lazyModel = lazyModel;
    }

    /**
     * Selected tenant getter method
     * @return the current selected method
     */
    public SystemTenant getSelectedTenant() {
        return selectedTenant;
    }

    /**
     * Selected tenant setter method
     * @param selectedTenant to be used or updated
     */
    public void setSelectedTenant(SystemTenant selectedTenant) {
        this.selectedTenant = selectedTenant;
    }

    /**
     * Tenant REST Service Access getter method to perform actions
     * @return the current tenant rest service access
     */
    public TenantRESTServiceAccess getService() {
        return service;
    }

    /**
     * Tenant REST Service Access setter method
     * @param service to be used or set
     */
    public void setService(TenantRESTServiceAccess service) {
        this.service = service;
    }

    /**
     * Tenant start date setter
     * @return the tenant start date
     */
    public Date getTenantStartDate() { return tenantStartDate; }

    /**
     * Tenant start date setter
     * @param tenantStartDate to be used or set
     */
    public void setTenantStartDate(Date tenantStartDate) { this.tenantStartDate = tenantStartDate; }

    /**
     * Tenant end date getter
     * @return the tenant end date
     */
    public Date getTenantEndDate() { return tenantEndDate; }

    /**
     * Tenant end date setter
     * @param tenantEndDate to be used or set
     */
    public void setTenantEndDate(Date tenantEndDate) { this.tenantEndDate = tenantEndDate; }

    /**
     * On Row Select is a action listener in case a record is selected
     * @param event listen when row as been selected
     */
    public void onRowSelect(SelectEvent<SystemTenant> event) {
        this.selectedTenant= event.getObject();
        FacesMessage msg = new FacesMessage(JSFUtil.getMessage("rd_tenantSelected"), String.valueOf(event.getObject().getId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
