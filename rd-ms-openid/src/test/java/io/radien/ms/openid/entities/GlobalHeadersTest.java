package io.radien.ms.openid.entities;

import io.radien.api.security.TokensPlaceHolder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalHeadersTest {

    @InjectMocks
    GlobalHeaders target;

    @Mock
    TokensPlaceHolder tokensPlaceHolder;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUpdate() {
        //tokensPlaceHolder = mock(tokensPlaceHolder.getClass());
        when(tokensPlaceHolder.getAccessToken()).thenReturn("Batata");
        MultivaluedMap<String, String> incomingHeaders = new MultivaluedHashMap<>();
        MultivaluedMap<String, String> outgoingHeaders = new MultivaluedHashMap<>();
        MultivaluedMap<String, String> result = target.update(incomingHeaders,outgoingHeaders);
        assertEquals("Bearer Batata",result.getFirst("Authorization"));
    }
}