/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
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

import io.radien.api.model.user.SystemUser;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.ms.openid.entities.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import java.io.Serializable;

/**
 * TokenPlaceHolder implementation to be extended by any
 * service/component that needs to propagate the access token to consume
 * secured service, as well to consult the current logged user
 */
public abstract class TokensPropagator implements TokensPlaceHolder, Serializable {

    private String accessToken;
    private String refreshToken;

    @Context
    private HttpServletRequest servletRequest;

    protected SystemUser getInvokerUser() {
        return (Principal) servletRequest.getSession().getAttribute("USER");
    }

    protected void preProcess() {
        HttpSession httpSession = this.servletRequest.getSession(false);
        if (httpSession.getAttribute("accessToken") != null &&
                httpSession.getAttribute("refreshToken") != null) {
            this.accessToken = httpSession.getAttribute("accessToken").toString();
            this.refreshToken = httpSession.getAttribute("refreshToken").toString();
        }
        else {
            // Lets obtain (at least) accessToken from Header
            String token = this.servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
            if (token != null && token.startsWith("Bearer ")) {
                this.accessToken = token.substring(7);
            }
        }
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }
}
