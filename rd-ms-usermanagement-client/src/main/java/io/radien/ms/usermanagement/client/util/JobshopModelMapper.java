/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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

import io.radien.api.SystemVariables;
import io.radien.api.entity.Page;
import io.radien.api.model.jobshop.SystemStudentIdentity;
import io.radien.api.util.FactoryUtilService;
import io.radien.api.util.PageFactory;
import io.radien.ms.usermanagement.client.entities.JobshopItem;
import io.radien.ms.usermanagement.client.entities.StudentIdentity;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class JobshopModelMapper {

    public static SystemStudentIdentity mapStudentIdentity(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();

            return convertToStudentIdentity(jsonObject);
        }
    }

    /**
     * Obtains a User Page from a Json input stream
     * @param is to be mapped
     * @return a page of user mapped from the input stream
     */
    public static Page<StudentIdentity> mapStudentIdentityToPage(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();

            return convertToStudentIdentityPageObject(jsonObject);
        }
    }

    public static List<JobshopItem> mapJobshopItemList(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            List<JobshopItem> itemsList = new ArrayList<>();
            JsonArray jsonArray = jsonReader.readArray();
            for(int i = 0;i<jsonArray.size();i++) {
                itemsList.add(convertToJobshopItem(jsonArray.getJsonObject(i)));
            }
            return itemsList;
        }
    }

    public static Page<StudentIdentity> convertToStudentIdentityPageObject(JsonObject jsonObject){
        Page<?> page = PageFactory.convertToPageObject(jsonObject);
        JsonArray jsonValues = page.getJsonValues();
        ArrayList<StudentIdentity> pageResults = new ArrayList<>();
        if(jsonValues != null){
            for(int i = 0;i<jsonValues.size();i++){
                pageResults.add(convertToStudentIdentity(jsonValues.getJsonObject(i)));
            }
        }
        return new Page<>(pageResults, page.getCurrentPage(), page.getTotalResults(), page.getTotalPages());
    }

    private static StudentIdentity convertToStudentIdentity(JsonObject jsonObject) {
        StudentIdentity identity = new StudentIdentity();
        identity.setId(FactoryUtilService.getLongFromJson(SystemVariables.ID.getFieldName(), jsonObject));
        identity.setName(FactoryUtilService.getStringFromJson(SystemVariables.NAME.getFieldName(), jsonObject));
        identity.setPhoneNumber(FactoryUtilService.getStringFromJson("phoneNumber", jsonObject));
        identity.setEmail(FactoryUtilService.getStringFromJson("email", jsonObject));
        identity.setCourse(FactoryUtilService.getStringFromJson("course", jsonObject));

        return identity;
    }

    private static JobshopItem convertToJobshopItem(JsonObject jsonObject) {
        JobshopItem item = new JobshopItem();
        item.setId(FactoryUtilService.getLongFromJson("id", jsonObject));
        item.setLabel(FactoryUtilService.getStringFromJson("label", jsonObject));
        item.setWeight(FactoryUtilService.getIntFromJson("weight", jsonObject));

        return item;
    }
}
