package io.radien;

import io.radien.api.KeycloakConfigs;
import io.radien.api.SystemProperties;
import io.radien.exception.InternalServerErrorException;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

import static io.radien.api.OAFProperties.RADIEN_ENV;

public class Authenticator {

    private final Map<String, String> properties;

    private final Config config;

    public Authenticator() {
        config = ConfigProvider.getConfig();
        if(getProperty(RADIEN_ENV).equalsIgnoreCase("LOCAL")){
            Unirest.config().verifySsl(false);
        }
        HttpResponse<?> response = Unirest.post(getProperty(KeycloakConfigs.IDP_URL).concat(getProperty(KeycloakConfigs.TOKEN_PATH)))
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
                                .field("client_id", getProperty(KeycloakConfigs.ADMIN_CLIENT_ID))
                                .field("grant_type", "password")
                                .field("client_secret", getProperty(KeycloakConfigs.ADMIN_CLIENT_SECRET))
                                .field("username", getProperty(KeycloakConfigs.RADIEN_USERNAME))
                                .field("password", getProperty(KeycloakConfigs.RADIEN_PASSWORD))
                                .asObject(HashMap.class);
        if(!response.isSuccess()){
            throw new InternalServerErrorException("Error on keycloak login: " + response.getBody().toString());
        }
        properties = (Map<String, String>) response.getBody();
    }

    public String getAuthorization(){
        return "Bearer ".concat(properties.get("access_token"));
    }

    private String getProperty(SystemProperties property){
        return config.getValue(property.propKey(), String.class);
    }
}
