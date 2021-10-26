/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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
package io.rd.ms.service.config;

import io.rd.api.Event;
import io.rd.api.OAFAccess;
import io.rd.api.OAFProperties;
import io.rd.api.SystemProperties;
import io.rd.api.messages.SystemMessages;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
/**
 * @author Rajesh Gavvala
 * @author mawe
 */
@RequestScoped
public class DemomsOAF implements OAFAccess {
    private static final long serialVersionUID = 6812608123262000061L;

    private final Config config;

    private Map<String, Locale> supportedLocales = new HashMap<>();

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public DemomsOAF() {
        this.config = ConfigProvider.getConfig();
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public void fireEvent(Event event) {

    }

    @Override
    public Locale findLocale(String language) {
        try {
            for (String key : getSupportedLocales().keySet()) {
                Locale locale = getSupportedLocales().get(key);
                if (locale.toLanguageTag().equalsIgnoreCase(language)) {
                    return locale;
                }
            }
        } catch (Exception e) {
            log.error(SystemMessages.KERNEL_LOCALE_ERROR.message(), e);
        }
        return getDefaultLocale();
    }

    @Override
    public String getProperty(SystemProperties cfg) {
        return config.getValue(cfg.propKey(),String.class);
    }

    /**
     * Returns this application {@link ResourceBundle} object based on its name
     *
     * @param bundleName
     *                       the name of the resourceBundle to return
     * @return {@link ResourceBundle} identified by the bundleName
     */
    @Override
    public ResourceBundle getResourceBundle(String bundleName) {
        Locale locale = Locale.getDefault();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return ResourceBundle.getBundle(bundleName, locale);
    }

    @Override
    public Map<String, Locale> getSupportedLocales() {
        loadSupportedLocales();
        return supportedLocales;
    }

    private void loadSupportedLocales() {
        try {
            for (String languageTag : getProperty(OAFProperties.SYS_SUPPORTED_LOCALES).split(",")) {
                try {
                    Locale locale = Locale.forLanguageTag(languageTag);
                    supportedLocales.put(locale.toLanguageTag(), locale);
                    log.info("[OAF] added locale {}", locale);
                } catch (Exception e) {
                    log.error("[OAF] IETF BCP 47 languageTag {} for registering supported locale is not supported.", languageTag);
                }

            }
        } catch (Exception e) {
            log.error(SystemMessages.KERNEL_LOCALE_ERROR.message(), e);
        }
    }

    @Override
    public Locale getDefaultLocale() {
        try {
            return Locale.forLanguageTag(getProperty(OAFProperties.SYS_DEFAULT_LOCALE));
        } catch (Exception e) {
            log.error("[OAF] IETF BCP 47 languageTag {} for default locale is not supported.", getProperty(OAFProperties.SYS_DEFAULT_LOCALE));
        }
        return Locale.forLanguageTag("en-us");
    }

}
