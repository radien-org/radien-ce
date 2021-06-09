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
package io.radien.ms.permissionmanagement.config;

import io.radien.api.OAFAccess;
import io.radien.api.Event;
import io.radien.api.SystemProperties;
import io.radien.api.OAFProperties;
import io.radien.api.kernel.messages.SystemMessages;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Permission management oaf access configuration
 * @author Bruno Gama
 */
@ApplicationScoped
public class ConfigPermission implements OAFAccess {

    private final Config config;

    private Map<String, Locale> supportedLocales = new HashMap<>();

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public ConfigPermission() {
        config = ConfigProvider.getConfig();
    }

    /**
     * Configuration Permission version getter
     * @return the configuration permission version
     */
    @Override
    public String getVersion() {
        return null;
    }

    /**
     * Configuration Permission system administrator user id getter
     * @return the permission oaf configuration system administrator user id
     */
    @Override
    public Long getSystemAdminUserId() {
        return 0L;
    }

    /**
     * Configuration Permission fire event caller
     */
    @Override
    public void fireEvent(Event event) {

    }

    /**
     * Configuration Permission find locale getter
     * @return the permission oaf configuration found locale for the required language
     */
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

    /**
     * Configuration Permission property endpoint getter
     * @return the permission oaf configuration property
     */
    @Override
    public String getProperty(SystemProperties cfg) {
        return config.getValue(cfg.propKey(),String.class);
    }

    /**
     * Returns this application {@link ResourceBundle} object based on its name
     * @param bundleName the name of the resourceBundle to return
     * @return {@link ResourceBundle} identified by the bundleName
     */
    @Override
    public ResourceBundle getResourceBundle(String bundleName) {
        Locale locale = Locale.getDefault();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return ResourceBundle.getBundle(bundleName, locale);
    }

    /**
     * Configuration Permission supported locales list getter
     * @return a map of permission oaf configuration supported locales
     */
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

    /**
     * Configuration Permission default locale getter
     * @return the permission oaf configuration default locale
     */
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
