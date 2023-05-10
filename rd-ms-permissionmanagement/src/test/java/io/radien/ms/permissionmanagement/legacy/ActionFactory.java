package io.radien.ms.permissionmanagement.legacy;


import io.radien.ms.permissionmanagement.entities.ActionEntity;

import java.util.Date;

public class ActionFactory {
    public static ActionEntity create(String name, Long createUser){
        ActionEntity action = new ActionEntity();
        action.setName(name);
        action.setCreateUser(createUser);
        Date now = new Date();
        action.setLastUpdate(now);
        action.setCreateDate(now);
        return action;
    }
}
