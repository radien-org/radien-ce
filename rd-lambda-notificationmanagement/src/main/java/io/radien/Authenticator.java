package io.radien;

import com.google.inject.Singleton;
import io.radien.api.KeycloakConfigs;
import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.exception.InternalServerErrorException;
import javax.inject.Inject;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class Authenticator {
    private Map<String, String> properties = new HashMap<>(); //init for testing
    private final OAFAccess oaf;

    @Inject
    public Authenticator(OAFAccess oaf) {
        this.oaf = oaf;
    }

    public void login(){
        if(oaf.getProperty(OAFProperties.RADIEN_ENV, "PROD").equalsIgnoreCase("LOCAL")){
            Unirest.config().verifySsl(false);
        }
        HttpResponse<?> response = Unirest.post(oaf.getProperty(KeycloakConfigs.IDP_URL).concat(
                                                oaf.getProperty(KeycloakConfigs.TOKEN_PATH)))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
                .field("client_id", oaf.getProperty(KeycloakConfigs.ADMIN_CLIENT_ID))
                .field("grant_type", "password")
                .field("client_secret", oaf.getProperty(KeycloakConfigs.ADMIN_CLIENT_SECRET))
                .field("username", oaf.getProperty(KeycloakConfigs.RADIEN_USERNAME))
                .field("password", oaf.getProperty(KeycloakConfigs.RADIEN_PASSWORD))
                .asObject(HashMap.class);
        if(!response.isSuccess()){
            throw new InternalServerErrorException("Error on keycloak login: " + response.getBody().toString());
        }
        properties = (Map<String, String>) response.getBody();
    }

    public String getAuthorization(){
        return "Bearer ".concat(properties.get("access_token"));
    }
}
