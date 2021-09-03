/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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
import io.radien.ms.tenantmanagement.client.services.ContractFactory;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class ContractMessageBodyWriterTest extends TestCase {

    private final static String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";

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
        String result = "{\"" +
                "id\":null," +
                "\"name\":\"a\"," +
                "\"start\":\"%s\"," +
                "\"end\":\"%s\"," +
                "\"createUser\":1," +
                "\"lastUpdateUser\":1," +
                "\"createDate\":\"%s\"," +
                "\"lastUpdate\":\"%s\"" +
                "}";
        LocalDateTime time1 = LocalDateTime.now();
        ContractMessageBodyWriter target = new ContractMessageBodyWriter();
        LocalDateTime time2 = LocalDateTime.now();
        Contract contract = ContractFactory.create("a", time1,time2,1L);
        Date d1 = Calendar.getInstance().getTime();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Date d2 = Calendar.getInstance().getTime();
        contract.setCreateDate(d1);
        contract.setLastUpdate(d2);
        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
        result = String.format(result,time1,time2,df.format(d1),df.format(d2));
        target.writeTo(contract,null,null,null, null,null, baos);

        assertEquals(result,baos.toString());
    }
}