/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
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
package io.rd.web.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Model;
import javax.inject.Inject;

import io.rd.api.OAFAccess;
import io.rd.api.OAFProperties;
import io.rd.webapp.AbstractWebapp;

/**
 * @author Rajesh Gavvala
 * @author Marco Weiland
 */
public @ApplicationScoped @Model class WebApp extends AbstractWebapp {
	private static final long serialVersionUID = 6812608123262000038L;

	@Inject
	private OAFAccess oaf;

	@Override
	public String getProperty(OAFProperties cfg) {
		return getOAF().getProperty(cfg);
	}

	@Override
	public OAFAccess getOAF() {
		return oaf;
	}

}
