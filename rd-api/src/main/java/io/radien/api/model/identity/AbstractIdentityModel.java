/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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
package io.radien.api.model.identity;

import io.radien.api.model.AbstractModel;

/**
 * Class that represents a user identity
 *
 * @author Marco Weiland
 */
public abstract class AbstractIdentityModel extends AbstractModel implements SystemIdentity {
	private static final long serialVersionUID = 4687165032538623479L;

	/**
	 * Getter for the user full name, either with comma divided or space divided
	 * @param reverse in case of true will separate name with coma (last name, first name)
	 *                in case of false will separate the name with a space (first name last name)
	 * @return the user full name
	 */
	public String getFullname(boolean reverse) {
		return reverse ? getLastname() + ", " + getFirstname() : getFirstname() + " " + getLastname();
	}

	/**
	 * Will return the user full name in the correct order (first name last name)
	 * @return the user full name
	 */
	public String getFullname() {
		return getFullname(false);
	}

}
