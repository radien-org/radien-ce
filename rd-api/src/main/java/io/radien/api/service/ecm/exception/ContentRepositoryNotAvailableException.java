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
package io.radien.api.service.ecm.exception;

import javax.ws.rs.core.Response;

/**
 * Exception extending the base SystemException, to be thrown when error occurs in the CMS
 *
 * @author Marco Weiland
 */
public class ContentRepositoryNotAvailableException extends ContentException {
	private static final long serialVersionUID = -4346627471389807097L;

	public ContentRepositoryNotAvailableException() {
		super();
	}

	public ContentRepositoryNotAvailableException(String message, Exception e, Response.Status responseStatus) {
		super(message, e, responseStatus);
	}
}
