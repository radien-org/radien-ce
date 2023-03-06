package io.radien.lambda.notificationmanagement.util.email.service;


import io.radien.api.service.mail.MailServiceAccess;
import io.radien.lambda.notificationmanagement.util.email.params.EmailParams;
import io.radien.exception.InternalServerErrorException;
import io.radien.lambda.notificationmanagement.util.ContentService;
import org.json.simple.parser.ParseException;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;

import java.io.IOException;
import org.mockito.junit.MockitoRule;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


public class EmailNotificationServiceTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    @Mock
    private MailServiceAccess mailService;

    @Mock
    private ContentService contentService;

    @InjectMocks
    private EmailNotificationService emailNotificationService;

    @Test
    public void notifyBehaviour() {
        emailNotificationService.notifyBehaviour(new EmailParams());
        Mockito.verify(mailService).send(any());
    }

    @Test(expected = InternalServerErrorException.class)
    public void notifyBehaviourException() throws IOException, ParseException, java.text.ParseException {
        when(contentService.getContentByViewIdAndLanguage(null, null)).thenThrow(IOException.class);
        emailNotificationService.notifyBehaviour(new EmailParams());
    }
}