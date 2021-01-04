package io.radien.ms.usermanagement.service;

import io.radien.api.Event;
import io.radien.api.OAFAccess;
import io.radien.api.SystemProperties;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import javax.ejb.Stateless;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@Stateless
public class ConfigService implements OAFAccess, Serializable {

    private static final long serialVersionUID = 224814422013232692L;

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public String getProperty(SystemProperties cfg) {
        Config config = ConfigProvider.getConfig();
        return config.getValue(cfg.propKey(),String.class);
    }

    @Override
    public ResourceBundle getResourceBundle(String bundleName) {
        return null;
    }

    @Override
    public void fireEvent(Event event) {

    }

    @Override
    public Locale getDefaultLocale() {
        return null;
    }

    @Override
    public Map<String, Locale> getSupportedLocales() {
        return null;
    }

    @Override
    public Locale findLocale(String language) {
        return null;
    }
}
