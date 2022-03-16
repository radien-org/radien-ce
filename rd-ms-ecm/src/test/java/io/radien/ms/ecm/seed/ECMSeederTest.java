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
package io.radien.ms.ecm.seed;

import io.radien.api.service.ecm.ContentServiceAccess;
import io.radien.api.service.ecm.exception.ContentNotAvailableException;
import io.radien.api.service.ecm.exception.ContentRepositoryNotAvailableException;
import io.radien.api.service.ecm.exception.InvalidClientException;
import io.radien.api.service.ecm.exception.NameNotValidException;
import io.radien.api.service.ecm.model.ContentType;
import io.radien.api.service.ecm.model.ContentVersion;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.ecm.model.Folder;
import io.radien.api.service.ecm.model.GenericEnterpriseContent;
import io.radien.api.service.ecm.model.VersionableEnterpriseContent;
import io.radien.api.service.i18n.I18NServiceAccess;
import io.radien.ms.ecm.ContentRepository;
import io.radien.ms.ecm.config.ConfigHandler;
import io.radien.ms.ecm.constants.CmsConstants;
import io.radien.ms.ecm.domain.ContentDataProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.jcr.RepositoryException;
import org.eclipse.microprofile.config.Config;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ECMSeederTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    private ECMSeeder ecmSeeder;

    @Mock
    private ContentRepository contentRepository;
    @Mock
    private ContentServiceAccess contentServiceAccess;
    @Mock
    private ContentDataProvider contentDataProvider;
    @Mock
    private ConfigHandler configHandler;
    @Mock
    private I18NServiceAccess i18NServiceAccess;


    @Before
    public void init() throws NoSuchFieldException {
        when(configHandler.getSupportedLanguages()).thenReturn("en");
        when(configHandler.getSupportedClients()).thenReturn("radien");
        when(configHandler.getDefaultClient()).thenReturn("radien");
        when(configHandler.getDefaultLanguage()).thenReturn("en");
    }

    @Test
    public void testInitNoRun() {
        when(configHandler.getSeedContent()).thenReturn("false");
        ecmSeeder.init();
        verify(contentRepository, never()).registerCNDNodeTypes(anyString());
    }

    @Test
    public void testInitNewInitNoContent() throws RepositoryException {
        when(configHandler.getSeedContent()).thenReturn("true");
        when(configHandler.getSeedInsertOnly()).thenReturn("true");

        //Root Node
        when(configHandler.getRootNode(anyString())).thenReturn("rootNode");
        when(contentServiceAccess.getByViewIdLanguage("rootNode", false, ""))
                .thenReturn(new ArrayList<>());
        when(contentRepository.getRootNodePath())
                .thenReturn("/");
        doAnswer(invocation -> {
            Object arg1 = invocation.getArgument(1);

            ((EnterpriseContent) arg1).setJcrPath("/rootNode");
            return null;
        }).when(contentServiceAccess).save(argThat((String arg) -> arg.equals("radien")),
                argThat((EnterpriseContent arg) -> arg.getName().equals("rootNode")));

        //PROPERTIES Node
        when(configHandler.getPropertiesNode()).thenReturn("propertiesNode");
        when(contentServiceAccess.getByViewIdLanguage("propertiesNode", false, null))
                .thenReturn(new ArrayList<>());

        //HTMLContent Node
        when(configHandler.getHtmlNode(anyString())).thenReturn("htmlNode");
        when(contentServiceAccess.getByViewIdLanguage("htmlNode", false, null))
                .thenReturn(new ArrayList<>());

        //Documents Node
        when(configHandler.getDocumentsNode(anyString())).thenReturn("docsNode");
        when(contentServiceAccess.getByViewIdLanguage("docsNode", false, null))
                .thenReturn(new ArrayList<>());
        doAnswer(invocation -> {
            Object arg0 = invocation.getArgument(1);

            ((EnterpriseContent) arg0).setJcrPath("/rootNode/docsNode");
            return null;
        }).when(contentServiceAccess).save(argThat((String arg) -> arg.equals("radien"))
                ,argThat((EnterpriseContent arg) -> arg.getName().equals("docsNode")));
        //// Auto Create Folders
        when(configHandler.getAutoCreateNodes()).thenReturn("documents,reports");
        when(contentServiceAccess.getChildrenFiles("docsNode"))
                .thenReturn(new ArrayList<>());

        //Skip InitContent
        when(contentDataProvider.getContents()).thenReturn(new ArrayList<>());

        //Init
        ecmSeeder.init();
        verify(contentServiceAccess).save(argThat((String arg) -> arg.equals("radien")),
                argThat((EnterpriseContent arg) -> arg.getName().equals("rootNode")));
        verify(contentServiceAccess).save(argThat((String arg) -> arg.equals("radien")),
                argThat((EnterpriseContent arg) -> arg.getName().equals("propertiesNode")));
        verify(contentServiceAccess).save(argThat((String arg) -> arg.equals("radien")),
                argThat((EnterpriseContent arg) -> arg.getName().equals("htmlNode")));
        verify(contentServiceAccess).save(argThat((String arg) -> arg.equals("radien")),
                argThat((EnterpriseContent arg) -> arg.getName().equals("docsNode")));
        verify(contentServiceAccess).save(argThat((String arg) -> arg.equals("radien")),
                argThat((EnterpriseContent arg) -> arg.getName().equals("documents")));
        verify(contentServiceAccess).save(argThat((String arg) -> arg.equals("radien")),
                argThat((EnterpriseContent arg) -> arg.getName().equals("reports")));
    }

    @Test
    public void testInitSecondInitNoContent() throws RepositoryException {
        when(configHandler.getSeedContent()).thenReturn("true");
        when(configHandler.getSeedInsertOnly()).thenReturn("true");

        //Root Node
        when(configHandler.getRootNode(anyString())).thenReturn("rootNode");
        Folder rootNode = new Folder("rootNode");
        rootNode.setParentPath("/");
        rootNode.setViewId("rootNode");
        when(contentServiceAccess.getByViewIdLanguage("rootNode", false, null))
                .thenReturn(Collections.singletonList(rootNode));

        //Properties Node
        when(configHandler.getPropertiesNode()).thenReturn("propertiesNode");
        Folder propertiesNode = new Folder("propertiesNode");
        propertiesNode.setParentPath("/");
        propertiesNode.setViewId("propertiesNode");
        when(contentServiceAccess.getByViewIdLanguage("propertiesNode", false, ""))
                .thenReturn(Collections.singletonList(propertiesNode));

        //HTMLContent Node
        when(configHandler.getHtmlNode(anyString())).thenReturn("htmlNode");
        Folder htmlNode = new Folder("htmlNode");
        htmlNode.setParentPath("/");
        htmlNode.setViewId("htmlNode");
        when(contentServiceAccess.getByViewIdLanguage("htmlNode", false, ""))
                .thenReturn(Collections.singletonList(htmlNode));

        //Documents Node
        when(configHandler.getDocumentsNode(anyString())).thenReturn("docsNode");
        Folder docsNode = new Folder("docsNode");
        docsNode.setParentPath("/");
        docsNode.setViewId("docsNode");
        when(contentServiceAccess.getFirstByViewIdLanguage("docsNode", false, null))
                .thenReturn(docsNode);
        //// Auto Create Folders
        when(configHandler.getAutoCreateNodes()).thenReturn("documents,reports");
        Folder reportsFolder = new Folder("reports");
        when(contentServiceAccess.getChildrenFiles("docsNode"))
                .thenReturn(Collections.singletonList(reportsFolder));

        //Skip InitContent
        when(contentDataProvider.getContents()).thenReturn(new ArrayList<>());

        //Init
        ecmSeeder.init();
        verify(contentServiceAccess, never()).save(argThat((String arg) -> arg.equals("radien")),
                argThat((EnterpriseContent arg) -> arg.getName().equals("rootNode")));
        verify(contentServiceAccess, never()).save(argThat((String arg) -> arg.equals("radien")),
                argThat((EnterpriseContent arg) -> arg.getName().equals("propertiesNode")));
        verify(contentServiceAccess, never()).save(argThat((String arg) -> arg.equals("radien")),
                argThat((EnterpriseContent arg) -> arg.getName().equals("htmlNode")));
        verify(contentRepository).updateFolderSupportedLanguages("radien", "htmlNode");
        verify(contentServiceAccess, never()).save(argThat((String arg) -> arg.equals("radien")),
                argThat((EnterpriseContent arg) -> arg.getName().equals("docsNode")));
        verify(contentRepository).updateFolderSupportedLanguages("radien", "docsNode");
        verify(contentServiceAccess).save(argThat((String arg) -> arg.equals("radien")),
                argThat((EnterpriseContent arg) -> arg.getName().equals("documents")));
    }



    @Test
    public void testInitContent() throws NameNotValidException, RepositoryException {
        when(configHandler.getSeedContent()).thenReturn("true");
        when(configHandler.getSeedInsertOnly()).thenReturn("true");

        //Root Node
        when(configHandler.getRootNode(anyString())).thenReturn("rootNode");
        when(contentServiceAccess.getByViewIdLanguage("rootNode", false, ""))
                .thenReturn(new ArrayList<>());
        when(contentRepository.getRootNodePath())
                .thenReturn("/");
        doAnswer(invocation -> {
            Object arg0 = invocation.getArgument(1);

            ((EnterpriseContent) arg0).setJcrPath("/rootNode");
            return null;
        }).when(contentServiceAccess).save(argThat((String arg) -> arg.equals("radien")),
                argThat((EnterpriseContent arg) -> arg.getName().equals("rootNode")));

        //Properties Node
        when(configHandler.getPropertiesNode()).thenReturn("propertiesNode");
        when(contentServiceAccess.getByViewIdLanguage("propertiesNode", false, null))
                .thenReturn(new ArrayList<>());

        //HTMLContent Node
        when(configHandler.getHtmlNode(anyString())).thenReturn("htmlNode");
        when(contentServiceAccess.getByViewIdLanguage("htmlNode", false, null))
                .thenReturn(new ArrayList<>());

        //Documents Node
        when(configHandler.getDocumentsNode(anyString())).thenReturn("docsNode");
        when(contentServiceAccess.getByViewIdLanguage("docsNode", false, null))
                .thenReturn(new ArrayList<>());
        doAnswer(invocation -> {
            Object arg0 = invocation.getArgument(1);

            ((EnterpriseContent) arg0).setJcrPath("/rootNode/docsNode");
            return null;
        }).when(contentServiceAccess).save(argThat((String arg) -> arg.equals("radien")),
                argThat((EnterpriseContent arg) -> arg.getName().equals("docsNode")));
        //// Auto Create Folders
        when(configHandler.getAutoCreateNodes()).thenReturn("documents,reports");
        when(contentServiceAccess.getChildrenFiles("docsNode"))
                .thenReturn(new ArrayList<>());

        // Init Content
        //// Generic Content
        EnterpriseContent generic = new GenericEnterpriseContent("genericContent");
        generic.setLanguage("en");
        generic.setViewId("genericViewId");
        when(contentDataProvider.getContents()).thenReturn(new ArrayList<>());
        when(contentServiceAccess.getByViewIdLanguage("genericViewId", false, "en"))
                .thenReturn(new ArrayList<>());
        //// Versionable Exists Same Version
        EnterpriseContent versionableUptoDate = new VersionableEnterpriseContent();
        versionableUptoDate.setName("versionableSameVersion");
        versionableUptoDate.setViewId("versionableSameVersionViewId");
        versionableUptoDate.setLanguage("en");
        versionableUptoDate.setContentType(ContentType.DOCUMENT);
        versionableUptoDate.setParentPath("/radien/path");
        ((VersionableEnterpriseContent)versionableUptoDate).setVersionable(true);
        ((VersionableEnterpriseContent)versionableUptoDate).setVersion(new ContentVersion("1.0.0"));
        when(contentServiceAccess.getByViewIdLanguage("versionableSameVersionViewId", false, "en"))
                .thenReturn(Collections.singletonList(versionableUptoDate));
        //// Versionable New
        EnterpriseContent versionableNew = new VersionableEnterpriseContent();
        versionableNew.setName("versionableNew");
        versionableNew.setViewId("versionableNew");
        versionableNew.setLanguage("en");
        versionableNew.setContentType(ContentType.DOCUMENT);
        versionableNew.setParentPath("/radien/path");
        ((VersionableEnterpriseContent)versionableNew).setVersionable(true);
        ((VersionableEnterpriseContent)versionableNew).setVersion(new ContentVersion("1.1.0"));
        when(contentServiceAccess.getByViewIdLanguage("versionableNew", false, "en"))
                .thenReturn(Collections.singletonList(versionableUptoDate));

        when(contentDataProvider.getContents())
                .thenReturn(Arrays.asList(generic, versionableUptoDate, versionableNew));
        //Init
        ecmSeeder.init();
        verify(contentServiceAccess).save(argThat((String arg) -> arg.equals("radien")),
                argThat((EnterpriseContent arg) -> arg.getName().equals("rootNode")));
        verify(contentServiceAccess).save(argThat((String arg) -> arg.equals("radien")),
                argThat((EnterpriseContent arg) -> arg.getName().equals("propertiesNode")));
        verify(contentServiceAccess).save(argThat((String arg) -> arg.equals("radien")),
                argThat((EnterpriseContent arg) -> arg.getName().equals("htmlNode")));
        verify(contentServiceAccess).save(argThat((String arg) -> arg.equals("radien")),
                argThat((EnterpriseContent arg) -> arg.getName().equals("docsNode")));
        verify(contentServiceAccess).save(argThat((String arg) -> arg.equals("radien")),
                argThat((EnterpriseContent arg) -> arg.getName().equals("documents")));
        verify(contentServiceAccess).save(argThat((String arg) -> arg.equals("radien")),
                argThat((EnterpriseContent arg) -> arg.getName().equals("reports")));

        verify(contentServiceAccess).save("radien", generic);
        verify(contentServiceAccess, never()).save("radien", versionableUptoDate);
        verify(contentServiceAccess).save("radien", versionableNew);
    }
}
