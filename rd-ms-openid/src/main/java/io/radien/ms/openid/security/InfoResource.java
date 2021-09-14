/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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
package io.radien.ms.openid.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Properties;

/**
 * Resource class with all the requests to provide Info from archive
 *
 * @author Nuno Santana
 */
@Path("info")
@RequestScoped
public class InfoResource  {
	@Context
	private HttpServletRequest servletRequest;

	private static final Logger log = LoggerFactory.getLogger(InfoResource.class);


	@GET
	public Response getInfo() {
		try {
			Properties properties = new Properties();
			properties.load(servletRequest.getServletContext()
					.getResourceAsStream("/META-INF/MANIFEST.MF"));
			return Response.ok(properties.getProperty("distname")).build();
		} catch (IOException e) {
			log.error(e.getMessage(),e);
			return Response.serverError().build();
		}
	}


}
