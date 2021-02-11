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


import io.radien.ms.tenantmanagement.client.services.ContractFactory;
import io.radien.ms.tenantmanagement.entities.Contract;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;

public class ContractMessageBodyWriterTest extends TestCase {

    @Test
    public void testIsWriteable() {
        ContractMessageBodyWriter target = new ContractMessageBodyWriter();
        assertTrue(target.isWriteable(Contract.class,null,null,null));
    }

    @Test
    public void testGetSize() {
        ContractMessageBodyWriter target = new ContractMessageBodyWriter();
        assertEquals(0L,target.getSize(null,null,null,null,null));
    }

    @Test
    public void testWriteTo() throws IOException {
        String result = "{" +
                "\"id\":3," +
                "\"name\":\"AB\"," +
                "\"start\":\"2021-01-22T13:59:17.468\"," +
                "\"end\":\"2021-01-22T13:59:17.555\"," +
                "\"createUser\":2," +
                "\"lastUpdateUser\":2," +
                "\"createDate\":null," +
                "\"lastUpdate\":null" +
                "}";

        ContractMessageBodyWriter target = new ContractMessageBodyWriter();
        Contract contract = new Contract(ContractFactory.create("AB", LocalDateTime.parse("2021-01-22T13:59:17.468"),LocalDateTime.parse("2021-01-22T13:59:17.555"),2L));
        contract.setId(3L);
        contract.setCreateDate(null);
        contract.setLastUpdate(null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        target.writeTo(contract,null,null,null, null,null, baos);

        assertEquals(result,baos.toString());
    }
}