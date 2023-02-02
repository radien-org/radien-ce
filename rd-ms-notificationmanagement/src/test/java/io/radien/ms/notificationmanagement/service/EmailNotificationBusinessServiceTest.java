package io.radien.ms.notificationmanagement.service;


import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.mail.MailServiceAccess;
import io.radien.exception.BadRequestException;
import io.radien.exception.NotFoundException;
import io.radien.exception.SystemException;
import io.radien.ms.ecm.client.services.ContentRESTServiceClient;
import java.util.HashMap;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EmailNotificationBusinessServiceTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    @InjectMocks
    public EmailNotificationBusinessService businessService;
    @Mock
    public MailServiceAccess mailService;
    @Mock
    public ContentRESTServiceClient contentService;

    @Test(expected = BadRequestException.class)
    public void testNotifyEmailIsNull() {
        businessService.notify(null, "", "", new HashMap<>());
    }

    @Test(expected = BadRequestException.class)
    public void testNotifyNoEmail() {
        businessService.notify("", "", "", new HashMap<>());
    }

    @Test(expected = NotFoundException.class)
    public void testNotifyNoContent() throws SystemException {
        when(contentService.getByViewIdAndLanguage(anyString(), anyString())).thenReturn(null);
        businessService.notify("e@mail.com", "viewId", "en", new HashMap<>());
    }

    @Test
    public void testNotifyFail() throws SystemException {
        when(contentService.getByViewIdAndLanguage(anyString(), anyString())).thenThrow(new SystemException("ERROR"));
        assertFalse(businessService.notify("e@mail.com", "viewId", "en", new HashMap<>()));
    }

    @Test
    public void testNotify() throws SystemException {
        when(contentService.getByViewIdAndLanguage(anyString(), anyString())).thenReturn(mock(EnterpriseContent.class));
        assertTrue(businessService.notify("e@mail.com", "", "", new HashMap<>()));
    }
}