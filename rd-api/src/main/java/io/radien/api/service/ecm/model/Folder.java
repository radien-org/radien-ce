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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * ECM Folder properties class
 *
 * @author Marco Weiland
 */
@JsonDeserialize(as = Folder.class)
public class Folder extends AbstractECMModel implements EnterpriseContent {

	private static final long serialVersionUID = 2618986654707693235L;

	/**
	 * ECM Folder properties constructor
	 * @param name of the folder
	 */
	public Folder(String name) {
		this.name = name;
		this.contentType = ContentType.FOLDER;
	}

	/**
	 * Compares this object with the specified object for order. Returns a negative integer, zero,
	 * or a positive integer as this object is less than, equal to, or greater than the specified object.
	 * @param o the object to be compared.
	 * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
	 */
	@Override
	public int compareTo(EnterpriseContent o) {
		return this.getName().compareTo(o.getName());
	}

	/**
	 * This object (which is already a string!) is itself returned.
	 * @return he string itself.
	 */
	@Override
	public String toString() {
		return name;
	}
}
