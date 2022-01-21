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
package io.radien.ms.ticketmanagement.client.services;

import io.radien.api.SystemVariables;
import io.radien.api.entity.Page;
import io.radien.api.util.FactoryUtilService;
import io.radien.exception.GenericErrorCodeMessage;
import io.radien.ms.ticketmanagement.client.entities.Ticket;
import io.radien.ms.ticketmanagement.client.entities.TicketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonObjectBuilder;
import javax.json.Json;
import javax.json.JsonArray;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

/**
 * Ticket factory for mapping and converting given information
 *
 * @author Rui Soares
 */
public class TicketFactory {

    private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";

    private static Logger log = LoggerFactory.getLogger(TicketFactory.class);

    public static Ticket create(Long userId, String token, Long type,
                                String data, Long createdUser){
        Ticket ticket = new Ticket();
        ticket.setUserId(userId);
        ticket.setToken(token);
        ticket.setTicketType(type);
        ticket.setExpireDate(setExpirationDate(type));
        ticket.setData(data);

        ticket.setCreateUser(createdUser);
        ticket.setLastUpdateUser(createdUser);
        Date now = new Date();
        ticket.setLastUpdate(now);
        ticket.setCreateDate(now);
        return ticket;
    }

    /**
     * Converts a JSONObject to a SystemTicket object Used by the Application
     * DataInit to seed Data in the database
     *
     * @param jsonTicket the JSONObject to convert
     * @return the Ticket Object
     * @throws ParseException in case of any issue while parsing the JSON
     */
    public static Ticket convert(JsonObject jsonTicket) throws ParseException {
        Long id = FactoryUtilService.getLongFromJson(SystemVariables.ID.getFieldName(), jsonTicket);
        Long userId = FactoryUtilService.getLongFromJson("userId", jsonTicket);
        JsonObject ticketType = convertTypeToJson(FactoryUtilService.getStringFromJson("ticketType", jsonTicket));
        Long ticketTypeId = FactoryUtilService.getLongFromJson(SystemVariables.ID.getFieldName(), ticketType);
        String token = FactoryUtilService.getStringFromJson("token", jsonTicket);
        String data = FactoryUtilService.getStringFromJson("data", jsonTicket);


        Long createUser = FactoryUtilService.getLongFromJson("createUser", jsonTicket);
        Long lastUpdateUser = FactoryUtilService.getLongFromJson("lastUpdateUser", jsonTicket);
        String createDate = FactoryUtilService.getStringFromJson("createDate", jsonTicket);
        String lastUpdate = FactoryUtilService.getStringFromJson("lastUpdate", jsonTicket);

        Ticket ticket = create(userId, token, ticketTypeId, data, createUser);

        ticket.setId(id);

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

        ticket.setLastUpdateUser(lastUpdateUser);

        if(createDate != null) {
            ticket.setCreateDate(formatter.parse(createDate));
        } else {
            ticket.setCreateDate(null);
        }

        if(lastUpdate != null) {
            ticket.setLastUpdate(formatter.parse(lastUpdate));
        } else {
            ticket.setLastUpdate(null);
        }

        return ticket;
    }

    private static JsonObject convertTypeToJson(String ticket){
        try(JsonReader jsonReader = Json.createReader(new StringReader(ticket))){
            return jsonReader.readObject();
        }
    }

    /**
     * Extract and obtains SystemTicketType from Json
     * @param typeId information to be set
     * @return a correct ticket type
     */
    private static TicketType getTypeFromId(Long typeId) {
        TicketType ticketType = TicketType.getById(typeId);
        if (ticketType == null) {
            String ticketTypeNotFound = GenericErrorCodeMessage.TICKET_TYPE_NOT_FOUND.toString();
            log.error(ticketTypeNotFound, typeId);
        }
        if (ticketType == null) {
            throw new IllegalStateException(GenericErrorCodeMessage.TICKET_TYPE_NOT_FOUND.toString());
        }
        return ticketType;
    }

    private static LocalDate setExpirationDate(Long typeId){
        TicketType ticketType = getTypeFromId(typeId);

        return LocalDate.now().plusDays(ticketType.getExpirationPeriod());
    }

    /**
     * Converts a System user to a Json Object
     *
     * @param ticket system user to be converted to json
     * @return json object with keys and values constructed
     */
    public static JsonObject convertToJsonObject(Ticket ticket) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        FactoryUtilService.addValueLong(builder, "id", ticket.getId());
        FactoryUtilService.addValueLong(builder, "userId", ticket.getUserId());
        FactoryUtilService.addValue(builder, "token", ticket.getToken());

        TicketType ticketType = TicketType.getById(ticket.getTicketType());
        JsonObjectBuilder typeBuilder = Json.createObjectBuilder();

        if(ticketType!=null){
            FactoryUtilService.addValueLong(typeBuilder, "id", ticketType.getId());
            FactoryUtilService.addValue(typeBuilder, "type", ticketType.getType());

        }

        FactoryUtilService.addValue(builder, "ticketType", typeBuilder.build());
        FactoryUtilService.addValue(builder, "expireDate", ticket.getExpireDate());
        FactoryUtilService.addValueLong(builder, "createUser", ticket.getCreateUser());
        FactoryUtilService.addValueLong(builder, "lastUpdateUser", ticket.getLastUpdateUser());
        FactoryUtilService.addValue(builder, "createDate", ticket.getCreateDate());
        FactoryUtilService.addValue(builder, "lastUpdate", ticket.getLastUpdate());

        return  builder.build();
    }

    /**
     * Converts a JsonObject into a Permission Page object
     * @param page the JsonObject to convert
     * @return the Page encapsulating information regarding permissions
     * @throws ParseException in case of any issue while parsing the JSON
     */
    public static Page<Ticket> convertJsonToPage(JsonObject page) throws ParseException {
        int currentPage = io.radien.api.util.FactoryUtilService.getIntFromJson("page", page);
        JsonArray results = io.radien.api.util.FactoryUtilService.getArrayFromJson("results", page);
        int totalPages = io.radien.api.util.FactoryUtilService.getIntFromJson("totalPages", page);
        int totalResults = io.radien.api.util.FactoryUtilService.getIntFromJson("totalResults", page);

        ArrayList<Ticket> pageResults = new ArrayList<>();

        if(results != null){
            for(int i = 0;i<results.size();i++){
                pageResults.add(convert(results.getJsonObject(i)));
            }
        }
        return new Page<>(pageResults,currentPage,totalResults,totalPages);
    }

}
