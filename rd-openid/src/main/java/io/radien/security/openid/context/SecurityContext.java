package io.radien.security.openid.context;

import io.radien.security.openid.model.Authentication;
import java.io.Serializable;

public interface SecurityContext extends Serializable {

    /**
     * Obtains the currently authenticated principal, or an authentication request token.
     * @return the <code>Authentication</code> or <code>null</code> if no authentication
     * information is available
     */
    Authentication getAuthentication();

    /**
     * Changes the currently authenticated principal, or removes the authentication
     * information.
     * @param authentication the new <code>Authentication</code> token, or
     * <code>null</code> if no further authentication information should be stored
     */
    void setAuthentication(Authentication authentication);

}
