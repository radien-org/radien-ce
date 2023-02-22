package io.radien.lambda.notificationmanagement;


import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.radien.lambda.notificationmanagement.App;
import io.radien.lambda.notificationmanagement.util.email.params.EmailParams;
import io.radien.lambda.notificationmanagement.util.email.service.EmailNotificationService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;

import org.mockito.junit.MockitoRule;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AppTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private static MockedStatic<Guice> guiceMock;

    private final static EmailNotificationService notificationService = mock(EmailNotificationService.class);
    private final static Injector injector = Guice.createInjector(new AbstractModule() {
        @Override
        protected void configure() {
            bind(EmailNotificationService.class).toInstance(notificationService);
        }
    });

    @BeforeClass
    public static void setupStatics() {
        guiceMock = Mockito.mockStatic(Guice.class);
        when(Guice.createInjector(any(Module.class))).thenReturn(injector);
    }
    @AfterClass
    public static void stop() {
        if (guiceMock != null) {
            guiceMock.close();
        }
    }

    @Test
    public void handleRequest_shouldReturnConstantValue() throws JsonProcessingException {
        App app = new App();

        SQSEvent event = new SQSEvent();
        List<SQSEvent.SQSMessage> records = new ArrayList<>();
        EmailParams testParams = new EmailParams();
        testParams.setArguments(new HashMap<>());
        testParams.setLanguage("en");
        testParams.setEmail("email@target.com");
        testParams.setNotificationViewId("email-1");
        ObjectMapper mapper = new ObjectMapper();
        SQSEvent.SQSMessage record = new SQSEvent.SQSMessage();
        record.setBody(mapper.writeValueAsString(testParams));
        records.add(record);
        event.setRecords(records);

        app.handleRequest(event, null);
        verify(notificationService).notifyBehaviour(any(EmailParams.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void handleRequest_illegalArgumentInvalidBody() throws JsonProcessingException {
        App app = new App();

        SQSEvent event = new SQSEvent();
        List<SQSEvent.SQSMessage> records = new ArrayList<>();
        SQSEvent.SQSMessage record = new SQSEvent.SQSMessage();
        record.setBody("notAJSON");
        records.add(record);
        event.setRecords(records);

        app.handleRequest(event, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void handleRequest_illegalArgumentNoParams() throws JsonProcessingException {
        App app = new App();

        SQSEvent event = new SQSEvent();
        List<SQSEvent.SQSMessage> records = new ArrayList<>();
        SQSEvent.SQSMessage record = new SQSEvent.SQSMessage();
        records.add(record);
        event.setRecords(records);

        app.handleRequest(event, null);
    }
}
