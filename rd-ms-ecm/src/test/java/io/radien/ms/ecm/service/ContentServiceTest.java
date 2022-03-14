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
package io.radien.ms.ecm.service;

import io.radien.api.service.ecm.exception.ContentNotAvailableException;
import io.radien.api.service.ecm.exception.ContentRepositoryNotAvailableException;
import io.radien.api.service.ecm.exception.ElementNotFoundException;
import io.radien.api.service.ecm.exception.InvalidClientException;
import io.radien.api.service.ecm.exception.NameNotValidException;
import io.radien.api.service.ecm.model.ContentType;
import io.radien.api.service.ecm.model.ContentVersion;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.ecm.model.GenericEnterpriseContent;
import io.radien.api.service.ecm.model.SystemContentVersion;
import io.radien.api.service.mail.model.MailType;
import io.radien.ms.ecm.ContentRepository;
import io.radien.ms.ecm.config.ConfigHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ContentServiceTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    private ContentService contentServiceAccess;

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private ConfigHandler configHandler;

    @Before
    public void init() throws NoSuchFieldException {
        when(configHandler.getDefaultLanguage()).thenReturn("en");
        when(configHandler.getSupportedClients()).thenReturn("radien");
    }

    @Test
    public void testGetChildrenFiles() throws NameNotValidException, ContentRepositoryNotAvailableException, ElementNotFoundException {
        EnterpriseContent result1 = createGenericEnterpriseContent("result1", ContentType.HTML, null, null);
        EnterpriseContent result2 = createGenericEnterpriseContent("result2", ContentType.HTML, null, null);
        when(contentRepository.getChildren(anyString()))
                .thenReturn(Arrays.asList(result1, result2));
        List<EnterpriseContent> resultList = contentServiceAccess.getChildrenFiles("viewId");
        assertNotNull(resultList);
        assertEquals(result1, resultList.get(0));
        assertEquals(result2, resultList.get(1));
    }

    @Test
    public void testGetChildrenFilesException() throws NameNotValidException, ContentRepositoryNotAvailableException, ElementNotFoundException {
        when(contentRepository.getChildren(anyString()))
                .thenThrow(new ContentRepositoryNotAvailableException());
        List<EnterpriseContent> resultList = contentServiceAccess.getChildrenFiles("viewId");
        assertNotNull(resultList);
        assertTrue(resultList.isEmpty());
    }

    @Test
    public void loadFile() throws NameNotValidException, ContentRepositoryNotAvailableException {
        EnterpriseContent result1 = createGenericEnterpriseContent("result1", ContentType.HTML, null, null);
        when(contentRepository.loadFile(anyString()))
                .thenReturn(result1);
        EnterpriseContent returned = contentServiceAccess.loadFile("path");
        assertEquals(result1, returned);
    }

    @Test(expected = ContentRepositoryNotAvailableException.class)
    public void loadFileException() throws NameNotValidException, ContentRepositoryNotAvailableException {
        when(contentRepository.loadFile(anyString()))
                .thenThrow(new ContentRepositoryNotAvailableException());
        contentServiceAccess.loadFile("path");
    }

    @Test
    public void testGetFolderContents() throws NameNotValidException, ContentRepositoryNotAvailableException, RepositoryException, ContentNotAvailableException {
        EnterpriseContent result1 = createGenericEnterpriseContent("result1", ContentType.HTML, null, null);
        EnterpriseContent result2 = createGenericEnterpriseContent("result2", ContentType.HTML, null, null);
        when(contentRepository.getFolderContents(anyString()))
                .thenReturn(Arrays.asList(result1, result2));
        List<EnterpriseContent> resultList = contentServiceAccess.getFolderContents("viewId");
        assertNotNull(resultList);
        assertEquals(result1, resultList.get(0));
        assertEquals(result2, resultList.get(1));
    }

    @Test(expected = ContentNotAvailableException.class)
    public void testGetFolderContentsError() throws ContentRepositoryNotAvailableException, RepositoryException, ContentNotAvailableException {
        when(contentRepository.getFolderContents(anyString()))
                .thenThrow(new PathNotFoundException());
        contentServiceAccess.getFolderContents("viewId");
    }

    @Test
    public void testGetContentVersions() throws NameNotValidException, ContentRepositoryNotAvailableException, RepositoryException, ContentNotAvailableException {
        EnterpriseContent result1 = createGenericEnterpriseContent("result1", ContentType.HTML, null, null);
        EnterpriseContent result2 = createGenericEnterpriseContent("result2", ContentType.HTML, null, null);
        when(contentRepository.getContentVersions(anyString()))
                .thenReturn(Arrays.asList(result1, result2));
        List<EnterpriseContent> resultList = contentServiceAccess.getContentVersions("viewId");
        assertNotNull(resultList);
        assertEquals(result1, resultList.get(0));
        assertEquals(result2, resultList.get(1));
    }

    @Test(expected = ContentNotAvailableException.class)
    public void testGetContentVersionsError() throws ContentRepositoryNotAvailableException, RepositoryException, ContentNotAvailableException {
        when(contentRepository.getFolderContents(anyString()))
                .thenThrow(new PathNotFoundException());
        contentServiceAccess.getFolderContents("viewId");
    }

    @Test
    public void testDeleteVersion() throws ContentRepositoryNotAvailableException, ContentNotAvailableException, RepositoryException {
        SystemContentVersion version = new ContentVersion("1.0.0");
        contentServiceAccess.deleteVersion("a/path", version);
        verify(contentRepository).deleteVersion("a/path", version);
    }

    @Test(expected = IllegalStateException.class)
    public void testDeleteVersionException() throws ContentRepositoryNotAvailableException, ContentNotAvailableException, RepositoryException {
        SystemContentVersion version = new ContentVersion("1.0.0");
        doThrow(new PathNotFoundException())
                .when(contentRepository).deleteVersion("a/path", version);
        contentServiceAccess.deleteVersion("a/path", version);
    }

    @Test
    public void testGetOrCreateDocumentsPath() throws ContentRepositoryNotAvailableException, RepositoryException,
            ContentNotAvailableException, InvalidClientException {
        contentServiceAccess.init();
        when(contentRepository.getOrCreateDocumentsPath("radien", "a/path"))
                .thenReturn("a/path");
        String result = contentServiceAccess.getOrCreateDocumentsPath("radien", "a/path");
        assertEquals("a/path", result);
    }

    @Test(expected = ContentNotAvailableException.class)
    public void testGetOrCreateDocumentsPathException() throws ContentRepositoryNotAvailableException, RepositoryException,
            ContentNotAvailableException, InvalidClientException {
        contentServiceAccess.init();
        when(contentRepository.getOrCreateDocumentsPath("radien", "a/path"))
                .thenThrow(new PathNotFoundException());
        contentServiceAccess.getOrCreateDocumentsPath("radien", "a/path");
    }

    @Test
    public void testSave() throws NameNotValidException, ContentRepositoryNotAvailableException, ContentNotAvailableException,
            RepositoryException, InvalidClientException {
        contentServiceAccess.init();
        EnterpriseContent content = createGenericEnterpriseContent("name", ContentType.HTML,
                null, null);
        contentServiceAccess.save("radien", content);
        verify(contentRepository).save("radien", content);
    }

    @Test(expected = ContentNotAvailableException.class)
    public void testSaveException() throws NameNotValidException, ContentRepositoryNotAvailableException,
            ContentNotAvailableException, RepositoryException, InvalidClientException {
        contentServiceAccess.init();
        EnterpriseContent content = createGenericEnterpriseContent("name", ContentType.HTML,
                null, null);
        doThrow(new PathNotFoundException())
                .when(contentRepository).save("radien", content);
        contentServiceAccess.save("radien", content);
    }

    @Test
    public void testDelete() throws NameNotValidException, ContentRepositoryNotAvailableException, ContentNotAvailableException, RepositoryException {
        EnterpriseContent content = createGenericEnterpriseContent("name", ContentType.HTML,
                "jcr/Path", "jcr/");
        contentServiceAccess.delete(content);
        verify(contentRepository).delete(content.getJcrPath());
    }

    @Test(expected = ContentNotAvailableException.class)
    public void testDeleteException() throws ContentRepositoryNotAvailableException, RepositoryException, ContentNotAvailableException {
        doThrow(new PathNotFoundException())
                .when(contentRepository).delete(anyString());
        contentServiceAccess.delete("a/path");
    }

    @Test
    public void testNotificationIdByType() {
        int value = 1;
        for(MailType type : MailType.values()) {
            String expected = "email-" + value++;
            assertEquals(expected, contentServiceAccess.getNotificationIdByType(type));
        }
    }

    @Test
    public void testNotificationIdByTypeNull() {
        String result = contentServiceAccess.getNotificationIdByType(null);
        assertNull(result);
    }

    @Test
    public void testGetByViewIdLanguageAvailable() throws NameNotValidException, ContentRepositoryNotAvailableException {
        EnterpriseContent result1 = createGenericEnterpriseContent("result1", ContentType.HTML, null, null);
        when(contentRepository.getByViewIdLanguage("result1", true, "de"))
                .thenReturn(Optional.of(Collections.singletonList(result1)));
        List<EnterpriseContent> resultList = contentServiceAccess.getByViewIdLanguage("result1", true, "de");
        assertFalse(resultList.isEmpty());
        assertEquals(result1, resultList.get(0));
    }

    @Test
    public void testGetByViewIdLanguageFallback() throws NameNotValidException, ContentRepositoryNotAvailableException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        getInitMethod().invoke(contentServiceAccess);
        EnterpriseContent result1 = createGenericEnterpriseContent("result1", ContentType.HTML, null, null);
        when(contentRepository.getByViewIdLanguage("result1", true, "de"))
                .thenReturn(Optional.empty());
        when(contentRepository.getByViewIdLanguage("result1", true, "en"))
                .thenReturn(Optional.of(Collections.singletonList(result1)));
        List<EnterpriseContent> resultList = contentServiceAccess.getByViewIdLanguage("result1", true, "de");
        assertFalse(resultList.isEmpty());
        assertEquals(result1, resultList.get(0));
    }

    @Test
    public void testGetByViewIdLanguageNoFallbackAvailable() throws NameNotValidException, ContentRepositoryNotAvailableException {
        when(contentRepository.getByViewIdLanguage("result1", true, "de"))
                .thenReturn(Optional.empty());
        when(contentRepository.getByViewIdLanguage("result1", true, "en"))
                .thenReturn(Optional.empty());
        List<EnterpriseContent> resultList = contentServiceAccess.getByViewIdLanguage("result1", true, "de");
        assertFalse(resultList.isEmpty());
        assertEquals(ContentType.ERROR, resultList.get(0).getContentType());
    }

    @Test
    public void testGetByViewIdLanguageException() throws NameNotValidException, ContentRepositoryNotAvailableException {
        when(contentRepository.getByViewIdLanguage("result1", true, "de"))
                .thenThrow(new ContentRepositoryNotAvailableException());
        List<EnterpriseContent> resultList = contentServiceAccess.getByViewIdLanguage("result1", true, "de");
        assertFalse(resultList.isEmpty());
        assertEquals(ContentType.ERROR, resultList.get(0).getContentType());
    }

    private EnterpriseContent createGenericEnterpriseContent(String name, ContentType contentType,
                                                             String jcrPath, String parentPath) throws NameNotValidException {
        EnterpriseContent result = new GenericEnterpriseContent(name);
        result.setContentType(contentType);
        result.setJcrPath(jcrPath);
        result.setParentPath(parentPath);
        return result;
    }

    private Method getInitMethod() throws NoSuchMethodException {
        Method initMethod = contentServiceAccess.getClass()
                .getDeclaredMethod("init");
        initMethod.setAccessible(true);
        return initMethod;
    }
}
