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
package io.radien.webapp.resource;

import io.radien.api.model.permission.SystemResource;
import io.radien.api.service.permission.ResourceRESTServiceAccess;
import io.radien.ms.permissionmanagement.client.entities.Resource;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.DataModelEnum;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.activeTenant.ActiveTenantDataModelManager;
import io.radien.webapp.activeTenant.ActiveTenantMandatory;
import io.radien.webapp.permission.LazyPermissionDataModel;
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
 * Resource Interface Data Model. Class responsible for managing and maintaining
 * the data between the front end and backend for the tenant tables.
 *
 * @author Bruno Gama
 */
@Model
@SessionScoped
public class ResourceDataModel extends AbstractManager implements Serializable {

    @Inject
    private ResourceRESTServiceAccess service;

    @Inject
    private ActiveTenantDataModelManager activeTenantDataModelManager;

    private LazyDataModel<? extends SystemResource> lazyModel;

    private SystemResource selectedResource;

    private SystemResource resource = new Resource();

    /**
     * Initialization of the resource data tables and models
     */
    @PostConstruct
    public void init() {
        try {
            if(activeTenantDataModelManager.isTenantActive()) {
                lazyModel = new LazyResourcesDataModel(service);
            } else {
                redirectToHomePage();
            }
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.GENERIC_ERROR_MESSAGE.getValue()), JSFUtil.getMessage(DataModelEnum.RESOURCE_RD_TENANT.getValue()));
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
     * Resource creation or update save method
     * @param systemResourceToSave system tenant to be saved
     * @return a string value to redirect the user into the correct page either send him to the table in case of success,
     * or into the edit menu in case of error
     */
    @ActiveTenantMandatory
    public String save(SystemResource systemResourceToSave) {
        try {
            this.service.create(systemResourceToSave);
            handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage(DataModelEnum.SAVE_SUCCESS_MESSAGE.getValue()),
                    JSFUtil.getMessage(DataModelEnum.RESOURCE_RD_TENANT.getValue()));
            resource = new Resource();
            return DataModelEnum.RESOURCE_DATA_TABLE_PAGE.getValue();
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.SAVE_ERROR_MESSAGE.getValue()), JSFUtil.getMessage(DataModelEnum.RESOURCE_RD_TENANT.getValue()));
            return DataModelEnum.RESOURCE_CREATION_PAGE.getValue();
        }
    }

    /**
     * Resource update edit record method
     * @return the correct page to where the user should be redirected
     */
    @ActiveTenantMandatory
    public String editRecords() {
        try {
            if (selectedResource != null) {
                return DataModelEnum.RESOURCE_DETAIL_PAGE.getValue();
            } else {
                handleMessage(FacesMessage.SEVERITY_WARN, JSFUtil.getMessage(DataModelEnum.SELECT_RECORD_FIRST.getValue()), JSFUtil.getMessage(DataModelEnum.RESOURCE_RD_TENANT.getValue()));
            }
            return DataModelEnum.RESOURCE_DATA_TABLE_PAGE.getValue();
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.SAVE_ERROR_MESSAGE.getValue()), JSFUtil.getMessage(DataModelEnum.RESOURCE_RD_TENANT.getValue()));
            return DataModelEnum.RESOURCE_DETAIL_PAGE.getValue();
        }
    }

    /**
     * Resource deletion in cascade method
     */
    @ActiveTenantMandatory
    public void delete(){
        try {
            if (selectedResource != null) {
                service.delete(selectedResource.getId());
                handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage(DataModelEnum.DELETE_SUCCESS.getValue()),
                        JSFUtil.getMessage(DataModelEnum.RESOURCE_RD_TENANT.getValue()));
            } else {
                handleMessage(FacesMessage.SEVERITY_WARN, JSFUtil.getMessage(DataModelEnum.ERROR_SELECT_RECORD_TO_DELETE.getValue()), JSFUtil.getMessage(DataModelEnum.RESOURCE_RD_TENANT.getValue()));
            }
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.DELETE_ERROR.getValue()), JSFUtil.getMessage(DataModelEnum.RESOURCE_RD_TENANT.getValue()));
        }
        resource = new Resource();
        selectedResource = null;
    }

    /**
     * Redirect user into resource table page
     */
    public String returnToDataTableRecords() {
        try {
            resource = new Resource();
            selectedResource = null;
            return DataModelEnum.RESOURCE_DATA_TABLE_PAGE.getValue();
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.DELETE_ERROR.getValue()), JSFUtil.getMessage(DataModelEnum.RESOURCE_RD_TENANT.getValue()));
            return DataModelEnum.RESOURCE_DATA_TABLE_PAGE.getValue();
        }
    }

    /**
     * Method to update and edit the given system tenant
     * @param systemResourceToEdit to be edited and updated
     * @return a string value to where the user should be redirected
     */
    @ActiveTenantMandatory
    public String edit(SystemResource systemResourceToEdit) {
        try {
            if(systemResourceToEdit != null && !systemResourceToEdit.getName().isEmpty()) {
                this.service.create(systemResourceToEdit);
                resource = new Resource();
                selectedResource = null;
                return DataModelEnum.RESOURCE_DATA_TABLE_PAGE.getValue();
            } else {
                handleMessage(FacesMessage.SEVERITY_ERROR, JSFUtil.getMessage(DataModelEnum.RESOURCE_NAME_IS_MANDATORY.getValue()), JSFUtil.getMessage(DataModelEnum.RESOURCE_RD_TENANT.getValue()));
                return DataModelEnum.RESOURCE_DETAIL_PAGE.getValue();
            }
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.EDIT_ERROR_MESSAGE.getValue()), JSFUtil.getMessage(DataModelEnum.RESOURCE_RD_TENANT.getValue()));
            return DataModelEnum.RESOURCE_DETAIL_PAGE.getValue();
        }
    }

    /**
     * Resource getter method
     * @return the resource in use
     */
    public SystemResource getResource() {
        return resource;
    }

    /**
     * Resource setter method
     * @param resource to be used or updated
     */
    public void setResource(SystemResource resource) {
        this.resource = resource;
    }

    /**
     * Lazy Model Data Table getter method
     * @return the lazy data model
     */
    public LazyDataModel<? extends SystemResource> getLazyModel() {
        return lazyModel;
    }

    /**
     * Lazy data model setter
     * @param lazyModel to be set
     */
    public void setLazyModel(LazyDataModel<? extends SystemResource> lazyModel) {
        this.lazyModel = lazyModel;
    }

    /**
     * Selected resource getter method
     * @return the current selected method
     */
    public SystemResource getSelectedResource() {
        return selectedResource;
    }

    /**
     * Selected resource setter method
     * @param selectedResource to be used or updated
     */
    public void setSelectedResource(SystemResource selectedResource) {
        this.selectedResource = selectedResource;
    }

    /**
     * Resource REST Service Access getter method to perform actions
     * @return the current resource rest service access
     */
    public ResourceRESTServiceAccess getService() {
        return service;
    }

    /**
     * Resource REST Service Access setter method
     * @param service to be used or set
     */
    public void setService(ResourceRESTServiceAccess service) {
        this.service = service;
    }

    /**
     * On Row Select is a action listener in case a record is selected
     * @param event listen when row as been selected
     */
    public void onRowSelect(SelectEvent<SystemResource> event) {
        this.selectedResource = event.getObject();
        FacesMessage msg = new FacesMessage(JSFUtil.getMessage(DataModelEnum.RESOURCE_SELECTED.getValue()), String.valueOf(event.getObject().getId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}

