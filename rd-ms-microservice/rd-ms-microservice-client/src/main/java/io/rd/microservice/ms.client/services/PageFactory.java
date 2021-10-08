/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
package io.rd.microservice.ms.client.services;

import io.rd.microservice.api.entity.Page;
import io.rd.microservice.ms.client.entities.Microservice;
import io.rd.microservice.api.util.FactoryUtilService;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.util.ArrayList;

/**
 * @author Rajesh Gavvala
 */
public class PageFactory {
    private static final long serialVersionUID = 6812608123262000046L;

    public static Page<Microservice> convert(JsonObject page) {
        int currentPage = FactoryUtilService.getIntFromJson("currentPage", page);
        JsonArray results = FactoryUtilService.getArrayFromJson("results", page);
        int totalPages = FactoryUtilService.getIntFromJson("totalPages", page);
        int totalResults = FactoryUtilService.getIntFromJson("totalResults", page);
        ArrayList<Microservice> pageResults = new ArrayList();
        if(results != null){
            for(int i = 0;i<results.size();i++){
                pageResults.add(MicroserviceFactory.convert(results.getJsonObject(i)));
            }
        }

        return new Page<>(pageResults,currentPage,totalResults,totalPages);
    }
}