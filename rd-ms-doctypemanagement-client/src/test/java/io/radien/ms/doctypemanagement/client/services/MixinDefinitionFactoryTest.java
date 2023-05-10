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
import io.radien.ms.doctypemanagement.client.entities.MixinDefinitionDTO;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MixinDefinitionFactoryTest {

    private final static Long ID = 1L;
    private final static String NAME = "name";
    private final static String NAMESPACE = "rd";
    private final static boolean ABSTRAKT = true;
    private final static boolean QUERYABLE = true;
    private final static boolean MIXIN = true;

    @Test
    public void testConvert() throws ParseException {
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        objectBuilder.add("id", ID);
        objectBuilder.add("name", NAME);
        objectBuilder.add("namespace", NAMESPACE);
        arrayBuilder.add(1L);
        objectBuilder.add("propertyDefinitions", arrayBuilder.build());
        objectBuilder.add("abstract", ABSTRAKT);
        objectBuilder.add("queryable", QUERYABLE);
        objectBuilder.add("mixin", MIXIN);

        MixinDefinitionDTO result = MixinDefinitionFactory.convert(objectBuilder.build());
        assertEquals(ID, result.getId());
        assertEquals(NAME, result.getName());
        assertEquals(NAMESPACE, result.getNamespace());
        assertEquals(1, result.getPropertyDefinitions().size());
        assertEquals(1L, (long) result.getPropertyDefinitions().get(0));
        assertEquals(ABSTRAKT, result.isAbstract());
        assertEquals(QUERYABLE, result.isQueryable());
        assertEquals(MIXIN, result.isMixin());
    }

    @Test
    public void testConvertToJsonObject() {
        MixinDefinitionDTO dto = new MixinDefinitionDTO();
        dto.setId(ID);
        dto.setName(NAME);
        dto.setNamespace(NAMESPACE);
        dto.setPropertyDefinitions(Collections.singletonList(1L));
        dto.setAbstract(ABSTRAKT);
        dto.setQueryable(QUERYABLE);
        dto.setMixin(MIXIN);

        JsonObject result = MixinDefinitionFactory.convertToJsonObject(dto);
        assertEquals((long) ID, result.getJsonNumber("id").longValue());
        assertEquals(NAME, result.getString("name"));
        assertEquals(NAMESPACE, result.getString("namespace"));
        assertEquals(1, result.getJsonArray("propertyDefinitions").size());
        assertEquals(1L, result.getJsonArray("propertyDefinitions").getJsonNumber(0).longValue());
        assertEquals(ABSTRAKT, result.getBoolean("abstract"));
        assertEquals(QUERYABLE, result.getBoolean("queryable"));
        assertEquals(MIXIN, result.getBoolean("mixin"));
    }

    @Test
    public void testConvertJsonToPage() throws ParseException {
        JsonObjectBuilder pageBuilder = Json.createObjectBuilder();
        JsonArrayBuilder pageResultsBuilder = Json.createArrayBuilder();
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        objectBuilder.add("id", ID);
        objectBuilder.add("name", NAME);
        objectBuilder.add("namespace", NAMESPACE);
        arrayBuilder.add(1L);
        objectBuilder.add("propertyDefinitions", arrayBuilder.build());
        objectBuilder.add("abstract", ABSTRAKT);
        objectBuilder.add("queryable", QUERYABLE);
        objectBuilder.add("mixin", MIXIN);

        pageBuilder.add("currentPage", 1);
        pageBuilder.add("totalPages", 1);
        pageBuilder.add("totalResults", 1);
        pageResultsBuilder.add(objectBuilder.build());
        pageBuilder.add("results", pageResultsBuilder.build());

        Page<MixinDefinitionDTO> result = MixinDefinitionFactory.convertJsonToPage(pageBuilder.build());
        assertEquals(1, result.getCurrentPage());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getTotalResults());
        assertEquals(1, result.getResults().size());
        assertEquals(ID, result.getResults().get(0).getId());
        assertEquals(NAME, result.getResults().get(0).getName());
        assertEquals(NAMESPACE, result.getResults().get(0).getNamespace());
        assertEquals(1, result.getResults().get(0).getPropertyDefinitions().size());
        assertEquals(1L, (long) result.getResults().get(0).getPropertyDefinitions().get(0));
        assertEquals(ABSTRAKT, result.getResults().get(0).isAbstract());
        assertEquals(QUERYABLE, result.getResults().get(0).isQueryable());
        assertEquals(MIXIN, result.getResults().get(0).isMixin());
    }

    @Test
    public void testConvertArray() throws ParseException {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
        JsonArrayBuilder propertyDefinitionArrayBuilder = Json.createArrayBuilder();
        objectBuilder.add("id", ID);
        objectBuilder.add("name", NAME);
        objectBuilder.add("namespace", NAMESPACE);
        propertyDefinitionArrayBuilder.add(1L);
        objectBuilder.add("propertyDefinitions", propertyDefinitionArrayBuilder.build());
        objectBuilder.add("abstract", ABSTRAKT);
        objectBuilder.add("queryable", QUERYABLE);
        objectBuilder.add("mixin", MIXIN);

        arrayBuilder.add(objectBuilder.build());

        List<MixinDefinitionDTO> results = MixinDefinitionFactory.convert(arrayBuilder.build());
        assertEquals(1, results.size());
        assertEquals(ID, results.get(0).getId());
        assertEquals(NAME, results.get(0).getName());
        assertEquals(NAMESPACE, results.get(0).getNamespace());
        assertEquals(1, results.get(0).getPropertyDefinitions().size());
        assertEquals(1L, (long) results.get(0).getPropertyDefinitions().get(0));
        assertEquals(ABSTRAKT, results.get(0).isAbstract());
        assertEquals(QUERYABLE, results.get(0).isQueryable());
        assertEquals(MIXIN, results.get(0).isMixin());

    }
}