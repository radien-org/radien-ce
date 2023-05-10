package io.radien.webapp.security;

import io.radien.api.OAFAccess;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertNotNull;

public class AuthorizationFilterTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    private AuthorizationFilter client;

    @Mock
    private OAFAccess oaf;

    @Test
    public void testAccessingOAF() {
        OAFAccess oafAccess = client.getOAF();
        assertNotNull(oafAccess);
    }
}