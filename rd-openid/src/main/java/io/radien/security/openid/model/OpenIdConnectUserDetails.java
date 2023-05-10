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
package io.radien.security.openid.model;

import java.util.Map;

/**
 * @author Marco Weiland
 * @author Rafael Fernandes
 */
public class OpenIdConnectUserDetails implements UserDetails {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String sub;
	private String username;
	private String userEmail;
	private String givenname;
	private String familyname;
	private String fullName;
	private String mobileNumber;
	

	public OpenIdConnectUserDetails(Map<String, String> userInfo) {
		this.sub = userInfo.get("sub");
		this.userEmail = userInfo.get("email");
		this.username = userInfo.get("preferred_username");
		this.givenname = userInfo.get("given_name");
		this.familyname = userInfo.get("family_name");
		this.mobileNumber = userInfo.get("mobile_number");
		this.fullName = givenname + " " + familyname;


	}
	@Override
	public String getUsername() {
		return username;
	}

	public String getUserEmail() {
		return userEmail;
	}


	/**
	 * @return the sub
	 */
	public String getSub() {
		return sub;
	}

	/**
	 * @param sub the sub to set
	 */
	public void setSub(String sub) {
		this.sub = sub;
	}

	/**
	 * @return the mobile number
	 */
	public String getMobileNumber() {
		return mobileNumber;
	}

	/**
	 * @param mobileNumber the mobile number to set
	 */
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	/**
	 * @return the givenname
	 */
	public String getGivenname() {
		return givenname;
	}

	/**
	 * @param givenname the givenname to set
	 */
	public void setGivenname(String givenname) {
		this.givenname = givenname;
	}

	/**
	 * @return the familyname
	 */
	public String getFamilyname() {
		return familyname;
	}

	/**
	 * @param familyname the familyname to set
	 */
	public void setFamilyname(String familyname) {
		this.familyname = familyname;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

}