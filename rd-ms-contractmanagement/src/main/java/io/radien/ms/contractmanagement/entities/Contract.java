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
package io.radien.ms.contractmanagement.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import java.time.LocalDateTime;



/**
 * JPA entity representing a contract in the openappframe system
 *
 * @author Santana
 */

@Entity
@Table(name = "CTR01")
public class Contract extends io.radien.ms.contractmanagement.client.entities.Contract {

	private static final long serialVersionUID = -7928267613860708799L;

	public Contract(){ }

	public Contract(io.radien.ms.contractmanagement.client.entities.Contract contract){
		super(contract);
	}
	@Id
	@TableGenerator(name = "GEN_SEQ_CTR01", allocationSize = 100)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "GEN_SEQ_CTR01")
	@Override
	public Long getId() {
		return super.getId();
	}

	@Column(unique = true)
	@Override
	public String getName() {
		return super.getName();
	}

	@Column
	@Override
	public LocalDateTime getStart() {
		return super.getStart();
	}

	@Column
	@Override
	public LocalDateTime getEnd() {
		return super.getEnd();
	}

}
