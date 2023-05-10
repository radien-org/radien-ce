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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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

    @Test
    public void testNotify() throws SystemException, MalformedURLException {
        Response response = mock(Response.class);
        EmailNotificationResourceClient mockClient = mock(EmailNotificationResourceClient.class);

        when(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_NOTIFICATIONMANAGEMENT)).thenReturn("http://a.url.com");
        when(clientServiceUtil.getEmailNotificationResourceClient(anyString())).thenReturn(mockClient);
        when(mockClient.notify(anyString(), anyString(), anyString(), any())).thenReturn(response);
        when(response.getStatus()).thenReturn(Response.Status.OK.getStatusCode());

        assertTrue(client.notify("", "", "", new HashMap<>()));
    }

    @Test(expected = SystemException.class)
    public void testNotifyException() throws SystemException, MalformedURLException {
        when(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_NOTIFICATIONMANAGEMENT)).thenReturn("http://a.url.com");
        when(clientServiceUtil.getEmailNotificationResourceClient(anyString())).thenThrow(MalformedURLException.class);

        client.notify("", "", "", new HashMap<>());
    }

    /**
     * Test to attempt to connect with the OAF
     */
    @Test
    public void testAccessingOAF() {
        OAFAccess oafAccess = client.getOAF();
        assertNotNull(oafAccess);
    }
}