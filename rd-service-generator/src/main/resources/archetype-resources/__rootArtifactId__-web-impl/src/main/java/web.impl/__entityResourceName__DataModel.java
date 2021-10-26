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
package ${package}.web.impl;

import ${package}.api.model.System${entityResourceName};
import ${package}.api.service.${entityResourceName}RESTServiceAccess;

import ${package}.ms.client.entities.${entityResourceName};
import ${package}.web.impl.AbstractManager;
import ${package}.webapp.JSFUtil;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import javax.inject.Inject;

import java.io.Serializable;

/**
 *
 * @author Rajesh Gavvala
 */
@Model
@SessionScoped
public class ${entityResourceName}DataModel extends AbstractManager implements Serializable {
    private static final long serialVersionUID = 6812608123262000035L;
    private static final Logger log = LoggerFactory.getLogger(${entityResourceName}DataModel.class);

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
                service.save(update${entityResourceName});
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
                handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage("rd_delete_success"), JSFUtil.getMessage("rd_entity"));
            }
        }catch (Exception e){
            handleError(e, JSFUtil.getMessage("rd_delete_error"), JSFUtil.getMessage("rd_entity"));
        }
    }

    public String create${entityResourceName}() {
        return "pretty:createEntity";
    }

    public String edit${entityResourceName}() {
        if(selected${entityResourceName} != null) {
            return "pretty:editEntity";
        }
        return "pretty:entities";
    }

    public String returnHome() {
        ${entityResourceName.toLowerCase()} = new ${entityResourceName}();
        selected${entityResourceName}=null;
        return "pretty:entities";
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
