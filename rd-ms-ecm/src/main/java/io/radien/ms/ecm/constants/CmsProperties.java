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

package io.radien.ms.ecm.constants;

import io.radien.api.SystemProperties;

public enum CmsProperties implements SystemProperties {
    //CmsConfig
    SYSTEM_MS_SEED_CONTENT("system.jcr.seed.content"),
    SYSTEM_MS_SEED_CONTENT_INSERT_ONLY("system.jcr.seed.insert.only"),
    SYSTEM_CMS_SUPPORTED_CLIENTS("system.jcr.supported.clients"),
    SYSTEM_CMS_DEFAULT_CLIENT("system.jcr.default.client"),
    //JcrConfig
    SYSTEM_CMS_REPO_HOME_DIR("system.jcr.home"),
    SYSTEM_CMS_REPO_SOURCE("system.jcr.source"),
    SYSTEM_CMS_REPO_MONGO_DB_NAME("oak.mongo.db"),
    SYSTEM_CMS_REPO_MONGO_DB_URI("oak.mongo.uri"),
    SYSTEM_CMS_CFG_AUTO_CREATE_FOLDERS("system.jcr.document.autocreate.folder.names"),
    SYSTEM_CMS_CFG_AUTO_CREATE_LEGAL_DOCUMETNS_FOLDERS("system.jcr.document.autocreate.legal_documents.folder.names"),
    OAF_NODE_TYPES("jcr/oafnodetypes.cnd"),
    //JcrNodes
    SYSTEM_CMS_CFG_NODE_ROOT("system.jcr.node.root"),
    SYSTEM_CMS_CFG_NODE_HTML("system.jcr.node.html"),
    SYSTEM_CMS_CFG_NODE_NOTIFICATION("system.jcr.node.notifications"),
    SYSTEM_CMS_CFG_NODE_DOCS("system.jcr.node.documents"),
    SYSTEM_CMS_CFG_NODE_PROPERTIES("system.jcr.node.properties"),
    SYSTEM_CMS_CFG_NODE_NEWS_FEED("system.jcr.node.newsfeed"),
    SYSTEM_CMS_CFG_NODE_IMAGE("system.jcr.node.images"),
    SYSTEM_CMS_CFG_NODE_IFRAME("system.jcr.node.iframe"),
    SYSTEM_CMS_CFG_NODE_APP_INFO("system.jcr.node.appinfo"),
    SYSTEM_CMS_CFG_NODE_STATIC_CONTENT("system.jcr.node.staticcontent"),
    SYSTEM_CMS_CFG_NODE_TAG("system.jcr.node.tag"),
    //TranslationConfig
    SYSTEM_MS_CONFIG_SUPPORTED_LANG_ECM("system.supported.languages"),
    SYSTEM_MS_CONFIG_DEFAULT_LANG_ECM("system.default.language");

    private String propKey;

    CmsProperties(String propKey) { this.propKey = propKey;}

    @Override
    public String propKey() {
        return propKey;
    }

    public static CmsProperties valueOfKey(String key) {
        for (CmsProperties cmsProperty : CmsProperties.values()) {
            if(cmsProperty.propKey().equals(key)){
                return cmsProperty;
            }
        }
        throw new IllegalArgumentException("No enum constant with value " + key);
    }
}
