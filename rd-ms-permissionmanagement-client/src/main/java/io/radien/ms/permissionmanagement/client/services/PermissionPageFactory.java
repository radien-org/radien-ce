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

import io.radien.api.entity.Page;
import io.radien.api.util.FactoryUtilService;
import io.radien.ms.permissionmanagement.client.entities.Permission;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.util.ArrayList;

/**
 * @author Newton Carvalho
 * Factory class to obtain Permission Pages from Json objects
 */
public class PermissionPageFactory {

    /**
     * Converts a JsonObject into a Permission Page object
     * @param page the JsonObject to convert
     * @return the Page encapsulating information regarding permissions
     */
    public static Page<Permission> convert(JsonObject page) {
        int currentPage = FactoryUtilService.getIntFromJson("currentPage", page);
        JsonArray results = FactoryUtilService.getArrayFromJson("results", page);
        int totalPages = FactoryUtilService.getIntFromJson("totalPages", page);
        int totalResults = FactoryUtilService.getIntFromJson("totalResults", page);
        ArrayList<Permission> pageResults = new ArrayList();
        if(results != null){
            for(int i = 0;i<results.size();i++){
                pageResults.add(PermissionFactory.convert(results.getJsonObject(i)));
            }
        }
        return new Page<>(pageResults,currentPage,totalResults,totalPages);
    }
}
