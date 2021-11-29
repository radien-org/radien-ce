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
package io.radien.ms.usermanagement.client.entities;

import java.io.Serializable;

/**
 * Pojo bean encapsulating information regarding password changing
 * @author newton carvalho
 */
public class UserPasswordChanging implements Serializable {

    private static final long serialVersionUID = 1860536680922155716L;
    private String login;
    private String oldPassword;
    private String newPassword;

    /**
     * Default constructor
     */
    public UserPasswordChanging(){}

    /**
     * Getter for login property
     * @return String that represents login property
     */
    public String getLogin() {
        return login;
    }

    /**
     * Setter for login property
     * @param login String that represents login property
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Getter for old password property
     * @return String that represents old password property
     */
    public String getOldPassword() {
        return oldPassword;
    }

    /**
     * Setter for old password property
     * @param oldPassword String that represents old password property
     */
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    /**
     * Getter for new password property
     * @return String that represents new password property
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * Setter for new password property
     * @param newPassword String that represents new password property
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}
