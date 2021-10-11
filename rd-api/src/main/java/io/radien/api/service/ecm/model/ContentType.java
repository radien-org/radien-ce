/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
package io.radien.api.service.ecm.model;

/**
 * Enum Class with available types of the CMS files
 *
 * @author Marco Weiland
 */
public enum ContentType {

	DOCUMENT("document"),
	HTML("html"),
	IMAGE("image"),
	NEWS_FEED("newsfeed"),
	FOLDER("folder"),
	NOTIFICATION("notification"),
	TAG("tag"),
	ERROR("error");

	private String key;

	/**
	 * Content type constructor
	 * @param key of the type
	 */
	ContentType(String key) {
		this.key = key;
	}

	/**
	 * Gets the content type enumerate by the given key
	 * @param key to be search
	 * @return the content type associated to the given key
	 */
	public static ContentType getByKey(String key) {
		ContentType returnType = null;
		for (ContentType type : values()) {
			if (type.key().equalsIgnoreCase(key)) {
				returnType = type;
				break;
			}
		}
		return returnType;
	}

	/**
	 * Content type key getter
	 * @return the value of the key
	 */
	public String key() {
		return key;
	}

}
