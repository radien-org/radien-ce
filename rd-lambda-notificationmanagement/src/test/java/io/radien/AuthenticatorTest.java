package io.radien;

import io.radien.api.KeycloakConfigs;
import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.exception.InternalServerErrorException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticatorTest {

    @Mock
    private OAFAccess oaf;

    @InjectMocks
    private Authenticator authenticator;

    @Before
    public void init(){
        when(oaf.getProperty(OAFProperties.RADIEN_ENV, "PROD")).thenReturn("LOCAL");
        when(oaf.getProperty(KeycloakConfigs.IDP_URL)).thenReturn("https://host.docker.internal:8343");
        when(oaf.getProperty(KeycloakConfigs.TOKEN_PATH)).thenReturn("/auth/realms/radien/protocol/openid-connect/token");
        when(oaf.getProperty(KeycloakConfigs.ADMIN_CLIENT_ID)).thenReturn("radien");
        when(oaf.getProperty(KeycloakConfigs.ADMIN_CLIENT_SECRET)).thenReturn("86c27042-b3a2-48ea-a936-025103144844");
        when(oaf.getProperty(KeycloakConfigs.RADIEN_USERNAME)).thenReturn("scorpion");
        when(oaf.getProperty(KeycloakConfigs.RADIEN_PASSWORD)).thenReturn("brutality");

    }

    @Test(expected = InternalServerErrorException.class)
    public void loginException(){
        when(oaf.getProperty(KeycloakConfigs.RADIEN_PASSWORD)).thenReturn("d");
        authenticator.login();
    }

    @Test
    public void getAccessToken(){
        authenticator.login();
        assertNotNull(authenticator.getAccessToken());
    }

    @Test
    public void getAuthorization(){
        authenticator.login();
        assertTrue(authenticator.getAuthorization().length() > "Bearer ".length());
    }
}