package io.radien.ms.config.lib.sources;

import io.radien.api.kernel.messages.SystemMessages;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemConfigSource implements ConfigSource {
    private static Logger log = LoggerFactory.getLogger(SystemConfigSource.class);
    private static final String SYSTEM_CONFIG_FILE = "system.properties";

    private final Map<String, String> systemProperties = new HashMap<>();

    public SystemConfigSource() {
        getSystemProperties();
    }

    @Override
    public Map<String, String> getProperties() {
        return systemProperties;
    }

    @Override
    public Set<String> getPropertyNames() {
        return systemProperties.keySet();
    }

    @Override
    public int getOrdinal() {
        return 110;
    }

    @Override
    public String getValue(String s) {
        return systemProperties.get(s);
    }

    @Override
    public String getName() {
        return "system";
    }

    protected void getSystemProperties() {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(SYSTEM_CONFIG_FILE)) {
            if(stream != null) {
                Properties propertiesList = new Properties();
                propertiesList.load(stream);
                if (!propertiesList.isEmpty()) {
                    propertiesList.stringPropertyNames().forEach(name -> {
                        systemProperties.put(name, propertiesList.getProperty(name));
                    });
                }
            }
        } catch (Exception e) {
            log.info(SystemMessages.KERNEL_PROPERTIES_ERROR.message(), e);
        }
        systemProperties.putAll(System.getenv());
    }
}
