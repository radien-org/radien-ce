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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.mail.AuthenticationFailedException;
import javax.security.auth.login.AccountExpiredException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.radien.api.model.user.SystemUser;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import io.radien.api.webapp.i18n.LocaleManagerAccess;
import io.radien.exception.AccountNotValidException;
import io.radien.exception.SessionNotValidException;


/**
 * Class responsible for managing the current user session
 *
 * @author Marco Weiland
 */
public @Model @SessionScoped class UserSession implements Serializable {

	private static final long serialVersionUID = 1198636791261091733L;
	private static final Logger log = LoggerFactory.getLogger(UserSession.class);
	private static final Marker audit = MarkerFactory.getMarker("AUDIT");

//	@Inject
//	@OIDCMode
//	protected AuthenticationService authService;
//	@Inject
//	protected AuthorizationServiceAccess authorizationService;
//	@Inject
//	protected UserServiceAccess userService;
//	@Inject
//	protected PermissionServiceAccess permissionService;
//	@Inject
//	protected ContextRoleServiceAccess contextRolesService;
//	@Inject
//	protected RoleServiceAccess rolesService;
//	@Inject
//	protected ResourceServiceAccess resourceService;
//	@Inject
//	protected ContextServiceAccess contextService;
//	@Inject
//	protected OperationServiceAccess operationService;
	@Inject
	protected LocaleManagerAccess localeManager;
	@Inject
	protected SessionHandler sessionHandler;
	@Inject
	protected RedirectHandler redirectUtil;
//	@Inject
//	protected LoggingUtil loggingUtil;
//	@Inject
//	protected UserContextManager userContextService;
//	@Inject
//	protected ScimServiceAccess scimServiceAccess;
//	@Inject
//	protected AccountAssociationServiceAccess accountAssociationService;
//	@Inject
//	protected UserPolicyRecordServiceAccess userPolicyRecordServiceAccess;
//	@Inject
//	private LegalDocumentsService legalDocumentsService;
//	@Inject
//	private UserPolicyService userPolicyService;

	protected SystemUser user;
//	protected SystemContext currentContext;
	protected boolean userRequestsPanelOpen;
	protected Set<Pair<String, Object[]>> bannerList = new HashSet<>();
	
	
	public void login(String userId) {
		log.info("user logged in: {}", userId);
		
	}

	public SystemUser loginActionExternal(String logon, HttpServletRequest request,
			HttpServletResponse response, UserDetails userDetails)
			throws AuthenticationFailedException, AccountNotValidException, AccountExpiredException {
		log.info("Init login Action External");
//		user = authService.authenticateExternal(logon, localeManager.getActiveLanguage(),
//				localeManager.getClientTzOffset(), userDetails);
//		authService.validate(user);
//		sessionHandler.register(request, response, user);
//		userContextService.reload();
//		log.info(audit, loggingUtil.buildAuditLog("Has logged in"));
//		userRequestsPanelOpen = true;
		return user;
	}

	public void reloadUser() {
//		long id = user.getId();
//		try {
//			user = userService.get(id);
//			userContextService.reload();
//		} catch (Exception e) {
//			log.error("problem reloading the user with id {}",id);
//		}

	}

	/**
	 * Calls the appropriate authentication service to attempt a login in the
	 * system
	 *
	 * @param logon
	 *                     the user identifier (noemaly the user email)
	 * @param password
	 *                     the user password (in clear text)
	 * @return the {@link SystemUser} object
	 * @throws AuthenticationFailedException
	 *                                           thrown if the authentication
	 *                                           fails
	 */
	public SystemUser loginAction(String logon, String password, HttpServletRequest request,
			HttpServletResponse response)
			throws AuthenticationFailedException, AccountNotValidException, AccountExpiredException {

//		user = authService.authenticate(logon, password, localeManager.getActiveLanguage(),
//				localeManager.getClientTzOffset());
//		authService.validate(user);
//		sessionHandler.register(request, response, user);
//		userContextService.reload();
//		log.info(audit, loggingUtil.buildAuditLog("Has logged in"));
//		userRequestsPanelOpen = true;
		return user;
	}
	
	public SystemUser getUser(){
		return getUser(false);
	}

	public SystemUser getUser(boolean forLog) {
		if ( user == null ) {
			try {
				user = sessionHandler.getUser();
				//TODO : CHECK, IN SOME CASES; USEROBJECT DOES NOT HAVE CORRECT LOGON
				if ( user == null && !forLog) {
					log.info("USER IS NULL");
				} else if(!forLog){
					log.info("USER LOGGED IN {}", user);
					if ( user.getLogon() == null ) {
						log.info("USER LOGON IS NULL");
					} else {
						log.info("usersession initialized for {}", user.getLogon());
					}
				}
			} catch (Exception e) {
				log.error("error on initialization", e);
			}
		}

		return user;
	}

	public void loadImpersonateUsers() {
		log.info("::: IMPERSONATION LOADING :::");
	}

	public boolean isLoggedIn() {
		return user != null;
	}

	/**
	 * Verifies if this is a valid session by validating if a user exists on it
	 *
	 * @return true if this session is valid
	 * @throws SessionNotValidException
	 *                                      thrown if session is not valid
	 */
	public boolean isValid() throws SessionNotValidException {
		try {
			return isLoggedIn();
		} catch (Exception e) {
			log.error("Error verifying user validity", e);
			throw new SessionNotValidException(e);
		}

	}

	/**
	 * Method responsible for login out a user by invalidating the current
	 * session
	 */
	public void logout() {
//		try {
//			scimServiceAccess.changeUserCurrentAssociatedAccount(user, null);
//		} catch (SystemException e) {
//			log.error("Error on changing account association for user {}", user.getLogon());
//		}
		sessionHandler.logout();
//		log.info(audit, loggingUtil.buildAuditLog("Logged out from the system"));
		redirectUtil.redirectToLogout();
	}

	/**
	 * Method responsible for login out a user by invalidating the current
	 * session
	 */
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		if(isLoggedIn()) {
//			try {
//				scimServiceAccess.changeUserCurrentAssociatedAccount(user, null);
//			} catch (SystemException e) {
//				log.error("Error on changing account association for user {}", user.getLogon());
//			}
		}
		sessionHandler.logout(request, response);
//		log.info(audit, loggingUtil.buildAuditLog("Logged out from the system"));
		redirectUtil.redirectToLogout();
	}

//	public boolean userHasAcceptedLatestPolicyDocuments(SystemContext context) {
//		if(user != null) {
//			return userPolicyService.userHasAcceptedPolicy(user, context.getContract(), PolicyDocType.TERMS_OF_USE) &&
//					userPolicyService.userHasAcceptedPolicy(user, context.getContract(), PolicyDocType.CONTACT) &&
//					userPolicyService.userHasAcceptedPolicy(user, context.getContract(), PolicyDocType.IMPRINT) &&
//					userPolicyService.userHasAcceptedPolicy(user, context.getContract(), PolicyDocType.LICENSES) &&
//					userPolicyService.userHasAcceptedPolicy(user, context.getContract(), PolicyDocType.COOKIES) &&
//					userPolicyService.userHasAcceptedPolicy(user, context.getContract(), PolicyDocType.DATA_PRIVACY_POLICY);
//		}
//		return false;
//	}

//	public boolean userHasAcceptedLatestGlobalPolicyDoc(PolicyDocType docType) {
//		return userHasAcceptedLatestLocalPolicyDoc(docType, null);
//	}
//
//	public boolean userHasAcceptedLatestLocalPolicyDoc(PolicyDocType docType, SystemOrganizationContract contract) {
//		if(user != null) {
//			return userPolicyService.userHasAcceptedPolicy(user, contract, docType);
//		}
//		return false;
//	}
//
//	public boolean userHasContext() {
//		UserContext userContext = userContextService.getCachedUserContext();
//		return userContext != null && !userContext.getContexts().isEmpty();
//	}
//
//	public boolean isUserAdminOfCurrentContext() {
//		if (userHasContext())
//			return getUserAdminRoleOfCurrentContext() != null;
//		return false;
//	}
//
//	public SystemAdminRole getUserAdminRoleOfCurrentContext() {
//		if (!userContextService.getCachedUserContext().getContexts().isEmpty()) {
//			if (authorizationService.isSuperAdmin())
//				return AdminRole.SUPER_ADMIN;
//
//			List<SystemRole> adminRoles = rolesService.getAdminSystemRolesOfOrganizationContract(currentContext.getContract());
//			for (SystemRole contextRole : contextRolesService.getSystemRolesByContextId(currentContext.getId())) {
//				for(SystemRole role : adminRoles) {
//					if (contextRole.getId().equals(role.getId())) {
//						return AdminRole.valueOf(role);
//					}
//				}
//			}
//		}
//		return null;
//	}
//
//	public boolean hasRoleInCurrentContext(String name) {
//		SystemContext context = currentContext;
//		assert context != null;
//		return hasRoleInContext(name, context);
//	}
//
//	public boolean userCurrentContextOrganizationAboveOrganizationType(OrgType type) {
//		SystemContext context = currentContext;
//		if (context != null) {
//			return OrgType.valueOfId(currentContext.getContract().getOrganizationType().getId()).isAbove(type);
//		}
//		return false;
//	}
//
//	public boolean userCurrentContextOrganizationAboveOrEqualOrganizationType(OrgType type) {
//		try {
//			return userCurrentContextOrgType().isAboveOrEqual(type);
//		} catch (UserHasNoCurrentContextException e) {
//			return false;
//		}
//	}
//
//	public boolean userCurrentContextOrganizationEqualOrganizationType(OrgType type) {
//		try {
//			return userCurrentContextOrgType() == type;
//		} catch (UserHasNoCurrentContextException e) {
//			return false;
//		}
//	}
//
//	public SystemOrgType userCurrentContextOrgType() throws UserHasNoCurrentContextException {
//		return OrgType.valueOf(userCurrentContextOrganizationType());
//	}
//
//	public SystemOrganizationType userCurrentContextOrganizationType() throws UserHasNoCurrentContextException {
//		SystemContext context = currentContext;
//		if (context != null) {
//			return currentContext.getContract().getOrganizationType();
//		} else {
//			throw new UserHasNoCurrentContextException();
//		}
//	}
//
//
//	private boolean hasRoleInContext(String name, SystemContext context) {
//		// TODO: BH improve this
//		for (SystemRole role : contextRolesService.getSystemRolesByContextId(context.getId())) {
//			if (role.getName().equals(name))
//				return true;
//		}
//		return false;
//	}
//
//	public boolean userHasPermission(Long operationId, String resource) {
//		assert user != null;
//		SystemContext context;
//		String contextErrorMsg ="";
//		if(authorizationService.isSuperAdmin()){
//			List<SystemContext> contexts = contextService.getSystemContextsByUserIdAndOrganizationContractId(user.getId(), SystemOrganizationData.CONTRACT_ID, false);
//			assert !contexts.isEmpty();
//			context = contexts.get(0);
//			contextErrorMsg = String.format("Super Admin id[%s]",user.getId());
//		} else {
//			if(currentContext == null){
//				return false;
//			} else {
//				context = currentContext;
//				contextErrorMsg = String.format("currentContext id[%s]",context.getId());
//			}
//		}
//		SystemOperation op = operationService.getSystemOperationById(operationId);
//		SystemResource res = resourceService.getSystemResourceByName(resource);
//		if(context == null){
//			String msg = String.format("Error while checking if user has permission. Unable to get Context[%s]",contextErrorMsg);
//			log.error(msg);
//		}
//		if(op == null){
//			String msg = String.format("Error while checking if user has permission. Unable to get Operation[%s]",operationId);
//			log.error(msg);
//		}
//		if(res == null){
//			String msg = String.format("Error while checking if user has permission. Unable to get Resource[%s]",resource);
//			log.error(msg);
//		}
//
//		return permissionService.hasPermission(context, op, res);
//	}
//
//	public boolean userHasPermissionForOrg(List<Long> operationids, String resource, Long organizationContractId) {
//		SystemResource res = resourceService.getSystemResourceByName(resource);
//		if(res == null){
//			String msg = String.format("Error while checking if user has permission. Unable to get Resource[%s]",resource);
//			log.error(msg);
//			return false;
//		}
//		return permissionService.userHasAccessAndOperationIdAndResourceIdAndOrganizationContractId(user.getId(),operationids,res.getId(),organizationContractId);
//  }
//
//	public boolean userHasAccessToManage(SystemOrganizationContract contract) {
//		return authorizationService.hasAccessToManage(contract, getCurrentContext().getContract());
//	}
//
//	public SystemContext getCurrentContext() {
//		return currentContext;
//	}
//
//	public void setCurrentContext(SystemContext context) {
//		if (context != null && (this.currentContext == null
//				|| !context.getId().equals(this.currentContext.getId()))) {
//			try {
//				scimServiceAccess.changeUserCurrentAssociatedAccount(user, context.getContract());
//			} catch (SystemException e) {
//				log.error("error on changing account association for user {}", user.getLogon());
//			}catch (Exception e) {
//				log.error("error on changing account association for user {}",user.getLogon(), e);
//			}
//			this.currentContext = context;
//		} else if(context == null) {
//			this.currentContext = null;
//		}
//	}
//
//	public UserContext getCachedUserContext() {
//		return userContextService.getCachedUserContext();
//	}
//
	
//
//	public boolean isUserRequestsPanelOpen() {
//		return userRequestsPanelOpen;
//	}
//
//	public void toggleUserRequestsPanel() {
//		userRequestsPanelOpen = !userRequestsPanelOpen;
//	}
//
//	public void addBannerToDisplay(String key, Object... parameters) {
//		Pair<String, Object[]> pair = new ImmutablePair<>(key, parameters);
//		bannerList.add(pair);
//	}
//
//	public void popMessages() {
//		for(Pair<String, Object[]> pair : bannerList) {
//			JSFUtil.addWarningMessage("", pair.getLeft(), pair.getRight());
//		}
//		bannerList = new HashSet<>();
//	}

}
