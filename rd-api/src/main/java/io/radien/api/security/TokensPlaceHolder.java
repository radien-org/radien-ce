package io.radien.api.security;

public interface TokensPlaceHolder {
    String getAccessToken();
    String getRefreshToken();
    void setAccessToken(String accessToken);
}
