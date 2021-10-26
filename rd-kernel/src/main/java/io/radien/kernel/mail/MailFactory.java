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
package io.radien.kernel.mail;



import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.mail.model.Mail;
import io.radien.api.service.mail.model.MailContentType;
import io.radien.api.service.mail.model.SystemMailTemplate;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

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
    protected String getHTMLBodyStyle() {
        return "body { margin: 0px; padding: 0px; background: #292929 ; font-family: Arial, Helvetica, sans-serif; color: #5B5B5B; }";
    }

    @Override
    protected String getHTMLContentWrapperStyle() {
        return ".content-wrapper { border: 1px solid #000; background-color: white; font-family: Verdana,Arial,sans-serif; width: 80%; margin: 0 auto; }";
    }

    @Override
    protected String getHTMLFooterStyle() {
        return ".footer { font-family: Verdana,Arial,sans-serif; font-size: 10px; }";
    }



    @Override
    public Mail create(SystemUser user, SystemMailTemplate template) {
        String email = user.getUserEmail() == null ? user.getLogon() : user.getUserEmail();
        return create(baseApp.getProperty(OAFProperties.SYS_MAIL_FROM_SYSTEM_ADMIN), email,
                MailMessage.createSubject(template), MailMessage.of(template), MailContentType.HTML);
    }

    @Override
    public Mail create(String targetEmail, SystemMailTemplate template) {
                return create(baseApp.getProperty(OAFProperties.SYS_MAIL_FROM_SYSTEM_ADMIN), targetEmail,
                MailMessage.createSubject(template), MailMessage.of(template), MailContentType.HTML);
    }

    @Override
    public Mail create(List<SystemUser> user, SystemMailTemplate template) {
        List<String> to = new ArrayList<>();
        user.forEach(u -> to.add(u.getUserEmail()));
        return create(baseApp.getProperty(OAFProperties.SYS_MAIL_FROM_SYSTEM_ADMIN), to,
                MailMessage.createSubject(template), MailMessage.of(template), MailContentType.HTML);
    }

    @Override
    public Mail create(SystemMailTemplate template, List<String> receiverEmails) {
        return create(baseApp.getProperty(OAFProperties.SYS_MAIL_FROM_SYSTEM_ADMIN), receiverEmails,
                MailMessage.createSubject(template), MailMessage.of(template), MailContentType.HTML);
    }

    @Override
    protected OAFAccess getOAF() {
        return baseApp;
    }

}
