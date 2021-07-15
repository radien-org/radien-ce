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
import io.radien.api.service.permission.PermissionRESTServiceAccess;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.DataModelEnum;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.activeTenant.ActiveTenantMandatory;
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

    @Inject
    private PermissionRESTServiceAccess service;

    /**
     * Initialization of the permission data tables and models
     */
    @PostConstruct
    @ActiveTenantMandatory
    public void init() {
        try {
            lazyModel = new LazyPermissionDataModel(service);
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.GENERIC_ERROR_MESSAGE.getValue()), JSFUtil.getMessage(DataModelEnum.PERMISSIONS_MESSAGE.getValue()));
        }
    }

    /**
     * Data reload method
     */
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
     * On Row Select is a action listener in case a record is selected
     * @param event listen when row as been selected
     */
    public void onRowSelect(SelectEvent<SystemPermission> event) {
        FacesMessage msg = new FacesMessage("Customer Selected", String.valueOf(event.getObject().getId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
