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

import io.radien.ms.doctypemanagement.client.JCRPropertyTypeResponseExceptionMapper;
import io.radien.ms.doctypemanagement.client.providers.JCRPropertyTypeMessageBodyWriter;
import io.radien.ms.doctypemanagement.client.services.PropertyTypeResourceClient;

import java.net.MalformedURLException;
import java.net.URL;

import javax.enterprise.context.RequestScoped;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
@RequestScoped
public class JCRPropertyTypeClientServiceUtil {
    public PropertyTypeResourceClient getJCRPropertyTypeResourceClient(String urlStr) throws MalformedURLException , TokenExpiredException {
        URL url = new URL(urlStr);
        return RestClientBuilder.
                 newBuilder()
                .baseUrl(url)
                .register(JCRPropertyTypeResponseExceptionMapper.class)
                .register(JCRPropertyTypeMessageBodyWriter.class)
                .build(PropertyTypeResourceClient.class);
    }
}
