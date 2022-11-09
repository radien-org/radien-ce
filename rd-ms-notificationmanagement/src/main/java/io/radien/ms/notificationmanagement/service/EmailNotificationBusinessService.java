package io.radien.ms.notificationmanagement.service;

import io.radien.api.model.user.SystemUser;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.mail.MailServiceAccess;
import io.radien.api.service.mail.model.Mail;
import io.radien.api.service.notification.email.EmailNotificationBusinessServiceAccess;
import io.radien.exception.BadRequestException;
import io.radien.exception.NotFoundException;
import io.radien.exception.SystemException;
import io.radien.ms.ecm.client.services.ContentRESTServiceClient;
import io.radien.ms.email.lib.MailTemplate;
import java.text.MessageFormat;
import java.util.Map;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;

@RequestScoped
public class EmailNotificationBusinessService implements EmailNotificationBusinessServiceAccess {

    @Inject
    private MailServiceAccess mailService;

    @Inject
    private ContentRESTServiceClient contentService;

    @Override
    public boolean notifyUser(SystemUser user, String notificationViewId, String language, Map<String, String> arguments) {
        if(user == null) {
            throw new BadRequestException("Current user not set");
        } else if(StringUtils.isEmpty(user.getUserEmail())) {
            throw new BadRequestException("User's email incorrectly configured");
        }
        EnterpriseContent emailContent = null;
        try {
            emailContent = contentService.getByViewIdAndLanguage(notificationViewId, language);
            if(emailContent == null) {
                throw new NotFoundException(MessageFormat.format("No email found for view id {0}", notificationViewId));
            }
            Mail optInEmail = mailService.create(user, new MailTemplate(emailContent, arguments));
            mailService.send(optInEmail);
            return true;
        } catch (SystemException e) {
            return false;
        }
    }
}
