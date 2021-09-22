package io.radien.ms.usermanagement.service;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import java.util.Iterator;

@ApplicationScoped
@Startup
public class ConfigInfo {
    protected static final Logger log = LoggerFactory.getLogger(ConfigInfo.class);

    @PostConstruct
    public void init(){
        Iterator<ConfigSource> iterator = ConfigProvider.getConfig().getConfigSources().iterator();
        log.info("Configuration sources begin");
        StringBuilder stringBuilder = new StringBuilder();
        while (iterator.hasNext()){
            ConfigSource current = iterator.next();
            stringBuilder.append(String.format("Source %s%n",current.getName()));
            for (String property : current.getProperties().keySet()) {
                stringBuilder.append(String.format("\tkey: %s%n", property));
            }
        }
        String msg = stringBuilder.toString();
        log.info(msg);
    }
}
