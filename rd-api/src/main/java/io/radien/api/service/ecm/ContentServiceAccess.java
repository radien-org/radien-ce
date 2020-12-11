/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
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
package io.radien.api.service.ecm;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.TreeNode;

import io.radien.api.service.ServiceAccess;
import io.radien.api.service.ecm.exception.ContentRepositoryNotAvailableException;
import io.radien.api.service.ecm.exception.ElementNotFoundException;
import io.radien.api.service.ecm.model.ContentType;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.mail.model.MailType;



/**
 * Interface that defines the methods for the CMS
 *
 * @author Marco Weiland
 */
public interface ContentServiceAccess extends ServiceAccess {

	String getNotificationIdByType(MailType assignContextAdmin);

	/**
	 * Attempts to return a content based on its identifier, active flag and
	 * language
	 *
	 * @param viewId     the content identifier
	 * @param activeOnly flag that indicates if the content is active or not
	 * @param language   the language identifier
	 * @return the {@link EnterpriseContent} if it finds it, or else null
	 */
	List<EnterpriseContent> getByViewIdLanguage(String viewId, boolean activeOnly, String language);

	default EnterpriseContent getFirstByViewIdLanguage(String viewId, boolean activeOnly, String language) {
		List<? extends EnterpriseContent> contentList = getByViewIdLanguage(viewId, activeOnly, language);
		return contentList != null && !contentList.isEmpty() ? contentList.get(0) : null;
	}

	default boolean exists(EnterpriseContent content) {
		EnterpriseContent foundContent = getFirstByViewIdLanguage(content.getViewId(), false, content.getLanguage());
		return foundContent != null && content.getLanguage().equalsIgnoreCase(foundContent.getLanguage());
	}

	

	/**
	 * Persistes the target Content
	 *
	 * @param obj the {@link EnterpriseContent} to be persisted
	 */
	void save(EnterpriseContent obj);

	/**
	 * Deletes the target enterprise content
	 *
	 * @param obj the {@link EnterpriseContent} to be deleted
	 */
	void delete(EnterpriseContent obj);

	List<EnterpriseContent> getByContentType(ContentType contentType, String language);

	TreeNode getDocumentTreeModel();

	List<EnterpriseContent> getChildrenFiles(String viewId);

	String getNotificationIdByTypeAndLanguage(MailType type, String languageCode);

	String getAppDesc(String app, String language);

	int countByTagName(String name);

	String getAppInfoId(String content, String app, String language);

	/**
	 * Tries to load the file present inside a content, if available
	 *
	 * @param content the {@link EnterpriseContent} from which the file will load
	 * @return the new child {@link EnterpriseContent}
	 * @throws ElementNotFoundException               Exception thrown if the
	 *                                                Element is not found
	 * @throws ContentRepositoryNotAvailableException Exception thrown if there is
	 *                                                an error while querying the
	 *                                                jcr
	 */
	EnterpriseContent loadFile(String jcrPath) throws ElementNotFoundException, ContentRepositoryNotAvailableException;

	Map<String, String> getAppDescriptions(String language);

	List<EnterpriseContent> getFolderContents(String path);

	List<EnterpriseContent> getContentVersions(String path);

	//TODO: What is this?
	String getOrCreateDocumentsPath(String path);

}
