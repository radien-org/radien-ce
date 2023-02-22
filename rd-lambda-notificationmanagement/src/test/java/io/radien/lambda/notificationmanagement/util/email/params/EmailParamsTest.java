package io.radien.lambda.notificationmanagement.util.email.params;


import io.radien.lambda.notificationmanagement.util.email.params.EmailParams;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class EmailParamsTest {

    private EmailParams params;

    private static final String EMAIL = "e@mail.com";
    private static final String NOTIFICATION_VIEW_ID = "viewId";
    private static final String LANGUAGE = "en";

    private static final Map<String, String> args = new HashMap<>();


    @Test
    public void getters(){
        args.put("testKey", "testValue");
        params = new EmailParams(EMAIL, NOTIFICATION_VIEW_ID, LANGUAGE, args);
        assertEquals(EMAIL, params.getEmail());
        assertEquals(NOTIFICATION_VIEW_ID, params.getNotificationViewId());
        assertEquals(LANGUAGE, params.getLanguage());
        assertEquals(args, params.getArguments());
    }

    @Test
    public void setters(){
        args.put("testKey", "testValue");
        params = new EmailParams();
        params.setEmail(EMAIL);
        params.setNotificationViewId(NOTIFICATION_VIEW_ID);
        params.setLanguage(LANGUAGE);
        params.setArguments(args);

        assertEquals(EMAIL, params.getEmail());
        assertEquals(NOTIFICATION_VIEW_ID, params.getNotificationViewId());
        assertEquals(LANGUAGE, params.getLanguage());
        assertEquals(args, params.getArguments());
    }
}