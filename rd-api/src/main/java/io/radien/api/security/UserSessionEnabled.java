/*
 * Copyright (c) 2016-present radien.io & its legal owners. All rights reserved.
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

/**
 * @author Marco Weiland <m.weiland@radien.io>
 *
 */
public interface UserSessionEnabled extends Appframeable {

	public void login(String userIdSubject,String email, String preferredUserName, String givenname,String familyName) throws Exception;
	
	public boolean isActive();

	public String getUserIdSubject();

	public String getEmail() ;

	public String getPreferredUserName() ;

	public String getUserFirstName();

	public String getUserLastName();

}
