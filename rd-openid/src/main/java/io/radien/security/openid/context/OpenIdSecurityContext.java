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
package io.radien.security.openid.context;

import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import io.radien.security.openid.model.UserDetails;
import javax.enterprise.context.SessionScoped;

/**
 * Component to store the details of the currently authenticated user
 * @author newton carvalho
 */
@SessionScoped
public class OpenIdSecurityContext implements SecurityContext {
    private static final long serialVersionUID = -8073015720062843473L;
    private UserDetails userDetails;

    /**
     * Retrieve the currently authenticated user
     * @return instance of {@link UserDetails}
     */
    public UserDetails getUserDetails() { return this.userDetails; }

    /**
     * Set/Store the currently authenticated user
     * @param userDetails instance of {@link UserDetails}
     */
    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    /**
     * Clear all the store information
     */
    public void clear() {
        this.userDetails = null;
    }
}
