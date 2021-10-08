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
package io.radien.ms.tenantmanagement.client.providers;

import io.radien.ms.tenantmanagement.client.entities.Contract;
import junit.framework.TestCase;
import org.junit.Test;

import javax.ws.rs.WebApplicationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ContractMessageBodyReaderTest extends TestCase {

    @Test
    public void testIsReadable() {
        ContractMessageBodyReader target = new ContractMessageBodyReader();
        assertTrue(target.isReadable(Contract.class,null,null,null));
    }

    @Test
    public void testReadFrom() throws IOException {
        String result = "{\"" +
                "id\":null," +
                "\"name\":\"a\"," +
                "\"start\":null," +
                "\"end\":null" +
                "}";
        ContractMessageBodyReader target = new ContractMessageBodyReader();
        InputStream in = new ByteArrayInputStream(result.getBytes());
        Contract contract = target.readFrom(null,null,null,null,null, in);
        assertNull(contract.getId());
        assertEquals("a", contract.getName());
        assertNull(contract.getStart());
        assertNull(contract.getEnd());
    }

    @Test
    public void testReadFromParseException() throws IOException {
        boolean success = false;
        String result = "{\"" +
                "id\":null," +
                "\"name\":\"a\"," +
                "\"start\":null," +
                "\"end\":null," +
                "\"lastUpdate\":\"seedOfEvil\"" +
                "}";
        ContractMessageBodyReader target = new ContractMessageBodyReader();
        InputStream in = new ByteArrayInputStream(result.getBytes());
        try {
            Contract contract = target.readFrom(null, null, null, null, null, in);
        } catch (WebApplicationException we){
            success = true;
        }
        assertTrue(success);
    }
}