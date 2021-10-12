/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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
package io.radien.webapp.${entityResourceName.toLowerCase()};

import io.radien.api.model.${entityResourceName.toLowerCase()}.System${entityResourceName};
import io.radien.api.service.${entityResourceName.toLowerCase()}.${entityResourceName}RESTServiceAccess;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.JSFUtil;

import ${package}.ms.client.entities.${entityResourceName};
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;

/**
 *
 * @author Rajesh Gavvala
 */
@Model
@SessionScoped
public class ${entityResourceName}DataModel extends AbstractManager implements Serializable {

    private static final String URL_CREATE_ENTITY = "pretty:${entityResourceName.toLowerCase()}Create";
    private static final String URL_EDIT_ENTITY = "pretty:${entityResourceName.toLowerCase()}Edit";
    private static final String URL_DELETE_ENTITY = "pretty:${entityResourceName.toLowerCase()}Delete";
    private static final String URL_ENTITY = "pretty:${entityResourceName.toLowerCase()}s";

    @Inject
    private ${entityResourceName}RESTServiceAccess service;

    private LazyDataModel<? extends System${entityResourceName}> lazy${entityResourceName}DataModel;
    private System${entityResourceName} selected${entityResourceName};
    private System${entityResourceName} ${entityResourceName.toLowerCase()} = new ${entityResourceName}();

    @PostConstruct
    public void init() {
        lazy${entityResourceName}DataModel = new Lazy${entityResourceName}DataModel(service);
    }

    public void onload() {
        init();
    }

    public LazyDataModel<? extends System${entityResourceName}> getLazy${entityResourceName}DataModel() {
        return lazy${entityResourceName}DataModel;
    }

    public void setLazy${entityResourceName}DataModel(LazyDataModel<? extends System${entityResourceName}> lazy${entityResourceName}DataModel) {
        this.lazy${entityResourceName}DataModel = lazy${entityResourceName}DataModel;
    }

    public System${entityResourceName} getSelected${entityResourceName}() {
        return selected${entityResourceName};
    }

    public void setSelected${entityResourceName}(System${entityResourceName} selected${entityResourceName}) {
        this.selected${entityResourceName} = selected${entityResourceName};
    }

    public ${entityResourceName}RESTServiceAccess getService() {
        return service;
    }

    public void setService(${entityResourceName}RESTServiceAccess service) {
        this.service = service;
    }

    public void create${entityResourceName}(System${entityResourceName} create${entityResourceName}){
        try{
            if(create${entityResourceName} != null){
                service.save(create${entityResourceName});
                handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage("rd_save_success"), JSFUtil.getMessage("rd_entity"));
            }
        }catch (Exception e){
            handleError(e, JSFUtil.getMessage("rd_save_error"), JSFUtil.getMessage("rd_entity"));
        }
    }

    public void update${entityResourceName}(System${entityResourceName} update${entityResourceName}){
        try{
            if(update${entityResourceName} != null){
                service.update${entityResourceName}(update${entityResourceName});
                handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage("rd_edit_success"), JSFUtil.getMessage("rd_entity"));
            }
        }catch (Exception e){
            handleError(e, JSFUtil.getMessage("rd_edit_error"), JSFUtil.getMessage("rd_entity"));
        }
    }

    public void delete${entityResourceName}(){
        try{
            if(selected${entityResourceName} != null){
                service.delete${entityResourceName}(selected${entityResourceName}.getId());
                handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage("rd_delete_success"),
                        JSFUtil.getMessage("rd_entity"));
            }
        }catch (Exception e){
            handleError(e, JSFUtil.getMessage("rd_delete_error"), JSFUtil.getMessage("rd_entity"));
        }
    }

    public String create${entityResourceName}RedirectURL() {
        if(selected${entityResourceName} == null) {
        return URL_CREATE_ENTITY;
        }
        return URL_ENTITY;
    }

    public String edit${entityResourceName}RedirectURL() {
        if(selected${entityResourceName} != null) {
            return URL_EDIT_ENTITY;
        }
        return URL_ENTITY;
    }

    public String delete${entityResourceName}RedirectURL() {
        if(selected${entityResourceName} != null) {
            return URL_DELETE_ENTITY;
        }
        return URL_ENTITY;
    }

    public String returnTo${entityResourceName}sRedirectURL() {
        ${entityResourceName.toLowerCase()} = new ${entityResourceName}();
        selected${entityResourceName}=null;
        return URL_ENTITY;
    }

    public System${entityResourceName} get${entityResourceName}() {
        return ${entityResourceName.toLowerCase()};
    }

    public void set${entityResourceName}(System${entityResourceName} ${entityResourceName.toLowerCase()}) {
        this.${entityResourceName.toLowerCase()} = ${entityResourceName.toLowerCase()};
    }

    public void onRowSelect(SelectEvent<System${entityResourceName}> event) {
        this.selected${entityResourceName}=event.getObject();
        FacesMessage msg = new FacesMessage("rd_entity_selected", String.valueOf(event.getObject()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

}
