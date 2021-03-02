/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.radien.ms.ecm.service;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.ms.ecm.client.entities.I18NProperty;
import io.radien.ms.ecm.client.entities.Translation;
import io.radien.ms.ecm.entities.I18NPropertyEntity;
import io.radien.ms.ecm.client.services.SystemI18NPropertyService;
import io.radien.ms.ecm.util.I18NPropertyEntityMapper;
import io.radien.ms.ecm.util.ResourceBundleLoader;
import org.jnosql.artemis.document.DocumentTemplate;
import org.jnosql.diana.api.document.DocumentQuery;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.jnosql.diana.api.document.query.DocumentQueryBuilder.select;

/**
 * @author andresousa
 */

@ApplicationScoped
public class I18NPropertyService implements SystemI18NPropertyService {
    @Inject
    private DocumentTemplate documentTemplate;
    @Inject
    private OAFAccess oafAccess;


    @Override
    public String getLocalizedMessage(String messageCode) {
        I18NProperty property = getByKey(messageCode);

        return property.getTranslations().stream().filter(t -> t.getLanguage().equalsIgnoreCase("en")).findFirst()
                .map(Translation::getDescription)
                .orElse(property.getKey());
    }

    @Override
    public I18NProperty save(I18NProperty property) {
        I18NPropertyEntity entity = I18NPropertyEntityMapper.mapToEntity(property);
        entity = documentTemplate.insert(entity);
        return I18NPropertyEntityMapper.mapToDTO(entity);
    }

    @Override
    public List<I18NProperty> save(List<I18NProperty> propertyList) {
        List<I18NPropertyEntity> entityList = I18NPropertyEntityMapper.mapToEntity(propertyList);
        Iterable<I18NPropertyEntity> properties = documentTemplate.insert(entityList);
        List<I18NProperty> result = new ArrayList<>();
        properties.forEach(var -> result.add(I18NPropertyEntityMapper.mapToDTO(var)));

        return result;
    }

    @Override
    public void delete(I18NProperty property) {
        I18NPropertyEntity entity = I18NPropertyEntityMapper.mapToEntity(property);
        documentTemplate.delete(I18NPropertyEntity.class, entity);
    }

    @Override
    public List<String> getKeys() {
        return getAll().stream().map(I18NProperty::getKey).collect(Collectors.toList());
    }

    @Override
    public List<I18NProperty> getAll() {
        DocumentQuery query = select().from("I18NPropertyEntity").build();
        List<I18NPropertyEntity> list = documentTemplate.select(query);
        return I18NPropertyEntityMapper.mapToDTO(list);
    }

    @Override
    public I18NProperty getByKey(String key) {
        Optional<I18NPropertyEntity> propertyOptional = documentTemplate.find(I18NPropertyEntity.class, key);
        return propertyOptional.map(I18NPropertyEntityMapper::mapToDTO).orElse(null);
    }

    @Override
    public void initializeProperties() {
        String availableLanguages = oafAccess.getProperty(OAFProperties.SYSTEM_MS_CONFIG_SUPPORTED_LANG_ECM);
        String defaultLanguage = oafAccess.getProperty(OAFProperties.SYSTEM_MS_CONFIG_DEFAULT_LANG_ECM);
        ResourceBundleLoader loader = new ResourceBundleLoader(availableLanguages, defaultLanguage);

        saveOrUpdate(loader.getAllProperties());
    }

    private void saveOrUpdate(I18NProperty property) {
        if(getByKey(property.getKey()) != null) {
            documentTemplate.update(property);
            return;
        }
        documentTemplate.insert(property);
    }

    private void saveOrUpdate(List<I18NProperty> propList) {
        List<I18NProperty> propertyList = getAll();
        List<I18NProperty> newProperties = new ArrayList<>(propList);
        List<I18NProperty> updatedProperties = new ArrayList<>(propList);
        newProperties.removeIf(p -> propertyList.stream().anyMatch(property -> property.getKey().equals(p.getKey())));
        updatedProperties.removeIf(p -> newProperties.stream().anyMatch(property -> property.getKey().equals(p.getKey())));

        documentTemplate.insert(I18NPropertyEntityMapper.mapToEntity(newProperties));
        documentTemplate.update(I18NPropertyEntityMapper.mapToEntity(updatedProperties));
    }
}