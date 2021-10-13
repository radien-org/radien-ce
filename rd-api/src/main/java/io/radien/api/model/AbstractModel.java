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
package io.radien.api.model;

import java.util.Date;

/**
 * An abstract model class that should be extended by all application entities
 *
 * @author Marco Weiland
 */
public abstract class AbstractModel extends ModelValueId implements Model {
	private static final long serialVersionUID = 1468276722471664639L;

	private Date createDate;
	private Date lastUpdate;
	private Long createUser;
	private Long lastUpdateUser;

	/**
	 * Record creation date getter
	 * @return the record creation date
	 */
	public Date getCreateDate() {
		return createDate;
	}

	/**
	 * Record Creation date setter
	 * @param createDate to be set
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	/**
	 * Record Last Update date getter
	 * @return Record last update date
	 */
	public Date getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * Record last update date setter
	 * @param lastUpdate to be set
	 */
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/**
	 * Record create user getter
	 * @return who created the record information
	 */
	public Long getCreateUser() {
		return createUser;
	}

	/**
	 * Record create user setter
	 * @param createUser to be set
	 */
	public void setCreateUser(Long createUser) {
		this.createUser = createUser;
	}

	/**
	 * Record last update user getter
	 * @return the user information from whom was the last to update the record information
	 */
	public Long getLastUpdateUser() {
		return lastUpdateUser;
	}

	/**
	 * Record last update user setter
	 * @param lastUpdateUser to be set
	 */
	public void setLastUpdateUser(Long lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	}

}
