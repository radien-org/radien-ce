package io.radien.security.openid.context;

import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import io.radien.security.openid.model.UserDetails;
import java.io.Serializable;

public interface SecurityContext extends Serializable {
    AccessToken getAccessToken();
    void setAccessToken(AccessToken accessToken);

    RefreshToken getRefreshToken();
    void setRefreshToken(RefreshToken refreshToken);

    State getState();
    void setState(State state);

    UserDetails getUserDetails();
    void setUserDetails(UserDetails userDetails);

    void clear();
}
