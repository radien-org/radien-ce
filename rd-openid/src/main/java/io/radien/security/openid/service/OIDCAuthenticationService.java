/*

	Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

 */
package io.radien.security.openid.service;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;

import io.radien.api.model.user.SystemUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;

import io.radien.exception.AuthenticationFailedException;

/**
 * This class contains the OIDC type login implementations
 *
 * @author Marco Weiland
 * @author José Rodrigues
 * @author Rafael Fernandes
 * @author Rui Pinho
 */
//@OIDCMode
//@Stateless
@RequestScoped
public class OIDCAuthenticationService implements AuthenticationService, Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(OIDCAuthenticationService.class);
	private static final String DEFAULT_LANGUAGE = "en";
	private static final String DEFAULT_TIMEZONE = "Greenwich (+00:00)";
	@Override
	public SystemUser authenticate(String logon, String password) throws AuthenticationFailedException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public SystemUser authenticate(String logon, String password, String language, String timezone)
			throws AuthenticationFailedException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public SystemUser authenticateExternal(String logon, String language, String timezone, UserDetails userDetails)
			throws AuthenticationFailedException {
		// TODO Auto-generated method stub
		return null;
	}

//	@Inject
//	private UserDataLayer userService;
//	@Inject
//	private UserRestClient userRestClient;
//	@Inject
//	private UserContextManager userContextService;
//	@Inject
//	private UserFactory userFactory;

	/**
	 * Authenticates via OIDC by checking if the current SecurityContext matches
	 * the requested logon variable
	 *
	 * @param logon
	 *                     String with the value of the user identity
	 * @param password
	 *                     String with the value of the users password
	 * @return A {@link SystemUser} object with the user details
	 * @throws AuthenticationFailedException
	 *                                           if the user fails to
	 *                                           authenticate
	 */
//	@Override
//	public SystemUser authenticate(String logon, String password) throws AuthenticationFailedException {
//		throw new AuthenticationFailedException("Authentication with username and password not supported");
//
//	}
//
//	/**
//	 * Authenticates the user by the OIDC provider and adapts it with the
//	 * language and timezone that comes from the client browser
//	 *
//	 * @param logon
//	 *                     String with the value of the user identity
//	 * @param password
//	 *                     String with the value of the users password
//	 * @param language
//	 *                     the locale 2Digit identifier coming from the browser
//	 * @param timezone
//	 *                     the timezone coming drom the client browser
//	 * @return A {@link SystemUser} object with the user details
//	 * @throws AuthenticationFailedException
//	 *                                           if the user fails to
//	 *                                           authenticate
//	 */
//	@Override
//	public SystemUser authenticate(String logon, String password, String language, String timezone)
//			throws AuthenticationFailedException {
//		throw new AuthenticationFailedException("Authentication with username and password not supported");
//	}
//
//	@Override
//	public SystemUser authenticateExternal(String logon, String language, String timezone, UserDetails userDetails)
//			throws AuthenticationFailedException {
//		SystemUser user = null;
//
//		if (logon.equalsIgnoreCase(SecurityContextHolder.getContext().getAuthentication().getName())) {
//			if (userDetails instanceof OpenIdConnectUserDetails) {
//				OpenIdConnectUserDetails openIdConnectUserDetails = (OpenIdConnectUserDetails) userDetails;
//				String userEmail = openIdConnectUserDetails.getUserEmail();
//				String oidcIdentity = openIdConnectUserDetails.getUsername();
//
//				try {
//					user = userService.getUserByLoginId(logon);
//					if (!user.getUserEmail().equalsIgnoreCase(userEmail)
//							&& !user.getLogon().equalsIgnoreCase(oidcIdentity)) {
//						throw new AuthenticationFailedException(
//								"[OIDCAuthenticationService] User/Identity mismatch, cannot map Identity to the user!");
//					}
//					if (user != null) {
//						return user;
//					}
//				} catch (Exception e) {
//					log.info("[OIDCAuthenticationService] user {} not found in system, go ahead with autogeneration",
//							logon, e);
//				}
//
//				try {
//					user = userFactory.create(openIdConnectUserDetails.getUsername(),
//							openIdConnectUserDetails.getUserEmail(), openIdConnectUserDetails.getGivenname(),
//							openIdConnectUserDetails.getFamilyname());
//					
//					//TODO: maintain this with IdP
//					user.setLanguage(language);
//					user.setTimezone(timezone);
//
//					userService.save(user);
//					log.info("[OIDCAuthenticationService] Created new user from ID Token {}", user.getLogon());
//					return user;
//				} catch (Exception e) {
//					log.info("[OIDCAuthenticationService] user {} could not be created",
//							logon, e);
//					throw new AuthenticationFailedException("Unknown user was authenticatd but could not be autocreated!");
//				}
//				
//				
//			} else {
//				throw new AuthenticationFailedException(
//						"Authentication with " + userDetails.getClass().getSimpleName() + " not supported");
//			}
//		} else {
//			log.error("[OIDCAuthenticationService] INVALID USER HAS BEEN TRIED TO REGISTER IN SESSION");
//			throw new AuthenticationFailedException("You are trying to register an unauthenticated user!");
//		}
//
//	}

}
