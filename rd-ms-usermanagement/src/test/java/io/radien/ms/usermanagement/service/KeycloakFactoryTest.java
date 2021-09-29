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
package io.radien.ms.usermanagement.service;

import io.radien.api.model.user.SystemUser;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.entities.UserEntity;
import io.radien.ms.usermanagement.legacy.UserFactory;
import javax.inject.Inject;
import javax.json.Json;
import kong.unirest.Unirest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Bruno Gama
 **/
public class KeycloakFactoryTest {

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test method for {@link KeycloakFactory#convertToUserRepresentation(SystemUser)}
     */
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

    /**
     * Test method for {@link KeycloakFactory#convertUpdateEmailToUserRepresentation(String, boolean)}
     */
    @Test
    public void convertUpdateEmailToUserRepresentation() {
        SystemUser user = UserFactory.create("firstName", "lastName",
                "logon", "sub", "email@email.com", 2L);

        UserRepresentation representations = KeycloakFactory.convertUpdateEmailToUserRepresentation(user.getUserEmail(), true);
        representations.setEmailVerified(false);
        assertEquals(user.getUserEmail(), representations.getEmail());
        assertFalse(representations.isEmailVerified());
    }
}