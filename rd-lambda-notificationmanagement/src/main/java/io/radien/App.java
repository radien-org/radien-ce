package io.radien;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.radien.email.service.EmailNotificationService;
import io.radien.email.params.EmailParams;

/**
 * Lambda function entry point. You can change to use other pojo type or implement
 * a different RequestHandler.
 *
 * @see <a href=https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html>Lambda Java Handler</a> for more information
 */
public class App implements RequestHandler<SQSEvent, Object> {

    private final EmailNotificationService emailService;

    public App() {
        emailService = new EmailNotificationService();
    }

    @Override
    public Object handleRequest(final SQSEvent input, final Context context) {
        EmailParams params = null;
        for(SQSEvent.SQSMessage r : input.getRecords()){
            try {
                ObjectMapper mapper = new ObjectMapper();
                if((params = mapper.readValue(r.getBody(), EmailParams.class)) != null){
                    break;
                }
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Unable to extract email parameters from message body.");
            }
        }
        if(params == null){
            throw new IllegalArgumentException("No email notification present in message body.");
        }
        emailService.notifyBehaviour(params);
        return input;
    }
}
