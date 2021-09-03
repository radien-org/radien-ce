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

import junit.framework.TestCase;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author Bruno Gama
 **/
public class UserSearchFilterTest extends TestCase {

    private final UserSearchFilter userSearchFilter;
    private final List<Long> ids = Arrays.asList(1L, 2L);

    public UserSearchFilterTest() {
        userSearchFilter = new UserSearchFilter("sub", "email", "logon", ids, false, false);
    }

    @Test
    public void testEmptyConstructor() {
        UserSearchFilter userSearchFilter1 = new UserSearchFilter();
        assertNull(userSearchFilter1.getSub());
        assertNull(userSearchFilter1.getEmail());
        assertNull(userSearchFilter1.getLogon());
        assertNull(userSearchFilter1.getIds());
        assertFalse(userSearchFilter1.isExact());
        assertFalse(userSearchFilter1.isLogicConjunction());
    }

    @Test
    public void testGetSub() {
        assertNotNull(userSearchFilter.getSub());
        assertEquals("sub", userSearchFilter.getSub());
    }

    @Test
    public void testSetSub() {
        userSearchFilter.setSub("newSub");
        assertNotNull(userSearchFilter.getSub());
        assertEquals("newSub", userSearchFilter.getSub());
    }

    @Test
    public void testGetEmail() {
        assertNotNull(userSearchFilter.getEmail());
        assertEquals("email", userSearchFilter.getEmail());
    }

    @Test
    public void testSetEmail() {
        userSearchFilter.setEmail("newEmail");
        assertNotNull(userSearchFilter.getEmail());
        assertEquals("newEmail", userSearchFilter.getEmail());
    }

    @Test
    public void testGetLogon() {
        assertNotNull(userSearchFilter.getLogon());
        assertEquals("logon", userSearchFilter.getLogon());
    }

    @Test
    public void testSetLogon() {
        userSearchFilter.setLogon("newLogon");
        assertNotNull(userSearchFilter.getLogon());
        assertEquals("newLogon", userSearchFilter.getLogon());
    }

    @Test
    public void testGetIds() {
        assertNotNull(userSearchFilter.getIds());
        assertEquals(ids, userSearchFilter.getIds());
    }

    @Test
    public void testSetIds() {
        List<Long> identifiers = Arrays.asList(1L, 2L, 3L);
        userSearchFilter.setIds(identifiers);
        assertNotNull(userSearchFilter.getIds());
        assertEquals(identifiers, userSearchFilter.getIds());
    }


    @Test
    public void testIsExact() {
        assertFalse(userSearchFilter.isExact());
    }

    @Test
    public void testSetExact() {
        userSearchFilter.setExact(true);
        assertTrue(userSearchFilter.isExact());
    }

    @Test
    public void testIsLogicConjunction() {
        assertFalse(userSearchFilter.isLogicConjunction());
    }

    @Test
    public void testSetLogicConjunction() {
        userSearchFilter.setLogicConjunction(true);
        assertTrue(userSearchFilter.isLogicConjunction());
    }
}