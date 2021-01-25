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
package io.radien.ms.rolemanagement.client.util;

import io.radien.api.model.role.SystemRole;
import io.radien.ms.rolemanagement.client.services.RoleFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import java.io.InputStream;
import java.util.List;

/**
 * @author Bruno Gama
 */
public class ListRoleModelMapper {

    /**
     * Maps the given information into a List of Roles
     * @param is Inputed stream to be converted
     * @return a list of role information
     */
    public static List<? extends SystemRole> map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonArray jsonArray = jsonReader.readArray();
            return RoleFactory.convert(jsonArray);
        }
    }
}
