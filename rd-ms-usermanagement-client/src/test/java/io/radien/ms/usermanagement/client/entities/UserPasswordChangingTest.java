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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Test class for {@link UserPasswordChanging}
 * @author newton carvalho
 */
public class UserPasswordChangingTest {

    /**
     * Test for constructor
     */
    @Test
    public void testConstructor() {
        UserPasswordChanging u = new UserPasswordChanging();
        assertNotNull(u);
        assertNull(u.getLogin());
        assertNull(u.getOldPassword());
        assertNull(u.getNewPassword());
    }

    /**
     * Test for setter {@link UserPasswordChanging#setOldPassword(String)}
     */
    @Test
    public void testSettingOldPassword() {
        UserPasswordChanging u = new UserPasswordChanging();
        String oldPass = "test";
        u.setOldPassword(oldPass);
        assertEquals(oldPass, u.getOldPassword());
    }


    /**
     * Test for setter {@link UserPasswordChanging#setOldPassword(String)}
     */
    @Test
    public void testSettingNewPassword() {
        UserPasswordChanging u = new UserPasswordChanging();
        String newPass = "test";
        u.setNewPassword(newPass);
        assertEquals(newPass, u.getNewPassword());
    }

    /**
     * Test for setter for {@link UserPasswordChanging#setLogin(String)}
     */
    @Test
    public void testSettingLogin() {
        UserPasswordChanging u = new UserPasswordChanging();
        String login = "test";
        u.setLogin(login);
        assertEquals(login, u.getLogin());
    }

    @Test
    public void testClear() {
        UserPasswordChanging u = new UserPasswordChanging();
        String login = "test";
        u.setLogin(login);
        assertEquals(login, u.getLogin());
        u.clear();
        assertNull(u.getLogin());
    }

    @Test
    public void testValidatePasswordFullInvalid() {
        UserPasswordChanging u = new UserPasswordChanging();
        u.setNewPassword(" ");
        assertFalse(u.validatePassword());
        assertEquals(5, u.getValidationErrors().size());
    }

    @Test
    public void testValidatePasswordValid() {
        UserPasswordChanging u = new UserPasswordChanging();
        u.setNewPassword("Abcd_123");
        assertTrue(u.validatePassword());
        assertEquals(0, u.getValidationErrors().size());
    }


}
