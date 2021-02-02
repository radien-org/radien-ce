package io.radien.ms.usermanagement.config;

import io.radien.api.Event;
import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.SystemProperties;
import io.radien.api.kernel.messages.SystemMessages;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@RequestScoped
public class UmmsOAF implements OAFAccess {

    private final Config config;

    private Map<String, Locale> supportedLocales = new HashMap<>();

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public UmmsOAF() {
        this.config = ConfigProvider.getConfig();
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public Long getSystemAdminUserId() {
        return 0L;
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
