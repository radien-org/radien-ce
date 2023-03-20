package io.radien.ms.email.lib;

import com.healthmarketscience.rmiio.RemoteInputStream;
import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.mail.model.Mail;
import io.radien.api.service.mail.model.MailContentType;
import io.radien.api.service.mail.model.SystemMailTemplate;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.mail.Transport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MailServiceTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    MailService target;

    @Mock
    MailFactory mailFactory;

    @Mock
    OAFAccess oafAccess;

    private static MockedStatic<Transport> transport;

    @BeforeClass
    public static void beforeClass(){
        transport = Mockito.mockStatic(Transport.class);
    }

    @AfterClass
    public static void destroy(){
        if(transport!=null) {
            transport.close();
        }
    }

    private String getNotificationManagement(){
        String url = "";
        when(oafAccess.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_NOTIFICATIONMANAGEMENT)).thenReturn(url);
        return url;
    }

    @Test
    public void testSend() {
        when(oafAccess.getProperty(OAFProperties.SYS_MAIL_TRANSPORT_PROTOCOL)).thenReturn("https");
        when(oafAccess.getProperty(OAFProperties.SYS_MAIL_HOST)).thenReturn("host");
        when(oafAccess.getProperty(OAFProperties.SYS_MAIL_SMTP_AUTH)).thenReturn("auth");
        when(oafAccess.getProperty(OAFProperties.SYS_MAIL_STARTTLS_ENABLE)).thenReturn("true");
        when(oafAccess.getProperty(OAFProperties.SYS_MAIL_SMTP_PORT)).thenReturn("1234");

        List<String> bCC = new ArrayList<>();
        bCC.add("");
        List<String> tO = new ArrayList<>();
        tO.add("something1@something1.com");
        List<String> cC = new ArrayList<>();
        cC.add("something2@something.com");
        Map<String, RemoteInputStream> attachments = new HashMap<>();

        Mail mail = new MailMessage("something@something", tO,"Subject","Body",
                MailContentType.HTML,attachments,cC,bCC);
        target.send(mail);

        transport.verify(() -> Transport.send(any()));
    }

    @Test
    public void testCreateTwoArgs() {

        List<String> bCC = new ArrayList<>();
        bCC.add("");
        List<String> tO = new ArrayList<>();
        tO.add("something1@something1.com");
        List<String> cC = new ArrayList<>();
        cC.add("something2@something.com");
        Map<String, RemoteInputStream> attachments = new HashMap<>();

        SystemUser user = mock(SystemUser.class);
        user.setId(1L);

        HashMap<String, String> args = new HashMap<>();
        SystemMailTemplate systemMailTemplate = mock(SystemMailTemplate.class);
        systemMailTemplate.setArgs(args);

        Mail mail = new MailMessage("something@something", tO,"Subject","Body",
                MailContentType.HTML,attachments,cC,bCC);
        when(mailFactory.create(user,systemMailTemplate)).thenReturn(mail);
        assertEquals(mail, target.create(user,systemMailTemplate));
    }

    @Test
    public void  testCreateTwoArgsWithString() {
        List<String> bCC = new ArrayList<>();
        bCC.add("");
        List<String> tO = new ArrayList<>();
        tO.add("something1@something1.com");
        List<String> cC = new ArrayList<>();
        cC.add("something2@something.com");
        Map<String, RemoteInputStream> attachments = new HashMap<>();

        HashMap<String, String> args = new HashMap<>();
        SystemMailTemplate systemMailTemplate = mock(SystemMailTemplate.class);
        systemMailTemplate.setArgs(args);

        Mail mail = new MailMessage("something@something", tO,"Subject","Body",
                MailContentType.HTML,attachments,cC,bCC);
        when(mailFactory.create("something123@something.com",systemMailTemplate)).thenReturn(mail);
        assertEquals(mail, target.create("something123@something.com",systemMailTemplate));
    }

    @Test
    public void testCreateTwoAgsWithListUser() {
        List<String> bCC = new ArrayList<>();
        bCC.add("");
        List<String> tO = new ArrayList<>();
        tO.add("something1@something1.com");
        List<String> cC = new ArrayList<>();
        cC.add("something2@something.com");
        Map<String, RemoteInputStream> attachments = new HashMap<>();

        List<SystemUser> systemUserList = new ArrayList<>();

        HashMap<String, String> args = new HashMap<>();
        SystemMailTemplate systemMailTemplate = mock(SystemMailTemplate.class);
        systemMailTemplate.setArgs(args);

        Mail mail = new MailMessage("something@something", tO,"Subject","Body",
                MailContentType.HTML,attachments,cC,bCC);
        when(mailFactory.create(systemUserList,systemMailTemplate)).thenReturn(mail);
        assertEquals(mail, target.create(systemUserList,systemMailTemplate));
    }

    @Test
    public void  testCreateOneArg() {
        List<String> bCC = new ArrayList<>();
        bCC.add("");
        List<String> tO = new ArrayList<>();
        tO.add("something1@something1.com");
        List<String> cC = new ArrayList<>();
        cC.add("something2@something.com");
        Map<String, RemoteInputStream> attachments = new HashMap<>();

        Mail mail = new MailMessage("something@something", tO,"Subject","Body",
                MailContentType.HTML,attachments,cC,bCC);
        when(mailFactory.create("something123@something.com")).thenReturn(mail);
        assertEquals(mail, target.create("something123@something.com"));
    }

    @Test
    public void testCreateFiveArgs() {
        List<String> bCC = new ArrayList<>();
        bCC.add("");
        List<String> tO = new ArrayList<>();
        tO.add("something1@something1.com");
        List<String> cC = new ArrayList<>();
        cC.add("something2@something.com");
        Map<String, RemoteInputStream> attachments = new HashMap<>();

        String from = "from";
        String t1 = "t1";
        String subject = "subject";
        String body = "body";

        HashMap<String, String> args = new HashMap<>();
        SystemMailTemplate systemMailTemplate = mock(SystemMailTemplate.class);
        systemMailTemplate.setArgs(args);

        Mail mail = new MailMessage("something@something", tO,"Subject","Body",
                MailContentType.HTML,attachments,cC,bCC);
        when(mailFactory.create(from,t1,subject,body,MailContentType.HTML)).thenReturn(mail);
        assertEquals(mail, target.create(from,t1,subject,body,MailContentType.HTML));
    }

    @Test
    public void testCreateFiveArgsWithStringList() {
        List<String> bCC = new ArrayList<>();
        bCC.add("");
        List<String> tO = new ArrayList<>();
        tO.add("something1@something1.com");
        List<String> cC = new ArrayList<>();
        cC.add("something2@something.com");
        Map<String, RemoteInputStream> attachments = new HashMap<>();

        String from = "from";
        String subject = "subject";
        String body = "body";

        HashMap<String, String> args = new HashMap<>();
        SystemMailTemplate systemMailTemplate = mock(SystemMailTemplate.class);
        systemMailTemplate.setArgs(args);

        Mail mail = new MailMessage("something@something", tO,"Subject","Body",
                MailContentType.HTML,attachments,cC,bCC);
        when(mailFactory.create(from,tO,subject,body,MailContentType.HTML)).thenReturn(mail);
        assertEquals(mail, target.create(from,tO,subject,body,MailContentType.HTML));
    }

    @Test
    public void testCreateAllArgs() {
        List<String> bCC = new ArrayList<>();
        bCC.add("");
        List<String> tO = new ArrayList<>();
        tO.add("something1@something1.com");
        List<String> cC = new ArrayList<>();
        cC.add("something2@something.com");
        Map<String, RemoteInputStream> attachments = new HashMap<>();

        String from = "from";
        String subject = "subject";
        String body = "body";

        HashMap<String, String> args = new HashMap<>();
        SystemMailTemplate systemMailTemplate = mock(SystemMailTemplate.class);
        systemMailTemplate.setArgs(args);

        Mail mail = new MailMessage("something@something", tO,"Subject","Body",
                MailContentType.HTML,attachments,cC,bCC);
        when(mailFactory.create(from,tO,subject,body,MailContentType.HTML,attachments, cC,bCC)).thenReturn(mail);
        assertEquals(mail, target.create(from,tO,subject,body,MailContentType.HTML,attachments, cC, bCC));
    }
}