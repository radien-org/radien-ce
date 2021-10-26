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
package ${package}.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;


/**
 *
 * @author Rajesh Gavvala
 */

@Entity
@Table(name = "${db-entity-table-name}")
public class ${resource-name} extends ${client-packageName}.entities.${resource-name} {

	private static final long serialVersionUID = -7928267613860708799L;

	public ${resource-name}(){ }

	public ${resource-name}(${client-packageName}.entities.${resource-name} ${resource-name-variable}){
		super(${resource-name-variable});
	}
	@Id
	@TableGenerator(name = "GEN_SEQ_${db-entity-table-name}", allocationSize = 100)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "GEN_SEQ_${db-entity-table-name}")
	@Override
	public Long getId() {
		return super.getId();
	}

	@Column(unique = true)
	@Override
	public String getMessage() {
		return super.getMessage();
	}
}

