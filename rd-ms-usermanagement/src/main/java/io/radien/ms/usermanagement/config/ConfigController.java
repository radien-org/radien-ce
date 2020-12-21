package io.radien.ms.usermanagement.config;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

@Path("/config")
@RequestScoped
public class ConfigController {

    @Path("/injected")
    @GET
    public String getInjectedConfigValue() {
        return "Config value as Injected by CDI ";
    }

    //TODO: Check why configurations are giving errors

    @Path("/lookup")
    @GET
    public String getLookupConfigValue() {
        Config config = ConfigProvider.getConfig();
        String value = config.getValue("value", String.class);
        return "Config value from ConfigProvider " + value;
    }
}
