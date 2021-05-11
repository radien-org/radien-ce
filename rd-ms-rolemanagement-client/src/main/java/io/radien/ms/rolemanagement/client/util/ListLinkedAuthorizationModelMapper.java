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

import io.radien.api.model.linked.authorization.SystemLinkedAuthorization;
import io.radien.ms.rolemanagement.client.services.LinkedAuthorizationFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import java.io.InputStream;
import java.util.List;

/**
 * Mapper utility class that allows to convert a JSON message into a list of system linked authorizations
 *
 * @author Bruno Gama
 */
public class ListLinkedAuthorizationModelMapper {

    private ListLinkedAuthorizationModelMapper() {
        //empty constructor
    }

    /**
     * Converts the received JSON message into a list of linked authorizations
     * @param is inputted information to be converted into the object
     * @return a list of system linked authorization
     */
    public static List<? extends SystemLinkedAuthorization> map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonArray jsonArray = jsonReader.readArray();
            return LinkedAuthorizationFactory.convert(jsonArray);
        }
    }
}
