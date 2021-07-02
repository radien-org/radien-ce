/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
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

import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.SystemException;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import io.radien.api.model.user.SystemUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.exception.AccountNotValidException;
import io.radien.exception.UserNotFoundException;
import io.radien.util.OAFHttpRequest;

/**
 * Main handler of all http application session,
 * Http/Https oriented implementation of {@link SessionHandler}
 *
 * @author Marco Weiland
 * @author Rafael Fernandes
 */
public @RequestScoped class HTTPSessionHandler implements SessionHandler {
	private static final long serialVersionUID = -4874752589449604685L;
	private static final Logger log = LoggerFactory.getLogger(HTTPSessionHandler.class);

	public HTTPSessionHandler() {
		log.debug(this + " initialized");
	}

	@PostConstruct
	private void init() {
		// empty
	}

	/**
	 * Get the current http session
	 * @param create should session be created or not
	 * @return the requested and created http session
	 */
	@Override
	public HttpSession getSession(boolean create) {
		ExternalContext externalContext = JSFUtil.getExternalContext();
		if(externalContext == null){
			return null;
		}
		return (HttpSession) JSFUtil.getExternalContext().getSession(create);
	}

	/**
	 * Get the current http session
	 * @param create should session be created or not
	 * @param request http servlet request endpoint
	 * @param response http servlet response endpoint
	 * @return the requested and created http session
	 */
	@Override
	public HttpSession getSession(boolean create, HttpServletRequest request, HttpServletResponse response) {
		return (HttpSession) JSFUtil.getFacesContext(request, response).getExternalContext().getSession(create);
	}

	/**
	 * Get current request
	 * @return http servlet request
	 */
	@Override
	public HttpServletRequest getRequest() {
		return (HttpServletRequest) JSFUtil.getExternalContext().getRequest();
	}

	/**
	 * Get active system user requesting creation
	 * @return the active system user
	 */
	@Override
	public SystemUser getUser() {
		try {
			HttpSession session = getSession(false);
			if (session != null) {
				return (SystemUser) session.getAttribute(ATTR_USER);
			} else {
				log.info("Session is NULL");
				return null;
			}
		} catch (IllegalStateException e) {
			log.info("NPE when we have no session, so, we don't have user in session", e);
			return null;
		}
	}

	/**
	 * Get active system user requesting creation
	 * @param request http servlet request endpoint
	 * @param response http servlet response endpoint
	 * @return the system user requesting session
	 * @throws UserNotFoundException in case of user was not beeing able to be found or does not exist
	 */
	@Override
	public SystemUser getUser(HttpServletRequest request, HttpServletResponse response) throws UserNotFoundException, SystemException {
		try {
			HttpSession session = getSession(false, request, response);
			if (session != null) {
				return (SystemUser) session.getAttribute(ATTR_USER);
			} else {
				throw new UserNotFoundException("User has not been in session");
			}

		} catch (UserNotFoundException e) {
			throw new UserNotFoundException(GenericErrorCodeMessage.RESOURCE_NOT_FOUND.toString());
		}catch (Exception e) {
			throw new SystemException(GenericErrorCodeMessage.GENERIC_ERROR.toString(e.getMessage()));
		}
	}

	/**
	 * Registration of the system user
	 * @param user to be register
	 * @throws AccountNotValidException Exception to be thrown when a given system account is not valid
	 */
	@Override
	public void register(SystemUser user) throws AccountNotValidException {
		if (user == null) {
			throw new AccountNotValidException();
		}
		HttpSession session = getSession(false);
		if (session != null) {
			session.setAttribute(ATTR_USER, user);
		}
	}

	/**
	 * Registration of the system user
	 * @param req requester endpoint to be registered
	 * @param user to be register
	 * @throws AccountNotValidException Exception to be thrown when a given system account is not valid
	 */
	@Override
	public void register(OAFHttpRequest req, SystemUser user) throws AccountNotValidException {
		if (user == null) {
			throw new AccountNotValidException();
		}
		HttpSession session = req.getSession(false);
		if (session != null) {
			session.setAttribute(ATTR_USER, user);
		}

	}

	/**
	 * Update the given system user
	 * @param user to be updated
	 * @throws AccountNotValidException Exception to be thrown when a given system account is not valid
	 */
	@Override
	public void update(SystemUser user) throws AccountNotValidException {
		if (user == null) {
			throw new AccountNotValidException();
		}
		HttpSession session = getSession(false);
		if (session != null) {
			session.removeAttribute(ATTR_USER);
			register(user);
		}
	}

	/**
	 * Method to logout the system user
	 */
	@Override
	public void logout() {
		getSession(false).invalidate();
		eraseCookies(JSFUtil.getExternalContext());
	}

	/**
	 * Method to logout the system user in the given endpoints
	 * @param request http servlet request endpoint
	 * @param response http servlet response endpoint
	 */
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		getSession(false, request, response).invalidate();
		eraseCookies(request, response);
	}

	/**
	 * Method to register given user session in given endpoints
	 * @param request http servlet request endpoint
	 * @param response http servlet response endpoint
	 * @param user to be register
	 */
	@Override
	public void register(HttpServletRequest request, HttpServletResponse response, SystemUser user) {
		HttpSession session = getSession(false, request, response);

		if (session != null) {
			session.setAttribute(ATTR_USER, user);
			log.info("user registered in session {}", user.getLogon());
		}
	}

	/**
	 * Method to recover a given user in a specific request enpdpoint
	 * @param request http servlet request endpoint
	 * @return if found
	 */
	@Override
	public SystemUser getUser(HttpServletRequest request) {
		return (SystemUser) request.getSession(false).getAttribute(ATTR_USER);
	}

	/**
	 * Delete all the registered cookies from a certain context
	 * @param externalContext context for the cookies to be deleted
	 */
	@Override
	public void eraseCookies(ExternalContext externalContext) {
		HttpServletRequest req = (HttpServletRequest) externalContext.getRequest();
		HttpServletResponse res = (HttpServletResponse) externalContext.getResponse();
		eraseCookies(req, res);
	}

	/**
	 *Delete all the registered cookies from a certain servlet endpoints
	 * @param request http servlet request endpoint
	 * @param response http servlet response endpoint
	 */
	@Override
	public void eraseCookies(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null)
			for (Cookie cookie : cookies) {
				cookie.setValue("");
				cookie.setPath("/");
				cookie.setMaxAge(0);
				response.addCookie(cookie); //NOSONAR L254 complains in this line of header injection - which is not the case we are deleting headers/cookies
			}
	}

}
