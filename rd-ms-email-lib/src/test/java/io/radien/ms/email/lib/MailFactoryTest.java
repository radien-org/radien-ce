package io.radien.ms.email.lib;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.mail.model.Mail;
import io.radien.api.service.mail.model.MailContentType;
import io.radien.api.service.mail.model.SystemMailTemplate;
import java.util.Arrays;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MailFactoryTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    MailFactory target;

    @Mock
    OAFAccess oafAccess;

    @Test
    public void testCreateTwoArgsWithString() {
        List<String> bCC = new ArrayList<>();
        bCC.add("");
        List<String> tO = new ArrayList<>();
        tO.add("something1@something1.com");
        List<String> cC = new ArrayList<>();
        cC.add("something2@something.com");

        List<String> targetEmail1 = new ArrayList<>(Collections.singleton("something1@something1.com"));
        String targetEmail = "something1@something1.com";

        HashMap<String, String> args = new HashMap<>();
        SystemMailTemplate systemMailTemplate = mock(SystemMailTemplate.class);

        EnterpriseContent enterpriseContent = Mockito.mock(EnterpriseContent.class);
        when(systemMailTemplate.getArgs()).thenReturn(args);
        when(systemMailTemplate.getContent()).thenReturn(enterpriseContent);
        when(enterpriseContent.getHtmlContent()).thenReturn("Body");
        when(enterpriseContent.getName()).thenReturn("subject");

        MailMessage mailMessage = mock(MailMessage.class);
        when(mailMessage.getBCC()).thenReturn(bCC);
        when(mailMessage.getCC()).thenReturn(cC);
        when(mailMessage.getFrom()).thenReturn("something@something");

        when(oafAccess.getProperty(OAFProperties.SYS_MAIL_FROM_SYSTEM_ADMIN)).thenReturn("something@something");

        MailContentType mailContentType = Mockito.mock(MailContentType.class);
        when(mailMessage.getContentType()).thenReturn(MailContentType.HTML);

        Mail resultMail = target.create(targetEmail,systemMailTemplate);

        AbstractMailFactory mailFactory = Mockito.mock(AbstractMailFactory.class);
        when(mailFactory.create(mailMessage.getFrom(),tO,"subject",null, "Body",mailContentType,null,
                mailMessage.getCC(),mailMessage.getBCC())).thenReturn(resultMail);

        resultMail.setCC(mailMessage.getCC());
        resultMail.setBCC(mailMessage.getBCC());

        assertEquals(mailMessage.getFrom(), resultMail.getFrom());
        assertEquals(targetEmail1, resultMail.getTO());
        assertEquals(enterpriseContent.getName(), resultMail.getSubject());
        assertEquals(mailMessage.getContentType(), resultMail.getContentType());
        assertNull(resultMail.getAttachments());
        assertEquals(mailMessage.getCC(),resultMail.getCC());
        assertEquals(mailMessage.getBCC(), resultMail.getBCC());
    }

    @Test
    public void testCreateWithEmailList() {
        List<String> bCC = new ArrayList<>();
        bCC.add("");
        List<String> tO = new ArrayList<>();
        tO.add("something1@something1.com");
        List<String> cC = new ArrayList<>();
        cC.add("something2@something.com");

        List<String> targetEmail1 = new ArrayList<>(Arrays.asList("something1@something1.com", "something2@something2.com"));

        HashMap<String, String> args = new HashMap<>();
        SystemMailTemplate systemMailTemplate = mock(SystemMailTemplate.class);

        EnterpriseContent enterpriseContent = Mockito.mock(EnterpriseContent.class);
        when(systemMailTemplate.getArgs()).thenReturn(args);
        when(systemMailTemplate.getContent()).thenReturn(enterpriseContent);
        when(enterpriseContent.getHtmlContent()).thenReturn("Body");
        when(enterpriseContent.getName()).thenReturn("subject");

        MailMessage mailMessage = mock(MailMessage.class);
        when(mailMessage.getBCC()).thenReturn(bCC);
        when(mailMessage.getCC()).thenReturn(cC);
        when(mailMessage.getFrom()).thenReturn("something@something");

        when(oafAccess.getProperty(OAFProperties.SYS_MAIL_FROM_SYSTEM_ADMIN)).thenReturn("something@something");

        MailContentType mailContentType = Mockito.mock(MailContentType.class);
        when(mailMessage.getContentType()).thenReturn(MailContentType.HTML);

        Mail resultMail = target.create(systemMailTemplate, targetEmail1);

        AbstractMailFactory mailFactory = Mockito.mock(AbstractMailFactory.class);
        when(mailFactory.create(mailMessage.getFrom(),tO,"subject",null, "Body",mailContentType,null,
                mailMessage.getCC(),mailMessage.getBCC())).thenReturn(resultMail);

        resultMail.setCC(mailMessage.getCC());
        resultMail.setBCC(mailMessage.getBCC());

        assertEquals(mailMessage.getFrom(), resultMail.getFrom());
        assertEquals(targetEmail1, resultMail.getTO());
        assertEquals(enterpriseContent.getName(), resultMail.getSubject());
        assertEquals(mailMessage.getContentType(), resultMail.getContentType());
        assertNull(resultMail.getAttachments());
        assertEquals(mailMessage.getCC(),resultMail.getCC());
        assertEquals(mailMessage.getBCC(), resultMail.getBCC());
    }

    @Test
    public void testCreateWithUser() {
        SystemUser mockUser = mock(SystemUser.class);
        when(mockUser.getUserEmail()).thenReturn("user@email.com");
        List<String> bCC = new ArrayList<>();
        bCC.add("");
        List<String> tO = new ArrayList<>();
        tO.add("something1@something1.com");
        List<String> cC = new ArrayList<>();
        cC.add("something2@something.com");

        HashMap<String, String> args = new HashMap<>();
        SystemMailTemplate systemMailTemplate = mock(SystemMailTemplate.class);

        EnterpriseContent enterpriseContent = Mockito.mock(EnterpriseContent.class);
        when(systemMailTemplate.getArgs()).thenReturn(args);
        when(systemMailTemplate.getContent()).thenReturn(enterpriseContent);
        when(enterpriseContent.getHtmlContent()).thenReturn("Body");
        when(enterpriseContent.getName()).thenReturn("subject");

        MailMessage mailMessage = mock(MailMessage.class);
        when(mailMessage.getBCC()).thenReturn(bCC);
        when(mailMessage.getCC()).thenReturn(cC);
        when(mailMessage.getFrom()).thenReturn("something@something");

        when(oafAccess.getProperty(OAFProperties.SYS_MAIL_FROM_SYSTEM_ADMIN)).thenReturn("something@something");

        MailContentType mailContentType = Mockito.mock(MailContentType.class);
        when(mailMessage.getContentType()).thenReturn(MailContentType.HTML);

        Mail resultMail = target.create(mockUser,  systemMailTemplate);

        AbstractMailFactory mailFactory = Mockito.mock(AbstractMailFactory.class);
        when(mailFactory.create(mailMessage.getFrom(),tO,"subject",null, "Body",mailContentType,null,
                mailMessage.getCC(),mailMessage.getBCC())).thenReturn(resultMail);

        resultMail.setCC(mailMessage.getCC());
        resultMail.setBCC(mailMessage.getBCC());

        assertEquals(mailMessage.getFrom(), resultMail.getFrom());
        assertEquals("user@email.com", resultMail.getTO().get(0));
        assertEquals(enterpriseContent.getName(), resultMail.getSubject());
        assertEquals(mailMessage.getContentType(), resultMail.getContentType());
        assertNull(resultMail.getAttachments());
        assertEquals(mailMessage.getCC(),resultMail.getCC());
        assertEquals(mailMessage.getBCC(), resultMail.getBCC());
    }

    @Test
    public void testCreateWithListUser() {
        List<SystemUser> userList = new ArrayList<>();
        SystemUser mockUser1 = mock(SystemUser.class);
        when(mockUser1.getUserEmail()).thenReturn("user1@email.com");
        SystemUser mockUser2 = mock(SystemUser.class);
        when(mockUser2.getUserEmail()).thenReturn("user2@email.com");
        userList.add(mockUser1);
        userList.add(mockUser2);
        List<String> bCC = new ArrayList<>();
        bCC.add("");
        List<String> tO = new ArrayList<>();
        tO.add("something1@something1.com");
        List<String> cC = new ArrayList<>();
        cC.add("something2@something.com");

        HashMap<String, String> args = new HashMap<>();
        SystemMailTemplate systemMailTemplate = mock(SystemMailTemplate.class);

        EnterpriseContent enterpriseContent = Mockito.mock(EnterpriseContent.class);
        when(systemMailTemplate.getArgs()).thenReturn(args);
        when(systemMailTemplate.getContent()).thenReturn(enterpriseContent);
        when(enterpriseContent.getHtmlContent()).thenReturn("Body");
        when(enterpriseContent.getName()).thenReturn("subject");

        MailMessage mailMessage = mock(MailMessage.class);
        when(mailMessage.getBCC()).thenReturn(bCC);
        when(mailMessage.getCC()).thenReturn(cC);
        when(mailMessage.getFrom()).thenReturn("something@something");

        when(oafAccess.getProperty(OAFProperties.SYS_MAIL_FROM_SYSTEM_ADMIN)).thenReturn("something@something");

        MailContentType mailContentType = Mockito.mock(MailContentType.class);
        when(mailMessage.getContentType()).thenReturn(MailContentType.HTML);

        Mail resultMail = target.create(userList,  systemMailTemplate);

        AbstractMailFactory mailFactory = Mockito.mock(AbstractMailFactory.class);
        when(mailFactory.create(mailMessage.getFrom(),tO,"subject",null, "Body",mailContentType,null,
                mailMessage.getCC(),mailMessage.getBCC())).thenReturn(resultMail);

        resultMail.setCC(mailMessage.getCC());
        resultMail.setBCC(mailMessage.getBCC());

        assertEquals(mailMessage.getFrom(), resultMail.getFrom());
        assertEquals("user1@email.com", resultMail.getTO().get(0));
        assertEquals("user2@email.com", resultMail.getTO().get(1));
        assertEquals(enterpriseContent.getName(), resultMail.getSubject());
        assertEquals(mailMessage.getContentType(), resultMail.getContentType());
        assertNull(resultMail.getAttachments());
        assertEquals(mailMessage.getCC(),resultMail.getCC());
        assertEquals(mailMessage.getBCC(), resultMail.getBCC());
    }

    /**
     * Test to attempt to connect with the OAF
     */
    @Test
    public void testAccessingOAF() {
        OAFAccess oafAccess = target.getOAF();
        assertNotNull(oafAccess);
    }
}