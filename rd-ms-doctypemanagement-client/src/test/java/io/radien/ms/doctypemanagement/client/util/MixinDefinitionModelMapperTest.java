package io.radien.ms.doctypemanagement.client.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.radien.api.entity.Page;
import io.radien.ms.doctypemanagement.client.entities.MixinDefinitionDTO;
import org.junit.Test;

import javax.json.JsonArray;
import javax.json.JsonObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MixinDefinitionModelMapperTest {

    @Test
    public void testMap() {
        MixinDefinitionDTO object = new MixinDefinitionDTO();
        object.setId(1L);
        object.setName("name");
        object.setNamespace("nmspc");
        object.setQueryable(true);
        object.setPropertyDefinitions(Collections.singletonList(1L));

        JsonObject result = MixinDefinitionModelMapper.map(object);
        assertEquals(object.getId().longValue(), result.getJsonNumber("id").longValue());
        assertEquals(object.getName(), result.getJsonString("name").getString());
        assertEquals(object.getNamespace(), result.getJsonString("namespace").getString());
        assertEquals(object.getPropertyDefinitions().size(),result.getJsonArray("propertyDefinitions").size());
        assertEquals(object.getPropertyDefinitions().get(0).longValue(), result.getJsonArray("propertyDefinitions").getJsonNumber(0).longValue());
        assertTrue(result.getBoolean("queryable"));
    }

    @Test
    public void testMapObjectList() {
        List<MixinDefinitionDTO> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            MixinDefinitionDTO object = new MixinDefinitionDTO();
            object.setId((long)i);
            object.setName("name");
            object.setNamespace("nmspc");
            object.setAbstract(true);
            object.setQueryable(true);
            object.setPropertyDefinitions(Collections.singletonList(1L));
            list.add(object);
        }

        JsonArray result = MixinDefinitionModelMapper.map(list);
        assertEquals(10, result.size());
    }

    @Test
    public void testMapStream() throws JsonProcessingException, ParseException {
        MixinDefinitionDTO object = new MixinDefinitionDTO();
        object.setId(1L);
        object.setName("name");
        object.setNamespace("nmspc");
        object.setAbstract(true);
        object.setQueryable(true);
        object.setPropertyDefinitions(Collections.singletonList(1L));
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = new ByteArrayInputStream(mapper.writeValueAsBytes(object));

        MixinDefinitionDTO result = MixinDefinitionModelMapper.map(is);
        assertEquals(object.getId(), result.getId());
        assertEquals(object.getName(), result.getName());
        assertEquals(object.getNamespace(), result.getNamespace());
        assertEquals(object.isAbstract(), result.isAbstract());
        assertEquals(object.isQueryable(), result.isQueryable());
        assertEquals(object.getPropertyDefinitions().size(), object.getPropertyDefinitions().size());
        assertEquals(object.getPropertyDefinitions().get(0).longValue(), object.getPropertyDefinitions().get(0).longValue());

    }

    @Test
    public void testMapToPage() throws JsonProcessingException {
        Page<MixinDefinitionDTO> page = new Page<>();
        page.setTotalPages(10);
        page.setTotalResults(10);
        page.setCurrentPage(1);
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = new ByteArrayInputStream(mapper.writeValueAsBytes(page));

        Page<MixinDefinitionDTO> result = MixinDefinitionModelMapper.mapToPage(is);
        assertEquals(page.getTotalPages(), result.getTotalPages());
        assertEquals(page.getTotalResults(), result.getTotalResults());
        assertEquals(page.getCurrentPage(), result.getCurrentPage());
    }

}