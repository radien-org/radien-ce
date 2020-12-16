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
package io.radien.ms.ecm.config;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * @author Marco Weiland <m.weiland@radien.io>
 *
 */
@RequestScoped
public class ConfigProvider {

	@Inject
    @ConfigProperty(name = "system.jcr.node.root")
    private String rootNodeName;
    
    @Inject
    @ConfigProperty(name = "system.jcr.node.documents")
    private String documentsNodeName;
    @Inject
    @ConfigProperty(name = "system.jcr.node.html")
    private String htmlNodeName;
    @Inject
    @ConfigProperty(name = "system.jcr.node.newsfeed")
    private String newsFeedNodeName;
    @Inject
    @ConfigProperty(name = "system.jcr.node.images")
    private String imagesNodeName;
    @Inject
    @ConfigProperty(name = "system.jcr.node.notifications")
    private String notificationsNodeName;
    @Inject
    @ConfigProperty(name = "system.jcr.node.tag")
    private String tagNodeName;
    
    @Inject
    @ConfigProperty(name = "system.default.language")
    private String defaultLanguage;
    
    @Inject
    @ConfigProperty(name = "system.supported.languages")
    private String supportedLanguages;
    
    @Inject
    @ConfigProperty(name = "system.jcr.nodetypes.cnd.file")
    private String nodeTypesCNDFile;

	/**
	 * @return the rootNodeName
	 */
	public String getRootNodeName() {
		return rootNodeName;
	}

	/**
	 * @return the documentsNodeName
	 */
	public String getDocumentsNodeName() {
		return documentsNodeName;
	}

	/**
	 * @return the defaultLanguage
	 */
	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	/**
	 * @return the supportedLanguages
	 */
	public List<String> getSupportedLanguages() {
		return Arrays.asList(supportedLanguages.split(","));
	}

	/**
	 * @return the htmlNodeName
	 */
	public String getHtmlNodeName() {
		return htmlNodeName;
	}

	/**
	 * @return the newsFeedNodeName
	 */
	public String getNewsFeedNodeName() {
		return newsFeedNodeName;
	}

	/**
	 * @return the imagesNodeName
	 */
	public String getImagesNodeName() {
		return imagesNodeName;
	}

	/**
	 * @return the notificationsNodeName
	 */
	public String getNotificationsNodeName() {
		return notificationsNodeName;
	}

	/**
	 * @return the tagNodeName
	 */
	public String getTagNodeName() {
		return tagNodeName;
	}

	/**
	 * @return the nodeTypesCNDFile
	 */
	public String getNodeTypesCNDFile() {
		return nodeTypesCNDFile;
	}
    
    
}
