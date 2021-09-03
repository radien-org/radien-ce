/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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
package io.radien.api.kernel.messages;

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Enum with SystemMessage types used by the SystemEnum class
 *
 * @author Marco Weiland
 */
public enum SystemMessageTypes {
	TECHNICAL("technical_error", "TechnicalMessageResourceBundle"), BUSINESS("business_error",
			"BusinessMessageResourceBundle"), SECURITY("security_error", "SecurityMessageResourceBundle");

	private static Map<SystemMessageTypes, ResourceBundle> resourceBundles = new EnumMap<>(SystemMessageTypes.class);
	private String typeKey;
	private String resourceBundleLocation;

	/**
	 * System Message Types constructor. Creates a new System message tyoe by a given type and location
	 * @param typeKey type message type
	 * @param resourceBundleLocation location for the message type to be used
	 */
	SystemMessageTypes(String typeKey, String resourceBundleLocation) {
		this.typeKey = typeKey;
		this.resourceBundleLocation = resourceBundleLocation;
	}

	/**
	 * System Message Type getter for the type
	 * @return the string value of the type of the system message type
	 */
	public String typeKey() {
		return typeKey;
	}

	/**
	 * Gets the resource bundle required by the error type code type
	 * @param errorType to be found
	 * @return a resource bundle that has been found
	 */
	public ResourceBundle resourceBundle(SystemMessageTypes errorType) {
		ResourceBundle resourceBundle = resourceBundles.get(errorType);
		if (resourceBundle == null) {
			synchronized (this) {
				resourceBundle = ResourceBundle.getBundle(resourceBundleLocation);
				resourceBundles.put(errorType, resourceBundle);
			}
		}

		return resourceBundle;
	}
}
