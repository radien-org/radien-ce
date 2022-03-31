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
import io.radien.ms.doctypemanagement.client.entities.MixinDefinitionDTO;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

public class MixinDefinitionFactory {
    private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";

    private MixinDefinitionFactory() {
        throw new IllegalStateException("Invalid usage");
    }

    public static MixinDefinitionDTO convert(JsonObject jsonMixinType) throws ParseException {
        Long id = FactoryUtilService.getLongFromJson("id", jsonMixinType);
        String name = FactoryUtilService.getStringFromJson("name", jsonMixinType);
        String namespace = FactoryUtilService.getStringFromJson("namespace", jsonMixinType);
        JsonArray jsonPropertyDefinitions = FactoryUtilService.getArrayFromJson("propertyDefinitions", jsonMixinType);
        List<Long> definitions = jsonPropertyDefinitions.getValuesAs(JsonNumber.class)
                .stream()
                .map(JsonNumber::longValue)
                .collect(Collectors.toList());
        boolean abstrakt = FactoryUtilService.getBooleanFromJson("abstract", jsonMixinType);
        boolean queryable = FactoryUtilService.getBooleanFromJson("queryable", jsonMixinType);
        boolean mixin = FactoryUtilService.getBooleanFromJson("mixin", jsonMixinType);

        String createDate = FactoryUtilService.getStringFromJson("createDate", jsonMixinType);
        String lastUpdate = FactoryUtilService.getStringFromJson("lastUpdate", jsonMixinType);
        Long createUser = FactoryUtilService.getLongFromJson("createUser", jsonMixinType);
        Long lastUpdateUser = FactoryUtilService.getLongFromJson("lastUpdateUser", jsonMixinType);
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

        MixinDefinitionDTO propertyType = new MixinDefinitionDTO();
        propertyType.setId(id);
        propertyType.setName(name);
        propertyType.setNamespace(namespace);
        propertyType.setPropertyDefinitions(definitions);
        propertyType.setAbstract(abstrakt);
        propertyType.setQueryable(queryable);
        propertyType.setMixin(mixin);

        propertyType.setCreateDate(createDate != null ? formatter.parse(createDate) : new Date());
        propertyType.setLastUpdate(lastUpdate != null ? formatter.parse(lastUpdate) : new Date());
        propertyType.setCreateUser(createUser);
        propertyType.setLastUpdateUser(lastUpdateUser);

        return propertyType;
    }

    public static JsonObject convertToJsonObject(MixinDefinitionDTO propertyType) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        propertyType.getPropertyDefinitions().forEach(arrayBuilder::add);

        FactoryUtilService.addValueLong(builder, "id", propertyType.getId());
        FactoryUtilService.addValue(builder, "name", propertyType.getName());
        FactoryUtilService.addValue(builder, "namespace", propertyType.getNamespace());
        FactoryUtilService.addValue(builder, "propertyDefinitions", arrayBuilder.build());
        FactoryUtilService.addValueBoolean(builder, "abstract", propertyType.isAbstract());
        FactoryUtilService.addValueBoolean(builder, "queryable", propertyType.isQueryable());
        FactoryUtilService.addValueBoolean(builder, "mixin", propertyType.isMixin());
        FactoryUtilService.addValue(builder, "createDate", propertyType.getCreateDate());
        FactoryUtilService.addValue(builder, "lastUpdate", propertyType.getLastUpdate());
        FactoryUtilService.addValueLong(builder, "createUser", propertyType.getCreateUser());
        FactoryUtilService.addValueLong(builder, "lastUpdateUser", propertyType.getLastUpdateUser());

        return builder.build();
    }

    public static Page<MixinDefinitionDTO> convertJsonToPage(JsonObject jsonObject) throws ParseException {
        int currentPage = FactoryUtilService.getIntFromJson("currentPage", jsonObject);
        JsonArray results = FactoryUtilService.getArrayFromJson("results", jsonObject);
        int totalPages = FactoryUtilService.getIntFromJson("totalPages", jsonObject);
        int totalResults = FactoryUtilService.getIntFromJson("totalResults", jsonObject);

        ArrayList<MixinDefinitionDTO> pageResults = new ArrayList<>();

        if(results != null){
            for(int i = 0;i<results.size();i++){
                pageResults.add(convert(results.getJsonObject(i)));
            }
        }
        return new Page<>(pageResults,currentPage,totalResults,totalPages);
    }

    public static List<MixinDefinitionDTO> convert(JsonArray jsonArray) throws ParseException {
        List<MixinDefinitionDTO> list = new ArrayList<>();
        for (JsonValue i : jsonArray) {
            MixinDefinitionDTO convert = convert(i.asJsonObject());
            list.add(convert);
        }
        return list;
    }
}
