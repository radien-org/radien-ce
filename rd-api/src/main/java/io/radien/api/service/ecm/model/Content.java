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
 * Simple implementation of an EnterpriseContent
 *
 * @author Marco Weiland
 */
@JsonDeserialize(as = Content.class)
public class Content extends AbstractECMModel implements EnterpriseContent {

	private static final long serialVersionUID = -4081831237381066985L;
	private Long id;

	public Content(String title, String htmlContent) {
		this.name = title;
		this.htmlContent = htmlContent;
		this.contentType = ContentType.HTML;
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

	@Override
	public void setId(Long id) {
		this.id = id;
	}

}
