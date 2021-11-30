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
package io.radien.api.model.user;

import java.io.Serializable;

/**
 * Contract describing information regarding password changing
 * @author newton carvalho
 */
public interface SystemUserPasswordChanging extends Serializable {

    /**
     * Getter for login property
     * @return String that represents login property
     */
    String getLogin();

    /**
     * Setter for login property
     * @param login String that represents login property
     */
    void setLogin(String login);

    /**
     * Getter for old password property
     * @return String that represents old password property
     */
    String getOldPassword();

    /**
     * Setter for old password property
     * @param oldPassword String that represents old password property
     */
    void setOldPassword(String oldPassword);

    /**
     * Getter for new password property
     * @return String that represents new password property
     */
    String getNewPassword();

    /**
     * Setter for new password property
     * @param newPassword String that represents new password property
     */
    void setNewPassword(String newPassword);
}
