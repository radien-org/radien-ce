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
package io.radien.api.service.ecm.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Marco Weiland
 */
@JsonDeserialize(as = Document.class)
public class Document extends AbstractECMModel implements EnterpriseContent {

	private static final long serialVersionUID = -1606222544052586973L;
	private Long id;

	public Document(byte[] fileStream, String name, long fileSize, String documentMimeType) {
		this.name = name;
		this.fileStream = fileStream;

		this.fileSize = fileSize;
		this.mimeType = documentMimeType;
		this.contentType = ContentType.DOCUMENT;
	}

	@Override
	public int compareTo(EnterpriseContent o) {
		return this.getName().compareTo(o.getName());
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

}
