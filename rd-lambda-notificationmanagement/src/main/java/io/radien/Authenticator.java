package io.radien;

import com.google.inject.Singleton;
import io.radien.api.KeycloakConfigs;
import io.radien.exception.InternalServerErrorException;
import io.radien.util.PropertyProvider;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static io.radien.api.OAFProperties.RADIEN_ENV;

@Singleton
public class Authenticator {

    private Map<String, String> properties = new HashMap<>(); //init for testing

    private static final String DEFAULT_ENV = "LOCAL";


    @PostConstruct
    public void init(){
        if(getEnv().equalsIgnoreCase("LOCAL")){
            Unirest.config().verifySsl(false);
        }
        HttpResponse<?> response = Unirest.post(PropertyProvider.getProperty(KeycloakConfigs.IDP_URL).concat(
                                                PropertyProvider.getProperty(KeycloakConfigs.TOKEN_PATH)))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
                .field("client_id", PropertyProvider.getProperty(KeycloakConfigs.ADMIN_CLIENT_ID))
                .field("grant_type", "password")
                .field("client_secret", PropertyProvider.getProperty(KeycloakConfigs.ADMIN_CLIENT_SECRET))
                .field("username", PropertyProvider.getProperty(KeycloakConfigs.RADIEN_USERNAME))
                .field("password", PropertyProvider.getProperty(KeycloakConfigs.RADIEN_PASSWORD))
                .asObject(HashMap.class);
        if(!response.isSuccess()){
            throw new InternalServerErrorException("Error on keycloak login: " + response.getBody().toString());
        }
        properties = (Map<String, String>) response.getBody();
    }


    public String getAuthorization(){
        return "Bearer ".concat(testFuncGetProperties().get("access_token"));
    }

    public Map<String, String> testFuncGetProperties(){
        return properties;
    }

    private String getEnv(){
        try{
            return PropertyProvider.getProperty(RADIEN_ENV);
        }catch(NoSuchElementException e){
            return DEFAULT_ENV;
        }
    }
}
