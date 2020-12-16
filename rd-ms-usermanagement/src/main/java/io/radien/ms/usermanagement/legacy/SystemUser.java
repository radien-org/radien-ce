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
package io.radien.ms.usermanagement.legacy;

import java.util.Date;

/**
 * Class that represents an application user
 *
 * @author Bruno Gama
 */
public interface SystemUser extends Model {

	String getLogon();

	void setLogon(String login);

	String getUserEmail();

	void setUserEmail(String userEmail);

	String getPassword();

	void setPassword(String password);

	String getFullName();

	@Deprecated
	boolean isAcceptedTermsAndConditions();

	@Deprecated
	void setAcceptedTermsAndConditions(boolean acceptedTermsAndConditions);

	boolean isEnabled();

	void setEnabled(boolean enabled);

}
