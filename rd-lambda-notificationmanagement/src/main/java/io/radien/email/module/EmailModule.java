package io.radien.email.module;

import com.google.inject.AbstractModule;
import io.radien.api.OAFAccess;
import io.radien.api.service.mail.MailServiceAccess;
import io.radien.ms.config.lib.MSOAF;
import io.radien.ms.email.lib.MailFactory;
import io.radien.ms.email.lib.MailService;

public class EmailModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(MailServiceAccess.class).to(MailService.class);
        bind(OAFAccess.class).to(MSOAF.class);
        //bind(MailFactory.class);
    }
}
