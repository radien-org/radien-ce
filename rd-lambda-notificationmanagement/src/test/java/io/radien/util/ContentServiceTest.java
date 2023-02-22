package io.radien.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.radien.Authenticator;
import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.service.ecm.model.Content;
import io.radien.api.service.ecm.model.EnterpriseContent;
import kong.unirest.HttpMethod;
import kong.unirest.MockClient;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContentServiceTest {

    @Mock
    private OAFAccess oaf;

    @Mock
    private Authenticator authenticator;

    @InjectMocks
    private ContentService contentService;

    @Test
    public void getContentByViewIdAndLanguage() throws IOException, ParseException, java.text.ParseException {
        String title = "title";
        String htmlContent = "body";
        EnterpriseContent content = new Content(title, htmlContent);

        when(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ECM, "http://host.docker.internal:8081/cms/v1")).thenReturn("http://host.docker.internal:8081/cms/v1");
        ObjectMapper mapper = new ObjectMapper();
        MockClient mock = MockClient.register();
        mock.expect(HttpMethod.GET, "http://host.docker.internal:8081/cms/v1/content/metadata")
                .thenReturn(mapper.writeValueAsString(content));
        EnterpriseContent mappedContent = contentService.getContentByViewIdAndLanguage("", "");

        assertEquals(title, mappedContent.getName());
        assertEquals(htmlContent, mappedContent.getHtmlContent());
    }
}