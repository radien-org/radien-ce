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
package io.radien.webapp.resource;

import io.radien.api.model.permission.SystemResource;
import io.radien.api.service.permission.ResourceRESTServiceAccess;
import io.radien.ms.permissionmanagement.client.entities.Resource;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.JSFUtil;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Bruno Gama
 */
@Model
@ApplicationScoped
public class ResourceDataModel extends AbstractManager implements Serializable {

    @Inject
    private ResourceRESTServiceAccess service;

    private LazyDataModel<? extends SystemResource> lazyModel;

    private SystemResource selectedResource;

    private SystemResource resource = new Resource();

    @PostConstruct
    public void init() {
        try {
            lazyModel = new LazyResourcesDataModel(service);
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_generic_error_message"), JSFUtil.getMessage("rd_resource"));
        }
    }

    public void onload() {
        try {
            init();
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_generic_error_message"), JSFUtil.getMessage("rd_resource"));
        }
    }

    public String save(SystemResource r) {
        try {
            this.service.create(r);
            handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage("rd_save_success"),
                    JSFUtil.getMessage("rd_resource"));
            resource = new Resource();
            return "resources";
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_save_error"), JSFUtil.getMessage("rd_resource"));
            return "resource";
        }
    }

    public String editRecords() {
        try {
            if (selectedResource != null) {
                return "resourceDetails";
            } else {
                handleMessage(FacesMessage.SEVERITY_WARN, JSFUtil.getMessage("rd_select_record_first"), JSFUtil.getMessage("rd_resource"));
            }
            return "resources";
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_save_error"), JSFUtil.getMessage("rd_resource"));
            return "resourceDetails";
        }
    }

    public void delete(){
        try {
            if (selectedResource != null) {
                service.delete(selectedResource.getId());
                handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage("rd_delete_success"),
                        JSFUtil.getMessage("rd_resource"));
            } else {
                handleMessage(FacesMessage.SEVERITY_WARN, JSFUtil.getMessage("rd_delete_select_record_first"), JSFUtil.getMessage("rd_resource"));
            }
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_delete_error"), JSFUtil.getMessage("rd_resource"));
        }

        resource = new Resource();
        selectedResource = null;
    }

    public String returnHome() {
        try {
            resource = new Resource();
            selectedResource = null;
            return "resources";
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_delete_error"), JSFUtil.getMessage("rd_resource"));
            return "resources";
        }
    }

    public String edit(SystemResource r) {
        try {
            if(r != null && !r.getName().isEmpty()) {
                this.service.create(r);
                resource = new Resource();
                selectedResource = null;
                return "resources";
            } else {
                handleMessage(FacesMessage.SEVERITY_ERROR, JSFUtil.getMessage("rd_resource_name_is_mandatory"), JSFUtil.getMessage("rd_resource"));
                return "resourceDetails";
            }
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_edit_error"), JSFUtil.getMessage("rd_resource"));
            return "resourceDetails";
        }
    }

    public SystemResource getResource() {
        return resource;
    }

    public void setResource(SystemResource resource) {
        this.resource = resource;
    }

    public LazyDataModel<? extends SystemResource> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<? extends SystemResource> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public SystemResource getSelectedResource() {
        return selectedResource;
    }

    public void setSelectedResource(SystemResource selectedResource) {
        this.selectedResource = selectedResource;
    }

    public ResourceRESTServiceAccess getService() {
        return service;
    }

    public void setService(ResourceRESTServiceAccess service) {
        this.service = service;
    }

    public void onRowSelect(SelectEvent<SystemResource> event) {
        this.selectedResource = event.getObject();
        FacesMessage msg = new FacesMessage("Action Selected", String.valueOf(event.getObject().getId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}

