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

import java.io.Serializable;
import java.util.Date;

/**
 * Interface defining the basic global auditing properties of a persistent Model
 *
 * @author Bruno Gama
 */
public interface Model extends Serializable {

	Long getId();
	void setId(Long id);

	Date getCreateDate();
	void setCreateDate(Date createDate);

	Date getLastUpdate();
	void setLastUpdate(Date lastUpdate);

	Long getCreateUser();
	void setCreateUser(Long createUser);

	Long getLastUpdateUser();
	void setLastUpdateUser(Long lastUpdate);
}
