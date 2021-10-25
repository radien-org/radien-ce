/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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
package io.rd.microservice.ms.service.entities;

import io.rd.microservice.ms.service.legacy.MicroserviceFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MicroserviceTest {
    Microservice microservice;

    @Before
    public void before(){
        microservice = MicroserviceFactory.create("TestName");
        microservice.setId(1L);

        new Microservice((io.rd.microservice.ms.client.entities.Microservice) microservice);
    }

    @Test
    public void getID_test(){
        Long id = 1L;
        assertNotNull(microservice.getId());
        assertEquals(id, microservice.getId());
    }

    @Test
    public void getName_test(){
        assertEquals("TestName",microservice.getName());
    }
}
