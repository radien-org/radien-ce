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
package io.radien.ms.tenantmanagement.client.services;

import io.radien.ms.tenantmanagement.client.entities.Contract;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import java.text.ParseException;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class ContractFactoryTest {

    Contract contract;
    JsonObject json;

    /**
     * Constructor class method were we are going to create the JSON and the user for
     * testing purposes.
     */
    public ContractFactoryTest() {

    }

    /**
     * Test method to validate the conversion of a User using a Json
     */
    @Test
    public void convert() throws ParseException {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.addNull("id");
        builder.add("name", "aa");
        builder.add("createUser", 2L);
        builder.add("lastUpdateUser", 2L);

        LocalDateTime time = LocalDateTime.now();
        builder.add("start",time.toString());
        builder.add("end",time.toString());

        json = builder.build();


        contract = ContractFactory.create("aa", time,time,2L);

        Contract convertedContract = ContractFactory.convert(json);

        assertEquals(contract.getId(), convertedContract.getId());
        assertEquals(contract.getName(), convertedContract.getName());
        assertEquals(contract.getStart(), convertedContract.getStart());
        assertEquals(contract.getCreateUser(), convertedContract.getCreateUser());
        assertEquals(contract.getLastUpdateUser(), convertedContract.getLastUpdateUser());
    }


}
