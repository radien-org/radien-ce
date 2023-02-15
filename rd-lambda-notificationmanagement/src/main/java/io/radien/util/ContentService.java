package io.radien.util;

import io.radien.api.OAFProperties;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.ms.ecm.client.services.EnterpriseContentMapper;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ContentService {

    private static final String CONTENT_URL = System.getenv(OAFProperties.SYSTEM_MS_ENDPOINT_ECM.propKey());

    private ContentService(){}

    public static EnterpriseContent getContentByViewIdAndLanguage(String authorization, String viewId, String language) throws IOException, ParseException, java.text.ParseException {
        System.out.println("auth=" + authorization);
        HttpResponse<byte[]> request = Unirest.get(CONTENT_URL.concat("/content/metadata"))
                .header("Authorization", authorization)
                .queryString("viewId", viewId)
                .queryString("lang", language)
                .asBytes();
        System.out.println("Request: " + request);
        return EnterpriseContentMapper.map(new ByteArrayInputStream(request.getBody()));
    }
}
