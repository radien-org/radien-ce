package io.radien.security.openid.validation;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import io.radien.exception.InvalidAccessTokenException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.powermock.reflect.Whitebox.setInternalState;

/**
 * Test for class {@link OpenIdTokenValidator}
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({UrlJwkProvider.class, RSASSAVerifier.class})
public class OpenIdTokenValidatorTest {
    private String issuer;
    private String jwkUrl;
    private String clientId;
    private JwkProvider provider;

    @InjectMocks
    private OpenIdTokenValidator openIdTokenValidator;

    @Before
    public void setUp() {
        openIdTokenValidator = new OpenIdTokenValidator();
        issuer = "test123";
        clientId = "12334-2223-2222";
        jwkUrl = "http://radien.io/";

        provider = mock(JwkProvider.class);
        setInternalState(openIdTokenValidator, "provider", provider);
        setInternalState(openIdTokenValidator, "issuer", issuer);
        setInternalState(openIdTokenValidator, "jwkUrl", jwkUrl);
        setInternalState(openIdTokenValidator, "clientId", clientId);
    }


    /**
     * Test for method {@link OpenIdTokenValidator#validate(JWSObject)}
     * @throws Exception thrown in case of invalid public key
     */
    @Test
    public void testValidate() throws Exception {
        String keyId = "11111";

        Long exp = Instant.now().getEpochSecond() + 3600L;

        Map<String, Object> map = new HashMap<>();
        map.put("iss", issuer);
        map.put("typ", "Bearer");
        map.put("exp", exp);

        map.put("aud", clientId);
        Payload payload = new Payload(map);

        JWSObject jsonToBeValidated = mock(JWSObject.class);
        when(jsonToBeValidated.getPayload()).thenReturn(payload);

        RSAPublicKey mockedPublicKey = mock(RSAPublicKey.class);
        Jwk jwk = mock(Jwk.class);
        when(jwk.getPublicKey()).thenReturn(mockedPublicKey);

        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.EdDSA,
                JOSEObjectType.JWT, "cty", new HashSet<>(),
                new URI("jku"), null, new URI("x5u"),
                null, null, new ArrayList<>(), keyId, true,
                new HashMap<>(), null);

        when(jsonToBeValidated.getHeader()).thenReturn(jwsHeader);
        when(provider.get(keyId)).thenReturn(jwk);
        when(jsonToBeValidated.verify(any(RSASSAVerifier.class))).thenReturn(Boolean.TRUE);

        try {
            openIdTokenValidator.validate(jsonToBeValidated);
        }
        catch (InvalidAccessTokenException i) {
            fail("invalid access token");
        }
    }

    /**
     * Test for method {@link OpenIdTokenValidator#validate(JWSObject)}
     * Scenario where iss claim field is invalid
     * @throws Exception thrown in case of invalid public key
     */
    @Test(expected = InvalidAccessTokenException.class)
    public void testValidateInvalidIss() throws Exception {
        String keyId = "11111";

        Map<String, Object> map = new HashMap<>();
        map.put("iss", 1111);
        map.put("typ", "Bearer");
        map.put("exp", 1636738481L);
        map.put("aud", clientId);
        Payload payload = new Payload(map);

        JWSObject jsonToBeValidated = mock(JWSObject.class);
        when(jsonToBeValidated.getPayload()).thenReturn(payload);

        RSAPublicKey mockedPublicKey = mock(RSAPublicKey.class);
        Jwk jwk = mock(Jwk.class);
        when(jwk.getPublicKey()).thenReturn(mockedPublicKey);

        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.EdDSA,
                JOSEObjectType.JWT, "cty", new HashSet<>(),
                new URI("jku"), null, new URI("x5u"),
                null, null, new ArrayList<>(), keyId, true,
                new HashMap<>(), null);

        when(jsonToBeValidated.getHeader()).thenReturn(jwsHeader);
        when(provider.get(keyId)).thenReturn(jwk);
        when(jsonToBeValidated.verify(any(RSASSAVerifier.class))).thenReturn(Boolean.TRUE);

        openIdTokenValidator.validate(jsonToBeValidated);
    }

    /**
     * Test for method {@link OpenIdTokenValidator#validate(JWSObject)}
     * Scenario where auth type is invalid
     * @throws Exception thrown in case of invalid public key
     */
    @Test(expected = InvalidAccessTokenException.class)
    public void testValidateInvalidType() throws Exception {
        String keyId = "11111";

        Map<String, Object> map = new HashMap<>();
        map.put("iss", issuer);
        map.put("typ", "DPoP");
        map.put("exp", 1636738481L);
        map.put("aud", clientId);
        Payload payload = new Payload(map);

        JWSObject jsonToBeValidated = mock(JWSObject.class);
        when(jsonToBeValidated.getPayload()).thenReturn(payload);

        RSAPublicKey mockedPublicKey = mock(RSAPublicKey.class);
        Jwk jwk = mock(Jwk.class);
        when(jwk.getPublicKey()).thenReturn(mockedPublicKey);

        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.EdDSA,
                JOSEObjectType.JWT, "cty", new HashSet<>(),
                new URI("jku"), null, new URI("x5u"),
                null, null, new ArrayList<>(), keyId, true,
                new HashMap<>(), null);

        when(jsonToBeValidated.getHeader()).thenReturn(jwsHeader);
        when(provider.get(keyId)).thenReturn(jwk);
        when(jsonToBeValidated.verify(any(RSASSAVerifier.class))).thenReturn(Boolean.TRUE);

        openIdTokenValidator.validate(jsonToBeValidated);
    }

    /**
     * Test for method {@link OpenIdTokenValidator#validate(JWSObject)}
     * Scenario where verifier is invalid
     * @throws Exception thrown in case of invalid public key
     */
    @Test(expected = InvalidAccessTokenException.class)
    public void testValidateInvalidVerifier() throws Exception {
        String keyId = "11111";
        JWSObject jsonToBeValidated = mock(JWSObject.class);

        RSAPublicKey mockedPublicKey = mock(RSAPublicKey.class);
        Jwk jwk = mock(Jwk.class);
        when(jwk.getPublicKey()).thenReturn(mockedPublicKey);

        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.EdDSA,
                JOSEObjectType.JWT, "cty", new HashSet<>(),
                new URI("jku"), null, new URI("x5u"),
                null, null, new ArrayList<>(), keyId, true,
                new HashMap<>(), null);

        when(jsonToBeValidated.getHeader()).thenReturn(jwsHeader);
        when(provider.get(keyId)).thenReturn(jwk);
        when(jsonToBeValidated.verify(any(RSASSAVerifier.class))).thenReturn(Boolean.FALSE);

        openIdTokenValidator.validate(jsonToBeValidated);
    }

    /**
     * Test for method {@link OpenIdTokenValidator#validate(JWSObject)}
     * Scenario where client is invalid
     * @throws Exception thrown in case of invalid public key
     */
    @Test(expected = InvalidAccessTokenException.class)
    public void testValidateInvalidClient() throws Exception {
        String keyId = "11111";

        Map<String, Object> map = new HashMap<>();
        map.put("iss", issuer);
        map.put("typ", "Bearer");
        map.put("aud", "1");
        Payload payload = new Payload(map);

        JWSObject jsonToBeValidated = mock(JWSObject.class);
        when(jsonToBeValidated.getPayload()).thenReturn(payload);

        RSAPublicKey mockedPublicKey = mock(RSAPublicKey.class);
        Jwk jwk = mock(Jwk.class);
        when(jwk.getPublicKey()).thenReturn(mockedPublicKey);

        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.EdDSA,
                JOSEObjectType.JWT, "cty", new HashSet<>(),
                new URI("jku"), null, new URI("x5u"),
                null, null, new ArrayList<>(), keyId, true,
                new HashMap<>(), null);

        when(jsonToBeValidated.getHeader()).thenReturn(jwsHeader);
        when(provider.get(keyId)).thenReturn(jwk);
        when(jsonToBeValidated.verify(any(RSASSAVerifier.class))).thenReturn(Boolean.TRUE);

        openIdTokenValidator.validate(jsonToBeValidated);
    }

    /**
     * Test for method {@link OpenIdTokenValidator#validate(JWSObject)}
     * Scenario where client is invalid
     * @throws Exception thrown in case of invalid public key
     */
    @Test(expected = InvalidAccessTokenException.class)
    public void testValidateTokenExpired() throws Exception {
        String keyId = "11111";

        Long exp = Instant.now().getEpochSecond() - 3600L;

        Map<String, Object> map = new HashMap<>();
        map.put("iss", issuer);
        map.put("typ", "Bearer");
        map.put("exp", exp);
        map.put("aud", clientId);
        Payload payload = new Payload(map);

        JWSObject jsonToBeValidated = mock(JWSObject.class);
        when(jsonToBeValidated.getPayload()).thenReturn(payload);

        RSAPublicKey mockedPublicKey = mock(RSAPublicKey.class);
        Jwk jwk = mock(Jwk.class);
        when(jwk.getPublicKey()).thenReturn(mockedPublicKey);

        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.EdDSA,
                JOSEObjectType.JWT, "cty", new HashSet<>(),
                new URI("jku"), null, new URI("x5u"),
                null, null, new ArrayList<>(), keyId, true,
                new HashMap<>(), null);

        when(jsonToBeValidated.getHeader()).thenReturn(jwsHeader);
        when(provider.get(keyId)).thenReturn(jwk);
        when(jsonToBeValidated.verify(any(RSASSAVerifier.class))).thenReturn(Boolean.TRUE);

        openIdTokenValidator.validate(jsonToBeValidated);
    }

    /**
     * Test for method {@link OpenIdTokenValidator#getProvider()}
     * @throws MalformedURLException in case of malformed url
     */
    @Test
    public void testGetProvider() throws MalformedURLException {
        JwkProvider prov = null;
        setInternalState(openIdTokenValidator, "provider", prov);
        setInternalState(openIdTokenValidator, "jwkUrl", "http://test.io.net");
        assertNotNull(openIdTokenValidator.getProvider());
    }

    /**
     * Test for method {@link OpenIdTokenValidator#validate(JWSObject)}
     * Scenario where client is invalid
     * @throws Exception thrown in case of invalid public key
     */
    @Test(expected = InvalidAccessTokenException.class)
    public void testValidateUsingInvalidProvider() throws Exception {
        JwkProvider prov = null;
        setInternalState(openIdTokenValidator, "provider", prov);
        setInternalState(openIdTokenValidator, "jwkUrl", "test");

        String keyId = "11111";
        JWSObject jsonToBeValidated = mock(JWSObject.class);

        RSAPublicKey mockedPublicKey = mock(RSAPublicKey.class);
        Jwk jwk = mock(Jwk.class);
        when(jwk.getPublicKey()).thenReturn(mockedPublicKey);

        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.EdDSA,
                JOSEObjectType.JWT, "cty", new HashSet<>(),
                new URI("jku"), null, new URI("x5u"),
                null, null, new ArrayList<>(), keyId, true,
                new HashMap<>(), null);

        when(jsonToBeValidated.getHeader()).thenReturn(jwsHeader);
        openIdTokenValidator.validate(jsonToBeValidated);
    }
}
