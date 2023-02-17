package io.radien.util;

import com.google.inject.Singleton;
import io.radien.api.OAFProperties;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.ms.ecm.client.services.EnterpriseContentMapper;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Singleton
public class ContentService {

    private static final String CONTENT_PROPERTY = OAFProperties.SYSTEM_MS_ENDPOINT_ECM.propKey();
    private static final String FALLBACK_URL = "http://host.docker.internal:8081/cms/v1";


    public EnterpriseContent getContentByViewIdAndLanguage(String authorization, String viewId, String language) throws IOException, ParseException, java.text.ParseException {
        HttpResponse<byte[]> request = Unirest.get(getContentUrl().concat("/content/metadata"))
                .header("Authorization", authorization)
                .queryString("viewId", viewId)
                .queryString("lang", language)
                .asBytes();
        return EnterpriseContentMapper.map(new ByteArrayInputStream(request.getBody()));
    }

    private String getContentUrl(){
        String url;
        if((url = System.getenv(CONTENT_PROPERTY)) != null){
            return url;
        }
        return FALLBACK_URL;
    }
}
