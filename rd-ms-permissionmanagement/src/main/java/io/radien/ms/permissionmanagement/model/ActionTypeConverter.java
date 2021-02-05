package io.radien.ms.permissionmanagement.model;

import io.radien.ms.permissionmanagement.client.entities.ActionType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ActionTypeConverter implements AttributeConverter<ActionType, Long> {

    @Override
    public Long convertToDatabaseColumn(ActionType actionType) {
        return actionType.getId();
    }

    @Override
    public ActionType convertToEntityAttribute(Long id) {
        return ActionType.getById(id);
    }
}
