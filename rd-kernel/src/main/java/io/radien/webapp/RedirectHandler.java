/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.radien.webapp;

import io.radien.api.Appframeable;

/**
 * User redirection handler
 *
 * @author Marco Weiland
 */
public interface RedirectHandler extends Appframeable {

    /**
     * Redirect user into given url path
     * @param url to redirect the user
     */
    void redirectTo(String url);

    /**
     * Redirects the user into home page/public index
     */
    void redirectToPublicIndex();

    /**
     * Redirects user into index
     * @param openDefaultApp
     */
    void redirectToIndex(boolean openDefaultApp);

    /**
     * Redirects user into logout page
     */
    void redirectToLogout();

    /**
     * Redirects user into an error page
     * @param exp to be throw/show
     */
    void redirectToErrorPage(Exception exp);
}
