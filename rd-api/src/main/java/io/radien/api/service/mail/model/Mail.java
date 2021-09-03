/*

	Copyright (c) 2021-present radien GmbH. All rights reserved.

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
package io.radien.api.service.mail.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.healthmarketscience.rmiio.RemoteInputStream;

/**
 * Email object with all the properties to be sent to whoever is requested
 *
 * @author Marco Weiland
 */
public interface Mail extends Serializable {

	/**
	 * Mail address to whom we are going to send the email (Only one receiver)
	 * @param toAddress email address
	 */
	void addTOAdresse(String toAddress);

	/**
	 * Mail address to whom we are going to send the email in cc (Only one receiver)
	 * @param ccAddress email address
	 */
	void addCCAdresse(String ccAddress);

	/**
	 * Mail address to whom we are going to send the email in bcc (Only one receiver)
	 * @param bccAddress email address
	 */
	void addBCCAdresse(String bccAddress);

	/**
	 * Gets the Mail addresses to whom we are going to send the email (Multiple receiver)
	 * @return a list of email addresses
	 */
	List<String> getTO();

	/**
	 * Gets the Mail addresses to whom we are going to send the email (Multiple receiver)
	 * @param to list of email addresses to send
	 */
	void setTO(List<String> to);

	/**
	 * Gets the Mail addresses to whom we are going to send the email in CC (Multiple receiver)
	 * @return a list of email addresses
	 */
	List<String> getCC();

	/**
	 * Gets the Mail addresses to whom we are going to send the email in CC (Multiple receiver)
	 * @param cc list of email addresses to send in cc
	 */
	void setCC(List<String> cc);

	/**
	 * Gets the Mail addresses to whom we are going to send the email in BCC (Multiple receiver)
	 * @return a list of email addresses
	 */
	List<String> getBCC();

	/**
	 * Gets the Mail addresses to whom we are going to send the email in BCC (Multiple receiver)
	 * @param bcc list of email addresses to send in cc
	 */
	void setBCC(List<String> bcc);

	/**
	 * Get the value of the email sender
	 * @return the email of the sender
	 */
	String getFrom();

	/**
	 * Sets the email of the sender
	 * @param from should the email come from
	 */
	void setFrom(String from);

	/**
	 * Email subject getter
	 * @return the email subject
	 */
	String getSubject();

	/**
	 * Email subject setter
	 * @param subject to be set
	 */
	void setSubject(String subject);

	/**
	 * Email body getter
	 * @return the email body
	 */
	String getBody();

	/**
	 * Email body setter
	 * @param body to be set or added
	 */
	void setBody(String body);

	/**
	 * Email content type getter
	 * @return the email subject
	 */
	MailContentType getContentType();

	/**
	 * Email content type setter
	 * @param contentType to be set
	 */
	void setContentType(MailContentType contentType);

	/**
	 * Email attachments getter
	 * @return the email attachments
	 */
	Map<String, RemoteInputStream> getAttachments();

	/**
	 * Email attachments setter
	 * @param attachments to be set
	 */
	void setAttachments(Map<String, RemoteInputStream> attachments);
}
