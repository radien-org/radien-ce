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
package io.radien.ms.permissionmanagement.client.entities;

import io.radien.api.model.permission.AbstractPermissionModel;
import io.radien.api.model.permission.SystemAction;
import io.radien.api.model.permission.SystemPermission;

public class Permission extends AbstractPermissionModel implements SystemPermission {

	private static final long serialVersionUID = -3532886874455311100L;

	private Long id;
	private String name;
	private Action action;
	
	public Permission(){}

	public Permission(Permission p) {
		this.id = p.getId();
		this.name = p.getName();
		this.action = p.getAction();
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

	public Action getAction() {
		return action;
	}

	public void setAction(SystemAction a) {
		this.action = (Action) a;
	}
}
