/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.kernel.listener;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Listener with single purpose of validating sessions timeouts to create them or destroy them depending on the
 * situation
 *
 * @author Marco Weiland
 */
public class SessionTimeoutListener implements HttpSessionListener {

	protected static final String SESSION_ATTRIBUTE_USER = "user";
	private static final Log logger = LogFactory.getLog(SessionTimeoutListener.class);

	/**
	 * Creates a new session for the requested http session event
	 * @param event to be created a new session in
	 */
	@Override
	public void sessionCreated(HttpSessionEvent event) {
		logger.debug("session created : " + event.getSession().getId());
	}

	/**
	 * Destroys the session for the requested http session event
	 * @param event where the session will have to be destroyed
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		try {
			prepareLogoutInfoAndLogoutActiveUser(session);
		} catch (Exception e) {
			logger.error("error while logging out at session destroyed : " + e.getMessage(), e);
		}
	}

	/**
	 * Preparation for disconnecting user session in the given http session
	 * @param httpSession where the user session must be shut down or deactivated
	 */
	public void prepareLogoutInfoAndLogoutActiveUser(HttpSession httpSession) {
		httpSession.removeAttribute(SESSION_ATTRIBUTE_USER);
	}

}
