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

package io.radien.webapp.tenantrole;

import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.model.tenantrole.SystemTenantRoleUser;
import io.radien.api.service.tenantrole.TenantRoleRESTServiceAccess;
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
 * JSF DataModel that will allow a first page (i.e tenantroles.xhtml)
 * to exhibit a DataTable/DataGrid containing the TenantRole associations
 * previously created.
 *
 * It relies on a LazyDataModel component to perform the lazy loading
 *
 * @author Newton Carvalho
 */
@Model
@SessionScoped
public class TenantRoleAssociationDataModel extends AbstractManager implements Serializable {

    private LazyDataModel<? extends SystemTenantRole> lazyModel;

    private SystemTenantRoleUser selectedAssociation;

    @Inject
    private TenantRoleRESTServiceAccess service;

    /**
     * The most import stuff. Initializes the LazyDataModel component
     */
    @PostConstruct
    public void init() {
        try {
            lazyModel = new LazyTenantRoleAssociationDataModel(service);
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_generic_error_message"),
                    JSFUtil.getMessage("tenant_role_associations"));
        }
    }

    /**
     * Makes the LazyDataModel perform the onload event/method
     */
    public void onload() {
        init();
    }

    /**
     * Getter for the property that corresponds to the LazyDataModel component
     * @return reference for the created LazyDataMode component
     */
    public LazyDataModel<? extends SystemTenantRole> getLazyModel() {
        return lazyModel;
    }

    /**
     * Setter for the property that corresponds to the LazyDataModel component
     * @param lazyModel  reference for the created LazyDataMode component
     */
    public void setLazyModel(LazyDataModel<? extends SystemTenantRole> lazyModel) {
        this.lazyModel = lazyModel;
    }

    /**
     * Getter for the property that corresponds to the TenantRole object selected as row in a DataGrid
     * @return reference for the selected TenantRole association
     */
    public SystemTenantRoleUser getSelectedAssociation() {
        return selectedAssociation;
    }

    /**
     * Setter for the property that corresponds to the TenantRole object selected as row in a DataGrid
     * @param selectedAssociation reference for the selected TenantRole association
     */
    public void setSelectedAssociation(SystemTenantRoleUser selectedAssociation) {
        this.selectedAssociation = selectedAssociation;
    }

    /**
     * Getter for the property that corresponds to the TenantRole Rest client
     * @return instance of TenantRoleRESTServiceAccess rest client
     */
    public TenantRoleRESTServiceAccess getService() {
        return service;
    }

    /**
     * Setter for the property that corresponds to the TenantRole Rest client
     * @param service  instance of TenantRoleRESTServiceAccess rest client
     */
    public void setService(TenantRoleRESTServiceAccess service) {
        this.service = service;
    }

    /**
     * Listener that will be executed in case of a row selection event
     * @param event Event that corresponds of selecting a row (that contains a TenantRole association)
     *              presented in a DataGrid
     */
    public void onRowSelect(SelectEvent<SystemTenantRole> event) {
        FacesMessage msg = new FacesMessage(JSFUtil.getMessage("rowSelected"), String.valueOf(event.getObject().getId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
