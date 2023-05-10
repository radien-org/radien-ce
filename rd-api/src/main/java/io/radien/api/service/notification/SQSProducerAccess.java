package io.radien.api.service.notification;

import java.util.Map;

public interface SQSProducerAccess {

    boolean emailNotification(String email, String notificationViewId, String language, Map<String, String> arguments);
}
