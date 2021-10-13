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
import io.radien.api.service.ecm.model.ContentType;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.ecm.model.RestTreeNode;
import io.radien.ms.ecm.constants.CmsConstants;
import io.radien.ms.ecm.domain.ContentDataProvider;
import io.radien.ms.ecm.factory.ContentFactory;
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
	public static final String IS_TRUE = "] = 'true' ";

	@Inject
	private OAFAccess oaf;
	@Inject
	private Config properties;
	@Inject
	private ContentFactory contentFactory;
	@Inject
	private ContentDataProvider dataProvider;
	@Inject
	private Repository repository;

	@PostConstruct
	private void init() {
	}

	public void save(EnterpriseContent obj) throws ContentRepositoryNotAvailableException {
		Session session = createSession();
		String viewId = obj.getViewId();
		String language = obj.getLanguage();
		Node content = null;
		boolean isMoveCommand = false;
		String newPath = null;
		String nameEscaped = Text.escapeIllegalJcrChars(obj.getName());

//		boolean isVersionable = obj instanceof GenericMandatoryVersionableEnterpriseContent && ((GenericMandatoryVersionableEnterpriseContent) obj).isVersionable();
//		if(isVersionable) {
//			viewIdEscaped = Text.escapeIllegalJcrChars(obj.getViewId());
//		}

		if (StringUtils.isBlank(viewId) || StringUtils.isBlank(language)) {
			contentFactory.decorateNewContent(obj);
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
						 Node parent, boolean isMoveCommand, String nameEscaped, String newPath) {
		try {
//			boolean isVersionable = false;
//			if(obj instanceof GenericMandatoryVersionableEnterpriseContent && ((GenericMandatoryVersionableEnterpriseContent) obj).isVersionable()) {
//				isVersionable = true;
//			}

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
				content.addMixin(CmsConstants.RADIEN_MIXIN_NODE_PROPS);

//				if(isVersionable) {
//					content.addMixin(CmsConstants.OAF_MIXIN_VERSIONABLE);
//					content.setProperty(CmsConstants.OAF_VERSION_COMMENT, ((GenericMandatoryVersionableEnterpriseContent) obj).getVersionComment());
//				}
				contentFactory.syncNode(content, obj, session);
				session.save();

//				if(isVersionable) {
//					//Create Date Mixin
//					content.addMixin(CmsConstants.OAF_MIXIN_VERSIONABLE_CREATE);
//					Date createdDate = obj.getCreateDate();
//					if (createdDate == null) {
//						createdDate = new Date();
//					}
//					Calendar calendar = Calendar.getInstance();
//					calendar.setTime(createdDate);
//					content.setProperty(CmsConstants.OAF_CREATED, calendar);
//
//					content.addMixin(CmsConstants.OAF_MIXIN_MANDATORY_CONTENT);
//					content.setProperty(CmsConstants.OAF_MANDATORY_APPROVAL, ((GenericMandatoryVersionableEnterpriseContent)obj).isMandatoryApprove());
//					content.setProperty(CmsConstants.OAF_MANDATORY_VIEW, ((GenericMandatoryVersionableEnterpriseContent)obj).isMandatoryView());
//
//					//Version Mixin
//					if(((VersionableEnterpriseContent)obj).getVersion() != null && !((VersionableEnterpriseContent)obj).getVersion().isEmpty()) {
//						content.addMixin(CmsConstants.OAF_MIXIN_VERSION);
//						SystemContentVersion version = new ContentVersion(((GenericMandatoryVersionableEnterpriseContent) obj).getVersion());
//						content.setProperty(CmsConstants.OAF_VERSION, version.getVersion());
//					}
//					session.save();
//
//					VersionManager versionManager = session.getWorkspace().getVersionManager();
//					versionManager.checkin(content.getPath());
//				}
				if (isMoveCommand) {
//					if(isVersionable) {
//						VersionManager versionManager = session.getWorkspace().getVersionManager();
//						versionManager.checkout(content.getPath());
//					}
					session.move(content.getPath(), newPath);
				}
				session.save();
//				if(isVersionable && content.isCheckedOut()) {
//					VersionManager versionManager = session.getWorkspace().getVersionManager();
//					versionManager.checkin(content.getPath());
//				}
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

	private Node getContentIfPathExists(Session session, EnterpriseContent obj, Node content) {
		try {
			if (content == null && StringUtils.isNotBlank(obj.getJcrPath())) {
				content = session.getNode(obj.getJcrPath());
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
				parent = session.getNode(obj.getParentPath());
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
			content = parent.addNode(viewID, JcrConstants.NT_FILE);
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
		content = addSupportedLocalesFolder(content, parent, nameEscaped);
		return content;
	}

	public EnterpriseContent loadFile(String jcrPath)
			throws ContentRepositoryNotAvailableException {
		EnterpriseContent content = null;
			Session session = createSession();
		try {
			content = getContentByPath(jcrPath, session);
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

	private EnterpriseContent getContentByPath(String jcrPath, Session session) throws RepositoryException {
		Node result = JcrUtils.getNodeIfExists(jcrPath, session);
		return contentFactory.convertJCRNode(result);
	}

	public void delete(EnterpriseContent obj) throws ContentRepositoryNotAvailableException {
		Session session = createSession();
		try {

			session.removeItem(obj.getJcrPath());

			session.save();

		} catch (RepositoryException e) {
			log.error("Error deleting EnterpriseContent file", e);
		} finally {
			session.logout();
		}
	}

	private Node getNodeByViewId(String viewId, boolean activeOnly)
			throws ContentRepositoryNotAvailableException, ElementNotFoundException {
		Session session = createSession();
		List<Node> results = new ArrayList<>();

		// Obtain the query manager for the session via the workspace ...
		QueryManager queryManager;
		try {
			queryManager = session.getWorkspace().getQueryManager();

			String expression = "" + "select * from " + "[" + CmsConstants.RADIEN_MIXIN_NODE_PROPS + "] as node " + "where ["
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
			return contentFactory.convertJCRNode(node);
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
				ecList.add(contentFactory.convertJCRNode(node));
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

		// Obtain the query manager for the session via the workspace ...
		QueryManager queryManager;
		try {
			queryManager = session.getWorkspace().getQueryManager();

			String expression = "select * from " + "[" + CmsConstants.RADIEN_MIXIN_NODE_PROPS + "] as node " + "where ["
					+ CmsConstants.RADIEN_VIEW_ID + JCR_END_OBJ + viewId + "' ";

			if (StringUtils.isNotBlank(language)) {
				expression += JCR_AND + CmsConstants.RADIEN_CONTENT_LANG + JCR_END_OBJ + language + "' ";
			}

			if (activeOnly) {
				expression += JCR_AND + CmsConstants.RADIEN_ACTIVE + IS_TRUE;
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

	private Node addSupportedLocalesFolder(Node content, Node parent, String nameEscaped) throws RepositoryException {
		if (nameEscaped.equals(properties.getValue(CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_HTML, String.class)) ||
				nameEscaped.equals(properties.getValue(CmsConstants.PropertyKeys.SYSTEM_CMS_CFG_NODE_NOTIFICATION, String.class))) {

			for (String lang : dataProvider.getSupportedLanguages()) {
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
					EnterpriseContent content = contentFactory.convertJCRNode(contentNode);
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
					EnterpriseContent content = contentFactory.convertJCRNode(contentNode);
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

	private Node getNode(Session session, String nodeType, boolean containsImage, String language) throws RepositoryException {
		Node resultNode = session.getRootNode().getNode(getOAF().getProperty(OAFProperties.SYSTEM_CMS_CFG_NODE_ROOT));
		if (resultNode != null) {
			resultNode = resultNode.getNode(getOAF().getProperty(OAFProperties.valueOfKey(nodeType)));
		}
		if (containsImage && resultNode != null) {
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

			enterpriseContent = contentFactory.create(rootName, rootName, ContentType.FOLDER);

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

		EnterpriseContent doc = contentFactory.convertJCRNode(node);
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
			throw new ElementNotFoundException("Element [" + viewId + "] and [ ] not found");
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

	private Session createSession() throws ContentRepositoryNotAvailableException {
		boolean error = false;
		try {
			Session adminSession = repository.login(getAdminCredentials());
			adminSession.setNamespacePrefix("rd", "http://www.jcp.org/jcr/rd/1.0");
			return adminSession;
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

}
