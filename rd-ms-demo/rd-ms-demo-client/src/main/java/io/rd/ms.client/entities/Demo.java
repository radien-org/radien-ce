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
package io.rd.ms.client.entities;

import io.rd.api.model.SystemDemo;
import io.rd.api.model.AbstractDemoModel;

/**
 * @author Rajesh Gavvala
 *
 */
public class Demo extends AbstractDemoModel implements SystemDemo {
private static final long serialVersionUID = 6812608123262000041L;

	private Long id;
	private String name;

	public Demo(){}

	public Demo(Demo demo) {
		this.id = demo.getId();
		this.name = demo.getName();
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
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name=name;
	}
	
}
