package io.radien.ms.notificationmanagement.client.util;

import io.radien.ms.notificationmanagement.client.services.EmailNotificationResourceClient;
import java.net.MalformedURLException;
import java.net.URL;
import javax.enterprise.context.RequestScoped;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
public class ClientServiceUtil {
    private static final Logger log = LoggerFactory.getLogger(ClientServiceUtil.class);

    public EmailNotificationResourceClient getEmailNotificationResourceClient(String urlStr) throws MalformedURLException {
        log.info(urlStr);
        URL url = new URL(urlStr);
        return RestClientBuilder
                .newBuilder()
                .baseUrl(url)
                .build(EmailNotificationResourceClient.class);
    }
}
