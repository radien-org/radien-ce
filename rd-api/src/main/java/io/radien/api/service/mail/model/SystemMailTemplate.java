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
package io.radien.api.service.mail.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import io.radien.api.service.ecm.model.EnterpriseContent;

/**
 * Email templates interface class
 *
 * @author Marco Weiland
 */
public interface SystemMailTemplate extends Serializable {

	/**
	 * Email template content getter
	 * @return the email template
	 */
	EnterpriseContent getContent();

	/**
	 * Email template content setter
	 * @param content to be set
	 */
	void setContent(EnterpriseContent content);

	/**
	 * Email template arguments getter
	 * @return the arguments in the email template
	 */
	Map<String, String> getArgs();

	/**
	 * Email template arguments setter
	 * @param args to be set
	 */
	void setArgs(HashMap<String, String> args);
}
