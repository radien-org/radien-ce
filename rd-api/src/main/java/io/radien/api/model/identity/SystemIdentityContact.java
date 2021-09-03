/*

	Copyright (c) 2021-present radien GmbH. All rights reserved.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

 */
package io.radien.api.model.identity;

import io.radien.api.Model;

/**
 * System Contact Identity interface
 *
 * @author Marco Weiland
 */
public interface SystemIdentityContact extends Model {

	/**
	 * System Contact id getter
	 * @return the system contact id
	 */
	Long getId();

	/**
	 * System Contact Identity setter id
	 * @param id to be set
	 */
	void setId(Long id);

	/**
	 * System Contact identity getter
	 * @return the system contact identity
	 */
	SystemIdentity getIdentity();

	/**
	 * System Contact Identity setter identity
	 * @param identity to be set
	 */
	void setIdentity(SystemIdentity identity);

	/**
	 * System Contact email getter
	 * @return the system contact email
	 */
	String getEmail();

	/**
	 * System Contact Identity setter email
	 * @param email to be set
	 */
	void setEmail(String email);

	/**
	 * System Contact street getter
	 * @return the system contact street
	 */
	String getStreet();

	/**
	 * System Contact Identity setter street
	 * @param street to be set
	 */
	void setStreet(String street);

	/**
	 * System Contact postal code getter
	 * @return the system contact postal code
	 */
	String getPostalCode();

	/**
	 * System Contact Identity setter postal code
	 * @param postalCode to be set
	 */
	void setPostalCode(String postalCode);

	/**
	 * System Contact city getter
	 * @return the system contact city
	 */
	String getCity();

	/**
	 * System Contact Identity setter city
	 * @param city to be set
	 */
	void setCity(String city);

	/**
	 * System Contact phone number getter
	 * @return the system contact phone number
	 */
	String getPhoneNumber();

	/**
	 * System Contact Identity setter phone number
	 * @param phoneNumber to be set
	 */
	void setPhoneNumber(String phoneNumber);

}
