package io.radien.ms.notificationmanagement.sqs;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.service.notification.SQSAccessAccess;
import io.radien.api.service.notification.email.EmailNotificationRESTServiceAccess;
import io.radien.exception.SystemException;
import org.apache.commons.lang3.StringUtils;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Map;

@RequestScoped
public class SQSProducer implements SQSAccessAccess {

    private static final Logger log = LoggerFactory.getLogger(SQSProducer.class);

    private static final String ENDPOINT = "placeholder";
    private static final String QUEUE_NAME = "Notification Queue";
    private static final Region REGION = Region.EU_WEST_1;

    private Session session;
    private SQSConnection connection;


    @Inject
    private OAFAccess oaf;

    @Inject
    private EmailNotificationRESTServiceAccess notificationService;


    @PostConstruct
    public void init(){
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
        } catch (URISyntaxException e) {
            log.error(MessageFormat.format("Failed to parse destination as URI: {0}", e));
        } catch (JMSException e) {
            log.error(MessageFormat.format("Something went wrong initializing: {0}", e));
        }
    }

    @PreDestroy
    public void terminate() throws JMSException {
        session.close();
        connection.close();
    }

    public boolean emailNotification(String email, String viewId, String language, Map<String, String> arguments){
        if(oaf.getProperty(OAFProperties.RADIEN_ENV).equalsIgnoreCase("LOCAL")){
            try {
                return notificationService.notify(email, viewId, language, arguments);
            } catch (SystemException e) {
                log.error(MessageFormat.format("Failed to obtain a valid url to mailing service: {0}", e));
            }
        }
        return sendNotification(formatEmailNotification(email, viewId, language, arguments));
    }

    private String formatEmailNotification(String email, String viewId, String language, Map<String, String> arguments){
        String message = "{\n  \"email\": \"".concat(email)
                .concat("\",\n  \"viewId\": \"").concat(viewId)
                .concat("\",\n  \"language\": \"").concat(language)
                .concat("\",\n  \"arguments\": {\"");
        for(Map.Entry<String, String> argument : arguments.entrySet()){
            message = message.concat("\n\"").concat(argument.getKey()).concat("\": \"")
                    .concat(argument.getValue()).concat("\",");
        }
        return StringUtils.removeEnd(message, ",").concat("\n}");
    }

    private boolean sendNotification(String message) {
        try {
            MessageProducer producer = session.createProducer(session.createQueue(QUEUE_NAME));
            producer.send(session.createTextMessage(message));
            return true;
        } catch (JMSException e) {
            log.error(MessageFormat.format("Something went wrong sending notification: {0}", e));
        }
        return false;
    }
}
