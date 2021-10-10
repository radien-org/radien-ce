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
package io.radien.ms.tenantmanagement.client.util;

import io.radien.api.entity.Page;
import io.radien.ms.tenantmanagement.client.entities.Contract;
import io.radien.ms.tenantmanagement.client.services.ContractFactory;
import junit.framework.TestCase;
import org.junit.Test;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Collections;

public class ContractModelMapperTest extends TestCase {

    @Test
    public void testMapInputStream() throws ParseException {
        String example = "{\n" +
                "\"name\": \"a\",\n" +
                "\"start\": \"2021-01-29T13:10:19.396\",\n" +
                "\"end\": \"2021-01-29T13:10:20.396\"\n" +
                "}";
        InputStream in = new ByteArrayInputStream(example.getBytes());
        Contract contract = ContractModelMapper.map(in);
        assertEquals("a", contract.getName());
        assertEquals(LocalDateTime.parse("2021-01-29T13:10:19.396"),contract.getStart());
    }

    @Test
    public void testMapJsonObject() {
        String name = "aa";
        LocalDateTime start  = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        Contract contract = ContractFactory.create(name,start,end,1L);
        JsonObject jsonObject = ContractModelMapper.map(contract);
        validateContractJsonObject(contract,jsonObject);
    }

    @Test
    public void testMapList() {
        String name = "aa";
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        Contract contract = ContractFactory.create(name,start,end,1L);
        JsonArray jsonArray = ContractModelMapper.map(Collections.singletonList(contract));
        assertEquals(1,jsonArray.size());
        JsonObject jsonObject = jsonArray.getJsonObject(0);
        validateContractJsonObject(contract,jsonObject);
    }

    private void validateContractJsonObject(Contract contract, JsonObject jsonObject){
        assertEquals(contract.getName(),jsonObject.getString("name"));
        assertEquals(contract.getStart().toString(),jsonObject.getString("start"));
        assertEquals(contract.getEnd().toString(),jsonObject.getString("end"));
    }

    @Test
    public void testMapInputStreamToPage() {
        String example = "{\n" +
                "\"currentPage\": 0,\n" +
                "\"results\": [\n" +
                "{\n" +
                "\"name\": \"a\",\n" +
                "\"start\": \"2021-01-29T13:10:19.396\",\n" +
                "\"end\": \"2021-01-29T13:10:20.396\"\n" +
                "}\n" +
                "],\n" +
                "\"totalPages\": 1,\n" +
                "\"totalResults\": 4\n" +
                "}";
        InputStream in = new ByteArrayInputStream(example.getBytes());
        Page<Contract> contract = ContractModelMapper.mapToPage(in);
        assertEquals(0, contract.getCurrentPage());
        assertEquals(1, contract.getTotalPages());
        assertEquals(4, contract.getTotalResults());
    }

    @Test
    public void testMapInputStreamToPageParseException() {
        String example = "{\n" +
                "\"currentPage\": 0,\n" +
                "\"results\": [\n" +
                "{\n" +
                "\"name\": \"a\",\n" +
                "\"createDate\": \"a\",\n" +
                "\"start\": \"2021-01-29T13:10:19.396\",\n" +
                "\"end\": \"2021-01-29T13:10:20.396\"\n" +
                "}\n" +
                "],\n" +
                "\"totalPages\": 1,\n" +
                "\"totalResults\": 4\n" +
                "}";
        InputStream in = new ByteArrayInputStream(example.getBytes());
        Page<Contract> contract = ContractModelMapper.mapToPage(in);
        assertNull(contract);
    }
}