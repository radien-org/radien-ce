package io.radien;


import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import io.radien.email.params.EmailParams;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AppTest {

    private App app;

    @Test
    public void handleRequest_shouldReturnConstantValue() {/*
        app = new App();
        String email = "e@mail.com";
        String viewId = "email-1";
        String language = "langolano";

        App function = new App();
        SQSEvent event = new SQSEvent();
        List<SQSEvent.SQSMessage> records = new ArrayList<>();
        Map<String, String> attributes = new HashMap<>();
        attributes.put("email", email);
        attributes.put("viewId", viewId);
        attributes.put("language", language);
        SQSEvent.SQSMessage record = new SQSEvent.SQSMessage();
        record.setAttributes(attributes);
        records.add(record);
        event.setRecords(records);

        function.handleRequest(event, null);
        verify(app).getEmailService().notifyBehaviour(any(EmailParams.class));*/
    }
}
