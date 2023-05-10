package io.radien.ms.doctypemanagement.client.entities;

import io.radien.api.security.TokensPlaceHolder;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import java.lang.reflect.Field;

import static org.mockito.Mockito.when;

public class GlobalHeadersTest extends TestCase {
    @InjectMocks
    GlobalHeaders target;

    @Mock
    TokensPlaceHolder tokensPlaceHolder;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUpdate() throws NoSuchFieldException, IllegalAccessException {
        tokensPlaceHolder = Mockito.mock(TokensPlaceHolder.class);
        Field tokensPlaceHolderField = target.getClass().getDeclaredField("tokensPlaceHolder");
        tokensPlaceHolderField.setAccessible(true);
        tokensPlaceHolderField.set(target, tokensPlaceHolder);

        when(tokensPlaceHolder.getAccessToken()).thenReturn("accessToken");
        MultivaluedMap<String, String> incomingHeaders = new MultivaluedHashMap<>();
        MultivaluedMap<String, String> outgoingHeaders = new MultivaluedHashMap<>();
        MultivaluedMap<String, String> result = target.update(incomingHeaders,outgoingHeaders);
        assertEquals("Bearer accessToken", result.getFirst("Authorization"));
    }

    @Test(expected = IllegalStateException.class)
    public void testUpdateNoInjection(){
        MultivaluedMap<String, String> incomingHeaders = new MultivaluedHashMap<>();
        MultivaluedMap<String, String> outgoingHeaders = new MultivaluedHashMap<>();
        MultivaluedMap<String, String> result = target.update(incomingHeaders,outgoingHeaders);
    }

}