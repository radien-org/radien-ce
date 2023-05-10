package io.radien.lambda.notificationmanagement.util.email.service;

import io.radien.lambda.notificationmanagement.util.email.params.EmailParams;

public interface EmailNotificationServiceAccess {

    void notifyBehaviour(EmailParams parameters);
}
