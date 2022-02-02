package io.radien.ms.usermanagement.service;

import io.radien.ms.usermanagement.client.exceptions.RemoteResourceException;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import kong.unirest.MultipartBody;
import kong.unirest.Unirest;
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

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

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
        when(Unirest.post(argThat("http://localhost:8080/TOKEN"::contentEquals))).thenReturn(requestWithBody);
        when(requestWithBody.header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)).
                thenReturn(requestWithBody);
        when(requestWithBody.field("client_id", "CLIENT")).thenReturn(multipartBody);
        when(multipartBody.field("client_secret", "SECRET")).thenReturn(multipartBody);
        when(multipartBody.field("grant_type", "client_credentials")).thenReturn(multipartBody);
        when(multipartBody.asObject(HashMap.class)).thenReturn(response);

        when(response.isSuccess()).thenReturn(Boolean.TRUE);
    }

    @Test
    public void getKeycloakClient() throws RemoteResourceException {
        assertNotNull(clientFactory.getKeycloakClient());
    }
}