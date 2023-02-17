package io.radien.email.service;

import io.radien.email.params.EmailParams;

public interface EmailNotificationServiceAccess {

    void notifyBehaviour(EmailParams parameters);
}
