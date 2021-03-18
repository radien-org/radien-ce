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
package io.radien.webapp.user;

import io.radien.api.model.user.SystemUser;
import io.radien.api.service.user.UserRESTServiceAccess;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import javax.inject.Inject;
import javax.inject.Named;

import java.io.Serializable;


/**
 *
 * @author Rajesh Gavvala
 */

@Named("userTableUI")
@SessionScoped
public class UserManager implements Serializable {
    private static final long serialVersionUID = -4406564138942194059L;
    private static final Logger log = LoggerFactory.getLogger(UserManager.class);

    private LazyDataModel<? extends SystemUser> lazyModel;

    private SystemUser selectedUser;

    @Inject
    private UserRESTServiceAccess service;

    @PostConstruct
    public void init() {
        lazyModel = new UserDataModel(service);
    }

    public void onload() {
        init();
    }

    public LazyDataModel<? extends SystemUser> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<? extends SystemUser> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public SystemUser getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(SystemUser selectedUser) {
        this.selectedUser = selectedUser;
    }

    public UserRESTServiceAccess getService() {
        return service;
    }

    public void setService(UserRESTServiceAccess service) {
        this.service = service;
    }

    public void onRowSelect(SelectEvent<SystemUser> event) {
        FacesMessage msg = new FacesMessage("User Selected", String.valueOf(event.getObject().getId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}

