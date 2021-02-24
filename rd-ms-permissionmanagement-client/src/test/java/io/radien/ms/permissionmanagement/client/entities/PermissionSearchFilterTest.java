package io.radien.ms.permissionmanagement.client.entities;

import io.radien.api.model.permission.SystemActionSearchFilter;
import io.radien.api.model.permission.SystemPermissionSearchFilter;
import org.junit.Assert;
import org.junit.Test;

public class PermissionSearchFilterTest {

    @Test
    public void testEmptyFilter() {
        SystemPermissionSearchFilter filter = new PermissionSearchFilter();
        Assert.assertFalse(filter.isExact());
        Assert.assertFalse(filter.isLogicConjunction());
        Assert.assertNull(filter.getName());
    }

    @Test
    public void testSetting() {
        SystemPermissionSearchFilter filter = new PermissionSearchFilter();
        Assert.assertFalse(filter.isExact());
        Assert.assertFalse(filter.isLogicConjunction());
        Assert.assertNull(filter.getName());

        filter.setExact(true);
        Assert.assertTrue(filter.isExact());

        filter.setLogicConjunction(true);
        Assert.assertTrue(filter.isLogicConjunction());

        filter.setName("perm1");
        Assert.assertNotNull(filter.getName());
        Assert.assertEquals(filter.getName(), "perm1");

        filter.setActionId(1L);
        filter.setResourceId(2L);

        Assert.assertNotNull(filter.getActionId());
        Assert.assertEquals(filter.getActionId(), new Long(1L));
        Assert.assertNotNull(filter.getResourceId());
        Assert.assertEquals(filter.getResourceId(), new Long(2L));

    }

    @Test
    public void testConstruct() {
        SystemPermissionSearchFilter filter =
                new PermissionSearchFilter("perm1", null, null, true, true);
        Assert.assertTrue(filter.isLogicConjunction());
        Assert.assertTrue(filter.isExact());
        filter.setName("perm1");
        Assert.assertNotNull(filter.getName());
        Assert.assertEquals(filter.getName(), "perm1");
        Assert.assertNull(filter.getActionId());
        Assert.assertNull(filter.getResourceId());
    }



}
