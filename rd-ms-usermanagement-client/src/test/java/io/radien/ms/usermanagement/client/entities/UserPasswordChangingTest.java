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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
        assertNull(u.getConfirmNewPassword());
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
     * Test for setter {@link UserPasswordChanging#setConfirmNewPassword(String)}
     */
    @Test
    public void testSettingConfirmNewPassword() {
        UserPasswordChanging u = new UserPasswordChanging();
        String confirmNewPass = "test";
        u.setConfirmNewPassword(confirmNewPass);
        assertEquals(confirmNewPass, u.getConfirmNewPassword());
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


}
