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
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.ecm.model.GenericEnterpriseContent;
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

    @Before
    public void init() throws MalformedURLException {
        MockitoAnnotations.initMocks(this);
        when(oaf.getProperty(OAFProperties.SYSTEM_MS_ENDPOINT_ECM))
                .thenReturn("http://a.url.com");
        when(clientServiceUtil.getResourceClient(anyString())).thenReturn(contentResource);

    }

    @Test
    public void testGetByViewIdAndLanguage() throws SystemException {
        EnterpriseContent content = new GenericEnterpriseContent("name");
        content.setViewId("viewId");
        content.setContentType(ContentType.HTML);
        JSONObject object = new JSONObject();
        object.put("name", content.getName());
        object.put("viewId", content.getViewId());
        object.put("contentType", content.getContentType().key());

        when(contentResource.getContentByViewIdLanguage("viewId", "language"))
                .thenReturn(Response.ok(new ByteArrayInputStream(object.toJSONString().getBytes())).build());
        EnterpriseContent result = contentClient.getByViewIdAndLanguage("viewId", "language");
        assertEquals(content.getName(), result.getName());
        assertEquals(content.getViewId(), result.getViewId());
        assertEquals(content.getContentType().key(), result.getContentType().key());
    }

    @Test(expected = SystemException.class)
    public void testGetByViewIdAndLanguageException() throws MalformedURLException, SystemException {
        when(clientServiceUtil.getResourceClient(anyString()))
            .thenThrow(new MalformedURLException());
        contentClient.getByViewIdAndLanguage("viewId", "language");
    }

    @Test
    public void testGetByViewIdAndLanguageContentTypeError() throws SystemException {
        EnterpriseContent content = new GenericEnterpriseContent("name");
        content.setViewId("viewId");
        content.setContentType(ContentType.ERROR);
        JSONObject object = new JSONObject();
        object.put("name", content.getName());
        object.put("viewId", content.getViewId());
        object.put("contentType", content.getContentType().key());

        when(contentResource.getContentByViewIdLanguage("viewId", "language"))
                .thenReturn(Response.ok(new ByteArrayInputStream(object.toJSONString().getBytes())).build());
        EnterpriseContent result = contentClient.getByViewIdAndLanguage("viewId", "language");
        assertEquals(content.getName(), result.getName());
        assertEquals(content.getViewId(), result.getViewId());
        assertEquals(content.getContentType().key(), result.getContentType().key());
    }

    @Test
    public void testGetByViewIdAndLanguageContentTypeErrorResponse() throws SystemException {
        EnterpriseContent content = new GenericEnterpriseContent("name");
        content.setViewId("viewId");
        content.setContentType(ContentType.ERROR);
        JSONObject object = new JSONObject();
        object.put("name", content.getName());
        object.put("viewId", content.getViewId());
        object.put("contentType", content.getContentType().key());

        when(contentResource.getContentByViewIdLanguage("viewId", "language"))
                .thenReturn(Response.status(Response.Status.NOT_FOUND).entity("Not found").build());
        EnterpriseContent result = contentClient.getByViewIdAndLanguage("viewId", "language");
        assertNull(result);
    }

    @Test
    public void testGetFileContent() throws SystemException {
        EnterpriseContent content = new GenericEnterpriseContent("name");
        content.setViewId("viewId");
        content.setContentType(ContentType.HTML);
        content.setJcrPath("/absolute/path/to/file");
        JSONObject object = new JSONObject();
        object.put("name", content.getName());
        object.put("viewId", content.getViewId());
        object.put("jcrPath", content.getJcrPath());
        object.put("contentType", content.getContentType().key());

        when(contentResource.getContentFile("/absolute/path/to/file"))
                .thenReturn(Response.ok(new ByteArrayInputStream(object.toJSONString().getBytes())).build());
        EnterpriseContent result = contentClient.getFileContent("/absolute/path/to/file");
        assertEquals(content.getName(), result.getName());
        assertEquals(content.getViewId(), result.getViewId());
        assertEquals(content.getJcrPath(), result.getJcrPath());
        assertEquals(content.getContentType().key(), result.getContentType().key());
    }

    @Test(expected = SystemException.class)
    public void testGetFileContentException() throws MalformedURLException, SystemException {
        when(clientServiceUtil.getResourceClient(anyString()))
                .thenThrow(new MalformedURLException());
        contentClient.getFileContent("/absolute/path/to/file");
    }

    @Test
    public void testGetFolderContents() throws SystemException {
        EnterpriseContent content = new GenericEnterpriseContent("name");
        content.setViewId("viewId");
        content.setContentType(ContentType.HTML);
        content.setJcrPath("/absolute/path/to/file");
        JSONArray array = new JSONArray();
        JSONObject object = new JSONObject();
        object.put("name", content.getName());
        object.put("viewId", content.getViewId());
        object.put("jcrPath", content.getJcrPath());
        object.put("contentType", content.getContentType().key());
        array.add(object);
        array.add(object);

        when(contentResource.getFolderContents("/absolute/path/to/file"))
                .thenReturn(Response.ok(new ByteArrayInputStream(array.toJSONString().getBytes())).build());
        List<EnterpriseContent> resultList = contentClient.getFolderContents("/absolute/path/to/file");
        for(EnterpriseContent result : resultList) {
            assertEquals(content.getName(), result.getName());
            assertEquals(content.getViewId(), result.getViewId());
            assertEquals(content.getJcrPath(), result.getJcrPath());
            assertEquals(content.getContentType().key(), result.getContentType().key());
        }
    }

    @Test
    public void testGetFolderContentsError() throws SystemException {
        when(contentResource.getFolderContents("/absolute/path/to/file"))
                .thenReturn(Response.status(Response.Status.NOT_FOUND).entity("NOT FOUND").build());
        List<EnterpriseContent> resultList = contentClient.getFolderContents("/absolute/path/to/file");
        assertEquals(0, resultList.size());
    }

    @Test(expected = SystemException.class)
    public void testGetFolderContentsException() throws MalformedURLException, SystemException {
        when(clientServiceUtil.getResourceClient(anyString()))
                .thenThrow(new MalformedURLException());
        contentClient.getFolderContents("/absolute/path/to/file");
    }

    @Test
    public void testGetOrCreateDocumentsPathSuccess() throws SystemException {
        when(contentResource.getOrCreateDocumentsPath("relative/path"))
                .thenReturn(Response.ok("/abolute/path/relative/path").build());
        String result = contentClient.getOrCreateDocumentsPath("relative/path");
        assertEquals("/abolute/path/relative/path", result);
    }

    @Test
    public void testGetOrCreateDocumentsPathError() throws SystemException {
        when(contentResource.getOrCreateDocumentsPath("relative/path"))
                .thenReturn(Response.status(Response.Status.NOT_FOUND)
                        .entity("an error").build());
        String result = contentClient.getOrCreateDocumentsPath("relative/path");
        assertNull(result);
    }

    @Test(expected = SystemException.class)
    public void testGetOrCreateDocumentsPathException() throws MalformedURLException, SystemException {
        when(clientServiceUtil.getResourceClient(anyString()))
                .thenThrow(new MalformedURLException());
        contentClient.getOrCreateDocumentsPath("relative/path");
    }

    @Test
    public void testSaveContentSuccess() throws SystemException {
        EnterpriseContent content = new GenericEnterpriseContent("name");
        when(contentResource.saveContent(any(EnterpriseContent.class)))
                .thenReturn(Response.ok().build());
        assertTrue(contentClient.saveContent(content));
    }

    @Test
    public void testSaveContentFail() throws SystemException {
        EnterpriseContent content = new GenericEnterpriseContent("name");
        when(contentResource.saveContent(any(EnterpriseContent.class)))
                .thenReturn(Response.status(Response.Status.NOT_FOUND).entity("Error saving").build());
        assertFalse(contentClient.saveContent(content));
    }

    @Test(expected = SystemException.class)
    public void testSaveContentException() throws MalformedURLException, SystemException {
        EnterpriseContent content = new GenericEnterpriseContent("name");
        when(clientServiceUtil.getResourceClient(anyString()))
                .thenThrow(new MalformedURLException());
        contentClient.saveContent(content);
    }

    @Test
    public void testDeleteContentByPathSuccess() throws SystemException {
        when(contentResource.deleteContent(any(DeleteContentFilter.class)))
                .thenReturn(Response.ok().build());
        assertTrue(contentClient.deleteContentByPath("/abolute/path/relative/path"));
    }

    @Test
    public void testDeleteContentByPathError() throws SystemException {
        when(contentResource.deleteContent(any(DeleteContentFilter.class)))
                .thenReturn(Response.status(Response.Status.NOT_FOUND).entity("NOT FOUND").build());
        assertFalse(contentClient.deleteContentByPath("/abolute/path/relative/path"));
    }

    @Test(expected = SystemException.class)
    public void testDeleteContentByPathException() throws MalformedURLException, SystemException {
        when(clientServiceUtil.getResourceClient(anyString()))
                .thenThrow(new MalformedURLException());
        contentClient.deleteContentByPath("relative/path");
    }

    @Test
    public void testDeleteContentByViewIDLanguageSuccess() throws SystemException {
        when(contentResource.deleteContent(any(DeleteContentFilter.class)))
                .thenReturn(Response.ok().build());
        assertTrue(contentClient.deleteContentByViewIDLanguage("viewId", "language"));
    }

    @Test
    public void testDeleteContentByViewIDLanguageError() throws SystemException {
        when(contentResource.deleteContent(any(DeleteContentFilter.class)))
                .thenReturn(Response.status(Response.Status.NOT_FOUND).entity("NOT FOUND").build());
        assertFalse(contentClient.deleteContentByViewIDLanguage("viewId", "language"));
    }

    @Test(expected = SystemException.class)
    public void testDeleteContentByViewIDLanguageException() throws MalformedURLException, SystemException {
        when(clientServiceUtil.getResourceClient(anyString()))
                .thenThrow(new MalformedURLException());
        contentClient.deleteContentByViewIDLanguage("viewId", "language");
    }
}
