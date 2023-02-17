package io.radien.email.service;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import io.radien.Authenticator;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.mail.MailServiceAccess;
import io.radien.email.module.EmailModule;
import io.radien.exception.InternalServerErrorException;
import io.radien.ms.email.lib.MailTemplate;
import io.radien.email.params.EmailParams;
import io.radien.generic.service.NotificationService;
import io.radien.util.ContentService;
import org.json.simple.parser.ParseException;

import java.io.IOException;

@Singleton
public class EmailNotificationService implements NotificationService<EmailParams> {

    private final MailServiceAccess mailService;

    private final Authenticator authenticator;

    private final ContentService contentService;


    public EmailNotificationService(){
        Injector injector = Guice.createInjector(new EmailModule());
        mailService = injector.getInstance(MailServiceAccess.class);
        authenticator = injector.getInstance(Authenticator.class);
        contentService = injector.getInstance(ContentService.class);
    }

    public void notifyBehaviour(EmailParams params){
        try {
            EnterpriseContent content = contentService.getContentByViewIdAndLanguage(authenticator.getAuthorization(), params.getNotificationViewId(), params.getLanguage());
            mailService.send(mailService.create(params.getEmail(), new MailTemplate(content, params.getArguments())));
        } catch (IOException | ParseException | java.text.ParseException e) {
            throw new InternalServerErrorException("Error obtaining mail content.");
        }
    }

    public Authenticator testFuncGetAuthenticator(){
        return authenticator;
    }
}
