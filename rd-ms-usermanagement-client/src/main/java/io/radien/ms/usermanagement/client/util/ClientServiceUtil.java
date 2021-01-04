package io.radien.ms.usermanagement.client.util;

import io.radien.ms.usermanagement.client.UserResponseExceptionMapper;
import io.radien.ms.usermanagement.client.providers.UserMessageBodyWriter;
import io.radien.ms.usermanagement.client.services.UserResourceClient;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.ejb.Stateless;
import java.net.MalformedURLException;
import java.net.URL;

@Stateless
public class ClientServiceUtil {
    public UserResourceClient getUserResourceClient(String urlStr) throws MalformedURLException {
        URL url = new URL(urlStr);
        return RestClientBuilder.
                newBuilder()
                .baseUrl(url)
                .register(UserResponseExceptionMapper.class)
                .register(UserMessageBodyWriter.class)
                .build(UserResourceClient.class);
    }
}
