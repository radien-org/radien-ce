package io.radien.api.service.i18n;

import io.radien.exception.SystemException;
import java.util.List;

import io.radien.api.Appframeable;
import io.radien.api.model.i18n.SystemI18NProperty;

public interface I18NRESTServiceAccess extends Appframeable {

    String getTranslation(String key, String language, String application) throws SystemException;

    boolean save(SystemI18NProperty property) throws SystemException;

    SystemI18NProperty findByKeyAndApplication(String key, String application) throws SystemException;

    List<SystemI18NProperty> findAllByApplication(String application) throws SystemException;
    
}
