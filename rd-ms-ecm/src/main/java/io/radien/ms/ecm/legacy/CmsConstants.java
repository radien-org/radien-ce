/*
 * Copyright (c) 2006-present openappframe.org & its legal owners. All rights reserved.
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
package io.radien.ms.ecm.legacy;

import org.apache.jackrabbit.JcrConstants;

/**
 * @author Marco Weiland
 */
public class CmsConstants {
    /**
     * The Component type for pages
     */
    public static final String COMPONENT_TYPE_PAGE = "Page";

    /**
     * The subpath for the metadata under sling:File resources
     */
    public static final String NN_METADATA = "metadata";
    
    /**
     * Node type base.
     */
    private static final String NAMESPACE = "rd";

    /**
     * News feed node type
     */
    private static final String NT_NEWS_FEED = NAMESPACE + ":Newsfeed";

    /**
     * Notification node type.
     */
    public static final String RD_NOTIFICATION = NAMESPACE + ":Notification";

    /**
     * Mixin node name
     */
    public static final String RD_MIXIN_NODE_PROPS = NAMESPACE + ":NodeType";

    /**
     * Versionable mixin
     */
    public static final String RD_MIXIN_VERSIONABLE = NAMESPACE + ":versionable";

    /**
     * Versionable create date mixin
     */
    public static final String RD_MIXIN_VERSIONABLE_PROPS = NAMESPACE + ":VersionableMixin";

    /**
     * the content viewId property key
     */
    public static final String RD_VIEW_ID = NAMESPACE + ":viewId";
    
    /**
     * the content name property
     */
    public static final String RD_NAME = NAMESPACE + ":name";
    
    /**
     * i18n locale property
     */
    public static final String RD_CONTENT_LANG = NAMESPACE + ":language";
    
    /**
     * the content type property key
     */
    public static final String RD_CONTENT_TYPE = NAMESPACE + ":contentType";
    
    /**
     * the content active property key
     */
    public static final String RD_ACTIVE = NAMESPACE + ":active";
    
    /**
     * the content htmlContent property key
     */
    public static final String RD_HTML_CONTENT = NAMESPACE + ":htmlContent";

    /**
     * the content viewId property key
     */
    public static final String RD_VERSION_COMMENT = NAMESPACE + ":versionComment";
    
    /**
     * the content system property key
     */
    public static final String RD_SYSTEM = NAMESPACE + ":system";

    /**
     * 
     */
    public static final String RD_CONTENT_PERMISSIONS = NAMESPACE + ":permissions" ;
    /**
     * Published flag property
     */
    public static final String RD_STEP = NAMESPACE + ":step";
    
    /**
     * the content fileSize property key
     */
    public static final String RD_FILE_SIZE = NAMESPACE + ":fileSize";
    
    /**
     * the content tags property key
     */
    public static final String RD_TAGS = NAMESPACE + ":tags";
    
    /**
     * the content linked application property key
     */
    public static final String RD_APP = NAMESPACE + ":app";

    /**
     * the content image property key
     */
    public static final String RD_IMAGE = NAMESPACE + ":image";
    
    /**
     * the content image name property key
     */
    public static final String RD_IMAGE_NAME = NAMESPACE + ":imageName";
    
    /**
     * the content image mime type property key
     */
    public static final String RD_IMAGE_MIME_TYPE = NAMESPACE + ":imageMimeType";
    
    /**
     * the content last edit date property
     */
    public static final String RD_LAST_EDIT_DATE = NAMESPACE + ":lastEditDate";
    
    /**
     * the content valid date property
     */
    public static final String RD_VALID_DATE = NAMESPACE + ":validDate";
    
    /**
     * the content creation date property
     */
    public static final String RD_CREATED = NAMESPACE + ":created";

    /**
     * the content version property
     */
    public static final String RD_VERSION = NAMESPACE + ":version";

    /**
     * the content author property
     */
    public static final String RD_CONTENT_AUTHOR = NAMESPACE + ":author";

    /**
     * The name of the admin user
     */
    public static final String USER_ADMIN = "admin";


    /**
     * Constant for the last modified by user
     */
    public static final String JCR_LASTMODIFIED_BY = JcrConstants.JCR_LASTMODIFIED + "By";
    
    /**
     * JCR Convenience Wrappers
     */
    /**
     * the content creation date property
     */
    public static final String JCR_CREATED = JcrConstants.JCR_CREATED;
    
    /**
     * the content path property
     */
    public static final String JCR_PATH = JcrConstants.JCR_PATH; 
    
    /**
     * File node type.
     */
    private static final String NT_FILE = JcrConstants.NT_FILE;

}
