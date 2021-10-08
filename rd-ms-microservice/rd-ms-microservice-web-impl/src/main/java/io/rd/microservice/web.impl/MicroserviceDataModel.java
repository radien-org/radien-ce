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
package io.rd.microservice.web.impl;

import io.rd.microservice.api.model.SystemMicroservice;
import io.rd.microservice.api.service.MicroserviceRESTServiceAccess;

import io.rd.microservice.ms.client.entities.Microservice;
import io.rd.microservice.web.impl.AbstractManager;
import io.rd.microservice.webapp.JSFUtil;
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
public class MicroserviceDataModel extends AbstractManager implements Serializable {
    private static final long serialVersionUID = 6812608123262000035L;
    private static final Logger log = LoggerFactory.getLogger(MicroserviceDataModel.class);

    @Inject
    private MicroserviceRESTServiceAccess service;

    private LazyDataModel<? extends SystemMicroservice> lazyMicroserviceDataModel;
    private SystemMicroservice selectedMicroservice;
    private SystemMicroservice microservice = new Microservice();

    @PostConstruct
    public void init() {
        lazyMicroserviceDataModel = new LazyMicroserviceDataModel(service);
    }

    public void onload() {
        init();
    }

    public LazyDataModel<? extends SystemMicroservice> getLazyMicroserviceDataModel() {
        return lazyMicroserviceDataModel;
    }

    public void setLazyMicroserviceDataModel(LazyDataModel<? extends SystemMicroservice> lazyMicroserviceDataModel) {
        this.lazyMicroserviceDataModel = lazyMicroserviceDataModel;
    }

    public SystemMicroservice getSelectedMicroservice() {
        return selectedMicroservice;
    }

    public void setSelectedMicroservice(SystemMicroservice selectedMicroservice) {
        this.selectedMicroservice = selectedMicroservice;
    }

    public MicroserviceRESTServiceAccess getService() {
        return service;
    }

    public void setService(MicroserviceRESTServiceAccess service) {
        this.service = service;
    }

    public void createMicroservice(SystemMicroservice createMicroservice){
        try{
            if(createMicroservice != null){
                service.save(createMicroservice);
                handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage("rd_save_success"), JSFUtil.getMessage("rd_entity"));
            }
        }catch (Exception e){
            handleError(e, JSFUtil.getMessage("rd_save_error"), JSFUtil.getMessage("rd_entity"));
        }
    }

    public void updateMicroservice(SystemMicroservice updateMicroservice){
        try{
            if(updateMicroservice != null){
                service.save(updateMicroservice);
                handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage("rd_edit_success"), JSFUtil.getMessage("rd_entity"));
            }
        }catch (Exception e){
            handleError(e, JSFUtil.getMessage("rd_edit_error"), JSFUtil.getMessage("rd_entity"));
        }
    }

    public void deleteMicroservice(){
        try{
            if(selectedMicroservice != null){
                service.deleteMicroservice(selectedMicroservice.getId());
                handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage("rd_delete_success"), JSFUtil.getMessage("rd_entity"));
            }
        }catch (Exception e){
            handleError(e, JSFUtil.getMessage("rd_delete_error"), JSFUtil.getMessage("rd_entity"));
        }
    }

    public String createMicroservice() {
        return "pretty:createEntity";
    }

    public String editMicroservice() {
        if(selectedMicroservice != null) {
            return "pretty:editEntity";
        }
        return "pretty:entities";
    }

    public String returnHome() {
        microservice = new Microservice();
        selectedMicroservice=null;
        return "pretty:entities";
    }

    public SystemMicroservice getMicroservice() {
        return microservice;
    }

    public void setMicroservice(SystemMicroservice microservice) {
        this.microservice = microservice;
    }

    public void onRowSelect(SelectEvent<SystemMicroservice> event) {
        this.selectedMicroservice=event.getObject();
        FacesMessage msg = new FacesMessage("rd_entity_selected", String.valueOf(event.getObject()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

}
