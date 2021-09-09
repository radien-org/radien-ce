package io.radien.ms.ecm.seed;

import io.radien.api.service.ecm.ContentServiceAccess;
import io.radien.api.service.ecm.exception.ContentRepositoryNotAvailableException;
import io.radien.api.service.ecm.model.ContentType;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.ecm.model.Folder;
import io.radien.ms.ecm.ContentRepository;
import io.radien.ms.ecm.client.entities.I18NProperty;
import io.radien.ms.ecm.constants.CmsConstants;
import io.radien.ms.ecm.domain.ResourceBundleLoader;
import io.radien.ms.ecm.event.ApplicationInitializedEvent;
import io.radien.ms.ecm.service.I18NPropertyService;
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
import java.util.List;
import java.util.stream.Collectors;

public @ApplicationScoped class ECMSeeder {
    private static final Logger log = LoggerFactory.getLogger(ECMSeeder.class);

    private ResourceBundleLoader dataProvider;

    @Inject
    private I18NPropertyService i18n;
    @Inject
    private ContentRepository repository;
    @Inject
    private ContentServiceAccess contentService;

    @Inject
    private Config config;

    @Inject
    @ConfigProperty(name = "system.supported.languages")
    private String supportedLanguages;
    @Inject
    @ConfigProperty(name = "system.default.language")
    private String defaultLanguage;

    @PostConstruct
    public void init() {
        log.info("Initializing CMS");
        if (Boolean.parseBoolean(config.getValue("system.jcr.seed.content", String.class))) {
            boolean insertOnly = Boolean.parseBoolean(config.getValue("system.jcr.seed.insert.only", String.class));
            dataProvider = new ResourceBundleLoader(supportedLanguages, defaultLanguage);
            initI18NProperties(insertOnly);
            initStructure();
            //initContent(insertOnly);
        }
    }

    private void initI18NProperties(boolean insertOnly) {
        List<I18NProperty> allProperties = dataProvider.getAllProperties();
        List<String> currentProperties = i18n.getAll().stream().map(I18NProperty::getKey).collect(Collectors.toList());
        for (I18NProperty property : allProperties) {
            if (insertOnly) {
                if (!currentProperties.contains(property.getKey())) {
                    saveProperty(property);
                } else {
                    log.info("Property skipped {}; Already existing.", property.getKey());
                }
            } else {
                saveProperty(property);
            }
        }
    }

    private void saveProperty(I18NProperty property) {
        try {
            i18n.save(property);
        } catch (Exception e) {
            log.warn("error saving property: {}", property.getKey(), e);
        }
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
/*
            initHTMLContentNode(rootNode, oafHTMLContent);

            initAppInfoNode(rootNode, oafAppInfoContent);

            initStaticContentNode(rootNode, oafStaticContentContent);

            initNotificationsNode(rootNode, oafNotificationsContent);

            initImagesNode(rootNode, oafImagesContent);

            initIFrameContentNode(rootNode, oafIFrameContent);

            initTagsNode(rootNode);

            initDocumentsNode(rootNode);
 */
        } catch (Exception e) {
            log.error("Error processing new content", e);
        }
    }

    @NotNull
    private EnterpriseContent initRootNode() throws ContentRepositoryNotAvailableException {
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

    @Asynchronous
    public void init(@Observes ApplicationInitializedEvent event) {
        log.info(event.getClass().getSimpleName() + " - received");
    }

}
