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
package io.radien.ms.doctypemanagement.client.util;

import io.radien.exception.TokenExpiredException;

import io.radien.ms.doctypemanagement.client.PropertyDefinitionResponseExceptionMapper;
import io.radien.ms.doctypemanagement.client.providers.MixinDefinitionMessageBodyReader;
import io.radien.ms.doctypemanagement.client.providers.MixinDefinitionMessageBodyWriter;
import io.radien.ms.doctypemanagement.client.providers.PropertyDefinitionMessageBodyReader;
import io.radien.ms.doctypemanagement.client.providers.PropertyDefinitionMessageBodyWriter;
import io.radien.ms.doctypemanagement.client.services.MixinDefinitionResourceClient;
import io.radien.ms.doctypemanagement.client.services.PropertyDefinitionResourceClient;

import java.net.MalformedURLException;
import java.net.URL;

import javax.enterprise.context.RequestScoped;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
@RequestScoped
public class ClientServiceUtil {
    public PropertyDefinitionResourceClient getPropertyDefinitionClient(String urlStr) throws MalformedURLException , TokenExpiredException {
        URL url = new URL(urlStr);
        return RestClientBuilder.
                 newBuilder()
                .baseUrl(url)
                .register(PropertyDefinitionResponseExceptionMapper.class)
                .register(PropertyDefinitionMessageBodyWriter.class)
                .register(PropertyDefinitionMessageBodyReader.class)
                .build(PropertyDefinitionResourceClient.class);
    }

    public MixinDefinitionResourceClient getMixinDefinitionClient(String urlStr) throws MalformedURLException , TokenExpiredException {
        URL url = new URL(urlStr);
        return RestClientBuilder.
                 newBuilder()
                .baseUrl(url)
                .register(PropertyDefinitionResponseExceptionMapper.class)
                .register(MixinDefinitionMessageBodyWriter.class)
                .register(MixinDefinitionMessageBodyReader.class)
                .build(MixinDefinitionResourceClient.class);
    }
}
