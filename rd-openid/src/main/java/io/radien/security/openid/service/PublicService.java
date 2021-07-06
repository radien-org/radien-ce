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
package io.radien.security.openid.service;

import org.springframework.stereotype.Service;

/**
 * @author Marco Weiland
 */
@Service
public class PublicService {
	private static final String PUBLIC_SERVICE_RESPONSE_TEXT = "This service available for all users including unauthorized";

	public String shared() {
		return PUBLIC_SERVICE_RESPONSE_TEXT;
	}
}
