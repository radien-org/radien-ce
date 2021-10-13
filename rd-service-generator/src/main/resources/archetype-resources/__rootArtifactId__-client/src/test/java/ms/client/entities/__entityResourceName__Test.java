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
package ${package}.ms.client.entities;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ${entityResourceName}Test {

    ${entityResourceName} ${entityResourceName.toLowerCase()};

    @Before
    public void before(){
        ${entityResourceName.toLowerCase()} = new ${entityResourceName}();
        ${entityResourceName.toLowerCase()}.setId(1L);
        ${entityResourceName.toLowerCase()}.setName("name-1");

        new ${entityResourceName}(${entityResourceName.toLowerCase()});
    }

    @Test
    public void getID_test() {
        Long id = 1L;
        assertEquals(id, ${entityResourceName.toLowerCase()}.getId());
    }

    @Test
    public void setID_test() {
        Long id = 2L;
        ${entityResourceName.toLowerCase()}.setId(id);
        assertEquals(id, ${entityResourceName.toLowerCase()}.getId());
    }

    @Test
    public void getName_test() {
        String name = "name-1";
        assertEquals(name, ${entityResourceName.toLowerCase()}.getName());
    }

    @Test
    public void setName_test() {
        String name = "name-2";
        ${entityResourceName.toLowerCase()}.setName(name);
        assertEquals(name, ${entityResourceName.toLowerCase()}.getName());
    }
}
