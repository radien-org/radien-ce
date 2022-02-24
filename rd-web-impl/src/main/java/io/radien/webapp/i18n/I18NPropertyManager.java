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

package io.radien.webapp.i18n;

import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.api.model.i18n.SystemI18NTranslation;
import io.radien.api.service.i18n.I18NRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.ecm.client.entities.i18n.I18NProperty;
import io.radien.ms.ecm.client.entities.i18n.I18NTranslation;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.JSFUtil;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.LazyDataModel;

@Model
@SessionScoped
public class I18NPropertyManager extends AbstractManager {
    @Inject
    private I18NRESTServiceAccess i18nService;

    private LazyDataModel<? extends SystemI18NProperty> dataModel;
    private SystemI18NProperty selectedProperty;
    private SystemI18NProperty newProperty;

    @PostConstruct
    public void init() {
        dataModel = new I18NPropertyDataModel(i18nService);
        selectedProperty = null;
    }

    public void initNewProperty() {
        newProperty = new I18NProperty();
        newProperty.setTranslations(new ArrayList<>());
    }

    public void updateProperty(SystemI18NProperty property) {
        try {
            if(i18nService.save(property)) {
                JSFUtil.addSuccessMessage(null, "rd_save_success", property.getKey());
            } else {
                JSFUtil.addErrorMessage(null, "rd_save_error", property.getKey());
            }
        } catch (SystemException e) {
            JSFUtil.addErrorMessage(null, "rd_save_error", property.getKey());
            log.error("Error saving I18NProperty {}", property.getKey(), e);
        }
    }

    public void addNewTranslationRow(SystemI18NProperty property) {
        property.getTranslations().add(new I18NTranslation());
        JSFUtil.addSuccessMessage("rd_new_row_added");
    }

    public void deleteProperty(SystemI18NProperty property) {
        JSFUtil.addErrorMessage("NOT YET IMPLEMENTED");
    }

    public void removeTranslation(SystemI18NProperty property, SystemI18NTranslation translation) {
        if(property.getTranslations().contains(translation)) {
            property.getTranslations().remove(translation);
        } else {
            JSFUtil.addErrorMessage("rd_property_not_found");
        }
    }

    public String goToCreatePage() {
        return "newProperty";
    }

    public void onRowCancel(RowEditEvent<SystemI18NProperty> event) {
        JSFUtil.addSuccessMessage("i18n_canceled_edit");
    }

    public void onRowEdit(RowEditEvent<SystemI18NProperty> event) {
        JSFUtil.addSuccessMessage("i18n_row_edited");
    }

    public void onRowExpand(ToggleEvent rowToggleSystemProperty) {
        selectedProperty = (SystemI18NProperty) rowToggleSystemProperty.getData();
    }

    public LazyDataModel<? extends SystemI18NProperty> getDataModel() {
        return dataModel;
    }

    public void setDataModel(LazyDataModel<? extends SystemI18NProperty> dataModel) {
        this.dataModel = dataModel;
    }

    public SystemI18NProperty getSelectedProperty() {
        return selectedProperty;
    }

    public void setSelectedProperty(SystemI18NProperty selectedProperty) {
        this.selectedProperty = selectedProperty;
    }

    public SystemI18NProperty getNewProperty() {
        return newProperty;
    }

    public void setNewProperty(SystemI18NProperty newProperty) {
        this.newProperty = newProperty;
    }
}
