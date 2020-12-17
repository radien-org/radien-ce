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
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(HTTPSessionHandler.class);

	public HTTPSessionHandler() {
		log.debug(this + " initialized");
	}

	@PostConstruct
	private void init() {
	}

	@Override
	public HttpSession getSession(boolean create) {
		ExternalContext externalContext = JSFUtil.getExternalContext();
		if(externalContext == null){
			return null;
		}
		return (HttpSession) JSFUtil.getExternalContext().getSession(create);
	}

	@Override
	public HttpSession getSession(boolean create, HttpServletRequest request, HttpServletResponse response) {
		return (HttpSession) JSFUtil.getFacesContext(request, response).getExternalContext().getSession(create);
	}

	@Override
	public HttpServletRequest getRequest() {
		return (HttpServletRequest) JSFUtil.getExternalContext().getRequest();
	}

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


	@Override
	public SystemUser getUser(HttpServletRequest request, HttpServletResponse response) throws UserNotFoundException {
		try {
			HttpSession session = getSession(false, request, response);
			if (session != null) {
				return (SystemUser) session.getAttribute(ATTR_USER);
			} else
			throw new UserNotFoundException("User has not been in session");

		} catch (UserNotFoundException e) {
			log.info("user not found in session",e);
			throw e;
		}catch (Exception e) {
			log.info(e.getMessage(),e);
			throw e;
		}
	}

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

	@Override
	public void logout() {
		getSession(false).invalidate();
		eraseCookies(JSFUtil.getExternalContext());
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		getSession(false, request, response).invalidate();
		eraseCookies(request, response);
	}

	@Override
	public void register(HttpServletRequest request, HttpServletResponse response, SystemUser user) {
		HttpSession session = getSession(false, request, response);

		if (session != null) {
			session.setAttribute(ATTR_USER, user);
			log.info("user registered in session {}", user.getLogon());
		}
	}

	@Override
	public SystemUser getUser(HttpServletRequest request) {
		return (SystemUser) request.getSession(false).getAttribute(ATTR_USER);
	}

	@Override
	public void eraseCookies(ExternalContext externalContext) {
		HttpServletRequest req = (HttpServletRequest) externalContext.getRequest();
		HttpServletResponse res = (HttpServletResponse) externalContext.getResponse();
		eraseCookies(req, res);
	}

	@Override
	public void eraseCookies(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null)
			for (Cookie cookie : cookies) {
				cookie.setValue("");
				cookie.setPath("/");
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
	}

}
