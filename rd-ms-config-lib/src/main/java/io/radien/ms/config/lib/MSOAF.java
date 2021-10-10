/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.ms.config.lib;

import io.radien.api.Event;
import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.SystemProperties;
import io.radien.api.kernel.messages.SystemMessages;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller is responsible for handling users actions on the page. It also manages the flow between pages.
 * Controller responds to the user actions and directs application flow.
 * The controller will request from HTTP get and HTTP post.
 */
@ApplicationScoped
@Default
public class MSOAF implements OAFAccess {

    private final transient Config config;

    private Map<String, Locale> supportedLocales = new HashMap<>();

    private static transient Logger log = LoggerFactory.getLogger(MSOAF.class);

    /**
     *Micro Service OAF Configuration constructor
     */
    public MSOAF() {
        this.config = ConfigProvider.getConfig();
    }

    /**
     * OAF Configuration controller version getter method
     * @return the oaf configuration controller version
     */
    @Override
    public String getVersion() {
        return null;
    }

    /**
     * OAF Configuration controller system administrator user id getter
     * @return the oaf configuration controller system administrator id
     */
    @Override
    public Long getSystemAdminUserId() {
        return 0L;
    }

    /**
     * OAF Configuration controller event launcher
     * @param event to be launch
     */
    @Override
    public void fireEvent(Event event) {
        log.error("fireEvent unsupported");
    }

    /**
     * OAF Configuration controller locale finder to get the correct language
     * @param language to be requested
     * @return the locale of the language of the oaf configuration controller
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
     * OAF Configuration controller property getter
     * @param cfg system properties configuration
     * @return the system properties property key value as a string
     */
    @Override
    public String getProperty(SystemProperties cfg) {
        return config.getValue(cfg.propKey(),String.class);
    }

    /**
     * Returns this application {@link ResourceBundle} object based on its name
     *
     * @param bundleName the name of the resourceBundle to return
     * @return {@link ResourceBundle} identified by the bundleName
     */
    @Override
    public ResourceBundle getResourceBundle(String bundleName) {
        Locale locale = Locale.getDefault();
        return ResourceBundle.getBundle(bundleName, locale);
    }

    /**
     * Method to return a complete list of all the supported locales for this specific oaf configuration controller
     * @return a map of supported and possible locales with language tag and locale
     */
    @Override
    public Map<String, Locale> getSupportedLocales() {
        loadSupportedLocales();
        return supportedLocales;
    }

    /**
     * OAF Configuration controller method to load all the supported locales
     */
    private void loadSupportedLocales() {
        try {
            for (String languageTag : getProperty(OAFProperties.SYS_SUPPORTED_LOCALES).split(",")) {
                setSupportLocales(languageTag);
            }
        } catch (Exception e) {
            log.error(SystemMessages.KERNEL_LOCALE_ERROR.message(), e);
        }
    }

    private void setSupportLocales(String languageTag) {
        try {
            Locale locale = Locale.forLanguageTag(languageTag);
            supportedLocales.put(locale.toLanguageTag(), locale);
            log.info("[OAF] added locale {}", locale);
        } catch (Exception e) {
            log.error("[OAF] IETF BCP 47 languageTag {} for registering supported locale is not supported.", languageTag);
        }
    }

    /**
     * OAF Configuration controller default locale getter if no default has been found then it will be returned
     * locale for language english
     * @return the default locale for the oaf configuration controller
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
