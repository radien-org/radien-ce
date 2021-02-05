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
package io.radien.ms.contractmanagement.providers;

import io.radien.ms.contractmanagement.client.entities.Contract;
import io.radien.ms.contractmanagement.client.services.ContractFactory;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;


public class ContractListMessageBodyWriterTest extends TestCase {

    @Test
    public void testIsWriteable() {
        ContractListMessageBodyWriter target = new ContractListMessageBodyWriter();
        assertTrue(target.isWriteable(null,null,null,null));
    }

    @Test
    public void testGetSize() {
        ContractListMessageBodyWriter target = new ContractListMessageBodyWriter();
        assertEquals(0,target.getSize(null,null,null,null,null));
    }

    @Test
    public void testWriteTo() throws IOException {

        String example = "[{" +
                "\"id\":null," +
                "\"name\":\"AB\"," +
                "\"start\":\"2021-01-22T13:59:17.468\"," +
                "\"end\":\"2021-01-22T13:59:17.555\"," +
                "\"createUser\":2," +
                "\"lastUpdateUser\":2," +
                "\"createDate\":null," +
                "\"lastUpdate\":null" +
                "}]";
        ContractListMessageBodyWriter target = new ContractListMessageBodyWriter();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Contract u = ContractFactory.create("AB", LocalDateTime.parse("2021-01-22T13:59:17.468"),LocalDateTime.parse("2021-01-22T13:59:17.555"),2L);
        u.setCreateDate(null);
        u.setLastUpdate(null);
        target.writeTo(Collections.singletonList(u),null,null,null,null,null,baos);
        assertEquals(example,baos.toString());
    }
}