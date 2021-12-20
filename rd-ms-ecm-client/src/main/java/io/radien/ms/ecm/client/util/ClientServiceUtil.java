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
package io.radien.ms.ecm.client.util;

import io.radien.exception.ModelResponseExceptionMapper;
import io.radien.ms.ecm.client.providers.LegalDocumentTypeMessageBodyWriter;
import io.radien.ms.ecm.client.services.LegalDocumentTypeResourceClient;
import java.net.MalformedURLException;
import java.net.URL;
import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Constructor for communication with the Legal document type clients
 * @author Newton Carvalho
 */
@ApplicationScoped
public class ClientServiceUtil {

    private static final Logger log = LoggerFactory.getLogger(ClientServiceUtil.class);

    /**
     * Gets a Rest Client for Legal Document Type domain
     * @param urlStr url of rest endpoint
     * @return a client from the Legal Document Type resource
     * @throws MalformedURLException in case of any url issue
     */
    public LegalDocumentTypeResourceClient getLegalDocumentTypeClient(String urlStr) throws MalformedURLException {
        URL url = new URL(urlStr);
        log.info(urlStr);
        return RestClientBuilder.
                newBuilder()
                .baseUrl(url)
                .register(ModelResponseExceptionMapper.class)
                .register(LegalDocumentTypeMessageBodyWriter.class)
                .build(LegalDocumentTypeResourceClient.class);
    }
}
