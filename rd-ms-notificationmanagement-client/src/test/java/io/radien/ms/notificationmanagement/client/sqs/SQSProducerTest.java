package io.radien.ms.notificationmanagement.client.sqs;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.service.notification.email.EmailNotificationRESTServiceAccess;
import io.radien.exception.SystemException;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.jms.JMSException;
import java.util.HashMap;
import java.util.Map;

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

    @Test
    public void testEmailNotificationLocal() throws SystemException {
        String email = "e@mail.com";
        String viewId = "viewId";
        String language = "language";
        Map<String, String> arguments = new HashMap<>();
        when(oaf.getProperty(OAFProperties.RADIEN_ENV)).thenReturn("LOCAL");
        sqsProducer.emailNotification(email, viewId, language, arguments);
        verify(notificationService).notify(email, viewId, language, arguments);
    }

    @Test
    public void testEmailNotification() throws SystemException, JMSException {
        String email = "e@mail.com";
        String viewId = "viewId";
        String language = "language";
        Map<String, String> arguments = new HashMap<>();
        when(oaf.getProperty(OAFProperties.RADIEN_ENV)).thenReturn("NOT LOCAL");

        assertTrue(sqsProducer.emailNotification(email, viewId, language, arguments));
        verify(notificationService, times(0)).notify(email, viewId, language, arguments);
    }
}