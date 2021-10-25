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
package ${package}.entities;

import ${client-packageName}.services.${resource-name}Factory;
import junit.framework.TestCase;
import org.junit.Test;


/**
 *
 * @author Rajesh Gavvala
 */

public class ${resource-name}Test extends TestCase {

    ${resource-name} ${resource-name-variable};

    public ${resource-name}Test() {
        ${resource-name-variable} = new ${resource-name}(${resource-name}Factory.create("testMessage"));
        ${resource-name-variable}.setId(1L);
    }

    @Test
    public void testGetId() {
        assertNotNull(${resource-name-variable}.getId());
        assertEquals((Long) 1L, ${resource-name-variable}.getId());
    }

    @Test
    public void testSetId() {
        ${resource-name-variable}.setId(2L);
        assertNotNull(${resource-name-variable}.getId());
        assertEquals((Long) 2L, ${resource-name-variable}.getId());
    }

    @Test
    public void testGetMessage() {
        assertNotNull(${resource-name-variable}.getMessage());
        assertEquals("testMessage", ${resource-name-variable}.getMessage());
    }

    @Test
    public void testSetMessage() {
        ${resource-name-variable}.setMessage("testMessage");
        assertEquals("testMessage",${resource-name-variable}.getMessage());
    }
}