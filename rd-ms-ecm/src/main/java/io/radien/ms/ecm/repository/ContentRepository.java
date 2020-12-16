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
package io.radien.ms.ecm.repository;


import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.jcr.version.VersionManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.util.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.TreeNode;

import io.radien.api.service.ecm.exception.ContentRepositoryNotAvailableException;
import io.radien.api.service.ecm.exception.ElementNotFoundException;
import io.radien.api.service.ecm.model.ContentType;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.ecm.model.GenericEnterpriseContent;
import io.radien.api.service.ecm.model.RestTreeNode;
import io.radien.ms.ecm.config.ConfigProvider;
import io.radien.ms.ecm.legacy.CmsConstants;
import io.radien.ms.ecm.legacy.CmsSeeder;
import io.radien.ms.ecm.legacy.ContentFactory;
import io.radien.ms.ecm.legacy.JcrSessionHandler;
import io.radien.ms.ecm.legacy.RepositoryNodeService;

/**
 * @author Marco Weiland <m.weiland@radien.io>
 */
@ApplicationScoped
public class ContentRepository implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(ContentRepository.class);
    private static final String FILE_SEPARATOR = "/";

    private static final long serialVersionUID = 1L;

    private static final String JCR_OR = " or [";
    private static final String JCR_AND = " and [";
    private static final String JCR_AND_ESCAPED = "' " + JCR_AND;
    public static final String JCR_END_OBJ = "] = '";

    public static final String IS_TRUE = "] = 'true' ";

    @Inject
    private ConfigProvider cfg;
    @Inject
    private ContentFactory contentFactory;
    @Inject
    private JcrSessionHandler sessionHandler;
    @Inject
    private RepositoryNodeService nodeService;
    @Inject 
    private CmsSeeder initializer;
    
    
    @PostConstruct
    private void init() {
    	
    	initializer.init(this, nodeService);
    	
    }
    
    
    /**
     * saves the given {@link EnterpriseContent} in the jackrabbit repository
     *
     * @param obj the obj to be saved in the JCR
     */
    public void save(EnterpriseContent obj) throws ContentRepositoryNotAvailableException {
        Session session = sessionHandler.createSession();
        String viewId = obj.getViewId();
        String language = obj.getLanguage();
        Node content = null;
        boolean isMoveCommand = false;
        String newPath = null;
        String viewIdEscaped = Text.escapeIllegalJcrChars(obj.getName());
        
        if (StringUtils.isBlank(viewId) || StringUtils.isBlank(language)) {
            contentFactory.decorateNewContent(obj);
        } else {
            try {
                List<Node> nodeList = nodeService.getNodeByViewIdLanguage(session, viewId, false, language);

                if (!nodeList.isEmpty()) {
                    content = nodeList.get(nodeList.size()-1);
                }
                if (content != null && obj.getParentPath() == null) {
                    obj.setParentPath(content.getParent().getPath());
                }
                newPath = obj.getParentPath() + FILE_SEPARATOR + viewIdEscaped;
                if (content != null && !newPath.equals(content.getPath())) {
                    isMoveCommand = true;
                }
            } catch (RepositoryException | ContentRepositoryNotAvailableException e) {
                log.error("Error accessing the JCR repository while trying to find content", e);
            }
        }

        content = nodeService.getNodeIfPathExists(session, obj, content);

        Node parent = nodeService.getParentIfParentPathExists(session, obj);

        Pair<Boolean, String> moveCommandValues = new ImmutablePair<>(isMoveCommand, newPath);

        process(session, obj, language, content, parent, moveCommandValues, viewIdEscaped);

    }

    private void process(Session session, EnterpriseContent obj, String language, Node content, Node parent,
                         Pair<Boolean, String> moveCommandValues, String viewIdEscaped) {
        try {
            boolean isVersionable = false;
            
            isVersionable = true;
            
            if (content == null) {
                content = setUpNodeByType(session, obj, language, parent, viewIdEscaped);
                nodeService.setUpNewNodeMixins(content, isVersionable, obj);
            }
            contentFactory.syncNode(content, obj, session);
            session.save();

            if (content != null) {
                if (Boolean.TRUE.equals(moveCommandValues.getLeft())) {
                    session.move(content.getPath(), moveCommandValues.getRight());
                }
                session.save();
                if(isVersionable && content.isCheckedOut()) {
                    VersionManager versionManager = session.getWorkspace().getVersionManager();
                    versionManager.checkin(content.getPath());
                }
                log.info("[+] Added content:{} ", content.getIdentifier());
            }
        } catch (ItemExistsException e) {
            log.warn("Item {} already exists in this repository, skipping", obj.getName());
        } catch (RepositoryException e) {
            log.error("Error saving content", e);
        } finally {
            session.logout();
        }
    }

    private Node setUpNodeByType(Session session, EnterpriseContent obj, String language, Node parent, String viewIdEscaped) throws RepositoryException {
        Node content = null;
        switch (obj.getContentType()) {
            case DOCUMENT:
                content = nodeService.addNodeToParent(session, language, parent, viewIdEscaped, cfg.getDocumentsNodeName(), false);
                break;
            case FOLDER:
                content = nodeService.prepareFolderNode(session, language, parent, viewIdEscaped, cfg.getSupportedLanguages());
                break;
            case HTML:
                content = nodeService.addNodeToParent(session, language, parent, viewIdEscaped, cfg.getHtmlNodeName(), true);
                break;
            case NEWS_FEED:
                content = nodeService.addNodeToParent(session, language, parent, viewIdEscaped, cfg.getNewsFeedNodeName(), true);
                break;
            case TAG:
                content = nodeService.prepareTagNode(session, language, parent, viewIdEscaped);
                break;
            case IMAGE:
                content = nodeService.addNodeToParent(session, language, parent, viewIdEscaped, cfg.getImagesNodeName(), false);
                break;
            case NOTIFICATION:
                content = nodeService.addNodeToParent(session, language, parent, viewIdEscaped, cfg.getNotificationsNodeName(), true);
                break;
            
            case ERROR:
                log.error("Content type ERROR was atempted to be processed.");
                break;
        }
        return content;
    }

    public EnterpriseContent loadFile(String jcrPath)
            throws ContentRepositoryNotAvailableException {
        Session session = sessionHandler.createSession();
        EnterpriseContent content = null;
        try {
            content = getContentByVersionPath(jcrPath, session);
            content.setFile(contentFactory.getFile(session, jcrPath));
        } catch (PathNotFoundException e) {
            log.error("Error loading EnterpriseContent File", e);
        } catch (RepositoryException | IOException e) {
            log.error("Error loading file", e);
            throw new ContentRepositoryNotAvailableException();
        } finally {
            session.logout();
        }
        return content;
    }

    public void delete(EnterpriseContent obj) throws ContentRepositoryNotAvailableException {
        Session session = sessionHandler.createSession();
        try {
            session.removeItem(obj.getJcrPath());
            session.save();

        } catch (RepositoryException e) {
            log.error("Error deleting EnterpriseContent file", e);
        } finally {
            session.logout();
        }
    }

    public String getAppInfoId(String content, String app, String language)
            throws ContentRepositoryNotAvailableException {

        List<EnterpriseContent> ecList = new ArrayList<>();
        Session session = sessionHandler.createSession();
        try {
            QueryManager queryManager = session.getWorkspace().getQueryManager();

            String expression = "select * from [" + CmsConstants.RD_MIXIN_NODE_PROPS + "] as base where ["
                    + CmsConstants.RD_CONTENT_TYPE + JCR_END_OBJ + content + JCR_AND_ESCAPED
                    + CmsConstants.RD_APP + JCR_END_OBJ + app + JCR_AND_ESCAPED
                    + CmsConstants.RD_CONTENT_LANG + JCR_END_OBJ + language + "'";

            Query query = queryManager.createQuery(expression, Query.JCR_SQL2);

            QueryResult result = query.execute();

            final NodeIterator nodes = result.getNodes();
            while (nodes.hasNext()) {
                ecList.add(contentFactory.convertJCRNode((Node) nodes.next()));
            }

        } catch (RepositoryException e) {
            throw new ContentRepositoryNotAvailableException();
        } finally {
            session.logout();
        }
        if (!ecList.isEmpty()) {
            return ecList.get(0).getViewId();
        }

        return null;
    }

    public Optional<List<EnterpriseContent>> getByViewIdLanguage(String viewId, boolean activeOnly, String language)
            throws ContentRepositoryNotAvailableException {
        Session session = sessionHandler.createSession();
        List<Node> nodeList = nodeService.getNodeByViewIdLanguage(session, viewId, activeOnly, language);
        if (nodeList.isEmpty()) {
            return Optional.empty();
        }

        try {
            List<EnterpriseContent> ecList = new ArrayList<>();
            for (Node node : nodeList) {
                ecList.add(contentFactory.convertJCRNode(node));
            }
            return Optional.of(ecList);
        } catch (RepositoryException e) {
            throw new ContentRepositoryNotAvailableException();
        } finally {
          session.logout();
        }
    }

    public List<EnterpriseContent> getByContentType(ContentType contentType, String language) throws ContentRepositoryNotAvailableException {

        List<EnterpriseContent> fooList = new ArrayList<>();
        Session session = sessionHandler.createSession();
        try {
            QueryManager queryManager = session.getWorkspace().getQueryManager();

            String expression = "select * from " + "[" + CmsConstants.RD_MIXIN_NODE_PROPS + "] as b where " + "["
                    + CmsConstants.RD_CONTENT_TYPE + JCR_END_OBJ + contentType.key() + JCR_AND_ESCAPED
                    + CmsConstants.RD_CONTENT_LANG + JCR_END_OBJ + language + JCR_AND_ESCAPED
                    + CmsConstants.RD_SYSTEM + IS_TRUE + JCR_AND
                    + CmsConstants.RD_ACTIVE + IS_TRUE;

            Query query = queryManager.createQuery(expression, Query.JCR_SQL2);

            QueryResult result = query.execute();

            final NodeIterator nodes = result.getNodes();
            while (nodes.hasNext()) {
                Node contentNode = (Node) nodes.next();
                nodeService.convertJcrNode(fooList, contentNode);
            }

        } catch (RepositoryException e) {
            throw new ContentRepositoryNotAvailableException();
        } finally {
            session.logout();
        }

        return fooList;

    }

    public String getAppDesc(String app, String language) throws ContentRepositoryNotAvailableException {
        StringBuilder desc = new StringBuilder();
        Session session = sessionHandler.createSession();
        try {
            QueryManager queryManager = session.getWorkspace().getQueryManager();

            String expression = "select [" + CmsConstants.RD_HTML_CONTENT + "] from [" + CmsConstants.RD_MIXIN_NODE_PROPS + "] as b where ["
                    + CmsConstants.RD_CONTENT_TYPE + "] = 'appinfo' and ["
                    + CmsConstants.RD_APP + JCR_END_OBJ + app + "' and ["
                    + CmsConstants.RD_CONTENT_LANG + JCR_END_OBJ + language + "'";

            Query query = queryManager.createQuery(expression, Query.JCR_SQL2);

            QueryResult result = query.execute();

            final NodeIterator nodes = result.getNodes();
            while (nodes.hasNext()) {
                Node contentNode = (Node) nodes.next();
                EnterpriseContent teste = contentFactory.convertJCRNode(contentNode);
                desc.append(teste.getHtmlContent());
            }
        } catch (RepositoryException e) {
            throw new ContentRepositoryNotAvailableException();
        } finally {
            session.logout();
        }

        return desc.toString();
    }

    public Map<String, String> getAppDescriptions(String language) throws ContentRepositoryNotAvailableException {
        Map<String, String> resultDescriptions = new HashMap<>();
        Session session = sessionHandler.createSession();
        try {
            QueryManager queryManager = session.getWorkspace().getQueryManager();

            String expression = "select [" + CmsConstants.RD_HTML_CONTENT + "] from [" + CmsConstants.RD_MIXIN_NODE_PROPS + "] as b where ["
                    + CmsConstants.RD_CONTENT_TYPE + "] = 'appinfo' and ["
                    + CmsConstants.RD_CONTENT_LANG + JCR_END_OBJ + language + "'";

            Query query = queryManager.createQuery(expression, Query.JCR_SQL2);

            QueryResult result = query.execute();

            final NodeIterator nodes = result.getNodes();
            while (nodes.hasNext()) {
                Node contentNode = (Node) nodes.next();
                EnterpriseContent descriptionContent = contentFactory.convertJCRNode(contentNode);
                resultDescriptions.put(descriptionContent.getApp(),
                        descriptionContent.getHtmlContent());
            }
        } catch (RepositoryException e) {
            throw new ContentRepositoryNotAvailableException();
        } finally {
            session.logout();
        }

        return resultDescriptions;
    }

//    public List<EnterpriseContent> searchContent(boolean enableSystemContent, List<String> tags, Pageable pageable) throws ContentRepositoryNotAvailableException {
//
//        List<EnterpriseContent> fooList = new ArrayList<>();
//        Session session = sessionHandler.createSession();
//        try {
//            QueryManager queryManager = session.getWorkspace().getQueryManager();
//
//            StringBuilder expression = new StringBuilder("" + "select * " + "from " + "[" + CmsConstants.RD_MIXIN_NODE_PROPS + "] as b " + "where " + "["
//                    + CmsConstants.RD_SYSTEM + JCR_END_OBJ + enableSystemContent + "' " + "and (" + "["
//                    + CmsConstants.RD_CONTENT_TYPE + JCR_END_OBJ + ContentType.HTML.key() + "' " + JCR_OR
//                    + CmsConstants.RD_CONTENT_TYPE + JCR_END_OBJ + ContentType.NEWS_FEED.key() + "' " + JCR_OR
//                    + CmsConstants.RD_CONTENT_TYPE + JCR_END_OBJ + ContentType.NOTIFICATION.key() + "' " + ") ");
//
//            if (tags != null && !tags.isEmpty()) {
//                for (String tag : tags) {
//                    expression.append(JCR_AND).append(CmsConstants.RD_TAGS).append(JCR_END_OBJ).append(tag).append("' ");
//                }
//            }
//
//            List<String> orders = pageable.getSort().get().map(o -> "[oaf:"
//                    + o.getProperty() + "] " + o.getDirection().name()).collect(Collectors.toList());
//
//            if (!orders.isEmpty()) {
//                expression.append("order by ").append(String.join(", ", orders));
//            }
//
//            Query query = queryManager.createQuery(expression.toString(), Query.JCR_SQL2);
//
//            QueryResult result = query.execute();
//
//            final NodeIterator nodes = result.getNodes();
//            while (nodes.hasNext()) {
//                nodeService.convertJcrNode(fooList, (Node) nodes.next());
//            }
//
//        } catch (RepositoryException e) {
//            throw new ContentRepositoryNotAvailableException();
//        } finally {
//            session.logout();
//        }
//
//        return fooList;
//    }

    public String getRootNodePath() throws ContentRepositoryNotAvailableException {
        Session session = sessionHandler.createSession();
        try {
            return session.getRootNode().getPath();
        } catch (RepositoryException e) {
            throw new ContentRepositoryNotAvailableException();
        } finally {
            session.logout();
        }
    }

    public TreeNode getDocumentsTreeModel() throws ContentRepositoryNotAvailableException {
        TreeNode root = null;
        Session session = sessionHandler.createSession();
        try {
            Node docsNode = nodeService.getNode(session, cfg.getDocumentsNodeName(), false, null);
            if (docsNode != null) {
                String rootName = docsNode.getName();
                EnterpriseContent enterpriseContent = contentFactory.create(rootName, rootName, ContentType.FOLDER);
                root = new RestTreeNode(enterpriseContent, null);
                for (Node node : JcrUtils.getChildNodes(nodeService.getNode(session, cfg.getDocumentsNodeName(), false, null))) {
                    nodeService.loopNodes(root, node);
                }
            }

        } catch (RepositoryException e) {
            log.error("Error getting document tree model", e);
        } finally {
            session.logout();
        }

        return root;
    }

    public Collection<EnterpriseContent> getChildren(String viewId)
            throws ContentRepositoryNotAvailableException, ElementNotFoundException {

        Session session = sessionHandler.createSession();
        List<Node> nodes = nodeService.getNodeByViewIdLanguage(session, viewId, true, null);

        if (nodes.isEmpty()) {
            throw new ElementNotFoundException("Element [" + viewId + "] and [ ] not found");
        }

        Node parent = nodes.get(0);
        List<EnterpriseContent> results = new ArrayList<>();

        try {
            for (Node node : JcrUtils.getChildNodes(parent)) {
                nodeService.loopNodes(results, node, -1, -1);
            }
        } catch (PathNotFoundException e) {
            throw new ElementNotFoundException(e.getMessage());
        } catch (RepositoryException e) {
            throw new ContentRepositoryNotAvailableException();
        } finally {
            session.logout();
        }

        return results;
    }

    public int countByTagName(String name) throws ContentRepositoryNotAvailableException {
        QueryManager queryManager;
        Session session = sessionHandler.createSession();

        try {
            queryManager = session.getWorkspace().getQueryManager();

            String expression = "" + "select * " + "from " + "[" + CmsConstants.RD_MIXIN_NODE_PROPS + "] as b " + "where " + "["
                    + CmsConstants.RD_SYSTEM + IS_TRUE + JCR_AND
                    + CmsConstants.RD_TAGS + JCR_END_OBJ + name + "' and ( ["
                    + CmsConstants.RD_CONTENT_TYPE + JCR_END_OBJ + ContentType.HTML.key() + "' " + JCR_OR
                    + CmsConstants.RD_CONTENT_TYPE + JCR_END_OBJ + ContentType.NEWS_FEED.key() + "' " + JCR_OR
                    + CmsConstants.RD_CONTENT_TYPE + JCR_END_OBJ + ContentType.NOTIFICATION.key() + "' " + ") ";

            Query query = queryManager.createQuery(expression, Query.JCR_SQL2);

            QueryResult result = query.execute();

            return (int) result.getNodes().getSize();

        } catch (RepositoryException e) {
            throw new ContentRepositoryNotAvailableException();
        } finally {
            session.logout();
        }
    }

    public String getOrCreateDocumentsPath(String path) throws ContentRepositoryNotAvailableException, RepositoryException {
        Session session = sessionHandler.createSession();
        Node docsNode = nodeService.getNode(session, cfg.getDocumentsNodeName(), false, null);
        JcrUtils.getOrCreateByPath(String.format("/%s/%s%s", cfg.getRootNodeName(), docsNode.getName(), path), JcrConstants.NT_FOLDER, session);

        session.save();
        session.logout();
        return path;
    }

    public List<EnterpriseContent> getFolderContents(String path) throws ContentRepositoryNotAvailableException, RepositoryException {
        Session session = sessionHandler.createSession();

        Node resultNode = session.getRootNode().getNode(cfg.getRootNodeName());
        if (resultNode != null) {
            resultNode = resultNode.getNode(String.format("%s%s", cfg.getDocumentsNodeName(), path));
        } else {
            throw new RepositoryException("Repository root node not available!");
        }

        List<EnterpriseContent> results = new ArrayList<>();
        nodeService.loopNodes(results, resultNode, 0, 1);

        session.logout();

        return results;
    }

    public void updateFolderSupportedLanguages(String contentPath, String nameEscaped) {
            Session session = null;
        try {
            session = sessionHandler.createSession();
            Node contentNode = session.getNode(contentPath);
            Node rootNode = session.getRootNode().getNode("oaf");
            nodeService.addSupportedLocalesFolder(contentNode, rootNode, nameEscaped, cfg.getSupportedLanguages()); 
            session.save();
        } catch (RepositoryException e) {
            log.error("Repository exception. Not possible to update {}", nameEscaped, e);
        } catch (ContentRepositoryNotAvailableException e) {
            log.error("Content Repository not available. Not possible to update {}", nameEscaped, e);
        } finally {
            if(session != null && session.isLive()) {
                session.logout();
            }
        }

    }

    public List<EnterpriseContent> getContentVersions(String path) throws RepositoryException, ContentRepositoryNotAvailableException {
        List<EnterpriseContent> resultsDTO = new ArrayList<>();
        Session session = sessionHandler.createSession();

        VersionManager versionManager = session.getWorkspace().getVersionManager();
        VersionHistory history = versionManager.getVersionHistory(path);
        VersionIterator iterator = history.getAllVersions();
        while (iterator.hasNext()) {
            Version version = iterator.nextVersion();
            getNodeContentVersions(resultsDTO, version.getFrozenNode());
        }
        session.logout();
        return resultsDTO;
    }

    private void getNodeContentVersions(List<EnterpriseContent> resultsDTO, Node versionNode) throws RepositoryException {
        if (versionNode.getName().equals(JcrConstants.JCR_SYSTEM)) {
            return;
        }

        if (!versionNode.getName().equalsIgnoreCase(JcrConstants.JCR_CONTENT)) {
            GenericEnterpriseContent doc = contentFactory.convertJCRVersionNode(versionNode);
            if(doc != null)  resultsDTO.add(doc);
        }

        NodeIterator nodes = versionNode.getNodes();
        while (nodes.hasNext()) {
            getNodeContentVersions(resultsDTO, nodes.nextNode());
        }
    }

    private List<EnterpriseContent> getCurrentEnterpriseContents(EnterpriseContent enterpriseContent, VersionManager versionManager) throws RepositoryException {
        List<EnterpriseContent> resultsList = new ArrayList<>();

        VersionHistory versionHistory = versionManager.getVersionHistory(enterpriseContent.getJcrPath());
        VersionIterator iterator = versionHistory.getAllVersions();
        while (iterator.hasNext()) {
            Version version = iterator.nextVersion();
            List<EnterpriseContent> versionContent = new ArrayList<>();
            getNodeContentVersions(versionContent , version.getFrozenNode());
            Optional<EnterpriseContent> firstContent = getSingleCurrentEnterpriseContent(versionContent);
            firstContent.ifPresent(resultsList::add);
        }
        return resultsList;
    }

    private Optional<EnterpriseContent> getSingleCurrentEnterpriseContent(List<EnterpriseContent> contentList) {
        if(contentList == null || contentList.isEmpty()) {
            return Optional.empty();
        } else {
            EnterpriseContent firstContent = contentList.get(0);
            if (firstContent.getValidDate() != null && firstContent.getValidDate().before(new Date())) {
                return Optional.of(firstContent);
            } else {
                return Optional.empty();
            }
        }
    }

    private List<EnterpriseContent> parseNodeForVersionableValidContent(Node node, VersionManager versionManager, String docType, String userLanguage,
                                                                                          String defaultLanguage) throws RepositoryException {
        List<EnterpriseContent> resultsList = new ArrayList<>();
        if(node != null) {
            List<EnterpriseContent> currentNodeList = new ArrayList<>();
            nodeService.loopNodes(currentNodeList, node, 0, 1);
            List<EnterpriseContent> possibleMatch = currentNodeList.stream().filter(o -> o.getViewId().contains(docType)
                    && o.getLanguage().equals(userLanguage)).collect(Collectors.toList());
            if(possibleMatch.isEmpty()) {
                possibleMatch = currentNodeList.stream().filter(o -> o.getViewId().contains(docType)
                        && o.getLanguage().equals(defaultLanguage)).collect(Collectors.toList());
            }

            if (!possibleMatch.isEmpty()) {
                for (EnterpriseContent vec : possibleMatch) {
                    resultsList = getCurrentEnterpriseContents(vec, versionManager);
                }
            }
        }
        return resultsList;
    }

    public Optional<EnterpriseContent> getVersionableContentByPathAndTypeAndLanguage(String path, String docType,
                                                                                                       String userLanguage, String defaultLanguage) throws ContentRepositoryNotAvailableException, RepositoryException {
        List<EnterpriseContent> resultsDTO = new ArrayList<>();
        Session session = sessionHandler.createSession();
        VersionManager versionManager = session.getWorkspace().getVersionManager();
        Node currentNode = JcrUtils.getOrCreateByPath(path, JcrConstants.NT_FOLDER, session);
        Node docRootNode = JcrUtils.getNodeIfExists("/oaf/oaf_documents", session);
        while(!docRootNode.getPath().equals(currentNode.getPath()) && resultsDTO.isEmpty()) {
            resultsDTO.addAll(parseNodeForVersionableValidContent(currentNode, versionManager, docType, userLanguage, defaultLanguage));
            if(!resultsDTO.isEmpty()) {
                resultsDTO.sort(Comparator.comparing(EnterpriseContent::getValidDate));
                resultsDTO.get(resultsDTO.size() - 1).setParentPath(currentNode.getPath());
                break;
            }
            currentNode = currentNode.getParent();
        }
        session.logout();
        if(resultsDTO.isEmpty()) {
            log.warn("Could not found for path and language {}; {}", path, userLanguage);
            return Optional.empty();
        }
        return Optional.ofNullable(resultsDTO.get(resultsDTO.size() - 1));
    }

    private GenericEnterpriseContent getContentByVersionPath(String jcrPath, Session session) throws RepositoryException {
        Node result = JcrUtils.getNodeIfExists(jcrPath, session);

        return contentFactory.convertJCRVersionNode(result);
    }

}

