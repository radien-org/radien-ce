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
package io.radien.ms.permissionmanagement.client.services;

import io.radien.api.util.FactoryUtilService;
import io.radien.ms.permissionmanagement.client.entities.Resource;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Newton Carvalho
 * Utilitary class to create Resource class instances
 */
public class ResourceFactory {

    /**
     * Create a Resource with already predefine fields.
     *
     * @param name resource name
     * @param createUser the user which has created the permission
     * @return a Resource object to be used
     */
    public static Resource create(String name, Long createUser){
        Resource u = new Resource();
        u.setName(name);
        u.setCreateUser(createUser);
        Date now = new Date();
        u.setLastUpdate(now);
        u.setCreateDate(now);
        return u;
    }

    /**
     * Converts a JSONObject to a SystemResource object Used by the Application
     * DataInit to seed Data in the database
     *
     * @param resourceAsJsonObject the JSONObject to convert
     * @return the SystemResource object
     */
    public static Resource convert(JsonObject resourceAsJsonObject) {
        Long id = FactoryUtilService.getLongFromJson("id", resourceAsJsonObject);
        String name = FactoryUtilService.getStringFromJson("name", resourceAsJsonObject);
        Long createResource = FactoryUtilService.getLongFromJson("createUser", resourceAsJsonObject);
        Long updateResource = FactoryUtilService.getLongFromJson("lastUpdateUser", resourceAsJsonObject);
        Resource resource = new Resource();
        resource.setId(id);
        resource.setName(name);
        resource.setCreateUser(createResource);
        resource.setLastUpdateUser(updateResource);
        resource.setCreateDate(new Date());
        resource.setLastUpdate(new Date());
        return resource;
    }

    /**
     * Converts a System resource to a Json Object
     *
     * @param a system resource to be converted to json
     * @return json object with keys and values constructed
     */
    public static JsonObject convertToJsonObject(Resource a) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        FactoryUtilService.addValueLong(builder, "id", a.getId());
        FactoryUtilService.addValue(builder, "name", a.getName());
        FactoryUtilService.addValueLong(builder, "createUser", a.getCreateUser());
        FactoryUtilService.addValueLong(builder, "lastUpdateUser", a.getLastUpdateUser());
        return builder.build();
    }

    /**
     * Converts a Json Array into an Resource List
     * @param jsonArray
     * @return
     */
    public static List<Resource> convert(JsonArray jsonArray) {
        return jsonArray.stream().map(i->convert(i.asJsonObject())).collect(Collectors.toList());
    }
}