package io.radien.security.openid.validation;

import com.nimbusds.jose.JWSObject;
import io.radien.exception.InvalidAccessTokenException;

/**
 * Contract for a class/component whose concern is validate a token
 * @author Newton Carvalho
 */
public interface TokenValidator {

    /**
     * Validates a token
     * @param jwsObject token to be validate
     * @throws InvalidAccessTokenException thrown in case of token being invalid
     */
    void validate(JWSObject jwsObject) throws InvalidAccessTokenException;
}
