package io.radien.api.service.notification.email;

import java.util.Map;

public interface EmailNotificationBusinessServiceAccess {
    boolean notify(String email, String notificationViewId, String language, Map<String, String> arguments);
}
