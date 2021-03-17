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
 * See the License for the specific language governing actions and
 * limitations under the License.
 */
package io.radien.webapp.tenant;

import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.tenantmanagement.client.entities.Tenant;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.JSFUtil;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import java.text.MessageFormat;

/**
 * @author Newton Carvalho
 */
@Model
@RequestScoped
public class TenantManager extends AbstractManager {

    @Inject
    private TenantRESTServiceAccess tenantRESTServiceAccess;

    protected SystemTenant tenant = new Tenant();

    public String save(SystemTenant r) {
        try {
            if (r.getId() == null) {
                this.tenantRESTServiceAccess.create(r);
            } else {
               this.tenantRESTServiceAccess.update(r);
            }
            handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage("rd_save_success"),
                    JSFUtil.getMessage("rd_tenant"));
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_save_error"), JSFUtil.getMessage("rd_tenant"));
        }
        return "tenant";
}

    public String edit(SystemTenant t) {
        this.setTenant(t);
        return "tenant";
    }

    //TODO: Using just for testing purposes  (edit/update). Will be removed once datatable with lazy is finished
    public String retrieveAndEditTenant() throws Exception {
        SystemTenant t = this.tenantRESTServiceAccess.getTenantById(this.tenant.getId()).
                orElseThrow(() -> new SystemException(MessageFormat.format(JSFUtil.getMessage(
                        "rd_tenant_not_found"), this.tenant.getId())));
        return edit(t);
    }

    public SystemTenant getTenant() {
        return tenant;
    }

    public void setTenant(SystemTenant tenant) {
        this.tenant = tenant;
    }
}