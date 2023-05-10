/*
 *
 *  * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
 *  * <p>
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  * <p>
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  * <p>
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.radien.ms.ecm.service;

import io.radien.api.entity.Page;
import io.radien.api.model.i18n.SystemI18NTranslation;
import io.radien.exception.SystemException;
import io.radien.ms.ecm.config.ConfigHandler;
import java.util.List;

import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;

import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.api.service.i18n.I18NServiceAccess;
import io.radien.ms.ecm.datalayer.I18NRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;

@Stateless
public class I18NService implements I18NServiceAccess {

    @Inject
    private I18NRepository repository;

    @Inject
    private ConfigHandler configHandler;

    private String defaultLanguage;

    @PostConstruct
    public void init() {
        defaultLanguage = configHandler.getDefaultLanguage();
    }

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
    public Page<SystemI18NProperty> getAll(String application, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) throws SystemException {
        return repository.getAll(application, pageNo, pageSize, sortBy, isAscending);
    }

    @Override
    public SystemI18NProperty findByKeyAndApplication(String key, String application) throws SystemException {
        return repository.findByKeyAndApplication(key, application);
    }

    @Override
    public List<SystemI18NProperty> findAllByApplication(String application) throws SystemException {
        return repository.findAllByApplication(application);
    }

    @Override
    public  Map<String, String> findAllByApplicationAndLanguage(String application, String language) throws SystemException {
        List<SystemI18NProperty> propertyList = repository.findAllByApplication(application);
        return propertyList.stream()
                .filter(property -> property.getTranslations().stream().anyMatch(translation -> translation.getLanguage().equals(language)))
                .map(property -> new ImmutablePair<>(property.getKey(), property.getTranslation(language)))
                .collect(Collectors.toMap(ImmutablePair::getLeft, ImmutablePair::getRight));
    }
    
}
