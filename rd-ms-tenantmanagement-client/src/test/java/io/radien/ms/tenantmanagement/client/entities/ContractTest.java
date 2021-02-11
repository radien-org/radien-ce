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
package io.radien.ms.tenantmanagement.client.entities;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class ContractTest {

    private Contract contract;
    private LocalDateTime start;
    private LocalDateTime end;
    private String name;

    public ContractTest() {
        contract = new Contract();
        start = LocalDateTime.now();
        end = LocalDateTime.now();
        name = "name";
        contract.setId(2L);
        contract.setName(name);
        contract.setStart(start);
        contract.setEnd(end);
        contract = new Contract(contract);
    }

    @Test
    public void getId() {
        assertSame(2L, contract.getId());
    }

    @Test
    public void setId() {
        contract.setId(3L);
        assertSame(3L, contract.getId());
    }

    @Test
    public void getName() {
        assertEquals("name", contract.getName());
    }

    @Test
    public void setName() {
        contract.setName("testNameSetter");
        assertEquals("testNameSetter", contract.getName());
    }

    @Test
    public void getStart() {
        assertEquals(start, contract.getStart());
    }

    @Test
    public void setStart() {
        LocalDateTime start = LocalDateTime.now();
        contract.setStart(start);
        assertEquals(start, contract.getStart());
    }

    @Test
    public void getEnd() {
        assertEquals(end, contract.getEnd());
    }

    @Test
    public void setEnd() {
        LocalDateTime end = LocalDateTime.now();
        contract.setEnd(end);
        assertEquals(end, contract.getEnd());
    }

}
