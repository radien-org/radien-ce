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
package io.radien.api.service.ecm.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.radien.api.service.ecm.model.*;

/**
 * Deserializer or parser from json parser into Enterprise Content object
 *
 * @author Marco Weiland
 */
public class EnterpriseContentDeserializer extends JsonDeserializer<EnterpriseContent> {

	/**
	 * Converts a received json parser into a enterprise content
	 * @param jp to be parsed
	 * @param context to be used
	 * @return a new constructed and ready to use enterprise content object
	 * @throws IOException in case of any issue while parsing or reading the json information
	 */
	@Override
	public EnterpriseContent deserialize(JsonParser jp, DeserializationContext context) throws IOException {
		ObjectMapper mapper = (ObjectMapper) jp.getCodec();
		ObjectNode root = mapper.readTree(jp);
		if (root != null) {

			ContentType contentType = ContentType.getByKey(root.get("contentType").asText());

			switch (contentType) {
				case IMAGE:
				case DOCUMENT:
					return mapper.readValue(root.toString(), Document.class);
				case FOLDER:
					return mapper.readValue(root.toString(), Folder.class);
				case HTML:
				case ERROR:
				case NEWS_FEED:
				case NOTIFICATION:
				case TAG:
				default:
					return mapper.readValue(root.toString(), GenericEnterpriseContent.class);

				}

		}
		return null;
	}
}
