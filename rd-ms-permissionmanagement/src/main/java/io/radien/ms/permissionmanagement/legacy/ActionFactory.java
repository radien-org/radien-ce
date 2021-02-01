package io.radien.ms.permissionmanagement.legacy;


import io.radien.ms.permissionmanagement.client.entities.ActionType;
import io.radien.ms.permissionmanagement.model.Action;

import java.util.Date;

public class ActionFactory {
    public static Action create(String name, ActionType actionType, Long createUser){
        Action action = new Action();
        action.setName(name);
        action.setCreateUser(createUser);
        Date now = new Date();
        action.setLastUpdate(now);
        action.setCreateDate(now);
        action.setActionType(actionType);
        return action;
    }
}
