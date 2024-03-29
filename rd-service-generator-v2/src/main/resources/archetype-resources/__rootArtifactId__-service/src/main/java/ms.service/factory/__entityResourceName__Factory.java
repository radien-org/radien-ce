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
package ${package}.ms.service.factory;

import io.radien.api.model.${entityResourceName.toLowerCase()}.System${entityResourceName};
import io.radien.api.util.FactoryUtilService;

import ${package}.ms.service.entities.${entityResourceName}Entity;
import javax.enterprise.context.RequestScoped;

import java.io.Serializable;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
/**
 * Factory class responsible for producing ${entityResourceName}Entity related objects
 *
 * @author Bruno Gama
 * @author Nuno Santana
 */
@RequestScoped
public class ${entityResourceName}Factory implements Serializable {

	/**
	 * Create a ${entityResourceName}Entity with already predefine fields.
	 *
	 * @param name of ${entityResourceName}Entity

	 * @return a ${entityResourceName}Entity object to be used
	 */
	public static ${entityResourceName}Entity create(String name) {
		${entityResourceName}Entity u = new ${entityResourceName}Entity();
		u.setName(name);

		return u;
	}

	/**
	 * Converts a JSONObject to a System${entityResourceName}Entity object Used by the Application
	 * DataInit to seed Data in the database
	 *
	 * @param jsonObject${entityResourceName} the JSONObject to convert
	 * @return the SystemDemoObject
	 */
	//TODO: Complete the object conversion fields missing
	public static ${entityResourceName}Entity convert(JsonObject jsonObject${entityResourceName}) {
		Long id = FactoryUtilService.getLongFromJson("id", jsonObject${entityResourceName});
		String name = FactoryUtilService.getStringFromJson("name", jsonObject${entityResourceName});

		${entityResourceName}Entity ${entityResourceName.toLowerCase()} = new ${entityResourceName}Entity();
		${entityResourceName.toLowerCase()}.setId(id);
		${entityResourceName.toLowerCase()}.setName(name);

		return ${entityResourceName.toLowerCase()};
	}

	/**
	 * Converts a System${entityResourceName} to a Json Object
	 *
	 * @param ${entityResourceName.toLowerCase()} to be converted to json
	 * @return json object with keys and values constructed
	 */
	public static JsonObject convertToJsonObject(System${entityResourceName} ${entityResourceName.toLowerCase()}) {
		JsonObjectBuilder builder = Json.createObjectBuilder();

		FactoryUtilService.addValueLong(builder, "id", ${entityResourceName.toLowerCase()}.getId());
		FactoryUtilService.addValue(builder, "name", ${entityResourceName.toLowerCase()}.getName());

		return  builder.build();
	}
}
