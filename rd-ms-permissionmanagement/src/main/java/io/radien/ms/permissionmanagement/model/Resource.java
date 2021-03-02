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
 * @author Newton Carvalho
 *
 * Contains the definitions of an Resource described on
 * {@link io.radien.ms.permissionmanagement.client.entities.Resource}
 * plus the JPA entity mapping
 */
@Entity
@Table(name = "RES01")
public class Resource extends io.radien.ms.permissionmanagement.client.entities.Resource {

	public Resource() {}

	public Resource(io.radien.ms.permissionmanagement.client.entities.Resource a) {
		super(a);
	}

	@Override
	@Id
	@TableGenerator(name = "GEN_SEQ_RES01", allocationSize = 2000)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "GEN_SEQ_RES01")
	public Long getId() {
		return super.getId();
	}

	@Override
	@Column(name = "NAME", nullable = false, unique = true)
	public String getName() {
		return super.getName();
	}
}