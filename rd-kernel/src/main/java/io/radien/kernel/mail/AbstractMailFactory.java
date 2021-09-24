/*

	Copyright (c) 2006-present radien.io & its legal owners. All rights reserved.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

 */
package io.radien.kernel.mail;

import com.healthmarketscience.rmiio.RemoteInputStream;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.mail.model.Mail;
import io.radien.api.service.mail.model.MailContentType;
import io.radien.api.service.mail.model.SystemMailTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Abstract Factory class responsible for creating system emails
 *
 * @author Marco Weiland
 */
public abstract class AbstractMailFactory {

	private static final Logger log = LoggerFactory.getLogger(AbstractMailFactory.class);
	private static final String MAILFROM_MUST_NOT_BE_EMPTY = "MAILFROM MUST NOT BE EMPTY!";

	public Mail create(String tO) {
		return create(getOAF().getProperty(OAFProperties.SYS_MAIL_FROM_SYSTEM_ADMIN), tO,
				getOAF().getProperty(OAFProperties.SYS_MAIL_SUBJECT),
				getOAF().getProperty(OAFProperties.SYS_MAIL_BODY), MailContentType.HTML);
	}

	public Mail create(String from, String tO, String subject, String body, MailContentType contentType) {

		List<String> tOList = new ArrayList<>();
		if (tO != null && !tO.isEmpty()) {
			tOList.add(tO);
		}

		return create(from, tOList, subject, body, contentType);
	}

	public Mail create(String from, List<String> tO, String subject, String body, MailContentType contentType) {

		return create(from, tO, subject, body, contentType, null, null, null);
	}

	public Mail create(String from, List<String> tO, String subject, String body, MailContentType contentType,
			Map<String, RemoteInputStream> attachments, List<String> cC, List<String> bCC) {

		handleParameters(from, tO, subject, body, contentType, cC, bCC);

		return new MailMessage(from, tO, subject, body, contentType, attachments, cC, bCC);
	}

	private void handleParameters(String from, List<String> tO, String subject, String body,
			MailContentType contentType, List<String> cC, List<String> bCC) {

		if (from == null) {
			throw new IllegalArgumentException(MAILFROM_MUST_NOT_BE_EMPTY);
		}

		if (subject == null || subject.equalsIgnoreCase("")) {
			throw new IllegalArgumentException(MAILFROM_MUST_NOT_BE_EMPTY);
		}

		if (body == null || body.equalsIgnoreCase("")) {
			throw new IllegalArgumentException(MAILFROM_MUST_NOT_BE_EMPTY);
		}

		if (contentType == null) {
			contentType = MailContentType.HTML;
		}

		if (contentType == MailContentType.HTML) {
			body = getBodyMessage(body);
		}

		if (tO == null || tO.isEmpty()) {
			throw new IllegalArgumentException("MAILTO MUST NOT BE EMPTY!");
		}
		if (cC == null || cC.isEmpty()) {
			cC = new ArrayList<>();
		}
		if (bCC == null || bCC.isEmpty()) {
			bCC = new ArrayList<>();
		}

		log.info("Email parameters: body[{}], cc[{}], bcc[{}]", body, cC, bCC);
	}

	private String getBodyMessage(String htmlMessage) {

		// GENERIC
		return ("<html><head><style type=\"text/css\">" + getHTMLBodyStyle() + getHTMLContentWrapperStyle()
				+ getHTMLFooterStyle() + "</style></head><body>"
				+ "<div class=\"content-wrapper\"><table><tr><td><img src=\"cid:image\"></td>"
				+ "</tr><tr><td></td></tr><tr><td>" + htmlMessage + "</td></tr><tr>"
				+ "<td></td></tr><tr><td><center><div class=\"footer\">" + "&copy;&nbsp;openappframe.org <br>"
				+ "</div></center></td></tr></table></div></body></html>");
	}

    protected abstract OAFAccess getOAF();

	protected abstract String getHTMLBodyStyle();

	protected abstract String getHTMLContentWrapperStyle();

	protected abstract String getHTMLFooterStyle();

	public abstract Mail create(SystemUser user, SystemMailTemplate template);

	public abstract Mail create(String targetEmail, SystemMailTemplate template);

	public abstract Mail create(List<SystemUser> user, SystemMailTemplate template);

	public abstract Mail create(SystemMailTemplate template, List<String> receiverEmails);
}
