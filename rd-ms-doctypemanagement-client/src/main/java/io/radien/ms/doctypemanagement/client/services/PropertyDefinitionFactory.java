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
package io.radien.ms.doctypemanagement.client.services;

import io.radien.api.entity.Page;
import io.radien.api.util.FactoryUtilService;

import io.radien.ms.doctypemanagement.client.entities.PropertyDefinition;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class PropertyDefinitionFactory {

    private PropertyDefinitionFactory() {
        throw new IllegalStateException("Invalid usage");
    }

    public static PropertyDefinition convert(JsonObject jsonPropertyType) {
        Long id = FactoryUtilService.getLongFromJson("id", jsonPropertyType);
        String name = FactoryUtilService.getStringFromJson("name", jsonPropertyType);
        boolean mandatory = FactoryUtilService.getBooleanFromJson("mandatory", jsonPropertyType);
        boolean protekted = FactoryUtilService.getBooleanFromJson("protected", jsonPropertyType);
        int requiredType = FactoryUtilService.getIntFromJson("requiredType", jsonPropertyType);
        boolean multiple = FactoryUtilService.getBooleanFromJson("multiple", jsonPropertyType);

        LocalDateTime createDate = FactoryUtilService.getLocalDateTimeFromJson("createDate", jsonPropertyType);
        LocalDateTime lastUpdate = FactoryUtilService.getLocalDateTimeFromJson("lastUpdate", jsonPropertyType);
        Long createUser = FactoryUtilService.getLongFromJson("createUser", jsonPropertyType);
        Long lastUpdateUser = FactoryUtilService.getLongFromJson("lastUpdateUser", jsonPropertyType);

        PropertyDefinition propertyType = new PropertyDefinition();
        propertyType.setId(id);
        propertyType.setName(name);
        propertyType.setMandatory(mandatory);
        propertyType.setProtected(protekted);
        propertyType.setRequiredType(requiredType);
        propertyType.setMultiple(multiple);

        propertyType.setCreateDate(createDate != null ? Timestamp.valueOf(createDate) : new Date());
        propertyType.setLastUpdate(lastUpdate != null ? Timestamp.valueOf(lastUpdate) : new Date());
        propertyType.setCreateUser(createUser);
        propertyType.setLastUpdateUser(lastUpdateUser);

        return propertyType;
    }

    public static JsonObject convertToJsonObject(PropertyDefinition propertyType) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        FactoryUtilService.addValueLong(builder, "id", propertyType.getId());
        FactoryUtilService.addValue(builder, "name", propertyType.getName());
        FactoryUtilService.addValueBoolean(builder, "mandatory", propertyType.isMandatory());
        FactoryUtilService.addValueBoolean(builder, "protected", propertyType.isProtected());
        FactoryUtilService.addValueInt(builder, "requiredType", propertyType.getRequiredType());
        FactoryUtilService.addValueBoolean(builder, "multiple", propertyType.isMultiple());
        FactoryUtilService.addValue(builder, "createDate", propertyType.getCreateDate());
        FactoryUtilService.addValue(builder, "lastUpdate", propertyType.getLastUpdate());
        FactoryUtilService.addValueLong(builder, "createUser", propertyType.getCreateUser());
        FactoryUtilService.addValueLong(builder, "lastUpdateUser", propertyType.getLastUpdateUser());

        return builder.build();
    }

    public static Page<PropertyDefinition> convertJsonToPage(JsonObject jsonObject) {
        int currentPage = FactoryUtilService.getIntFromJson("currentPage", jsonObject);
        JsonArray results = FactoryUtilService.getArrayFromJson("results", jsonObject);
        int totalPages = FactoryUtilService.getIntFromJson("totalPages", jsonObject);
        int totalResults = FactoryUtilService.getIntFromJson("totalResults", jsonObject);

        ArrayList<PropertyDefinition> pageResults = new ArrayList<>();

        if(results != null){
            for(int i = 0;i<results.size();i++){
                pageResults.add(convert(results.getJsonObject(i)));
            }
        }
        return new Page<>(pageResults,currentPage,totalResults,totalPages);
    }

    public static List<PropertyDefinition> convert(JsonArray jsonArray) {
        return jsonArray.stream().map(i->convert(i.asJsonObject())).collect(Collectors.toList());
    }
}
