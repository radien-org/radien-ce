/*
 *
 *  * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
 *  * <p>
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  * <p>
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  * <p>
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.radien.ms.ecm.client.services;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.security.TokensPlaceHolder;
import io.radien.api.service.ecm.model.ContentType;
import io.radien.api.service.ecm.model.ContentVersion;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.ecm.model.GenericEnterpriseContent;
import io.radien.api.service.ecm.model.SystemContentVersion;
import io.radien.exception.SystemException;
import io.radien.ms.authz.client.UserClient;
import io.radien.ms.authz.security.AuthorizationChecker;
import io.radien.ms.ecm.client.controller.ContentResource;
import io.radien.ms.ecm.client.entities.DeleteContentFilter;
import io.radien.ms.ecm.client.util.ClientServiceUtil;
import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.util.List;
import javax.ws.rs.core.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ContentRESTServiceClientTest {
    @InjectMocks
    private ContentRESTServiceClient contentClient;

    @Mock
    private ClientServiceUtil clientServiceUtil;
    @Mock
    private ContentResource contentResource;
    @Mock
    private AuthorizationChecker authorizationChecker;
    @Mock
    private UserClient userClient;
    @Mock
    private TokensPlaceHolder tokensPlaceHolder;
    @Mock
    private OAFAccess oaf;

    private static final String NAME_PARAM = "name";
    private static final String VIEWID_PARAM = "viewId";
    private static final String LANGUAGE_PARAM = "language";
    private static final String CONTENT_TYPE_PARAM = "contentType";
    private static final String JCR_PATH_PARAM = "jcrPath";
    private static final String ABSOLUTE_PATH_PARAM = "/absolute/path/to/file";
    private static final String RELATIVE_PATH_PARAM = "relative/path";
    private static final String CLIENT_PARAM = "radien";

    @Before
    public void init() throws MalformedURLException {
        MockitoAnnotations.initMocks(this);
        when(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ECM))
                .thenReturn("http://a.url.com");
        when(clientServiceUtil.getContentResourceClient(anyString())).thenReturn(contentResource);

    }

    @Test
    public void testGetByViewIdAndLanguage() throws SystemException {
        EnterpriseContent content = new GenericEnterpriseContent(NAME_PARAM);
        content.setViewId(VIEWID_PARAM);
        content.setContentType(ContentType.HTML);
        JSONObject object = new JSONObject();
        object.put(NAME_PARAM, content.getName());
        object.put(VIEWID_PARAM, content.getViewId());
        object.put(CONTENT_TYPE_PARAM, content.getContentType().key());

        when(contentResource.getContentByViewIdLanguage(VIEWID_PARAM, LANGUAGE_PARAM))
                .thenReturn(Response.ok(new ByteArrayInputStream(object.toJSONString().getBytes())).build());
        EnterpriseContent result = contentClient.getByViewIdAndLanguage(VIEWID_PARAM, LANGUAGE_PARAM);
        assertEquals(content.getName(), result.getName());
        assertEquals(content.getViewId(), result.getViewId());
        assertEquals(content.getContentType().key(), result.getContentType().key());
    }

    @Test(expected = SystemException.class)
    public void testGetByViewIdAndLanguageException() throws MalformedURLException, SystemException {
        when(clientServiceUtil.getContentResourceClient(anyString()))
            .thenThrow(new MalformedURLException());
        contentClient.getByViewIdAndLanguage(VIEWID_PARAM, LANGUAGE_PARAM);
    }

    @Test
    public void testGetByViewIdAndLanguageContentTypeError() throws SystemException {
        EnterpriseContent content = new GenericEnterpriseContent(NAME_PARAM);
        content.setViewId(VIEWID_PARAM);
        content.setContentType(ContentType.ERROR);
        JSONObject object = new JSONObject();
        object.put(NAME_PARAM, content.getName());
        object.put(VIEWID_PARAM, content.getViewId());
        object.put(CONTENT_TYPE_PARAM, content.getContentType().key());

        when(contentResource.getContentByViewIdLanguage(VIEWID_PARAM, LANGUAGE_PARAM))
                .thenReturn(Response.ok(new ByteArrayInputStream(object.toJSONString().getBytes())).build());
        EnterpriseContent result = contentClient.getByViewIdAndLanguage(VIEWID_PARAM, LANGUAGE_PARAM);
        assertEquals(content.getName(), result.getName());
        assertEquals(content.getViewId(), result.getViewId());
        assertEquals(content.getContentType().key(), result.getContentType().key());
    }

    @Test
    public void testGetByViewIdAndLanguageContentTypeErrorResponse() throws SystemException {
        EnterpriseContent content = new GenericEnterpriseContent(NAME_PARAM);
        content.setViewId(VIEWID_PARAM);
        content.setContentType(ContentType.ERROR);
        JSONObject object = new JSONObject();
        object.put(NAME_PARAM, content.getName());
        object.put(VIEWID_PARAM, content.getViewId());
        object.put(CONTENT_TYPE_PARAM, content.getContentType().key());

        when(contentResource.getContentByViewIdLanguage(VIEWID_PARAM, LANGUAGE_PARAM))
                .thenReturn(Response.status(Response.Status.NOT_FOUND).entity("Not found").build());
        EnterpriseContent result = contentClient.getByViewIdAndLanguage(VIEWID_PARAM, LANGUAGE_PARAM);
        assertNull(result);
    }

    @Test
    public void testGetFileContent() throws SystemException {
        EnterpriseContent content = new GenericEnterpriseContent(NAME_PARAM);
        content.setViewId(VIEWID_PARAM);
        content.setContentType(ContentType.HTML);
        content.setJcrPath(ABSOLUTE_PATH_PARAM);
        JSONObject object = new JSONObject();
        object.put(NAME_PARAM, content.getName());
        object.put(VIEWID_PARAM, content.getViewId());
        object.put(JCR_PATH_PARAM, content.getJcrPath());
        object.put(CONTENT_TYPE_PARAM, content.getContentType().key());

        when(contentResource.getContentFile(ABSOLUTE_PATH_PARAM))
                .thenReturn(Response.ok(new ByteArrayInputStream(object.toJSONString().getBytes())).build());
        EnterpriseContent result = contentClient.getFileContent(ABSOLUTE_PATH_PARAM);
        assertEquals(content.getName(), result.getName());
        assertEquals(content.getViewId(), result.getViewId());
        assertEquals(content.getJcrPath(), result.getJcrPath());
        assertEquals(content.getContentType().key(), result.getContentType().key());
    }

    @Test(expected = SystemException.class)
    public void testGetFileContentException() throws MalformedURLException, SystemException {
        when(clientServiceUtil.getContentResourceClient(anyString()))
                .thenThrow(new MalformedURLException());
        contentClient.getFileContent(ABSOLUTE_PATH_PARAM);
    }

    @Test
    public void testGetFolderContents() throws SystemException {
        EnterpriseContent content = new GenericEnterpriseContent(NAME_PARAM);
        content.setViewId(VIEWID_PARAM);
        content.setContentType(ContentType.HTML);
        content.setJcrPath(ABSOLUTE_PATH_PARAM);
        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();
        object.put(NAME_PARAM, content.getName());
        object.put(VIEWID_PARAM, content.getViewId());
        object.put(JCR_PATH_PARAM, content.getJcrPath());
        object.put(CONTENT_TYPE_PARAM, content.getContentType().key());
        array.add(object);
        array.add(object);

        when(contentResource.getFolderContents(ABSOLUTE_PATH_PARAM))
                .thenReturn(Response.ok(new ByteArrayInputStream(array.toJSONString().getBytes())).build());
        List<EnterpriseContent> resultList = contentClient.getFolderContents(ABSOLUTE_PATH_PARAM);
        for(EnterpriseContent result : resultList) {
            assertEquals(content.getName(), result.getName());
            assertEquals(content.getViewId(), result.getViewId());
            assertEquals(content.getJcrPath(), result.getJcrPath());
            assertEquals(content.getContentType().key(), result.getContentType().key());
        }
    }

    @Test
    public void testGetFolderContentsError() throws SystemException {
        when(contentResource.getFolderContents(ABSOLUTE_PATH_PARAM))
                .thenReturn(Response.status(Response.Status.NOT_FOUND).entity("NOT FOUND").build());
        List<EnterpriseContent> resultList = contentClient.getFolderContents(ABSOLUTE_PATH_PARAM);
        assertEquals(0, resultList.size());
    }

    @Test(expected = SystemException.class)
    public void testGetFolderContentsException() throws MalformedURLException, SystemException {
        when(clientServiceUtil.getContentResourceClient(anyString()))
                .thenThrow(new MalformedURLException());
        contentClient.getFolderContents(ABSOLUTE_PATH_PARAM);
    }

    @Test
    public void testGetOrCreateDocumentsPathSuccess() throws SystemException {
        when(contentResource.getOrCreateDocumentsPath(CLIENT_PARAM, RELATIVE_PATH_PARAM))
                .thenReturn(Response.ok(ABSOLUTE_PATH_PARAM).build());
        String result = contentClient.getOrCreateDocumentsPath(CLIENT_PARAM, RELATIVE_PATH_PARAM);
        assertEquals(ABSOLUTE_PATH_PARAM, result);
    }

    @Test
    public void testGetOrCreateDocumentsPathError() throws SystemException {
        when(contentResource.getOrCreateDocumentsPath(CLIENT_PARAM, RELATIVE_PATH_PARAM))
                .thenReturn(Response.status(Response.Status.NOT_FOUND)
                        .entity("an error").build());
        String result = contentClient.getOrCreateDocumentsPath(CLIENT_PARAM, RELATIVE_PATH_PARAM);
        assertNull(result);
    }

    @Test(expected = SystemException.class)
    public void testGetOrCreateDocumentsPathException() throws MalformedURLException, SystemException {
        when(clientServiceUtil.getContentResourceClient(anyString()))
                .thenThrow(new MalformedURLException());
        contentClient.getOrCreateDocumentsPath(CLIENT_PARAM, RELATIVE_PATH_PARAM);
    }

    @Test
    public void testGetContentVersionsSuccess() throws SystemException {
        EnterpriseContent content = new GenericEnterpriseContent(NAME_PARAM);
        content.setViewId(VIEWID_PARAM);
        content.setContentType(ContentType.HTML);
        content.setJcrPath(ABSOLUTE_PATH_PARAM);
        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();
        object.put(NAME_PARAM, content.getName());
        object.put(VIEWID_PARAM, content.getViewId());
        object.put(JCR_PATH_PARAM, content.getJcrPath());
        object.put(CONTENT_TYPE_PARAM, content.getContentType().key());
        array.add(object);
        array.add(object);
        when(contentResource.getContentVersions(ABSOLUTE_PATH_PARAM))
                .thenReturn(Response.ok(new ByteArrayInputStream(array.toJSONString().getBytes())).build());
        List<EnterpriseContent> result = contentClient.getContentVersions(ABSOLUTE_PATH_PARAM);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals(NAME_PARAM, result.get(0).getName());
    }

    @Test
    public void testGetContentVersionsError() throws SystemException {
        when(contentResource.getContentVersions(ABSOLUTE_PATH_PARAM))
                .thenReturn(Response.status(Response.Status.NOT_FOUND)
                        .entity("an error").build());
        List<EnterpriseContent> result = contentClient.getContentVersions(ABSOLUTE_PATH_PARAM);
        assertTrue(result.isEmpty());
    }

    @Test(expected = SystemException.class)
    public void testGetContentVersionsException() throws MalformedURLException, SystemException {
        when(clientServiceUtil.getContentResourceClient(anyString()))
                .thenThrow(new MalformedURLException());
        contentClient.getContentVersions(ABSOLUTE_PATH_PARAM);
    }

    @Test
    public void testDeleteVersion() throws SystemException {
        when(contentResource.deleteVersionable(anyString(), any(ContentVersion.class)))
                .thenReturn(Response.ok().build());
        SystemContentVersion contentVersion = new ContentVersion("1.0.0");
        assertTrue(contentClient.deleteVersion(ABSOLUTE_PATH_PARAM, contentVersion));
    }

    @Test
    public void testDeleteVersionError() throws SystemException {
        when(contentResource.deleteVersionable(anyString(), any(ContentVersion.class)))
                .thenReturn(Response.status(Response.Status.NOT_FOUND)
                        .entity("an error").build());
        SystemContentVersion contentVersion = new ContentVersion("1.0.0");
        assertFalse(contentClient.deleteVersion(ABSOLUTE_PATH_PARAM, contentVersion));
    }

    @Test(expected = SystemException.class)
    public void testDeleteVersionException() throws MalformedURLException, SystemException {
        when(clientServiceUtil.getContentResourceClient(anyString()))
                .thenThrow(new MalformedURLException());
        SystemContentVersion contentVersion = new ContentVersion("1.0.0");
        contentClient.deleteVersion(ABSOLUTE_PATH_PARAM, contentVersion);
    }

    @Test
    public void testSaveContentSuccess() throws SystemException {
        EnterpriseContent content = new GenericEnterpriseContent(NAME_PARAM);
        when(contentResource.saveContent(anyString(), any(EnterpriseContent.class)))
                .thenReturn(Response.ok().build());
        assertTrue(contentClient.saveContent(CLIENT_PARAM, content));
    }

    @Test
    public void testSaveContentFail() throws SystemException {
        EnterpriseContent content = new GenericEnterpriseContent(NAME_PARAM);
        when(contentResource.saveContent(anyString(), any(EnterpriseContent.class)))
                .thenReturn(Response.status(Response.Status.NOT_FOUND).entity("Error saving").build());
        assertFalse(contentClient.saveContent(CLIENT_PARAM, content));
    }

    @Test(expected = SystemException.class)
    public void testSaveContentException() throws MalformedURLException, SystemException {
        EnterpriseContent content = new GenericEnterpriseContent(NAME_PARAM);
        when(clientServiceUtil.getContentResourceClient(anyString()))
                .thenThrow(new MalformedURLException());
        contentClient.saveContent(CLIENT_PARAM, content);
    }

    @Test
    public void testDeleteContentByPathSuccess() throws SystemException {
        when(contentResource.deleteContent(any(DeleteContentFilter.class)))
                .thenReturn(Response.ok().build());
        assertTrue(contentClient.deleteContentByPath(ABSOLUTE_PATH_PARAM));
    }

    @Test
    public void testDeleteContentByPathError() throws SystemException {
        when(contentResource.deleteContent(any(DeleteContentFilter.class)))
                .thenReturn(Response.status(Response.Status.NOT_FOUND).entity("NOT FOUND").build());
        assertFalse(contentClient.deleteContentByPath(ABSOLUTE_PATH_PARAM));
    }

    @Test(expected = SystemException.class)
    public void testDeleteContentByPathException() throws MalformedURLException, SystemException {
        when(clientServiceUtil.getContentResourceClient(anyString()))
                .thenThrow(new MalformedURLException());
        contentClient.deleteContentByPath(RELATIVE_PATH_PARAM);
    }

    @Test
    public void testDeleteContentByViewIDLanguageSuccess() throws SystemException {
        when(contentResource.deleteContent(any(DeleteContentFilter.class)))
                .thenReturn(Response.ok().build());
        assertTrue(contentClient.deleteContentByViewIDLanguage(VIEWID_PARAM, LANGUAGE_PARAM));
    }

    @Test
    public void testDeleteContentByViewIDLanguageError() throws SystemException {
        when(contentResource.deleteContent(any(DeleteContentFilter.class)))
                .thenReturn(Response.status(Response.Status.NOT_FOUND).entity("NOT FOUND").build());
        assertFalse(contentClient.deleteContentByViewIDLanguage(VIEWID_PARAM, LANGUAGE_PARAM));
    }

    @Test(expected = SystemException.class)
    public void testDeleteContentByViewIDLanguageException() throws MalformedURLException, SystemException {
        when(clientServiceUtil.getContentResourceClient(anyString()))
                .thenThrow(new MalformedURLException());
        contentClient.deleteContentByViewIDLanguage(VIEWID_PARAM, LANGUAGE_PARAM);
    }
}
