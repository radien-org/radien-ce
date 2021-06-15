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
package io.radien.ms.usermanagement.service;

import io.radien.api.model.user.SystemUser;
import io.radien.ms.usermanagement.legacy.UserFactory;
import org.junit.Test;
import org.keycloak.representations.idm.UserRepresentation;

import static org.junit.Assert.assertEquals;

/**
 * @author Bruno Gama
 **/
public class KeycloakFactoryTest {

    @Test
    public void convertToUserRepresentation() {
        SystemUser user = UserFactory.create("firstName", "lastName",
                "logon", "sub", "email", 2L);

        UserRepresentation representations = KeycloakFactory.convertToUserRepresentation(user);

        assertEquals(user.getFirstname(), representations.getFirstName());
        assertEquals(user.getLastname(), representations.getLastName());
        assertEquals(user.getLogon(), representations.getUsername());
        assertEquals(user.getUserEmail(), representations.getEmail());
        assertEquals(user.isEnabled(), representations.isEnabled());
    }
}