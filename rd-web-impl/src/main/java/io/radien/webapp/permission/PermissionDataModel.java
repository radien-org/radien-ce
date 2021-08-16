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
package io.radien.webapp.permission;

import io.radien.api.model.permission.SystemPermission;
import io.radien.api.service.permission.ActionRESTServiceAccess;
import io.radien.api.service.permission.PermissionRESTServiceAccess;
import io.radien.api.service.permission.ResourceRESTServiceAccess;
import io.radien.ms.permissionmanagement.client.entities.Permission;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.DataModelEnum;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.activeTenant.ActiveTenantDataModelManager;
import io.radien.webapp.activeTenant.ActiveTenantMandatory;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;

/**
 * Permission Interface Data Model. Class responsible for managing and maintaining
 * the data between the front end and backend for the tenant tables.
 *
 * @author Bruno Gama
 */
@Model
@SessionScoped
public class PermissionDataModel extends AbstractManager implements Serializable {

    private LazyDataModel<? extends SystemPermission> lazyModel;

    private SystemPermission selectedPermission;
    private SystemPermission previousSelectedPermission;

    @Inject
    private PermissionRESTServiceAccess service;

    @Inject
    private ActionRESTServiceAccess actionRESTServiceAccess;

    @Inject
    private ResourceRESTServiceAccess resourceRESTServiceAccess;

    @Inject
    private ActiveTenantDataModelManager activeTenantDataModelManager;

    @Inject
    private PermissionManager permissionManager;

    /**
     * Initialization of the permission data tables and models
     */
    @PostConstruct
    public void init() {
        try {
            if(activeTenantDataModelManager.isTenantActive()) {
                lazyModel = new LazyPermissionDataModel(service, actionRESTServiceAccess, resourceRESTServiceAccess);
            } else {
                redirectToHomePage();
            }
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.GENERIC_ERROR_MESSAGE.getValue()), JSFUtil.getMessage(DataModelEnum.PERMISSIONS_MESSAGE.getValue()));
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
     * Lazy Model Data Table getter method
     * @return the lazy data model
     */
    public LazyDataModel<? extends SystemPermission> getLazyModel() {
        return lazyModel;
    }

    /**
     * Lazy data model setter
     * @param lazyModel to be set
     */
    public void setLazyModel(LazyDataModel<? extends SystemPermission> lazyModel) {
        this.lazyModel = lazyModel;
    }

    /**
     * Selected permission getter method
     * @return the current selected method
     */
    public SystemPermission getSelectedPermission() {
        return selectedPermission;
    }

    /**
     * Selected permission setter method
     * @param selectedPermission to be used or updated
     */
    public void setSelectedPermission(SystemPermission selectedPermission) {
        this.selectedPermission = selectedPermission;
    }

    /**
     * Getter method for the attribute that corresponds to the previous selected permission
     * @return instance of {@link SystemPermission}
     */
    public SystemPermission getPreviousSelectedPermission() {
        return previousSelectedPermission;
    }

    /**
     * Setter method for the attribute that corresponds to the previous selected permission
     * @param previousSelectedPermission instance of {@link SystemPermission}
     */
    public void setPreviousSelectedPermission(SystemPermission previousSelectedPermission) {
        this.previousSelectedPermission = previousSelectedPermission;
    }

    /**
     * Getter method for the attribute that corresponds to the Permission Manager
     * @return instance of Permission Manager
     */
    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    /**
     * Setter method for the attribute that corresponds to the Permission Manager
     * @param permissionManager instance of Permission Manager
     */
    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    /**
     * Permission REST Service Access getter method to perform actions
     * @return the current permission rest service access
     */
    public PermissionRESTServiceAccess getService() {
        return service;
    }

    /**
     * Permission REST Service Access setter method
     * @param service to be used or set
     */
    public void setService(PermissionRESTServiceAccess service) {
        this.service = service;
    }

    /**
     * Action REST Service Access getter method to perform actions
     * @return the current action rest service access
     */
    public ActionRESTServiceAccess getActionRESTServiceAccess() {
        return actionRESTServiceAccess;
    }

    /**
     * Action REST Service Access setter method
     * @param service to be used or set
     */
    public void setActionRESTServiceAccess(ActionRESTServiceAccess service) {
        this.actionRESTServiceAccess = service;
    }

    /**
     * Resource REST Service Access getter method to perform resources
     * @return the current resource rest service access
     */
    public ResourceRESTServiceAccess getResourceRESTServiceAccess() {
        return resourceRESTServiceAccess;
    }

    /**
     * Resource REST Service Access setter method
     * @param service to be used or set
     */
    public void setResourceRESTServiceAccess(ResourceRESTServiceAccess service) {
        this.resourceRESTServiceAccess = service;
    }

    /**
     * On Row Select is a action listener in case a record is selected
     * @param event listen when row as been selected
     */
    public void onRowSelect(SelectEvent<SystemPermission> event) {
        // Logic to perform the "undo selection" or "rollback selection"
        if (previousSelectedPermission != null && event.getObject().getId().
                equals(previousSelectedPermission.getId())) {
            selectedPermission = new Permission();
            previousSelectedPermission = new Permission();
        } else {
            previousSelectedPermission = event.getObject();
        }
    }

    /**
     * Prepare a selected permission to be updated
     * @return the correct page to where the user should be redirected
     */
    @ActiveTenantMandatory
    public String editRecords() {
        try {
            if (selectedPermission != null && selectedPermission.getId() != null) {
                return permissionManager.edit(selectedPermission);
            }
            handleMessage(FacesMessage.SEVERITY_WARN, JSFUtil.getMessage(DataModelEnum.SELECT_RECORD_FIRST.getValue()));
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.SAVE_ERROR_MESSAGE.getValue()), JSFUtil.getMessage(DataModelEnum.ACTION_MESSAGE.getValue()));
        }
        return DataModelEnum.PERMISSION_MAIN_PAGE.getValue();
    }
}
