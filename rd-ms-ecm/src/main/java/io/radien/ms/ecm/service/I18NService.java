package io.radien.ms.ecm.service;

import io.radien.exception.SystemException;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.api.service.i18n.I18NServiceAccess;
import io.radien.ms.ecm.datalayer.I18NRepository;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@RequestScoped
public class I18NService implements I18NServiceAccess {

    @Inject
    private I18NRepository repository;
    @Inject
    @ConfigProperty(name = "system.default.language")
    private String defaultLanguage;

    @Override
    public String getTranslation(String key, String language, String application) throws SystemException {
        String result = repository.getTranslation(key, language, application);
        if(result.equals(key)) {
            result = repository.getTranslation(key, defaultLanguage, application);
        }
        return result;
    }

    @Override
    public void save(SystemI18NProperty property) throws SystemException {
        repository.save(property);
    }

    @Override
    public void deleteProperties(List<SystemI18NProperty> properties) throws SystemException {
        for (SystemI18NProperty property : properties) {
            repository.deleteProperty(property);
        }
    }

    @Override
    public void deleteApplicationProperties(String application) throws SystemException {
        repository.deleteApplication(application);
    }

    @Override
    public SystemI18NProperty findByKeyAndApplication(String key, String application) throws SystemException {
        return repository.findByKeyAndApplication(key, application);
    }

    @Override
    public List<SystemI18NProperty> findAllByApplication(String application) throws SystemException {
        return repository.findAllByApplication(application);
    }
    
}
