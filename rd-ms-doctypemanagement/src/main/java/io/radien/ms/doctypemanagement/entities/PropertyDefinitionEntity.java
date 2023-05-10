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
package io.radien.ms.doctypemanagement.entities;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import io.radien.ms.doctypemanagement.client.entities.PropertyDefinition;

@Entity
@Table(name = "PROP_TYP01")
public class PropertyDefinitionEntity extends PropertyDefinition {

	public PropertyDefinitionEntity(){ }

	public PropertyDefinitionEntity(PropertyDefinition propertyType){
		super(propertyType);
	}

	@Id
	@TableGenerator(name = "GEN_SEQ_PROP_TYP01", allocationSize = 2000)
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "GEN_SEQ_PROP_TYP01")
	@Override
	public Long getId() {
		return super.getId();
	}

	@Column
	@Override
	public String getName() {
		return super.getName();
	}

	@Column
	@Override
	public boolean isMandatory() { return super.isMandatory(); }

	@Column
	@Override
	public boolean isProtected() { return super.isProtected(); }

	@Column
	@Override
	public int getRequiredType() { return super.getRequiredType(); }

	@Column
	@Override
	public boolean isMultiple() { return super.isMultiple(); }

	@Column
	@Override
	public Date getCreateDate() {
		return super.getCreateDate();
	}

	@Column
	@Override
	public Date getLastUpdate() {
		return super.getLastUpdate();
	}

	@Column
	@Override
	public Long getCreateUser() {
		return super.getCreateUser();
	}

	@Column
	@Override
	public Long getLastUpdateUser() {
		return super.getLastUpdateUser();
	}
}
