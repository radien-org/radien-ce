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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.TreeNode;

import io.radien.api.service.ecm.exception.ContentRepositoryNotAvailableException;
import io.radien.api.service.ecm.model.ContentType;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.ecm.model.RestTreeNode;

/**
 * @author Marco Weiland
 */
@RequestScoped
public class RepositoryNodeService {
    private static final Logger log = LoggerFactory.getLogger(RepositoryNodeService.class);
    private static final String FILE_SEPARATOR = "/";

    private static final String JCR_AND = " and [";
    public static final String JCR_END_OBJ = "] = '";
    public static final String IS_TRUE = "] = 'true' ";

    @Inject
    @ConfigProperty(name = "system.jcr.node.root")
    private String rootNode;

    @Inject
    @ConfigProperty(name = "system.jcr.node.documents")
    private String documentNode;
    
    @Inject
    @ConfigProperty(name = "system.jcr.node.html")
    private String htmlNode;
    
    @Inject
    @ConfigProperty(name = "system.jcr.node.newsfeed")
    private String newsfeedNode;
    
    @Inject
    @ConfigProperty(name = "system.jcr.node.images")
    private String imageNode;
    
    @Inject
    @ConfigProperty(name = "system.jcr.node.notifications")
    private String notificationsNode;
    
    
    
    @Inject
    private ContentFactory contentFactory;
    @Inject
    private JcrSessionHandler sessionHandler;

    public void registerCNDNodeTypes(String cndFileName) throws ContentRepositoryNotAvailableException {
        Session session = sessionHandler.createSession();
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(cndFileName)) {
            if (resourceAsStream != null) {
                CndImporter.registerNodeTypes(new InputStreamReader(resourceAsStream), session, true);
            }
        } catch (Exception e) {
            log.error("Error registering node type", e);
        } finally {
            session.logout();
        }
    }

    public void setUpNewNodeMixins(Node newNode, boolean isVersionable, EnterpriseContent object) throws RepositoryException {
        if (newNode != null) {
            newNode.addMixin(CmsConstants.OAF_MIXIN_NODE_PROPS);
            if(isVersionable) {
                newNode.addMixin(CmsConstants.OAF_MIXIN_VERSIONABLE);
                newNode.addMixin(CmsConstants.OAF_MIXIN_VERSIONABLE_PROPS);
            }
        }
    }

    public Node getNodeIfPathExists(Session session, EnterpriseContent obj, Node content) {
        try {
            if (content == null && StringUtils.isNotBlank(obj.getJcrPath())) {
                content = session.getNode(obj.getJcrPath());
            }

        } catch (RepositoryException e) {
            log.warn("could not retrieve {}",obj.getJcrPath(), e);
            log.warn("could not retrieve",e);
        }
        return content;
    }

    public Node getParentIfParentPathExists(Session session, EnterpriseContent obj) {
        Node parent = null;
        try {
            if (StringUtils.isNotBlank(obj.getParentPath())) {
                parent = session.getNode(obj.getParentPath());
            }
        } catch (RepositoryException e) {
            log.warn("could not retrieve {}",obj.getJcrPath(), e);
            log.warn("could not retrieve",e);
        }
        return parent;
    }

    public Node addSupportedLocalesFolder(Node content, Node parent, String nameEscaped, List<String> supportedLanguages) throws RepositoryException {
        if (nameEscaped.equals(htmlNode) ||
                nameEscaped.equals(notificationsNode) ||
                nameEscaped.equals(newsfeedNode) ) {

            for (String lang : supportedLanguages) {
                try {
                    if (StringUtils.isNotBlank(lang)) {
                        content = parent.addNode(nameEscaped + FILE_SEPARATOR + lang, JcrConstants.NT_FOLDER);
                    }
                } catch(ItemExistsException e) {
                    log.info("Language {} already exists, skipping...", lang);
                }
            }
        }
        return content;
    }

    public Node getNode(Session session, String nodeType, boolean containsImage, String language) throws RepositoryException {
        Node resultNode = session.getRootNode().getNode(rootNode);
        if (resultNode != null) {
            resultNode = resultNode.getNode(nodeType);
        } else {
            throw new RepositoryException("Repository root node not available!");
        }
        if (containsImage && resultNode != null) {
            resultNode = resultNode.getNode(language);
        }
        return resultNode;
    }

    public Node addNodeToParent(Session session, String language, Node parent, String viewID, String systemCmsCfgNodeDocs, boolean b) throws RepositoryException {
        Node content = null;
        if (parent == null) {
            parent = getNode(session, systemCmsCfgNodeDocs, b, language);
        }
        if (parent != null) {
            content = parent.addNode(viewID, JcrConstants.NT_FILE);
        }
        return content;
    }

    public Node prepareTagNode(Session session, String language, Node parent, String nameEscaped) throws RepositoryException {
        Node content = null;
        if (parent == null) {
            parent = getNode(session, CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_TAG, false, language);
        }
        if (parent != null) {
            content = parent.addNode(nameEscaped, JcrConstants.NT_FILE);
        }
        return content;
    }

    public Node prepareFolderNode(Session session, String language, Node parent, String nameEscaped, List<String> supportedLanguages) throws RepositoryException {
        Node content = null;
        if (parent == null) {
            parent = getNode(session, CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_DOCS, false, language);
        }
        if (parent != null) {
            content = parent.addNode(nameEscaped, JcrConstants.NT_FOLDER);
        }
        content = addSupportedLocalesFolder(content, parent, nameEscaped, supportedLanguages);
        return content;
    }

    public void loopNodes(List<EnterpriseContent> results, Node node, int cLevel, int nLevels) throws RepositoryException {

        if (node.getName().equals(JcrConstants.JCR_SYSTEM)) {
            return;
        }

        if (!node.getName().equalsIgnoreCase(JcrConstants.JCR_CONTENT)) {
            EnterpriseContent doc = contentFactory.convertJCRNode(node);
            results.add(doc);
        }

        NodeIterator nodes = node.getNodes();
        if(nLevels == -1 || cLevel < nLevels) {
            while (nodes.hasNext()) {
                loopNodes(results, nodes.nextNode(), cLevel + 1, nLevels);
            }
        }
    }

    public void loopNodes(TreeNode documentParent, Node node) throws RepositoryException {
        // Skip the virtual (and large!) jcr:system subtree && jcr:content child
        // (so binaries are only loaded on demand)
        if (JcrConstants.JCR_SYSTEM.equals(node.getName()) || JcrConstants.JCR_CONTENT.equals(node.getName())) {
            return;
        }

        EnterpriseContent doc = contentFactory.convertJCRNode(node);
        TreeNode treeNode;
        if (doc.getContentType() == ContentType.DOCUMENT) {
            treeNode = new RestTreeNode(doc.getContentType().key(), doc, documentParent);
        } else {
            treeNode = new RestTreeNode(doc, documentParent);
        }
        //TODO: CHECK THIS
        //FIXME: fix it
//        documentParent.getChildren().add(treeNode);

        NodeIterator nodes = node.getNodes();
        while (nodes.hasNext()) {
            loopNodes(treeNode, nodes.nextNode());
        }
    }

    public List<Node> getNodeByViewIdLanguage(Session session, String viewId, boolean activeOnly, String language)
            throws ContentRepositoryNotAvailableException {

        List<Node> results = new ArrayList<>();
        // Obtain the query manager for the session via the workspace ...
        QueryManager queryManager;
        try {
            queryManager = session.getWorkspace().getQueryManager();

            String expression = "select * from " + "[" + CmsConstants.OAF_MIXIN_NODE_PROPS + "] as node " + "where ["
                    + CmsConstants.OAF_VIEW_ID + JCR_END_OBJ + viewId + "' ";

            if (StringUtils.isNotBlank(language)) {
                expression += JCR_AND + CmsConstants.OAF_CONTENT_LANG + JCR_END_OBJ + language + "' ";
            }
            if (activeOnly) {
                expression += JCR_AND + CmsConstants.OAF_ACTIVE + IS_TRUE;
            }

            Query query = queryManager.createQuery(expression, Query.JCR_SQL2);
            QueryResult result = query.execute();

            final NodeIterator nodes = result.getNodes();
            while (nodes.hasNext()) {
                results.add((Node) nodes.next());
            }

        } catch (RepositoryException e) {
            throw new ContentRepositoryNotAvailableException();
        }

        return results;
    }

    public void convertJcrNode(List<EnterpriseContent> fooList, Node contentNode) {
        try {
            EnterpriseContent content = contentFactory.convertJCRNode(contentNode);
            if (content != null) {
                fooList.add(content);
            }
        } catch (Exception e) {
            log.info("Error getting content by type", e);
        }
    }
}
