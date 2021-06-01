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
package io.radien.util;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Simple extension of a {@link HttpServletResponseWrapper} that enables custom
 * headers on a response
 *
 * @author Marco Weiland
 */
public class OAFHttpResponse extends HttpServletResponseWrapper {
	// holds custom header and value mapping
	private final Map<String, String> customHeaders;

	/**
	 * Custom OAF HTTP Request constructor by a given response
	 * @param response for the creation of the OAF HTTP response
	 */
	OAFHttpResponse(HttpServletResponse response) {
		super(response);
		this.customHeaders = new HashMap<>();
	}

	/**
	 * Adds the following name and value into the header of the HTTP response
	 * @param name identifier
	 * @param value for the header
	 */
	void putHeader(String name, String value) {
		this.customHeaders.put(name, value);
	}

	/**
	 * Retrieves by a given name the correct http response header
	 * @param name to be found
	 * @return string value of the http response header
	 */
	@Override
	public String getHeader(String name) {
		// check the custom headers first
		String headerValue = customHeaders.get(name);

		if (headerValue != null) {
			return headerValue;
		}
		// else return from into the original wrapped object
		return ((HttpServletResponse) getResponse()).getHeader(name);
	}

	/**
	 * Retrieves all the existent headers into an enumeration string
	 * @return a enumeration array list of strings of all the existent header names
	 */
	@Override
	public Collection<String> getHeaderNames() {
		// create a set of the custom header names
		Set<String> set = new HashSet<>(customHeaders.keySet());

		// now add the headers from the wrapped request object
		Enumeration<String> e = ((HttpServletRequest) getResponse()).getHeaderNames();
		while (e.hasMoreElements()) {
			// add the names of the request headers into the list
			String n = e.nextElement();
			set.add(n);
		}

		// create an enumeration from the set and return
		return set;
	}

}
