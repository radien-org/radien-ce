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
package io.radien.api.model.user;

import io.radien.api.Model;

import java.util.Date;

/**
 * Class that represents an application user
 *
 * @author Bruno Gama
 */
public interface SystemUser extends Model {

	/**
	 * System user logon getter
	 * @return System user logon
	 */
	public String getLogon();

	/**
	 * System user logon setter
	 * @param logon to be set
	 */
	public void setLogon(String logon);

	/**
	 * System user email getter
	 * @return System user email
	 */
	public String getUserEmail();

	/**
	 * System user email setter
	 * @param userEmail to be set
	 */
	public void setUserEmail(String userEmail);

	/**
	 * System user termination date getter
	 * @return System user termination date
	 */
	public Date getTerminationDate();

	/**
	 * System user termination date setter
	 * @param terminationDate to be set
	 */
	public void setTerminationDate(Date terminationDate);

	/**
	 * System user is enable getter
	 * @return System user is still enable
	 */
	public boolean isEnabled();

	/**
	 * System user is enable setter
	 * @param enabled to enable or disable user
	 */
	public void setEnabled(boolean enabled);

	/**
	 * System user subject getter
	 * @return System user subject
	 */
	public String getSub();

	/**
	 * System user subject setter
	 * @param sub to be set
	 */
	public void setSub(String sub);

	/**
	 * System user first name getter
	 * @return System user first name
	 */
	public String getFirstname();

	/**
	 * System user first name setter
	 * @param firstname to be set
	 */
	public void setFirstname(String firstname);

	/**
	 * System user last name getter
	 * @return System user last name
	 */
	public String getLastname();

	/**
	 * System user last name setter
	 * @param lastname to be set
	 */
	public void setLastname(String lastname);

	/**
	 * System user is delegated creation getter
	 * @return System user delegated creation
	 */
	public boolean isDelegatedCreation();

	/**
	 * System user delegated creation setter
	 * @param delegatedCreation to be set
	 */
	public void setDelegatedCreation(boolean delegatedCreation);

}
