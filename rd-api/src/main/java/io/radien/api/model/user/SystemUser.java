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
package io.radien.api.model.user;

import io.radien.api.Model;
import io.radien.api.model.identity.SystemIdentity;

import java.util.Date;

/**
 * Class that represents an application user
 *
 * @author Bruno Gama
 */
public interface SystemUser extends Model {

	public String getLogon();
	public void setLogon(String logon);

	public String getUserEmail();
	public void setUserEmail(String userEmail);

	public String getPassword();
	public void setPassword(String password);

	public Date getTerminationDate();
	public void setTerminationDate(Date terminationDate);

	public boolean isAcceptedTermsAndConditions();
	public void setAcceptedTermsAndConditions(boolean acceptedTermsAndConditions);

	public boolean isEnabled();
	public void setEnabled(boolean enabled);

	public SystemIdentity getIdentity();
	public void setIdentity(SystemIdentity systemIdentity);

}
