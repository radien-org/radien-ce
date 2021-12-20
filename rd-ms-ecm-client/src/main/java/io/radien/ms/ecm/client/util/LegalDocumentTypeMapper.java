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
package io.radien.ms.ecm.client.util;

import io.radien.api.entity.Page;
import io.radien.ms.ecm.client.entities.legal.LegalDocumentType;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;

import static io.radien.api.SystemVariables.ID;
import static io.radien.api.SystemVariables.NAME;
import static io.radien.api.SystemVariables.PAGE_CURRENT;
import static io.radien.api.SystemVariables.PAGE_RESULTS;
import static io.radien.api.SystemVariables.PAGE_TOTALS;
import static io.radien.api.SystemVariables.PAGE_TOTAL_RESULTS;
import static io.radien.api.SystemVariables.TENANT_ID;
import static io.radien.api.SystemVariables.TO_BE_ACCEPTED;
import static io.radien.api.SystemVariables.TO_BE_SHOWN;
import static io.radien.api.util.FactoryUtilService.addValue;
import static io.radien.api.util.FactoryUtilService.getArrayFromJson;
import static io.radien.api.util.FactoryUtilService.getBooleanFromJson;
import static io.radien.api.util.FactoryUtilService.getIntFromJson;
import static io.radien.api.util.FactoryUtilService.getLongFromJson;
import static io.radien.api.util.FactoryUtilService.getStringFromJson;

/**
 * LegalDocumentType Property Object Mapper and converter
 *
 * @author Newton Carvalho
 */
public class LegalDocumentTypeMapper {

    /**
     * Hidden private default constructor to attend Sonarqube rule
     */
    private LegalDocumentTypeMapper() {

    }

    /**
     * Converts a LegalDocumentType property into a Json Object
     * @param model to be converted
     * @return a json object filled with the given I18N keys and values
     */
    public static JsonObject map(LegalDocumentType model) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        addValue(builder, ID.getFieldName(), model.getId());
        addValue(builder, NAME.getFieldName(), model.getName());
        addValue(builder, TENANT_ID.getFieldName(), model.getTenantId());
        addValue(builder, TO_BE_ACCEPTED.getFieldName(), model.isToBeAccepted());
        addValue(builder, TO_BE_SHOWN.getFieldName(), model.isToBeShown());
        return builder.build();
    }

    /**
     * Maps a list of LegalDocumentType properties into an json array
     * @param models list of LegalDocumentType to be converted
     * @return json array of the given information
     */
    public static JsonArray map(List<LegalDocumentType> models) {
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        models.forEach(model -> {
            JsonObject jsonObject = map(model);
            arrayBuilder.add(jsonObject);
        });
        return arrayBuilder.build();
    }

    /**
     * Converts a given Input Stream into a LegalDocumentType object
     * @param is to be converted
     * @return a LegalDocumentType object with the given information
     */
    public static LegalDocumentType map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            return map(jsonReader.readObject());
        }
    }

    /**
     * Converts a given JsonValue into a LegalDocumentType object
     * @param jsonValue to be converted
     * @return a LegalDocumentType object with the given information
     */
    public static LegalDocumentType map(JsonValue jsonValue) {
        return map(jsonValue.asJsonObject());
    }

    /**
     * Converts a given JsonObject into a LegalDocumentType object
     * @param jsonObject to be converted
     * @return a LegalDocumentType object with the given information
     */
    public static LegalDocumentType map(JsonObject jsonObject) {
        LegalDocumentType model = new LegalDocumentType();
        model.setId(getLongFromJson(ID.getFieldName(), jsonObject));
        model.setName(getStringFromJson(NAME.getFieldName(), jsonObject));
        model.setTenantId(getLongFromJson(TENANT_ID.getFieldName(), jsonObject));
        model.setToBeAccepted(getBooleanFromJson(
                TO_BE_ACCEPTED.getFieldName(), jsonObject));
        model.setToBeShown(getBooleanFromJson(
                TO_BE_SHOWN.getFieldName(), jsonObject));
        return model;
    }

    /**
     * Obtains a LegalDocumentType Page from a Json input stream
     * @param is to be mapped
     * @return a page of legal document type mapped from the input stream
     */
    public static Page<LegalDocumentType> mapToPage(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            return mapToPage(jsonReader.readObject());
        }
    }

    /**
     * Converts a JsonObject into a LegalDocumentType Page object
     * @param page the JsonObject to convert
     * @return the Page encapsulating information regarding legal document types
     */
    public static Page<LegalDocumentType> mapToPage(JsonObject page) {
        int currentPage = getIntFromJson(PAGE_CURRENT.getFieldName(), page);
        JsonArray results = getArrayFromJson(PAGE_RESULTS.getFieldName(), page);
        int totalPages = getIntFromJson(PAGE_TOTALS.getFieldName(), page);
        int totalResults = getIntFromJson(PAGE_TOTAL_RESULTS.getFieldName(), page);
        List<LegalDocumentType> pageResults = new ArrayList<>();
        if(results != null){
            for(int i = 0;i<results.size();i++){
                pageResults.add(map(results.getJsonObject(i)));
            }
        }
        return new Page<>(pageResults,currentPage,totalResults,totalPages);
    }

    /**
     * Obtains a LegalDocumentType Page from a Json input stream
     * @param is to be mapped
     * @return a page of legal document type mapped from the input stream
     */
    public static List<LegalDocumentType> mapToList(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonArray jsonArray = jsonReader.readArray();
            return jsonArray.stream().map(LegalDocumentTypeMapper::map).
                    collect(Collectors.toList());
        }
    }
}
