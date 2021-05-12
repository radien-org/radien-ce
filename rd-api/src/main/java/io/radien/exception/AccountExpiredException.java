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
 * Exception to be thrown when a system account passed the expiry date
 *
 * @author Marco Weiland
 */
public class AccountExpiredException extends SystemException {
	private static final long serialVersionUID = -1297670284728759226L;

	/**
	 * Account Expired Exception empty constructor
	 */
	public AccountExpiredException() {
		super();
	}

	/**
	 * Account Expired Exception exception constructor
	 * @param e exception to be added into the account expired exception
	 */
	public AccountExpiredException(Exception e) {
		super(e);
	}

	/**
	 * Account Expired Exception message constructor
	 * @param message to be added into the account expired exception
	 */
	public AccountExpiredException(String message) {
		super(message);
	}

}
