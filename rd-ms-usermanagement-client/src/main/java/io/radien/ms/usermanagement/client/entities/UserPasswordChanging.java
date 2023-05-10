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

import io.radien.api.model.user.SystemUserPasswordChanging;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Pojo bean encapsulating information regarding password changing
 * @author newton carvalho
 */
public class UserPasswordChanging implements SystemUserPasswordChanging {
    private static final String NO_LOWER_CASE_CHARACTER_ERROR = "change_password_no_lowercase_match";
    private static final String NO_UPPER_CASE_CHARACTER_ERROR = "change_password_no_uppercase_match";
    private static final String NO_NUMBER_CHARACTER_ERROR = "change_password_no_number_match";
    private static final String NO_SPECIAL_CHARACTER_ERROR = "change_password_no_special_character_match";

    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final String INSUFICIENT_LENGTH_ERROR = "change_password_insuficient_length";

    private static final long serialVersionUID = 1860536680922155716L;
    private String login;
    private String oldPassword;
    private String newPassword;

    private List<String> validationErrors;

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

    public List<String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<String> validationErrors) {
        this.validationErrors = validationErrors;
    }

    /**
     * Erase all properties
     */
    public void clear() {
        this.oldPassword = null;
        this.newPassword = null;
        this.login = null;
    }

    public boolean validatePassword() {
        boolean valid = true;
        validationErrors = new ArrayList<>();

        Pattern lowerCase = Pattern.compile("(.*[a-z].*)");
        Pattern upperCase = Pattern.compile("(.*[A-Z].*)");
        Pattern number = Pattern.compile("(.*\\d.*)");
        Pattern specialCharacter = Pattern.compile("(.*[!\"`'#%&,:;<>=@{}~_$()*+/\\\\?\\[\\]^|]+.*)");

        if(!lowerCase.matcher(newPassword).find()) {
            validationErrors.add(NO_LOWER_CASE_CHARACTER_ERROR);
            valid = false;
        }
        if(!upperCase.matcher(newPassword).find()) {
            validationErrors.add(NO_UPPER_CASE_CHARACTER_ERROR);
            valid = false;
        }
        if(!number.matcher(newPassword).find()) {
            validationErrors.add(NO_NUMBER_CHARACTER_ERROR);
            valid = false;
        }
        if(!specialCharacter.matcher(newPassword).find()) {
            validationErrors.add(NO_SPECIAL_CHARACTER_ERROR);
            valid = false;
        }
        if(newPassword.trim().length() < PASSWORD_MIN_LENGTH) {
            validationErrors.add(INSUFICIENT_LENGTH_ERROR);
            valid = false;
        }
        return valid;
    }

}
