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
package io.radien.ms.openid.entities;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

public class PrincipalTest {

    private Principal principal = new Principal();
    private Date terminationDate = new Date();
    private Date date = new Date();

    public PrincipalTest() {
        principal.setId(2L);
        principal.setLogon("testLogon");
        principal.setUserEmail("testEmail@testEmail.pt");
        principal.setFirstname("testFirstName");
        principal.setLastname("testLastName");
        principal.setSub("42a64cb0-4600-11eb-b378-0242ac130002");
        principal.setTerminationDate(terminationDate);
        principal.setEnabled(true);
        principal.setDelegatedCreation(false);
        principal.setCreateDate(date);
        principal.setLastUpdate(date);
        principal.setCreateUser(2L);
        principal.setLastUpdateUser(2L);
    }

    @Test
    public void PrincipalConstructor() {
        Principal p = new Principal(principal);

        assertEquals(p.getId(), principal.getId());
        assertEquals(p.getLogon(), principal.getLogon());
        assertEquals(p.getUserEmail(), principal.getUserEmail());
        assertEquals(p.getFirstname(), principal.getFirstname());
        assertEquals(p.getLastname(), principal.getLastname());
        assertEquals(p.getSub(), principal.getSub());
        assertEquals(p.getTerminationDate(), principal.getTerminationDate());
        assertEquals(p.isEnabled(), principal.isEnabled());
    }

    @Test
    public void getId() {
        assertSame(2L, principal.getId());
    }

    @Test
    public void setId() {
        principal.setId(3L);
        assertSame(3L, principal.getId());
    }

    @Test
    public void getLogon() {
        assertEquals("testLogon", principal.getLogon());
    }

    @Test
    public void setLogon() {
        principal.setLogon("testLogonSetter");
        assertEquals("testLogonSetter", principal.getLogon());
    }

    @Test
    public void getTerminationDate() {
        assertEquals(terminationDate, principal.getTerminationDate());
    }

    @Test
    public void setTerminationDate() {
        Date terminationDateSetter = new Date();
        principal.setTerminationDate(terminationDateSetter);
        assertEquals(terminationDateSetter, principal.getTerminationDate());
    }

    @Test
    public void getUserEmail() {
        assertEquals("testEmail@testEmail.pt", principal.getUserEmail());
    }

    @Test
    public void setUserEmail() {
        principal.setUserEmail("testEmailSetter@testEmailSetter.pt");
        assertEquals("testEmailSetter@testEmailSetter.pt", principal.getUserEmail());
    }

    @Test
    public void getFirstname() {
        assertEquals("testFirstName", principal.getFirstname());
    }

    @Test
    public void setFirstname() {
        principal.setFirstname("testFirstNameSetter");
        assertEquals("testFirstNameSetter", principal.getFirstname());
    }

    @Test
    public void getLastname() {
        assertEquals("testLastName", principal.getLastname());
    }

    @Test
    public void setLastname() {
        principal.setLastname("testLastNameSetter");
        assertEquals("testLastNameSetter", principal.getLastname());
    }

    @Test
    public void isEnabled() {
        assertTrue(principal.isEnabled());
    }

    @Test
    public void setEnabled() {
        principal.setEnabled(false);
        assertFalse(principal.isEnabled());
    }

    @Test
    public void getSub() {
        assertEquals("42a64cb0-4600-11eb-b378-0242ac130002", principal.getSub());
    }

    @Test
    public void setSub() {
        principal.setSub("329a6251-f891-475a-a655-1cf59dc52b25");
        assertEquals("329a6251-f891-475a-a655-1cf59dc52b25", principal.getSub());
    }

    @Test
    public void getDelegatedCreation() {
        assertFalse(principal.isDelegatedCreation());
    }

    @Test
    public void setDelegatedCreation() {
        principal.setDelegatedCreation(true);
        assertTrue(principal.isDelegatedCreation());
    }

    @Test
    public void getCreateDate() {
        assertEquals(date, principal.getCreateDate());
    }

    @Test
    public void setCreateDate() {
        Date newDate = new Date();
        principal.setCreateDate(newDate);
        assertEquals(newDate, principal.getCreateDate());
    }

    @Test
    public void getLastUpdate() {
        assertEquals(date, principal.getLastUpdate());
    }

    @Test
    public void setLastUpdate() {
        Date newDate = new Date();
        principal.setLastUpdate(newDate);
        assertEquals(newDate, principal.getLastUpdate());
    }

    @Test
    public void getCreateUser() {
        assertEquals((Long) 2L, principal.getCreateUser());
    }

    @Test
    public void setCreateUser() {
        principal.setCreateUser(3L);
        assertEquals((Long) 3L, principal.getCreateUser());
    }

    @Test
    public void getLastUpdateUser() {
        assertEquals((Long) 2L, principal.getLastUpdateUser());
    }

    @Test
    public void setLastUpdateUser() {
        principal.setLastUpdateUser(3L);
        assertEquals((Long) 3L, principal.getLastUpdateUser());
    }
}
