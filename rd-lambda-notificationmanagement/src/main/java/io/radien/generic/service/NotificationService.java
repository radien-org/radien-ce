package io.radien.generic.service;

import io.radien.generic.params.NotificationParams;

public interface NotificationService<T extends NotificationParams> {

    void notifyBehaviour(T parameters);
}
