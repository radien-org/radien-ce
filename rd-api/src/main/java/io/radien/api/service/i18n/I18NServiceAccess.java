package io.radien.api.service.i18n;

import java.util.List;

import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.api.service.ServiceAccess;

public interface I18NServiceAccess extends ServiceAccess {

    String getTranslation(String key, String language, String application);

    SystemI18NProperty findByKeyAndApplication(String key, String application);

    List<SystemI18NProperty> findAllByApplication(String application);

    void save(SystemI18NProperty property);

    void deleteProperties(List<SystemI18NProperty> properties);

    void deleteApplicationProperties(String application);
}
