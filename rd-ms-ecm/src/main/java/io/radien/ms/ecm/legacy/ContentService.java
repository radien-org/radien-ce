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
package io.radien.ms.ecm.legacy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.TreeNode;

import io.radien.api.service.ecm.ContentServiceAccess;
import io.radien.api.service.ecm.exception.ContentRepositoryNotAvailableException;
import io.radien.api.service.ecm.exception.ElementNotFoundException;
import io.radien.api.service.ecm.model.ContentType;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.ecm.model.GenericEnterpriseContent;
import io.radien.api.service.mail.model.MailType;
import io.radien.ms.ecm.repository.ContentRepository;

/**
 * Default implementation of the ContentService using JackRabbit
 *
 * @author Marco Weiland
 */
public class ContentService implements ContentServiceAccess {

	private static final Logger log = LoggerFactory.getLogger(ContentService.class);
	private static final long serialVersionUID = 1L;

	
	private String defaultLanguage;

	@Inject
	private ContentRepository contentRepository;

	public List<EnterpriseContent> getChildrenFiles(String viewId) {
		List<EnterpriseContent> results = new ArrayList<>();
		try {
			results.addAll(contentRepository.getChildren(viewId));
		} catch (ContentRepositoryNotAvailableException | ElementNotFoundException e) {
			log.error("Error getting children files", e);
		}

		return results;
	}

	@Override
	public List<EnterpriseContent> getByViewIdLanguage(String viewId, boolean activeOnly, String language) {
		Optional<List<EnterpriseContent>> contentList = null;
		try {
			contentList = contentRepository.getByViewIdLanguage(viewId, activeOnly, language);

			if (contentList.isPresent() && contentList.get() != null && !contentList.get().isEmpty()) {
	        	return contentList.get();
	        } else {
				log.error("[ContentService] : Error on retrieving content with viewid: {} and language {} - we are trying to get that content with default language: {}",viewId, language, defaultLanguage);
				contentList = contentRepository.getByViewIdLanguage(viewId, activeOnly, defaultLanguage);

				if (contentList.isPresent() && contentList.get() != null && !contentList.get().isEmpty()) {
					return contentList.get();
				} else {
					log.error("[ContentService] : Error on retrieving default content with viewid: {} and language {}",viewId, defaultLanguage);

					List<EnterpriseContent> list = new ArrayList<>();
					list.add(createErrorContent(viewId, language));
					return list;
				}
			}
		} catch (ContentRepositoryNotAvailableException e) {
			log.error("CMS not available!");
		}

		List<EnterpriseContent> list = new ArrayList<>();
		list.add(createErrorContent(viewId, language));
		return list;
	}


	public EnterpriseContent getFirstByViewIdLanguage(String viewId, boolean activeOnly, String language) {
		EnterpriseContent content = null;
		try {
			List<EnterpriseContent> contentList = getByViewIdLanguage(viewId, activeOnly, language);

			content = contentList != null && !contentList.isEmpty() ? contentList.get(0) : null;

	        if ( content != null && !content.getContentType().equals(ContentType.ERROR) ) {
	        	return content;
	        }
		} catch (Exception e) {
			log.error("[ContentService] : Error on retrieving content with viewid: {} and language {} - we are trying to get that content with default language: {}",viewId, language, defaultLanguage, e);
		}

		try {
			List<EnterpriseContent> defaultContentList = getByViewIdLanguage(viewId, activeOnly, defaultLanguage);

	        content = defaultContentList != null && !defaultContentList.isEmpty() ? defaultContentList.get(0) : null;

	        return content;
		} catch (Exception e) {
			log.error("[ContentService] : Error on retrieving default content with viewid: {} and language {}",viewId, defaultLanguage, e);
		}

        return createErrorContent(viewId,language);
    }

	private EnterpriseContent createErrorContent(String viewId, String language) {
		EnterpriseContent content = new GenericEnterpriseContent();
		content.setName("<ERROR> Content not found");
    	content.setContentType(ContentType.ERROR);
    	content.setViewId(UUID.randomUUID().toString());
    	content.setHtmlContent("Oooops! Something went wrong. The elemt which should be displayed is not found, and there is not fallbackelement. The Support has been informed about that matter!");
    	content.setCreateDate(new Date());
    	log.error("[ContentService] : we could not identify content with viewid: {} and language {}, we have returned a generic error message",viewId, language);
		return content;
	}

	private EnterpriseContent createVersionableErrorContent(String path, String userLanguage) {
		EnterpriseContent content = new GenericEnterpriseContent();
		content.setName("<ERROR> Content not found");
		content.setContentType(ContentType.ERROR);
		content.setViewId(UUID.randomUUID().toString());
		content.setHtmlContent("Oooops! Something went wrong. The elemt which should be displayed is not found, and there is not fallbackelement. The Support has been informed about that matter!");
		content.setCreateDate(new Date());
		log.error("[ContentService] : we could not identify content in path: {} and language {}, we have returned a generic error message",path, userLanguage);
		return content;
	}

	public String getAppInfoId(String content, String app, String language) {

		String viewId=null;

		try {
			viewId = contentRepository.getAppInfoId(content, app, language);
		} catch (ContentRepositoryNotAvailableException e) {
			log.error("Error validating content with content {}, app {} and language {}", content, app, language);
		}

		return viewId;
	}

	@Override
	public void save(EnterpriseContent obj) {
		try {
			contentRepository.save(obj);
		} catch (ContentRepositoryNotAvailableException e) {
			log.error("problem",e);
		}
	}


	@Override
	public void delete(EnterpriseContent obj) {
		try {
			contentRepository.delete(obj);
		} catch (ContentRepositoryNotAvailableException e) {
			log.error("problem",e);
		}
	}

	public EnterpriseContent loadFile(String jcrPath)
			throws ContentRepositoryNotAvailableException {
		return contentRepository.loadFile(jcrPath);
	}

//	@Override
//	public PageImpl<EnterpriseContent> search(List<String> tags, Pageable pageable) {
//		log.info("Searching for  all documents");
//		List<EnterpriseContent> searchContent = new ArrayList<>();
//		try {
//			searchContent = contentRepository.searchContent(true, tags, pageable);
//		} catch (ContentRepositoryNotAvailableException e) {
//			log.error("Error while searching content in the JCR", e);
//		}
//
//		int start = Math.toIntExact(pageable.getOffset());
//		int end = Math.min((start + pageable.getPageSize()), searchContent.size());
//		return new PageImpl<>(searchContent.subList(start, end), pageable, searchContent.size());
//	}

	public List<EnterpriseContent> getByContentType(ContentType contentType, String language) {
		log.info("Getting types {} on {} language", contentType.key(), language);
		List<EnterpriseContent> results = new ArrayList<>();
		try {
			results.addAll(contentRepository.getByContentType(contentType,language));
		} catch (ContentRepositoryNotAvailableException e) {
			log.error("Error calling getByContentType", e);
		}

		return results;
	}

	public String getAppDesc(String app, String language) {
		log.info("Getting description");
		try {
			return contentRepository.getAppDesc(app, language);
		} catch (ContentRepositoryNotAvailableException e) {
			log.error("Error calling getByContentType", e);
		}
		return "";
	}

	public Map<String, String> getAppDescriptions(String languageCode) {
		Map<String, String> appDescriptions = new HashMap<>();
		try {
			appDescriptions = contentRepository.getAppDescriptions(languageCode);
		} catch (ContentRepositoryNotAvailableException e) {
			log.error("CMS not available!");
		}
		return appDescriptions;
	}

	@Override
	public List<EnterpriseContent> getFolderContents(String path) {
		List<EnterpriseContent> result = new ArrayList<>();
		try {
			result.addAll(contentRepository.getFolderContents(path));
		} catch (ContentRepositoryNotAvailableException e) {
			log.error("Repository not available", e);
		} catch (RepositoryException e) {
			log.error("Repository exception", e);
		}

		return result;
	}

	@Override
	public List<EnterpriseContent> getContentVersions(String path) {
		try {
			return contentRepository.getContentVersions(path);
		} catch (RepositoryException e) {
			log.error("Repository exception", e);
		} catch (ContentRepositoryNotAvailableException e) {
			log.error("Repository not available", e);
		}
		return null;
	}

	

	@Override
	public String getOrCreateDocumentsPath(String path) {
		try {
			return contentRepository.getOrCreateDocumentsPath(path);
		} catch (ContentRepositoryNotAvailableException e) {
			log.error("Repository not available", e);
		} catch (RepositoryException e) {
			log.error("Repository exception", e);
		}
		return null;
	}

	public TreeNode getDocumentTreeModel() {
		try {
			return contentRepository.getDocumentsTreeModel();
		} catch (ContentRepositoryNotAvailableException e) {
			log.error("CMS not available!");
		}
		//FIXME: fix this intermediate workaround
		return null;
	}

	public String getNotificationIdByType(MailType type) {
		switch (type) {
			case CONFIRMATION:
				return "email-1";
			case RESET_PASSWORD:
				return "email-2";
			case CHANGE_EMAIL:
				return "email-3";
			
			default:
				return "UNDEFINED";
		}
	}

	@Override
	public String getNotificationIdByTypeAndLanguage(MailType type, String languageCode) {
		return getNotificationIdByType(type) + "_" + languageCode;

	}

	@Override
	public int countByTagName(String name) {
		int count = 0;
		try {
			return contentRepository.countByTagName(name);
		} catch(ContentRepositoryNotAvailableException e) {
			log.error("Error calling countByTagName", e);
		}
		return count;
	}
}
