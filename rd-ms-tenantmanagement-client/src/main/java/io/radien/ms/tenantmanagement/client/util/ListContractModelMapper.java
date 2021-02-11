package io.radien.ms.tenantmanagement.client.util;

import io.radien.api.model.contract.SystemContract;
import io.radien.ms.tenantmanagement.client.services.ContractFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;

public class ListContractModelMapper {
    public static List<? extends SystemContract> map(InputStream is) throws ParseException {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonArray jsonArray = jsonReader.readArray();
            return ContractFactory.convert(jsonArray);
        }
    }
}
