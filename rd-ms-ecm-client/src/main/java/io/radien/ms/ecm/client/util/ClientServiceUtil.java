/*
 *
 *  * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
 *  * <p>
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  * <p>
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  * <p>
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.radien.ms.ecm.client.util;

import io.radien.ms.ecm.client.controller.ContentResource;
import io.radien.ms.ecm.client.controller.I18NResource;
import io.radien.ms.ecm.client.providers.DeletePropertyFilterMessageBodyReader;
import io.radien.ms.ecm.client.providers.EnterpriseContentMessageBodyReader;
import io.radien.ms.ecm.client.providers.I18NPropertyMessageBodyReader;
import java.net.MalformedURLException;
import java.net.URL;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
@Named("ContentClientServiceUtil")
public class ClientServiceUtil {
    private static final Logger log = LoggerFactory.getLogger(ClientServiceUtil.class);

    public ContentResource getContentResourceClient(String urlStr) throws MalformedURLException {
        URL url = new URL(urlStr);
        log.info("Content Controller Resource Client Url - {}", urlStr);
        return RestClientBuilder
                .newBuilder()
                .baseUrl(url)
                .register(EnterpriseContentMessageBodyReader.class)
                .build(ContentResource.class);
    }

    public I18NResource getI18NResourceClient(String urlStr) throws MalformedURLException {
        URL url = new URL(urlStr);
        log.info("Content Controller Resource Client Url - {}", urlStr);
        return RestClientBuilder
                .newBuilder()
                .baseUrl(url)
                .register(I18NPropertyMessageBodyReader.class)
                .register(DeletePropertyFilterMessageBodyReader.class)
                .build(I18NResource.class);
    }
}
