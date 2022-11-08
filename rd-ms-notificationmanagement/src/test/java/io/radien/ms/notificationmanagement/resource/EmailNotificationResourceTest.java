package io.radien.ms.notificationmanagement.resource;

import io.radien.api.service.notification.email.EmailNotificationBusinessServiceAccess;
import io.radien.ms.notificationmanagement.service.EmailNotificationBusinessService;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class EmailNotificationResourceTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    @InjectMocks
    public EmailNotificationResource notificationResource;
    @Mock
    public EmailNotificationBusinessServiceAccess businessService;
    @Mock
    private HttpServletRequest servletRequest;

    @Test
    public void testNotifyCurrentUser() {
        when(businessService.notifyUser(any(), anyString(), anyString(), any())).thenReturn(true);
        assertEquals(200, notificationResource.notifyCurrentUser("", "", new HashMap<>()).getStatus());
    }
}