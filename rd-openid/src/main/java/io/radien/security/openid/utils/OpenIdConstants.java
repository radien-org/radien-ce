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
package io.radien.security.openid.utils;

/**
 * Utility class to define constants
 */
public final class OpenIdConstants {

    /**
     * Convention for Utility classes:
     * Add a private constructor to hide the implicit public one
     */
    private OpenIdConstants() {}

    /** URI regarding authentication process triggering */
    public static final String LOGIN_URI = "/login";

    /** URI for auth code callback */
    public static final String AUTH_CALLBACK_URI = "/auth";

    /** Variable name that refers stored URL */
    public static final String SAVED_URL_STATE = "savedURL";

    /** Variable name that refers OIDC State */
    public static final String OIDC_STATE = "oidcState";
}
