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
package io.radien.ms.permissionmanagement.client.entities;

import io.radien.api.model.permission.AbstractPermissionModel;
import io.radien.api.model.permission.SystemPermission;

/**
 * Entity that corresponds to the Permission
 *
 * @author Newton Carvalho
 */
public class Permission extends AbstractPermissionModel implements SystemPermission {

	private static final long serialVersionUID = -9109161353494484789L;

	private Long id;
	private String name;
	private Long actionId;
	private Long resourceId;

	/**
	 * Permission empty constructor
	 */
	public Permission(){}

	/**
	 * Permission object constructor
	 * @param p permission information to be added and created
	 */
	public Permission(Permission p) {
		this.id = p.getId();
		this.name = p.getName();
		this.actionId = p.getActionId();
		this.resourceId = p.getResourceId();
	}

	/**
	 * Permission id getter
	 * @return the permission id
	 */
	@Override
	public Long getId() {
		return id;
	}

	/**
	 * Permission id setter
	 * @param id to be set
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Permission name getter
	 * @return the permission name
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Permission name setter
	 * @param name to be set
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Permission action id getter
	 * @return the permission action id associated
	 */
	@Override
	public Long getActionId() {
		return actionId;
	}

	/**
	 * Permission action id setter
	 * @param actionId to be set
	 */
	public void setActionId(Long actionId) {
		this.actionId = actionId;
	}

	/**
	 * Permission resource id getter
	 * @return the permission resource id associated
	 */
	@Override
	public Long getResourceId() { return resourceId; }

	/**
	 * Permission resource id setter
	 * @param resourceId to be set
	 */
	@Override
	public void setResourceId(Long resourceId) { this.resourceId = resourceId; }
}
