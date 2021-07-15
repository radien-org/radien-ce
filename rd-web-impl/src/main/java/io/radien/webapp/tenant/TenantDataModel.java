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

import io.radien.api.model.tenant.SystemActiveTenant;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.model.tenant.SystemTenantType;
import io.radien.api.service.tenant.ActiveTenantRESTServiceAccess;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.ms.tenantmanagement.client.entities.TenantType;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.DataModelEnum;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.activeTenant.ActiveTenantMandatory;
import io.radien.webapp.security.UserSession;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    @Inject
    private UserSession userSession;

    @Inject
    private ActiveTenantRESTServiceAccess activeTenantRESTServiceAccess;

    private LazyDataModel<? extends SystemTenant> lazyModel;

    private SystemTenant selectedTenant;

    private SystemTenant tenant = new Tenant();

    protected Date tenantStartDate;
    protected Date tenantEndDate;

    /**
     * Initialization of the tenant data tables and models
     */
    @PostConstruct
    @ActiveTenantMandatory
    public void init() {
        try {
            lazyModel = new LazyTenantDataModel(service);
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.GENERIC_ERROR_MESSAGE.getValue()),
                    JSFUtil.getMessage(DataModelEnum.TENANT_RD_TENANT.getValue()));
        }
    }

    /**
     * Data reload method
     */
    public void onload() {
        init();
    }

    /**
     * Tenant creation or update save method that will validate all the mandatory fields and convert the
     * given dates from text to date format
     * @param systemTenantToSave system tenant to be saved
     * @return a string value to redirect the user into the correct page either send him to the table in case of success,
     * or into the edit menu in case of error
     */
    @ActiveTenantMandatory
    public String save(SystemTenant systemTenantToSave) {
        try {
            fillParentClientId(systemTenantToSave);
            validateMandatoryFields(systemTenantToSave);
            convertAndSetDates(systemTenantToSave, this.tenantStartDate, this.tenantEndDate);
            if (systemTenantToSave.getId() == null) {
                this.service.create(systemTenantToSave);
            } else {
                this.service.update(systemTenantToSave);
            }
            handleMessage(FacesMessage.SEVERITY_INFO,
                    JSFUtil.getMessage(DataModelEnum.SAVE_SUCCESS_MESSAGE.getValue()),
                    JSFUtil.getMessage(DataModelEnum.TENANT_RD_TENANT.getValue()));
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.SAVE_ERROR_MESSAGE.getValue()),
                    JSFUtil.getMessage(DataModelEnum.TENANT_RD_TENANT.getValue()));
            return DataModelEnum.TENANT_CREATION_PAGE.getValue();
        }
        tenant = new Tenant();
        return DataModelEnum.TENANT_MAIN_PAGE.getValue();
    }

    /**
     * Automatically fill the parent id and client id if necessary
     * @param systemTenantToSave system tenant to be updated
     */
    @ActiveTenantMandatory
    private void fillParentClientId(SystemTenant systemTenantToSave) throws SystemException {
        if(systemTenantToSave.getTenantType().equals(TenantType.CLIENT_TENANT)) {
            retrieveParentTenantId(systemTenantToSave);
        } else if(systemTenantToSave.getTenantType().equals(TenantType.SUB_TENANT)) {
            retrieveParentTenantId(systemTenantToSave);
            retrieveClientTenantId(systemTenantToSave);
        }
    }

    /**
     * By a given tenant will calculate the previous one the given belongs to (parent tenant)
     * @param systemTenantToSave to be verified
     */
    @ActiveTenantMandatory
    private void retrieveParentTenantId(SystemTenant systemTenantToSave) throws SystemException {
        try {
            List<? extends SystemActiveTenant> activeTenants = activeTenantRESTServiceAccess.getActiveTenantByFilter(
                            userSession.getUserId(), null, null, true);
            for(SystemActiveTenant aCt : activeTenants) {
                if(aCt.getIsTenantActive()) {
                    systemTenantToSave.setParentId(aCt.getTenantId());
                }
            }
        } catch (SystemException e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.SAVE_ERROR_MESSAGE.getValue()),
                    JSFUtil.getMessage(DataModelEnum.TENANT_RD_TENANT.getValue()));
            throw e;
        }
    }

    /**
     * By a given tenant will calculate the previous client one the given belongs to (client tenant)
     * @param systemTenantToSave to be verified
     */
    @ActiveTenantMandatory
    private void retrieveClientTenantId(SystemTenant systemTenantToSave) throws SystemException {
        try {
            List<? extends SystemActiveTenant> activeTenants = activeTenantRESTServiceAccess.getActiveTenantByFilter(
                    userSession.getUserId(), null, null, true);
            for(SystemActiveTenant aCt : activeTenants) {
                if(aCt.getIsTenantActive()) {
                    requestUpperTenant(systemTenantToSave, aCt.getTenantId());
                }
            }
        } catch (SystemException e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.SAVE_ERROR_MESSAGE.getValue()),
                    JSFUtil.getMessage(DataModelEnum.TENANT_RD_TENANT.getValue()));
            throw e;
        }
    }

    /**
     * Recursive method that if the upper tenant is not a client tenant the following method will continue searching
     * until he finds one that is client tenant and the requested one belongs to
     * @param systemTenantToSave to be verified
     * @param tenantId of the last upper tenant
     * @throws SystemException in case of token expiration or any issue on the application
     */
    @ActiveTenantMandatory
    private void requestUpperTenant(SystemTenant systemTenantToSave, Long tenantId) throws SystemException {
        try {
            Optional<SystemTenant> tenantToBeSearch = service.getTenantById(tenantId);

            if(tenantToBeSearch.isPresent()) {
                if(tenantToBeSearch.get().getTenantType().equals(TenantType.CLIENT_TENANT)) {
                    systemTenantToSave.setClientId(tenantToBeSearch.get().getId());
                } else {
                    requestUpperTenant(systemTenantToSave, tenantToBeSearch.get().getParentId());
                }
            } else {
                handleMessage(FacesMessage.SEVERITY_ERROR,
                        JSFUtil.getMessage(DataModelEnum.TENANT_NOT_FOUND.getValue()),
                        JSFUtil.getMessage(DataModelEnum.TENANT_RD_TENANT.getValue()));
            }
        } catch (SystemException e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.SAVE_ERROR_MESSAGE.getValue()),
                    JSFUtil.getMessage(DataModelEnum.TENANT_RD_TENANT.getValue()));
        }
    }

    /**
     * Tenant update edit record method
     * @return the correct page to where the user should be redirected
     */
    @ActiveTenantMandatory
    public String editRecords() throws IOException {
        if (selectedTenant != null) {
            return DataModelEnum.TENANT_DETAIL_PAGE.getValue();
        } else {
            handleMessage(FacesMessage.SEVERITY_WARN,
                    JSFUtil.getMessage(DataModelEnum.SELECT_RECORD_FIRST.getValue()),
                    JSFUtil.getMessage(DataModelEnum.TENANT_RD_TENANT.getValue()));
        }
        return DataModelEnum.TENANT_MAIN_PAGE.getValue();
    }

    /**
     * Tenant deletion in cascade method
     */
    @ActiveTenantMandatory
    public void deleteTenantHierarchy(){
        try {
            if (selectedTenant != null) {
                service.deleteTenantHierarchy(selectedTenant.getId());
                handleMessage(FacesMessage.SEVERITY_INFO,
                        JSFUtil.getMessage(DataModelEnum.DELETE_SUCCESS.getValue()),
                        JSFUtil.getMessage(DataModelEnum.TENANT_RD_TENANT.getValue()));
            }
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.DELETE_ERROR.getValue()),
                    JSFUtil.getMessage(DataModelEnum.TENANT_RD_TENANT.getValue()));
        }

        tenant = new Tenant();
        selectedTenant=null;
    }

    /**
     * Redirect user into tenant table page
     */
    public String returnToDataTableRecords() {
        tenant = new Tenant();
        selectedTenant=null;
        return DataModelEnum.TENANT_MAIN_PAGE.getValue();
    }

    /**
     * Method to validate all the requested fields in the given system tenant
     * @param r to be validated
     * @throws Exception in case of incoherence in the data
     */
    @ActiveTenantMandatory
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
            sendErrorMessage(JSFUtil.getMessage(DataModelEnum.TENANT_CLIENT_EMAIL_IS_MANDATORY.getValue()));
        }
    }

    /**
     * Check if in the given client tenant the client phone number is filled
     * @param r system tenant to be validated
     * @throws Exception to be throw in case of client phone number is not filled
     */
    private void clientPhoneNumberValidation(SystemTenant r) throws Exception {
        if(r.getClientPhoneNumber() == null) {
            sendErrorMessage(JSFUtil.getMessage(DataModelEnum.TENANT_CLIENT_PHONE_IS_MANDATORY.getValue()));
        }
    }

    /**
     * Check if in the given client tenant the client country is filled
     * @param r system tenant to be validated
     * @throws Exception to be throw in case of client country is not filled
     */
    private void clientCountryValidation(SystemTenant r) throws Exception {
        if(r.getClientCountry() == null || r.getClientCountry().equals("")) {
            sendErrorMessage(JSFUtil.getMessage(DataModelEnum.TENANT_CLIENT_COUNTRY_IS_MANDATORY.getValue()));
        }
    }

    /**
     * Check if in the given client tenant the client city is filled
     * @param r system tenant to be validated
     * @throws Exception to be throw in case of client city is not filled
     */
    private void clientCityValidation(SystemTenant r) throws Exception {
        if(r.getClientCity() == null || r.getClientCity().equals("")) {
            sendErrorMessage(JSFUtil.getMessage(DataModelEnum.TENANT_CLIENT_CITY_IS_MANDATORY.getValue()));
        }
    }

    /**
     * Check if in the given client tenant the client zip code is filled
     * @param r system tenant to be validated
     * @throws Exception to be throw in case of client zip code is not filled
     */
    private void clientZipCodeValidation(SystemTenant r) throws Exception {
        if(r.getClientZipCode() == null || r.getClientZipCode().equals("")) {
            sendErrorMessage(JSFUtil.getMessage(DataModelEnum.TENANT_CLIENT_ZIP_CODE_IS_MANDATORY.getValue()));
        }
    }

    /**
     * Check if in the given client tenant the client address is filled
     * @param r system tenant to be validated
     * @throws Exception to be throw in case of client address is not filled
     */
    private void clientAddressValidation(SystemTenant r) throws Exception {
        if(r.getClientAddress() == null || r.getClientAddress().equals("")) {
            sendErrorMessage(JSFUtil.getMessage(DataModelEnum.TENANT_CLIENT_ADDRESS_IS_MANDATORY.getValue()));
        }
    }

    /**
     * Send the requested message to the user
     * @param message to be throw to the user
     * @throws Exception to be throw
     */
    private void sendErrorMessage(String message) throws Exception {
        handleMessage(FacesMessage.SEVERITY_ERROR, message,
                JSFUtil.getMessage(DataModelEnum.TENANT_RD_TENANT.getValue()));
        throw new Exception();
    }

    /**
     * Method to update and edit the given system tenant
     * @param systemTenantToEdit to be edited and updated
     * @return a string value to where the user should be redirected
     */
    @ActiveTenantMandatory
    public String edit(SystemTenant systemTenantToEdit) throws IOException {
        try {
            this.service.update(systemTenantToEdit);
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.EDIT_ERROR_MESSAGE.getValue()),
                    JSFUtil.getMessage(DataModelEnum.TENANT_RD_TENANT.getValue()));
        }
        return DataModelEnum.TENANT_MAIN_PAGE.getValue();
    }

    /**
     * Conversion method to map string values into dates
     * @param tenant to be converted and stored the new information
     * @param tenantStartDate to be converted
     * @param tenantEndDate to be converted
     */
    @ActiveTenantMandatory
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
        FacesMessage msg = new FacesMessage(JSFUtil.getMessage(DataModelEnum.TENANT_SELECTED_TENANT.getValue()),
                String.valueOf(event.getObject().getId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
