/*
 * Copyright (c) 2021-present radien GmbH. All rights reserved.
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
 *
 */
package io.radien.ms.ecm;

import com.fasterxml.jackson.core.TreeNode;
import io.radien.api.Appframeable;
import io.radien.api.OAFAccess;
import io.radien.api.OAFProperties;
import io.radien.api.service.ecm.exception.ContentRepositoryNotAvailableException;
import io.radien.api.service.ecm.exception.ElementNotFoundException;
import io.radien.api.service.ecm.model.*;
import io.radien.ms.ecm.constants.CmsConstants;
import io.radien.ms.ecm.domain.ContentDataProvider;
import io.radien.ms.ecm.util.ContentMappingUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.util.Text;
import org.eclipse.microprofile.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.jcr.*;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.version.VersionManager;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.*;

/**
 * Main implementation of the
 *
 * @author Marco Weiland
 */
public @RequestScoped @Default class ContentRepository implements Serializable, Appframeable {

	private static final Logger log = LoggerFactory.getLogger(ContentRepository.class);
	private static final long serialVersionUID = 3705349362214763287L;

	private long initCount = 0;

	private static final String FILE_SEPARATOR = "/";

	private static final String JCR_OR = " or [";
	private static final String JCR_AND = " and [";
	private static final String JCR_AND_ESCAPED = "' " + JCR_AND;
	public static final String JCR_END_OBJ = "] = '";
	public static final String IS_TRUE = "] = true ";

	@Inject
	private OAFAccess oaf;
	@Inject
	private Config properties;
	@Inject
	private ContentMappingUtils contentMappingUtils;
	@Inject
	private ContentDataProvider dataProvider;
	@Inject
	private Repository repository;

	@PostConstruct
	private void init() {
	}

	public void save(EnterpriseContent obj) throws ContentRepositoryNotAvailableException, RepositoryException {
		Session session = createSession();
		String viewId = obj.getViewId();
		String language = obj.getLanguage();
		Node content = null;
		boolean isMoveCommand = false;
		String newPath = null;
		String nameEscaped = Text.escapeIllegalJcrChars(obj.getName());

		if (StringUtils.isBlank(viewId) || StringUtils.isBlank(language)) {
			//TODO: REVIEW - Sets default properties
			contentMappingUtils.decorateNewContent(obj);
		} else {
			try {
				List<Node> nodeList = getNodeByViewIdLanguage(session, viewId, false, language);

				if (!nodeList.isEmpty()) {
					content = nodeList.get(nodeList.size() - 1);
				}
				if (content != null && obj.getParentPath() == null) {
					obj.setParentPath(content.getParent().getPath());
				}
				newPath = obj.getParentPath() + FILE_SEPARATOR + nameEscaped;
				if (content != null && !newPath.equals(content.getPath())) {
					isMoveCommand = true;
				}
			} catch (RepositoryException | ContentRepositoryNotAvailableException e) {
				log.error("Error accessing the JCR repository while trying to find content", e);
			}
		}
		content = getContentIfPathExists(session, obj, content);
		Node parent = getParentIfParentPathExists(session, obj);

		process(session, obj, language, content, parent, isMoveCommand, nameEscaped, newPath);
	}

	private void process(Session session, EnterpriseContent obj, String language, Node content,
						 Node parent, boolean isMoveCommand, String nameEscaped, String newPath) throws RepositoryException {
		try {
			boolean isVersionable = obj instanceof SystemVersionableEnterpriseContent && ((SystemVersionableEnterpriseContent)obj).isVersionable();
			if (content == null) {
				switch (obj.getContentType()) {
					case DOCUMENT:
						content = addNodeToParent(session, language, parent, nameEscaped, CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_DOCS, false);
						break;
					case HTML:
						content = addNodeToParent(session, language, parent, nameEscaped, CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_HTML, true);
						break;
					case IMAGE:
						content = addNodeToParent(session, language, parent, nameEscaped, CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_IMAGE, false);
						break;
					case FOLDER:
						content = prepareFolderNode(session, language, parent, nameEscaped);
						break;
					case NOTIFICATION:
						content = addNodeToParent(session, language, parent, nameEscaped, CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_NOTIFICATION, true);
						break;
					case TAG:
						content = prepareTagNode(session, language, parent, nameEscaped);
						break;
					default:
						log.error("Unknown doc type {}", obj.getContentType());
				}

				if (content == null) {
					throw new RepositoryException("Content is null");
				}
				setupNodeMixins(content, obj);
			}
			VersionManager versionManager = session.getWorkspace().getVersionManager();
			if(isVersionable && !content.isCheckedOut()) {
				versionManager.checkout(content.getPath());
			}

			contentMappingUtils.syncNode(content, obj, session);
			session.save();

			if(isVersionable && content.isCheckedOut()) {
				versionManager.checkin(content.getPath());
			}

			if (isMoveCommand) {
				if(isVersionable && !content.isCheckedOut()) {
					versionManager.checkout(content.getPath());
				}
				session.move(content.getPath(), newPath);
				if(isVersionable && content.isCheckedOut()) {
					versionManager.checkin(content.getPath());
				}
			}
			session.save();
			log.info("[+] Added content:{} ", content.getIdentifier());
		} catch (ItemExistsException e) {
			log.warn("Item {} already exists in this repository, skipping", obj.getName());
		}  finally {
			session.logout();
		}

	}

	private void setupNodeMixins(Node content, EnterpriseContent obj) throws RepositoryException {
		content.addMixin(CmsConstants.RADIEN_GENERIC_CONTENT_MIXIN);
		if(Arrays.asList(ContentType.DOCUMENT, ContentType.IMAGE).contains(obj.getContentType())) {
			content.addMixin(CmsConstants.RADIEN_FILE_CONTENT_MIXIN);
		} else if(ContentType.HTML.equals(obj.getContentType())) {
			content.addMixin(CmsConstants.RADIEN_HTML_CONTENT_MIXIN);
		}
		if(obj instanceof SystemVersionableEnterpriseContent) {
			content.addMixin(JcrConstants.MIX_VERSIONABLE);
			content.addMixin(CmsConstants.RADIEN_VERSIONABLE_CONTENT_MIXIN);
		}
		if(obj instanceof SystemMandatoryEnterpriseContent) {
			content.addMixin(CmsConstants.RADIEN_MANDATORY_CONTENT_MIXIN);
		}
	}

	private Node getContentIfPathExists(Session session, EnterpriseContent obj, Node content) {
		try {
			if (content == null && StringUtils.isNotBlank(obj.getJcrPath())) {
				content = JcrUtils.getNodeIfExists(obj.getJcrPath(), session);
			}
		} catch(RepositoryException e) {
				log.warn("could not retrieve {}",obj.getJcrPath(), e);
				log.warn("could not retrieve",e);
		}
		return content;
	}

	private Node getParentIfParentPathExists(Session session, EnterpriseContent obj) {
		Node parent = null;
		try {
			if(StringUtils.isNotBlank(obj.getParentPath())) {
				parent = JcrUtils.getNodeIfExists(obj.getParentPath(), session);
			}
		} catch (RepositoryException e) {
			log.warn("could not retrieve {}",obj.getJcrPath(), e);
			log.warn("could not retrieve",e);
		}
		return parent;
	}

	private Node addNodeToParent(Session session, String language, Node parent, String viewID, String systemCmsCfgNodeDocs,
								 boolean b) throws RepositoryException {
		Node content = null;
		if(parent == null) {
			parent = getNode(session, systemCmsCfgNodeDocs, b, language);
		}
		if(parent != null) {
			content = parent.addNode(viewID, CmsConstants.RADIEN_BASE_NODE_TYPE);
		}
		return content;
	}

	private Node prepareTagNode(Session session, String language, Node parent, String nameEscaped) throws RepositoryException {
		Node content = null;
		if(parent == null) {
			parent = getNode(session, CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_TAG, false, language);
		}
		if(parent != null) {
			content = parent.addNode(nameEscaped, JcrConstants.NT_FILE);
		}
		return content;
	}

	private Node prepareFolderNode(Session session, String language, Node parent, String nameEscaped) throws RepositoryException {
		Node content = null;
		if(parent == null) {
			parent = getNode(session, CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_DOCS, false, language);
		}
		if(parent != null) {
			content = parent.addNode(nameEscaped, JcrConstants.NT_FOLDER);
		}
		addSupportedLocalesFolder(content, parent, nameEscaped);
		return content;
	}

	public EnterpriseContent loadFile(String jcrPath)
			throws ContentRepositoryNotAvailableException {
		EnterpriseContent content = null;
			Session session = createSession();
		try {
			content = getContentByPath(jcrPath, session);
			content.setFile(contentMappingUtils.getFile(session, jcrPath));
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

	private EnterpriseContent getContentByPath(String jcrPath, Session session) throws RepositoryException {
		Node result = JcrUtils.getNodeIfExists(jcrPath, session);
		return contentMappingUtils.convertJCRNode(result);
	}

	public void delete(String path) throws ContentRepositoryNotAvailableException, RepositoryException {
		Session session = createSession();
		try {
			session.removeItem(path);
			session.save();
		} finally {
			session.logout();
		}
	}

	public void delete(EnterpriseContent obj) throws ContentRepositoryNotAvailableException, RepositoryException {
		delete(obj.getJcrPath());
	}

	private Node getNodeByViewId(String viewId, boolean activeOnly)
			throws ContentRepositoryNotAvailableException, ElementNotFoundException {
		Session session = createSession();
		List<Node> results = new ArrayList<>();

		// Obtain the query manager for the session via the workspace ...
		QueryManager queryManager;
		try {
			queryManager = session.getWorkspace().getQueryManager();

			String expression = "" + "select * from " + "[" + CmsConstants.RADIEN_BASE_NODE_TYPE + "] as node " + "where ["
					+ CmsConstants.RADIEN_VIEW_ID + "] = '" + viewId + "' ";
			if (activeOnly) {
				expression += " and [" + CmsConstants.RADIEN_ACTIVE + "] = '" + activeOnly + "' ";
			}

			Query query = queryManager.createQuery(expression, Query.JCR_SQL2);
			// TODO: IMPLEMENT PAGINATING
			// query.setLimit(pageSize);
			// query.setOffset((pageNumber - 1) * pageSize);

			QueryResult result = query.execute();

			final NodeIterator nodes = result.getNodes();
			while (nodes.hasNext()) {
				results.add((Node) nodes.next());

			}

		} catch (RepositoryException e) {
			throw new ContentRepositoryNotAvailableException();
		} finally {
			session.logout();
		}
		if (!results.isEmpty()) {
			return results.get(0);
		}

		throw new ElementNotFoundException("Element [" + viewId + "] not found");

	}

	public EnterpriseContent getByViewId(String viewId, boolean activeOnly)
			throws ContentRepositoryNotAvailableException, ElementNotFoundException {
		Node node = getNodeByViewId(viewId, activeOnly);

		try {
			return contentMappingUtils.convertJCRNode(node);
		} catch (RepositoryException e) {
			throw new ContentRepositoryNotAvailableException();
		}

	}

	public Optional<List<EnterpriseContent>> getByViewIdLanguage(String viewId, boolean activeOnly, String language)
			throws ContentRepositoryNotAvailableException {
		Session session = createSession();
		List<Node> nodeList = getNodeByViewIdLanguage(session, viewId, activeOnly, language);
		if (nodeList.isEmpty()) {
			return Optional.empty();
		}

		try {
			List<EnterpriseContent> ecList = new ArrayList<>();
			for (Node node : nodeList) {
				ecList.add(contentMappingUtils.convertJCRNode(node));
			}
			return Optional.ofNullable(ecList);
		} catch (RepositoryException e) {
			throw new ContentRepositoryNotAvailableException();
		} finally {
			session.logout();
		}
	}

	private List<Node> getNodeByViewIdLanguage(Session session, String viewId, boolean activeOnly, String language)
			throws ContentRepositoryNotAvailableException {

		List<Node> results = new ArrayList<>();
		QueryManager queryManager;
		try {
			queryManager = session.getWorkspace().getQueryManager();

			String expression = "select * from " + "[" + CmsConstants.RADIEN_GENERIC_CONTENT_MIXIN + "] as node " + "where ["
					+ CmsConstants.RADIEN_VIEW_ID + JCR_END_OBJ + viewId + "' ";

			if (StringUtils.isNotBlank(language)) {
				expression += JCR_AND + CmsConstants.RADIEN_CONTENT_LANG + JCR_END_OBJ + language + "' ";
			}
			if (activeOnly) {
				expression += JCR_AND + CmsConstants.RADIEN_ACTIVE + IS_TRUE;
			}

			Query query = queryManager.createQuery(expression, Query.JCR_SQL2);
			NodeIterator nodes = query.execute().getNodes();
			while (nodes.hasNext()) {
				results.add((Node) nodes.next());
			}
		} catch (RepositoryException e) {
			throw new ContentRepositoryNotAvailableException();
		}

		return results;
	}

	public void registerCNDNodeTypes(String cndFileName) throws ContentRepositoryNotAvailableException {
		Session session = createSession();
		NodeType[] nodeTypes;
		try {
			nodeTypes = CndImporter.registerNodeTypes(
					new InputStreamReader(getClass().getClassLoader().getResourceAsStream(cndFileName)), session);
			for (NodeType nt : nodeTypes) {
				log.info("Registered: " + nt.getName());
			}
		} catch (Exception e) {
			log.error("Error registering node type", e);
		} finally {
			session.logout();
		}
	}

	public void updateFolderSupportedLanguages(String contentPath, String nameEscaped) throws RepositoryException, ContentRepositoryNotAvailableException {
		Session session = createSession();

		try {
			Node contentNode = session.getNode(contentPath);
			Node rootNode = session.getRootNode().getNode("radien");
			addSupportedLocalesFolder(contentNode, rootNode, nameEscaped);
			session.save();
		} catch (RepositoryException e) {
			log.error("Repository exception. Not possible to update {}", nameEscaped, e);
			throw e;
		} finally {
			session.logout();
		}
	}

	private void addSupportedLocalesFolder(Node content, Node parent, String nameEscaped) throws RepositoryException {
		if (nameEscaped.equals(properties.getValue(CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_HTML, String.class)) ||
				nameEscaped.equals(properties.getValue(CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_NOTIFICATION, String.class))) {

			for (String lang : dataProvider.getSupportedLanguages()) {
				try {
					if (StringUtils.isNotBlank(lang)) {
						parent.addNode(nameEscaped + FILE_SEPARATOR + lang, JcrConstants.NT_FOLDER);
					}
				} catch(ItemExistsException e) {
					log.info("Language {} already exists, skipping...", lang);
				}
			}
		}
	}

	public List<EnterpriseContent> getByContentType(ContentType contentType, boolean activeOnly,
													boolean includeSystemContent) throws ContentRepositoryNotAvailableException {
		Session session = createSession();

		List<EnterpriseContent> fooList = new ArrayList<>();

		// Obtain the query manager for the session via the workspace ...
		QueryManager queryManager;
		try {
			queryManager = session.getWorkspace().getQueryManager();

			String expression = "" + "select * " + "from " + "[" + JcrConstants.NT_BASE + "] as b " + "where " + "["
					+ CmsConstants.RADIEN_CONTENT_TYPE + "] = '" + contentType.key() + "' " + "and ["
					+ CmsConstants.RADIEN_SYSTEM + "] = '" + includeSystemContent + "' ";
			if (activeOnly) {
				expression += "and [" + CmsConstants.RADIEN_ACTIVE + "] = '" + activeOnly + "' ";
			}

			Query query = queryManager.createQuery(expression, Query.JCR_SQL2);
			// TODO: IMPLEMENT PAGINATING
			// query.setLimit(pageSize);
			// query.setOffset((pageNumber - 1) * pageSize);

			QueryResult result = query.execute();

			final NodeIterator nodes = result.getNodes();
			while (nodes.hasNext()) {
				Node contentNode = (Node) nodes.next();
				try {
					EnterpriseContent content = contentMappingUtils.convertJCRNode(contentNode);
					if (content != null) {
						fooList.add(content);
					}
				} catch (Exception e) {
					log.info("Error getting content type", e);
				}

			}

		} catch (RepositoryException e) {
			throw new ContentRepositoryNotAvailableException();
		} finally {
			session.logout();
		}

		return fooList;

	}

	public List<EnterpriseContent> searchContent(int pageSize, int pageNumber, String searchTerm,
												 boolean enableSystemContent) throws ContentRepositoryNotAvailableException {
		Session session = createSession();

		if (pageNumber == 0) {
			pageNumber = 1;
		}
		if (pageSize == 0) {
			pageSize = 5;
		}
		List<EnterpriseContent> fooList = new ArrayList<>();

		// Obtain the query manager for the session via the workspace ...
		QueryManager queryManager;
		try {
			queryManager = session.getWorkspace().getQueryManager();

			String expression = "" + "select * " + "from " + "[" + JcrConstants.NT_BASE + "] as b " + "where " + "["
					+ CmsConstants.RADIEN_SYSTEM + "] = '" + enableSystemContent + "' " + "and (" + "["
					+ CmsConstants.RADIEN_CONTENT_TYPE + "] = '" + ContentType.HTML.key() + "' " + " or ["
					+ CmsConstants.RADIEN_CONTENT_TYPE + "] = '" + ContentType.NEWS_FEED.key() + "' " + " or ["
					+ CmsConstants.RADIEN_CONTENT_TYPE + "] = '" + ContentType.NOTIFICATION.key() + "' " + ") ";

			Query query = queryManager.createQuery(expression, Query.JCR_SQL2);
			// TODO: IMPLEMENT PAGINATING
			// query.setLimit(pageSize);
			// query.setOffset((pageNumber - 1) * pageSize);

			QueryResult result = query.execute();

			final NodeIterator nodes = result.getNodes();
			while (nodes.hasNext()) {
				Node contentNode = (Node) nodes.next();
				try {
					EnterpriseContent content = contentMappingUtils.convertJCRNode(contentNode);
					if (content != null) {
						fooList.add(content);
					}
				} catch (Exception e) {
					log.info("Error searching by content", e);
				}

			}

		} catch (RepositoryException e) {
			throw new ContentRepositoryNotAvailableException();
		} finally {
			session.logout();
		}

		return fooList;
	}

	public String getRootNodePath() throws ContentRepositoryNotAvailableException {
		Session session = createSession();

		try {
			return session.getRootNode().getPath();
		} catch (RepositoryException e) {
			throw new ContentRepositoryNotAvailableException();
		} finally {
			session.logout();
		}
	}

	protected Node getRootNode() throws ContentRepositoryNotAvailableException {
		Session session = createSession();

		try {
			return session.getRootNode();
		} catch (RepositoryException e) {
			throw new ContentRepositoryNotAvailableException();
		} finally {
			session.logout();
		}
	}

	private Node getNode(Session session, String nodeType, boolean findLanguage, String language) throws RepositoryException {
		Node resultNode = session.getRootNode().getNode(getOAF().getProperty(OAFProperties.SYSTEM_CMS_CFG_NODE_ROOT));
		if (resultNode != null) {
			resultNode = resultNode.getNode(getOAF().getProperty(OAFProperties.valueOfKey(nodeType)));
		}
		if (findLanguage && resultNode != null) {
			resultNode = resultNode.getNode(language);
		}
		return resultNode;
	}

	protected Node getNode(String nodeId) throws ContentRepositoryNotAvailableException {
		try {
			return getRootNode().getNode(nodeId);
		} catch (RepositoryException e) {
			log.error("Error getting node", e);
		}
		return null;
	}

	protected Node getHTMLContentNode()
			throws PathNotFoundException, ContentRepositoryNotAvailableException, RepositoryException {
		return getNode(getOAF().getProperty(OAFProperties.SYSTEM_CMS_CFG_NODE_ROOT))
				.getNode(getOAF().getProperty(OAFProperties.SYSTEM_CMS_CFG_NODE_HTML));
	}

	protected Node getNewsFeedContentNode()
			throws PathNotFoundException, ContentRepositoryNotAvailableException, RepositoryException {
		return getNode(getOAF().getProperty(OAFProperties.SYSTEM_CMS_CFG_NODE_ROOT))
				.getNode(getOAF().getProperty(OAFProperties.SYSTEM_CMS_CFG_NODE_NEWS_FEED));
	}

	protected Node getImageContentNode()
			throws PathNotFoundException, ContentRepositoryNotAvailableException, RepositoryException {
		return getNode(getOAF().getProperty(OAFProperties.SYSTEM_CMS_CFG_NODE_ROOT))
				.getNode(getOAF().getProperty(OAFProperties.SYSTEM_CMS_CFG_NODE_IMAGE));
	}

	protected Node getDocumentsContentNode()
			throws PathNotFoundException, ContentRepositoryNotAvailableException, RepositoryException {
		return getNode(getOAF().getProperty(OAFProperties.SYSTEM_CMS_CFG_NODE_ROOT))
				.getNode(getOAF().getProperty(OAFProperties.SYSTEM_CMS_CFG_NODE_DOCS));
	}

	protected Node getNotificationContentNode()
			throws PathNotFoundException, ContentRepositoryNotAvailableException, RepositoryException {
		return getNode(getOAF().getProperty(OAFProperties.SYSTEM_CMS_CFG_NODE_ROOT))
				.getNode(getOAF().getProperty(OAFProperties.SYSTEM_CMS_CFG_NODE_NOTIFICATION));
	}

	public TreeNode getDocumentsTreeModel() {
		TreeNode root = null;
		try {
			String rootName = getDocumentsContentNode().getName();

			EnterpriseContent enterpriseContent = null;

			enterpriseContent = contentMappingUtils.create(rootName, rootName, ContentType.FOLDER);

			root = new RestTreeNode(enterpriseContent, null);
			for (Node node : JcrUtils.getChildNodes(getDocumentsContentNode())) {
				loopNodes(root, node);
			}

		} catch (ContentRepositoryNotAvailableException | RepositoryException e) {
			log.error("Error getting document tree model", e);
		}

		return root;
	}

	private void loopNodes(TreeNode documentParent, Node node) throws RepositoryException {
		// Skip the virtual (and large!) jcr:system subtree && jcr:content child
		// (so binaries are only loaded on demand)
		if (node.getName().equals(JcrConstants.JCR_SYSTEM) || node.getName().equals(JcrConstants.JCR_CONTENT)) {
			return;
		}

		EnterpriseContent doc = contentMappingUtils.convertJCRNode(node);
		TreeNode treeNode = null;
		switch (doc.getContentType()) {
			case DOCUMENT:
				treeNode = new RestTreeNode(doc.getContentType().key(), doc, documentParent);
				break;

			default:
				treeNode = new RestTreeNode(doc, documentParent);
				break;
		}

		NodeIterator nodes = node.getNodes();
		while (nodes.hasNext()) {
			loopNodes(treeNode, nodes.nextNode());
		}
	}

	public Collection<EnterpriseContent> getChildren(String viewId)
			throws ContentRepositoryNotAvailableException, ElementNotFoundException {

		Session session = createSession();
		List<Node> nodes = getNodeByViewIdLanguage(session, viewId, true, null);

		if (nodes.isEmpty()) {
			throw new ElementNotFoundException("Element [" + viewId + "] not found");
		}

		Node parent = nodes.get(0);
		List<EnterpriseContent> results = new ArrayList<>();

		try {
			for (Node node : JcrUtils.getChildNodes(parent)) {
				loopNodes(results, node, -1, -1);
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

	private void loopNodes(List<EnterpriseContent> results, Node node, int cLevel, int nLevels) throws RepositoryException {
		if (node.getName().equals(JcrConstants.JCR_SYSTEM)) {
			return;
		}
		if (!node.getName().equalsIgnoreCase(JcrConstants.JCR_CONTENT)) {
			EnterpriseContent doc = contentMappingUtils.convertJCRNode(node);
			results.add(doc);
		}
		NodeIterator nodes = node.getNodes();
		if(nLevels == -1 || cLevel < nLevels) {
			while (nodes.hasNext()) {
				loopNodes(results, nodes.nextNode(), cLevel + 1, nLevels);
			}
		}
	}

	private Session createSession() throws ContentRepositoryNotAvailableException {
		boolean error = false;
		try {
			return repository.login(getAdminCredentials());
		} catch (Exception e) {
			log.error("Error creating new JCR session", e);
			error = true;
			throw new ContentRepositoryNotAvailableException();
		} finally {
			if (!error) {
				initCount++;
				log.info("{} |ACTION: -createJCRSession | INIT COUNT: {}", this, initCount);
			} else {
				log.error("{} | ACTION: -createJCRSession FAILED!", this.getClass());
			}
		}
	}

	private static SimpleCredentials getAdminCredentials() {
		return new SimpleCredentials(CmsConstants.USER_ADMIN, "admin".toCharArray());
	}

	@Override
	public OAFAccess getOAF() {
		return oaf;
	}
	
	public String getOrCreateDocumentsPath(String path) throws ContentRepositoryNotAvailableException, RepositoryException {
	    Session session = createSession();
	    try {
	        String root = getOAF().getProperty(OAFProperties.SYSTEM_CMS_CFG_NODE_ROOT);
	        Node docsNode = getNode(session, CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_DOCS, false, null);
	        if (docsNode == null) {
	            throw new RepositoryException(String.format("%s not found", CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_DOCS));
	        }
	        JcrUtils.getOrCreateByPath(String.format("/%s/%s%s", root, docsNode.getName(), path), JcrConstants.NT_FOLDER, session);
	        session.save();
	    } catch (Exception e) {
	        log.error("ERROR " , e);
	    }finally {
	        session.logout();
	    }
	    return path;
	}

    public List<EnterpriseContent> getFolderContents(String path) throws ContentRepositoryNotAvailableException, RepositoryException {
        Session session = createSession();
        List<EnterpriseContent> results = new ArrayList<>();
        try {
			Node resultNode = session.getNode(path);
            loopNodes(results, resultNode, 0, 1);
        } finally {
            session.logout();
        }
        return results;
    }
}
