package io.radien.api.service.notification.email;

import io.radien.api.model.user.SystemUser;
import java.util.Map;

public interface EmailNotificationBusinessServiceAccess {
    boolean notifyUser(SystemUser user, String notificationViewId, String language, Map<String, String> arguments);
}
