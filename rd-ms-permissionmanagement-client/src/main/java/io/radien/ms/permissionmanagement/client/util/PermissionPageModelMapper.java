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
package io.radien.ms.permissionmanagement.client.util;

import io.radien.api.entity.Page;
import io.radien.ms.permissionmanagement.client.entities.Permission;
import io.radien.ms.permissionmanagement.client.services.PermissionPageFactory;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.InputStream;

/**
 * @author Newton Carvalho
 */
public class PermissionPageModelMapper {

    /**
     * Obtains a Permission Page from a Json input stream
     * @param is
     * @return
     */
    public static Page<Permission> map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();

            return PermissionPageFactory.convert(jsonObject);
        }
    }
}
