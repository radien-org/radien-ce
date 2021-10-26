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
package io.rd.microservice.api.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Interface defining the basic global auditing properties of a persistent Model
 * @author Rajesh Gavvala
 * @author Marco Weiland
 */
public interface Model extends Serializable {

	Long getId();

	void setId(Long id);

	Date getCreateDate();

	void setCreateDate(Date createDate);

	Date getLastUpdate();

	void setLastUpdate(Date lastUpdate);

	Long getCreateMicroservice();

	void setCreateMicroservice(Long createMicroservice);

	Long getLastUpdateMicroservice();

	void setLastUpdateMicroservice(Long lastUpdate);
}
