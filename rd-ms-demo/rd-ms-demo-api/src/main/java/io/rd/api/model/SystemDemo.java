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
package io.rd.api.model;

import io.rd.api.model.Model;

import java.util.Date;


/**
 * Class that represents an application user
 * @author Rajesh Gavvala
 */
public interface SystemDemo extends Model {

	public String getName();
	public void setName(String name);

}
