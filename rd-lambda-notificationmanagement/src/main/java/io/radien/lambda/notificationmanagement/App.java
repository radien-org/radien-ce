package io.radien.lambda.notificationmanagement;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.radien.lambda.notificationmanagement.util.email.module.EmailModule;
import io.radien.lambda.notificationmanagement.util.email.service.EmailNotificationService;
import io.radien.lambda.notificationmanagement.util.email.params.EmailParams;

/**
 * Lambda function entry point. You can change to use other pojo type or implement
 * a different RequestHandler.
 *
 * @see <a href=https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html>Lambda Java Handler</a> for more information
 */
public class App implements RequestHandler<SQSEvent, Object> {

    private final EmailNotificationService emailService;

    public App() {
        Injector injector = Guice.createInjector(new EmailModule());
        emailService = injector.getInstance(EmailNotificationService.class);
    }

    @Override
    public Object handleRequest(final SQSEvent input, final Context context) {
        EmailParams params = null;
        for(SQSEvent.SQSMessage r : input.getRecords()){
            try {
                ObjectMapper mapper = new ObjectMapper();
                if((params = mapper.readValue(r.getBody(), EmailParams.class)) != null){
                    emailService.notifyBehaviour(params);
                }
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Unable to extract email parameters from message body.");
            }
        }
        return input;
    }
}
