package io.radien.ms.ecm.seed;

import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.api.service.ecm.ContentServiceAccess;
import io.radien.api.service.ecm.exception.ContentNotAvailableException;
import io.radien.api.service.ecm.exception.ContentRepositoryNotAvailableException;
import io.radien.api.service.ecm.model.*;
import io.radien.api.service.i18n.I18NServiceAccess;
import io.radien.ms.ecm.ContentRepository;
import io.radien.ms.ecm.constants.CmsConstants;
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
    private ContentRepository repository;
    @Inject
    private ContentServiceAccess contentService;
    @Inject
    private ContentDataProvider contentDataProvider;
    @Inject
    private I18NServiceAccess i18NServiceAccess;
    @Inject
    private Config config;

    @Inject
    @ConfigProperty(name = "system.supported.languages")
    private String supportedLanguages;
    @Inject
    @ConfigProperty(name = "system.default.language")
    private String defaultLanguage;

    private TranslationDataProvider translationDataProvider;

    @PostConstruct
    public void init() {
        log.info("Initializing CMS");
        if (Boolean.parseBoolean(config.getValue("system.jcr.seed.content", String.class))) {
            boolean insertOnly = Boolean.parseBoolean(config.getValue("system.jcr.seed.insert.only", String.class));
            initStructure();
            initContent(insertOnly);
            initI18NProperties();
        }
    }

    private void initI18NProperties() {
        translationDataProvider = new TranslationDataProvider(supportedLanguages, defaultLanguage);
        List<SystemI18NProperty> allProperties = translationDataProvider.getAllProperties();
        allProperties.forEach(i18NServiceAccess::save);
    }

    private void initStructure() {
        try {
            repository.registerCNDNodeTypes(CmsConstants.PropertyKeys.OAF_NODE_TYPES);

            EnterpriseContent rootNode;
            EnterpriseContent oafHTMLContent = null;
            EnterpriseContent oafNewsContent = null;
            EnterpriseContent oafAppInfoContent = null;
            EnterpriseContent oafStaticContentContent = null;
            EnterpriseContent oafImagesContent = null;
            EnterpriseContent oafNotificationsContent = null;
            EnterpriseContent oafIFrameContent = null;

            rootNode = initRootNode();
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
                config.getValue(CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_ROOT, String.class),
                false, null);

        if (byViewIdLanguage != null && !byViewIdLanguage.isEmpty()) {
            rootNode = byViewIdLanguage.get(0);
        }
        if (rootNode == null || rootNode.getContentType().equals(ContentType.ERROR)) {

            log.info("[CMS] : ENABLED : Content Repository Root Node initialization");
            rootNode = new Folder(config.getValue(CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_ROOT, String.class));
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
        EnterpriseContent documentsNode = contentService
                .getFirstByViewIdLanguage(config.getValue(CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_DOCS, String.class), false, null);
        if (documentsNode == null || documentsNode.getContentType().equals(ContentType.ERROR)) {
            log.info("[CMS] : ENABLED : Content Repository Documents Node initialization");

            documentsNode = new Folder(config.getValue(CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_DOCS, String.class));
            documentsNode.setParentPath(rootNode.getJcrPath());
            documentsNode.setViewId(documentsNode.getName());

            contentService.save(documentsNode);

            log.info("[CMS] : DOCUMENTS NODE : INITIALIZED {}", documentsNode);
        } else {
            log.info("[CMS] : ENABLED : Content Repository Documents Node already initialized; Checking for updated locales.");
            repository.updateFolderSupportedLanguages(documentsNode.getParentPath(), config.getValue(CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_DOCS, String.class));
        }
        autoCreateDocumentFolders(documentsNode);
    }

    private void autoCreateDocumentFolders(EnterpriseContent documentsNode) throws ContentRepositoryNotAvailableException, ContentNotAvailableException {
        String[] folderNames = config.getValue(CmsConstants.PropertyKeys.SYSTEM_DMS_CFG_AUTO_CREATE_FOLDERS, String.class)
                .split(",");
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

    private void initHTMLContentNode(EnterpriseContent rootNode, EnterpriseContent oafHTMLContent) throws RepositoryException, ContentRepositoryNotAvailableException, ContentNotAvailableException {
        List<EnterpriseContent> tmpContent = contentService
                .getByViewIdLanguage(config.getValue(CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_HTML, String.class), false, "");

        if (tmpContent != null && !tmpContent.isEmpty()) {
            oafHTMLContent = tmpContent.get(0);
        }

        if (oafHTMLContent == null || oafHTMLContent.getContentType().equals(ContentType.ERROR)) {
            log.info("[CMS] : ENABLED : Content Repository HTML Node initialization");

            oafHTMLContent = new Folder(config.getValue(CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_HTML, String.class));
            oafHTMLContent.setParentPath(rootNode.getJcrPath());
            oafHTMLContent.setViewId(oafHTMLContent.getName());

            contentService.save(oafHTMLContent);
            log.info("[CMS] : HTML NODE : INITIALIZED {}", oafHTMLContent);
        } else {
            log.info("[CMS] : ENABLED : Content Repository HTML Node already initialized; Checking for updated locales.");
            repository.updateFolderSupportedLanguages(oafHTMLContent.getParentPath(), config.getValue(CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_HTML, String.class));
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
