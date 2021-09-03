/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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

import java.io.InputStream;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;

import io.radien.api.model.permission.SystemPermission;
import io.radien.ms.permissionmanagement.client.services.PermissionFactory;

/**
 * Maps a Json Array input stream into a Collection (Permission)
 * @author Newton Carvalho
 */
public class ListPermissionModelMapper {

    /**
     * Permission list converter, will covert a given input stream into a list of system permissions
     * @param is to be converted
     * @return a list of system permissions that have been converted
     */
    public static List<? extends SystemPermission> map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonArray jsonArray = jsonReader.readArray();
            return PermissionFactory.convert(jsonArray);
        }
    }
}
