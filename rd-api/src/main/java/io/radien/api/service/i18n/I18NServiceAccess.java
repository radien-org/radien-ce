package io.radien.api.service.i18n;

import io.radien.exception.SystemException;
import java.util.List;

import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.api.service.ServiceAccess;

public interface I18NServiceAccess extends ServiceAccess {

    String getTranslation(String key, String language, String application) throws SystemException;

    SystemI18NProperty findByKeyAndApplication(String key, String application) throws SystemException;

    List<SystemI18NProperty> findAllByApplication(String application) throws SystemException;

    void save(SystemI18NProperty property) throws SystemException;

    void deleteProperties(List<SystemI18NProperty> properties) throws SystemException;

    void deleteApplicationProperties(String application) throws SystemException;
}
