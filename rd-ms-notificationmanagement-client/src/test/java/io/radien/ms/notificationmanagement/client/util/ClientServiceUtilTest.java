package io.radien.ms.notificationmanagement.client.util;


import java.net.MalformedURLException;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertTrue;

public class ClientServiceUtilTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    private ClientServiceUtil serviceUtil;

    @Test
    public void testGetResourceClient() {
        boolean valid = true;
        try {
            serviceUtil.getEmailNotificationResourceClient("http://url.test.pt") ;
        } catch (MalformedURLException e) {
            valid = false;
        }
        assertTrue(valid);
    }
    @Test(expected = MalformedURLException.class)
    public void testGetResourceClientException() throws MalformedURLException {
        serviceUtil.getEmailNotificationResourceClient("not.a.valid.url") ;
    }

}