/*
 *
 *  * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
 *  * <p>
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  * <p>
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  * <p>
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.radien.ms.ecm.seed;

import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.api.service.ecm.ContentServiceAccess;
import io.radien.api.service.ecm.exception.ContentNotAvailableException;
import io.radien.api.service.ecm.exception.ContentRepositoryNotAvailableException;
import io.radien.api.service.ecm.model.*;
import io.radien.api.service.i18n.I18NServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.ecm.ContentRepository;
import io.radien.ms.ecm.config.ConfigHandler;
import io.radien.ms.ecm.constants.CmsConstants;
import io.radien.ms.ecm.constants.CmsProperties;
import io.radien.ms.ecm.domain.ContentDataProvider;
import io.radien.ms.ecm.domain.TranslationDataProvider;
import io.radien.ms.ecm.event.ApplicationInitializedEvent;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.jcr.RepositoryException;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public @ApplicationScoped class ECMSeeder {
    private static final Logger log = LoggerFactory.getLogger(ECMSeeder.class);

    @Inject
    private ConfigHandler configHandler;
    @Inject
    private ContentRepository repository;
    @Inject
    private ContentServiceAccess contentService;
    @Inject
    private ContentDataProvider contentDataProvider;
    @Inject
    private I18NServiceAccess i18NServiceAccess;

    private String supportedLanguages;
    private String defaultLanguage;

    @PostConstruct
    public void init() {
        log.info("Initializing CMS");
        if (Boolean.parseBoolean(configHandler.getSeedContent())) {
            supportedLanguages = configHandler.getSupportedLanguages();
            defaultLanguage = configHandler.getDefaultLanguage();
            boolean insertOnly = Boolean.parseBoolean(configHandler.getSeedInsertOnly());
            initStructure();
            initContent(insertOnly);
            initI18NProperties();
        }
    }

    private void initI18NProperties() {
        TranslationDataProvider translationDataProvider = new TranslationDataProvider(supportedLanguages, defaultLanguage);
        List<SystemI18NProperty> allProperties = translationDataProvider.getAllProperties();
        for (SystemI18NProperty allProperty : allProperties) {
            try {
                i18NServiceAccess.save(allProperty);
            } catch (SystemException e) {
                log.warn("Error saving {} for {}", allProperty.getKey(), allProperty.getApplication(), e);
            }
        }
    }

    private void initStructure() {
        try {
            repository.registerCNDNodeTypes(CmsProperties.OAF_NODE_TYPES.propKey());

            EnterpriseContent rootNode;
            EnterpriseContent oafPropertiesContent = null;
            EnterpriseContent oafHTMLContent = null;
            EnterpriseContent oafNewsContent = null;
            EnterpriseContent oafAppInfoContent = null;
            EnterpriseContent oafStaticContentContent = null;
            EnterpriseContent oafImagesContent = null;
            EnterpriseContent oafNotificationsContent = null;
            EnterpriseContent oafIFrameContent = null;

            rootNode = initRootNode();
            initPropertiesContent(rootNode, oafPropertiesContent);
            initHTMLContentNode(rootNode, oafHTMLContent);
/*
            initAppInfoNode(rootNode, oafAppInfoContent);

            initStaticContentNode(rootNode, oafStaticContentContent);

            initNotificationsNode(rootNode, oafNotificationsContent);

            initImagesNode(rootNode, oafImagesContent);

            initIFrameContentNode(rootNode, oafIFrameContent);

            initTagsNode(rootNode);
 */
            initDocumentsNode(rootNode);

        } catch (Exception e) {
            log.error("Error processing new content", e);
        }
    }

    @NotNull
    private EnterpriseContent initRootNode() throws ContentRepositoryNotAvailableException, ContentNotAvailableException {
        EnterpriseContent rootNode = null;
        List<EnterpriseContent> byViewIdLanguage = contentService.getByViewIdLanguage(
                configHandler.getRootNode(),
                false, null);

        if (byViewIdLanguage != null && !byViewIdLanguage.isEmpty()) {
            rootNode = byViewIdLanguage.get(0);
        }
        if (rootNode == null || rootNode.getContentType().equals(ContentType.ERROR)) {

            log.info("[CMS] : ENABLED : Content Repository Root Node initialization");
            rootNode = new Folder(configHandler.getRootNode());
            rootNode.setParentPath(repository.getRootNodePath());
            rootNode.setViewId(rootNode.getName());

            contentService.save(rootNode);

            log.info("[CMS] : ROOT NODE : INITIALIZED {}", rootNode);

        } else {
            log.info("[CMS] : ENABLED : Content Repository Root Node already initialized");

        }
        return rootNode;
    }

    private void initDocumentsNode(EnterpriseContent rootNode) throws RepositoryException, ContentRepositoryNotAvailableException, ContentNotAvailableException {
        EnterpriseContent documentsNode = contentService.getFirstByViewIdLanguage(configHandler.getDocumentsNode(), false, null);
        if (documentsNode == null || documentsNode.getContentType().equals(ContentType.ERROR)) {
            log.info("[CMS] : ENABLED : Content Repository Documents Node initialization");

            documentsNode = new Folder(configHandler.getDocumentsNode());
            documentsNode.setParentPath(rootNode.getJcrPath());
            documentsNode.setViewId(documentsNode.getName());

            contentService.save(documentsNode);

            log.info("[CMS] : DOCUMENTS NODE : INITIALIZED {}", documentsNode);
        } else {
            log.info("[CMS] : ENABLED : Content Repository Documents Node already initialized; Checking for updated locales.");
            repository.updateFolderSupportedLanguages(documentsNode.getParentPath(), configHandler.getDocumentsNode());
        }
        autoCreateDocumentFolders(documentsNode);
    }

    private void autoCreateDocumentFolders(EnterpriseContent documentsNode) throws ContentRepositoryNotAvailableException, ContentNotAvailableException {
        String[] folderNames = configHandler.getAutoCreateNodes().split(",");
        List<String> children = contentService.getChildrenFiles(documentsNode.getViewId())
                .stream().filter(ec -> ec.getContentType() == ContentType.FOLDER).map(EnterpriseContent::getName).collect(Collectors.toList());
        for (String folderName : folderNames) {
            if (!children.contains(folderName)) {
                EnterpriseContent folder = new Folder(folderName);
                folder.setParentPath(documentsNode.getJcrPath());
                folder.setViewId(folderName);
                contentService.save(folder);
                log.info("[CMS] folder created: {}", folder);
            }
        }
    }

    private void initPropertiesContent(EnterpriseContent rootNode, EnterpriseContent oafPropertiesContent) throws ContentRepositoryNotAvailableException, ContentNotAvailableException {
        List<EnterpriseContent> tmpContent = contentService.getByViewIdLanguage(configHandler.getPropertiesNode(), false, "");

        if (tmpContent != null && !tmpContent.isEmpty()) {
            oafPropertiesContent = tmpContent.get(0);
        }

        if (oafPropertiesContent == null || oafPropertiesContent.getContentType().equals(ContentType.ERROR)) {
            log.info("[CMS] : ENABLED : Content Repository Properties Node initialization");

            oafPropertiesContent = new Folder(configHandler.getPropertiesNode());
            oafPropertiesContent.setParentPath(rootNode.getJcrPath());
            oafPropertiesContent.setViewId(oafPropertiesContent.getName());

            contentService.save(oafPropertiesContent);
            log.info("[CMS] : PROPERTIES NODE : INITIALIZED {}", oafPropertiesContent);
        } else {
            log.info("[CMS] : ENABLED : Content Repository PROPERTIES Node already initialized;.");
        }
    }

    private void initHTMLContentNode(EnterpriseContent rootNode, EnterpriseContent oafHTMLContent) throws RepositoryException, ContentRepositoryNotAvailableException, ContentNotAvailableException {
        List<EnterpriseContent> tmpContent = contentService.getByViewIdLanguage(configHandler.getHtmlNode(), false, "");

        if (tmpContent != null && !tmpContent.isEmpty()) {
            oafHTMLContent = tmpContent.get(0);
        }

        if (oafHTMLContent == null || oafHTMLContent.getContentType().equals(ContentType.ERROR)) {
            log.info("[CMS] : ENABLED : Content Repository HTML Node initialization");

            oafHTMLContent = new Folder(configHandler.getHtmlNode());
            oafHTMLContent.setParentPath(rootNode.getJcrPath());
            oafHTMLContent.setViewId(oafHTMLContent.getName());

            contentService.save(oafHTMLContent);
            log.info("[CMS] : HTML NODE : INITIALIZED {}", oafHTMLContent);
        } else {
            log.info("[CMS] : ENABLED : Content Repository HTML Node already initialized; Checking for updated locales.");
            repository.updateFolderSupportedLanguages(oafHTMLContent.getParentPath(), configHandler.getHtmlNode());
        }
    }

    /**
     * Seeds all the content present in this project json files
     */
    private void initContent(boolean insertOnly) {
        for (EnterpriseContent content : contentDataProvider.getContents()) {
            String viewId = content.getViewId();
            String language = content.getLanguage();
            try {
                content.setLastEditDate(new Date());
                content.setAuthor("System");
                List<EnterpriseContent> exists = contentService.getByViewIdLanguage(content.getViewId(), false, language);
                if (exists != null && !exists.isEmpty() && content.getLanguage().equals(exists.get(exists.size() - 1).getLanguage())) {
                    EnterpriseContent rootVersion = exists.get(exists.size() - 1);
                    if (shouldUpdateContent(content, rootVersion)) {
                        contentService.save(content);
                        log.info("Updated content with viewID {}", content.getViewId());
                    }
                } else {
                    contentService.save(content);
                    log.info("Saved new content with viewID {}", content.getViewId());
                }
            } catch (Exception e) {
                log.error("Error seeding viewId: {} with language: {}",viewId,language, e);
            }
        }
    }

    private boolean shouldUpdateContent(EnterpriseContent content, EnterpriseContent rootVersion) {
        if (rootVersion == null || rootVersion.getContentType().equals(ContentType.ERROR)
                || (rootVersion.getContentType() != ContentType.TAG && !rootVersion.getParentPath().equals(content.getParentPath()))) {
            return true;
        }
        else {
            if (content instanceof SystemVersionableEnterpriseContent) {
                return ((SystemVersionableEnterpriseContent) content).getVersion().compareTo(((SystemVersionableEnterpriseContent) rootVersion).getVersion()) > 0;
            } else {
                return true;
            }
        }
    }

    @Asynchronous
    public void init(@Observes ApplicationInitializedEvent event) {
        log.info("{} - received", event.getClass().getSimpleName());
    }

}
