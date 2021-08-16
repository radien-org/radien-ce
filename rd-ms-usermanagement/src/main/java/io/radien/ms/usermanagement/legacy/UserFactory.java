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
import io.radien.api.util.FactoryUtilService;

import io.radien.ms.usermanagement.client.util.UserFactoryUtil;
import io.radien.ms.usermanagement.entities.UserEntity;

import java.util.Map;
import javax.enterprise.context.RequestScoped;

import javax.json.JsonObject;

/**
 * Factory class responsible for producing User related objects
 *
 * @author Bruno Gama
 * @author Nuno Santana
 */
@RequestScoped
public class UserFactory extends UserFactoryUtil {

	private static final long serialVersionUID = -5005556415803181075L;

	/**
	 * Create a user with already predefine fields.
	 *
	 * @param firstname user first name
	 * @param lastname user last name
	 * @param logon user logon
	 * @param sub user subject
	 * @param createdUser the user which has created the user
	 * @param email user email
	 * @return a User object to be used
	 */
	public static UserEntity create(String firstname, String lastname, String logon, String sub, String email, Long createdUser) {
		UserEntity user = new UserEntity();
		user.setEnabled(true);
		return (UserEntity) createUser(user, firstname, lastname, logon, sub, email, createdUser);
	}

	/**
	 * Converts a JSONObject to a SystemUser object Used by the Application
	 * DataInit to seed Data in the database
	 *
	 * @param userEntity the JSONObject to convert
	 * @return the SystemUserObject
	 */
	public static UserEntity convert(JsonObject userEntity) {
		UserEntity user = new UserEntity();
		Map<String, Object> userEntityMappedValues = convertJsonObject(userEntity);
		Boolean delegatedCreation = FactoryUtilService.getBooleanFromJson("delegatedCreation",userEntity);
		Boolean enabled = FactoryUtilService.getBooleanFromJson("enabled",userEntity);

		user.setId((Long) userEntityMappedValues.get("id"));
		if(enabled != null) {
			user.setDelegatedCreation(enabled);
		}

		if(delegatedCreation != null) {
			user.setDelegatedCreation(delegatedCreation);
		}
		return (UserEntity) createUser(user, (String) userEntityMappedValues.get("firstname"), (String) userEntityMappedValues.get("lastname"),
				(String) userEntityMappedValues.get("logon"), (String) userEntityMappedValues.get("sub"),
				(String) userEntityMappedValues.get("userEmail"), (Long) userEntityMappedValues.get("createUser"));
	}

	/**
	 * Converts a System user to a Json Object
	 *
	 * @param userEntity system user to be converted to json
	 * @return json object with keys and values constructed
	 */
	public static JsonObject convertToJsonObject(SystemUser userEntity) {
		return convertSystemUserToJsonObject(userEntity);
	}


}
