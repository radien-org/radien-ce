package io.radien.ms.ecm.domain;

import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.ms.ecm.factory.ContentFactory;
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
    private ContentFactory contentFactory;
    @Inject
    @ConfigProperty(name = "system.supported.languages")
    private String supportedLanguages;

    private static List<EnterpriseContent> contents = new ArrayList<>();

    @PostConstruct
    private void init() {
        log.info("Reading init file: {}", INIT_FILE);
        try {
            for (Object o : getJSONDataSourceArray()) {
                EnterpriseContent content = contentFactory.convertJSONObject((JSONObject)o);
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
