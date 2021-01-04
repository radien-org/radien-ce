package io.radien.ms.usermanagement.client.services;

import io.radien.ms.usermanagement.client.entities.Page;
import io.radien.ms.usermanagement.client.entities.User;
import junit.framework.TestCase;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class PageFactoryTest extends TestCase {

    @Test
    public void testConvert() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        builder.add("currentPage",-1);
        builder.add("results",jsonArrayBuilder.build());
        builder.add("totalPages",1);
        builder.add("totalResults",4);
        Page<User> page = PageFactory.convert(builder.build());
        assertEquals(-1,page.getCurrentPage());
        assertEquals(0,page.getResults().size());
        assertEquals(1,page.getTotalPages());
        assertEquals(4,page.getTotalResults());
    }
}