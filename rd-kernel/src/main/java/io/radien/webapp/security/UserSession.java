/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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
package io.radien.webapp.security;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.model.user.SystemUser;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.api.security.UserSessionEnabled;
import io.radien.api.service.LoginHook;
import io.radien.api.service.user.UserRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.usermanagement.client.services.UserFactory;
import io.radien.webapp.JSFUtil;
import java.io.IOException;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class responsible for managing the current user session
 *
 * @author Marco Weiland
 */
public @Named @SessionScoped class UserSession implements UserSessionEnabled, TokensPlaceHolder {

	private static final long serialVersionUID = 1198636791261091733L;
	private static final Logger log = LoggerFactory.getLogger(UserSession.class);

	@Inject
	private UserRESTServiceAccess userClientService;

	@Inject
	private OAFAccess oaf;

	private SystemUser user;

	private String accessToken;

	private String refreshToken;
	/**
	 * Initialization of the user session
	 */
	@PostConstruct
	private void init() {
		log.info("Session initiated");
	}

	/**
	 * Method for taking care and processing the login of the current active user and his session
	 * @param userIdSubject user subject identifier
	 * @param email user email
	 * @param preferredUserName preferred user selected name
	 * @param givenname user name
	 * @param familyName user last name
	 * @param accessToken public access token for authorization purposes
	 * @param refreshToken private access token for refreshing the public access token
	 * @throws Exception in case of any issue while performing the login, starting the user session or validating the
	 * user information
	 */
	public void login(String userIdSubject,String email, String preferredUserName, String givenname,String familyName,String accessToken, String refreshToken) throws Exception {
		log.info("User session login starting");
		log.info("user logged in: {}", userIdSubject);
		//TODO:		refresh access token if needed
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		try {
			Optional<SystemUser> existingUser = userClientService.getUserBySub(userIdSubject);
			SystemUser systemUser;
			if (!existingUser.isPresent()) {
				systemUser = UserFactory.create(givenname, familyName, preferredUserName, userIdSubject, email, getOAF().getSystemAdminUserId());
				userClientService.create(systemUser, true);
				Optional<SystemUser> userBySub = userClientService.getUserBySub(userIdSubject);
				if(userBySub.isPresent()) {
					systemUser = userBySub.get();
				}
				String loginHook = getOAF().getProperty(OAFProperties.LOGIN_HOOK_ACTIVE);
				if("true".equalsIgnoreCase(loginHook)){
					Instance<LoginHook> hooks = CDI.current().select(LoginHook.class);
					for(LoginHook hook:hooks){
						int idx = hook.getClass().getSimpleName().indexOf("$");
						String name;
						if (idx != -1) {
							name = hook.getClass().getSimpleName().substring(0, idx);
						} else {
							name = hook.getClass().getSimpleName();
						}
						String msg = String.format("Starting execution of %s",name);
						log.info(msg);
						hook.execute();
						msg = String.format("Finished execution of %s",name);
						log.info(msg);
					}
				}
			} else {
				systemUser = existingUser.get();
			}
			this.user = systemUser;
		} catch (SystemException exception){
			log.error(exception.getMessage());
		}
		if (this.user == null){
			this.user = UserFactory.create(givenname,familyName,preferredUserName, userIdSubject,email,-1L);
		}
		String msg = String.format("userId:%d",getUserId());
		log.info(msg);
	}

	/**
	 * User session user object getter
	 * @return the active logged in user
	 */
	@Override
	public SystemUser getUser() {
		return user;
	}

	/**
	 * User session setter for systemUser
	 * @param user systemUser object
	 */
	public void setUser(SystemUser user) {
		this.user = user;
	}

	/**
	 * User session id getter
	 * @return the user id
	 */
	public Long getUserId() { return user != null ? user.getId() : null; }

	/**
	 * Validates if the user session is still active or not
	 * @return true if user is still active
	 */
	public boolean isActive() {
		return user!=null && user.isEnabled();
	}

	/**
	 * User session subject getter
	 * @return the userIdSubject
	 */
	public String getUserIdSubject() {
		return user.getSub();
	}

	/**
	 * User session email getter
	 * @return the email
	 */
	public String getEmail() {
		return user.getUserEmail();
	}

	/**
	 * User session prefered user name getter
	 * @return the preferredUserName
	 */
	public String getPreferredUserName() {
		log.info("get preferred user Name");
		return user.getLogon() != null ? user.getLogon() : user.getUserEmail();
	}

	/**
	 * User session first name getter
	 * @return the userFirstName
	 */
	public String getUserFirstName() {
		log.info("get First Name");
		return user.getFirstname();
	}

	/**
	 * User session user last name
	 * @return the userLastName
	 */
	public String getUserLastName() {
		log.info("get Last Name");
		return user.getLastname();
	}

	/**
	 * User session user full name getter
	 * @return the userFullName
	 */
	public String getUserFullName() {
		log.info("get Full Name");
		return user.getFirstname() + " " +user.getLastname();
	}

	/**
	 * Gets the current OAF access
	 * @return the oaf object
	 */
	@Override
	public OAFAccess getOAF() {
		return oaf;
	}

	/**
	 * User session access token getter
	 * @return the user access token
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * User session access token setter
	 * @param accessToken to be set
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * User session refresh token getter
	 * @return user refresh token
	 */
	public String getRefreshToken() {
		return refreshToken;
	}

	/**
	 * User session refresh token setter
	 * @param refreshToken to be set
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}


	/**
	 * Perform the logout process
	 * @return boolean that indicates if the process was successfully concluded or not
	 */
	public boolean logout() {
		if (isActive()) {
			ExternalContext externalContext = JSFUtil.getExternalContext();
			HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
			HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
			return logout(request, response);
		}
		return false;
	}

	/**
	 * Core (internal) that uses the servlet request
	 * and servlet response to perform the logout process
	 * @param request used to obtain the logout url, the cookies and so on
	 * @param response used to perform redirection
	 * @return boolean that indicates if the process was successfully concluded or not
	 */
	protected boolean logout(HttpServletRequest request, HttpServletResponse response) {
		try {
			log.info("going to start logout process");
			request.logout();
			String logoutUrl = getLogoutURL();
			log.info("going to redirect to the following url {}", logoutUrl);
			response.sendRedirect(logoutUrl);
			return true;
		} catch (ServletException | IOException e) {
			log.error("error performing logout", e);
			return false;
		}
	}

	/**
	 * Retrieve the logout URL
	 * @return String that represents the logout url
	 */
	protected String getLogoutURL() {
		String keyCloakLogoutURL = oaf.getProperty(OAFProperties.AUTH_LOGOUT_URI);
		StringBuilder sb = new StringBuilder().
				append(keyCloakLogoutURL).
				append("?redirect_uri=").
				append(getApplicationURL());
		return sb.toString();
	}

	/**
	 * Retrieves the base url (schema + server name + port + context) for the current application
	 * @return String that represent the context url path
	 */
	protected String getApplicationURL() {
		String applicationUrl = oaf.getProperty(OAFProperties.SYS_HOSTNAME) +
				oaf.getProperty(OAFProperties.SYS_APPLICATION_CONTEXT);
		log.info("application url {}", applicationUrl);
		return applicationUrl;
	}
}
