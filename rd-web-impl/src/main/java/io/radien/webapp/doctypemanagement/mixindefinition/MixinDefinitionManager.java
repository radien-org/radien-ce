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
package io.radien.webapp.doctypemanagement.mixindefinition;

import io.radien.api.entity.Page;
import io.radien.api.model.docmanagement.mixindefinition.SystemMixinDefinition;
import io.radien.api.model.docmanagement.propertydefinition.SystemPropertyDefinition;
import io.radien.api.service.docmanagement.mixindefinition.MixinDefinitionRESTServiceAccess;
import io.radien.api.service.docmanagement.propertydefinition.PropertyDefinitionRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.doctypemanagement.client.entities.MixinDefinitionDTO;
import io.radien.ms.doctypemanagement.client.entities.PropertyDefinition;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.JSFUtil;
import io.radien.webapp.doctypemanagement.propertydefinition.PropertyTypes;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.LazyDataModel;

@Model
@SessionScoped
public class MixinDefinitionManager extends AbstractManager implements Serializable {

    private static final String URL_CREATE_ENTITY = "pretty:mixinDefinitionCreate";
    private static final String URL_EDIT_ENTITY = "pretty:mixinDefinitionEdit";
    private static final String URL_ENTITY = "pretty:mixinDefinitions";

    private DualListModel<SystemPropertyDefinition> pickList;

    @Inject
    private MixinDefinitionRESTServiceAccess service;
    @Inject
    private PropertyDefinitionRESTServiceAccess propertyService;

    private LazyDataModel<SystemMixinDefinition<Long>> dataModel;
    private SystemMixinDefinition<Long> selectedMixinDefinition;
    private SystemMixinDefinition<Long> newMixinDefinition = new MixinDefinitionDTO();

    @PostConstruct
    public void init() {
        dataModel = new MixinDefinitionDataModel(service);
        selectedMixinDefinition = null;
    }

    public void onload() {
        init();
    }

    public void save(SystemMixinDefinition<Long> mixinDefinition){
        try {
            if(mixinDefinition != null) {
                mixinDefinition.setPropertyDefinitions(
                        pickList.getTarget().stream().map(SystemPropertyDefinition::getId).collect(Collectors.toList())
                );
                if(service.save(mixinDefinition)) {
                    JSFUtil.addSuccessMessage(null, "rd_save_success", mixinDefinition.getName());
                } else {
                    JSFUtil.addErrorMessage(null, "rd_save_error", mixinDefinition.getName());
                }
            }
        } catch (Exception e) {
            log.error("Error saving MixinDefinition {}", mixinDefinition.getName(), e);
            JSFUtil.addErrorMessage(null, "rd_save_error", mixinDefinition.getName());
        }
    }

    public void deleteMixinDefinition(SystemMixinDefinition<Long> mixinDefinition) {
        try {
            if(mixinDefinition != null) {
                if(service.deleteMixinDefinition(mixinDefinition.getId())) {
                    JSFUtil.addSuccessMessage(null, "rd_delete_success", mixinDefinition.getName());
                } else {
                    JSFUtil.addErrorMessage(null, "rd_delete_error", mixinDefinition.getName());
                }
            }
        } catch (Exception e) {
            log.error("Error deleting Property Definition", e);
            JSFUtil.addErrorMessage(null, "rd_delete_error", mixinDefinition.getName());
        }
    }

    public String getPropertyDefinitionNamesAsString(SystemMixinDefinition<Long> mixinDefinition) {
        try {
            return propertyService.getPropertyDefinitionNamesByIds(mixinDefinition.getPropertyDefinitions());
        } catch (SystemException e) {
            log.error("Could not retrieve definitions for {}", mixinDefinition.getName(), e);
            return "ERR";
        }
    }

    public String createMixinDefinitionRedirectURL() {
        try {
            Page<? extends SystemPropertyDefinition> sourceList = propertyService.getAll(null, 1, 2000, new ArrayList<>(), false);
            pickList = new DualListModel<>(new ArrayList<>(sourceList.getResults()), new ArrayList<>());
        } catch (SystemException e) {
            log.error("Error loading available property definitions", e);
            JSFUtil.addErrorMessage(null, "rd_propertyDefinition_error_retrieving_definitions");
        }
        return URL_CREATE_ENTITY;
    }

    public String editMixinDefinitionRedirectURL() {
        if(selectedMixinDefinition != null) {
            return URL_EDIT_ENTITY;
        }
        return URL_ENTITY;
    }

    public String returnToMixinDefinitionsRedirectURL() {
        newMixinDefinition = new MixinDefinitionDTO();
        selectedMixinDefinition = null;
        return URL_ENTITY;
    }

    public LazyDataModel<? extends SystemMixinDefinition<Long>> getDataModel() {
        return dataModel;
    }

    public void setDataModel(LazyDataModel<SystemMixinDefinition<Long>> dataModel) {
        this.dataModel = dataModel;
    }

    public SystemMixinDefinition<Long> getSelectedMixinDefinition() {
        return selectedMixinDefinition;
    }

    public void setSelectedMixinDefinition(SystemMixinDefinition<Long> selectedMixinDefinition) {
        this.selectedMixinDefinition = selectedMixinDefinition;
    }

    public SystemMixinDefinition<Long> getNewMixinDefinition() {
        return newMixinDefinition;
    }

    public void setNewMixinDefinition(SystemMixinDefinition<Long> newMixinDefinition) {
        this.newMixinDefinition = newMixinDefinition;
    }

    public DualListModel<SystemPropertyDefinition> getPickList() {
        return pickList;
    }

    public void setPickList(DualListModel<SystemPropertyDefinition> pickList) {
        this.pickList = pickList;
    }

    public void onRowSelect(SelectEvent<SystemMixinDefinition<Long>> event) {
        this.selectedMixinDefinition =event.getObject();
        FacesMessage msg = new FacesMessage("rd_entity_selected", String.valueOf(event.getObject()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

}
