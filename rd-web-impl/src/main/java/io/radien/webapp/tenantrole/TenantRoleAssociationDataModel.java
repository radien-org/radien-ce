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

import io.radien.api.model.tenant.SystemTenant;
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
 * @author Newton Carvalho
 */
@Model
@SessionScoped
public class TenantRoleAssociationDataModel extends AbstractManager implements Serializable {


    private LazyDataModel<? extends SystemTenantRole> lazyModel;

    private SystemTenantRoleUser selectedAssociation;

    @Inject
    private TenantRoleRESTServiceAccess service;

    @PostConstruct
    public void init() {
        try {
            lazyModel = new LazyTenantRoleAssociationDataModel(service);
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_generic_error_message"),
                    JSFUtil.getMessage("tenant_role_associations"));
        }
    }

    public void onload() {
        try {
            init();
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_generic_error_message"),
                    JSFUtil.getMessage("tenant_role_associations"));
        }
    }

    public LazyDataModel<? extends SystemTenantRole> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<? extends SystemTenantRole> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public SystemTenantRoleUser getSelectedAssociation() {
        return selectedAssociation;
    }

    public void setSelectedAssociation(SystemTenantRoleUser selectedAssociation) {
        this.selectedAssociation = selectedAssociation;
    }

    public TenantRoleRESTServiceAccess getService() {
        return service;
    }

    public void setService(TenantRoleRESTServiceAccess service) {
        this.service = service;
    }

    public void onRowSelect(SelectEvent<SystemTenant> event) {
        FacesMessage msg = new FacesMessage("Customer Selected", String.valueOf(event.getObject().getId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
