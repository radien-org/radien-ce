package io.radien.ms.permissionmanagement.client.entities;

import io.radien.api.model.permission.AbstractActionModel;
import io.radien.api.model.permission.SystemActionType;

public class Action extends AbstractActionModel {
    private Long id;
    private String name;
    private ActionType actionType = ActionType.READ;

    public Action() {}

    public Action(Action a) {
        this.id = a.getId();
        this.name = a.getName();
        this.actionType = a.getType();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public ActionType getType() {
        return actionType;
    }

    @Override
    public void setType(SystemActionType actionType) {
        this.actionType = (ActionType) actionType;
    }
}
