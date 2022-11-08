/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.radien.ms.email.lib;

import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.RemoteInputStreamClient;
import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.mail.MailServiceAccess;
import io.radien.api.service.mail.model.Mail;
import io.radien.api.service.mail.model.MailContentType;
import io.radien.api.service.mail.model.SystemMailTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.mail.*;
import javax.mail.internet.*;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Simple Mail Service class, responsible for sending email notifications
 *
 * @author Marco Weiland
 */
@RequestScoped
public class MailService implements MailServiceAccess {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    @Inject
    @Default
    private OAFAccess baseApp;

    @Inject
    private MailFactory mailFactory;

    public void send(Mail mailMessage) {
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", baseApp.getProperty(OAFProperties.SYS_MAIL_TRANSPORT_PROTOCOL));
        props.setProperty("mail.host", baseApp.getProperty(OAFProperties.SYS_MAIL_HOST));
        props.setProperty("mail.smtp.auth", baseApp.getProperty(OAFProperties.SYS_MAIL_SMTP_AUTH));
        props.setProperty("mail.smtp.starttls.enable", baseApp.getProperty(OAFProperties.SYS_MAIL_STARTTLS_ENABLE));
        props.setProperty("mail.smtp.port", baseApp.getProperty(OAFProperties.SYS_MAIL_SMTP_PORT));
        props.setProperty("mail.debug",Boolean.toString(log.isInfoEnabled()));
        Session session = Session.getDefaultInstance(props, new SMTPAuthenticator());
        session.setDebug(log.isInfoEnabled());

        List<File> deleteFiles = new ArrayList<>();
        try {
            // create a message
            Message message = new MimeMessage(session);
            // set the from
            message.setFrom(new InternetAddress(mailMessage.getFrom()));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(String.join(",", mailMessage.getTO())));
            if (mailMessage.getCC() != null) {
                message.setRecipients(Message.RecipientType.CC,
                        InternetAddress.parse(String.join(",", mailMessage.getCC())));
            }
            if (mailMessage.getBCC() != null) {
                message.setRecipients(Message.RecipientType.BCC,
                        InternetAddress.parse(String.join(",", mailMessage.getBCC())));
            }

            message.setSubject(MimeUtility.encodeText(mailMessage.getSubject(), "utf-8", "B"));

            // create the Multipart
            Multipart multiPart = new MimeMultipart();


            // send a multipart message// create and fill the first message part
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            if(mailMessage.getContentType() == MailContentType.HTML) {
                mimeBodyPart.setContent(mailMessage.getBody(), MediaType.TEXT_HTML+";charset="+StandardCharsets.UTF_8.name());
            } else {
                mimeBodyPart.setText(mailMessage.getBody(), StandardCharsets.UTF_8.toString(), mailMessage.getContentType().type());
            }
            multiPart.addBodyPart(mimeBodyPart);

            File file;
            if (mailMessage.getAttachments() != null && !mailMessage.getAttachments().isEmpty()) {
                for (Map.Entry<String, RemoteInputStream> stringRemoteInputStreamEntry : mailMessage.getAttachments()
                        .entrySet()) {

                    file = new File((String) ((Map.Entry) stringRemoteInputStreamEntry).getKey());
                    deleteFiles.add(file);

                    try (InputStream is = RemoteInputStreamClient
                            .wrap((RemoteInputStream) ((Map.Entry) stringRemoteInputStreamEntry).getValue());
                         BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file, false))) {

                        byte[] buff = new byte[8192];
                        int len;
                        while (0 < (len = is.read(buff))) {
                            os.write(buff, 0, len);
                        }

                        MimeBodyPart mbpFile = new MimeBodyPart();
                        DataSource source = new FileDataSource(file);
                        mbpFile.setDataHandler(new DataHandler(source));
                        mbpFile.setFileName(file.getName());
                        multiPart.addBodyPart(mbpFile);

                    } catch (Exception e) {
                        log.error("Error sending email", e);
                        throw e;
                    }

                }
            }


            // add the Multipart to the message
            message.setContent(multiPart);

            log.info("Initiating Mail transport");
            Transport.send(message);
            log.info("Finished Mail transport");
        } catch (Exception mex) {
            log.error("Error sending email", mex);

        } finally {
            for (File file : deleteFiles) {
                boolean isDeleted = file.delete();
                if (isDeleted) {
                    log.info("file deleted : {}", file.getName());
                }
            }
        }

    }

    @Override
    public void sendMailAsync(Mail mailMessage) {
        ExecutorService emailExecutor = Executors.newSingleThreadExecutor();
        emailExecutor.execute(() -> send(mailMessage));
        emailExecutor.shutdown();
    }

    private class SMTPAuthenticator extends javax.mail.Authenticator {
        public PasswordAuthentication getPasswordAuthentication() {
            String username = baseApp.getProperty(OAFProperties.SYS_MAIL_USER);
            String password = baseApp.getProperty(OAFProperties.SYS_MAIL_PASSWORD);
            return new PasswordAuthentication(username, password);
        }
    }

    @Override
    public Mail create(SystemUser user, SystemMailTemplate template){
        return mailFactory.create(user, template);
    }

    @Override
    public Mail create(String tO, SystemMailTemplate template){
        return mailFactory.create(tO, template);
    }

    @Override
    public Mail create(List<SystemUser> users, SystemMailTemplate template){
        return mailFactory.create(users, template);
    }

    @Override
    public Mail create(String tO){
        return mailFactory.create(tO);
    }

    @Override
    public Mail create(String from, String tO, String subject, String body, MailContentType contentType){
        return mailFactory.create(from, tO, subject, body, contentType);
    }

    @Override
    public Mail create(String from, List<String> tO, String subject, String body, MailContentType contentType){
        return mailFactory.create(from, tO, subject, body, contentType);
    }

    @Override
    public Mail create(String from, List<String> tO, String subject, String body, MailContentType contentType,
                       Map<String, RemoteInputStream> attachments, List<String> cC, List<String> bCC){
        return mailFactory.create(from, tO, subject, body, contentType, attachments, cC, bCC);
    }

}

