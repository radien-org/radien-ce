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
package io.radien.ms.doctypemanagement.client.entities;

import io.radien.api.model.AbstractModel;
import io.radien.api.model.docmanagement.propertydefinition.SystemPropertyDefinition;
import javax.jcr.Value;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.qom.QueryObjectModelConstants;
import javax.jcr.version.OnParentVersionAction;

public class PropertyDefinition extends AbstractModel implements SystemPropertyDefinition {

	private Long id;
	private String name;
	private boolean mandatory;
	private boolean protekted;
	private int propertyType;
	private boolean multiple;

	public PropertyDefinition(){}

	public PropertyDefinition(PropertyDefinition property) {
		this.id = property.getId();
		this.name = property.getName();
		this.mandatory = property.isMandatory();
		this.protekted = property.isProtected();
		this.propertyType = property.getRequiredType();
		this.multiple = property.isMultiple();
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
	public NodeType getDeclaringNodeType() {
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name=name;
	}

	@Override
	public boolean isAutoCreated() {
		return false;
	}

	@Override
	public boolean isMandatory() {
		return mandatory;
	}

	@Override
	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	@Override
	public int getOnParentVersion() {
		return OnParentVersionAction.VERSION;
	}

	@Override
	public boolean isProtected() {
		return protekted;
	}

	@Override
	public void setProtected(boolean protekted) {
		this.protekted = protekted;
	}

	@Override
	public boolean isProtekted() {
		return isProtected();
	}

	@Override
	public void setProtekted(boolean protekted) {
		setProtected(protekted);
	}

	@Override
	public int getRequiredType() {
		return propertyType;
	}

	@Override
	public void setRequiredType(int propertyType) {
		this.propertyType = propertyType;
	}

	@Override
	public String[] getValueConstraints() {
		return null;
	}

	@Override
	public Value[] getDefaultValues() {
		return null;
	}

	@Override
	public boolean isMultiple() {
		return multiple;
	}

	@Override
	public void setMultiple(boolean multiple) {
		this.multiple = multiple;
	}

	@Override
	public String[] getAvailableQueryOperators() {
		return new String[] {
				QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO,
				QueryObjectModelConstants.JCR_OPERATOR_NOT_EQUAL_TO,
				QueryObjectModelConstants.JCR_OPERATOR_GREATER_THAN,
				QueryObjectModelConstants.JCR_OPERATOR_GREATER_THAN_OR_EQUAL_TO,
				QueryObjectModelConstants.JCR_OPERATOR_LESS_THAN,
				QueryObjectModelConstants.JCR_OPERATOR_LESS_THAN_OR_EQUAL_TO,
				QueryObjectModelConstants.JCR_OPERATOR_LIKE };
	}

	@Override
	public boolean isFullTextSearchable() {
		return false;
	}

	@Override
	public boolean isQueryOrderable() {
		return true;
	}
}
