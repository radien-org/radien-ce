package io.radien.ms.permissionmanagement.client.entities;

import io.radien.api.model.permission.SystemActionType;

import java.util.Arrays;

public enum ActionType implements SystemActionType {

    READ(1L, "READ"),
    WRITE(2l, "WRITE"),
    EXECUTION(3l, "EXECUTION"),
    CREATE(4l, "CREATE"),
    UPDATE(5l, "UPDATE"),
    DELETE(6L, "DELETE"),
    LIST(7l, "LIST");

    private Long id;
    private String name;

    private ActionType(Long id, String name) {
        setId(id);
        setName(name);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) { this.id = id; }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) { this.name = name; }

    public static ActionType getByName(String name) {
        return Arrays.stream(ActionType.values()).
                filter(a -> a.getName().equals(name)).findFirst().orElse(null);
    }

    public static ActionType getById(Long id) {
        return Arrays.stream(ActionType.values()).
                filter(a -> a.getId().equals(id)).findFirst().orElse(null);
    }
}
