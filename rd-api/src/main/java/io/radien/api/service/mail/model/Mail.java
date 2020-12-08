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
package io.radien.api.service.mail.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.healthmarketscience.rmiio.RemoteInputStream;

/**
 * @author Marco Weiland
 */
public interface Mail extends Serializable {

	void addTOAdresse(String toAddress);

	void addCCAdresse(String ccAddress);

	void addBCCAdresse(String bccAddress);

	List<String> getTO();

	void setTO(List<String> to);

	List<String> getCC();

	void setCC(List<String> cc);

	List<String> getBCC();

	void setBCC(List<String> bcc);

	String getFrom();

	void setFrom(String from);

	String getSubject();

	void setSubject(String subject);

	String getBody();

	void setBody(String body);

	MailContentType getContentType();

	void setContentType(MailContentType contentType);

	Map<String, RemoteInputStream> getAttachments();

	void setAttachments(Map<String, RemoteInputStream> attachments);
}
