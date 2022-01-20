package io.radien.ms.ticketmanagement.config;

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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@ApplicationScoped
public class TicketConfiguration implements OAFAccess {

    private final Config config;

    private Map<String, Locale> supportedLocales;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * ticket OAF Configuration constructor
     */
    public TicketConfiguration() {
        config = ConfigProvider.getConfig();
        supportedLocales = new HashMap<>();
    }

    /**
     * Configuration ticket version getter
     * @return the configuration permission version
     */
    @Override
    public String getVersion() {
        return null;
    }

    /**
     * Configuration ticket system administrator user id getter
     * @return the ticket oaf configuration system administrator user id
     */
    @Override
    public Long getSystemAdminUserId() {
        return 0L;
    }

    /**
     * Configuration ticket fire event caller
     */
    @Override
    public void fireEvent(Event event) {
        // empty
    }

    /**
     * Configuration ticket find locale getter
     * @return the ticket oaf configuration found locale for the required language
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
     * Configuration ticket property endpoint getter
     * @return the ticket oaf configuration property
     */
    @Override
    public String getProperty(SystemProperties cfg) {
        return config.getValue(cfg.propKey(), String.class);
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
     * Configuration ticket supported locales list getter
     * @return a map of ticket oaf configuration supported locales
     */
    @Override
    public Map<String, Locale> getSupportedLocales() {
        loadSupportedLocales();
        return supportedLocales;
    }

    /**
     * Configuration ticket load of all the supported locales
     */
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
     * Configuration ticket default locale getter
     * @return the ticket oaf configuration default locale
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
