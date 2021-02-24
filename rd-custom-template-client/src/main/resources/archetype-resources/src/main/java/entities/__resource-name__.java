/*
 * Copyright (c) 2016-present radien.io & its legal owners. All rights reserved.
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

import io.radien.api.model.${resource-name-variable}.System${resource-name};

/**
 * @author Rajesh Gavvala
 *
 */


public class ${resource-name} implements System${resource-name} {
	private static final long serialVersionUID = -3532886874455311100L;

	private Long id;
	private String message;

	public ${resource-name}(){}

	public ${resource-name}(${resource-name} ${resource-name-variable}) {
		this.id = ${resource-name-variable}.getId();
		this.message = ${resource-name-variable}.getMessage();
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
	public String getMessage() {
		return message;
	}

	@Override
	public void setMessage(String message) {
		this.message = message;
	}
	
}
