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

package io.radien.ms.ecm.domain;

import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.api.model.i18n.SystemI18NTranslation;
import io.radien.ms.ecm.client.entities.i18n.I18NProperty;
import io.radien.ms.ecm.client.entities.i18n.I18NTranslation;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TranslationDataProvider implements Serializable {
    private static final long serialVersionUID = 2663306776212087270L;
    private static final Logger log = LoggerFactory.getLogger(TranslationDataProvider.class);
    /**
     * The default resource bundle for i18n support
     */
    public static final String DEFAULT_BUNDLE_NAME = "i18n/Language";
    private final List<Locale> locales;

    private transient final List<SystemI18NProperty> allProperties;

    public TranslationDataProvider(String availableLanguages, String defaultLanguage) {
        allProperties = new LinkedList<>();
        locales = Stream.of(availableLanguages.split(","))
                .map(Locale::forLanguageTag)
                .collect(Collectors.toCollection(LinkedList::new));
        ResourceBundle defaultBundle = ResourceBundle.getBundle(DEFAULT_BUNDLE_NAME, Locale.ROOT);
        List<String> bundleKeys = Collections.list(defaultBundle.getKeys());
        List<ResourceBundle> bundleList = locales.stream()
                .map(l -> ResourceBundle.getBundle(DEFAULT_BUNDLE_NAME, l)).collect(Collectors.toList());

        for(String bundleKey : bundleKeys) {
            SystemI18NProperty property = new I18NProperty();
            property.setKey(bundleKey);
            property.setApplication("radien");
            List<SystemI18NTranslation> translations = new LinkedList<>();
            for(ResourceBundle bundle : bundleList) {
                String language = bundle.getLocale().toLanguageTag();
                if(StringUtils.isEmpty(language) || language.equals("und")) {
                    log.warn("[MessagesResourceBundle] could not load language/locale for bundlekey: {} and language: {} - load default language {}",bundleKey, language, defaultLanguage);
                    language = defaultLanguage;
                }
                SystemI18NTranslation translation = new I18NTranslation();
                translation.setLanguage(language);
                translation.setValue(bundle.getString(bundleKey));
                translations.add(translation);
            }
            property.setTranslations(translations);
            allProperties.add(property);
        }
    }

    public List<Locale> getLocales() {
        return locales;
    }

    public List<SystemI18NProperty> getAllProperties() {
        return allProperties;
    }
}
