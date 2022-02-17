package io.radien.ms.ecm.service;

import io.radien.exception.SystemException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.api.service.i18n.I18NServiceAccess;
import io.radien.ms.ecm.datalayer.I18NRepository;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
public class I18NService implements I18NServiceAccess {
    private static final Logger log = LoggerFactory.getLogger(I18NService.class);

    @Inject
    private I18NRepository repository;
    @Inject
    @ConfigProperty(name = "system.default.language")
    private String defaultLanguage;

    @Override
    public String getTranslation(String key, String language, String application) {
        String result = null;
        try {
            result = repository.getTranslation(key, language, application);
            if(result.equals(key)) {
                result = repository.getTranslation(key, defaultLanguage, application);
            }
        } catch (SystemException e) {
            log.error("Error retrieving translation for {} in {} for application {}", key, language, application, e);
        }
        return result;
    }

    @Override
    public void save(SystemI18NProperty property) {
        try {
            repository.save(property);
        } catch (SystemException e) {
            log.error("Error saving property {} for {}", property.getKey(), property.getApplication(), e);
        }

    }

    @Override
    public void deleteProperties(List<SystemI18NProperty> properties) {
        I18NRepository i18NRepository = repository;
        for (SystemI18NProperty property : properties) {
            try {
                i18NRepository.deleteProperty(property);
            } catch (SystemException e) {
                log.error("Error deleting property {} for {}", property.getKey(), property.getApplication(), e);
            }
        }
    }

    @Override
    public void deleteApplicationProperties(String application) {
        try {
            repository.deleteApplication(application);
        } catch (SystemException e) {
            log.error("Error deleting properties for {}", application, e);

        }
    }

    @Override
    public SystemI18NProperty findByKeyAndApplication(String key, String application) {
        try {
            return repository.findByKeyAndApplication(key, application);
        } catch (SystemException e) {
            log.error("Error retrieving property by key {} and application {}", key, application, e);
        }
        return null;
    }

    @Override
    public List<SystemI18NProperty> findAllByApplication(String application) {
        try {
            return repository.findAllByApplication(application);
        } catch (SystemException e) {
            log.error("Error retrieving all properties for {}", application, e);
        }
        return new ArrayList<>();
    }
    
}
