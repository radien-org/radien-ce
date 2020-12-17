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

import io.radien.api.service.user.UserDataAccessLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.model.user.SystemUser;
import io.radien.api.service.user.UserServiceAccess;

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
	public SystemUser get(Long userId) {
		SystemUser user = userDAL.get(userId);
		return user;
	}

	@Override
	public List<SystemUser> get(List<Long> userId) {
		return userDAL.get(userId);
	}

	@Override
	public List<SystemUser> getAll() {
		return userDAL.getAll();
	}

	@Override
	public void save(SystemUser user) {
		userDAL.save(user);
	}

	@Override
	public void delete(SystemUser user) {
		userDAL.delete(user);
	}

	@Override
	public void delete(Collection<SystemUser> user) {
		userDAL.delete(user);
	}
}
