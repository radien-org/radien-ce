package io.radien.email.service;


import io.radien.api.service.mail.MailServiceAccess;
import io.radien.email.params.EmailParams;
import io.radien.exception.InternalServerErrorException;
import io.radien.util.ContentService;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class EmailNotificationServiceTest {

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