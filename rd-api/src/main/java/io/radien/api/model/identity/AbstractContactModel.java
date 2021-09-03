/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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

import io.radien.api.model.AbstractModel;

/**
 * Simple contact model
 *
 * @author Marco Weiland
 */
public abstract class AbstractContactModel extends AbstractModel implements SystemIdentityContact {
	private static final long serialVersionUID = -7786804847671643217L;

	private Long id;
	private SystemIdentity identity;
	private String contactType;
	private String email;
	private String street;
	private String postalCode;
	private String city;
	private String phoneNumber;

	/**
	 * Abstract contact Model getter id
	 * @return the abstract contact model id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Abstract contact Model setter id
	 * @param id to be set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Abstract contact Model getter identity
	 * @return the abstract contact model identity
	 */
	public SystemIdentity getIdentity() {
		return identity;
	}

	/**
	 * Abstract contact Model setter identity
	 * @param identity to be set
	 */
	public void setIdentity(SystemIdentity identity) {
		this.identity = identity;
	}

	/**
	 * Abstract contact Model getter contact type
	 * @return the abstract contact model contact type
	 */
	public String getContactType() {
		return contactType;
	}

	/**
	 * Abstract contact Model setter contact type
	 * @param contactType to be set
	 */
	public void setContactType(String contactType) {
		this.contactType = contactType;
	}

	/**
	 * Abstract contact Model getter email
	 * @return the abstract contact model email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Abstract contact Model setter email
	 * @param email to be set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Abstract contact Model getter street
	 * @return the abstract contact model street
	 */
	public String getStreet() {
		return street;
	}

	/**
	 * Abstract contact Model setter street
	 * @param street to be set
	 */
	public void setStreet(String street) {
		this.street = street;
	}

	/**
	 * Abstract contact Model getter postal code
	 * @return the abstract contact model postal code
	 */
	public String getPostalCode() {
		return postalCode;
	}

	/**
	 * Abstract contact Model setter postal code
	 * @param postalCode to be set
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	/**
	 * Abstract contact Model getter city
	 * @return the abstract contact model city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Abstract contact Model setter city
	 * @param city to be set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Abstract contact Model getter phone number
	 * @return the abstract contact model phone number
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * Abstract contact Model setter phone number
	 * @param phoneNumber to be set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

}
