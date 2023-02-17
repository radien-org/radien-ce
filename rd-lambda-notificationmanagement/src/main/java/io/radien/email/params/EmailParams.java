package io.radien.email.params;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.radien.generic.params.NotificationParams;

import java.util.Map;

public class EmailParams implements NotificationParams {


    private String email;

    private String notificationViewId;

    private String language;

    private Map<String, String> arguments;

    public EmailParams(){
    }

    public EmailParams(@JsonProperty("email") String email,
                       @JsonProperty("notificationViewId") String notificationViewId,
                       @JsonProperty("language") String language,
                       @JsonProperty("arguments")Map<String, String> arguments) {
        this.email = email;
        this.notificationViewId = notificationViewId;
        this.language = language;
        this.arguments = arguments;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNotificationViewId() {
        return notificationViewId;
    }

    public void setNotificationViewId(String notificationViewId) {
        this.notificationViewId = notificationViewId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Map<String, String> getArguments() {
        return arguments;
    }

    public void setArguments(Map<String, String> arguments) {
        this.arguments = arguments;
    }
}
