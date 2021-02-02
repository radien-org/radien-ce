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
package io.radien.ms.usermanagement.service;

import io.radien.api.entity.Page;
import io.radien.api.model.user.SystemUser;
import io.radien.api.model.user.SystemUserSearchFilter;
import io.radien.api.service.batch.BatchSummary;
import io.radien.api.service.user.UserServiceAccess;
import io.radien.exception.UniquenessConstraintException;
import io.radien.exception.UserNotFoundException;
import io.radien.ms.usermanagement.client.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * @author Nuno Santana
 */

@Stateless
public class UserBusinessService implements Serializable {

	private static final long serialVersionUID = 9136599710056928804L;

	private static final Logger log = LoggerFactory.getLogger(UserBusinessService.class);

	@Inject
	private UserServiceAccess userServiceAccess;

	public Page<? extends SystemUser> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending){
		return userServiceAccess.getAll(search,pageNo,pageSize,sortBy,isAscending);
	}

	public List<? extends SystemUser> getUsers(SystemUserSearchFilter filter){
		return userServiceAccess.getUsers(filter);
	}

	public SystemUser get(long id) throws UserNotFoundException {
		return userServiceAccess.get(id);
	}
	public List<SystemUser> get(List<Long> ids) {
		return userServiceAccess.get(ids);
	}

	public void delete(long id){
		userServiceAccess.delete(id);
	}

	public void save(User user) throws UniquenessConstraintException, UserNotFoundException {
		userServiceAccess.save(user);
	}

	public BatchSummary create(List<? extends SystemUser> users) {
		BatchSummary summary = userServiceAccess.create(users);
		return summary;
	}
}
