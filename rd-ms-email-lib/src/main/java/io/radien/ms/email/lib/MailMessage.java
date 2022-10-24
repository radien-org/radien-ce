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
import io.radien.api.service.mail.model.Mail;
import io.radien.api.service.mail.model.MailContentType;
import io.radien.api.service.mail.model.SystemMailTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;

import java.util.List;
import java.util.Map;

/**
 * @author Marco Weiland
 */
public class MailMessage implements Mail {
	private static final Logger log = LoggerFactory.getLogger(MailMessage.class);
	private static final long serialVersionUID = 1L;
	public String from;
	public String subject;
	public String body;
	public MailContentType contentType;
	private List<String> tO;
	private List<String> cC;
	private List<String> bCC;
	private Map<String, RemoteInputStream> attachments;

	public MailMessage(String from, List<String> tO, String subject, String body, MailContentType contentType,
                       Map<String, RemoteInputStream> attachments, List<String> cC, List<String> bCC) {
		this.from = from;
		this.tO = tO;
		this.subject = subject;
		this.body = body;
		this.contentType = contentType;
		this.attachments = attachments;
		this.cC = cC;
		this.bCC = bCC;

	}

	public static String of(SystemMailTemplate template) {
		return generateMessage(template.getContent().getHtmlContent(), template.getArgs());
	}

	public static String createSubject(SystemMailTemplate template) {
		return generateMessage(template.getContent().getName(), template.getArgs());
	}

	private static String generateMessage(String htmlContent, Map<String, String> args) {
		ST template = new ST(htmlContent,'$','$');
		for (String key : args.keySet()) {
			try {
				template.add(key, args.get(key));
			} catch (Exception e) {
				log.error("[MailMessage] : Could not substitute dynamic attribute for key: {} with value: {}",key,args.get(key));
			}

		}
		return template.render();
	}

	public List<String> getTO() {
		return tO;
	}

	public void setTO(List<String> tO) {
		this.tO = tO;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public MailContentType getContentType() {
		return contentType;
	}

	public void setContentType(MailContentType contentType) {
		this.contentType = contentType;
	}

	public Map<String, RemoteInputStream> getAttachments() {
		return attachments;
	}

	public void setAttachments(Map<String, RemoteInputStream> attachments) {
		this.attachments = attachments;
	}

	public List<String> getCC() {
		return cC;
	}

	public void setCC(List<String> cC) {
		this.cC = cC;
	}

	public List<String> getBCC() {
		return bCC;
	}

	public void setBCC(List<String> bCC) {
		this.bCC = bCC;
	}

	public void addTOAdresse(String toAddress) {
		tO.add(toAddress);
	}

	public void addCCAdresse(String ccAddress) {
		cC.add(ccAddress);
	}

	public void addBCCAdresse(String bccAddress) {
		bCC.add(bccAddress);
	}

	@Override
	public String toString() {
		return "MailMessage{" +
				"from='" + from + '\'' +
				", subject='" + subject + '\'' +
				", body='" + body + '\'' +
				", contentType=" + contentType +
				", tO=" + tO +
				", cC=" + cC +
				", bCC=" + bCC +
				", attachments=" + attachments +
				'}';
	}
}
