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
package io.radien.webapp.resource;

import io.radien.api.model.permission.SystemResource;
import io.radien.api.service.permission.ResourceRESTServiceAccess;
import io.radien.ms.permissionmanagement.client.entities.Resource;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.JSFUtil;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;

/**
 * @author Newton Carvalho
 */
@Model
@RequestScoped
public class ResourceManager extends AbstractManager {

    @Inject
    private ResourceRESTServiceAccess resourceRESTServiceAccess;

    protected SystemResource resource = new Resource();

    public String save(SystemResource r) {
        try {
            this.resourceRESTServiceAccess.create(r);
            handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage("rd_save_success"),
                    JSFUtil.getMessage("rd_resource"));
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_save_error"), JSFUtil.getMessage("rd_resource"));
        }
        return "resource";
}

    public String edit(SystemResource t) {
        this.setResource(t);
        return "resource";
    }

    public SystemResource getResource() {
        return resource;
    }

    public void setResource(SystemResource resource) {
        this.resource = resource;
    }
}