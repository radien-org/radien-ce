package io.radien.security.openid.context.client;

import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import io.radien.security.openid.model.Authentication;
import java.io.Serializable;

public interface ClientContext extends Serializable {
    AccessToken getAccessToken();
    void setAccessToken(AccessToken accessToken);

    RefreshToken getRefreshToken();
    void setRefreshToken(RefreshToken refreshToken);

    State getState();
    void setState(State state);

    Authentication getAuthentication();
    void setAuthentication(Authentication authentication);

    void clear();
}
