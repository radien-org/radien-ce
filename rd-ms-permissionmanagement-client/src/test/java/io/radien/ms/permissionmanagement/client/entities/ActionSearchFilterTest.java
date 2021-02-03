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
        Assert.assertNull(actionSearchFilter.getActionType());
    }

    @Test
    public void testSettingValues() {
        SystemActionSearchFilter actionSearchFilter = new ActionSearchFilter();
        Assert.assertFalse(actionSearchFilter.isExact());
        Assert.assertFalse(actionSearchFilter.isLogicConjunction());
        Assert.assertNull(actionSearchFilter.getName());
        Assert.assertNull(actionSearchFilter.getActionType());

        actionSearchFilter.setLogicConjunction(true);
        Assert.assertTrue(actionSearchFilter.isLogicConjunction());
        actionSearchFilter.setExact(true);
        Assert.assertTrue(actionSearchFilter.isExact());
        actionSearchFilter.setActionType(ActionType.READ);
        Assert.assertEquals(actionSearchFilter.getActionType(), ActionType.READ);
        actionSearchFilter.setName("act1");
        Assert.assertEquals(actionSearchFilter.getName(), "act1");
    }

    @Test
    public void testConstructor() {
        SystemActionSearchFilter actionSearchFilter = new ActionSearchFilter("act1",
                ActionType.WRITE, true, true);
        Assert.assertTrue(actionSearchFilter.isExact());
        Assert.assertTrue(actionSearchFilter.isLogicConjunction());
        Assert.assertNotNull(actionSearchFilter.getName());
        Assert.assertNotNull(actionSearchFilter.getActionType());
        Assert.assertEquals(actionSearchFilter.getActionType(), ActionType.WRITE);
        Assert.assertEquals(actionSearchFilter.getName(), "act1");
    }

}
