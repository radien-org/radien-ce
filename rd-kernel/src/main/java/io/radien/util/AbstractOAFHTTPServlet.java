/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
	private static final long serialVersionUID = 403313317087741322L;

	protected abstract void process(OAFHttpRequest req, OAFHttpResponse res) throws ServletException, IOException;

	/**
	 * Called by the server (via the service method) to allow a servlet to handle a GET request.
	 * Overriding this method to support a GET request also automatically supports an HTTP HEAD request.
	 * A HEAD request is a GET request that returns no body in the response, only the request header fields.
	 * When overriding this method, read the request data, write the response headers, get the response's
	 * writer or output stream object, and finally, write the response data. It's best to include content
	 * type and encoding. When using a PrintWriter object to return the response, set the content type before
	 * accessing the PrintWriter object.
	 * The servlet container must write the headers before committing the response, because in HTTP the headers
	 * must be sent before the response body.
	 * Where possible, set the Content-Length header (with the ServletResponse.setContentLength(int) method), to
	 * allow the servlet container to use a persistent connection to return its response to the client, improving
	 * performance. The content length is automatically set if the entire response fits inside the response buffer.
	 * The GET method should be safe, that is, without any side effects for which users are held responsible.
	 * For example, most form queries have no side effects. If a client request is intended to change stored data,
	 * the request should use some other HTTP method.
	 * The GET method should also be idempotent, meaning that it can be safely repeated. Sometimes making a method
	 * safe also makes it idempotent. For example, repeating queries is both safe and idempotent, but buying a product
	 * online or modifying data is neither safe nor idempotent.
	 * If the request is incorrectly formatted, doGet returns an HTTP "Bad Request" message.
	 * @param req an HttpServletRequest object that contains the request the client has made of the servlet
	 * @param resp an HttpServletResponse object that contains the response the servlet sends to the client
	 * @throws ServletException if the request for the GET could not be handled
	 * @throws IOException if an input or output error is detected when the servlet handles the GET request
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			processRequestAndResponse(req, resp);
		} catch (Exception e) {
			log.error("problem processing the request",e);
		}
	}

	/**
	 * Called by the server (via the service method) to allow a servlet to handle a POST request.
	 * The HTTP POST method allows the client to send data of unlimited length to the Web server a single time
	 * and is useful when posting information such as credit card numbers.
	 * When overriding this method, read the request data, write the response headers, get the response's writer
	 * or output stream object, and finally, write the response data. It's best to include content type and encoding.
	 * When using a PrintWriter object to return the response, set the content type before accessing the PrintWriter
	 * object.
	 * The servlet container must write the headers before committing the response, because in HTTP the headers must
	 * be sent before the response body.
	 * Where possible, set the Content-Length header (with the ServletResponse.setContentLength(int) method), to
	 * allow the servlet container to use a persistent connection to return its response to the client, improving
	 * performance. The content length is automatically set if the entire response fits inside the response buffer.
	 * When using HTTP 1.1 chunked encoding (which means that the response has a Transfer-Encoding header), do not
	 * set the Content-Length header.
	 * This method does not need to be either safe or idempotent. Operations requested through POST can have side
	 * effects for which the user can be held accountable, for example, updating stored data or buying items online.
	 * If the HTTP POST request is incorrectly formatted, doPost returns an HTTP "Bad Request" message.
	 * @param req an HttpServletRequest object that contains the request the client has made of the servlet
	 * @param resp an HttpServletResponse object that contains the response the servlet sends to the client
	 * @throws ServletException if the request for the POST could not be handled
	 * @throws IOException if an input or output error is detected when the servlet handles the request
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			processRequestAndResponse(req, resp);
		} catch (Exception e) {
			log.error("problem processing the request",e);
		}
	}

	/**
	 * This sections provides the logical breakdown of the HTTP request-response process.
	 * After the client sends its request to a server, it is helpful to define a set of
	 * logical steps which the server must perform before a response is sent.
	 * @param req an HttpServletRequest object that contains the request the client has made of the servlet
	 * @param resp an HttpServletResponse object that contains the response the servlet sends to the client
	 * @throws ServletException if the request for the POST could not be handled
	 * @throws IOException if an input or output error is detected when the servlet handles the request
	 */
	private void processRequestAndResponse(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		OAFHttpRequest oafRequest = new OAFHttpRequest(req);
		OAFHttpResponse oafResponse = new OAFHttpResponse(resp);
		process(oafRequest, oafResponse);
	}

	/**
	 * Receives standard HTTP requests from the public service method and dispatches them to the doXXX
	 * methods defined in this class. This method is an HTTP-specific version of the
	 * Servlet.service(javax.servlet.ServletRequest, javax.servlet.ServletResponse) method.
	 * @param req the HttpServletRequest object that contains the request the client made of the servlet
	 * @param resp the HttpServletResponse object that contains the response the servlet returns to the client
	 * @throws ServletException if the request for the TRACE cannot be handled
	 * @throws IOException if an input or output error occurs while the servlet is handling the TRACE request
	 */
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		OAFHttpRequest oafRequest = new OAFHttpRequest(req);
		OAFHttpResponse oafResponse = new OAFHttpResponse(resp);
		super.service(oafRequest, oafResponse);
	}

	/**
	 * If a byte array contains non-Unicode text, you can convert the text to Unicode with one of the String
	 * constructor methods. Conversely, you can convert a String object into a byte array of non-Unicode characters
	 * with the String.getBytes method. When invoking either of these methods, you specify the encoding identifier
	 * as one of the parameters.
	 * @param is input stream to parse
	 * @param requestLength length of the parser message
	 * @return a array of bytes
	 * @throws IOException if an input or output error occurs while the servlet is handling the parser request
	 */
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
