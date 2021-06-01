/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
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

/**
 * Mail type, depending on the subject, body, and request the email can be a confirmation, reset password,
 * change email and many others.
 *
 * @author Marco Weiland <m.weiland@radien.io>
 */
public enum MailType {
	CONFIRMATION, 
	RESET_PASSWORD, 
	CHANGE_EMAIL, 
	CONTRACT_REQUEST, 
	CONTRACT_REQUEST_ACCEPT, 
	CONTRACT_REQUEST_DENIED,
	CONTRACT_ASSIGN_USER, 
	CONTRACT_ASSIGN_ADMIN,
}
