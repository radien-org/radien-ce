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
package io.radien.ms.ecm.legacy;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.service.ecm.model.EnterpriseContent;

/**
 * @author Marco Weiland
 */
@RequestScoped
public class ContentDataProvider {
	protected static final Logger log = LoggerFactory.getLogger(ContentDataProvider.class);
	private static final String INIT_FILE = "jcr/content.json";
	private static final String S3_FILE_PREFIX = "content";

	@Inject
	private ContentFactory contentFactory;
	@Inject
	private S3FileUtil s3FileUtil;

    @Inject
    @ConfigProperty(name = "system.supported.languages")
    private String supportedLanguagesCSV;
	
			

	private static List<EnterpriseContent> contents = new ArrayList<>();


	@PostConstruct
	private void init() {
		log.info("Reading init file: {}", INIT_FILE);
		try {
			for (Object o : getJSONDataSourceArray()) {
				EnterpriseContent content = contentFactory.convertJSONObject(o);
				if (content != null) {
					contents.add(content);
				}
			}
		} catch (Exception e) {
			log.error("Error on data content provider init", e);
		}
	}

	private JSONArray getJSONDataSourceArray() throws IOException {
		List<String> contentFiles;
		if(!s3FileUtil.isLoadLocalFiles()) {
			s3FileUtil.getS3FilesStartingWith(S3_FILE_PREFIX);
			contentFiles = getSupportedLanguages()
					.stream().map(lang -> "content_" + lang.replace("-", "_") + ".json").collect(Collectors.toList());
		} else {
			contentFiles = getSupportedLanguages()
					.stream().map(lang -> "jcr/content_" + lang.replace("-", "_") + ".json").collect(Collectors.toList());
		}
		JSONArray a = null;
		for(String lang : contentFiles) {
			if(s3FileUtil.isLoadLocalFiles()) {
				a = parseLocalFile(a, lang);
			} else {
				a = parseS3File(a, lang);
			}
		}
		return a;
	}

	private JSONArray parseLocalFile(JSONArray jsonArray, String lang) {
		JSONParser parser = new JSONParser();
		try (InputStream stream = getClass().getClassLoader()
				.getResourceAsStream(lang)) {
			if (stream != null) {
				if(jsonArray == null) {
					jsonArray = (JSONArray) parser.parse(new InputStreamReader(stream));
				} else {
					jsonArray.addAll((JSONArray) parser.parse(new InputStreamReader(stream)));
				}
			}
		} catch (Exception e) {
			log.error("Error parsing " + lang + ". Skipping file.", e);
		}
		return jsonArray;
	}

	private JSONArray parseS3File(JSONArray jsonArray, String lang) {
		JSONParser parser = new JSONParser();
		try (InputStream stream = s3FileUtil.getClassLoaderForBucketFiles()
				.getResourceAsStream(lang)) {
			if (stream != null) {
				if(jsonArray == null) {
					jsonArray = (JSONArray) parser.parse(new InputStreamReader(stream));
				} else {
					jsonArray.addAll((JSONArray) parser.parse(new InputStreamReader(stream)));
				}
			}
		} catch (Exception e) {
			log.error("Error parsing " + lang + ". Skipping file.", e);
		}
		return jsonArray;
	}

	List<EnterpriseContent> getContents() {
		return contents;
	}

	public List<String> getSupportedLanguages() {
		return Arrays.asList(supportedLanguagesCSV.split(","));
	}

}
