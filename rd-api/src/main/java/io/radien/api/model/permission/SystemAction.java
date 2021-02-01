package io.radien.api.model.permission;

import io.radien.api.Model;
public interface SystemAction extends Model {
    Long getId();
    void setId(Long id);
    String getName();
    void setName(String name);
    SystemActionType getActionType();
    void setActionType(SystemActionType actionType);
}
