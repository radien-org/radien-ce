package io.radien.webapp;

import io.radien.api.Appframeable;

public interface RedirectHandler extends Appframeable {

    void redirectTo(String url);

    void redirectToPublicIndex();

    void redirectToIndex(boolean openDefaultApp);

    void redirectToLogout();

    void redirectToErrorPage(Exception exp);
}
