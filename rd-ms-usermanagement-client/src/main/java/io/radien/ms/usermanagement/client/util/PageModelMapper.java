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
package io.radien.ms.usermanagement.client.util;

import io.radien.api.entity.Page;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.services.PageFactory;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.InputStream;

/**
 * @author Marco Weiland 
 *
 */
public class PageModelMapper {

    public static Page<User> map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();

            return PageFactory.convert(jsonObject);
        }
    }
}
