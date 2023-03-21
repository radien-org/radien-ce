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



import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.mail.model.Mail;
import io.radien.api.service.mail.model.MailContentType;
import io.radien.api.service.mail.model.SystemMailTemplate;

import java.text.MessageFormat;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import org.apache.geronimo.mail.util.Base64;

/**
 * Default implementation of the {@link AbstractMailFactory}
 *
 * @author Marco Weiland
 */
public @RequestScoped
class MailFactory extends AbstractMailFactory {

    @Inject
    private OAFAccess baseApp;

    @Override
    protected String getAnchorStyle() {
        return "a  color: #5D5D9E; }" +
                "\n" +
                "a:hover { color: #B2B3B4; }";
    }

    @Override
    protected String getHTMLBodyStyle() {
        return "body { width: 100%;  min-height: 70px; max-width: 1024px; margin: 10px; display: block; }";
    }

    @Override
    protected String getImgStyle() {
        return "img { display: block; margin: auto; }";
    }

    @Override
    protected String getContentStyle() {
        return "#subject { display: block; text-align: center; font-size: 14pt; font-family: Segoe, 'Segoe UI', 'Helvetica Neue', sans-serif; font-weight: bold; margin-bottom: 1em; } " +
                "\n" +
                "#body { font-size: 12pt; font-family: Segoe, 'Segoe UI', 'Helvetica Neue', sans-serif; line-height: 1.5; }";
    }

    @Override
    protected String getHTMLContentWrapperStyle() {
        return ".content-wrapper { min-height: 70px; max-width: 1024px;}";
    }

    @Override
    protected String getHTMLFooterStyle() {
        return ".footer { font-size: 10pt; font-family: Segoe, 'Segoe UI', 'Helvetica Neue', sans-serif; margin-top: 3em; }";
    }



    @Override
    public Mail create(SystemUser user, SystemMailTemplate template) {
        String email = user.getUserEmail() == null ? user.getLogon() : user.getUserEmail();
        String logoHtml = getLogoHtml(template);
        return create(baseApp.getProperty(OAFProperties.SYS_MAIL_FROM_SYSTEM_ADMIN), email,
                MailMessage.createSubject(template), logoHtml, MailMessage.of(template), MailContentType.HTML);
    }

    @Override
    public Mail create(String targetEmail, SystemMailTemplate template) {
        String logoHtml = getLogoHtml(template);
        return create(baseApp.getProperty(OAFProperties.SYS_MAIL_FROM_SYSTEM_ADMIN), targetEmail,
                MailMessage.createSubject(template), logoHtml, MailMessage.of(template), MailContentType.HTML);
    }

    @Override
    public Mail create(List<SystemUser> user, SystemMailTemplate template) {
        String logoHtml = getLogoHtml(template);
        List<String> to = new ArrayList<>();
        user.forEach(u -> to.add(u.getUserEmail()));
        return create(baseApp.getProperty(OAFProperties.SYS_MAIL_FROM_SYSTEM_ADMIN), to,
                MailMessage.createSubject(template), logoHtml, MailMessage.of(template), MailContentType.HTML);
    }

    @Override
    public Mail create(SystemMailTemplate template, List<String> receiverEmails) {
        String logoHtml = getLogoHtml(template);
        return create(baseApp.getProperty(OAFProperties.SYS_MAIL_FROM_SYSTEM_ADMIN), receiverEmails,
                MailMessage.createSubject(template), logoHtml, MailMessage.of(template), MailContentType.HTML);
    }

    @Override
    protected OAFAccess getOAF() {
        return baseApp;
    }

    private static String getLogoHtml(SystemMailTemplate template) {
        return template.getContent().getImage() == null ? null : MessageFormat.format("<img src=\"data:image/png;base64,{0}\" />",
                new String(Base64.encode(template.getContent().getImage())));
    }
}
