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

package io.radien.ms.ecm;

import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.service.ecm.exception.ContentNotAvailableException;
import io.radien.api.service.ecm.exception.ContentRepositoryNotAvailableException;
import io.radien.api.service.ecm.exception.ElementNotFoundException;
import io.radien.api.service.ecm.model.ContentType;
import io.radien.api.service.ecm.model.ContentVersion;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.ecm.model.Folder;
import io.radien.api.service.ecm.model.MandatoryEnterpriseContent;
import io.radien.api.service.ecm.model.SystemVersionableEnterpriseContent;
import io.radien.api.service.ecm.model.VersionableEnterpriseContent;
import io.radien.ms.ecm.constants.CmsConstants;
import io.radien.ms.ecm.domain.ContentDataProvider;
import io.radien.ms.ecm.util.ContentMappingUtils;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.jcr.GuestCredentials;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.commons.cnd.ParseException;
import org.eclipse.microprofile.config.Config;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.internal.util.reflection.FieldSetter;

import static com.mongodb.internal.connection.tlschannel.util.Util.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ContentRepositoryTest {
    @InjectMocks
    private ContentRepository contentRepository;
    @Mock
    private Config config;
    @Mock
    private OAFAccess oaf;
    @Spy
    private ContentMappingUtils contentMappingUtils;
    @Mock
    private ContentDataProvider dataProvider;

    private static boolean initialized = false;
    private static Session session;
    private static Repository transientRepository;

    private final static String fileArray = "101,120,97,109,112,108,101";

    @Before
    public void init() throws RepositoryException, NoSuchFieldException {
        MockitoAnnotations.initMocks(this);
        if(!initialized) {
            transientRepository = JcrUtils.getRepository();
            session = transientRepository.login(new GuestCredentials());
            initialized = true;
        }
        Field repositoryField = contentRepository.getClass().getDeclaredField("repository");
        FieldSetter.setField(contentRepository, repositoryField, transientRepository);

        when(oaf.getProperty(OAFProperties.SYSTEM_CMS_CFG_NODE_ROOT))
                .thenReturn("radien");
        when(config.getValue(CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_HTML, String.class))
                .thenReturn("rd_html");
        when(config.getValue(CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_NOTIFICATION, String.class))
                .thenReturn("rd_notifications");

    }

    @Test
    public void test001RegisterNodeTypes() throws ContentRepositoryNotAvailableException, RepositoryException, ParseException, IOException {
        registerNodeTypes();
        assertNotNull(session.getWorkspace().getNodeTypeManager().getNodeType("rd:NodeType"));
    }

    @Test
    public void test002SaveRootNode() throws ContentRepositoryNotAvailableException, RepositoryException {
        Folder rootFolder = new Folder("radien");
        rootFolder.setParentPath(contentRepository.getRootNodePath());
        rootFolder.setViewId(rootFolder.getName());

        contentRepository.save(rootFolder);
        assertEquals("/radien", rootFolder.getJcrPath());
        assertEquals("/", rootFolder.getParentPath());
    }

    @Test
    public void test003SaveChildNode() throws ContentRepositoryNotAvailableException, RepositoryException {
        Folder documentsFolder = new Folder("rd_documents");
        documentsFolder.setParentPath(contentRepository.getRootNodePath());
        documentsFolder.setViewId(documentsFolder.getName());

        contentRepository.save(documentsFolder);
        assertEquals("/rd_documents", documentsFolder.getJcrPath());
        assertEquals("/", documentsFolder.getParentPath());
    }

    @Test
    public void test004SaveMoveNode() throws ContentRepositoryNotAvailableException, RepositoryException {
        Folder documentsFolder = new Folder("rd_documents");
        documentsFolder.setParentPath(contentRepository.getRootNodePath() + "radien");
        documentsFolder.setViewId(documentsFolder.getName());

        contentRepository.save(documentsFolder);
        assertEquals("/radien/rd_documents", documentsFolder.getJcrPath());
        assertEquals("/radien", documentsFolder.getParentPath());
    }

    @Test
    public void test005GetViewIdLanguage() throws ContentRepositoryNotAvailableException, RepositoryException {
        Optional<List<EnterpriseContent>> optFoundNodes = contentRepository.getByViewIdLanguage("rd_documents", false, null);
        assertTrue(optFoundNodes.isPresent());
        List<EnterpriseContent> foundNodes = optFoundNodes.get();
        assertFalse(foundNodes.isEmpty());
        assertTrue(foundNodes.size() == 1);
        EnterpriseContent node = foundNodes.get(0);
        assertEquals("rd_documents", node.getName());
        assertEquals("rd_documents", node.getViewId());
        assertEquals("/radien", node.getParentPath());
        assertEquals("/radien/rd_documents", node.getJcrPath());
    }

    @Test
    public void test006SaveDocumentVersionable1() throws ContentRepositoryNotAvailableException, RepositoryException {
        when(oaf.getProperty(OAFProperties.valueOfKey(CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_DOCS)))
                .thenReturn("rd_documents");

        VersionableEnterpriseContent versionableEnterpriseContent = new VersionableEnterpriseContent();
        versionableEnterpriseContent.setName("name");
        versionableEnterpriseContent.setViewId("name-viewId");
        versionableEnterpriseContent.setActive(true);
        versionableEnterpriseContent.setSystem(true);
        versionableEnterpriseContent.setContentType(ContentType.DOCUMENT);
        versionableEnterpriseContent.setLanguage("en");
        versionableEnterpriseContent.setHtmlContent("v1");
        versionableEnterpriseContent.setVersionable(true);
        versionableEnterpriseContent.setVersionComment("first Version");
        versionableEnterpriseContent.setValidDate(new Date());
        versionableEnterpriseContent.setFile(ArrayUtils.toPrimitive(Arrays.stream(fileArray.split(","))
                .map(Byte::valueOf).collect(Collectors.toList()).toArray(new Byte[0])));

        contentRepository.save(versionableEnterpriseContent);
        assertEquals("/radien/rd_documents", versionableEnterpriseContent.getParentPath());
        assertEquals("/radien/rd_documents/name", versionableEnterpriseContent.getJcrPath());
    }

    @Test
    public void test006SaveDocumentVersionable2() throws ContentRepositoryNotAvailableException, RepositoryException {
        when(oaf.getProperty(OAFProperties.valueOfKey(CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_DOCS)))
                .thenReturn("rd_documents");

        VersionableEnterpriseContent versionableEnterpriseContent = new VersionableEnterpriseContent();
        versionableEnterpriseContent.setName("name");
        versionableEnterpriseContent.setViewId("name-viewId");
        versionableEnterpriseContent.setActive(true);
        versionableEnterpriseContent.setSystem(true);
        versionableEnterpriseContent.setContentType(ContentType.DOCUMENT);
        versionableEnterpriseContent.setLanguage("en");
        versionableEnterpriseContent.setHtmlContent("v2");
        versionableEnterpriseContent.setVersionable(true);
        versionableEnterpriseContent.setVersionComment("second Version");
        versionableEnterpriseContent.setValidDate(new Date());
        versionableEnterpriseContent.setFile(ArrayUtils.toPrimitive(Arrays.stream(fileArray.split(","))
                .map(Byte::valueOf).collect(Collectors.toList()).toArray(new Byte[0])));
        versionableEnterpriseContent.setVersion(new ContentVersion("1.2.0"));

        contentRepository.save(versionableEnterpriseContent);
        assertEquals("/radien/rd_documents", versionableEnterpriseContent.getParentPath());
        assertEquals("/radien/rd_documents/name", versionableEnterpriseContent.getJcrPath());
    }

    @Test
    public void test006SaveDocumentVersionable3() throws ContentRepositoryNotAvailableException, RepositoryException {
        when(oaf.getProperty(OAFProperties.valueOfKey(CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_DOCS)))
                .thenReturn("rd_documents");

        VersionableEnterpriseContent versionableEnterpriseContent = new VersionableEnterpriseContent();
        versionableEnterpriseContent.setName("name");
        versionableEnterpriseContent.setViewId("name-viewId");
        versionableEnterpriseContent.setActive(true);
        versionableEnterpriseContent.setSystem(true);
        versionableEnterpriseContent.setContentType(ContentType.DOCUMENT);
        versionableEnterpriseContent.setLanguage("en");
        versionableEnterpriseContent.setHtmlContent("v3");
        versionableEnterpriseContent.setVersionable(true);
        versionableEnterpriseContent.setVersionComment("third Version");
        versionableEnterpriseContent.setValidDate(new Date());
        versionableEnterpriseContent.setFile(ArrayUtils.toPrimitive(Arrays.stream(fileArray.split(","))
                .map(Byte::valueOf).collect(Collectors.toList()).toArray(new Byte[0])));
        versionableEnterpriseContent.setVersion(new ContentVersion("1.3.0"));

        contentRepository.save(versionableEnterpriseContent);
        assertEquals("/radien/rd_documents", versionableEnterpriseContent.getParentPath());
        assertEquals("/radien/rd_documents/name", versionableEnterpriseContent.getJcrPath());
    }

    @Test
    public void test007GetContentVersions() throws ContentRepositoryNotAvailableException, RepositoryException {
        List<EnterpriseContent> contentVersions = contentRepository
                .getContentVersions("/radien/rd_documents/name");
        assertNotNull(contentVersions);
        assertTrue(contentVersions.size() == 3);
        assertEquals("first Version", ((SystemVersionableEnterpriseContent) (contentVersions.get(0))).getVersionComment());
        assertEquals("second Version", ((SystemVersionableEnterpriseContent) (contentVersions.get(1))).getVersionComment());
        assertEquals("third Version", ((SystemVersionableEnterpriseContent) (contentVersions.get(2))).getVersionComment());
    }

    @Test
    public void test008DeleteContentVersion1() throws ContentRepositoryNotAvailableException, RepositoryException, ContentNotAvailableException {
        contentRepository.deleteVersion("/radien/rd_documents/name", new ContentVersion("1.2.0"));
        List<EnterpriseContent> contentVersions = contentRepository.getContentVersions("/radien/rd_documents/name");
        assertNotNull(contentVersions);
        assertTrue(contentVersions.size() == 2);
        assertEquals("first Version", ((SystemVersionableEnterpriseContent) (contentVersions.get(0))).getVersionComment());
        assertEquals("third Version", ((SystemVersionableEnterpriseContent) (contentVersions.get(1))).getVersionComment());
    }

    @Test
    public void test009DeleteContentVersion2() throws ContentRepositoryNotAvailableException, ContentNotAvailableException, RepositoryException {
        contentRepository.deleteVersion("/radien/rd_documents/name", new ContentVersion("1.3.0"));
        List<EnterpriseContent> contentVersions = contentRepository.getContentVersions("/radien/rd_documents/name");
        assertNotNull(contentVersions);
        assertTrue(contentVersions.size() == 1);
        assertEquals("first Version", ((SystemVersionableEnterpriseContent) (contentVersions.get(0))).getVersionComment());
    }

    @Test(expected = PathNotFoundException.class)
    public void test010DeleteContentVersionable() throws ContentRepositoryNotAvailableException, ContentNotAvailableException, RepositoryException {
        contentRepository.delete("/radien/rd_documents/name");
        List<EnterpriseContent> contentVersions = contentRepository.getContentVersions("/radien/rd_documents/name");
    }

    @Test
    public void test011SaveHTMLMandatory1() throws ContentRepositoryNotAvailableException, RepositoryException {
        when(oaf.getProperty(OAFProperties.valueOfKey(CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_HTML)))
                .thenReturn("rd_html");
        when(dataProvider.getSupportedLanguages())
                .thenReturn(Collections.singletonList("en"));
        Folder htmlFolder = new Folder("rd_html");
        htmlFolder.setParentPath(contentRepository.getRootNodePath() + "radien");
        htmlFolder.setViewId(htmlFolder.getName());

        contentRepository.save(htmlFolder);
        assertEquals("/radien/rd_html", htmlFolder.getJcrPath());
        assertEquals("/radien", htmlFolder.getParentPath());
        contentRepository.updateFolderSupportedLanguages("/radien/rd_html", "rd_html");

        MandatoryEnterpriseContent mandatoryEnterpriseContent = new MandatoryEnterpriseContent();
        mandatoryEnterpriseContent.setName("nameHtml");
        mandatoryEnterpriseContent.setViewId("nameHtml-viewId");
        mandatoryEnterpriseContent.setActive(true);
        mandatoryEnterpriseContent.setSystem(true);
        mandatoryEnterpriseContent.setContentType(ContentType.HTML);
        mandatoryEnterpriseContent.setLanguage("en");
        mandatoryEnterpriseContent.setHtmlContent("v1");
        mandatoryEnterpriseContent.setMandatoryApproval(true);
        mandatoryEnterpriseContent.setMandatoryView(true);
        contentRepository.save(mandatoryEnterpriseContent);
        assertEquals("/radien/rd_html/en", mandatoryEnterpriseContent.getParentPath());
        assertEquals("/radien/rd_html/en/nameHtml", mandatoryEnterpriseContent.getJcrPath());
    }

    @Test
    public void test011SaveHTMLMandatory2() throws ContentRepositoryNotAvailableException, RepositoryException {
        MandatoryEnterpriseContent mandatoryEnterpriseContent = new MandatoryEnterpriseContent();
        mandatoryEnterpriseContent.setName("nameHtml");
        mandatoryEnterpriseContent.setViewId("nameHtml-viewId-updated");
        mandatoryEnterpriseContent.setActive(true);
        mandatoryEnterpriseContent.setSystem(true);
        mandatoryEnterpriseContent.setContentType(ContentType.HTML);
        mandatoryEnterpriseContent.setLanguage("en");
        mandatoryEnterpriseContent.setHtmlContent("v2 update");
        mandatoryEnterpriseContent.setMandatoryApproval(true);
        mandatoryEnterpriseContent.setMandatoryView(true);
        mandatoryEnterpriseContent.setJcrPath("/radien/rd_html/en/nameHtml");
        contentRepository.save(mandatoryEnterpriseContent);
        assertEquals("/radien/rd_html/en", mandatoryEnterpriseContent.getParentPath());
        assertEquals("/radien/rd_html/en/nameHtml", mandatoryEnterpriseContent.getJcrPath());
    }

    @Test
    public void test012SaveDocumentMandatory() throws ContentRepositoryNotAvailableException, RepositoryException {
        when(oaf.getProperty(OAFProperties.valueOfKey(CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_DOCS)))
                .thenReturn("rd_documents");

        MandatoryEnterpriseContent mandatoryEnterpriseContent = new MandatoryEnterpriseContent();
        mandatoryEnterpriseContent.setName("nameDoc");
        mandatoryEnterpriseContent.setViewId("nameDoc-viewId");
        mandatoryEnterpriseContent.setActive(true);
        mandatoryEnterpriseContent.setSystem(true);
        mandatoryEnterpriseContent.setContentType(ContentType.DOCUMENT);
        mandatoryEnterpriseContent.setLanguage("en");
        mandatoryEnterpriseContent.setHtmlContent("v1");
        mandatoryEnterpriseContent.setMandatoryView(true);
        mandatoryEnterpriseContent.setMandatoryApproval(true);
        mandatoryEnterpriseContent.setFile(ArrayUtils.toPrimitive(Arrays.stream(fileArray.split(","))
               .map(Byte::valueOf).collect(Collectors.toList()).toArray(new Byte[0])));
        contentRepository.save(mandatoryEnterpriseContent);
        assertEquals("/radien/rd_documents", mandatoryEnterpriseContent.getParentPath());
        assertEquals("/radien/rd_documents/nameDoc", mandatoryEnterpriseContent.getJcrPath());
    }

    @Test
    public void test013LoadFile() throws ContentRepositoryNotAvailableException {
        MandatoryEnterpriseContent content = (MandatoryEnterpriseContent) contentRepository.loadFile("/radien/rd_documents/nameDoc");
        assertEquals("/radien/rd_documents", content.getParentPath());
        assertEquals("/radien/rd_documents/nameDoc", content.getJcrPath());
        assertTrue(content.getFileSize() > 0);
        assertNotNull(content.getFile());
    }

    @Test
    public void test014GetOrCreateDocumentsPathAndContents() throws ContentRepositoryNotAvailableException, RepositoryException {
        when(oaf.getProperty(OAFProperties.valueOfKey(CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_DOCS)))
                .thenReturn("rd_documents");

        String newPath = "/random_path/with_Folders/nested";
        contentRepository.getOrCreateDocumentsPath(newPath);
        List<EnterpriseContent> folderContents = contentRepository.getFolderContents("/radien/rd_documents" + newPath);
        assertFalse(folderContents.isEmpty());
        assertTrue(folderContents.size() == 1);
        assertTrue(folderContents.get(0).getContentType() == ContentType.FOLDER);
    }

    @Test
    public void test015GetChildrenTest() throws ContentRepositoryNotAvailableException, RepositoryException, ElementNotFoundException {
        when(oaf.getProperty(OAFProperties.valueOfKey(CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_DOCS)))
                .thenReturn("rd_documents");

        Collection<EnterpriseContent> folderContents = contentRepository.getChildren("nested");
        assertTrue(folderContents.isEmpty());
    }

    private void restartSession() throws RepositoryException {
        session.logout();
        session = transientRepository.login(new GuestCredentials());;
    }

    private void registerNodeTypes() throws ContentRepositoryNotAvailableException, RepositoryException {
        contentRepository.registerCNDNodeTypes(CmsConstants.PropertyKeys.OAF_NODE_TYPES);
        restartSession();
    }

}