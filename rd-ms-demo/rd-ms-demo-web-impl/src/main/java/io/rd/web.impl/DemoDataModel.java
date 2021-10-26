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
package io.rd.web.impl;

import io.rd.api.model.SystemDemo;
import io.rd.api.service.DemoRESTServiceAccess;

import io.rd.ms.client.entities.Demo;
import io.rd.webapp.JSFDemoUtil;
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
 *
 * @author Rajesh Gavvala
 */
@Model
@SessionScoped
public class DemoDataModel extends AbstractManager implements Serializable {
    private static final long serialVersionUID = 6812608123262000035L;

    private static final String URL_CREATE_ENTITY = "pretty:createEntity";
    private static final String URL_EDIT_ENTITY = "pretty:editEntity";
    private static final String URL_ENTITY = "pretty:entities";


    @Inject
    private DemoRESTServiceAccess service;

    private LazyDataModel<? extends SystemDemo> lazyDemoDataModel;
    private SystemDemo selectedDemo;
    private SystemDemo demo = new Demo();

    @PostConstruct
    public void init() {
        lazyDemoDataModel = new LazyDemoDataModel(service);
    }

    public void onload() {
        init();
    }

    public LazyDataModel<? extends SystemDemo> getLazyDemoDataModel() {
        return lazyDemoDataModel;
    }

    public void setLazyDemoDataModel(LazyDataModel<? extends SystemDemo> lazyDemoDataModel) {
        this.lazyDemoDataModel = lazyDemoDataModel;
    }

    public SystemDemo getSelectedDemo() {
        return selectedDemo;
    }

    public void setSelectedDemo(SystemDemo selectedDemo) {
        this.selectedDemo = selectedDemo;
    }

    public DemoRESTServiceAccess getService() {
        return service;
    }

    public void setService(DemoRESTServiceAccess service) {
        this.service = service;
    }

    public void createDemo(SystemDemo createDemo){
        try{
            if(createDemo != null){
                service.save(createDemo);
                handleMessage(FacesMessage.SEVERITY_INFO, JSFDemoUtil.getMessage("rd_save_success"), JSFDemoUtil.getMessage("rd_entity"));
            }
        }catch (Exception e){
            handleError(e, JSFDemoUtil.getMessage("rd_save_error"), JSFDemoUtil.getMessage("rd_entity"));
        }
    }

    public void updateDemo(SystemDemo updateDemo){
        try{
            if(updateDemo != null){
                service.save(updateDemo);
                handleMessage(FacesMessage.SEVERITY_INFO, JSFDemoUtil.getMessage("rd_edit_success"), JSFDemoUtil.getMessage("rd_entity"));
            }
        }catch (Exception e){
            handleError(e, JSFDemoUtil.getMessage("rd_edit_error"), JSFDemoUtil.getMessage("rd_entity"));
        }
    }

    public void deleteDemo(){
        try{
            if(selectedDemo != null){
                service.deleteDemo(selectedDemo.getId());
                handleMessage(FacesMessage.SEVERITY_INFO, JSFDemoUtil.getMessage("rd_delete_success"), JSFDemoUtil.getMessage("rd_entity"));
            }
        }catch (Exception e){
            handleError(e, JSFDemoUtil.getMessage("rd_delete_error"), JSFDemoUtil.getMessage("rd_entity"));
        }
    }

    public String createDemo() {
        return URL_CREATE_ENTITY;
    }

    public String editDemo() {
        if(selectedDemo != null) {
            return URL_EDIT_ENTITY;
        }
        return URL_ENTITY;
    }

    public String returnHome() {
        demo = new Demo();
        selectedDemo=null;
        return URL_ENTITY;
    }

    public SystemDemo getDemo() {
        return demo;
    }

    public void setDemo(SystemDemo demo) {
        this.demo = demo;
    }

    public void onRowSelect(SelectEvent<SystemDemo> event) {
        this.selectedDemo=event.getObject();
        FacesMessage msg = new FacesMessage("rd_entity_selected", String.valueOf(event.getObject()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

}
