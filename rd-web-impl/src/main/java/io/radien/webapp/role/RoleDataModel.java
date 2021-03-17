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
package io.radien.webapp.role;

import io.radien.api.model.role.SystemRole;
import io.radien.api.service.role.RoleRESTServiceAccess;
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
public class RoleDataModel implements Serializable {

    private LazyDataModel<? extends SystemRole> lazyModel;

    private SystemRole selectedRole;

    @Inject
    private RoleRESTServiceAccess service;

    @PostConstruct
    public void init() {
        lazyModel = new LazyRoleDataModel(service);
    }

    public void onload() {
        init();
    }

    public LazyDataModel<? extends SystemRole> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<? extends SystemRole> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public SystemRole getSelectedRole() {
        return selectedRole;
    }

    public void setSelectedRole(SystemRole selectedRole) {
        this.selectedRole = selectedRole;
    }

    public RoleRESTServiceAccess getService() {
        return service;
    }

    public void setService(RoleRESTServiceAccess service) {
        this.service = service;
    }

    public void onRowSelect(SelectEvent<SystemRole> event) {
        FacesMessage msg = new FacesMessage("Customer Selected", String.valueOf(event.getObject().getId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}