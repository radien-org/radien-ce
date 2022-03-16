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
import io.radien.api.service.ecm.exception.ContentException;
import io.radien.api.service.ecm.exception.ContentNotAvailableException;
import io.radien.api.service.ecm.exception.ContentRepositoryNotAvailableException;
import io.radien.api.service.ecm.exception.NameNotValidException;
import io.radien.api.service.ecm.model.ContentType;
import io.radien.api.service.ecm.model.ContentVersion;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.ecm.model.GenericEnterpriseContent;
import io.radien.ms.ecm.client.entities.DeleteContentFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.core.Response;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
    public void testGetContentFile() throws NameNotValidException {
        EnterpriseContent result = new GenericEnterpriseContent("name");
        when(contentServiceAccess.loadFile(anyString()))
                .thenReturn(result);
        Response responseResult = controllerResource.getContentFile("/file/path");
        assertEquals(Response.Status.OK.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }

    @Test(expected = ContentException.class)
    public void testGetContentFileNotFound() {
        when(contentServiceAccess.loadFile(anyString()))
                .thenThrow(new ContentException("element not found"));
        controllerResource.getContentFile("/file/path");
    }

    @Test(expected = ContentNotAvailableException.class)
    public void testGetContentFileRepositoryNotAvailable() {
        when(contentServiceAccess.loadFile(anyString()))
                .thenThrow(new ContentNotAvailableException());
        controllerResource.getContentFile("/file/path");
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
    public void testGetContentByViewIdLanguageRepositoryNotAvailable() {
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

    @Test(expected = ContentRepositoryNotAvailableException.class)
    public void testGetFolderContentsRepositoryNotAvailable() throws Exception {
        EnterpriseContent result = new GenericEnterpriseContent("name");
        result.setContentType(ContentType.HTML);
        result.setViewId("viewId");
        result.setLanguage("en");
        when(contentServiceAccess.getFolderContents("a/path"))
                .thenThrow(new ContentRepositoryNotAvailableException());
        controllerResource.getFolderContents("a/path");
    }

    @Test(expected = ContentNotAvailableException.class)
    public void testGetFolderContentsContentNotAvailable() throws Exception {
        EnterpriseContent result = new GenericEnterpriseContent("name");
        result.setContentType(ContentType.HTML);
        result.setViewId("viewId");
        result.setLanguage("en");
        when(contentServiceAccess.getFolderContents("a/path"))
                .thenThrow(new ContentNotAvailableException());
        controllerResource.getFolderContents("a/path");
    }

    @Test
    public void testGetOrCreateDocumentsPath() {
        String path = "/a/path";
        when(contentServiceAccess.getOrCreateDocumentsPath("radien", path))
                .thenReturn(path);
        Response responseResult = controllerResource.getOrCreateDocumentsPath("radien", path);
        assertEquals(Response.Status.OK.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }

    @Test(expected = ContentRepositoryNotAvailableException.class)
    public void testGetOrCreateDocumentsPathRepositoryNotAvailable() {
        String path = "/a/path";
        when(contentServiceAccess.getOrCreateDocumentsPath("radien", path))
                .thenThrow(new ContentRepositoryNotAvailableException());
        controllerResource.getOrCreateDocumentsPath("radien", path);
    }

    @Test(expected = ContentNotAvailableException.class)
    public void testGetOrCreateDocumentsPathContentNotAvailable() {
        String path = "/a/path";
        when(contentServiceAccess.getOrCreateDocumentsPath("radien", path))
                .thenThrow(new ContentNotAvailableException());
        controllerResource.getOrCreateDocumentsPath("radien", path);
    }

    @Test
    public void testGetContentVersions() throws NameNotValidException {
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

    @Test(expected = ContentRepositoryNotAvailableException.class)
    public void testGetContentVersionsRepositoryNotAvailable() {
        String path = "/a/path";
        when(contentServiceAccess.getContentVersions(path))
                .thenThrow(new ContentRepositoryNotAvailableException());
        controllerResource.getContentVersions(path);
    }

    @Test(expected = ContentNotAvailableException.class)
    public void testGetContentVersionsContentNotAvailable() {
        String path = "/a/path";
        when(contentServiceAccess.getContentVersions(path))
                .thenThrow(new ContentNotAvailableException());
        controllerResource.getContentVersions(path);
    }

    @Test
    public void testDeleteVersion() {
        Response responseResult = controllerResource.deleteVersionable("", new ContentVersion("1.0.0"));
        assertEquals(Response.Status.OK.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }

    @Test(expected = ContentRepositoryNotAvailableException.class)
    public void testDeleteVersionRepositoryNotAvailable() {
        doThrow(new ContentRepositoryNotAvailableException())
                .when(contentServiceAccess).deleteVersion(anyString(), any(ContentVersion.class));
        controllerResource.deleteVersionable("", new ContentVersion("1.0.0"));
    }

    @Test(expected = ContentNotAvailableException.class)
    public void testDeleteVersionContentNotAvailable() {
        doThrow(new ContentNotAvailableException())
                .when(contentServiceAccess).deleteVersion(anyString(), any(ContentVersion.class));
        controllerResource.deleteVersionable("", new ContentVersion("1.0.0"));
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

    @Test(expected = ContentRepositoryNotAvailableException.class)
    public void testSaveRepositoryNotAvailable() throws Exception {
        EnterpriseContent content = new GenericEnterpriseContent("name");
        content.setContentType(ContentType.HTML);
        content.setViewId("viewId");
        content.setLanguage("en");
        doThrow(new ContentRepositoryNotAvailableException()).when(contentServiceAccess).save("radien", content);
        controllerResource.saveContent("radien", content);
    }

    @Test(expected = ContentNotAvailableException.class)
    public void testSaveContentNotAvailable() throws Exception {
        EnterpriseContent content = new GenericEnterpriseContent("name");
        content.setContentType(ContentType.HTML);
        content.setViewId("viewId");
        content.setLanguage("en");
        doThrow(new ContentNotAvailableException()).when(contentServiceAccess).save("radien", content);
        controllerResource.saveContent("radien", content);
    }

    @Test
    public void testDeleteByPath() {
        Response responseResult = controllerResource.deleteContent(new DeleteContentFilter("a/path"));
        assertEquals(Response.Status.OK.getStatusCode(), responseResult.getStatusInfo().getStatusCode());
    }

    @Test(expected = ContentRepositoryNotAvailableException.class)
    public void testDeleteByPathException() {
        doThrow(new ContentRepositoryNotAvailableException()).when(contentServiceAccess).delete(anyString());
        controllerResource.deleteContent(new DeleteContentFilter("a/path"));
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

    @Test(expected = ContentRepositoryNotAvailableException.class)
    public void testDeleteContentViewIdLanguageRepositoryNotAvailable() throws NameNotValidException {
        EnterpriseContent result = new GenericEnterpriseContent("name");
        result.setContentType(ContentType.HTML);
        result.setViewId("viewId");
        result.setLanguage("en");
        when(contentServiceAccess.getByViewIdLanguage("viewId", true, "en"))
                .thenReturn(Collections.singletonList(result));
        doThrow(new ContentRepositoryNotAvailableException())
                .when(contentServiceAccess).delete(result);
        controllerResource.deleteContent(new DeleteContentFilter("viewId", "en"));
    }

    @Test(expected = ContentNotAvailableException.class)
    public void testDeleteContentViewIdLanguageNotAvailable() throws NameNotValidException {
        EnterpriseContent result = new GenericEnterpriseContent("name");
        result.setContentType(ContentType.HTML);
        result.setViewId("viewId");
        result.setLanguage("en");
        when(contentServiceAccess.getByViewIdLanguage("viewId", true, "en"))
                .thenReturn(Collections.singletonList(result));
        doThrow(new ContentNotAvailableException())
                .when(contentServiceAccess).delete(result);
        controllerResource.deleteContent(new DeleteContentFilter("viewId", "en"));
    }
}
