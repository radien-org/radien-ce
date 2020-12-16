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
package io.radien.ms.usermanagement.legacy;

import java.util.Collection;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.model.user.SystemUser;
import io.radien.api.service.user.UserServiceAccess;
import io.radien.exception.UserNotFoundException;

/**
 * @author Bruno Gama
 */
@RequestScoped
public class UserService implements UserServiceAccess {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(UserService.class);


	@Inject
	private UserDataAccessLayer userDAL;


	@Override
	public SystemUser getUserByLoginId(String loginId) throws UserNotFoundException {
		SystemUser user = userDAL.getUserByLoginId(loginId);
//		log.debug(audit, loggingUtil.buildAuditLogNumberOfResults(user));
		return user;
	}

	@Override
	public SystemUser get(Long userId) {
		SystemUser user = userDAL.get(userId);
//		log.debug(audit, loggingUtil.buildAuditLogNumberOfResults(user));
		return user;
	}

	@Override
	public List<SystemUser> get(List<Long> userIds){
		return userDAL.get(userIds);
	}

	@Override
	public List<SystemUser> getAll(){
		return userDAL.getAll();
	}

	@Override
	public void save(SystemUser user) {
//		log.info(audit, loggingUtil.buildAuditLog("Attempting to save user: {}", user));
		userDAL.save(user);
		//TODO rf: UNS
//		log.info(audit, loggingUtil.buildAuditLog("User saved successfully"));
	}

	@Override
	@Deprecated
	public SystemUser getUserByEmailAndEnabled(String email, boolean enabled) throws UserNotFoundException {
		SystemUser user = userDAL.getUserByEmailAndEnabled(email, enabled);
//		log.debug(audit, loggingUtil.buildAuditLogNumberOfResults(user));
		return user;
	}

	@Override
	public void delete(SystemUser systemUser) {
//		log.info(audit, loggingUtil.buildAuditLog("Attempting to delete user: {}", systemUser.getFullName()));
		userDAL.delete(systemUser);
//		log.info(audit, loggingUtil.buildAuditLog("User deleted successfully"));
	}

	@Override
	public void delete(Collection<SystemUser> systemUsers) {
//		log.info(audit, loggingUtil.buildAuditLog("Attempting to delete users: {}",systemUsers.stream().map(i->Long.toString(i.getId())).collect(Collectors.joining(",")) ));
		userDAL.delete(systemUsers);
//		log.info(audit, loggingUtil.buildAuditLog("Users deleted successfully"));
	}

	@Override
	public List<SystemUser> getUsers(boolean registered, Long days) {
		return userDAL.getUsers(registered, days);
	}

	@Override
	public List<SystemUser> getUsersById(Collection<Long> ids) {
		List<SystemUser> users = userDAL.getUsersById(ids);
//		log.debug(audit, loggingUtil.buildAuditLogNumberOfResults(users));
		return users;
	}
}
