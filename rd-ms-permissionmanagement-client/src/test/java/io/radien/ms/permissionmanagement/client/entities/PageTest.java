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
package io.radien.ms.permissionmanagement.client.entities;

import io.radien.api.entity.Page;
import io.radien.api.model.permission.SystemPermission;
import io.radien.ms.permissionmanagement.client.entities.Permission;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PageTest {

    private List<Permission> listOfPermissions = new ArrayList<>();
    private Page<Permission> page;

    public PageTest() {
        Permission permission1 = new Permission();
        permission1.setName("permission1");
        Permission permission2 = new Permission();
        permission2.setName("permission2");

        listOfPermissions.add(permission1);
        listOfPermissions.add(permission2);

        page = new Page<>(listOfPermissions, 1, 4, 4);
    }

    @Test
    public void getResults() {
        List<? extends SystemPermission> listOfExtractedPermissions = page.getResults();
        assertEquals(listOfExtractedPermissions, listOfPermissions);
        assertEquals(listOfExtractedPermissions.size(), listOfPermissions.size());

        SystemPermission firstExtractedPermission = listOfExtractedPermissions.get(0);
        SystemPermission secondExtractedPermission = listOfExtractedPermissions.get(1);

        assertEquals("permission1", firstExtractedPermission.getName());

        assertEquals("permission2", secondExtractedPermission.getName());
    }

    @Test
    public void setResults() {
        Permission permission3 = new Permission();
        permission3.setName("permission3");

        List<Permission> newListOfPermissions = new ArrayList<>();
        newListOfPermissions.add(permission3);
        page.setResults(newListOfPermissions);

        List<? extends SystemPermission> newListOfExtractedPermissions = page.getResults();
        assertEquals(newListOfExtractedPermissions, newListOfPermissions);
        assertEquals(newListOfExtractedPermissions.size(), newListOfPermissions.size());

        SystemPermission newFirstExtractedPermission = newListOfExtractedPermissions.get(0);

        assertEquals("permission3", newFirstExtractedPermission.getName());

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
