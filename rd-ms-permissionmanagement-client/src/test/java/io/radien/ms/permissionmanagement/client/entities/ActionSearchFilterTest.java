package io.radien.ms.permissionmanagement.client.entities;

import io.radien.api.model.permission.SystemActionSearchFilter;
import org.junit.Assert;
import org.junit.Test;

public class ActionSearchFilterTest {

    @Test
    public void testEmptyFilter() {
        SystemActionSearchFilter actionSearchFilter = new ActionSearchFilter();
        Assert.assertFalse(actionSearchFilter.isExact());
        Assert.assertFalse(actionSearchFilter.isLogicConjunction());
        Assert.assertNull(actionSearchFilter.getName());
    }

    @Test
    public void testSettingValues() {
        SystemActionSearchFilter actionSearchFilter = new ActionSearchFilter();
        Assert.assertFalse(actionSearchFilter.isExact());
        Assert.assertFalse(actionSearchFilter.isLogicConjunction());
        Assert.assertNull(actionSearchFilter.getName());

        actionSearchFilter.setLogicConjunction(true);
        Assert.assertTrue(actionSearchFilter.isLogicConjunction());
        actionSearchFilter.setExact(true);
        Assert.assertTrue(actionSearchFilter.isExact());
        actionSearchFilter.setName("act1");
        Assert.assertEquals(actionSearchFilter.getName(), "act1");
    }

    @Test
    public void testConstructor() {
        SystemActionSearchFilter actionSearchFilter = new ActionSearchFilter("act1",true, true);
        Assert.assertTrue(actionSearchFilter.isExact());
        Assert.assertTrue(actionSearchFilter.isLogicConjunction());
        Assert.assertNotNull(actionSearchFilter.getName());
        Assert.assertEquals(actionSearchFilter.getName(), "act1");
    }

}
