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
package io.radien.ms.permissionmanagement.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.TableGenerator;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;

/**
 * Contains the definitions of an Action described on
 * {@link io.radien.ms.permissionmanagement.client.entities.Action}
 * plus the JPA entity mapping
 *
 * @author Newton Carvalho
 */
@Entity
@Table(name = "ACT01")
public class ActionEntity extends io.radien.ms.permissionmanagement.client.entities.Action {

	/**
	 * Entity Action empty constructor
	 */
	public ActionEntity() {}

	/**
	 * Entity action constructor
	 * @param a {@link io.radien.ms.permissionmanagement.client.entities.Action} action to be created
	 */
	public ActionEntity(io.radien.ms.permissionmanagement.client.entities.Action a) {
		super(a);
	}

	/**
	 * Entity action id value
	 * @return the action id
	 */
	@Override
	@Id
	@TableGenerator(name = "GEN_SEQ_ACT01", allocationSize = 2000)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "GEN_SEQ_ACT01")
	public Long getId() {
		return super.getId();
	}

	/**
	 * Entity action name value
	 * @return the action name
	 */
	@Override
	@Column(name = "NAME", nullable = false, unique = true)
	public String getName() {
		return super.getName();
	}
}
