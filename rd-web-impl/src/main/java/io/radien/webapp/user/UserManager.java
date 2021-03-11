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
import io.radien.webapp.security.UserSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;

import javax.faces.view.ViewScoped;

import javax.inject.Inject;
import javax.inject.Named;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Rajesh Gavvala
 */

@Named("userTableUI")
@ViewScoped
public class UserManager implements Serializable {
    private static final long serialVersionUID = -4406564138942194059L;
    private static final Logger log = LoggerFactory.getLogger(UserManager.class);

    @Inject
    private UserRESTServiceAccess userService;

    @Inject
    private UserSession userSession;

    private UserDataModel userDataModel;
    private List<? extends SystemUser> datasource;
    private SystemUser selectedUser;
    private boolean validationTrue;

    @PostConstruct
    public void init() {
        validationTrue = false;
        initModel();
    }

    public void initModel() {
        datasource = userService.getUserList();
        log.info("loading user dataModel");
        userDataModel = new UserDataModel(datasource);
        log.info("loaded user dataModel");
    }

    public UserDataModel getUserDataModel() {
        return userDataModel;
    }

    public void setUserDataModel(UserDataModel userDataModel) {
        this.userDataModel = userDataModel;
    }


    public String getProfile(SystemUser row) {
        userSession.setSelectedUser(row);
        return "pretty:user";
    }

    public void updateUser() {
        if (selectedUser != null) {
            userService.updateUser(selectedUser);
        }
    }

    public String initiateResetPassword() {
        if (userSession.getSelectedUser() != null) {
            validationTrue = userService.setInitiateResetPassword(userSession.getSelectedUser().getId());
            userSession.setValidationTrue(validationTrue);
        }
        return null;
    }

    public String backToDatatable() {
        setValidationTrue(false);
        userSession.setValidationTrue(validationTrue);
        return "pretty:users";
    }

    public String deleteUser(SystemUser user) {
        if (user != null) {
            validationTrue = userService.deleteUser(user.getId());
            if (validationTrue) {
                initModel();
            }
        }
        return "pretty:users";
    }

    public boolean isValidationTrue() {
        return validationTrue;
    }

    public void setValidationTrue(boolean validationTrue) {
        this.validationTrue = validationTrue;
    }


    public SystemUser getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(SystemUser selectedUser) {
        this.selectedUser = selectedUser;
    }
}

