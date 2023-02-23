package io.radien.ms.notificationmanagement.client.sqs;

import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.service.notification.email.EmailNotificationRESTServiceAccess;
import io.radien.exception.SystemException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.jms.JMSException;
import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;


@RunWith(MockitoJUnitRunner.class)
public class SQSProducerTest extends TestCase {

    @Mock
    private OAFAccess oaf;

    @Mock
    private EmailNotificationRESTServiceAccess notificationService;


    @Spy
    @InjectMocks
    private SQSProducer sqsProducer;

    private static final SQSConnection mockConnection = mock(SQSConnection.class);

    private static MockedConstruction<SQSConnectionFactory> mocked;

    @BeforeClass
    public static void initTests() {
        mocked = Mockito.mockConstruction(SQSConnectionFactory.class, (mock, context) -> {when(mock.createConnection()).thenReturn(mockConnection);});

    }

    @AfterClass
    public static void afterTests() {
        if(mocked != null) {
            mocked.close();
        }
    }

    @Test
    public void testEmailNotificationLocal() throws SystemException, JMSException {
        AmazonSQSMessagingClientWrapper mockWrapper = mock(AmazonSQSMessagingClientWrapper.class);
        when(mockConnection.getWrappedAmazonSQSClient()).thenReturn(mockWrapper);
        when(mockWrapper.queueExists(anyString())).thenReturn(true);
        when(mockConnection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(mock(Session.class));
        String email = "e@mail.com";
        String viewId = "viewId";
        String language = "language";
        Map<String, String> arguments = new HashMap<>();
        when(oaf.getProperty(OAFProperties.RADIEN_ENV, "LOCAL")).thenReturn("LOCAL");
        sqsProducer.init();
        sqsProducer.emailNotification(email, viewId, language, arguments);
        verify(notificationService).notify(email, viewId, language, arguments);
    }

    @Test
    public void testEmailNotification() throws SystemException, JMSException, NoSuchFieldException {
        AmazonSQSMessagingClientWrapper mockWrapper = mock(AmazonSQSMessagingClientWrapper.class);
        Queue mockQueue = mock(Queue.class);
        MessageProducer mockProducer = mock(MessageProducer.class);
        Session mockSession = mock(Session.class);
        when(mockConnection.getWrappedAmazonSQSClient()).thenReturn(mockWrapper);
        when(mockWrapper.queueExists(anyString())).thenReturn(true);
        when(mockConnection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(mockSession);
        when(mockSession.createQueue(anyString())).thenReturn(mockQueue);
        when(mockSession.createProducer(mockQueue)).thenReturn(mockProducer);
        String email = "e@mail.com";
        String viewId = "viewId";
        String language = "language";
        Map<String, String> arguments = new HashMap<>();
        when(oaf.getProperty(OAFProperties.RADIEN_ENV, "LOCAL")).thenReturn("NOT LOCAL");

        sqsProducer.init();
        assertTrue(sqsProducer.emailNotification(email, viewId, language, arguments));
        verify(notificationService, times(0)).notify(email, viewId, language, arguments);
    }
}