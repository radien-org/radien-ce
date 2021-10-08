/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.ms.openid.service;

import io.radien.ms.openid.entities.Principal;

import javax.json.JsonObject;
import javax.json.JsonString;
import java.util.Date;

/**
 * An utilitarian class to create instance of Principal class
 * @author Newton Carvalho
 */
public class PrincipalFactory {

    /**
     * Create a user principal with already predefine fields.
     *
     * @param firstname user first name
     * @param lastname user last name
     * @param logon user logon
     * @param sub user subject
     * @param createdUser the user which has created the user
     * @param email user email
     * @return a User object to be used
     */
    public static Principal create(String firstname, String lastname, String logon, String sub, String email,
                                   Long createdUser){
        Principal principal = new Principal();
        principal.setFirstname(firstname);
        principal.setLastname(lastname);
        principal.setLogon(logon);
        principal.setEnabled(true);
        principal.setSub(sub);
        principal.setCreateUser(createdUser);
        Date now = new Date();
        principal.setLastUpdate(now);
        principal.setCreateDate(now);
        principal.setUserEmail(email);
        return principal;
    }

    /**
     * Convert jsonObject in Principal
     *
     * @param jsonObject to be converted
     * @return Principal to be used
     */
    public static Principal convert(JsonObject jsonObject) {

        String givenName = getString(jsonObject,"given_name");
        String familyName = getString(jsonObject,"family_name");
        String userName = getString(jsonObject,"preferred_username");
        String email = getString(jsonObject,"email");
        String sub = getString(jsonObject,"sub");
        return create(givenName, familyName, userName, sub, email, -1L);
    }

    /**
     * Get string from jsonObject
     *
     * @param jsonObject to fetch the string
     * @param fieldName from string value we want to fetch
     * @return String with value or null if non-existent
     */
    private static String getString(JsonObject jsonObject, String fieldName){
        if(jsonObject!=null) {
            Object value = jsonObject.get(fieldName);
            if (value != null) {
                return ((JsonString) value).getString();
            }
        }
        return null;
    }
}
