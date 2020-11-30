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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.Appframeable;

/**
 * Custom servlet class that extends the standard {@link HttpServlet} and add
 * utility methods to it
 *
 * @author Marco Weiland
 */
public abstract class AbstractOAFHTTPServlet extends HttpServlet implements Appframeable {

	protected static final Logger log = LoggerFactory.getLogger(AbstractOAFHTTPServlet.class);
	private static final long serialVersionUID = 1L;

	protected abstract void process(OAFHttpRequest req, OAFHttpResponse res) throws ServletException, IOException;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			processRequestAndResponse(req, resp);
		} catch (Exception e) {
			log.error("problem processing the request",e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			processRequestAndResponse(req, resp);
		} catch (Exception e) {
			log.error("problem processing the request",e);
		}
	}

	private void processRequestAndResponse(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		OAFHttpRequest oafRequest = new OAFHttpRequest(req);
		OAFHttpResponse oafResponse = new OAFHttpResponse(resp);
		process(oafRequest, oafResponse);
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		OAFHttpRequest oafRequest = new OAFHttpRequest(req);
		OAFHttpResponse oafResponse = new OAFHttpResponse(resp);
		super.service(oafRequest, oafResponse);
	}

	protected byte[] getBytes(InputStream is, long requestLength) throws IOException {

		int len;
		int size = (int) requestLength;
		byte[] buf;

		if (is instanceof ByteArrayInputStream) {
			size = is.available();
			buf = new byte[size];
		} else {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				buf = new byte[size];
				while ((len = is.read(buf, 0, size)) != -1) {
					bos.write(buf, 0, len);
				}
				buf = bos.toByteArray();
			} finally {
				try {
					bos.close();
				} catch (Exception e) {
					log.error("Error on getBytes method", e);
				}

			}

		}
		return buf;
	}

}
