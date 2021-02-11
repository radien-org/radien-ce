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

public class Contract extends AbstractModel implements SystemContract {

	private static final long serialVersionUID = -3532886874455311100L;

	private Long id;
	private String name;
	private LocalDateTime start;
	private LocalDateTime end;

	public Contract(){}

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

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public LocalDateTime getStart() {
		return start;
	}

	@Override
	public void setStart(LocalDateTime start) {
		this.start=start;
	}

	@Override
	public LocalDateTime getEnd() {
		return end;
	}

	@Override
	public void setEnd(LocalDateTime end) {
		this.end = end;
	}
}
