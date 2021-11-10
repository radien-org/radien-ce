package io.radien.security.openid.context;

import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import io.radien.security.openid.model.UserDetails;
import javax.enterprise.context.SessionScoped;

@SessionScoped
public class OpenIdSecurityContext implements SecurityContext {
    private static final long serialVersionUID = -8073015720062843473L;
    private AccessToken accessToken;
    private RefreshToken refreshToken;
    private State state;
    private UserDetails userDetails;

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

    public UserDetails getUserDetails() { return userDetails; };
    public void setUserDetails(UserDetails userDetails) { this.userDetails = userDetails; }

    public void clear() {
        this.userDetails = null;
        this.accessToken = null;
        this.refreshToken = null;
        this.state = null;
    }
}
