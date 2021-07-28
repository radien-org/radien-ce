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
package io.radien.ms.tenantmanagement.providers;

import io.radien.api.model.tenant.SystemContract;
import io.radien.ms.tenantmanagement.entities.ContractEntity;
import junit.framework.TestCase;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

public class SystemContractMessageBodyReaderTest extends TestCase {

    @Test
    public void testIsReadable() {
        SystemContractMessageBodyReader target = new SystemContractMessageBodyReader();
        assertTrue(target.isReadable(ContractEntity.class,null,null,null));
    }

    @Test
    public void testReadFrom() throws IOException {
        String read = "{\n" +
                "        \"id\": 3,\n" +
                "        \"name\": \"AB\",\n" +
                "        \"start\": \"2021-01-22T13:59:17.468\",\n" +
                "        \"end\": \"2021-01-22T13:59:17.555\",\n" +
                "        \"createUser\": null,\n" +
                "        \"lastUpdateUser\": null,\n" +
                "        \"createDate\": null,\n" +
                "        \"lastUpdate\": null\n" +
                "    }";
        SystemContractMessageBodyReader target = new SystemContractMessageBodyReader();
        InputStream inputStream = new ByteArrayInputStream(read.getBytes());
        SystemContract contract =target.readFrom(null,null,null,null,null, inputStream);
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
    public void testReadFromParseException() throws IOException {
        boolean success = false;
        String read = "{\n" +
                "        \"id\": 3,\n" +
                "        \"name\": \"AB\",\n" +
                "        \"start\": \"2021-01-22T13:59:17.468\",\n" +
                "        \"end\": \"2021-01-22T13:59:17.555\",\n" +
                "        \"createUser\": null,\n" +
                "        \"lastUpdateUser\": null,\n" +
                "        \"createDate\": \"a\",\n" +
                "        \"lastUpdate\": \"b\"\n" +
                "    }";

        SystemContractMessageBodyReader target = new SystemContractMessageBodyReader();
        InputStream inputStream = new ByteArrayInputStream(read.getBytes());
        try {
            SystemContract contract = target.readFrom(null, null, null, null, null, inputStream);
        } catch (WebApplicationException e){
            success = true;
        }
        assertTrue(success);

    }
}