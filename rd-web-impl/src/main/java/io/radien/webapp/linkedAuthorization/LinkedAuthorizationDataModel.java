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
 * Linked Authorization Interface Data Model. Class responsible for managing and maintaining
 * the data between the front end and backend for the tenant tables.
 *
 * @author Bruno Gama
 */
@Model
@SessionScoped
public class LinkedAuthorizationDataModel extends AbstractManager implements Serializable {


    private LazyDataModel<? extends SystemLinkedAuthorization> lazyModel;

    private SystemLinkedAuthorization selectedLinkedAuthorization;

    @Inject
    private LinkedAuthorizationRESTServiceAccess service;

    /**
     * Initialization of the linked authorization data tables and models
     */
    @PostConstruct
    @ActiveTenantMandatory
    public void init() {
        try {
            lazyModel = new LazyLinkedAuthorizationDataModel(service);
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.GENERIC_ERROR_MESSAGE.getValue()), JSFUtil.getMessage(DataModelEnum.LINKED_AUTHORIZATION_MESSAGE.getValue()));
        }
    }

    /**
     * Data reload/refresh method
     */
    public void onload() {
        init();
    }

    /**
     * Lazy Model Data Table getter method
     * @return the lazy data model
     */
    public LazyDataModel<? extends SystemLinkedAuthorization> getLazyModel() {
        return lazyModel;
    }

    /**
     * Lazy data model setter
     * @param lazyModel to be set
     */
    public void setLazyModel(LazyDataModel<? extends SystemLinkedAuthorization> lazyModel) {
        this.lazyModel = lazyModel;
    }

    /**
     * Selected linked authorization getter method
     * @return the current selected method
     */
    public SystemLinkedAuthorization getSelectedLinkedAuthorization() {
        return selectedLinkedAuthorization;
    }

    /**
     * Selected linked authorization setter method
     * @param selectedLinkedAuthorization to be used or updated
     */
    public void setSelectedLinkedAuthorization(SystemLinkedAuthorization selectedLinkedAuthorization) {
        this.selectedLinkedAuthorization = selectedLinkedAuthorization;
    }

    /**
     * Linked Authorization REST Service Access getter method to perform actions
     * @return the current linked authorization rest service access
     */
    public LinkedAuthorizationRESTServiceAccess getService() {
        return service;
    }

    /**
     * Linked Authorization REST Service Access setter method
     * @param service to be used or set
     */
    public void setService(LinkedAuthorizationRESTServiceAccess service) {
        this.service = service;
    }

    /**
     * On Row Select is a action listener in case a record is selected
     * @param event listen when row as been selected
     */
    public void onRowSelect(SelectEvent<SystemTenant> event) {
        FacesMessage msg = new FacesMessage("Customer Selected", String.valueOf(event.getObject().getId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
