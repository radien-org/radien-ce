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
import io.radien.api.SystemProperties;
import io.radien.ms.ecm.constants.CmsProperties;
import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.inject.Inject;

@Stateful
public class ConfigHandler {
    @Inject
    private OAFAccess oafAccess;

    //CmsConfig
    private String seedContent;
    private String seedInsertOnly;
    //JcrConfig
    private String repoHome;
    private String repoSource;
    private String mongoDbName;
    private String mongoDbUri;
    private String autoCreateNodes;
    //JcrNodes
    private String rootNode;
    private String htmlNode;
    private String notificationNode;
    private String documentsNode;
    private String propertiesNode;
    //TranslationConfig
    private String supportedLanguages;
    private String defaultLanguage;


    @PostConstruct
    public void init() {
        this.seedContent = oafAccess.getProperty(CmsProperties.SYSTEM_MS_SEED_CONTENT);
        this.seedInsertOnly = oafAccess.getProperty(CmsProperties.SYSTEM_MS_SEED_CONTENT_INSERT_ONLY);
        this.repoHome = oafAccess.getProperty(CmsProperties.SYSTEM_CMS_REPO_HOME_DIR);
        this.repoSource = oafAccess.getProperty(CmsProperties.SYSTEM_CMS_REPO_SOURCE);
        this.mongoDbName = oafAccess.getProperty(CmsProperties.SYSTEM_CMS_REPO_MONGO_DB_NAME);
        this.mongoDbUri = oafAccess.getProperty(CmsProperties.SYSTEM_CMS_REPO_MONGO_DB_URI);
        this.autoCreateNodes = oafAccess.getProperty(CmsProperties.SYSTEM_CMS_CFG_AUTO_CREATE_FOLDERS);
        this.rootNode = oafAccess.getProperty(CmsProperties.SYSTEM_CMS_CFG_NODE_ROOT);
        this.htmlNode = oafAccess.getProperty(CmsProperties.SYSTEM_CMS_CFG_NODE_HTML);
        this.notificationNode = oafAccess.getProperty(CmsProperties.SYSTEM_CMS_CFG_NODE_NOTIFICATION);
        this.documentsNode = oafAccess.getProperty(CmsProperties.SYSTEM_CMS_CFG_NODE_DOCS);
        this.propertiesNode = oafAccess.getProperty(CmsProperties.SYSTEM_CMS_CFG_NODE_PROPERTIES);
        this.supportedLanguages = oafAccess.getProperty(CmsProperties.SYSTEM_MS_CONFIG_SUPPORTED_LANG_ECM);
        this.defaultLanguage = oafAccess.getProperty(CmsProperties.SYSTEM_MS_CONFIG_DEFAULT_LANG_ECM);
    }

    public String getSeedContent() {
        return seedContent;
    }

    public String getSeedInsertOnly() {
        return seedInsertOnly;
    }

    public String getRepoHome() {
        return repoHome;
    }

    public String getRepoSource() {
        return repoSource;
    }

    public String getMongoDbName() {
        return mongoDbName;
    }

    public String getMongoDbUri() {
        return mongoDbUri;
    }

    public String getAutoCreateNodes() {
        return autoCreateNodes;
    }

    public String getRootNode() {
        return rootNode;
    }

    public String getHtmlNode() {
        return htmlNode;
    }

    public String getNotificationNode() {
        return notificationNode;
    }

    public String getDocumentsNode() {
        return documentsNode;
    }

    public String getPropertiesNode() {
        return propertiesNode;
    }

    public String getSupportedLanguages() {
        return supportedLanguages;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public String getProperty(SystemProperties property) {
        return oafAccess.getProperty(property);
    }
}
