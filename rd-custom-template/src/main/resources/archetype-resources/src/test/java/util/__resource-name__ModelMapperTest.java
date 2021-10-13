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
package ${package}.util;

import ${client-packageName}.services.${resource-name}Factory;
import ${package}.entities.${resource-name};

import junit.framework.TestCase;
import org.junit.Test;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.ParseException;

/**
 * @author Rajesh Gavvala
 * @author Nuno Santana
 * @author Bruno Gama
 */
public class ${resource-name}ModelMapperTest extends TestCase {

    @Test
    public void testMapInputStream() throws ParseException {
        String example = "{\n" +
                "        \"id\": 30,\n" +
                "        \"message\": \"testMessage\"" +
                "    }";
        InputStream in = new ByteArrayInputStream(example.getBytes());
        ${resource-name} ${resource-name-variable} = ${resource-name}ModelMapper.map(in);
        assertEquals((Long)30L, ${resource-name-variable}.getId());
        assertEquals("testMessage",${resource-name-variable}.getMessage());
    }

    @Test
    public void testMapJsonObject() {
        ${resource-name} ${resource-name-variable} = new ${resource-name}(${resource-name}Factory.create("testMessage"));
        JsonObject jsonObject = ${resource-name}ModelMapper.map(${resource-name-variable});

        assertEquals(${resource-name-variable}.getMessage(),jsonObject.getString("message"));
    }

}
