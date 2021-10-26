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

import io.radien.exception.SystemException;

/**
 * Exception to be thrown when a element is not found on the cms
 *
 * @author Marco Weiland
 */
public class ElementNotFoundException extends SystemException {
	private static final long serialVersionUID = -7116170979219764244L;

	/**
	 * Element not found exception message constructor
	 * @param message to be added
	 */
	public ElementNotFoundException(String message) {
		super(message);
	}
}
