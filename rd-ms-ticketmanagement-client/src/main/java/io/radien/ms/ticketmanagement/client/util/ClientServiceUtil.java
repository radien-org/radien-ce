/*
 * Copyright (c) 2006-present radien GmbH & its legal owners.
 * All rights reserved.<p>Licensed under the Apache License, Version 2.0
 * (the "License");you may not use this file except in compliance with the
 * License.You may obtain a copy of the License at<p>http://www.apache.org/licenses/LICENSE-2.0<p>Unless required by applicable law or
 * agreed to in writing, softwaredistributed under the License is distributed
 * on an "AS IS" BASIS,WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.See the License for the specific language
 * governing permissions andlimitations under the License.
 */

package io.radien.ms.ticketmanagement.client.util;

import io.radien.ms.ticketmanagement.client.TicketResponseExceptionMapper;
import io.radien.ms.ticketmanagement.client.providers.TicketMessageBodyWriter;
import io.radien.ms.ticketmanagement.client.services.TicketResourceClient;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.net.MalformedURLException;
import java.net.URL;

@RequestScoped
@Named("TicketClientServiceUtil")
public class ClientServiceUtil {

    private static final Logger log = LoggerFactory.getLogger(ClientServiceUtil.class);

    public TicketResourceClient getTicketResourceClient(String urlStr) throws MalformedURLException {
        log.info(urlStr);
        URL url = new URL(urlStr);
        return RestClientBuilder.
                newBuilder()
                .baseUrl(url)
                .register(TicketResponseExceptionMapper.class)
                .register(TicketMessageBodyWriter.class)
                .build(TicketResourceClient.class);
    }
}
