/*

	Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.

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
package io.radien.api.service.mail;

import java.util.List;
import java.util.Map;

import com.healthmarketscience.rmiio.RemoteInputStream;

import io.radien.api.model.user.SystemUser;
import io.radien.api.service.ServiceAccess;
import io.radien.api.service.mail.model.Mail;
import io.radien.api.service.mail.model.MailContentType;
import io.radien.api.service.mail.model.SystemMailTemplate;

/**
 * Mail Service Access interface class
 *
 * @author Marco Weiland
 */
public interface MailServiceAccess extends ServiceAccess {

	/**
	 * Mail service send email message
	 * @param mailMessage to be sent
	 * @throws Exception in case of any issue while attempting sending the email
	 */
	void send(Mail mailMessage) throws Exception;

	/**
	 * Send email asynchronous to receivers
	 * @param mailMessage to be sent
	 */
	void sendMailAsync(Mail mailMessage);

	/**
	 * Mail service creation constructor
	 * @param users list of users to be sent
	 * @param template to be used
	 * @return the mail object
	 */
	Mail create(List<SystemUser> users, SystemMailTemplate template);

	/**
	 * Mail service creation constructor
	 * @param user user to be sent
	 * @param template to be used
	 * @return the mail object
	 */
	Mail create(SystemUser user, SystemMailTemplate template);

	/**
	 * Mail service creation constructor
	 * @param tO whom should the email be sent
	 * @return email object to be sent
	 */
	Mail create(String tO);

	/**
	 * Mail service creation constructor
	 * @param from should the email be sent
	 * @param tO whom the email should be sent
	 * @param subject of the email
	 * @param body of the email
	 * @param contentType of the email
	 * @return email object to be sent
	 */
	Mail create(String from, String tO, String subject, String body, MailContentType contentType);

	/**
	 * Mail service creation constructor
	 * @param from should the email be sent
	 * @param tO whom should the email be sent
	 * @param subject of the email
	 * @param body of the email
	 * @param contentType of the email
	 * @return email object to be sent
	 */
	Mail create(String from, List<String> tO, String subject, String body, MailContentType contentType);

	/**
	 * Mail service creation constructor
	 * @param from should the email be sent
	 * @param tO whom should the email be sent
	 * @param subject of the email
	 * @param body of the email
	 * @param contentType of the email
	 * @param attachments in the email
	 * @param cC to be sent to
	 * @param bCC to be sent to
	 * @return email object to be sent
	 */
	Mail create(String from, List<String> tO, String subject, String body, MailContentType contentType,
				Map<String, RemoteInputStream> attachments, List<String> cC, List<String> bCC);
}