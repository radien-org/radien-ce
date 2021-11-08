package io.radien.security.openid.context.client;

import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import javax.enterprise.context.SessionScoped;

@SessionScoped
public class OAuthClientContext implements ClientContext {
    private AccessToken accessToken;
    private RefreshToken refreshToken;
    private State state;

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
}
