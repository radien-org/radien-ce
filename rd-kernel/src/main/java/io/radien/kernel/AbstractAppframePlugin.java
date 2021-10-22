/*
 * Copyright (c) 2006-present openappframe.org & its legal owners. All rights reserved.
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
package io.radien.kernel;

import javax.annotation.PostConstruct;

/**
 * Abstract appframe plugin class contructor
 *
 * @author Marco Weiland
 */
public abstract class AbstractAppframePlugin extends OAF {
	private static final long serialVersionUID = -3888664421101765706L;

	public abstract String getPluginProperties();

	/**
	 * Will request for the plugin to be loaded in the OAF
	 */
	@PostConstruct
	private void loadPlugin() {
		loadPlugin(this);
	}

}
