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
package io.radien.ms.usermanagement.entities;

import io.radien.ms.usermanagement.legacy.UserFactory;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.Date;

public class UserTest extends TestCase {

    User user;
    private final Date terminationDate = new Date();

    public UserTest() {
        user = UserFactory.create("testFirstName", "testLastname", "logonTest",
                "89f43c61-3ea5-4d8a-b08b-3a437eaed84e", "emailtest@emailtest.pt", 3L);
        user.setId(2L);
        user.setTerminationDate(terminationDate);
        user.setEnabled(true);
    }

    @Test
    public void testGetId() {
        assertNotNull(user.getId());
        assertEquals((Long) 2L, user.getId());
    }

    @Test
    public void testSetId() {
        user.setId(4L);

        assertNotNull(user.getId());
        assertEquals((Long) 4L, user.getId());
    }

    @Test
    public void testGetLogon() {
        assertNotNull(user.getLogon());
        assertEquals("logonTest", user.getLogon());
    }

    @Test
    public void testSetLogon() {
        user.setLogon("logonTestRefactored");

        assertNotNull(user.getLogon());
        assertEquals("logonTestRefactored", user.getLogon());
    }

    @Test
    public void testGetTerminationDate() {
        assertNotNull(user.getTerminationDate());
        assertEquals(terminationDate, user.getTerminationDate());
    }

    @Test
    public void testSetTerminationDate() {
        Date terminationDateRefactored = new Date();
        user.setTerminationDate(terminationDateRefactored);

        assertNotNull(user.getTerminationDate());
        assertEquals(terminationDateRefactored, user.getTerminationDate());
    }

    @Test
    public void testGetUserEmail() {
        assertNotNull(user.getUserEmail());
        assertEquals("emailtest@emailtest.pt", user.getUserEmail());
    }

    @Test
    public void testSetUserEmail() {
        user.setUserEmail("emailtestRefactored@emailtestRefactored.pt");

        assertNotNull(user.getUserEmail());
        assertEquals("emailtestRefactored@emailtestRefactored.pt", user.getUserEmail());
    }

    @Test
    public void testGetFirstname() {
        assertNotNull(user.getFirstname());
        assertEquals("testFirstName", user.getFirstname());
    }

    @Test
    public void testSetFirstname() {
        user.setFirstname("testFirstNameRefactored");

        assertNotNull(user.getFirstname());
        assertEquals("testFirstNameRefactored", user.getFirstname());
    }

    @Test
    public void testGetLastname() {
        assertNotNull(user.getLastname());
        assertEquals("testLastname", user.getLastname());
    }

    @Test
    public void testSetLastname() {
        user.setLastname("testLastnameRefactored");

        assertNotNull(user.getLastname());
        assertEquals("testLastnameRefactored", user.getLastname());
    }

    @Test
    public void testIsEnabled() {
        assertTrue(user.isEnabled());
    }

    @Test
    public void testSetEnabled() {
        user.setEnabled(false);
        assertFalse(user.isEnabled());
    }

    @Test
    public void testGetSub() {
        assertNotNull(user.getSub());
        assertEquals("89f43c61-3ea5-4d8a-b08b-3a437eaed84e", user.getSub());
    }

    @Test
    public void testSetSub() {
        user.setSub("e084ea7f-d3c2-4472-9a9d-f69997fdecbd");

        assertNotNull(user.getSub());
        assertEquals("e084ea7f-d3c2-4472-9a9d-f69997fdecbd", user.getSub());
    }
}