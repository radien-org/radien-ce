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

package io.radien.ms.ecm.controller;

import io.radien.api.service.ecm.ContentServiceAccess;
import io.radien.api.service.ecm.exception.ContentNotAvailableException;
import io.radien.api.service.ecm.exception.ContentRepositoryNotAvailableException;
import io.radien.api.service.ecm.exception.ElementNotFoundException;
import io.radien.api.service.ecm.exception.InvalidClientException;
import io.radien.api.service.ecm.exception.NameNotValidException;
import io.radien.api.service.ecm.model.ContentType;
import io.radien.api.service.ecm.model.ContentVersion;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.ecm.model.GenericEnterpriseContent;
import io.radien.exception.SystemException;
import io.radien.ms.ecm.client.entities.DeleteContentFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class ContentResourceClientTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    private ContentResourceClient controllerResource;

    @Mock
    private ContentServiceAccess contentServiceAccess;

    @Test
    public void testGetContentFile() throws NameNotValidException, ContentRepositoryNotAvailableException, ElementNotFoundException {
        EnterpriseContent result = new GenericEnterpriseContent("name");
        when(contentServiceAccess.loadFile(anyString()))
                .thenReturn(result);
        Response responseResult = controllerResource.getContentFile("/file/path");
        assertEquals(Response.Status.OK.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }

    @Test
    public void testGetContentFileNotFound() throws NameNotValidException, ContentRepositoryNotAvailableException, ElementNotFoundException {
        EnterpriseContent result = new GenericEnterpriseContent("name");
        when(contentServiceAccess.loadFile(anyString()))
                .thenThrow(new ElementNotFoundException("element not found"));
        Response responseResult = controllerResource.getContentFile("/file/path");
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }

    @Test
    public void testGetContentFileRepositoryNotAvailable() throws NameNotValidException, ContentRepositoryNotAvailableException, ElementNotFoundException {
        EnterpriseContent result = new GenericEnterpriseContent("name");
        when(contentServiceAccess.loadFile(anyString()))
                .thenThrow(new ContentRepositoryNotAvailableException());
        Response responseResult = controllerResource.getContentFile("/file/path");
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }

    @Test
    public void testGetContentByViewIdLanguage() throws NameNotValidException {
        EnterpriseContent result = new GenericEnterpriseContent("name");
        result.setContentType(ContentType.HTML);
        result.setViewId("viewId");
        result.setLanguage("en");
        when(contentServiceAccess.getByViewIdLanguage("viewId", true, "en"))
                .thenReturn(Collections.singletonList(result));
        Response responseResult = controllerResource.getContentByViewIdLanguage("viewId", "en");
        assertEquals(Response.Status.OK.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }

    @Test
    public void testGetContentByViewIdLanguageNotFound() throws NameNotValidException {
        EnterpriseContent result = new GenericEnterpriseContent("name");
        result.setContentType(ContentType.ERROR);
        result.setViewId("viewId");
        result.setLanguage("en");
        when(contentServiceAccess.getByViewIdLanguage("viewId", true, "en"))
                .thenReturn(Collections.singletonList(result));
        Response responseResult = controllerResource.getContentByViewIdLanguage("viewId", "en");
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }

    @Test
    public void testGetContentByViewIdLanguageRepositoryNotAvailable() throws NameNotValidException {
        when(contentServiceAccess.getByViewIdLanguage("viewId", true, "en"))
                .thenReturn(new ArrayList<>());
        Response responseResult = controllerResource.getContentByViewIdLanguage("viewId", "en");
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }

    @Test
    public void testGetFolderContents() throws Exception {
        EnterpriseContent result = new GenericEnterpriseContent("name");
        result.setContentType(ContentType.HTML);
        result.setViewId("viewId");
        result.setLanguage("en");
        when(contentServiceAccess.getFolderContents("a/path"))
                .thenReturn(Collections.singletonList(result));
        Response responseResult = controllerResource.getFolderContents("a/path");
        assertEquals(Response.Status.OK.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }

    @Test
    public void testGetFolderContentsRepositoryNotAvailable() throws Exception {
        EnterpriseContent result = new GenericEnterpriseContent("name");
        result.setContentType(ContentType.HTML);
        result.setViewId("viewId");
        result.setLanguage("en");
        when(contentServiceAccess.getFolderContents("a/path"))
                .thenThrow(new ContentRepositoryNotAvailableException());
        Response responseResult = controllerResource.getFolderContents("a/path");
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }

    @Test
    public void testGetFolderContentsContentNotAvailable() throws Exception {
        EnterpriseContent result = new GenericEnterpriseContent("name");
        result.setContentType(ContentType.HTML);
        result.setViewId("viewId");
        result.setLanguage("en");
        when(contentServiceAccess.getFolderContents("a/path"))
                .thenThrow(new ContentNotAvailableException());
        Response responseResult = controllerResource.getFolderContents("a/path");
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }

    @Test
    public void testGetOrCreateDocumentsPath() throws ContentRepositoryNotAvailableException, ContentNotAvailableException, InvalidClientException {
        String path = "/a/path";
        when(contentServiceAccess.getOrCreateDocumentsPath("radien", path))
                .thenReturn(path);
        Response responseResult = controllerResource.getOrCreateDocumentsPath("radien", path);
        assertEquals(Response.Status.OK.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }

    @Test
    public void testGetOrCreateDocumentsPathRepositoryNotAvailable() throws ContentRepositoryNotAvailableException, ContentNotAvailableException, InvalidClientException {
        String path = "/a/path";
        when(contentServiceAccess.getOrCreateDocumentsPath("radien", path))
                .thenThrow(new ContentRepositoryNotAvailableException());
        Response responseResult = controllerResource.getOrCreateDocumentsPath("radien", path);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }

    @Test
    public void testGetOrCreateDocumentsPathContentNotAvailable() throws ContentRepositoryNotAvailableException, ContentNotAvailableException, InvalidClientException {
        String path = "/a/path";
        when(contentServiceAccess.getOrCreateDocumentsPath("radien", path))
                .thenThrow(new ContentNotAvailableException());
        Response responseResult = controllerResource.getOrCreateDocumentsPath("radien", path);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }

    @Test
    public void testGetContentVersions() throws ContentRepositoryNotAvailableException, ContentNotAvailableException, NameNotValidException {
        List<EnterpriseContent> resultList = new ArrayList<>();
        EnterpriseContent content = new GenericEnterpriseContent("name");
        resultList.add(content);
        resultList.add(content);
        String path = "/a/path";
        when(contentServiceAccess.getContentVersions(path))
                .thenReturn(resultList);
        Response responseResult = controllerResource.getContentVersions(path);
        assertEquals(Response.Status.OK.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }

    @Test
    public void testGetContentVersionsRepositoryNotAvailable() throws ContentRepositoryNotAvailableException, ContentNotAvailableException {
        String path = "/a/path";
        when(contentServiceAccess.getContentVersions(path))
                .thenThrow(new ContentRepositoryNotAvailableException());
        Response responseResult = controllerResource.getContentVersions(path);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }

    @Test
    public void testGetContentVersionsContentNotAvailable() throws ContentRepositoryNotAvailableException, ContentNotAvailableException {
        String path = "/a/path";
        when(contentServiceAccess.getContentVersions(path))
                .thenThrow(new ContentNotAvailableException());
        Response responseResult = controllerResource.getContentVersions(path);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }

    @Test
    public void testDeleteVersion() {
        Response responseResult = controllerResource.deleteVersionable("", new ContentVersion("1.0.0"));
        assertEquals(Response.Status.OK.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }

    @Test
    public void testDeleteVersionRepositoryNotAvailable() throws ContentRepositoryNotAvailableException, ContentNotAvailableException {
        doThrow(new ContentRepositoryNotAvailableException())
                .when(contentServiceAccess).deleteVersion(anyString(), any(ContentVersion.class));
        Response responseResult = controllerResource.deleteVersionable("", new ContentVersion("1.0.0"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }

    @Test
    public void testDeleteVersionContentNotAvailable() throws ContentRepositoryNotAvailableException, ContentNotAvailableException {
        doThrow(new ContentNotAvailableException())
                .when(contentServiceAccess).deleteVersion(anyString(), any(ContentVersion.class));
        Response responseResult = controllerResource.deleteVersionable("", new ContentVersion("1.0.0"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }

    @Test
    public void testSave() throws Exception {
        EnterpriseContent content = new GenericEnterpriseContent("name");
        content.setContentType(ContentType.HTML);
        content.setViewId("viewId");
        content.setLanguage("en");
        Response responseResult = controllerResource.saveContent("radien", content);
        assertEquals(Response.Status.OK.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }

    @Test
    public void testSaveRepositoryNotAvailable() throws Exception {
        EnterpriseContent content = new GenericEnterpriseContent("name");
        content.setContentType(ContentType.HTML);
        content.setViewId("viewId");
        content.setLanguage("en");
        doThrow(new ContentRepositoryNotAvailableException()).when(contentServiceAccess).save("radien", content);
        Response responseResult = controllerResource.saveContent("radien", content);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }

    @Test
    public void testSaveContentNotAvailable() throws Exception {
        EnterpriseContent content = new GenericEnterpriseContent("name");
        content.setContentType(ContentType.HTML);
        content.setViewId("viewId");
        content.setLanguage("en");
        doThrow(new ContentNotAvailableException()).when(contentServiceAccess).save("radien", content);
        Response responseResult = controllerResource.saveContent("radien", content);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }

    @Test
    public void testDeleteByPath() {
        Response responseResult = controllerResource.deleteContent(new DeleteContentFilter("a/path"));
        assertEquals(Response.Status.OK.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }

    @Test
    public void testDeleteByPathException() throws SystemException {
        doThrow(new ContentRepositoryNotAvailableException()).when(contentServiceAccess).delete(anyString());
        Response responseResult = controllerResource.deleteContent(new DeleteContentFilter("a/path"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }

    @Test
    public void testDeleteContentViewIdLanguage() throws NameNotValidException {
        EnterpriseContent result = new GenericEnterpriseContent("name");
        result.setContentType(ContentType.HTML);
        result.setViewId("viewId");
        result.setLanguage("en");
        when(contentServiceAccess.getByViewIdLanguage("viewId", true, "en"))
                .thenReturn(Collections.singletonList(result));
        Response responseResult = controllerResource.deleteContent(new DeleteContentFilter("viewId", "en"));
        assertEquals(Response.Status.OK.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }

    @Test
    public void testDeleteContentViewIdLanguageRepositoryNotAvailable() throws NameNotValidException, ContentRepositoryNotAvailableException, ContentNotAvailableException {
        EnterpriseContent result = new GenericEnterpriseContent("name");
        result.setContentType(ContentType.HTML);
        result.setViewId("viewId");
        result.setLanguage("en");
        when(contentServiceAccess.getByViewIdLanguage("viewId", true, "en"))
                .thenReturn(Collections.singletonList(result));
        doThrow(new ContentRepositoryNotAvailableException())
                .when(contentServiceAccess).delete(result);
        Response responseResult = controllerResource.deleteContent(new DeleteContentFilter("viewId", "en"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }

    @Test
    public void testDeleteContentViewIdLanguageNotAvailable() throws NameNotValidException, ContentRepositoryNotAvailableException, ContentNotAvailableException {
        EnterpriseContent result = new GenericEnterpriseContent("name");
        result.setContentType(ContentType.HTML);
        result.setViewId("viewId");
        result.setLanguage("en");
        when(contentServiceAccess.getByViewIdLanguage("viewId", true, "en"))
                .thenReturn(Collections.singletonList(result));
        doThrow(new ContentNotAvailableException())
                .when(contentServiceAccess).delete(result);
        Response responseResult = controllerResource.deleteContent(new DeleteContentFilter("viewId", "en"));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }
}
