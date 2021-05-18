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
import io.radien.ms.permissionmanagement.client.entities.Action;
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
public class ActionDataModel extends AbstractManager implements Serializable {

    @Inject
    private ActionRESTServiceAccess service;

    private LazyDataModel<? extends SystemAction> lazyModel;

    private SystemAction selectedAction;

    private SystemAction action = new Action();

    @PostConstruct
    public void init() {
        try {
            lazyModel = new LazyActionsDataModel(service);
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_generic_error_message"), JSFUtil.getMessage("rd_actions"));
        }
    }

    public void onload() {
        try {
            init();
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_generic_error_message"), JSFUtil.getMessage("rd_actions"));
        }
    }

    public String save(SystemAction a) {
        try {
            this.service.create(a);
            handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage("rd_save_success"),
                    JSFUtil.getMessage("rd_action"));
            action = new Action();
            return "actions";
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_save_error"), JSFUtil.getMessage("rd_action"));
            return "action";
        }
    }

    public String editRecords() {
        try {
            if (selectedAction != null) {
                return "actionDetails";
            } else {
                handleMessage(FacesMessage.SEVERITY_WARN, JSFUtil.getMessage("rd_select_record_first"), JSFUtil.getMessage("rd_action"));
            }
            return "actions";
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_save_error"), JSFUtil.getMessage("rd_action"));
            return "actionDetails";
        }
    }

    public void delete(){
        try {
            if (selectedAction != null) {
                service.delete(selectedAction.getId());
                handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage("rd_delete_success"),
                        JSFUtil.getMessage("rd_action"));
            } else {
                handleMessage(FacesMessage.SEVERITY_WARN, JSFUtil.getMessage("rd_delete_select_record_first"), JSFUtil.getMessage("rd_action"));
            }
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_delete_error"), JSFUtil.getMessage("rd_action"));
        }

        action = new Action();
        selectedAction = null;
    }

    public String returnHome() {
        try {
            action = new Action();
            selectedAction = null;
            return "actions";
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_delete_error"), JSFUtil.getMessage("rd_action"));
            return "actions";
        }
    }

    public String edit(SystemAction a) {
        try {
            if(a != null && !a.getName().isEmpty()) {
                this.service.create(a);
                action = new Action();
                selectedAction = null;
                return "actions";
            } else {
                handleMessage(FacesMessage.SEVERITY_ERROR, JSFUtil.getMessage("rd_action_name_is_mandatory"), JSFUtil.getMessage("rd_action"));
                return "actionDetails";
            }
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_edit_error"), JSFUtil.getMessage("rd_action"));
            return "actionDetails";
        }
    }

    public SystemAction getAction() {
        return action;
    }

    public void setAction(SystemAction action) {
        this.action = action;
    }

    public LazyDataModel<? extends SystemAction> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<? extends SystemAction> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public SystemAction getSelectedAction() {
        return selectedAction;
    }

    public void setSelectedAction(SystemAction selectedAction) {
        this.selectedAction = selectedAction;
    }

    public ActionRESTServiceAccess getService() {
        return service;
    }

    public void setService(ActionRESTServiceAccess service) {
        this.service = service;
    }

    public void onRowSelect(SelectEvent<SystemAction> event) {
        this.selectedAction = event.getObject();
        FacesMessage msg = new FacesMessage(JSFUtil.getMessage("rd_action_selected"), String.valueOf(event.getObject().getId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}

