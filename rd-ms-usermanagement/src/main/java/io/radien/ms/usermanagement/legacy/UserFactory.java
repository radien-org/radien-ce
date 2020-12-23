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
import io.radien.ms.usermanagement.entities.User;

import javax.enterprise.context.RequestScoped;
import javax.json.*;
import java.io.Serializable;
import java.util.Date;


/**
 * Factory class responsible for producing User related objects
 *
 * @author Marco Weiland
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

	/**
	 * Converts a JSONObject to a SystemUser object Used by the Application
	 * DataInit to seed Data in the database
	 *
	 * @param person the JSONObject to convert
	 * @return the SystemUserObject
	 */
	//TODO: Complete the object conversion fields missing
	public static User convert(JsonObject person) {
		String logon = getStringFromJson("logon", person);
		String userEmail = getStringFromJson("userEmail", person);
		String createUser = getStringFromJson("createUser", person);
		String lastUpdateUser = getStringFromJson("lastUpdateUser", person);
		String sub = getStringFromJson("sub", person);
		String firstname = getStringFromJson("firstname", person);
		String lastname = getStringFromJson("lastname",person);

		User user = new User();
		user.setLogon(logon);
		user.setUserEmail(userEmail);
		// TODO: Set password protected
//		user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
		user.setCreateDate(new Date());
		user.setLastUpdate(new Date());
		user.setSub(sub);
		user.setFirstname(firstname);
		user.setLastname(lastname);

		return user;
	}

	//TODO: Complete the object conversion fields missing
	public static JsonObject convertToJsonObject(SystemUser person) {
		JsonObjectBuilder builder = Json.createObjectBuilder();

		addValue(builder, "id", person.getId());
		addValue(builder, "logon", person.getLogon());
		addValue(builder, "userEmail", person.getUserEmail());
		addValue(builder, "createUser", person.getCreateUser());
		addValue(builder, "lastUpdateUser", person.getLastUpdateUser());
		addValue(builder, "sub", person.getSub());
		addValue(builder, "firstname", person.getFirstname());
		addValue(builder, "lastname", person.getLastname());
		return  builder.build();
	}

	private static void addValue(JsonObjectBuilder builder, String key, Object value) {
		if (value != null) {
			builder.add(key, value.toString());
		} else {
			builder.addNull(key);
		}
	}

	private static String getStringFromJson(String key, JsonObject json) {
		String returnedString = null;
		if (json.containsKey(key)) {
			JsonString value = json.getJsonString(key);
			if (value != null) {
				returnedString = value.getString();
			}
		}
		return returnedString;
	}

	private static Integer getIntFromJson(String key, JsonObject json) {
		Integer returnedValue = null;
		if (json.containsKey(key)) {
			JsonNumber value = json.getJsonNumber(key);
			if (value != null) {
				returnedValue = value.intValue();
			}
		}
		return returnedValue;
	}
}
