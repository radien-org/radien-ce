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

package io.radien.ms.ecm.datalayer;

import io.radien.api.entity.Page;
import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.api.model.i18n.SystemI18NTranslation;
import io.radien.exception.SystemException;
import io.radien.ms.ecm.client.entities.i18n.I18NProperty;
import io.radien.ms.ecm.producer.JongoConnectionHandler;
import io.radien.ms.ecm.util.i18n.JongoQueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

@Stateless
public class I18NRepository {
    private static final String FIELD_KEY = "key";
    private static final String FIELD_APPLICATION = "application";

    @Inject
    private JongoConnectionHandler jongoConnectionHandler;

    public String getTranslation(String key, String language, String application) throws IllegalStateException, SystemException {
        SystemI18NProperty property = findByKeyAndApplication(key, application);
        if (property != null) {
            String finalLanguage = language;
            Optional<SystemI18NTranslation> languageTranslation = property.getTranslations().stream().filter(obj -> obj.getLanguage().equals(finalLanguage))
                    .findFirst();

            if (languageTranslation.isPresent()) {
                return languageTranslation.get().getValue();
            }
            language = Locale.forLanguageTag(language).getLanguage();
            return getTranslation(key, language, application);
        }
        return key;
    }

    public void save(SystemI18NProperty entity) throws SystemException {
        jongoConnectionHandler.apply(input -> {
            if(!existsKeyAndApplication(input, entity.getKey(), entity.getApplication())) {
                input.insert(entity);
            } else {
                String query = new JongoQueryBuilder()
                    .addEquality(FIELD_KEY, entity.getKey())
                    .addEquality(FIELD_APPLICATION, entity.getApplication())
                    .build();
                input
                    .update(query)
                    .with(entity);
            }
            return null;
        }, I18NProperty.class.getSimpleName());
    }

    public void deleteProperty(SystemI18NProperty property) throws SystemException {
        jongoConnectionHandler.apply(input -> {
            String query = new JongoQueryBuilder()
                    .addEquality(FIELD_KEY, property.getKey())
                    .addEquality(FIELD_APPLICATION, property.getApplication())
                    .build();
            input.remove(query);
            return null;
        }, I18NProperty.class.getSimpleName());
    }

    public void deleteApplication(String application) throws SystemException {
        jongoConnectionHandler.apply(input -> {
            String query = new JongoQueryBuilder()
                            .addEquality(FIELD_APPLICATION, application)
                            .build();
            input.remove(query);
            return null;
        }, I18NProperty.class.getSimpleName());
    }

    public List<SystemI18NProperty> findAllByApplication(String application) throws SystemException {
        return jongoConnectionHandler.apply(input -> {
            String query = new JongoQueryBuilder()
                    .addEquality(FIELD_APPLICATION, application)
                    .build();
            List<SystemI18NProperty> results = new ArrayList<>();
            input.find(query)
                    .as(I18NProperty.class)
                    .forEach(results::add);
            return results;
        }, I18NProperty.class.getSimpleName());
    }

    public SystemI18NProperty findByKeyAndApplication(String key, String application) throws IllegalStateException, SystemException {
        return jongoConnectionHandler.apply((input -> {
            String query = new JongoQueryBuilder()
                    .addEquality(FIELD_KEY, key)
                    .addEquality(FIELD_APPLICATION, application)
                    .build();
            MongoCursor<I18NProperty> entities = input
                    .find(query).as(I18NProperty.class);
            if(entities.count() > 1) {
                throw new IllegalStateException("Multiple values found for the same key");
            }

            return entities.count() != 0 ? entities.next() : null;
        }), I18NProperty.class.getSimpleName());
    }

    private boolean existsKeyAndApplication(MongoCollection collection, String key, String application) {
        String query = new JongoQueryBuilder()
                .addEquality(FIELD_KEY, key)
                .addEquality(FIELD_APPLICATION, application)
                .build();
        return collection.count(query) != 0;
    }

    public Page<SystemI18NProperty> getAll(String application, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) throws SystemException {
        return jongoConnectionHandler.apply((input -> {
            String query = "{}";
            if(application != null) {
                query = new JongoQueryBuilder()
                        .addEquality(FIELD_APPLICATION, application)
                        .build();
            }
            List<SystemI18NProperty> results = new ArrayList<>();
            MongoCursor<I18NProperty> entities = input
                    .find(query)
                    .skip((pageNo > 0 ? ( ( pageNo - 1 ) * pageSize ) : 0))
                    .limit(pageSize)
                    //.sort()
                    .as(I18NProperty.class);
            entities.forEach(results::add);

            Page<SystemI18NProperty> pageResult = new Page<>();
            pageResult.setResults(results);
            pageResult.setTotalResults(Math.toIntExact(input.count(query)));
            pageResult.setTotalPages(pageResult.getTotalResults() % pageSize==0 ?
                    pageResult.getTotalResults()/pageSize : pageResult.getTotalResults()/pageSize+1);
            pageResult.setCurrentPage(pageNo);

            return pageResult;
        }), I18NProperty.class.getSimpleName());
    }
}
