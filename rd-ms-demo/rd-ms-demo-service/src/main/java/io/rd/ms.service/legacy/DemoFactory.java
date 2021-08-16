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
package io.rd.ms.service.legacy;

import io.rd.api.model.SystemDemo;
import io.rd.api.util.FactoryUtilService;
import io.rd.ms.service.entities.Demo;

import javax.enterprise.context.RequestScoped;
import javax.json.*;
import java.io.Serializable;

/**
 * Factory class responsible for producing Demo related objects
 *
 * @author Bruno Gama
 * @author Nuno Santana
 */
@RequestScoped
public class DemoFactory implements Serializable {

	private static final long serialVersionUID = 6812608123262000066L;

	/**
	 * Create a demo with already predefine fields.
	 *
	 * @param firstname demo first name

	 * @return a Demo object to be used
	 */
	public static Demo create(String firstname) {
		Demo u = new Demo();
		u.setName(firstname);

		return u;
	}

	/**
	 * Converts a JSONObject to a SystemDemo object Used by the Application
	 * DataInit to seed Data in the database
	 *
	 * @param person the JSONObject to convert
	 * @return the SystemDemoObject
	 */
	//TODO: Complete the object conversion fields missing
	public static Demo convert(JsonObject person) {
		Long id = FactoryUtilService.getLongFromJson("id", person);
		String name = FactoryUtilService.getStringFromJson("name", person);

		Demo demo = new Demo();
		demo.setId(id);
		demo.setName(name);

		return demo;
	}

	/**
	 * Converts a System demo to a Json Object
	 *
	 * @param person system demo to be converted to json
	 * @return json object with keys and values constructed
	 */
	public static JsonObject convertToJsonObject(SystemDemo person) {
		JsonObjectBuilder builder = Json.createObjectBuilder();

		FactoryUtilService.addValueLong(builder, "id", person.getId());
		FactoryUtilService.addValue(builder, "name", person.getName());

		return  builder.build();
	}
}
