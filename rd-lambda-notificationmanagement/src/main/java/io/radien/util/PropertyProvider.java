package io.radien.util;

import io.radien.api.SystemProperties;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

public class PropertyProvider {

    private static final Config config = ConfigProvider.getConfig();

    private PropertyProvider(){}

    public static String getProperty(SystemProperties property){
        return config.getValue(property.propKey(), String.class);
    }
}
