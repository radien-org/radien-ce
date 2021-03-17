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
import io.radien.api.service.tenant.TenantRESTServiceAccess;
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
public class TenantDataModel implements Serializable {


    private LazyDataModel<? extends SystemTenant> lazyModel;

    private SystemTenant selectedTenant;

    @Inject
    private TenantRESTServiceAccess service;

    @PostConstruct
    public void init() {
        lazyModel = new LazyTenantDataModel(service);
    }

    public void onload() {
        init();
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
        FacesMessage msg = new FacesMessage("Customer Selected", String.valueOf(event.getObject().getId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
