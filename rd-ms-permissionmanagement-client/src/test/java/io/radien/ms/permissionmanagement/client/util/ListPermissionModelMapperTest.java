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

public class ListPermissionModelMapperTest {

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

    @Test
    public void creatingEmptyPermissionListFromJson() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("[]".getBytes());
        List<? extends SystemPermission> convertedList = ListPermissionModelMapper.map(inputStream);
        Assert.assertNotNull(convertedList);
        Assert.assertEquals(convertedList.size(), 0);
    }
}
