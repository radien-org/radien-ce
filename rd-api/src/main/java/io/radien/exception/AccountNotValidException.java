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
 * Exception to be thrown when a given system account is not valid, this can be
 * bexause:
 * <p>
 * 1. The current account is not present, or null 2. The user has the account
 * disabled 3. The account is missing required information for it to be valid
 * </p>
 *
 * @author Marco Weiland
 */
public class AccountNotValidException extends SystemException {
	private static final long serialVersionUID = 4184028146041086749L;

	public AccountNotValidException() {
		super();
	}

	public AccountNotValidException(Exception e) {
		super(e);
	}

	public AccountNotValidException(String message) {
		super(message);
	}

}
