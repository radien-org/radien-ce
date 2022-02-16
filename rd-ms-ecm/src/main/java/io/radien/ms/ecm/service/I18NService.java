package io.radien.ms.ecm.service;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.api.service.i18n.I18NServiceAccess;
import io.radien.ms.ecm.datalayer.I18NRepository;

@RequestScoped
public class I18NService implements I18NServiceAccess {

    @Inject
    private I18NRepository repository;

    @Override
    public String getTranslation(String key, String language, String application) {
        return repository.getTranslation(key, language, application);
    }

    @Override
    public void save(SystemI18NProperty property) {
        repository.save(property);
        
    }

    @Override
    public SystemI18NProperty findByKeyAndApplication(String key, String application) {
        return repository.findByKeyAndApplication(key, application);
    }

    @Override
    public List<SystemI18NProperty> findAllByApplication(String application) {
        return repository.findAllByApplication(application);
    }
    
}
