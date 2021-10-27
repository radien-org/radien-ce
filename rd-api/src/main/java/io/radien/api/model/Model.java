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
package io.radien.api.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Interface defining the basic global auditing properties of a persistent Model
 *
 * @author Marco Weiland
 */
public interface Model extends Serializable {

	/**
	 * Model id getter
	 * @return model id
	 */
	Long getId();

	/**
	 * Model id setter
	 * @param id to be set
	 */
	void setId(Long id);

	/**
	 * Record creation date getter
	 * @return the record creation date
	 */
	Date getCreateDate();

	/**
	 * Record Creation date setter
	 * @param createDate to be set
	 */
	void setCreateDate(Date createDate);

	/**
	 * Record Last Update date getter
	 * @return Record last update date
	 */
	Date getLastUpdate();

	/**
	 * Record last update date setter
	 * @param lastUpdate to be set
	 */
	void setLastUpdate(Date lastUpdate);

	/**
	 * Record create user getter
	 * @return who created the record information
	 */
	Long getCreateUser();

	/**
	 * Record create user setter
	 * @param createUser to be set
	 */
	void setCreateUser(Long createUser);

	/**
	 * Record last update user getter
	 * @return the user information from whom was the last to update the record information
	 */
	Long getLastUpdateUser();

	/**
	 * Record last update user setter
	 * @param lastUpdate to be set
	 */
	void setLastUpdateUser(Long lastUpdate);
}
