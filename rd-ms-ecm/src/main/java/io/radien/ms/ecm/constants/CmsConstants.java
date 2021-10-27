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

import org.apache.jackrabbit.JcrConstants;

/**
 * Constants used throughout the oaf CMS
 */
public class CmsConstants {


    /**
     * Private constructor to prevent instantiation of class.
     */
    private CmsConstants() {
    }

    /**
     * The Request attribute for whether or not editing is enabled
     */
    public static final String ATTR_EDIT_ENABLED = "cmsEditEnabled";

    /**
     * The Component type for pages
     */
    public static final String COMPONENT_TYPE_PAGE = "Page";

    /**
     * Node type base.
     */

    private static final String NAMESPACE = "oaf";

    /**
     * The subpath for the metadata under sling:File resources
     */
    public static final String NN_METADATA = "metadata";

    /**
     * Static content node type.
     */
    public static final String NT_STATIC_CONTENT = NAMESPACE + ":StaticContent";

    /**
     * News feed node type
     */
    private static final String NT_NEWS_FEED = NAMESPACE + ":NewsFeed";

    /**
     * Page node type.
     */
    private static final String NT_APP_INFO = NAMESPACE + ":AppInfo";

    /**
     * File node type.
     */
    private static final String NT_FILE = NAMESPACE + ":File";

    /**
     * Notification node type.
     */
    public static final String NT_NOTIFICATION = NAMESPACE + ":Notification";

    /**
     * Video node type
     */
    private static final String NT_VIDEO = NAMESPACE + ":Iframe";


    /**
     * Constant for the last modified by user
     */
    public static final String PN_LAST_MODIFIED_BY = JcrConstants.JCR_LASTMODIFIED + "By";


    /**
     * Mixin node name
     */
    public static final String OAF_MIXIN_NODE_PROPS = "oaf:NodeType";

    /**
     * Versionable mixin
     */
    public static final String OAF_MIXIN_VERSIONABLE = "oaf:versionable";

    /**
     * Versionable create date mixin
     */
    public static final String OAF_MIXIN_VERSIONABLE_CREATE = "oaf:VersionableCreateMixin";
    /**
     * Versionable version identifier mixin
     */
    public static final String OAF_MIXIN_VERSION = "oaf:VersionMixin";
    /**
     * Mandatory content mixin
     */
    public static final String OAF_MIXIN_MANDATORY_CONTENT = "oaf:MandatoryContent";

    /**
     * the content viewId property key
     */
    public static final String OAF_VIEW_ID = "oaf:viewId";
    /**
     * the content name property
     */
    public static final String OAF_NAME = "oaf:name";
    /**
     * i18n locale property
     */
    public static final String OAF_CONTENT_LANG = "oaf:language";
    /**
     * the content type property key
     */
    public static final String OAF_CONTENT_TYPE = "oaf:contentType";
    /**
     * the content active property key
     */
    public static final String OAF_ACTIVE = "oaf:active";
    /**
     * the content htmlContent property key
     */
    public static final String OAF_HTML_CONTENT = "oaf:htmlContent";
    /**
     * the content viewId property key
     */
    public static final String OAF_VERSION_COMMENT = "oaf:versionComment";
    /**
     * the content viewId property key
     */
    public static final String OAF_MANDATORY_VIEW = "oaf:mandatoryView";
    /**
     * the content viewId property key
     */
    public static final String OAF_VERSION = "oaf:version";
    /**
     * the content viewId property key
     */
    public static final String OAF_MANDATORY_APPROVAL = "oaf:mandatoryApproval";
    /**
     * the content system property key
     */
    public static final String OAF_SYSTEM = NAMESPACE + ":system";

    public static final String OAF_CONTENT_PERMISSIONS = NAMESPACE + ":permissions" ;
    /**
     * Published flag property
     */
    public static final String OAF_STEP = NAMESPACE + ":step";
    /**
     * the content fileSize property key
     */
    public static final String OAF_FILE_SIZE = NAMESPACE + ":fileSize";
    /**
     * the content tags property key
     */
    public static final String OAF_TAGS = NAMESPACE + ":tags";
    /**
     * the content linked application property key
     */
    public static final String OAF_APP = NAMESPACE + ":app";

    /**
     * the content image property key
     */
    public static final String OAF_IMAGE = NAMESPACE + ":image";
    /**
     * the content image name property key
     */
    public static final String OAF_IMAGE_NAME = NAMESPACE + ":imageName";
    /**
     * the content image mime type property key
     */
    public static final String OAF_IMAGE_MIME_TYPE = NAMESPACE + ":imageMimeType";
    /**
     * the content last edit date property
     */
    public static final String OAF_LAST_EDIT_DATE = NAMESPACE + ":lastEditDate";
    /**
     * the content valid date property
     */
    public static final String OAF_VALID_DATE = NAMESPACE + ":validDate";
    /**
     * the content creation date property
     */
    public static final String JCR_CREATED = "jcr:created";
    /**
     * the content creation date property
     */
    public static final String OAF_CREATED = "oaf:created";
    /**
     * the content author property
     */
    public static final String OAF_CONTENT_AUTHOR = NAMESPACE + ":author";

    /**
     * The resource types which can be published
     */
    protected static final String[] PUBLISHABLE_TYPES = new String[] { CmsConstants.NT_FILE, CmsConstants.NT_NEWS_FEED, CmsConstants.NT_APP_INFO, CmsConstants.OAF_HTML_CONTENT };

    /**
     * The name of the admin user
     */
    public static final String USER_ADMIN = "admin";

    /**
     *
     */
    public static class PropertyKeys {

        public static final String OAF_NODE_TYPES = "jcr/oafnodetypes.cnd";
        public static final String SYSTEM_CMS_CFG_NODE_ROOT = "system.jcr.node.root";
        public static final String SYSTEM_CMS_CFG_NODE_HTML = "system.jcr.node.html";
        public static final String SYSTEM_CMS_CFG_NODE_NEWS_FEED = "system.jcr.node.newsfeed";
        public static final String SYSTEM_CMS_CFG_NODE_NOTIFICATION = "system.jcr.node.notifications";
        public static final String SYSTEM_CMS_CFG_NODE_DOCS = "system.jcr.node.documents";
        public static final String SYSTEM_CMS_CFG_NODE_IMAGE = "system.jcr.node.images";
        public static final String SYSTEM_CMS_CFG_NODE_IFRAME = "system.jcr.node.iframe";
        public static final String SYSTEM_DMS_CFG_AUTO_CREATE_FOLDERS = "system.jcr.document.autocreate.folder.names";
        public static final String SYSTEM_CMS_CFG_NODE_APP_INFO = "system.jcr.node.appinfo";
        public static final String SYSTEM_CMS_CFG_NODE_STATIC_CONTENT = "system.jcr.node.staticcontent";
        public static final String SYSTEM_CMS_CFG_NODE_TAG = "system.jcr.node.tag";

        private PropertyKeys() {}
    }


}
