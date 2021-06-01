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

import io.radien.api.Model;

import java.time.LocalDateTime;

/**
 * Class that represents an application contract
 *
 * @author Santana
 */
public interface SystemContract extends Model {

	/**
	 * System Contract name getter
	 * @return system contract name
	 */
	public String getName();

	/**
	 * System Contract name setter
	 * @param name to be set
	 */
	public void setName(String name);

	/**
	 * System Contract start date getter
	 * @return system contract start date
	 */
	public LocalDateTime getStart();

	/**
	 * System Contract start date setter
	 * @param time to be set
	 */
	public void setStart(LocalDateTime time);

	/**
	 * System Contract end date getter
	 * @return system contract end date
	 */
	public LocalDateTime getEnd();

	/**
	 * System Contract end date setter
	 * @param time to be set
	 */
	public void setEnd(LocalDateTime time);
}
