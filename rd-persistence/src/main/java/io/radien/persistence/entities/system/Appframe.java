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
package io.radien.persistence.entities.system;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import io.radien.api.AbstractModel;
import io.radien.api.IAppframe;

/**
 * @author Marco Weiland
 */

@Entity
@Table(name = "APPSYS01")
// @NamedQuery(name = "Appframe.findAll", query = "SELECT t FROM Appframe t")
public class Appframe extends AbstractModel implements IAppframe {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	@Column(unique = true, nullable = false)
	private String version;

	@Column
	private Date createDate;

	@Column
	private Date lastUpdate;

	@Lob
	private String log;
	
	public Appframe() {
		
	}

	public Appframe(Long id, String version) {
		this.id = id;
		this.version = version;
		createDate = new Date();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

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

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

}
