/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.radien.ms.openid.security;

import io.radien.api.security.TokensPlaceHolder;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Default;
import java.io.Serializable;

/**
 * TokenPlaceHolder implementation to be used by any
 * service/module that needs to propagate the access token to consume
 * secured services
 *
 * @author Newton Carvalho
 */
@SessionScoped
@Default
public class TokenHolder implements TokensPlaceHolder, Serializable {

    private String accessToken;
    private String refreshToken;

    /**
     * Token place holder access token getter
     * @return token place holder access token
     */
    @Override
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Token place holder access token setter
     * @param accessToken to be set
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Token place holder refresh token getter
     * @return token place holder refresh token
     */
    @Override
    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Token place holder refresh token setter
     * @param refreshToken to be set
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
