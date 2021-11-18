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

import javax.servlet.http.HttpServletRequest;

/**
 * Utility class to perform URL operations
 * @author Newton Carvalho
 */
public class URLUtils {

    /**
     * Convention for Utility classes:
     * Add a private constructor to hide the implicit public one
     */
    private URLUtils() {}

    /**
     * Given a request, assemblies the URL that corresponds
     * to the application context
     * @param request parameter to supply information for url assembling
     * @return String containing base app url
     */
    public static String getAppContextURL(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getScheme()).append("://");
        sb.append(request.getServerName());
        if (request.getServerPort() > 0) {
            sb.append(":");
            sb.append(request.getServerPort());
        }
        sb.append(request.getContextPath());
        return sb.toString();
    }

    /**
     * Retrieve/assemblies the URL referred by a Http Request object, what includes
     * the Query String part
     * @param request Http servlet request used as parameter
     * @return String describing the complete URL
     */
    public static String getCompleteURL(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        if (request.getQueryString() != null) {
            requestURL.append("?").append(request.getQueryString());
        }
        return requestURL.toString();
    }

}
