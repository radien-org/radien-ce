package io.radien.api.service.notification;

import java.util.Map;

public interface SQSAccessAccess {

    boolean emailNotification(String email, String notificationViewId, String language, Map<String, String> arguments);
}
