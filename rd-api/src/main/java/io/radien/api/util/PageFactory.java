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
package io.radien.api.util;

import io.radien.api.SystemVariables;
import io.radien.api.entity.Page;

import javax.json.JsonArray;
import javax.json.JsonObject;
/**
 * Page factory converter. This class converts a received json object into a page of users
 *
 * @author Nuno Santana
 * @author Rajesh Gavvala
 */
public class PageFactory {
    /**
     * Empty private constructor
     */
    private PageFactory(){}

    /**
     * Converts a JsonObject to a Page object
     *
     * @param jsonObject the JsonObject to convert
     * @return the SystemUserObject
     */
    public static Page<?> convertToPageObject(JsonObject jsonObject) {
        int currentPage = FactoryUtilService.getIntFromJson(SystemVariables.PAGE_CURRENT.getFieldName(), jsonObject);
        JsonArray results = FactoryUtilService.getArrayFromJson(SystemVariables.PAGE_RESULTS.getFieldName(), jsonObject);
        int totalPages = FactoryUtilService.getIntFromJson(SystemVariables.PAGE_TOTALS.getFieldName(), jsonObject);
        int totalResults = FactoryUtilService.getIntFromJson(SystemVariables.PAGE_TOTAL_RESULTS.getFieldName(), jsonObject);
        return new Page<>(results, currentPage, totalResults, totalPages);
    }

}
