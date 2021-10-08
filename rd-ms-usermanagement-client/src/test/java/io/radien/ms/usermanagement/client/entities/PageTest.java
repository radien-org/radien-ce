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
package io.radien.ms.usermanagement.client.entities;

import io.radien.api.entity.Page;
import io.radien.api.model.user.SystemUser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PageTest {

    private List<User> listOfUsers = new ArrayList<>();
    private Page<User> page;

    public PageTest() {
        User user1 = new User();
        user1.setLogon("user1Logon");
        user1.setUserEmail("user1Email@user1Email.pt");
        user1.setSub("65c31b6d-1a82-4fa5-96a0-b889de9aceb6");
        User user2 = new User();
        user2.setLogon("user2Logon");
        user2.setUserEmail("user2Email@user2Email.pt");
        user2.setSub("e13a862b-37c6-4c30-bec5-6be6d81860ad");

        listOfUsers.add(user1);
        listOfUsers.add(user2);

        page = new Page<>(listOfUsers, 1, 4, 4);
    }

    @Test
    public void getResults() {
        List<? extends SystemUser> listOfExtractedUsers = page.getResults();
        assertEquals(listOfExtractedUsers, listOfUsers);
        assertEquals(listOfExtractedUsers.size(), listOfUsers.size());

        SystemUser firstExtractedUser = listOfExtractedUsers.get(0);
        SystemUser secondExtractedUser = listOfExtractedUsers.get(1);

        assertEquals("user1Logon", firstExtractedUser.getLogon());
        assertEquals("65c31b6d-1a82-4fa5-96a0-b889de9aceb6", firstExtractedUser.getSub());

        assertEquals("user2Email@user2Email.pt", secondExtractedUser.getUserEmail());
        assertEquals("e13a862b-37c6-4c30-bec5-6be6d81860ad", secondExtractedUser.getSub());
    }

    @Test
    public void setResults() {
        User user3 = new User();
        user3.setLogon("user3Logon");
        user3.setUserEmail("user3Email@user3Email.pt");
        user3.setSub("a79b5f43-5854-4a42-9de0-86c6098a9295");

        List<User> newListOfUsers = new ArrayList<>();
        newListOfUsers.add(user3);
        page.setResults(newListOfUsers);

        List<? extends SystemUser> newListOfExtractedUsers = page.getResults();
        assertEquals(newListOfExtractedUsers, newListOfUsers);
        assertEquals(newListOfExtractedUsers.size(), newListOfUsers.size());

        SystemUser newFirstExtractedUser = newListOfExtractedUsers.get(0);

        assertEquals("user3Logon", newFirstExtractedUser.getLogon());
        assertEquals("user3Email@user3Email.pt", newFirstExtractedUser.getUserEmail());
        assertEquals("a79b5f43-5854-4a42-9de0-86c6098a9295", newFirstExtractedUser.getSub());

    }

    @Test
    public void getCurrentPage() {
        assertEquals(1, page.getCurrentPage());
    }

    @Test
    public void setCurrentPage() {
        assertEquals(1, page.getCurrentPage());
        page.setCurrentPage(2);
        assertEquals(2, page.getCurrentPage());
    }

    @Test
    public void getTotalResults() {
        assertEquals(4, page.getTotalResults());
    }

    @Test
    public void setTotalResults() {
        assertEquals(4, page.getTotalResults());
        page.setTotalResults(2);
        assertEquals(2, page.getTotalResults());
    }

    @Test
    public void getTotalPages() {
        assertEquals(4, page.getTotalPages());
    }

    @Test
    public void setTotalPages() {
        assertEquals(4, page.getTotalPages());
        page.setTotalPages(2);
        assertEquals(2, page.getTotalPages());
    }
}
