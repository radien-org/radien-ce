/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.webapp.doctypemanagement.propertydefinition;

import io.radien.api.model.docmanagement.propertydefinition.SystemPropertyDefinition;
import io.radien.api.service.docmanagement.propertydefinition.PropertyDefinitionRESTServiceAccess;
import io.radien.ms.doctypemanagement.client.entities.PropertyDefinition;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.JSFUtil;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;

@Model
@SessionScoped
public class PropertyDefinitionManager extends AbstractManager implements Serializable {

    private static final String URL_CREATE_ENTITY = "pretty:propertyDefinitionCreate";
    private static final String URL_EDIT_ENTITY = "pretty:propertyDefinitionEdit";
    private static final String URL_DELETE_ENTITY = "pretty:propertyDefinitionDelete";
    private static final String URL_ENTITY = "pretty:propertyDefinitions";

    @Inject
    private PropertyDefinitionRESTServiceAccess service;

    private LazyDataModel<SystemPropertyDefinition> dataModel;
    private SystemPropertyDefinition selectedPropertyDefinition;
    private SystemPropertyDefinition newPropertyType = new PropertyDefinition();

    @PostConstruct
    public void init() {
        dataModel = new PropertyDefinitionDataModel(service);
        selectedPropertyDefinition = null;
    }

    public void onload() {
        init();
    }

    public void save(SystemPropertyDefinition propertyDefinition){
        try {
            if(propertyDefinition != null) {
                if(service.save(propertyDefinition)) {
                    JSFUtil.addSuccessMessage(null, "rd_save_success", propertyDefinition.getName());
                } else {
                    JSFUtil.addErrorMessage(null, "rd_save_error", propertyDefinition.getName());
                }
            }
        } catch (Exception e) {
            log.error("Error saving PropertyDefinition {}", propertyDefinition.getName(), e);
            JSFUtil.addErrorMessage(null, "rd_save_error", propertyDefinition.getName());
        }
    }

    public void deletePropertyDefinition(SystemPropertyDefinition propertyDefinition) {
        try {
            if(propertyDefinition != null) {
                if(service.deletePropertyDefinition(propertyDefinition.getId())) {
                    JSFUtil.addSuccessMessage(null, "rd_delete_success", propertyDefinition.getName());
                } else {
                    JSFUtil.addErrorMessage(null, "rd_delete_error", propertyDefinition.getName());
                }
            }
        } catch (Exception e) {
            log.error("Error deleting Property Definition", e);
            JSFUtil.addErrorMessage(null, "rd_delete_error", propertyDefinition.getName());
        }
    }

    public String createPropertyDefinitionRedirectURL() {
        return URL_CREATE_ENTITY;
    }

    public String editPropertyDefinitionRedirectURL() {
        if(selectedPropertyDefinition != null) {
            return URL_EDIT_ENTITY;
        }
        return URL_ENTITY;
    }

    public String deletePropertyDefinitionRedirectURL() {
        if(selectedPropertyDefinition != null) {
            return URL_DELETE_ENTITY;
        }
        return URL_ENTITY;
    }

    public String returnToPropertyDefinitionsRedirectURL() {
        newPropertyType = new PropertyDefinition();
        selectedPropertyDefinition = null;
        return URL_ENTITY;
    }

    public LazyDataModel<? extends SystemPropertyDefinition> getDataModel() {
        return dataModel;
    }

    public void setDataModel(LazyDataModel<SystemPropertyDefinition> dataModel) {
        this.dataModel = dataModel;
    }

    public SystemPropertyDefinition getSelectedPropertyDefinition() {
        return selectedPropertyDefinition;
    }

    public void setSelectedPropertyDefinition(SystemPropertyDefinition selectedPropertyDefinition) {
        this.selectedPropertyDefinition = selectedPropertyDefinition;
    }

    public SystemPropertyDefinition getNewPropertyType() {
        return newPropertyType;
    }

    public void setNewPropertyType(SystemPropertyDefinition newPropertyType) {
        this.newPropertyType = newPropertyType;
    }

    public PropertyTypes[] getPropertyTypes() {
        return PropertyTypes.values();
    }

    public void onRowSelect(SelectEvent<SystemPropertyDefinition> event) {
        this.selectedPropertyDefinition =event.getObject();
        FacesMessage msg = new FacesMessage("rd_entity_selected", String.valueOf(event.getObject()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

}
