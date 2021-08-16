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
package io.radien.ms.usermanagement.client.services;

import io.radien.api.entity.Page;
import io.radien.ms.usermanagement.client.entities.User;

import io.radien.ms.usermanagement.client.util.UserFactoryUtil;
import javax.json.JsonObject;

/**
 * Page factory converter. This class converts a received json object into a page of users
 *
 * @author Nuno Santana
 */
public class PageFactory extends UserFactoryUtil {

    /**
     * Converts a JsonObject to a Page object
     *
     * @param page the JsonObject to convert
     * @return the SystemUserObject
     */
    public static Page<User> convert(JsonObject page) {
        return convertToUserPageObject(page);
    }
}
