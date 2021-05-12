/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
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
 * Exception to be throw when the user authentication failed
 *
 * @author Marco Weiland
 */
public class AuthenticationFailedException extends SystemException {
	private static final long serialVersionUID = 4184028146041086749L;

	/**
	 * Authentication Failed Exception empty constructor
	 */
	public AuthenticationFailedException() {
		super();
	}

	/**
	 * Authentication Failed Exception exception constructor
	 * @param e exception to be added into the authentication failed exception
	 */
	public AuthenticationFailedException(Exception e) {
		super(e);
	}

	/**
	 * Authentication Failed Exception message constructor
	 * @param message to be added into the authentication failed exception
	 */
	public AuthenticationFailedException(String message) {
		super(message);
	}

}
