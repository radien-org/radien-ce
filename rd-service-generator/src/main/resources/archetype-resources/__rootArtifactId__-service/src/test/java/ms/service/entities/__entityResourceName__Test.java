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
package ${package}.ms.service.entities;

import ${package}.ms.service.legacy.${entityResourceName}Factory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ${entityResourceName}Test {
    ${entityResourceName} ${entityResourceName.toLowerCase()};

    @Before
    public void before(){
        ${entityResourceName.toLowerCase()} = ${entityResourceName}Factory.create("TestName");
        ${entityResourceName.toLowerCase()}.setId(1L);

        new ${entityResourceName}((io.rd.ms.client.entities.${entityResourceName}) ${entityResourceName.toLowerCase()});
    }

    @Test
    public void getID_test(){
        Long id = 1L;
        assertNotNull(${entityResourceName.toLowerCase()}.getId());
        assertEquals(id, ${entityResourceName.toLowerCase()}.getId());
    }

    @Test
    public void getName_test(){
        assertEquals("TestName",${entityResourceName.toLowerCase()}.getName());
    }
}
