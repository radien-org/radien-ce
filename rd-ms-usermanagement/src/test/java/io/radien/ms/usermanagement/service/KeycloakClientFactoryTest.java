package io.radien.ms.usermanagement.service;

import io.radien.api.KeycloakConfigs;
import io.radien.api.OAFProperties;
import io.radien.ms.usermanagement.client.exceptions.RemoteResourceException;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import kong.unirest.MultipartBody;
import kong.unirest.Unirest;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;

import static io.radien.api.OAFProperties.RADIEN_ENV;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.reflect.Whitebox.setInternalState;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({ "javax.net.ssl.*" })
@PrepareForTest({ Unirest.class})
public class KeycloakClientFactoryTest {

    @InjectMocks
    KeycloakClientFactory clientFactory;
    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(Unirest.class);
        MultipartBody multipartBody = mock(MultipartBody.class);
        HttpRequestWithBody requestWithBody = mock(HttpRequestWithBody.class);
        HttpResponse<HashMap> response = mock(HttpResponse.class);
        Config config =mock(Config.class);

        String idpUrl = "http://localhost:8080";
        String rdTokenPath = "/TOKEN";
        String endpointUrl = idpUrl + rdTokenPath;
        String rdClientId = "CLIENT";
        String rdClientSecret = "SECRET";
        String rdUserPath = "/USER";


        when(config.getValue(KeycloakConfigs.TOKEN_PATH.propKey(),String.class)).thenReturn(rdTokenPath);
        when(config.getValue(KeycloakConfigs.USER_PATH.propKey(),String.class)).thenReturn(rdUserPath);
        when(config.getValue(KeycloakConfigs.ADMIN_CLIENT_ID.propKey(),String.class)).thenReturn(rdClientId);
        when(config.getValue(KeycloakConfigs.ADMIN_CLIENT_SECRET.propKey(),String.class)).thenReturn(rdClientSecret);
        when(config.getValue(KeycloakConfigs.RADIEN_CLIENT_ID.propKey(),String.class)).thenReturn(rdClientId);
        when(config.getValue(KeycloakConfigs.RADIEN_SECRET.propKey(),String.class)).thenReturn(rdClientSecret);
        when(config.getValue(KeycloakConfigs.RADIEN_TOKEN_PATH.propKey(),String.class)).thenReturn(rdTokenPath);
        when(config.getValue(OAFProperties.RADIEN_ENV.propKey(),String.class)).thenReturn("LOCAL");
        when(config.getValue(KeycloakConfigs.IDP_URL.propKey(),String.class)).thenReturn(idpUrl);
        setInternalState(this.clientFactory, "config", config);


        when(Unirest.post(argThat(endpointUrl::contentEquals))).thenReturn(requestWithBody);
        when(requestWithBody.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)).
                thenReturn(requestWithBody);
        when(requestWithBody.field("client_id", rdClientId)).thenReturn(multipartBody);
        when(multipartBody.field("client_secret", rdClientSecret)).thenReturn(multipartBody);
        when(multipartBody.field("grant_type", "client_credentials")).thenReturn(multipartBody);
        when(multipartBody.asObject(HashMap.class)).thenReturn(response);

        when(response.isSuccess()).thenReturn(Boolean.TRUE);
    }

    @Test
    public void getKeycloakClient() throws RemoteResourceException {
        assertNotNull(clientFactory.getKeycloakClient());
    }
}