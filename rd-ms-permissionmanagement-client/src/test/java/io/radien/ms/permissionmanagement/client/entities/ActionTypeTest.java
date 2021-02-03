package io.radien.ms.permissionmanagement.client.entities;

import io.radien.api.model.permission.SystemActionType;
import org.junit.Assert;
import org.junit.Test;

public class ActionTypeTest {

    @Test
    public void testGetById() {
        SystemActionType systemActionType = ActionType.READ;
        ActionType actionType = ActionType.getById(systemActionType.getId());
        Assert.assertNotNull(actionType);
        Assert.assertEquals(systemActionType, actionType);
    }

    @Test
    public void testGetByName() {
        SystemActionType systemActionType = ActionType.WRITE;
        ActionType actionType = ActionType.getByName(systemActionType.getName());
        Assert.assertNotNull(actionType);
        Assert.assertEquals(systemActionType, actionType);
    }

    @Test
    public void testGetNonExistentByName() {
        ActionType actionType = ActionType.getByName("WRITE X");
        Assert.assertNull(actionType);
    }

    @Test
    public void testGetNonExistentById() {
        ActionType actionType = ActionType.getById(11111L);
        Assert.assertNull(actionType);
    }
}
