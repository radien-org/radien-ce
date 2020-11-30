/**
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
package io.radien.security.openid.model;

import static org.springframework.security.core.authority.AuthorityUtils.commaSeparatedStringToAuthorityList;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
	

	public OpenIdConnectUserDetails(Map<String, String> userInfo) {
		this.sub = userInfo.get("sub");
		this.userEmail = userInfo.get("email");
		this.username = userInfo.get("preferred_username");
		this.givenname = userInfo.get("given_name");
		this.familyname = userInfo.get("family_name");
		
		
		
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return commaSeparatedStringToAuthorityList("USER");
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public String getUserEmail() {
		return userEmail;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
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

}