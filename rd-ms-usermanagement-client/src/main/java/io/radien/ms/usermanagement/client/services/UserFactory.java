package io.radien.ms.usermanagement.client.services;

import io.radien.ms.usermanagement.client.entities.User;
import io.radien.ms.usermanagement.client.util.FactoryUtilService;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.Date;

public class UserFactory {

    /**
     * Create a user with already predefine fields.
     *
     * @param firstname user first name
     * @param lastname user last name
     * @param logon user logon
     * @param sub user subject
     * @param createdUser the user which has created the user
     * @param email user email
     * @return a User object to be used
     */
    public static User create(String firstname, String lastname, String logon, String sub, String email, Long createdUser){
        User u = new User();
        u.setFirstname(firstname);
        u.setLastname(lastname);
        u.setLogon(logon);
        u.setEnabled(true);
        u.setSub(sub);
        u.setCreateUser(createdUser);
        Date now = new Date();
        u.setLastUpdate(now);
        u.setCreateDate(now);
        u.setUserEmail(email);
        return u;
    }

    /**
     * Converts a JSONObject to a SystemUser object Used by the Application
     * DataInit to seed Data in the database
     *
     * @param person the JSONObject to convert
     * @return the SystemUserObject
     */
    //TODO: Complete the object conversion fields missing
    public static User convert(JsonObject person) {
        Long id = FactoryUtilService.getLongFromJson("id", person);
        String logon = FactoryUtilService.getStringFromJson("logon", person);
        String userEmail = FactoryUtilService.getStringFromJson("userEmail", person);
        Long createUser = FactoryUtilService.getLongFromJson("createUser", person);
        Long lastUpdateUser = FactoryUtilService.getLongFromJson("lastUpdateUser", person);
        String sub = FactoryUtilService.getStringFromJson("sub", person);
        String firstname = FactoryUtilService.getStringFromJson("firstname", person);
        String lastname = FactoryUtilService.getStringFromJson("lastname",person);

        User user = new User();
        user.setId(id);
        user.setLogon(logon);
        user.setUserEmail(userEmail);
        user.setCreateUser(createUser);
        // TODO: Set password protected
//		user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setCreateDate(new Date());
        user.setLastUpdate(new Date());
        user.setSub(sub);
        user.setFirstname(firstname);
        user.setLastname(lastname);

        return user;
    }

    /**
     * Converts a System user to a Json Object
     *
     * @param person system user to be converted to json
     * @return json object with keys and values constructed
     */
    //TODO: Complete the object conversion fields missing
    public static JsonObject convertToJsonObject(User person) {
        JsonObjectBuilder builder = Json.createObjectBuilder();

        FactoryUtilService.addValue(builder, "id", person.getId());
        FactoryUtilService.addValue(builder, "logon", person.getLogon());
        FactoryUtilService.addValue(builder, "userEmail", person.getUserEmail());
        FactoryUtilService.addValueLong(builder, "createUser", person.getCreateUser());
        FactoryUtilService.addValueLong(builder, "lastUpdateUser", person.getLastUpdateUser());
        FactoryUtilService.addValue(builder, "sub", person.getSub());
        FactoryUtilService.addValue(builder, "firstname", person.getFirstname());
        FactoryUtilService.addValue(builder, "lastname", person.getLastname());
        return  builder.build();
    }

}
