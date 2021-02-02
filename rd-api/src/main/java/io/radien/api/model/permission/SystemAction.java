package io.radien.api.model.permission;

import io.radien.api.Model;
public interface SystemAction extends Model {
    Long getId();
    void setId(Long id);
    String getName();
    void setName(String name);
    SystemActionType getType();
    void setType(SystemActionType actionType);
}
