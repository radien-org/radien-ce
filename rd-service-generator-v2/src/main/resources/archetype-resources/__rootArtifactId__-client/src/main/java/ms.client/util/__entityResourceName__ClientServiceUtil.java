/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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
package ${package}.ms.client.util;

import io.radien.exception.TokenExpiredException;

import ${package}.ms.client.${entityResourceName}ResponseExceptionMapper;
import ${package}.ms.client.providers.${entityResourceName}MessageBodyWriter;
import ${package}.ms.client.services.${entityResourceName}ResourceClient;

import java.net.MalformedURLException;
import java.net.URL;

import javax.enterprise.context.RequestScoped;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
/**
 * Utility class that produces Rest Clients for ${entityResourceName}management
 * @author Bruno Gama
 */
@RequestScoped
public class ${entityResourceName}ClientServiceUtil {

    /**
     * Gets a Rest Client for user
     * @param urlStr url of rest endpoint
     * @return a client form the user
     * @throws MalformedURLException in case of any url issue
     * @throws TokenExpiredException  in case of JWT token expiration
     */
    public ${entityResourceName}ResourceClient get${entityResourceName}ResourceClient(String urlStr)throws MalformedURLException , TokenExpiredException {
        URL url = new URL(urlStr);
        return RestClientBuilder.
                 newBuilder()
                .baseUrl(url)
                .register(${entityResourceName}ResponseExceptionMapper.class)
                .register(${entityResourceName}MessageBodyWriter.class)
                .build(${entityResourceName}ResourceClient.class);
    }
}
