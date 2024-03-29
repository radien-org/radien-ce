package io.radien.lambda.notificationmanagement.util.email.module;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import io.radien.lambda.notificationmanagement.util.Authenticator;
import io.radien.api.OAFAccess;
import io.radien.api.service.mail.MailServiceAccess;
import io.radien.lambda.notificationmanagement.util.email.service.EmailNotificationService;
import io.radien.ms.config.lib.MSOAF;
import io.radien.ms.email.lib.MailService;
import io.radien.lambda.notificationmanagement.util.ContentService;

public class EmailModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(EmailNotificationService.class).in(Singleton.class);
        bind(MailServiceAccess.class).to(MailService.class).in(Singleton.class);
        bind(Authenticator.class).in(Singleton.class);
        bind(ContentService.class).in(Singleton.class);
        bind(OAFAccess.class).to(MSOAF.class);
    }
}
