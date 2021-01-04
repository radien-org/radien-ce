package io.radien.ms.usermanagement.client.services;

import io.radien.ms.usermanagement.client.entities.Page;
import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.util.FactoryUtilService;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.util.ArrayList;

public class PageFactory {
    /**
     * Converts a JsonObject to a Page object
     *
     * @param page the JsonObject to convert
     * @return the SystemUserObject
     */
    public static Page<User> convert(JsonObject page) {
        int currentPage = FactoryUtilService.getIntFromJson("currentPage", page);
        JsonArray results = FactoryUtilService.getArrayFromJson("results", page);
        int totalPages = FactoryUtilService.getIntFromJson("totalPages", page);
        int totalResults = FactoryUtilService.getIntFromJson("totalResults", page);
        ArrayList<User> pageResults = new ArrayList();
        if(results != null){
            for(int i = 0;i<results.size();i++){
                pageResults.add(UserFactory.convert(results.getJsonObject(i)));
            }
        }

        return new Page<>(pageResults,currentPage,totalResults,totalPages);
    }
}
