package ${package}.factory;

import ${package}.model.${library-name};

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 * @author Rajesh Gavvala
 *
 */

public class ${library-name}Factory {
    public static ${library-name} convert(JsonObject jsonObject) {
        Long id = ${library-name}FactoryService.getLongFromJson("id", jsonObject);
        String message = ${library-name}FactoryService.getStringFromJson("message", jsonObject);

        ${library-name} template = new ${library-name}();
        template.setId(id);
        template.setMessage(message);
        return template;
    }

    public static JsonObject convertToJsonObject(${library-name} template) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        ${library-name}FactoryService.addValueLong(builder, "id", template.getId());
        ${library-name}FactoryService.addValue(builder, "message", template.getMessage());
        return  builder.build();
    }
}
