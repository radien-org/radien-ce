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
package io.radien.webapp.role;

import io.radien.api.model.role.SystemRole;
import io.radien.api.service.role.RoleRESTServiceAccess;
import io.radien.ms.rolemanagement.client.entities.Role;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.DataModelEnum;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.activeTenant.ActiveTenantDataModelManager;
import io.radien.webapp.activeTenant.ActiveTenantMandatory;
import io.radien.webapp.resource.LazyResourcesDataModel;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.Serializable;

/**
 * Role Interface Data Model. Class responsible for managing and maintaining
 * the data between the front end and backend for the tenant tables.
 *
 * @author Bruno Gama
 */
@Model
@SessionScoped
public class RoleDataModel extends AbstractManager implements Serializable {

    @Inject
    private RoleRESTServiceAccess service;

    @Inject
    private ActiveTenantDataModelManager activeTenantDataModelManager;

    private LazyDataModel<? extends SystemRole> lazyModel;

    private SystemRole selectedRole;

    private SystemRole role = new Role();

    /**
     * Initialization of the tenant data tables and models
     */
    @PostConstruct
    public void init() {
        try {
            if(activeTenantDataModelManager.isTenantActive()) {
                lazyModel = new LazyRoleDataModel(service);
            } else {
                redirectToHomePage();
            }
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.GENERIC_ERROR_MESSAGE.getValue()), JSFUtil.getMessage(DataModelEnum.ROLE_MESSAGE.getValue()));
        }
    }

    /**
     * Data reload method
     */
    @ActiveTenantMandatory
    public void onload() {
        init();
    }

    /**
     * Role creation or update save method
     * @param systemRoleToSave system tenant to be saved
     * @return a string value to redirect the user into the correct page either send him to the table in case of success,
     * or into the edit menu in case of error
     */
    @ActiveTenantMandatory
    public String save(SystemRole systemRoleToSave) {
        try {
            this.service.create(systemRoleToSave);
            handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage(DataModelEnum.SAVE_SUCCESS_MESSAGE.getValue()),
                    JSFUtil.getMessage(DataModelEnum.ROLE_MESSAGE.getValue()));
            role = new Role();
            return DataModelEnum.ROLE_MAIN_PAGE.getValue();
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.SAVE_ERROR_MESSAGE.getValue()), JSFUtil.getMessage(DataModelEnum.ROLE_MESSAGE.getValue()));
            return DataModelEnum.ROLE_CREATION_PAGE.getValue();
        }
    }

    /**
     * Role update edit record method
     * @return the correct page to where the user should be redirected
     */
    @ActiveTenantMandatory
    public String editRecords() {
        try {
            if (selectedRole != null) {
                return DataModelEnum.ROLE_DETAIL_PAGE.getValue();
            } else {
                handleMessage(FacesMessage.SEVERITY_WARN, JSFUtil.getMessage(DataModelEnum.SELECT_RECORD_FIRST.getValue()), JSFUtil.getMessage(DataModelEnum.ROLE_MESSAGE.getValue()));
            }
            return DataModelEnum.ROLE_MAIN_PAGE.getValue();
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.SAVE_ERROR_MESSAGE.getValue()), JSFUtil.getMessage(DataModelEnum.ROLE_MESSAGE.getValue()));
            return DataModelEnum.ROLE_DETAIL_PAGE.getValue();
        }
    }

    /**
     * Tenant deletion role record method
     */
    @ActiveTenantMandatory
    public void delete(){
        try {
            if (selectedRole != null) {
                service.delete(selectedRole.getId());
                handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage(DataModelEnum.DELETE_SUCCESS.getValue()),
                        JSFUtil.getMessage(DataModelEnum.ROLE_MESSAGE.getValue()));
            } else {
                handleMessage(FacesMessage.SEVERITY_WARN, JSFUtil.getMessage(DataModelEnum.ERROR_SELECT_RECORD_TO_DELETE.getValue()), JSFUtil.getMessage(DataModelEnum.ROLE_MESSAGE.getValue()));
            }
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.DELETE_ERROR.getValue()), JSFUtil.getMessage(DataModelEnum.ROLE_MESSAGE.getValue()));
        }

        role = new Role();
        selectedRole = null;
    }

    /**
     * Redirect user into tenant table page
     */
    public String returnToDataTableRecords() {
        try {
            role = new Role();
            selectedRole = null;
            return DataModelEnum.ROLE_MAIN_PAGE.getValue();
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.DELETE_ERROR.getValue()), JSFUtil.getMessage(DataModelEnum.ROLE_MESSAGE.getValue()));
            return DataModelEnum.ROLE_MAIN_PAGE.getValue();
        }
    }

    /**
     * Method to update and edit the given system role
     * @param systemRoleToEdit to be edited and updated
     * @return a string value to where the user should be redirected
     */
    @ActiveTenantMandatory
    public String edit(SystemRole systemRoleToEdit) {
        try {
            if(systemRoleToEdit != null && !systemRoleToEdit.getName().isEmpty()) {
                this.service.create(systemRoleToEdit);
                role = new Role();
                selectedRole = null;
                return DataModelEnum.ROLE_MAIN_PAGE.getValue();
            } else {
                handleMessage(FacesMessage.SEVERITY_ERROR, JSFUtil.getMessage(DataModelEnum.ROLE_NAME_MANDATORY.getValue()), JSFUtil.getMessage(DataModelEnum.ROLE_MESSAGE.getValue()));
                return DataModelEnum.ROLE_DETAIL_PAGE.getValue();
            }
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.EDIT_ERROR_MESSAGE.getValue()), JSFUtil.getMessage(DataModelEnum.ROLE_MESSAGE.getValue()));
            return DataModelEnum.ROLE_DETAIL_PAGE.getValue();
        }
    }

    /**
     * Role getter method
     * @return the tenant in use
     */
    public SystemRole getRole() {
        return role;
    }

    /**
     * Role setter method
     * @param role to be used or updated
     */
    public void setRole(SystemRole role) {
        this.role = role;
    }

    /**
     * Lazy Model Data Table getter method
     * @return the lazy data model
     */
    public LazyDataModel<? extends SystemRole> getLazyModel() {
        return lazyModel;
    }

    /**
     * Lazy data model setter
     * @param lazyModel to be set
     */
    public void setLazyModel(LazyDataModel<? extends SystemRole> lazyModel) {
        this.lazyModel = lazyModel;
    }

    /**
     * Selected role getter method
     * @return the current selected method
     */
    public SystemRole getSelectedRole() {
        return selectedRole;
    }

    /**
     * Selected role setter method
     * @param selectedRole to be used or updated
     */
    public void setSelectedRole(SystemRole selectedRole) {
        this.selectedRole = selectedRole;
    }

    /**
     * Role REST Service Access getter method to perform actions
     * @return the current tenant rest service access
     */
    public RoleRESTServiceAccess getService() {
        return service;
    }

    /**
     * Role REST Service Access setter method
     * @param service to be used or set
     */
    public void setService(RoleRESTServiceAccess service) {
        this.service = service;
    }

    /**
     * On Row Select is a action listener in case a record is selected
     * @param event listen when row as been selected
     */
    public void onRowSelect(SelectEvent<SystemRole> event) {
        this.selectedRole = event.getObject();
        FacesMessage msg = new FacesMessage(JSFUtil.getMessage(DataModelEnum.ROLE_SELECTED.getValue()), String.valueOf(event.getObject().getId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}