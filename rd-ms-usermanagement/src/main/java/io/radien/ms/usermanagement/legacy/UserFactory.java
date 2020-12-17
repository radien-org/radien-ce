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

import io.radien.api.model.user.SystemUser;
import io.radien.persistence.entities.user.User;

import javax.enterprise.context.RequestScoped;
import java.io.Serializable;

/**
 * Factory class responsible for producing User related objects
 *
 * @author Marco Weiland
 * @author jsr
 */
@RequestScoped
public class UserFactory implements Serializable {

	private static final long serialVersionUID = -5005556415803181075L;

	/**
	 * Converts a SystemUser object to the corresponding JPA entity
	 * {@link User}, so it can be saved on the DB
	 *
	 * @param o the SystemUser to convert
	 * @return The jpa compliant {@link User}
	 */
	public User convert(SystemUser o) {
		User user = (User) o;
		return user;
	}
}
