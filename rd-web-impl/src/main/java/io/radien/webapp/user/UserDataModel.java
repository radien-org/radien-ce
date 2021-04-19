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
package io.radien.webapp.user;

import io.radien.api.model.user.SystemUser;
import io.radien.api.service.user.UserRESTServiceAccess;

import io.radien.ms.usermanagement.client.entities.User;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.JSFUtil;
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
public class UserDataModel extends AbstractManager implements Serializable {
    private static final long serialVersionUID = -4406564138942194060L;
    private static final Logger log = LoggerFactory.getLogger(UserDataModel.class);

    @Inject
    private UserRESTServiceAccess service;

    private LazyDataModel<? extends SystemUser> lazyUserDataModel;
    private SystemUser selectedUser;
    private SystemUser user = new User();

    @PostConstruct
    public void init() {
        lazyUserDataModel = new LazyUserDataModel(service);
    }

    public void onload() {
        init();
    }

    public LazyDataModel<? extends SystemUser> getLazyUserDataModel() {
        return lazyUserDataModel;
    }

    public void setLazyUserDataModel(LazyDataModel<? extends SystemUser> lazyUserDataModel) {
        this.lazyUserDataModel = lazyUserDataModel;
    }

    public SystemUser getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(SystemUser selectedUser) {
        this.selectedUser = selectedUser;
    }

    public UserRESTServiceAccess getService() {
        return service;
    }

    public void setService(UserRESTServiceAccess service) {
        this.service = service;
    }

    public void updateUser(SystemUser updateUser){
        try{
            if(updateUser != null){
                service.updateUser(updateUser);
                handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage("rd_edit_success"), JSFUtil.getMessage("rd_user"));
            }
        }catch (Exception e){
            handleError(e, JSFUtil.getMessage("rd_edit_error"), JSFUtil.getMessage("rd_user"));
        }
    }

    public void deleteUser(){
        try{
            if(selectedUser != null){
                service.deleteUser(selectedUser.getId());
                handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage("rd_delete_success"), JSFUtil.getMessage("rd_user"));
            }
        }catch (Exception e){
            handleError(e, JSFUtil.getMessage("rd_delete_error"), JSFUtil.getMessage("rd_user"));
        }
    }

    public void sendUpdatePasswordEmail(){
        try{
            if(selectedUser != null){
                service.sendUpdatePasswordEmail(selectedUser.getId());
                handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage("rd_send_update_password_email_success"),
                        JSFUtil.getMessage("rd_user"));
            }
        }catch (Exception e){
            handleError(e, JSFUtil.getMessage("rd_send_update_password_email_error"), JSFUtil.getMessage("rd_user"));
        }
    }

    public String editRecord() {
        if(selectedUser != null) {
            return "pretty:user";
        }
        return "pretty:users";
    }

    public String userProfile() {
        if(selectedUser != null) {
            return "pretty:userProfile";
        }
        return "pretty:users";
    }

    public String returnHome() {
        user = new User();
        selectedUser=null;
        return "pretty:users";
    }

    public void onRowSelect(SelectEvent<SystemUser> event) {
        this.selectedUser=event.getObject();
        FacesMessage msg = new FacesMessage("User Selected", String.valueOf(event.getObject().getLogon()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

}
