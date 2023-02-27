package io.radien.ms.notificationmanagement.client.sqs;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.service.notification.SQSProducerAccess;
import io.radien.api.service.notification.email.EmailNotificationRESTServiceAccess;
import io.radien.exception.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@RequestScoped
public class SQSProducer implements SQSProducerAccess {

    enum FinishStates{
        SUCCESSFUL,
        INVALID_URI,
        GENERIC
    }

    private static final Logger log = LoggerFactory.getLogger(SQSProducer.class);

    private static final String ENDPOINT = "http://host.docker.internal:4566";
    private static final String QUEUE_NAME = "NotificationQueue";
    private static final Region REGION = Region.EU_WEST_1;

    private static final String LOCAL_ENV_NAME = "LOCAL";

    private Session session;
    private SQSConnection connection;

    @Inject
    private OAFAccess oaf;

    @Inject
    private EmailNotificationRESTServiceAccess notificationService;

    @PostConstruct
    private void init(){
        setup();
    }

    public FinishStates setup(){
        try {
            SQSConnectionFactory connectionFactory = new SQSConnectionFactory(
                    new ProviderConfiguration(),
                    SqsClient.builder()
                            .endpointOverride(new URI(ENDPOINT))
                            .region(REGION)
                            .credentialsProvider(DefaultCredentialsProvider.create())
                            .build()
            );
            connection = connectionFactory.createConnection();

            if(!connection.getWrappedAmazonSQSClient().queueExists(QUEUE_NAME)){
                connection.getWrappedAmazonSQSClient().createQueue(QUEUE_NAME);
            }

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            log.info("Created queue with name \"{}\".", QUEUE_NAME);
            return FinishStates.SUCCESSFUL;
        } catch (URISyntaxException e) {
            log.error("Failed to parse destination as URI: {}", e.toString());
            return FinishStates.INVALID_URI;
        } catch (JMSException e) {
            log.error("Something went wrong initializing: {}", e.toString());
            return FinishStates.GENERIC;
        }
    }

    @PreDestroy
    public void terminate() throws JMSException {
        session.close();
        connection.close();
    }

    public boolean emailNotification(String email, String viewId, String language, Map<String, String> arguments){
        if(oaf.getProperty(OAFProperties.RADIEN_ENV, LOCAL_ENV_NAME).equalsIgnoreCase(LOCAL_ENV_NAME)){
            try {
                log.info("Sent locally.");
                return notificationService.notify(email, viewId, language, arguments);
            } catch (SystemException e) {
                log.error("Failed to obtain a valid url to mailing service: {}", e.toString());
            }
        }
        log.info("Sent non-locally. Env: {}", oaf.getProperty(OAFProperties.RADIEN_ENV));
        return sendNotification(formatEmailNotification(email, viewId, language, arguments));
    }

    private String formatEmailNotification(String email, String viewId, String language, Map<String, String> arguments){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("email", email);
        builder.add("viewId", viewId);
        builder.add("language", language);
        JsonObjectBuilder argumentsBuilder = Json.createObjectBuilder();
        for(Map.Entry<String, String> argument : arguments.entrySet()){
            argumentsBuilder.add(argument.getKey(), argument.getValue());
        }
        builder.add("arguments", argumentsBuilder.build());
        return builder.build().toString();
    }

    private boolean sendNotification(String message) {
        try {
            MessageProducer producer = session.createProducer(session.createQueue(QUEUE_NAME));
            producer.send(session.createTextMessage(message));
            return true;
        } catch (JMSException e) {
            log.error("Something went wrong sending notification: {}", e.toString());
        }
        return false;
    }
}
