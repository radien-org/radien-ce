/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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
package io.rd.microservice.ms.service.entities;

import javax.persistence.*;


/**
 * JPA entity representing a microservice in the openappframe system
 *
 * @author Marco Weiland
 */

@Entity
@Table(name = "Microservice01")
public class Microservice extends io.rd.microservice.ms.client.entities.Microservice {
	private static final long serialVersionUID = 6812608123262000065L;

	public Microservice(){ }

	public Microservice(io.rd.microservice.ms.client.entities.Microservice microservice){
		super(microservice);
	}
	@Id
	@TableGenerator(name = "GEN_SEQ_Microservice01", allocationSize = 2000)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "GEN_SEQ_Microservice01")
	@Override
	public Long getId() {
		return super.getId();
	}


	@Column
	@Override
	public String getName() {
		return super.getName();
	}

}
