package ${package}.util;

import ${package}.factory.${library-name}Factory;
import ${package}.model.${library-name};


import javax.json.*;
import java.io.InputStream;


/**
 * @author mawe
 * @ Rajesh Gavvala
 */
public class ${library-name}Mapper {

    public static JsonObject map(${library-name} model) {
        return ${library-name}Factory.convertToJsonObject(model);
    }

    public static ${library-name} map(InputStream is) {
        try(JsonReader jsonReader = Json.createReader(is)) {
            JsonObject jsonObject = jsonReader.readObject();

            return ${library-name}Factory.convert(jsonObject);
        }
    }

}
