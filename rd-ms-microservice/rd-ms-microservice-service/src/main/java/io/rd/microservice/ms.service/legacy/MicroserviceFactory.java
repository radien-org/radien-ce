/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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
package io.rd.microservice.ms.service.legacy;

import io.rd.microservice.api.model.SystemMicroservice;
import io.rd.microservice.api.util.FactoryUtilService;
import io.rd.microservice.ms.service.entities.Microservice;

import javax.enterprise.context.RequestScoped;
import javax.json.*;
import java.io.Serializable;

/**
 * Factory class responsible for producing Microservice related objects
 *
 * @author Bruno Gama
 * @author Nuno Santana
 */
@RequestScoped
public class MicroserviceFactory implements Serializable {

	private static final long serialVersionUID = 6812608123262000066L;

	/**
	 * Create a microservice with already predefine fields.
	 *
	 * @param firstname microservice first name

	 * @return a Microservice object to be used
	 */
	public static Microservice create(String firstname) {
		Microservice u = new Microservice();
		u.setName(firstname);

		return u;
	}

	/**
	 * Converts a JSONObject to a SystemMicroservice object Used by the Application
	 * DataInit to seed Data in the database
	 *
	 * @param person the JSONObject to convert
	 * @return the SystemMicroserviceObject
	 */
	//TODO: Complete the object conversion fields missing
	public static Microservice convert(JsonObject person) {
		Long id = FactoryUtilService.getLongFromJson("id", person);
		String name = FactoryUtilService.getStringFromJson("name", person);

		Microservice microservice = new Microservice();
		microservice.setId(id);
		microservice.setName(name);

		return microservice;
	}

	/**
	 * Converts a System microservice to a Json Object
	 *
	 * @param person system microservice to be converted to json
	 * @return json object with keys and values constructed
	 */
	public static JsonObject convertToJsonObject(SystemMicroservice person) {
		JsonObjectBuilder builder = Json.createObjectBuilder();

		FactoryUtilService.addValueLong(builder, "id", person.getId());
		FactoryUtilService.addValue(builder, "name", person.getName());

		return  builder.build();
	}
}
