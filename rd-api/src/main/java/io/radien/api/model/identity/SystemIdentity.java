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
package io.radien.api.model.identity;

import io.radien.api.Model;

/**
 * System user identity interface
 *
 * @author Marco Weiland
 */
public interface SystemIdentity extends Model {

	/**
	 * user identity first name getter
	 * @return the user first name
	 */
	public String getFirstname();

	/**
	 * user identity first name setter
	 * @param firstname to be set
	 */
	public void setFirstname(String firstname);

	/**
	 * user identity last name getter
	 * @return the user last name
	 */
	public String getLastname();

	/**
	 * user identity last name setter
	 * @param lastname to be set
	 */
	public void setLastname(String lastname);

	/**
	 * user identity full name getter with reverse parameter
	 * @param reverse should the full name be returned with last name first or not
	 * @return the user full name
	 */
	public String getFullname(boolean reverse);

	/**
	 * user identity full name getter with first name and then last name
	 * @return the user full name
	 */
	public String getFullname();


}
