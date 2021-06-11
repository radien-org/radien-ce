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
package io.radien.ms.usermanagement.client.util;

import io.radien.exception.TokenExpiredException;
import io.radien.ms.usermanagement.client.UserResponseExceptionMapper;
import io.radien.ms.usermanagement.client.providers.UserMessageBodyWriter;
import io.radien.ms.usermanagement.client.services.UserResourceClient;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.ejb.Stateless;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Utility class that produces Rest Clients for User management
 * @author Bruno Gama
 */
@Stateless
public class ClientServiceUtil {

    /**
     * Gets a Rest Client for user
     * @param urlStr url of rest endpoint
     * @return a client form the user
     * @throws MalformedURLException in case of any url issue
     * @throws TokenExpiredException  in case of JWT token expiration
     */
    public UserResourceClient getUserResourceClient(String urlStr) throws MalformedURLException , TokenExpiredException {
        URL url = new URL(urlStr);
        return RestClientBuilder.
                 newBuilder()
                .baseUrl(url)
                .register(UserResponseExceptionMapper.class)
                .register(UserMessageBodyWriter.class)
                .build(UserResourceClient.class);
    }
}
