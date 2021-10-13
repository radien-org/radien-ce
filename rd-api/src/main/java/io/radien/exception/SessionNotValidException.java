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
package io.radien.exception;

/**
 * Custom exception to be thrown when the use session is invalid
 *
 * @author Marco Weiland
 */
public class SessionNotValidException extends SystemException {
	private static final long serialVersionUID = 8594021107704520414L;

	/**
	 * Session Not Valid Exception empty constructor
	 */
	public SessionNotValidException() {
		super();
	}

	/**
	 * Session Not Valid Exception exception constructor
	 * @param e exception to be added into the session not valid exception
	 */
	public SessionNotValidException(Exception e) {
		super(e);
	}

	/**
	 * Session Not Valid Exception message constructor
	 * @param message to be added into the session not valid exception
	 */
	public SessionNotValidException(String message) {
		super(message);
	}

}
