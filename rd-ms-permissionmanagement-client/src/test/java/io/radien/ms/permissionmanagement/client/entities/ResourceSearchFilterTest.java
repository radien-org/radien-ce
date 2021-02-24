package io.radien.ms.permissionmanagement.client.entities;

import io.radien.api.model.permission.SystemResourceSearchFilter;
import org.junit.Assert;
import org.junit.Test;

public class ResourceSearchFilterTest {

    @Test
    public void testEmptyFilter() {
        SystemResourceSearchFilter resourceSearchFilter = new ResourceSearchFilter();
        Assert.assertFalse(resourceSearchFilter.isExact());
        Assert.assertFalse(resourceSearchFilter.isLogicConjunction());
        Assert.assertNull(resourceSearchFilter.getName());
    }

    @Test
    public void testSettingValues() {
        SystemResourceSearchFilter resourceSearchFilter = new ResourceSearchFilter();
        Assert.assertFalse(resourceSearchFilter.isExact());
        Assert.assertFalse(resourceSearchFilter.isLogicConjunction());
        Assert.assertNull(resourceSearchFilter.getName());

        resourceSearchFilter.setLogicConjunction(true);
        Assert.assertTrue(resourceSearchFilter.isLogicConjunction());
        resourceSearchFilter.setExact(true);
        Assert.assertTrue(resourceSearchFilter.isExact());
        resourceSearchFilter.setName("act1");
        Assert.assertEquals(resourceSearchFilter.getName(), "act1");
    }

    @Test
    public void testConstructor() {
        SystemResourceSearchFilter resourceSearchFilter = new ResourceSearchFilter("act1",true, true);
        Assert.assertTrue(resourceSearchFilter.isExact());
        Assert.assertTrue(resourceSearchFilter.isLogicConjunction());
        Assert.assertNotNull(resourceSearchFilter.getName());
        Assert.assertEquals(resourceSearchFilter.getName(), "act1");
    }

}
