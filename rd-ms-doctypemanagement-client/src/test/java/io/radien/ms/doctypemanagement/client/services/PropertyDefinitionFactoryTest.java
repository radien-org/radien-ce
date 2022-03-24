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

package io.radien.ms.doctypemanagement.client.services;

import io.radien.api.entity.Page;
import io.radien.api.util.FactoryUtilService;
import io.radien.ms.doctypemanagement.client.entities.PropertyDefinition;
import java.text.ParseException;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonString;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PropertyDefinitionFactoryTest {
    private static final Long ID = 1L;
    private static final String NAME = "name";
    private static final boolean MANDATORY = false;
    private static final boolean PROTEKTED = true;
    private static final int REQUIREDTYPE = 1;
    private static final boolean MULTIPLE = false;

    private static final Long CREATEUSER = 1L;


    @Test
    public void testConvert() throws ParseException {
        PropertyDefinition result = PropertyDefinitionFactory.convert(createObject());
        assertEquals(ID, result.getId());
        assertEquals(NAME, result.getName());
        assertEquals(MANDATORY, result.isMandatory());
        assertEquals(PROTEKTED, result.isProtected());
        assertEquals(REQUIREDTYPE, result.getRequiredType());
        assertEquals(MULTIPLE, result.isMultiple());
        assertEquals(CREATEUSER, result.getCreateUser());
    }

    @Test
    public void testConvertToJsonObject() {
        PropertyDefinition object = new PropertyDefinition();
        object.setId(ID);
        object.setName(NAME);
        object.setMandatory(MANDATORY);
        object.setProtekted(PROTEKTED);
        object.setRequiredType(REQUIREDTYPE);
        object.setMultiple(MULTIPLE);
        object.setCreateUser(CREATEUSER);

        JsonObject result = PropertyDefinitionFactory.convertToJsonObject(object);
        assertEquals(ID, Long.valueOf(result.get("id").toString()));
        assertEquals(NAME, ((JsonString) result.get("name")).getString());
        assertEquals(MANDATORY, Boolean.parseBoolean(result.get("mandatory").toString()));
        assertEquals(PROTEKTED, Boolean.parseBoolean(result.get("protected").toString()));
        assertEquals(REQUIREDTYPE, Integer.parseInt(result.get("requiredType").toString()));
        assertEquals(MULTIPLE, Boolean.parseBoolean(result.get("multiple").toString()));
        assertEquals(CREATEUSER, Long.valueOf(result.get("createUser").toString()));
        assertNotNull(result.get("createDate"));
    }

    @Test
    public void testConvertJsonToPage() throws ParseException {
        JsonObject propertyObj = createObject();
        JsonObjectBuilder pageObj = Json.createObjectBuilder();
        pageObj.add("currentPage", 1);
        pageObj.add("totalPages", 1);
        pageObj.add("totalResults", 1);
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        arrayBuilder.add(propertyObj);
        pageObj.add("results", arrayBuilder.build());

        Page<PropertyDefinition> result = PropertyDefinitionFactory.convertJsonToPage(pageObj.build());
        assertEquals(1, result.getCurrentPage());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getTotalResults());
    }

    @Test
    public void testConvertArray() throws ParseException {
        JsonObject propertyObj = createObject();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        arrayBuilder.add(propertyObj);
        arrayBuilder.add(propertyObj);

        List<PropertyDefinition> resultList = PropertyDefinitionFactory.convert(arrayBuilder.build());
        assertEquals(2, resultList.size());
    }

    private JsonObject createObject() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValueLong(builder, "id", ID);
        FactoryUtilService.addValue(builder, "name", NAME);
        FactoryUtilService.addValueBoolean(builder, "mandatory", MANDATORY);
        FactoryUtilService.addValueBoolean(builder, "protected", PROTEKTED);
        FactoryUtilService.addValueInt(builder, "requiredType", REQUIREDTYPE);
        FactoryUtilService.addValueBoolean(builder, "multiple", MULTIPLE);
        FactoryUtilService.addValueLong(builder, "createUser", CREATEUSER);

        return builder.build();
    }
}
