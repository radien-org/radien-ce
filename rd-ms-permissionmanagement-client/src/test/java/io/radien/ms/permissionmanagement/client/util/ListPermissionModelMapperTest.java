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

package io.radien.ms.permissionmanagement.client.util;

import io.radien.api.model.permission.SystemPermission;
import io.radien.ms.permissionmanagement.client.entities.Permission;
import io.radien.ms.permissionmanagement.client.services.PermissionFactory;
import org.junit.Assert;
import org.junit.Test;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * List Permission Mapper and converter test
 * {@link io.radien.ms.permissionmanagement.client.util.ListPermissionModelMapper}
 */
public class ListPermissionModelMapperTest {

    /**
     * Test to create permission list based on json informaiton
     */
    @Test
    public void creatingPermissionListFromJson() {
        List<Permission> list = new ArrayList<>();
        Permission sp1 = PermissionFactory.create("a", 1L, 2L,100L);
        Permission sp2 = PermissionFactory.create("b", 3L, 4L,101L);
        Permission sp3 = PermissionFactory.create("c", 5L, 6L,102L);

        list.add(sp1);
        list.add(sp2);
        list.add(sp3);

        JsonArray jsonArray = PermissionModelMapper.map(list);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonArray.toString().getBytes());
        List<? extends SystemPermission> convertedList = ListPermissionModelMapper.map(inputStream);
        Assert.assertNotNull(convertedList);

        boolean foundAllPermissions = convertedList.stream().allMatch(p -> p.getId() == sp1.getId() ||
                p.getId() == sp2.getId() || p.getId() == sp3.getId());

        Assert.assertTrue(foundAllPermissions);
    }

    /**
     * Test to create a empty list of permissions
     */
    @Test
    public void creatingEmptyPermissionListFromJson() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("[]".getBytes());
        List<? extends SystemPermission> convertedList = ListPermissionModelMapper.map(inputStream);
        Assert.assertNotNull(convertedList);
        Assert.assertEquals(0, convertedList.size());
    }
}
