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

/**
 * @author Bruno Gama
 */
@Model
@SessionScoped
public class PermissionDataModel extends AbstractManager implements Serializable {

    private LazyDataModel<? extends SystemPermission> lazyModel;

    private SystemPermission selectedPermission;

    @Inject
    private PermissionRESTServiceAccess service;

    @PostConstruct
    public void init() {
        try {
            lazyModel = new LazyPermissionDataModel(service);
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_generic_error_message"), JSFUtil.getMessage("rd_permissions"));
        }
    }

    public void onload() {
        try {
            init();
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_generic_error_message"), JSFUtil.getMessage("rd_permissions"));
        }
    }

    public LazyDataModel<? extends SystemPermission> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<? extends SystemPermission> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public SystemPermission getSelectedPermission() {
        return selectedPermission;
    }

    public void setSelectedPermission(SystemPermission selectedPermission) {
        this.selectedPermission = selectedPermission;
    }

    public PermissionRESTServiceAccess getService() {
        return service;
    }

    public void setService(PermissionRESTServiceAccess service) {
        this.service = service;
    }

    public void onRowSelect(SelectEvent<SystemPermission> event) {
        FacesMessage msg = new FacesMessage("Customer Selected", String.valueOf(event.getObject().getId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
