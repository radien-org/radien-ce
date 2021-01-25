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
package io.radien.ms.rolemanagement.util;

import io.radien.ms.rolemanagement.entities.Role;
import io.radien.ms.rolemanagement.factory.RoleFactory;

import javax.json.*;
import java.io.InputStream;
import java.util.List;

/**
 * @author Bruno Gama
 */
public class RoleModelMapper {

    public static JsonObject map(Role model) {
        return RoleFactory.convertToJsonObject(model);
    }

    public static JsonArray map(List<Role> models) {
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        models.forEach(model -> {
            JsonObject jsonObject = map(model);
            arrayBuilder.add(jsonObject);
        });
        return arrayBuilder.build();
    }

    public static Role map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();

            return RoleFactory.convert(jsonObject);
        }
    }
}
