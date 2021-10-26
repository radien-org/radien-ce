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
package io.radien.security.openid.service;

import java.util.Date;

import io.radien.api.model.user.SystemUser;
import org.springframework.security.core.userdetails.UserDetails;

import io.radien.exception.AccountExpiredException;
import io.radien.exception.AccountNotValidException;
import io.radien.exception.AuthenticationFailedException;

/**
 * This interface defines the basic authentication contract of the openappframe
 *
 * @author Marco Weiland
 */
public interface AuthenticationService {

	SystemUser authenticate(String logon, String password) throws AuthenticationFailedException;

	SystemUser authenticate(String logon, String password, String language, String timezone)
			throws AuthenticationFailedException;

	SystemUser authenticateExternal(String logon, String language, String timezone, UserDetails userDetails)
			throws AuthenticationFailedException;

	default void validate(SystemUser user) throws AccountExpiredException, AccountNotValidException {

		if (!user.isEnabled()) {
			throw new AccountNotValidException("User is disabled!");
		}
		if (user.getTerminationDate() != null && new Date().before(user.getTerminationDate())) {
			throw new AccountExpiredException("User is expired");
		}
	}

}
