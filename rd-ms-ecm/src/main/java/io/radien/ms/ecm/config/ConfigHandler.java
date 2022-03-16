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
import java.text.MessageFormat;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateful
public class ConfigHandler {
    protected static final Logger log = LoggerFactory.getLogger(ConfigHandler.class);

    @Inject
    private OAFAccess oafAccess;

    //CmsConfig
    private String seedContent;
    private String seedInsertOnly;
    private String supportedClients;
    private String defaultClient;
    //JcrConfig
    private String repoHome;
    private String repoSource;
    private String mongoDbName;
    private String mongoDbUri;
    private String autoCreateNodes;
    private String autoCreateLegalDocNodes;
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
        this.supportedClients = oafAccess.getProperty(CmsProperties.SYSTEM_CMS_SUPPORTED_CLIENTS);
        this.defaultClient = oafAccess.getProperty(CmsProperties.SYSTEM_CMS_DEFAULT_CLIENT);
        this.repoHome = oafAccess.getProperty(CmsProperties.SYSTEM_CMS_REPO_HOME_DIR);
        this.repoSource = oafAccess.getProperty(CmsProperties.SYSTEM_CMS_REPO_SOURCE);
        this.mongoDbName = oafAccess.getProperty(CmsProperties.SYSTEM_CMS_REPO_MONGO_DB_NAME);
        this.mongoDbUri = oafAccess.getProperty(CmsProperties.SYSTEM_CMS_REPO_MONGO_DB_URI);
        this.autoCreateNodes = oafAccess.getProperty(CmsProperties.SYSTEM_CMS_CFG_AUTO_CREATE_FOLDERS);
        this.autoCreateLegalDocNodes = oafAccess.getProperty(CmsProperties.SYSTEM_CMS_CFG_AUTO_CREATE_LEGAL_DOCUMETNS_FOLDERS);
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

    public String getSupportedClients() {
        return supportedClients;
    }

    public String getDefaultClient() {
        return defaultClient;
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

    public String getAutoCreateLegalDocNodes() {
        return autoCreateLegalDocNodes;
    }

    public String getRootNode(String client) {
        return MessageFormat.format(rootNode, client);
    }

    public String getRootNode() {
        return MessageFormat.format(rootNode, defaultClient);
    }

    public String getHtmlNode(String client) {
        return MessageFormat.format(htmlNode, client);
    }

    public String getNotificationNode(String client) {
        return MessageFormat.format(notificationNode, client);
    }

    public String getDocumentsNode(String client) {
        return MessageFormat.format(documentsNode, client);
    }

    public String getPropertiesNode(String client) {
        return MessageFormat.format(propertiesNode, client);
    }

    public String getPropertiesNode() {
        return MessageFormat.format(propertiesNode, defaultClient);
    }

    public String getSupportedLanguages() {
        return supportedLanguages;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public String getProperty(SystemProperties property, Optional<String> client) {
        String value = oafAccess.getProperty(property);
        if(value.contains("{0}")) {
            if(!client.isPresent()) {
                log.warn("Client value not provided, using default value {}", defaultClient);
            }
            value = MessageFormat.format(value, client.orElse(defaultClient));
        }
        return value;
    }
}
