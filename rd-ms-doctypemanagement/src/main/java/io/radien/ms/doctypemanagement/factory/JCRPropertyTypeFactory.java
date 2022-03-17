/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.ms.doctypemanagement.factory;

import io.radien.api.model.docmanagement.propertytype.SystemJCRPropertyType;
import io.radien.api.util.FactoryUtilService;

import io.radien.ms.doctypemanagement.entities.JCRPropertyTypeEntity;
import javax.enterprise.context.RequestScoped;

import java.io.Serializable;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
/**
 * Factory class responsible for producing JCRPropertyTypeEntity related objects
 *
 * @author Bruno Gama
 * @author Nuno Santana
 */
@RequestScoped
public class JCRPropertyTypeFactory implements Serializable {

	/**
	 * Create a JCRPropertyTypeEntity with already predefine fields.
	 *
	 * @param name of JCRPropertyTypeEntity

	 * @return a JCRPropertyTypeEntity object to be used
	 */
	public static JCRPropertyTypeEntity create(String name) {
		JCRPropertyTypeEntity u = new JCRPropertyTypeEntity();
		u.setName(name);

		return u;
	}

	/**
	 * Converts a JSONObject to a SystemJCRPropertyTypeEntity object Used by the Application
	 * DataInit to seed Data in the database
	 *
	 * @param jsonObjectJCRPropertyType the JSONObject to convert
	 * @return the SystemDemoObject
	 */
	//TODO: Complete the object conversion fields missing
	public static JCRPropertyTypeEntity convert(JsonObject jsonObjectJCRPropertyType) {
		Long id = FactoryUtilService.getLongFromJson("id", jsonObjectJCRPropertyType);
		String name = FactoryUtilService.getStringFromJson("name", jsonObjectJCRPropertyType);

		JCRPropertyTypeEntity jcrpropertytype = new JCRPropertyTypeEntity();
		jcrpropertytype.setId(id);
		jcrpropertytype.setName(name);

		return jcrpropertytype;
	}

	/**
	 * Converts a SystemJCRPropertyType to a Json Object
	 *
	 * @param jcrpropertytype to be converted to json
	 * @return json object with keys and values constructed
	 */
	public static JsonObject convertToJsonObject(SystemJCRPropertyType jcrpropertytype) {
		JsonObjectBuilder builder = Json.createObjectBuilder();

		FactoryUtilService.addValueLong(builder, "id", jcrpropertytype.getId());
		FactoryUtilService.addValue(builder, "name", jcrpropertytype.getName());

		return  builder.build();
	}
}
