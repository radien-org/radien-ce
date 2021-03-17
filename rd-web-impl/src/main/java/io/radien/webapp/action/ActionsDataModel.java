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
package io.radien.webapp.action;

import io.radien.api.model.permission.SystemAction;
import io.radien.api.service.permission.ActionRESTServiceAccess;
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
public class ActionsDataModel implements Serializable {


    private LazyDataModel<? extends SystemAction> lazyModel;

    private SystemAction selectedActions;

    @Inject
    private ActionRESTServiceAccess service;

    @PostConstruct
    public void init() {
        lazyModel = new LazyActionsDataModel(service);
    }

    public void onload() {
        init();
    }

    public LazyDataModel<? extends SystemAction> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<? extends SystemAction> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public SystemAction getSelectedActions() {
        return selectedActions;
    }

    public void setSelectedActions(SystemAction selectedActions) {
        this.selectedActions = selectedActions;
    }

    public ActionRESTServiceAccess getService() {
        return service;
    }

    public void setService(ActionRESTServiceAccess service) {
        this.service = service;
    }

    public void onRowSelect(SelectEvent<SystemAction> event) {
        FacesMessage msg = new FacesMessage("Customer Selected", String.valueOf(event.getObject().getId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}

