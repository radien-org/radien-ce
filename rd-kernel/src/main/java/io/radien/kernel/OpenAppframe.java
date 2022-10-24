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
package io.radien.kernel;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Named;

/**
 * ApplicationScoped bean responsible for getting application properties and
 * managing OAF kernel utilities
 *
 * @author Marco Weiland
 */
@Named("openAppframe") // !!! IMPORTANT !!! @Named MUST be annotated for
						// programatically EL Evaluation of the bean
@Alternative
@Priority(1)
public @ApplicationScoped class OpenAppframe extends OAF {

	public static final String OAF_EL_RESOLVE_NAME = "#{openAppframe}";
	private static final long serialVersionUID = -1802569158933030590L;

	/**
	 * Method responsible for loading the system properties for the oaf application and managing the kernel utilities
	 */
	public OpenAppframe() {
		super();
		loadSystemProperties();
	}

}
