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
 *
 */

package io.radien.ms.ecm.domain;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.SystemProperties;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.ms.ecm.util.ContentMappingUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ContentDataProvider {
    protected static final Logger log = LoggerFactory.getLogger(ContentDataProvider.class);
    private static final String INIT_FILE = "jcr/content.json";

    @Inject
    private ContentMappingUtils contentMappingUtils;
    @Inject
    private OAFAccess oafAccess;

    private String supportedLanguages;

    private static List<EnterpriseContent> contents = new ArrayList<>();

    @PostConstruct
    private void init() {
        supportedLanguages = oafAccess.getProperty(OAFProperties.SYSTEM_MS_CONFIG_SUPPORTED_LANG_ECM);
        log.info("Reading init file: {}", INIT_FILE);
        try {
            for (Object o : getJSONDataSourceArray()) {
                EnterpriseContent content = contentMappingUtils.convertSeederJSONObject((JSONObject) o);
                if (content != null) {
                    contents.add(content);
                }
            }
        } catch (Exception e) {
            log.error("Error on data content provider init", e);
        }
    }

    private JSONArray getJSONDataSourceArray() {
        List<String> contentFiles = getSupportedLanguages()
                .stream().map(lang -> "jcr/content_" + lang.replace("-", "_") + ".json").collect(Collectors.toList());
        JSONArray a = null;
        for(String lang : contentFiles) {
            JSONParser parser = new JSONParser();
            try (InputStream stream = getClass().getClassLoader()
                    .getResourceAsStream(lang)) {
                if (stream != null) {
                    if(a == null) {
                        a = (JSONArray) parser.parse(new InputStreamReader(stream));
                    } else {
                        a.addAll((JSONArray) parser.parse(new InputStreamReader(stream)));
                    }
                } else {
                    log.error("Content JSON not found: {}", lang);
                }
            } catch (Exception e) {
                log.error("Error parsing {}. Skipping file.", lang, e);
            }
        }
        return a;
    }

    public List<EnterpriseContent> getContents() {
        return contents;
    }

    public List<String> getSupportedLanguages() {
        return Arrays.asList(supportedLanguages.split(","));
    }
}
