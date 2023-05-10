package io.radien.ms.config.lib.sources;

import io.radien.api.kernel.messages.SystemMessages;
import io.radien.ms.config.lib.MSOAF;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemExternalConfigSource implements ConfigSource {
    private static Logger log = LoggerFactory.getLogger(SystemExternalConfigSource.class);
    private static final String SYSTEM_EXT_CONFIG_FILE = "system-ext.properties";

    private final Map<String, String> systemProperties = new HashMap<>();

    public SystemExternalConfigSource() {
        getExternalProperties();
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
        return 120;
    }

    @Override
    public String getValue(String s) {
        return systemProperties.get(s);
    }

    @Override
    public String getName() {
        return "system-ext";
    }

    protected void getExternalProperties() {
        File configDir = new File(System.getProperty("catalina.base"), "conf");
        File configFile = new File(configDir, SYSTEM_EXT_CONFIG_FILE);
        try (InputStream stream = Files.newInputStream(configFile.toPath())) {
            Properties propertiesList = new Properties();
            propertiesList.load(stream);
            propertiesList.stringPropertyNames().forEach(name -> {
                systemProperties.put(name, propertiesList.getProperty(name));
            });
        } catch (Exception e) {
            log.info(SystemMessages.KERNEL_PROPERTIES_ERROR.message(), e);
        }
    }
}
