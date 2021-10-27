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
package ${package}.ms.service.entities;

import javax.persistence.*;


/**
 * JPA entity representing a ${entityResourceName.toLowerCase()} in the openappframe system
 *
 * @author Marco Weiland
 */

@Entity
@Table(name = "${entityResourceName}01")
public class ${entityResourceName} extends ${package}.ms.client.entities.${entityResourceName} {
	private static final long serialVersionUID = 6812608123262000065L;

	public ${entityResourceName}(){ }

	public ${entityResourceName}(${package}.ms.client.entities.${entityResourceName} ${entityResourceName.toLowerCase()}){
		super(${entityResourceName.toLowerCase()});
	}
	@Id
	@TableGenerator(name = "GEN_SEQ_${entityResourceName}01", allocationSize = 2000)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "GEN_SEQ_${entityResourceName}01")
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
