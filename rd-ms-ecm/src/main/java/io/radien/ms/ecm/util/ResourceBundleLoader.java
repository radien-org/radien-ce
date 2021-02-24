package io.radien.ms.ecm.util;

import io.radien.ms.ecm.client.entities.I18NProperty;
import io.radien.ms.ecm.client.entities.LabelTypeEnum;
import io.radien.ms.ecm.client.entities.Translation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourceBundleLoader implements Serializable {
    private static final long serialVersionUID = -8943670686943378024L;
    private static final Logger log = LoggerFactory.getLogger(ResourceBundleLoader.class);
    /**
     * The default resource bundle for i18n support
     */
    public static final String DEFAULT_BUNDLE_NAME = "i18n/Language";
    private final List<Locale> locales;

    private List<I18NProperty> allProperties;

    public ResourceBundleLoader(String availableLanguages, String defaultLanguage) {
        allProperties = new LinkedList<>();
        locales = Stream.of(availableLanguages.split(",")).map(Locale::forLanguageTag).collect(Collectors.toCollection(LinkedList::new));
        ResourceBundle defaultBundle = ResourceBundle.getBundle(DEFAULT_BUNDLE_NAME, Locale.ROOT);
        List<String> bundleKeys = Collections.list(defaultBundle.getKeys());
        List<ResourceBundle> bundleList = locales.stream().map(l -> ResourceBundle.getBundle(DEFAULT_BUNDLE_NAME, l)).collect(Collectors.toList());

        for (String bundleKey : bundleKeys) {
            I18NProperty newProp = new I18NProperty();
            newProp.setKey(bundleKey);
            newProp.setType(LabelTypeEnum.MESSAGE);
            List<Translation> propTranslations = new LinkedList<>();
            for (ResourceBundle resourceBundle : bundleList) {
                String language = resourceBundle.getLocale().toLanguageTag();
                if(StringUtils.isBlank(resourceBundle.getLocale().toLanguageTag()) || language.equals("und")) {
                    language = defaultLanguage;
                    //THIS LOG MESSAGE MUST NOT BE CHANGED, IT IS USED TO CREATE AUTMATES ALERTS ON MISSING KEYS / TRANSLATIONS
                    try {
                        log.error("[MessagesResourceBundle] could not load language/locale for bundlekey: {} and language: {}",bundleKey,language);
                    } catch (Exception e) {
                        log.error("[MessagesResourceBundle] could not load language/locale",e);
                    }

                }
                Translation translation = new Translation();
                translation.setLanguage(language);
                translation.setDescription(resourceBundle.getString(bundleKey));
                propTranslations.add(translation);
            }
            newProp.setTranslations(propTranslations);
            allProperties.add(newProp);
        }
    }

    public List<I18NProperty> getAllProperties() {
        return this.allProperties;
    }

    /**
     * List of supported locales
     */
    public List<Locale> getLocales() {
        return this.locales;
    }

}
