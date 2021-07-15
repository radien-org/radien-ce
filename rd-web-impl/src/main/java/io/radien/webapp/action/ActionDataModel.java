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
 * Action Interface Data Model. Class responsible for managing and maintaining
 * the data between the front end and backend for the tenant tables.
 *
 * @author Bruno Gama
 */
@Model
@SessionScoped
public class ActionDataModel extends AbstractManager implements Serializable {

    @Inject
    private ActionRESTServiceAccess service;

    private LazyDataModel<? extends SystemAction> lazyModel;

    private SystemAction selectedAction;

    private SystemAction action = new Action();

    /**
     * Initialization of the action data tables and models
     */
    @PostConstruct
    @ActiveTenantMandatory
    public void init() {
        try {
            lazyModel = new LazyActionsDataModel(service);
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.GENERIC_ERROR_MESSAGE.getValue()), JSFUtil.getMessage(DataModelEnum.ACTIONS_MESSAGE.getValue()));
        }
    }

    /**
     * Data reload method
     */
    public void onload() {
        init();
    }

    /**
     * Action creation or update save method
     * @param systemAction system action to be saved
     * @return a string value to redirect the user into the correct page either send him to the table in case of success,
     * or into the edit menu in case of error
     */
    @ActiveTenantMandatory
    public String save(SystemAction systemAction) {
        try {
            this.service.create(systemAction);
            handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage(DataModelEnum.SAVE_SUCCESS_MESSAGE.getValue()),
                    JSFUtil.getMessage(DataModelEnum.ACTION_MESSAGE.getValue()));
            action = new Action();
            return DataModelEnum.ACTION_MAIN_PAGE.getValue();
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.SAVE_ERROR_MESSAGE.getValue()), JSFUtil.getMessage(DataModelEnum.ACTION_MESSAGE.getValue()));
            return DataModelEnum.ACTION_CREATION_PAGE.getValue();
        }
    }

    /**
     * Action update edit record method
     * @return the correct page to where the user should be redirected
     */
    @ActiveTenantMandatory
    public String editRecords() {
        try {
            if (selectedAction != null) {
                return DataModelEnum.ACTION_DETAIL_PAGE.getValue();
            } else {
                handleMessage(FacesMessage.SEVERITY_WARN, JSFUtil.getMessage(DataModelEnum.ERROR_SELECT_RECORD_TO_DELETE.getValue()), JSFUtil.getMessage(DataModelEnum.ACTION_MESSAGE.getValue()));
            }
            return DataModelEnum.ACTION_MAIN_PAGE.getValue();
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.SAVE_ERROR_MESSAGE.getValue()), JSFUtil.getMessage(DataModelEnum.ACTION_MESSAGE.getValue()));
            return DataModelEnum.ACTION_DETAIL_PAGE.getValue();
        }
    }

    /**
     * Action record deletion method
     */
    @ActiveTenantMandatory
    public void delete(){
        try {
            if (selectedAction != null) {
                service.delete(selectedAction.getId());
                handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage(DataModelEnum.DELETE_SUCCESS.getValue()),
                        JSFUtil.getMessage(DataModelEnum.ACTION_MESSAGE.getValue()));
            } else {
                handleMessage(FacesMessage.SEVERITY_WARN, JSFUtil.getMessage(DataModelEnum.ERROR_SELECT_RECORD_TO_DELETE.getValue()), JSFUtil.getMessage(DataModelEnum.ACTION_MESSAGE.getValue()));
            }
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.DELETE_ERROR.getValue()), JSFUtil.getMessage(DataModelEnum.ACTION_MESSAGE.getValue()));
        }

        action = new Action();
        selectedAction = null;
    }

    /**
     * Redirect user into action table page
     */
    @ActiveTenantMandatory
    public String returnToDataTableRecords() {
        try {
            action = new Action();
            selectedAction = null;
            return DataModelEnum.ACTION_MAIN_PAGE.getValue();
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.DELETE_ERROR.getValue()), JSFUtil.getMessage(DataModelEnum.ACTION_MESSAGE.getValue()));
            return DataModelEnum.ACTION_MAIN_PAGE.getValue();
        }
    }

    /**
     * Method to update and edit the given system action
     * @param systemAction to be edited and updated
     * @return a string value to where the user should be redirected
     */
    @ActiveTenantMandatory
    public String edit(SystemAction systemAction) {
        try {
            if (systemAction != null && !systemAction.getName().isEmpty()) {
                this.service.create(systemAction);
                action = new Action();
                selectedAction = null;
                return DataModelEnum.ACTION_MAIN_PAGE.getValue();
            } else {
                handleMessage(FacesMessage.SEVERITY_ERROR, JSFUtil.getMessage(DataModelEnum.ACTION_NAME_IS_MANDATORY.getValue()), JSFUtil.getMessage(DataModelEnum.ACTION_MESSAGE.getValue()));
                return DataModelEnum.ACTION_DETAIL_PAGE.getValue();
            }
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage(DataModelEnum.EDIT_ERROR_MESSAGE.getValue()), JSFUtil.getMessage(DataModelEnum.ACTION_MESSAGE.getValue()));
            return DataModelEnum.ACTION_DETAIL_PAGE.getValue();
        }
    }

    /**
     * Get model action getter method
     * @return the requested action
     */
    public SystemAction getAction() {
        return action;
    }

    /**
     * Action setter method
     * @param action to be set and used latter
     */
    public void setAction(SystemAction action) {
        this.action = action;
    }

    /**
     * Lazy Model Data Table getter method
     * @return the lazy data model
     */
    public LazyDataModel<? extends SystemAction> getLazyModel() {
        return lazyModel;
    }

    /**
     * Lazy data model setter
     * @param lazyModel to be set
     */
    public void setLazyModel(LazyDataModel<? extends SystemAction> lazyModel) {
        this.lazyModel = lazyModel;
    }

    /**
     * Selected action getter method
     * @return the actual selected action
     */
    public SystemAction getSelectedAction() {
        return selectedAction;
    }

    /**
     * Selected action setter method
     * @param selectedAction to be used or updated
     */
    public void setSelectedAction(SystemAction selectedAction) {
        this.selectedAction = selectedAction;
    }

    /**
     * Action REST Service Access getter method to perform actions
     * @return the current action rest service access
     */
    public ActionRESTServiceAccess getService() {
        return service;
    }

    /**
     * Action REST Service Access setter method
     * @param service to be used or set
     */
    public void setService(ActionRESTServiceAccess service) {
        this.service = service;
    }

    /**
     * On Row Select is a action listener in case a record is selected
     * @param event listen when row as been selected
     */
    public void onRowSelect(SelectEvent<SystemAction> event) {
        this.selectedAction = event.getObject();
        FacesMessage msg = new FacesMessage(JSFUtil.getMessage(DataModelEnum.ACTION_SELECTED.getValue()), String.valueOf(event.getObject().getId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}

