/*
	Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */
package io.radien.api.model.tenant;

/**
 * System tenant type interface class
 *
 * @author Bruno Gama
 */
public interface SystemTenantType {

    /**
     * System tenant type id getter
     * @return system tenant type id
     */
    public Long getId();

    /**
     * System tenant type id setter
     * @param id to be set
     */
    public void setId(Long id);

    /**
     * System tenant type name getter
     * @return system tenant type name
     */
    public String getName();

    /**
     * System tenant type name setter
     * @param name to be set
     */
    public void setName(String name);
}