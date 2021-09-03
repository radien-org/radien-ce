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

package io.radien.util;

import io.radien.exception.GenericErrorCodeMessage;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The HTTPConnectionUtil class provides utility method to do HTTP post connections.
 *
 * @author jsr
 */
public class HttpConnectionUtil {

    private static final Logger log = LoggerFactory.getLogger(HttpConnectionUtil.class);

    /** The response. */
    private HttpResponse response = null;

    /** The Constant BUFFER_SIZE. */
    private static final int BUFFER_SIZE = 1024 * 4;

    /**
     * The Post operation.
     *
     * @param url the URL
     * @param params the parameters
     * @return the HTTP response
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public HttpResponse post(String url, Map<String, String> params) throws IOException {
        log.debug("HTTPConnectionUtil.post method : started processing");
        SSLContext sslContext;
        try {
            sslContext = SSLContexts.custom()
                    .useProtocol("TLS")
                    .build();
            SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                    sslContext,
                    new String[]{"TLSv1.2"},
                    null,
                    SSLConnectionSocketFactory.getDefaultHostnameVerifier());

            HttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(f)
                    .build();
            HttpPost httppost = new HttpPost(url);

            // Add your data
            List<BasicNameValuePair> nameValuePairs = new ArrayList<>(params.size());

            for (Map.Entry<String, String> entry : params.entrySet()) {
                nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));

            }
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.DEF_CONTENT_CHARSET));

            response = httpClient.execute(httppost);
            log.debug("HTTPConnectionUtil.post method : Processing done.");

        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new IOException(GenericErrorCodeMessage.GENERIC_ERROR.toString(e.getMessage()));
        }
        return response;

    }

    /**
     * Gets the all headers.
     *
     * @return the all headers
     */
    public List<HeaderObj> getAllHeaders() {
        log.debug("HTTPConnectionUtil.getAllHeaders method : started processing");
        final Header[] allHeaders = response.getAllHeaders();

        final List<HeaderObj> ret = new ArrayList<>(allHeaders.length);
        for (final Header header : allHeaders) {

            ret.add(new HeaderObj(header.getName(), header.getValue()));
        }
        log.debug("HTTPConnectionUtil.getAllHeaders method : Processing done.");
        return ret;
    }

    /**
     * Gets the response code.
     *
     * @return the response code
     */
    public int getResponseCode() {
        return response.getStatusLine().getStatusCode();
    }

    /**
     * Gets the HTTP response content.
     *
     * @return the HTTP response content
     */
    public char[] getHTTPResponseContent() {
        log.debug("HTTPConnectionUtil.getHTTPResponseContent method : started processing");
        HttpEntity entity = response.getEntity();
        CharArrayWriter output = new CharArrayWriter();
        char[] cs = null;
        try (InputStreamReader in = new InputStreamReader(entity.getContent(), Charset.defaultCharset())) {
            char[] buffer = new char[BUFFER_SIZE];
            int n;
            while (-1 != (n = in.read(buffer))) {
                output.write(buffer, 0, n);
            }
            cs = output.toCharArray();

        } catch (IOException e) {
            log.error("Error occurred while trying to get the Http response content", e);
        }
        log.debug("HTTPConnectionUtil.getHTTPResponseContent method : Processing done.");
        return cs;
    }



    /**
     * Nested Class HeaderObj.
     */
    public static final class HeaderObj {

        /**
         * The name.
         */
        private String name;

        /**
         * The value.
         */
        private String value;

        /**
         * Instantiates a new header obj.
         *
         * @param name  the name
         * @param value the value
         */
        HeaderObj(final String name, final String value) {
            this.name = name;
            this.value = value;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return name + ": " + value;
        }
    }
}
