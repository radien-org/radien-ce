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
package io.rd.ms.client.util;

import io.rd.ms.client.DemoResponseExceptionMapper;
import io.rd.ms.client.providers.DemoMessageBodyWriter;
import io.rd.ms.client.services.DemoResourceClient;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.ejb.Stateless;
import java.net.MalformedURLException;
import java.net.URL;

@Stateless
public class ClientServiceUtil {
    private static final long serialVersionUID = 6812608123262000048L;

    public DemoResourceClient getDemoResourceClient(String urlStr) throws MalformedURLException {
        URL url = new URL(urlStr);
        return RestClientBuilder.
                 newBuilder()
                .baseUrl(url)
                .register(DemoResponseExceptionMapper.class)
                .register(DemoMessageBodyWriter.class)
                .build(DemoResourceClient.class);
    }
}
