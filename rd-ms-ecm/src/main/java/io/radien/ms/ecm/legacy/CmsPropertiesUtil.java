/*
 * Copyright (c) 2006-present openappframe.org & its legal owners. All rights reserved.
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
package io.radien.ms.ecm.legacy;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.OAFProperties;
/**
 * @author Marco Weiland
 */
public class CmsPropertiesUtil {

	private static final Logger log = LoggerFactory.getLogger(CmsPropertiesUtil.class);
	private static final String ERROR_MSG = "Error accessing configuration property for key {}";

	public CmsPropertiesUtil() {

	}

	public String get(String key) {
		return null;
	}

	public String get(OAFProperties enumKey) {
		return get(enumKey.propKey());
	}

}
