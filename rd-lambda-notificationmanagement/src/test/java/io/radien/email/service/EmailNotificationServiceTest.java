package io.radien.email.service;


import io.radien.Authenticator;
import io.radien.api.service.ecm.model.Content;
import io.radien.api.service.mail.MailServiceAccess;
import io.radien.api.service.mail.model.Mail;
import io.radien.api.service.mail.model.MailContentType;
import io.radien.email.params.EmailParams;
import io.radien.ms.email.lib.MailMessage;
import io.radien.ms.email.lib.MailTemplate;
import io.radien.util.ContentService;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class EmailNotificationServiceTest {

    @Mock
    private MailServiceAccess mailService;

    @Mock
    private ContentService contentService;

    @Mock
    private Authenticator authenticator;

    @InjectMocks
    private EmailNotificationService emailNotificationService;

    @Test
    public void notifyBehaviour() throws IOException, ParseException, java.text.ParseException {
        emailNotificationService.notifyBehaviour(new EmailParams());
        Mockito.verify(mailService).send(any());
    }
}