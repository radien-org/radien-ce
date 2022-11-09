package io.radien.ms.notificationmanagement.service;


import io.radien.api.model.user.SystemUser;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.mail.MailServiceAccess;
import io.radien.api.service.mail.model.Mail;
import io.radien.exception.BadRequestException;
import io.radien.exception.NotFoundException;
import io.radien.exception.SystemException;
import io.radien.ms.ecm.client.services.ContentRESTServiceClient;
import io.radien.ms.email.lib.MailTemplate;
import java.util.HashMap;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
    public void testNotifyUserIsNull() {
        businessService.notifyUser(null, "", "", new HashMap<>());
    }

    @Test(expected = BadRequestException.class)
    public void testNotifyUserNoEmail() {
        SystemUser user = mock(SystemUser.class);
        when(user.getUserEmail()).thenReturn("");

        businessService.notifyUser(user, "", "", new HashMap<>());
    }

    @Test(expected = NotFoundException.class)
    public void testNotifyUserNoContent() throws SystemException {
        SystemUser user = mock(SystemUser.class);
        when(user.getUserEmail()).thenReturn("userEmail");
        when(contentService.getByViewIdAndLanguage(anyString(), anyString())).thenReturn(null);

        businessService.notifyUser(user, "viewId", "en", new HashMap<>());
    }

    @Test
    public void testNotifyUser() throws SystemException {
        SystemUser user = mock(SystemUser.class);
        when(user.getUserEmail()).thenReturn("userEmail");
        when(contentService.getByViewIdAndLanguage(anyString(), anyString())).thenReturn(mock(EnterpriseContent.class));
        when(mailService.create(any(SystemUser.class), any(MailTemplate.class))).thenReturn(mock(Mail.class));

        assertTrue(businessService.notifyUser(user, "viewId", "en", new HashMap<>()));
    }

    @Test
    public void testNotifyUserFail() throws SystemException {
        SystemUser user = mock(SystemUser.class);
        when(user.getUserEmail()).thenReturn("userEmail");
        when(contentService.getByViewIdAndLanguage(anyString(), anyString())).thenThrow(new SystemException("ERROR"));

        assertFalse(businessService.notifyUser(user, "viewId", "en", new HashMap<>()));
    }


}