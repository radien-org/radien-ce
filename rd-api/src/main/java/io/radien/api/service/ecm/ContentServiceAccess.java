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
package io.radien.api.service.ecm;

import io.radien.api.service.ecm.exception.ContentNotAvailableException;
import io.radien.api.service.ecm.exception.ContentRepositoryNotAvailableException;
import io.radien.api.service.ecm.model.SystemContentVersion;
import java.util.List;


import io.radien.api.service.ServiceAccess;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.mail.model.MailType;

/**
 * Interface that defines the methods for the CMS
 *
 * @author Marco Weiland
 */
public interface ContentServiceAccess extends ServiceAccess {

	/**
	 * Content Service Access retrieves by a given mail type the notification id by the received type
	 * @param assignContextAdmin to be validated and retrieved the notification
	 * @return a string value of the notification
	 */
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

	/**
	 * Retrieves an Enterprise Content by searching for his activity value and language that belongs into a certain
	 * view id
	 * @param viewId to be search
	 * @param activeOnly to be filter in the certain ecm activity
	 * @param language of the ecm to be retrieved
	 * @return a existent enterprise content
	 */
	default EnterpriseContent getFirstByViewIdLanguage(String viewId, boolean activeOnly, String language) {
		List<? extends EnterpriseContent> contentList = getByViewIdLanguage(viewId, activeOnly, language);
		return contentList != null && !contentList.isEmpty() ? contentList.get(0) : null;
	}

	/**
	 * Validates if a certain enterprise content exists
	 * @param content to be searched
	 * @return true in case such ecm does exist
	 */
	default boolean exists(EnterpriseContent content) {
		EnterpriseContent foundContent = getFirstByViewIdLanguage(content.getViewId(), false, content.getLanguage());
		return foundContent != null && content.getLanguage().equalsIgnoreCase(foundContent.getLanguage());
	}

	/**
	 * Persists the target Content
	 *
	 * @param obj the {@link EnterpriseContent} to be persisted
	 */
	void save(String client, EnterpriseContent obj);

	/**
	 * Deletes the target enterprise content
	 *
	 * @param obj the {@link EnterpriseContent} to be deleted
	 */
	void delete(EnterpriseContent obj);

	/**
	 * Gets a list of all the children files existent for a given view id
	 * @param viewId to be searched
	 * @return a list of enterprise contents
	 */
	List<EnterpriseContent> getChildrenFiles(String viewId);

	/**
	 * Tries to load the file present inside a content, if available
	 * @param jcrPath  the {@link EnterpriseContent} from which the file will load
	 * @return the new child {@link EnterpriseContent}
	 */
	EnterpriseContent loadFile(String jcrPath);

	/**
	 * Content service folder contents getter
	 * @param path to be retrieved
	 * @throws ContentNotAvailableException given path was not found
	 * @throws ContentRepositoryNotAvailableException in case JCR Repository is not available or login was not possible
	 * @return a list of all the contents existent in a given path
	 */
	List<EnterpriseContent> getFolderContents(String path);

	/**
	 * Content service content versions getter
	 * @param path to be retrieved
	 * @return a list of all the contents versions existent in a given path
	 */
	List<EnterpriseContent> getContentVersions(String path);

	void deleteVersion(String path, SystemContentVersion version);

	String getOrCreateDocumentsPath(String client, String path);

	void delete(String path);
}
