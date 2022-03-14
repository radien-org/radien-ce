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
        when(oaf.getProperty(CmsProperties.SYSTEM_MS_CONFIG_SUPPORTED_LANG_ECM))
                .thenReturn("en");
        when(oaf.getProperty(CmsProperties.SYSTEM_CMS_CFG_NODE_ROOT))
                .thenReturn("root");
        when(oaf.getProperty(CmsProperties.SYSTEM_CMS_CFG_NODE_HTML))
                .thenReturn("html");
        when(oaf.getProperty(CmsProperties.SYSTEM_CMS_CFG_NODE_NOTIFICATION))
                .thenReturn("notification");
        when(oaf.getProperty(CmsProperties.SYSTEM_CMS_CFG_NODE_PROPERTIES))
                .thenReturn("properties");
        when(oaf.getProperty(CmsProperties.SYSTEM_CMS_REPO_HOME_DIR))
                .thenReturn("home");
        when(oaf.getProperty(CmsProperties.SYSTEM_CMS_REPO_SOURCE))
                .thenReturn("source");
        when(oaf.getProperty(CmsProperties.SYSTEM_CMS_REPO_MONGO_DB_NAME))
                .thenReturn("dbName");
        when(oaf.getProperty(CmsProperties.SYSTEM_CMS_REPO_MONGO_DB_URI))
                .thenReturn("dbUri");
        configHandler.init();
    }

    @Test
    public void testGetSupportedLanguages() {
        assertEquals("en", configHandler.getSupportedLanguages());
    }

    @Test
    public void testGetRootNode() {
        assertEquals("root", configHandler.getRootNode("root"));
    }

    @Test
    public void testGetHtmlNode() {
        assertEquals("html", configHandler.getHtmlNode("radien"));
    }

    @Test
    public void testGetNotificationNode() {
        assertEquals("notification", configHandler.getNotificationNode("radien"));
    }

    @Test
    public void testGetPropertiesNode() {
        assertEquals("properties", configHandler.getPropertiesNode("radien"));
    }

    @Test
    public void testGetRepoHome() {
        assertEquals("home", configHandler.getRepoHome());
    }

    @Test
    public void testGetRepoSource() {
        assertEquals("source", configHandler.getRepoSource());
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
    public void testGetProperty() {
        when(oaf.getProperty(CmsProperties.SYSTEM_CMS_CFG_NODE_DOCS))
                .thenReturn("rd_documents");
        assertEquals("rd_documents", configHandler.getProperty(CmsProperties.SYSTEM_CMS_CFG_NODE_DOCS, Optional.empty()));
    }
}
