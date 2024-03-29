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
package io.radien.webapp.security;

import io.radien.api.KeycloakConfigs;
import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.kernel.messages.SystemMessages;
import io.radien.api.model.user.SystemUser;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.api.security.UserSessionEnabled;
import io.radien.api.service.LoginHook;
import io.radien.api.service.user.UserRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.usermanagement.client.exceptions.RemoteResourceException;
import io.radien.ms.usermanagement.client.services.UserFactory;
import io.radien.webapp.JSFUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.eclipse.microprofile.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class responsible for managing the current user session
 *
 * @author Marco Weiland
 */
@Named
@Alternative
@Priority(1)
@SessionScoped
public class UserSession implements UserSessionEnabled, TokensPlaceHolder {

	private static final long serialVersionUID = 1198636791261091733L;
	private static final Logger log = LoggerFactory.getLogger(UserSession.class);

	@Inject
	private UserRESTServiceAccess userClientService;

	@Inject
	private OAFAccess oaf;

	private SystemUser user;

	private String accessToken;

	private String refreshToken;

	private String language;

	@Inject
	private Config config;

	private static final String IDP_LOGOUT_URL_PATTERN = "%s/auth/realms/%s/protocol/openid-connect/logout";

	/**
	 * Initialization of the user session
	 */
	@PostConstruct
	private void init() {
		log.info("Session initiated");
		if(language == null) {
			language = "de";
		}
	}

	/**
	 * Method for taking care and processing the login of the current active user and his session
	 * @param userIdSubject user subject identifier
	 * @param email user email
	 * @param preferredUserName preferred user selected name
	 * @param givenname user name
	 * @param familyName user last name
	 * @param mobileNumber user mobile number
	 * @param accessToken public access token for authorization purposes
	 * @param refreshToken private access token for refreshing the public access token
	 * @throws Exception in case of any issue while performing the login, starting the user session or validating the
	 * user information
	 */
	public void login(String userIdSubject,String email, String preferredUserName, String givenname, String familyName,
					  String mobileNumber, String accessToken, String refreshToken) throws Exception {
		log.info("User session login starting");
		log.info("user logged in: {}", userIdSubject);
		//TODO:		refresh access token if needed
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		try {
			Optional<SystemUser> existingUser = userClientService.getUserBySub(userIdSubject);
			SystemUser systemUser;
			if (!existingUser.isPresent()) {
				systemUser = UserFactory.create(givenname, familyName, preferredUserName, userIdSubject, email, mobileNumber, getOAF().getSystemAdminUserId(), false);
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
			this.user = UserFactory.create(givenname,familyName,preferredUserName, userIdSubject,email, mobileNumber, -1L, false);
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


	public String getMobileNumber() { return user.getMobileNumber(); }
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
			if (externalContext == null) {
				log.error("External context is null");
				return false;
			}
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
			request.getSession().invalidate();
			String logoutUrl = getLogoutURL(request);
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
	protected String getLogoutURL(HttpServletRequest request) {
		String keyCloakLogoutURL = String.format(IDP_LOGOUT_URL_PATTERN,
				config.getValue(KeycloakConfigs.IDP_URL.propKey(), String.class),
				config.getValue(KeycloakConfigs.APP_REALM.propKey(), String.class));
		return keyCloakLogoutURL + "?redirect_uri=" + getRedirectionURL(request);
	}

	public Map<String, String> fakeAuth(String token) throws RemoteResourceException {
		HashMap<String, String> result = null;
		log.info("Config Token: {}", config.getValue(KeycloakConfigs.TOKEN_PATH.propKey(), String.class));
		log.info("Token: {}", token);
		if(config.getValue(KeycloakConfigs.TOKEN_PATH.propKey(), String.class).equals(token)) {
			HttpResponse<?> response = Unirest.post(config.getValue(KeycloakConfigs.IDP_URL.propKey(), String.class)
							+ config.getValue(KeycloakConfigs.TOKEN_PATH.propKey(), String.class))
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
					.field("client_id", config.getValue(KeycloakConfigs.ADMIN_CLIENT_ID.propKey(), String.class))
					.field("grant_type", "client_credentials")
					.field("client_secret", config.getValue(KeycloakConfigs.ADMIN_CLIENT_SECRET.propKey(), String.class))
					.asObject(HashMap.class);
			if (response.isSuccess()) {
				return (HashMap<String, String>) response.getBody();
			} else {
				throw new RemoteResourceException("Error on login");
			}
		}
		return result;
	}

	/**
	 * Retrieves the redirection URL that corresponds for the current application
	 * (To be used as callback uri regarding the logout process)
	 * @return String that represent the redirection url path
	 */
	protected String getRedirectionURL(HttpServletRequest request) {
		String host = oaf.getProperty(OAFProperties.SYS_HOSTNAME);
		String context = oaf.getProperty(OAFProperties.SYS_APPLICATION_CONTEXT);

		if (host.startsWith(SystemMessages.KERNEL_PROPERTY_UNAVAILABLE.message())) {
			log.info("Property {} not defined", OAFProperties.SYS_HOSTNAME);
			StringBuilder sb = new StringBuilder();
			sb.append(request.getScheme()).append("://");
			sb.append(request.getServerName());
			if (request.getServerName().equals("localhost")) {
				sb.append(":");
				sb.append(request.getServerPort());
			}
			host = sb.toString();
		}
		if (context.startsWith(SystemMessages.KERNEL_PROPERTY_UNAVAILABLE.message())) {
			log.info("Property {} not defined", OAFProperties.SYS_APPLICATION_CONTEXT);
			context = request.getContextPath();
		}
		String applicationUrl = host + context;
		log.info("application url {}", applicationUrl);
		return applicationUrl;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}
