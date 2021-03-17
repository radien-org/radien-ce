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

package io.radien.webapp.linkedAuthorization;

import io.radien.api.model.linked.authorization.SystemLinkedAuthorization;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.service.linked.authorization.LinkedAuthorizationRESTServiceAccess;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.text.ParseException;

/**
 * @author Bruno Gama
 */
@Model
@SessionScoped
public class LinkedAuthorizationDataModel implements Serializable {


    private LazyDataModel<? extends SystemLinkedAuthorization> lazyModel;

    private SystemLinkedAuthorization selectedLinkedAuthorization;

    @Inject
    private LinkedAuthorizationRESTServiceAccess service;

    @PostConstruct
    public void init() throws MalformedURLException, ParseException {
        lazyModel = new LazyLinkedAuthorizationDataModel(service);
    }

    public void onload() throws MalformedURLException, ParseException {
        init();
    }

    public LazyDataModel<? extends SystemLinkedAuthorization> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<? extends SystemLinkedAuthorization> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public SystemLinkedAuthorization getSelectedLinkedAuthorization() {
        return selectedLinkedAuthorization;
    }

    public void setSelectedLinkedAuthorization(SystemLinkedAuthorization selectedLinkedAuthorization) {
        this.selectedLinkedAuthorization = selectedLinkedAuthorization;
    }

    public LinkedAuthorizationRESTServiceAccess getService() {
        return service;
    }

    public void setService(LinkedAuthorizationRESTServiceAccess service) {
        this.service = service;
    }

    public void onRowSelect(SelectEvent<SystemTenant> event) {
        FacesMessage msg = new FacesMessage("Customer Selected", String.valueOf(event.getObject().getId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
