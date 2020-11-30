/*

	Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.

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

import java.util.Date;
import java.util.List;

import io.radien.api.Model;

/**
 * @author Marco Weiland
 */
public interface SystemIdentity extends Model {

	String getIdentityKey();

	void setIdentityKey(String id);

	String getGlobalId();

	void setGlobalId(String globalId);

	String getFirstname();

	void setFirstname(String firstname);

	String getLastname();

	void setLastname(String lastname);

	String getFullname(boolean reverse);

	String getFullname();

	boolean isVerified();

	void setVerified(boolean verified);

	Date getVerificationDate();

	void setVerificationDate(Date verificationDate);

	List<SystemIdentityContact> getContacts();
}
