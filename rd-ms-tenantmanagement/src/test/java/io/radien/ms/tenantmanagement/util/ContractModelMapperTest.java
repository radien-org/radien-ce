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
package io.radien.ms.tenantmanagement.util;

import io.radien.ms.tenantmanagement.client.services.ContractFactory;
import io.radien.ms.tenantmanagement.entities.ContractEntity;
import junit.framework.TestCase;
import org.junit.Test;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Collections;

/**
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class ContractModelMapperTest extends TestCase {

    @Test
    public void testMapInputStream() throws ParseException {
        String example = "{\n" +
                "        \"id\": 3,\n" +
                "        \"name\": \"AB\",\n" +
                "        \"start\": \"2021-01-22T13:59:17.468\",\n" +
                "        \"end\": \"2021-01-22T13:59:17.555\",\n" +
                "        \"createUser\": null,\n" +
                "        \"lastUpdateUser\": null,\n" +
                "        \"createDate\": null,\n" +
                "        \"lastUpdate\": null\n" +
                "    }";
        InputStream in = new ByteArrayInputStream(example.getBytes());
        ContractEntity contract = ContractModelMapper.map(in);
        assertEquals((Long)3L, contract.getId());
        assertEquals("AB",contract.getName());
        assertEquals(LocalDateTime.parse("2021-01-22T13:59:17.468"),contract.getStart());
        assertEquals(LocalDateTime.parse("2021-01-22T13:59:17.555"),contract.getEnd());
        assertNull(contract.getCreateUser());
        assertNull(contract.getLastUpdateUser());
        assertNull(contract.getCreateDate());
        assertNull(contract.getLastUpdate());

    }

    @Test
    public void testMapJsonObject() {

        LocalDateTime time1 = LocalDateTime.now();
        LocalDateTime time2 = LocalDateTime.now();
        ContractEntity contract = new ContractEntity(ContractFactory.create("a", time1,time2,1L));
        JsonObject jsonObject = ContractModelMapper.map(contract);

        assertEquals(contract.getName(),jsonObject.getString("name"));
        assertEquals(contract.getStart().toString(),jsonObject.getString("start"));
        assertEquals(contract.getEnd().toString(),jsonObject.getString("end"));
        assertEquals(contract.getCreateUser().toString(),jsonObject.getJsonNumber("createUser").toString());
        assertEquals(contract.getLastUpdateUser().toString(),jsonObject.getJsonNumber("lastUpdateUser").toString());

    }

    @Test
    public void testMapList() {
        LocalDateTime time1 = LocalDateTime.now();
        LocalDateTime time2 = LocalDateTime.now();
        ContractEntity contract = new ContractEntity(ContractFactory.create("a", time1,time2,1L));
        JsonArray jsonArray = ContractModelMapper.map(Collections.singletonList(contract));
        assertEquals(1,jsonArray.size());
        JsonObject jsonObject = jsonArray.getJsonObject(0);

        assertEquals(contract.getName(),jsonObject.getString("name"));
        assertEquals(contract.getStart().toString(),jsonObject.getString("start"));
        assertEquals(contract.getEnd().toString(),jsonObject.getString("end"));
        assertEquals(contract.getCreateUser().toString(),jsonObject.getJsonNumber("createUser").toString());
        assertEquals(contract.getLastUpdateUser().toString(),jsonObject.getJsonNumber("lastUpdateUser").toString());

    }
}
