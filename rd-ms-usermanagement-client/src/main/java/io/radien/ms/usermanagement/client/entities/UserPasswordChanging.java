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
import org.apache.commons.codec.binary.Base64;

import static io.radien.api.SystemVariables.CONFIRM_NEW_PASSWORD;
import static io.radien.api.SystemVariables.NEW_PASSWORD;
import static io.radien.api.SystemVariables.OLD_PASSWORD;
import static io.radien.exception.GenericErrorCodeMessage.INVALID_VALUE_FOR_PARAMETER;

/**
 * Pojo bean encapsulating information regarding password changing
 * @author newton carvalho
 */
public class UserPasswordChanging implements Serializable {

    private static final long serialVersionUID = 1860536680922155716L;
    private String login;
    private String oldPassword;
    private String newPassword;
    private String confirmNewPassword;

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
        if (oldPassword != null) {
            return new String(Base64.decodeBase64(oldPassword));
        }
        return null;
    }

    /**
     * Setter for old password property
     * @param oldPassword String that represents old password property
     */
    public void setOldPassword(String oldPassword) {
        if (oldPassword == null) {
            throw new IllegalArgumentException(INVALID_VALUE_FOR_PARAMETER.
                    toString(OLD_PASSWORD.getLabel()));
        }
        this.oldPassword = Base64.encodeBase64String(oldPassword.getBytes());
    }

    /**
     * Getter for new password property
     * @return String that represents new password property
     */
    public String getNewPassword() {
        if (newPassword != null) {
            return new String(Base64.decodeBase64(newPassword));
        }
        return null;
    }

    /**
     * Setter for new password property
     * @param newPassword String that represents new password property
     */
    public void setNewPassword(String newPassword) {
        if (newPassword == null) {
            throw new IllegalArgumentException(INVALID_VALUE_FOR_PARAMETER.
                    toString(NEW_PASSWORD.getLabel()));
        }
        this.newPassword = Base64.encodeBase64String(newPassword.getBytes());
    }


    /**
     * Getter for confirm new password property
     * @return String that represents confirm new password property
     */
    public String getConfirmNewPassword() {
        if (confirmNewPassword != null) {
            return new String(Base64.decodeBase64(confirmNewPassword));
        }
        return null;
    }

    /**
     * Setter for confirm new password property
     * @param confirmNewPassword String that represents confirm new password property
     */
    public void setConfirmNewPassword(String confirmNewPassword) {
        if (confirmNewPassword == null) {
            throw new IllegalArgumentException(INVALID_VALUE_FOR_PARAMETER.
                    toString(CONFIRM_NEW_PASSWORD.getLabel()));
        }
        this.confirmNewPassword = Base64.encodeBase64String(confirmNewPassword.getBytes());
    }
}
