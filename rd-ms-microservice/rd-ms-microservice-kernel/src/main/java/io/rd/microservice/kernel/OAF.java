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
package io.rd.microservice.kernel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import io.rd.microservice.api.Event;
import io.rd.microservice.api.OAFAccess;
import io.rd.microservice.api.OAFProperties;
import io.rd.microservice.api.SystemProperties;
import io.rd.microservice.api.messages.SystemMessages;
import io.rd.microservice.kernel.eventbus.EventBus;

/**
 * Main OAF class that initializes application properties and manages the
 * eventBus
 * @author Rajesh Gavvala
 * @author Marco Weiland
 * 
 */
public abstract class OAF implements OAFAccess {

    protected static final long serialVersionUID = 6812608123262000031L;

	private static final Logger log = LoggerFactory.getLogger(OAF.class);

    private static final Marker SYSTEM_MARKER = MarkerFactory.getMarker("SYSTEM");

    /** The main system properties file */
    private static final String SYSTEM_EXT_CONFIG_FILE = "system-ext.properties";
    /** The external properties file */
    private static final String SYSTEM_CONFIG_FILE = "system.properties";

    /** Manifest file path */
    private static final String SYSTEM_MANIFEST_FILE = "/META-INF/MANIFEST.MF";
    /** Web app classes path */
    private static final String WEB_INF_CLASSES = "/WEB-INF/classes";

    /** The main Openappframe event bus */
    @Inject
    private EventBus eventBus;

    /** Object where all oaf properties are stored */
    private Properties systemProperties = new Properties();

    /** The SystemMessages enum value list */
    private SystemMessages[] systemMessages;

    private Map<String, Locale> supportedLocales = new HashMap<>();

    /**
     * On bean initialization, loads manifest properties and system properties
     * into corresponding objects
     */
    @PostConstruct
    void initApplication() {
        log.info(SYSTEM_MARKER, "[OAF] Initializing Base application --> {}", this);
        systemMessages = SystemMessages.values();
        loadManifest();
        loadSystemProperties();
        log.info(SYSTEM_MARKER, "[OAF] Base Application initialized --> {}", this);
    }

    /**
     * Obtains the given {@link AbstractAppframePlugin} object manifest and
     * properties
     *
     * @param plugin
     *                   the {@link AbstractAppframePlugin} object to analyze
     */
    void loadPlugin(AbstractAppframePlugin plugin) {
        loadPluginManifest(plugin);
        readPluginProperties(plugin.getPluginProperties());
        log.info(SYSTEM_MARKER, "[OAF] Base Application specialized by {}", plugin.getClass().getSimpleName());
    }

    /**
     * reads this class application manifest file adding any property to the
     * system properties object
     */
    private void loadManifest() {
        InputStream manifestStream = null;
        try {

            String resource = "/" + this.getClass().getName().replace(".", "/") + ".class";
            String archivePath = getArchivePath(resource);
            manifestStream = new URL(archivePath + SYSTEM_MANIFEST_FILE).openStream();
            Manifest manifest = new Manifest(manifestStream);

            Attributes attributes = manifest.getMainAttributes();
            for (Map.Entry<Object, Object> entry : attributes.entrySet()) {
                loadPropertiesFromAttributesObject(attributes, entry, SystemMessages.KERNEL_PROPERTIES_ERROR.message());
            }
        } catch (IOException ex) {
            log.error(SystemMessages.KERNEL_MANIFEST_ERROR.message(), ex);
        } finally {
            try {
                if (manifestStream != null) {
                    manifestStream.close();
                }
            } catch (IOException e) {
                log.error(SystemMessages.KERNEL_MANIFEST_ERROR.message(), e);
            }
        }
    }

    /**
     * Loads properties into this class {@link Properties} object based on the
     * {@link Attributes} object obtained when reading plugin or app files
     *
     * @param attributes
     *                       the object containing the initial properties
     * @param entry
     *                       the Mao entry containing property key and its value
     * @param errorMsg
     *                       error message logged in case of an error
     */
    private void loadPropertiesFromAttributesObject(Attributes attributes, Map.Entry<Object, Object> entry,
            String errorMsg) {
        try {
            Object key = entry.getKey();
            String keyString = String.valueOf(key);
            String object = "" + attributes.get(key);
            systemProperties.setProperty(keyString, object);
        } catch (Exception e) {
            log.error(errorMsg, e);
        }
    }

    /**
     * Method that reads the system and external file properties and load them
     * into this class {@link Properties} object
     */
    void loadSystemProperties() {
        InputStream stream = null;
        try {
            stream = getClass().getClassLoader().getResourceAsStream(SYSTEM_CONFIG_FILE);
            systemProperties.load(stream);
        } catch (Exception e) {
            log.error(SystemMessages.KERNEL_PROPERTIES_ERROR.message(), e);
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                log.error(SystemMessages.KERNEL_PROPERTIES_ERROR.message(), e);
            }
        }

        loadPropertiesFromCatalinaLocation(SYSTEM_EXT_CONFIG_FILE, SystemMessages.KERNEL_PROPERTIES_ERROR.message());
        loadSupportedLocales();
    }

    /**
     * Reads the properties from files that are located in the container conf
     * folder (normally tomcat) It locates them by the system property
     * 'catalina.base'
     *
     * @param systemExtConfigFile
     *                                the name of the properties file inside the
     *                                tomcat conf folder
     * @param s
     *                                error message to be logged in case of an
     *                                error
     */
    private void loadPropertiesFromCatalinaLocation(String systemExtConfigFile, String s) {
        File configDir = new File(System.getProperty("catalina.base"), "conf");
        File configFile = new File(configDir, systemExtConfigFile);
        try (InputStream stream = new FileInputStream(configFile)) {
            systemProperties.load(stream);
        } catch (Exception e) {
            log.info(s, e);
        }
    }

    /**
     * Loads the given {@link AbstractAppframePlugin} object into the
     * {@link Properties} object
     *
     * @param plugin
     *                   the {@link AbstractAppframePlugin} object to analyze
     */
    private void loadPluginManifest(AbstractAppframePlugin plugin) {
        InputStream manifestStream = null;
        try {

            String resource = "/" + plugin.getClass().getName().replace(".", "/") + ".class";
            String archivePath = getArchivePath(resource);
            manifestStream = new URL(archivePath + SYSTEM_MANIFEST_FILE).openStream();
            Manifest manifest = new Manifest(manifestStream);

            Attributes attributes = manifest.getMainAttributes();
            for (Map.Entry<Object, Object> entry : attributes.entrySet()) {
                loadPropertiesFromAttributesObject(attributes, entry, SystemMessages.PLUGIN_MANIFEST_ERROR.message());
            }
        } catch (IOException ex) {
            log.error(SystemMessages.PLUGIN_MANIFEST_ERROR.message(), ex);
        } finally {
            try {
                if (manifestStream != null) {
                    manifestStream.close();
                }
            } catch (IOException e) {
                log.error(SystemMessages.PLUGIN_MANIFEST_ERROR.message(), e);
            }
        }
    }

    /**
     * calls the {@link OAF#loadPropertiesFromCatalinaLocation(String, String)}
     *
     * @param contextPropertyFileName
     *                                    the file name from which the
     *                                    properties are laoded
     */
    private void readPluginProperties(String contextPropertyFileName) {
        loadPropertiesFromCatalinaLocation(contextPropertyFileName, SystemMessages.KERNEL_PROPERTIES_ERROR.message());
    }



    /**
     * Iterates the supported Locales list and tries to find the one identified
     * by the given value
     *
     * @param value
     *                  the locale identifier
     * @return the {@link Locale} object identified by the given value param
     */
    @Override
    public Locale findLocale(String value) {
        try {
            for (String key : getSupportedLocales().keySet()) {
                Locale locale = getSupportedLocales().get(key);
                if (locale.toLanguageTag().equalsIgnoreCase(value)) {
                    return locale;
                }
            }
        } catch (Exception e) {
            log.error(SystemMessages.KERNEL_LOCALE_ERROR.message(), e);
        }

        return getDefaultLocale();
    }

    /**
     * Gets the file path based on the given resource path
     *
     * @param resource
     *                     the resource, normally properties file, from
     * @return a String containing the correct file path
     */
    @NotNull
    private String getArchivePath(String resource) {
        String fullPath = this.getClass().getResource(resource).toString();
        String archivePath = fullPath.substring(0, fullPath.length() - resource.length());
        if (archivePath.endsWith("\\WEB-INF\\classes") || archivePath.endsWith(WEB_INF_CLASSES)) {
            archivePath = archivePath.substring(0, archivePath.length() - WEB_INF_CLASSES.length());
        }
        return archivePath;
    }

    /**
     * Fires an implementation of a {@link Event} onto this class
     * {@link OAF#eventBus}
     *
     * @param event
     *                  the Openappframe event to be fired on the CDI eventBus
     */
    @Override
    public void fireEvent(Event event) {
        eventBus.fireEvent(event);
    }

    /**
     * Returns the current application version
     *
     * @return String containing the value of the version
     */
    @Override
    public String getVersion() {
        return getProperty(OAFProperties.SYS_MF_APP_VERSION);
    }
    
    /**
     * Calls the method {@link OAF#getProperty(String)} using keys in the
     * {@link OAFProperties} enum
     *
     * @param cfg
     *                the value of the {@link OAFProperties} enum
     * @return String containing the property value
     */
    @Override
    public String getProperty(SystemProperties cfg) {
        return getProperty(cfg.propKey());
    }

    /**
     * Returns the property value based on its identifying key
     *
     * @param propKey
     *                    the property identifier
     * @return String containing the property value
     */
    protected String getProperty(String propKey) {
        String codeInfo = null;
        try {
            codeInfo = systemProperties.getProperty(propKey);
        } catch (Exception e) {
            log.error("{}: {}", SystemMessages.KERNEL_PROPERTY_UNAVAILABLE.message(), propKey, e);
        }
        if (codeInfo == null) {
            return SystemMessages.KERNEL_PROPERTY_UNAVAILABLE.message() + propKey;
        }
        return codeInfo;
    }

    /**
     * Similar to {@link OAF#getProperty(String)} but with and additional value
     * to be returned if the key is not found in the properties
     *
     * @param propKey
     *                         the property identifier
     * @param defaultValue
     *                         the default value returned if this key is not
     *                         present in the properties
     * @return string containing the property value
     */
    protected String getProperty(String propKey, String defaultValue) {
        String codeInfo = defaultValue;
        if (StringUtils.isNotEmpty(propKey)) {
            try {
                codeInfo = systemProperties.getProperty(propKey);
            } catch (Exception e) {
                log.error("{}: {}", SystemMessages.KERNEL_PROPERTY_UNAVAILABLE.message(), propKey, e);
            }
            if (codeInfo == null) {
                return defaultValue;
            }
        }
        return codeInfo;
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
        FacesContext context = FacesContext.getCurrentInstance();
        Locale locale = context.getViewRoot().getLocale();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return ResourceBundle.getBundle(bundleName, locale, loader, new UTF8Control());
    }

    @Override
    public Map<String, Locale> getSupportedLocales() {
        return supportedLocales;
    }

    private void loadSupportedLocales() {
        supportedLocales = new HashMap<>();
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

    public SystemMessages[] getSystemMessages() {
        return systemMessages;
    }

}
