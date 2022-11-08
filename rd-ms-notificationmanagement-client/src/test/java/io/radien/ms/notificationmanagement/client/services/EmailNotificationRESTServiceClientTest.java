package io.radien.ms.notificationmanagement.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.exception.SystemException;
import io.radien.ms.notificationmanagement.client.util.ClientServiceUtil;
import java.net.MalformedURLException;
import java.util.HashMap;
import javax.ws.rs.core.Response;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EmailNotificationRESTServiceClientTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    private EmailNotificationRESTServiceClient client;
    @Mock
    private OAFAccess oaf;
    @Mock
    private ClientServiceUtil clientServiceUtil;

    @Test(expected = SystemException.class)
    public void testNotifyCurrentUserException() throws MalformedURLException, SystemException {
        when(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_NOTIFICATIONMANAGEMENT))
                .thenReturn("http://a.url.com");
        when(clientServiceUtil.getEmailNotificationResourceClient(anyString()))
                .thenThrow(new MalformedURLException());

        client.notifyCurrentUser("", "", new HashMap<>());
    }

    @Test
    public void testNotifyCurrentUser() throws MalformedURLException, SystemException {
        EmailNotificationResourceClient mockClient = mock(EmailNotificationResourceClient.class);
        Response mockResponse = mock(Response.class);
        when(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_NOTIFICATIONMANAGEMENT))
                .thenReturn("http://a.url.com");
        when(clientServiceUtil.getEmailNotificationResourceClient(anyString()))
                .thenReturn(mockClient);
        when(mockClient.notifyCurrentUser(anyString(), anyString(), any())).thenReturn(mockResponse);
        when(mockResponse.getStatus()).thenReturn(200);

        assertEquals(true, client.notifyCurrentUser("", "", new HashMap<>()));
    }
}