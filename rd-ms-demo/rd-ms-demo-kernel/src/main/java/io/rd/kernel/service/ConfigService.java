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
package io.rd.kernel.service;

import io.rd.api.Configurable;
import io.rd.api.SystemProperties;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import javax.ejb.Stateless;
import java.io.Serializable;

/**
 * @author Rajesh Gavvala
 * @author Marco Weiland
 */
@Stateless
public class ConfigService implements Configurable, Serializable {

	private static final long serialVersionUID = 6812608123262000016L;

	@Override
	public String getProperty(SystemProperties cfg) {
		Config config = ConfigProvider.getConfig();
		return config.getValue(cfg.propKey(),String.class);
	}

}
