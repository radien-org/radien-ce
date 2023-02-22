package io.radien.lambda.notificationmanagement.util;

import com.google.inject.Singleton;
import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.ms.ecm.client.services.EnterpriseContentMapper;
import java.text.MessageFormat;
import javax.inject.Inject;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Singleton
public class ContentService {

    private static final String FALLBACK_URL = "http://host.docker.internal:8081/cms/v1";

    private final Authenticator authenticator;

    private final OAFAccess oaf;

    @Inject
    public ContentService(Authenticator authenticator, OAFAccess oaf) {
        this.authenticator = authenticator;
        this.oaf = oaf;
        authenticator.login();
    }


    public EnterpriseContent getContentByViewIdAndLanguage(String viewId, String language) throws IOException, ParseException, java.text.ParseException {
        String endpointUrl = MessageFormat.format("{0}/content/metadata", oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ECM, FALLBACK_URL));
        String authorization = authenticator.getAuthorization();
        HttpResponse<byte[]> request = Unirest.get(endpointUrl)
                .header("Authorization", authorization)
                .queryString("viewId", viewId)
                .queryString("lang", language)
                .asBytes();
        //System.out.println("Request body: " + request.getBody());
        return EnterpriseContentMapper.map(new ByteArrayInputStream(request.getBody()));
    }
}
