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
package ${package}.util;

import ${package}.${resource-name}ResponseExceptionMapper;
import ${package}.providers.${resource-name}MessageBodyWriter;
import ${package}.services.${resource-name}ResourceClient;

import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.ejb.Stateless;
import java.net.MalformedURLException;
import java.net.URL;

@Stateless
public class ClientServiceUtil {
    public ${resource-name}ResourceClient get${resource-name}ResourceClient(String urlStr) throws MalformedURLException {
        URL url = new URL(urlStr);
        return RestClientBuilder.
                newBuilder()
                .baseUrl(url)
                .register(${resource-name}ResponseExceptionMapper.class)
                .register(${resource-name}MessageBodyWriter.class)
                .build(${resource-name}ResourceClient.class);
    }
}
