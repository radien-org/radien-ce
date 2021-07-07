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
package io.radien.webapp.role;

import io.radien.api.model.role.SystemRole;
import io.radien.api.service.role.RoleRESTServiceAccess;
import io.radien.ms.rolemanagement.client.entities.Role;
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
 * @author Bruno Gama
 */
@Model
@SessionScoped
public class RoleDataModel extends AbstractManager implements Serializable {

    @Inject
    private RoleRESTServiceAccess service;

    private LazyDataModel<? extends SystemRole> lazyModel;

    private SystemRole selectedRole;

    private SystemRole role = new Role();

    @PostConstruct
    public void init() {
        try {
            lazyModel = new LazyRoleDataModel(service);
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_generic_error_message"), JSFUtil.getMessage("rd_role"));
        }
    }

    public void onload() {
        try {
            init();
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_generic_error_message"), JSFUtil.getMessage("rd_role"));
        }
    }

    public String save(SystemRole r) {
        try {
            this.service.create(r);
            handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage("rd_save_success"),
                    JSFUtil.getMessage("rd_role"));
            role = new Role();
            return "roles";
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_save_error"), JSFUtil.getMessage("rd_role"));
            return "role";
        }
    }

    public String editRecords() {
        try {
            if (selectedRole != null) {
                return "roleDetails";
            } else {
                handleMessage(FacesMessage.SEVERITY_WARN, JSFUtil.getMessage("rd_select_record_first"), JSFUtil.getMessage("rd_role"));
            }
            return "roles";
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_save_error"), JSFUtil.getMessage("rd_role"));
            return "roleDetails";
        }
    }

    public void delete(){
        try {
            if (selectedRole != null) {
                service.delete(selectedRole.getId());
                handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage("rd_delete_success"),
                        JSFUtil.getMessage("rd_role"));
            } else {
                handleMessage(FacesMessage.SEVERITY_WARN, JSFUtil.getMessage("rd_delete_select_record_first"), JSFUtil.getMessage("rd_role"));
            }
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_delete_error"), JSFUtil.getMessage("rd_role"));
        }

        role = new Role();
        selectedRole = null;
    }

    public String returnHome() {
        try {
            role = new Role();
            selectedRole = null;
            return "roles";
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_delete_error"), JSFUtil.getMessage("rd_role"));
            return "roles";
        }
    }

    public String edit(SystemRole r) {
        try {
            if(r != null && !r.getName().isEmpty()) {
                this.service.create(r);
                role = new Role();
                selectedRole = null;
                return "roles";
            } else {
                handleMessage(FacesMessage.SEVERITY_ERROR, JSFUtil.getMessage("rd_role_name_is_mandatory"), JSFUtil.getMessage("rd_role"));
                return "roleDetails";
            }
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_edit_error"), JSFUtil.getMessage("rd_role"));
            return "roleDetails";
        }
    }

    public SystemRole getRole() {
        return role;
    }

    public void setRole(SystemRole role) {
        this.role = role;
    }

    public LazyDataModel<? extends SystemRole> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<? extends SystemRole> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public SystemRole getSelectedRole() {
        return selectedRole;
    }

    public void setSelectedRole(SystemRole selectedRole) {
        this.selectedRole = selectedRole;
    }

    public RoleRESTServiceAccess getService() {
        return service;
    }

    public void setService(RoleRESTServiceAccess service) {
        this.service = service;
    }

    public void onRowSelect(SelectEvent<SystemRole> event) {
        this.selectedRole = event.getObject();
        FacesMessage msg = new FacesMessage(JSFUtil.getMessage("rd_role_selected"), String.valueOf(event.getObject().getId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}