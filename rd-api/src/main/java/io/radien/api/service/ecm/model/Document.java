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
 * ECM Document information object class
 *
 * @author Marco Weiland
 */
@JsonDeserialize(as = Document.class)
public class Document extends AbstractECMModel implements EnterpriseContent {

	private static final long serialVersionUID = -1606222544052586973L;

	/**
	 * ECM Document constructor
	 * @param fileStream to be uploaded
	 * @param name of the document
	 * @param fileSize of the document
	 * @param documentMimeType for the upload
	 */
	public Document(byte[] fileStream, String name, long fileSize, String documentMimeType) {
		this.name = name;
		this.fileStream = fileStream;

		this.fileSize = fileSize;
		this.mimeType = documentMimeType;
		this.contentType = ContentType.DOCUMENT;
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
