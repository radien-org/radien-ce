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
import javax.jms.JMSException;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;

import java.util.HashMap;
import java.util.Map;

import org.mockito.junit.MockitoRule;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;


public class SQSProducerTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private OAFAccess oaf;

    @Mock
    private EmailNotificationRESTServiceAccess notificationService;


    @Spy
    @InjectMocks
    private SQSProducer sqsProducer;

    private static final SQSConnection mockConnection = mock(SQSConnection.class);

    private static final MockedConstruction<SQSConnectionFactory> mocked = Mockito.mockConstruction(SQSConnectionFactory.class, (mock, context) -> when(mock.createConnection()).thenReturn(mockConnection));


    @AfterClass
    public static void afterTests() {
        if (mocked != null) {
            mocked.close();
        }
    }

    @Test
    public void testSetupJMSException() throws JMSException {
        AmazonSQSMessagingClientWrapper mockWrapper = mock(AmazonSQSMessagingClientWrapper.class);
        when(mockConnection.getWrappedAmazonSQSClient()).thenReturn(mockWrapper);
        when(mockWrapper.queueExists(anyString())).thenThrow(JMSException.class);
        assertEquals(SQSProducer.FinishStates.GENERIC, sqsProducer.setup());
    }

    @Test
    public void testEmailNotificationJMSException() throws JMSException {
        AmazonSQSMessagingClientWrapper mockWrapper = mock(AmazonSQSMessagingClientWrapper.class);
        when(mockConnection.getWrappedAmazonSQSClient()).thenReturn(mockWrapper);
        when(mockWrapper.queueExists(anyString())).thenReturn(false);
        Session session = mock(Session.class);
        when(mockConnection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(session);
        when(session.createQueue(anyString())).thenThrow(JMSException.class);
        sqsProducer.setup();
        when(oaf.getProperty(OAFProperties.RADIEN_ENV, "LOCAL")).thenReturn("NOT LOCAL");
        assertFalse(sqsProducer.emailNotification("", "", "", new HashMap<>()));
    }

    @Test
    public void testEmailNotificationLocal() throws SystemException, JMSException {
        AmazonSQSMessagingClientWrapper mockWrapper = mock(AmazonSQSMessagingClientWrapper.class);
        when(mockConnection.getWrappedAmazonSQSClient()).thenReturn(mockWrapper);
        when(mockWrapper.queueExists(anyString())).thenReturn(false);
        when(mockConnection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(mock(Session.class));
        String email = "e@mail.com";
        String viewId = "viewId";
        String language = "language";
        Map<String, String> arguments = new HashMap<>();
        when(oaf.getProperty(OAFProperties.RADIEN_ENV, "LOCAL")).thenReturn("LOCAL");
        sqsProducer.setup();
        sqsProducer.emailNotification(email, viewId, language, arguments);
        verify(notificationService).notify(email, viewId, language, arguments);
    }

    @Test
    public void testEmailNotification() throws SystemException, JMSException {
        AmazonSQSMessagingClientWrapper mockWrapper = mock(AmazonSQSMessagingClientWrapper.class);
        Queue mockQueue = mock(Queue.class);
        MessageProducer mockProducer = mock(MessageProducer.class);
        Session mockSession = mock(Session.class);
        when(mockConnection.getWrappedAmazonSQSClient()).thenReturn(mockWrapper);
        when(mockWrapper.queueExists(anyString())).thenReturn(false);
        when(mockConnection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(mockSession);
        when(mockSession.createQueue(anyString())).thenReturn(mockQueue);
        when(mockSession.createProducer(mockQueue)).thenReturn(mockProducer);
        String email = "e@mail.com";
        String viewId = "viewId";
        String language = "language";
        Map<String, String> arguments = new HashMap<>();
        arguments.put("key", "value");
        when(oaf.getProperty(OAFProperties.RADIEN_ENV, "LOCAL")).thenReturn("NOT LOCAL");

        sqsProducer.setup();
        assertTrue(sqsProducer.emailNotification(email, viewId, language, arguments));
        verify(notificationService, times(0)).notify(email, viewId, language, arguments);
    }

    @Test
    public void testTerminate() throws JMSException {
        AmazonSQSMessagingClientWrapper mockWrapper = mock(AmazonSQSMessagingClientWrapper.class);
        Session mockSession = mock(Session.class);
        when(mockConnection.getWrappedAmazonSQSClient()).thenReturn(mockWrapper);
        when(mockWrapper.queueExists(anyString())).thenReturn(false);
        when(mockConnection.createSession(false, Session.AUTO_ACKNOWLEDGE)).thenReturn(mockSession);
        sqsProducer.setup();
        sqsProducer.terminate();
        verify(mockSession).close();
        verify(mockConnection).close();
    }
}