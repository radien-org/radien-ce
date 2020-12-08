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

import io.radien.api.model.identity.SystemUser;
import io.radien.api.service.ServiceAccess;
import io.radien.api.service.mail.model.Mail;
import io.radien.api.service.mail.model.MailContentType;
import io.radien.api.service.mail.model.SystemMailTemplate;

/**
 * @author Marco Weiland
 */
public interface MailServiceAccess extends ServiceAccess {
	void send(Mail mailMessage) throws Exception;

	void sendMailAsync(Mail mailMessage);

	Mail create(List<SystemUser> users, SystemMailTemplate template);

	Mail create(SystemUser user, SystemMailTemplate template);

	Mail create(String tO);

	Mail create(String from, String tO, String subject, String body, MailContentType contentType);

	Mail create(String from, List<String> tO, String subject, String body, MailContentType contentType);

	Mail create(String from, List<String> tO, String subject, String body, MailContentType contentType,
				Map<String, RemoteInputStream> attachments, List<String> cC, List<String> bCC);
}
