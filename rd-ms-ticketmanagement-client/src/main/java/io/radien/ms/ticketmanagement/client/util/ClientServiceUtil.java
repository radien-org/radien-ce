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
