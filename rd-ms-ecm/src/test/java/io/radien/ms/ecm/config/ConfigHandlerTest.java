/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.radien.ms.ecm.config;

import io.radien.api.OAFAccess;
import io.radien.ms.ecm.constants.CmsProperties;
import java.util.Optional;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ConfigHandlerTest {
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @InjectMocks
    private ConfigHandler configHandler;
    @Mock
    private OAFAccess oaf;

    @Before
    public void init() {
        when(oaf.getProperty(CmsProperties.SYSTEM_MS_SEED_CONTENT))
                .thenReturn("true");
        when(oaf.getProperty(CmsProperties.SYSTEM_MS_SEED_CONTENT_INSERT_ONLY))
                .thenReturn("true");
        when(oaf.getProperty(CmsProperties.SYSTEM_CMS_SUPPORTED_CLIENTS))
                .thenReturn("radien");
        when(oaf.getProperty(CmsProperties.SYSTEM_CMS_DEFAULT_CLIENT))
                .thenReturn("radien");
        when(oaf.getProperty(CmsProperties.SYSTEM_CMS_REPO_HOME_DIR))
                .thenReturn("homeDir");
        when(oaf.getProperty(CmsProperties.SYSTEM_CMS_REPO_SOURCE))
                .thenReturn("repoSource");
        when(oaf.getProperty(CmsProperties.SYSTEM_CMS_REPO_MONGO_DB_NAME))
                .thenReturn("dbName");
        when(oaf.getProperty(CmsProperties.SYSTEM_CMS_REPO_MONGO_DB_URI))
                .thenReturn("dbUri");
        when(oaf.getProperty(CmsProperties.SYSTEM_CMS_CFG_AUTO_CREATE_FOLDERS))
                .thenReturn("legalDocuments");
        when(oaf.getProperty(CmsProperties.SYSTEM_CMS_CFG_NODE_ROOT))
                .thenReturn("{0}");
        when(oaf.getProperty(CmsProperties.SYSTEM_CMS_CFG_NODE_HTML))
                .thenReturn("{0}_html");
        when(oaf.getProperty(CmsProperties.SYSTEM_CMS_CFG_NODE_NOTIFICATION))
                .thenReturn("{0}_notification");
        when(oaf.getProperty(CmsProperties.SYSTEM_CMS_CFG_NODE_DOCS))
                .thenReturn("{0}_documents");
        when(oaf.getProperty(CmsProperties.SYSTEM_CMS_CFG_NODE_PROPERTIES))
                .thenReturn("{0}_properties");
        when(oaf.getProperty(CmsProperties.SYSTEM_MS_CONFIG_SUPPORTED_LANG_ECM))
                .thenReturn("en");
        when(oaf.getProperty(CmsProperties.SYSTEM_MS_CONFIG_DEFAULT_LANG_ECM))
                .thenReturn("en");
        configHandler.init();
    }

    @Test
    public void testGetSeedContent() {
        assertEquals("true", configHandler.getSeedContent());
    }
    @Test
    public void testGetSeedInsertOnly() {
        assertEquals("true", configHandler.getSeedInsertOnly());
    }
    @Test
    public void testGetSupportedClients() {
        assertEquals("radien", configHandler.getSupportedClients());
    }
    @Test
    public void testGetDefaultClient() {
        assertEquals("radien", configHandler.getDefaultClient());
    }
    @Test
    public void testGetRepoHome() {
        assertEquals("homeDir", configHandler.getRepoHome());
    }
    @Test
    public void testGetRepoSource() {
        assertEquals("repoSource", configHandler.getRepoSource());
    }
    @Test
    public void testGetMongoDbName() {
        assertEquals("dbName", configHandler.getMongoDbName());
    }
    @Test
    public void testGetMongoDbUri() {
        assertEquals("dbUri", configHandler.getMongoDbUri());
    }
    @Test
    public void testGetAutoCreateNodes() {
        assertEquals("legalDocuments", configHandler.getAutoCreateNodes());
    }
    @Test
    public void testGetRootNodeClient() {
        assertEquals("radien", configHandler.getRootNode("radien"));
    }
    @Test
    public void testGetRootNode() {
        assertEquals("radien", configHandler.getRootNode());
    }
    @Test
    public void testGetHtmlNode() {
        assertEquals("radien_html", configHandler.getHtmlNode("radien"));
    }
    @Test
    public void testGetNotificationNode() {
        assertEquals("radien_notification", configHandler.getNotificationNode("radien"));
    }
    @Test
    public void testGetDocumentsNode() {
        assertEquals("radien_documents", configHandler.getDocumentsNode("radien"));
    }
    @Test
    public void testGetPropertiesNodeClient() {
        assertEquals("radien_properties", configHandler.getPropertiesNode("radien"));
    }
    @Test
    public void testGetPropertiesNode() {
        assertEquals("radien_properties", configHandler.getPropertiesNode());
    }
    @Test
    public void testGetSupportedLanguages() {
        assertEquals("en", configHandler.getSupportedLanguages());
    }
    @Test
    public void testGetDefaultLanguage() {
        assertEquals("en", configHandler.getDefaultLanguage());
    }
    @Test
    public void testGetPropertyClientDefaultClient() {
        when(oaf.getProperty(CmsProperties.SYSTEM_CMS_CFG_NODE_DOCS))
                .thenReturn("{0}_documents");
        assertEquals("radien_documents", configHandler.getProperty(CmsProperties.SYSTEM_CMS_CFG_NODE_DOCS, Optional.empty()));
    }
    @Test
    public void testGetPropertyClientClient() {
        when(oaf.getProperty(CmsProperties.SYSTEM_CMS_CFG_NODE_DOCS))
                .thenReturn("{0}_documents");
        assertEquals("client_documents", configHandler.getProperty(CmsProperties.SYSTEM_CMS_CFG_NODE_DOCS, Optional.of("client")));
    }
}
