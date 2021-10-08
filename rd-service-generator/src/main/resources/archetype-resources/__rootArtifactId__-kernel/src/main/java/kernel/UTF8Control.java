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
package ${package}.kernel;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

/**
 * @author Rajesh Gavvala
 * @author Marco Weiland
 */
public class UTF8Control extends Control {

	private static final String DEFAULT_BUNDLE_EXTENSION = "properties";
	private static final long serialVersionUID = 6812608123262000033L;

	/**
	 * The below code is copied from default Control#newBundle() implementation.
	 * Only the PropertyResourceBundle line is changed to read the file as
	 * UTF-8.
	 */
	@Override
	public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
			throws IOException {

		String bundleName = toBundleName(baseName, locale);
		String resourceName = toResourceName(bundleName, DEFAULT_BUNDLE_EXTENSION);
		ResourceBundle bundle = null;
		InputStream stream = null;
		if (reload) {
			URL url = loader.getResource(resourceName);
			if (url != null) {
				URLConnection connection = url.openConnection();
				if (connection != null) {
					connection.setUseCaches(false);
					stream = connection.getInputStream();
				}
			}
		} else {
			stream = loader.getResourceAsStream(resourceName);
		}
		if (stream != null) {
			try (InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
				bundle = new PropertyResourceBundle(reader);
			} finally {
				stream.close();
			}
		}
		return bundle;
	}
}
