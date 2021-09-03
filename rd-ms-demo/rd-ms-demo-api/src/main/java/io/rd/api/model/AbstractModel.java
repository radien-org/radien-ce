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
package io.rd.api.model;

import java.util.Date;

/**
 * An abstract model class that should be extended by all application entitites
 * @author Rajesh Gavvala
 * @author Marco Weiland
 */
public abstract class AbstractModel implements Model {
	private static final long serialVersionUID = 6812608123262000003L;
	private Date createDate;
	private Date lastUpdate;
	private Long createDemo;
	private Long lastUpdateDemo;

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Long getCreateDemo() {
		return createDemo;
	}

	public void setCreateDemo(Long createDemo) {
		this.createDemo = createDemo;
	}

	public Long getLastUpdateDemo() {
		return lastUpdateDemo;
	}

	public void setLastUpdateDemo(Long lastUpdateDemo) {
		this.lastUpdateDemo = lastUpdateDemo;
	}

}
