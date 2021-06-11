/*
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
package io.radien.ms.tenantmanagement.client.entities;

import io.radien.api.model.AbstractModel;
import io.radien.api.model.tenant.SystemContract;

import java.time.LocalDateTime;

/**
 * Contract object class and constructor
 * @author Nuno Santana
 */
public class Contract extends AbstractModel implements SystemContract {

	private static final long serialVersionUID = -3532886874455311100L;

	private Long id;
	private String name;
	private LocalDateTime start;
	private LocalDateTime end;

	/**
	 * Contract object empty constructor
	 */
	public Contract(){}

	/**
	 * Contract constructor
	 * @param u contract to be created and used
	 */
	public Contract(Contract u) {
		this.id = u.getId();
		this.name = u.getName();
		this.start = u.getStart();
		this.end = u.getEnd();
		this.setCreateDate(u.getCreateDate());
		this.setCreateUser(u.getCreateUser());
		this.setLastUpdateUser(u.getLastUpdateUser());
		this.setLastUpdate(u.getLastUpdate());
	}

	/**
	 * Contract id getter
	 * @return the contract id
	 */
	@Override
	public Long getId() {
		return id;
	}

	/**
	 * Contract id setter
	 * @param id to be set
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Contract name getter
	 * @return the contract name
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Contract name setter
	 * @param name to be set
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Contract start date getter
	 * @return the contract start date
	 */
	@Override
	public LocalDateTime getStart() {
		return start;
	}

	/**
	 * Contract start date setter
	 * @param start to be set
	 */
	@Override
	public void setStart(LocalDateTime start) {
		this.start=start;
	}

	/**
	 * Contract end date getter
	 * @return the contract end date
	 */
	@Override
	public LocalDateTime getEnd() {
		return end;
	}

	/**
	 * Contract end date setter
	 * @param end to be set
	 */
	@Override
	public void setEnd(LocalDateTime end) {
		this.end = end;
	}
}
