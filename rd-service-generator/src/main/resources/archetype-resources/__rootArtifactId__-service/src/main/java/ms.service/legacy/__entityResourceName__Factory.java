/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
package ${package}.ms.service.legacy;

import ${package}.api.model.System${entityResourceName};
import ${package}.api.util.FactoryUtilService;
import ${package}.ms.service.entities.${entityResourceName};

import javax.enterprise.context.RequestScoped;
import javax.json.*;
import java.io.Serializable;

/**
 * Factory class responsible for producing ${entityResourceName} related objects
 *
 * @author Bruno Gama
 * @author Nuno Santana
 */
@RequestScoped
public class ${entityResourceName}Factory implements Serializable {

	private static final long serialVersionUID = 6812608123262000066L;

	/**
	 * Create a ${entityResourceName.toLowerCase()} with already predefine fields.
	 *
	 * @param firstname ${entityResourceName.toLowerCase()} first name

	 * @return a ${entityResourceName} object to be used
	 */
	public static ${entityResourceName} create(String firstname) {
		${entityResourceName} u = new ${entityResourceName}();
		u.setName(firstname);

		return u;
	}

	/**
	 * Converts a JSONObject to a System${entityResourceName} object Used by the Application
	 * DataInit to seed Data in the database
	 *
	 * @param person the JSONObject to convert
	 * @return the System${entityResourceName}Object
	 */
	//TODO: Complete the object conversion fields missing
	public static ${entityResourceName} convert(JsonObject person) {
		Long id = FactoryUtilService.getLongFromJson("id", person);
		String name = FactoryUtilService.getStringFromJson("name", person);

		${entityResourceName} ${entityResourceName.toLowerCase()} = new ${entityResourceName}();
		${entityResourceName.toLowerCase()}.setId(id);
		${entityResourceName.toLowerCase()}.setName(name);

		return ${entityResourceName.toLowerCase()};
	}

	/**
	 * Converts a System ${entityResourceName.toLowerCase()} to a Json Object
	 *
	 * @param person system ${entityResourceName.toLowerCase()} to be converted to json
	 * @return json object with keys and values constructed
	 */
	public static JsonObject convertToJsonObject(System${entityResourceName} person) {
		JsonObjectBuilder builder = Json.createObjectBuilder();

		FactoryUtilService.addValueLong(builder, "id", person.getId());
		FactoryUtilService.addValue(builder, "name", person.getName());

		return  builder.build();
	}
}
