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
package io.radien.ms.tenantmanagement.entities;


import io.radien.ms.tenantmanagement.client.services.ContractFactory;
import junit.framework.TestCase;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Date;

public class ContractTest extends TestCase {

    ContractEntity contract;
    private final Date lastUpdate = new Date();
    private final LocalDateTime start = LocalDateTime.now();
    private final LocalDateTime end = LocalDateTime.now();

    public ContractTest() {
        contract = new ContractEntity(ContractFactory.create("name",start,end,1L));
        contract.setId(2L);
        contract.setLastUpdate(lastUpdate);
    }

    @Test
    public void testGetId() {
        assertNotNull(contract.getId());
        assertEquals((Long) 2L, contract.getId());
    }

    @Test
    public void testSetId() {
        contract.setId(4L);

        assertNotNull(contract.getId());
        assertEquals((Long) 4L, contract.getId());
    }

    @Test
    public void testGetStart() {
        assertNotNull(contract.getStart());
        assertEquals(start, contract.getStart());
    }

    @Test
    public void testSetStart() {
        LocalDateTime test = LocalDateTime.now();
        contract.setStart(test);

        assertNotNull(contract.getStart());
        assertEquals(test, contract.getStart());
    }

    @Test
    public void testGetEnd() {
        assertNotNull(contract.getEnd());
        assertEquals(end, contract.getEnd());
    }

    @Test
    public void testSetEnd() {
        LocalDateTime test = LocalDateTime.now();
        contract.setEnd(test);

        assertNotNull(contract.getEnd());
        assertEquals(test, contract.getEnd());
    }

    @Test
    public void testGetName() {
        assertNotNull(contract.getName());
        assertEquals("name", contract.getName());
    }

    @Test
    public void testSetName() {
        contract.setName("test2");
        assertEquals("test2",contract.getName());
    }
}