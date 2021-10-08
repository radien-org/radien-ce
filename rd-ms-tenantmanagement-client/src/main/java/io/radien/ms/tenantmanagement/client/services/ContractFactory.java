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
package io.radien.ms.tenantmanagement.client.services;

import io.radien.api.entity.Page;
import io.radien.api.util.FactoryUtilService;
import io.radien.ms.tenantmanagement.client.entities.Contract;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Contract factory for mapping converting and creating contract objects
 *
 * @author Bruno Gama
 */
public class ContractFactory {

    private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";

    /**
     * Create a Contract with already predefine fields.
     *
     * @param name contract name
     * @param start begin of contract
     * @param end end of contract
     * @param createdUser the user which has created the contract
     * @return a Contract object to be used
     */
    public static Contract create(String name, LocalDateTime start, LocalDateTime end, Long createdUser){
        Contract contract = new Contract();
        contract.setName(name);
        contract.setStart(start);
        contract.setEnd(end);

        contract.setCreateUser(createdUser);
        contract.setLastUpdateUser(createdUser);
        Date now = new Date();
        contract.setLastUpdate(now);
        contract.setCreateDate(now);
        return contract;
    }

    /**
     * Converts a JSONObject to a SystemUser object Used by the Application
     * DataInit to seed Data in the database
     *
     * @param jsonContract the JSONObject to convert
     * @return the Contract Object
     */
    public static Contract convert(JsonObject jsonContract) throws ParseException {
        Long id = FactoryUtilService.getLongFromJson("id", jsonContract);
        String name = FactoryUtilService.getStringFromJson("name", jsonContract);
        LocalDateTime start = FactoryUtilService.getLocalDateTimeFromJson("start", jsonContract);
        LocalDateTime end = FactoryUtilService.getLocalDateTimeFromJson("end", jsonContract);
        Long createUser = FactoryUtilService.getLongFromJson("createUser", jsonContract);
        Long lastUpdateUser = FactoryUtilService.getLongFromJson("lastUpdateUser", jsonContract);
        String createDate = FactoryUtilService.getStringFromJson("createDate", jsonContract);
        String lastUpdate = FactoryUtilService.getStringFromJson("lastUpdate", jsonContract);
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        Contract contract = create(name,start,end,createUser);
        contract.setId(id);
        contract.setLastUpdateUser(lastUpdateUser);
        if(createDate != null) {
            contract.setCreateDate(formatter.parse(createDate));
        } else {
          contract.setCreateDate(null);
        }
        if(lastUpdate != null) {
            contract.setLastUpdate(formatter.parse(lastUpdate));
        } else {
            contract.setLastUpdate(null);
        }
        return contract;
    }

    /**
     * Converts a System user to a Json Object
     *
     * @param contract system user to be converted to json
     * @return json object with keys and values constructed
     */
    public static JsonObject convertToJsonObject(Contract contract) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        FactoryUtilService.addValueLong(builder, "id", contract.getId());
        FactoryUtilService.addValue(builder, "name", contract.getName());
        FactoryUtilService.addValue(builder, "start", contract.getStart());
        FactoryUtilService.addValue(builder, "end", contract.getEnd());
        FactoryUtilService.addValueLong(builder, "createUser", contract.getCreateUser());
        FactoryUtilService.addValueLong(builder, "lastUpdateUser", contract.getLastUpdateUser());
        FactoryUtilService.addValue(builder, "createDate", contract.getCreateDate());
        FactoryUtilService.addValue(builder, "lastUpdate", contract.getLastUpdate());

        return  builder.build();
    }

    /**
     * Contract method to convert a given json array into a list of contracts
     * @param jsonArray to be mapped and converted
     * @return a list of the mapped information with all the given contracts
     * @throws ParseException in case of issue while parsing the json array
     */
    public static List<Contract> convert(JsonArray jsonArray) throws ParseException{
        List<Contract> list = new ArrayList<>();
        for (JsonValue i : jsonArray) {
            Contract convert = convert(i.asJsonObject());
            list.add(convert);
        }
        return list;
    }

    /**
     * Converts a JsonObject into a Permission Page object
     * @param page the JsonObject to convert
     * @return the Page encapsulating information regarding permissions
     */
    public static Page<Contract> convertJsonToPage(JsonObject page) throws ParseException {
        int currentPage = FactoryUtilService.getIntFromJson("currentPage", page);
        JsonArray results = FactoryUtilService.getArrayFromJson("results", page);
        int totalPages = FactoryUtilService.getIntFromJson("totalPages", page);
        int totalResults = FactoryUtilService.getIntFromJson("totalResults", page);

        ArrayList<Contract> pageResults = new ArrayList<>();

        if(results != null){
            for(int i = 0;i<results.size();i++){
                pageResults.add(convert(results.getJsonObject(i)));
            }
        }
        return new Page<>(pageResults,currentPage,totalResults,totalPages);
    }
}
