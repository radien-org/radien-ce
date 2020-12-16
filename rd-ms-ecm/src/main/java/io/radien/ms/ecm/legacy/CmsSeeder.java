package io.radien.ms.ecm.legacy;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.radien.api.service.ecm.exception.ContentRepositoryNotAvailableException;
import io.radien.api.service.ecm.model.ContentType;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.ecm.model.Folder;
import io.radien.ms.ecm.config.ConfigProvider;
import io.radien.ms.ecm.repository.ContentRepository;

/**
 * Seeder class that runs after the application is ready
 */
@RequestScoped
public class CmsSeeder {
    private static final Logger log = LoggerFactory.getLogger(CmsSeeder.class);

    @Inject
    private ConfigProvider cfg;
    
    
    public void init(ContentRepository repo, RepositoryNodeService nodeService) {
        log.info("Initializing Content - JCR.");
        initStructure(repo, nodeService);
//        initContent();
        log.info("Finished initialization of Content - JCR.");
    }


    private void initStructure(ContentRepository repo, RepositoryNodeService nodeService) {
        try {
            nodeService.registerCNDNodeTypes(cfg.getNodeTypesCNDFile());

            EnterpriseContent rootNode;

            rootNode = initBaseNode(repo,null, null);
            initBaseNode(repo, rootNode, cfg.getHtmlNodeName());
//            initBaseNode(repo, rootNode, cfg.getNewsFeedNodeName());
//            initBaseNode(repo, rootNode, cfg.getNotificationsNodeName());
//            initBaseNode(repo, rootNode, cfg.getImagesNodeName());
//            initBaseNode(repo, rootNode, cfg.getTagNodeName());
            

//            initDocumentsNode(rootNode);
        } catch (Exception e) {
            log.error("Error processing new content", e);
        }
    }

    private EnterpriseContent initBaseNode(ContentRepository repo, EnterpriseContent rootNode, String nodeName) throws ContentRepositoryNotAvailableException {
        Optional<List<EnterpriseContent>> byViewIdLanguage;
        EnterpriseContent foundNode = null;
        if(rootNode == null) {
            byViewIdLanguage = repo.getByViewIdLanguage(cfg.getRootNodeName(), false, null);
        } else {
            byViewIdLanguage = repo.getByViewIdLanguage(nodeName, false, "");
        }
        if (byViewIdLanguage.isPresent()) {
            foundNode = byViewIdLanguage.get().get(0);
        }
        if(foundNode == null || foundNode.getContentType().equals(ContentType.ERROR)) {
            log.info("[CMS] : ENABLED : Content Repository Root Node initialization");

            if(rootNode == null) {
                foundNode = new Folder(cfg.getRootNodeName());
                foundNode.setParentPath(repo.getRootNodePath());
                foundNode.setViewId(foundNode.getName());
            } else {
                foundNode = new Folder(nodeName);
                foundNode.setParentPath(rootNode.getJcrPath());
                foundNode.setViewId(foundNode.getName());
            }
            repo.save(foundNode);

            log.info("[CMS] : NODE INITIALIZED {}", foundNode);
        } else if(rootNode != null) {
            log.info("[CMS] : ENABLED : Content Repository Node already initialized; Checking for updated locales.");
            repo.updateFolderSupportedLanguages(foundNode.getParentPath(), nodeName);
        }
        return foundNode;
    }
//
//    /**
//     * Initializes the documents node for placing arbitrary binary files
//     *
//     * @param rootNode the Root node where this node will be added
//     */
//    private void initDocumentsNode(EnterpriseContent rootNode) {
//        EnterpriseContent documentsNode = contentService
//                .getFirstByViewIdLanguage(properties.get(CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_DOCS), false, null);
//        EnterpriseContent oafDocumentsContent = null;
//        if (documentsNode == null || documentsNode.getContentType().equals(ContentType.ERROR)) {
//            log.info("[CMS] : ENABLED : Content Repository Documents Node initialization");
//
//            oafDocumentsContent = new Folder(properties.get(CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_DOCS));
//            oafDocumentsContent.setParentPath(rootNode.getJcrPath());
//            oafDocumentsContent.setViewId(oafDocumentsContent.getName());
//
//            documentsNode = oafDocumentsContent;
//            contentService.save(oafDocumentsContent);
//
//            log.info("[CMS] : DOCUMENTS NODE : INITIALIZED {}", oafDocumentsContent);
//        } else {
//            log.info("[CMS] : ENABLED : Content Repository Documents Node already initialized; Checking for updated locales.");
//            repository.updateFolderSupportedLanguages(documentsNode.getParentPath(), properties.get(CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_DOCS));
//            oafDocumentsContent = documentsNode;
//        }
//        if(documentsNode != null) {
//            String[] folderNames = properties.get(CmsConstants.PropertyKeys.SYSTEM_DMS_CFG_AUTO_CREATE_FOLDERS)
//                    .split(",");
//            List<String> children = contentService.getChildrenFiles(documentsNode.getViewId())
//                    .stream().filter(ec -> ec.getContentType() == ContentType.FOLDER).map(EnterpriseContent::getName).collect(Collectors.toList());
//            for (String folderName : folderNames) {
//                if (!children.contains(folderName)) {
//                    EnterpriseContent folder = new Folder(folderName);
//                    folder.setParentPath(oafDocumentsContent.getJcrPath());
//                    folder.setViewId(folderName);
//                    contentService.save(folder);
//                    log.info("[CMS] folder created: {}", folder);
//                }
//            }
//        }
//    }
//
//    /**
//     * Seeds all the content present in this project json files
//     */
//    private void initContent() {
//    	for (EnterpriseContent content : dataProvider.getContents()) {
//    		String viewId = content.getViewId();
//    		String language = content.getLanguage();
//	    	try {
//                content.setLastEditDate(new Date());
//                content.setStep(StepEnum.PUBLISHED); //By default all seed content is published
//                content.setAuthor("System");
//                List<EnterpriseContent> exists = contentService.getByViewIdLanguage(content.getViewId(), false, language);
//                if(exists != null && !exists.isEmpty() && content.getLanguage().equals(exists.get(exists.size() - 1).getLanguage())) {
//                    EnterpriseContent rootVersion = exists.get(exists.size() - 1);
//                    if(shouldUpdateContent(content, rootVersion)) {
//                        contentService.save(content);
//                        log.info("Updated content with viewID {}", content.getViewId());
//                    }
//                } else {
//                    contentService.save(content);
//                    log.info("Saved new content with viewID {}", content.getViewId());
//                }
//	        } catch (Exception e) {
//	            log.error("Error seeding viewId: {} with language: {}",viewId,language, e);
//	        }
//    	}
//    }
//
//    private boolean shouldUpdateContent(EnterpriseContent content, EnterpriseContent rootVersion) {
//        if (rootVersion == null || rootVersion.getContentType().equals(ContentType.ERROR)
//                || (rootVersion.getContentType() != ContentType.TAG && !rootVersion.getParentPath().equals(content.getParentPath()))) {
//            return true;
//        }
//        else {
//            if (content instanceof GenericVersionableEnterpriseContent) {
//                return false;
//            } else {
//                return true;
//            }
//        }
//    }
}

