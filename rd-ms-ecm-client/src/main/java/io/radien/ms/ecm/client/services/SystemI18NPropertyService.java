package io.radien.ms.ecm.client.services;

import io.radien.ms.ecm.client.entities.I18NProperty;

import java.util.List;

public interface SystemI18NPropertyService {

    String getLocalizedMessage(String messageCode);

    I18NProperty save(I18NProperty property);

    List<I18NProperty> save(List<I18NProperty> propertyList);

    void delete(I18NProperty property);

    List<String> getKeys();

    List<I18NProperty> getAll();

    I18NProperty getByKey(String key);

    void initializeProperties();
}
