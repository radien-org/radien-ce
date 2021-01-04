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
import io.radien.ms.usermanagement.client.util.FactoryUtilService;
import io.radien.ms.usermanagement.entities.User;

import javax.enterprise.context.RequestScoped;
import javax.json.*;
import java.io.Serializable;
import java.util.Date;


/**
 * Factory class responsible for producing User related objects
 *
 * @author Bruno Gama
 * @author Nuno Santana
 */
@RequestScoped
public class UserFactory implements Serializable {

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
	public static User create(String firstname, String lastname, String logon, String sub, String email, Long createdUser) {
		User u = new User();
		u.setFirstname(firstname);
		u.setLastname(lastname);
		u.setLogon(logon);
		u.setEnabled(true);
		u.setSub(sub);
		u.setCreateUser(createdUser);
		Date now = new Date();
		u.setLastUpdate(now);
		u.setCreateDate(now);
		u.setUserEmail(email);
		return u;
	}

	/**
	 * Converts a JSONObject to a SystemUser object Used by the Application
	 * DataInit to seed Data in the database
	 *
	 * @param person the JSONObject to convert
	 * @return the SystemUserObject
	 */
	//TODO: Complete the object conversion fields missing
	public static User convert(JsonObject person) {
		String logon = FactoryUtilService.getStringFromJson("logon", person);
		String userEmail = FactoryUtilService.getStringFromJson("userEmail", person);
		Long createUser = FactoryUtilService.getLongFromJson("createUser", person);
		Long lastUpdateUser = FactoryUtilService.getLongFromJson("lastUpdateUser", person);
		String sub = FactoryUtilService.getStringFromJson("sub", person);
		String firstname = FactoryUtilService.getStringFromJson("firstname", person);
		String lastname = FactoryUtilService.getStringFromJson("lastname",person);

		User user = new User();
		user.setLogon(logon);
		user.setUserEmail(userEmail);
		user.setCreateDate(new Date());
		user.setLastUpdate(new Date());
		user.setSub(sub);
		user.setFirstname(firstname);
		user.setLastname(lastname);
		user.setCreateUser(createUser);

		return user;
	}

	/**
	 * Converts a System user to a Json Object
	 *
	 * @param person system user to be converted to json
	 * @return json object with keys and values constructed
	 */
	public static JsonObject convertToJsonObject(SystemUser person) {
		JsonObjectBuilder builder = Json.createObjectBuilder();

		FactoryUtilService.addValue(builder, "id", person.getId());
		FactoryUtilService.addValue(builder, "logon", person.getLogon());
		FactoryUtilService.addValue(builder, "userEmail", person.getUserEmail());
		FactoryUtilService.addValueLong(builder, "createUser", person.getCreateUser());
		FactoryUtilService.addValueLong(builder, "lastUpdateUser", person.getLastUpdateUser());
		FactoryUtilService.addValue(builder, "sub", person.getSub());
		FactoryUtilService.addValue(builder, "firstname", person.getFirstname());
		FactoryUtilService.addValue(builder, "lastname", person.getLastname());
		return  builder.build();
	}
}
