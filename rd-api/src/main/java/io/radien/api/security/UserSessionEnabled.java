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
package io.radien.api.security;

import io.radien.api.Appframeable;
import io.radien.api.model.user.SystemUser;

/**
 * User Session enabled information interface class
 *
 * @author Marco Weiland
 */
public interface UserSessionEnabled extends Appframeable {

	/**
	 * Login process for the given parameters user
	 * @param userIdSubject subject identifier of the user that is making the login
	 * @param email of the user that is making the login
	 * @param preferredUserName of the user that is making the login
	 * @param givenname of the user that is making the login
	 * @param familyName of the user that is making the login
	 * @param accessToken of the user that is making the login
	 * @param refreshToken of the user that is making the login
	 * @throws Exception in case of any non specific error exceptions
	 */
	public void login(String userIdSubject,String email, String preferredUserName, String givenname,String familyName,
					  String accessToken, String refreshToken) throws Exception;

	/**
	 * Is user active getter
	 * @return if the user is still active or not
	 */
	public boolean isActive();

	/**
	 * User subject id getter
	 * @return the user subject
	 */
	public String getUserIdSubject();

	/**
	 * User email getter
	 * @return the user email
	 */
	public String getEmail() ;

	/**
	 * User preferred user name getter
	 * @return the preferred user name specified by the user
	 */
	public String getPreferredUserName() ;

	/**
	 * User first name getter
	 * @return user first name
	 */
	public String getUserFirstName();

	/**
	 * User last name getter
	 * @return user last name
	 */
	public String getUserLastName();

	/**
	 * User id getter
	 * @return user id
	 */
	public Long getUserId();

	/**
	 * User getter
	 * @return user information
	 */
	public SystemUser getUser();

}
