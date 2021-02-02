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
package io.radien.ms.permissionmanagement.model;

import javax.persistence.*;

/**
 * @author mawe
 *
 */
@Entity
@Table(name = "PERM01")
public class Permission extends io.radien.ms.permissionmanagement.client.entities.Permission {

	public Permission() {}

	public Permission(io.radien.ms.permissionmanagement.client.entities.Permission p) {
		super(p);
		// TODO: Necessary review it
		if (p.getAction() != null) {
			this.setAction(new Action(p.getAction()));
		}
	}

	@Override
	@Id
	@SequenceGenerator(name = "GEN_SEQ_PERM01", sequenceName = "SEQ_PERM01", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GEN_SEQ_PERM01")
	public Long getId() {
		return super.getId();
	}


	@Override
	@Column(name = "NAME", nullable = false, unique = true)
	public String getName() {
		return super.getName();
	}

	@OneToOne(targetEntity = io.radien.ms.permissionmanagement.model.Action.class)
	@JoinColumn(name = "ACTION_ID")
	public Action getAction() {
		return (Action) super.getAction();
	}

	public void setAction(Action a) {
		super.setAction(a);
	}
}
