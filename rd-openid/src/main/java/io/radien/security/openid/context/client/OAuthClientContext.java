package io.radien.security.openid.context.client;

import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import io.radien.security.openid.model.Authentication;
import javax.enterprise.context.SessionScoped;

@SessionScoped
public class OAuthClientContext implements ClientContext {
    private static final long serialVersionUID = -8073015720062843473L;
    private AccessToken accessToken;
    private RefreshToken refreshToken;
    private State state;
    private Authentication authentication;

    public AccessToken getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public RefreshToken getRefreshToken() {
        return refreshToken;
    }
    public void setRefreshToken(RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }

    public State getState() { return state; }
    public void setState(State state) { this.state = state; }

    public Authentication getAuthentication() { return this.authentication; }
    public void setAuthentication(Authentication authentication) { this.authentication = authentication; }

    public void clear() {
        this.authentication = null;
        this.accessToken = null;
        this.refreshToken = null;
        this.state = null;
    }
}
