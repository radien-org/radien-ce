package io.radien.ms.notificationmanagement.client.util;

import io.radien.ms.notificationmanagement.client.services.EmailNotificationResourceClient;
import java.net.MalformedURLException;
import java.net.URL;
import javax.enterprise.context.RequestScoped;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

@RequestScoped
public class ClientServiceUtil {
    public EmailNotificationResourceClient getEmailNotificationResourceClient(String urlStr) throws MalformedURLException {
        URL url = new URL(urlStr);
        return RestClientBuilder
                .newBuilder()
                .baseUrl(url)
                .build(EmailNotificationResourceClient.class);
    }
}
