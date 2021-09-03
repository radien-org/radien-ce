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
package io.radien.ms.usermanagement.client.entities;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class UserTest {

    private User user = new User();
    private Date terminationDate = new Date();

    public UserTest() {
        user.setId(2L);
        user.setLogon("testLogon");
        user.setUserEmail("testEmail@testEmail.pt");
        user.setFirstname("testFirstName");
        user.setLastname("testLastName");
        user.setSub("42a64cb0-4600-11eb-b378-0242ac130002");
        user.setTerminationDate(terminationDate);
        user.setEnabled(true);
    }

    @Test
    public void getId() {
        assertSame(2L, user.getId());
    }

    @Test
    public void setId() {
        user.setId(3L);
        assertSame(3L, user.getId());
    }

    @Test
    public void getLogon() {
        assertEquals("testLogon", user.getLogon());
    }

    @Test
    public void setLogon() {
        user.setLogon("testLogonSetter");
        assertEquals("testLogonSetter", user.getLogon());
    }

    @Test
    public void getTerminationDate() {
        assertEquals(terminationDate, user.getTerminationDate());
    }

    @Test
    public void setTerminationDate() {
        Date terminationDateSetter = new Date();
        user.setTerminationDate(terminationDateSetter);
        assertEquals(terminationDateSetter, user.getTerminationDate());
    }

    @Test
    public void getUserEmail() {
        assertEquals("testEmail@testEmail.pt", user.getUserEmail());
    }

    @Test
    public void setUserEmail() {
        user.setUserEmail("testEmailSetter@testEmailSetter.pt");
        assertEquals("testEmailSetter@testEmailSetter.pt", user.getUserEmail());
    }

    @Test
    public void getFirstname() {
        assertEquals("testFirstName", user.getFirstname());
    }

    @Test
    public void setFirstname() {
        user.setFirstname("testFirstNameSetter");
        assertEquals("testFirstNameSetter", user.getFirstname());
    }

    @Test
    public void getLastname() {
        assertEquals("testLastName", user.getLastname());
    }

    @Test
    public void setLastname() {
        user.setLastname("testLastNameSetter");
        assertEquals("testLastNameSetter", user.getLastname());
    }

    @Test
    public void isEnabled() {
        assertTrue(user.isEnabled());
    }

    @Test
    public void setEnabled() {
        user.setEnabled(false);
        assertFalse(user.isEnabled());
    }

    @Test
    public void getSub() {
        assertEquals("42a64cb0-4600-11eb-b378-0242ac130002", user.getSub());
    }

    @Test
    public void setSub() {
        user.setSub("329a6251-f891-475a-a655-1cf59dc52b25");
        assertEquals("329a6251-f891-475a-a655-1cf59dc52b25", user.getSub());
    }
}
