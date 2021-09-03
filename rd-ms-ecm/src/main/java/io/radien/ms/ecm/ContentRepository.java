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
import io.radien.ms.ecm.factory.ContentFactory;
import io.radien.ms.ecm.util.OafConstants;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.util.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Main implementation of the
 *
 * @author Marco Weiland
 */
public @RequestScoped @Default class ContentRepository implements Serializable, Appframeable {

	private static final Logger log = LoggerFactory.getLogger(ContentRepository.class);
	private static final long serialVersionUID = 1L;
	@Inject
	private OAFAccess oaf;
	@Inject
	private ContentFactory contentFactory;
	@Inject
	private Session session;

	@PostConstruct
	private void init() {

	}

	public void save(EnterpriseContent obj) {
		String viewId = obj.getViewId();
		Node content = null;
		Node parent = null;
		boolean isMoveCommand = false;
		String nameEscaped = Text.escapeIllegalJcrChars(obj.getName());

		if (viewId == null || viewId.equalsIgnoreCase("")) {
			contentFactory.decorateNewContent(obj);
		} else {
			try {
				content = getNodeByViewId(viewId, false);
				if (obj.getParentPath() == null) {
					obj.setParentPath(content.getParent().getPath());
				}
				String newPath = obj.getParentPath() + "/" + nameEscaped;
				if (!newPath.equals(content.getPath())) {
					isMoveCommand = true;
				}
			} catch (Exception e) {
				// log.info("Error saving content", e);
				// TODO jsr: refactor all this mess
			}
		}

		try {
			if (content == null) {
				if (obj.getJcrPath() != null && !obj.getJcrPath().equalsIgnoreCase("")) {
					content = session.getNode(obj.getJcrPath());
				}
			}

		} catch (RepositoryException e) {
			log.info("Error saving the content", e);
		}

		try {
			if (obj.getParentPath() != null && !obj.getParentPath().equalsIgnoreCase("")) {
				parent = session.getNode(obj.getParentPath());
			}
		} catch (RepositoryException e) {
			log.error("ERror saving content", e);
		}

		try {
			if (content == null) {
				switch (obj.getContentType()) {
					case DOCUMENT:
						if (parent == null) {
							parent = getDocumentsContentNode();
						}
						content = parent.addNode(nameEscaped, JcrConstants.NT_FILE);
						break;
					case FOLDER:
						if (parent == null) {
							parent = getDocumentsContentNode();
						}
						content = parent.addNode(nameEscaped, JcrConstants.NT_FOLDER);
						break;
					case HTML:
						if (parent == null) {
							parent = getHTMLContentNode();
						}
						content = parent.addNode(nameEscaped, JcrConstants.NT_FILE);
						break;
					case NEWS_FEED:
						if (parent == null) {
							parent = getNewsFeedContentNode();
						}
						content = parent.addNode(nameEscaped, JcrConstants.NT_FILE);
						break;
					case IMAGE:
						if (parent == null) {
							parent = getImageContentNode();
						}
						content = parent.addNode(nameEscaped, JcrConstants.NT_FILE);
						break;
					case NOTIFICATION:
						if (parent == null) {
							parent = getNotificationContentNode();
						}
						content = parent.addNode(nameEscaped, JcrConstants.NT_FILE);
						break;
				}

				content.addMixin(OafConstants.OAF_MIXIN_NODE_PROPS);

			}

			contentFactory.syncNode(content, obj, session);

			if (isMoveCommand) {
				session.move(content.getPath(), obj.getParentPath() + "/" + nameEscaped);
			}

			session.save();
		} catch (ContentRepositoryNotAvailableException | RepositoryException e) {
			log.error("Error saving content", e);
		}

	}

	public EnterpriseContent loadFile(EnterpriseContent content)
			throws ElementNotFoundException, ContentRepositoryNotAvailableException {
		try {
			content.setFile(contentFactory.getFile(session, content.getJcrPath()));
		} catch (PathNotFoundException e) {
			log.error("Error lading EnterPriseOCntent File", e);
			throw new ElementNotFoundException(e.getMessage());
		} catch (RepositoryException | IOException e) {
			log.error("Error loading file", e);
			throw new ContentRepositoryNotAvailableException();
		}
		return content;
	}

	public void delete(EnterpriseContent obj) {
		try {

			session.removeItem(obj.getJcrPath());

			session.save();

		} catch (RepositoryException e) {
			log.error("Error deleting EnterpriseContent file", e);
		}
	}

	private Node getNodeByViewId(String viewId, boolean activeOnly)
			throws ContentRepositoryNotAvailableException, ElementNotFoundException {

		List<Node> results = new ArrayList<>();

		// Obtain the query manager for the session via the workspace ...
		QueryManager queryManager;
		try {
			queryManager = session.getWorkspace().getQueryManager();

			String expression = "" + "select * from " + "[" + JcrConstants.NT_BASE + "] as node " + "where ["
					+ OafConstants.OAF_VIEW_ID + "] = '" + viewId + "' ";
			if (activeOnly) {
				expression += " and [" + OafConstants.OAF_ACTIVE + "] = '" + activeOnly + "' ";
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

	public void registerCNDNodeTypes(String cndFileName) {
		NodeType[] nodeTypes;
		try {
			nodeTypes = CndImporter.registerNodeTypes(
					new InputStreamReader(getClass().getClassLoader().getResourceAsStream(cndFileName)), session);
			for (NodeType nt : nodeTypes) {
				log.info("Registered: " + nt.getName());
			}
		} catch (Exception e) {
			log.error("Error registering node type", e);
		}

	}

	public List<EnterpriseContent> getByContentType(ContentType contentType, boolean activeOnly,
													boolean includeSystemContent) throws ContentRepositoryNotAvailableException {

		List<EnterpriseContent> fooList = new ArrayList<>();

		// Obtain the query manager for the session via the workspace ...
		QueryManager queryManager;
		try {
			queryManager = session.getWorkspace().getQueryManager();

			String expression = "" + "select * " + "from " + "[" + JcrConstants.NT_BASE + "] as b " + "where " + "["
					+ OafConstants.OAF_CONTENT_TYPE + "] = '" + contentType.key() + "' " + "and ["
					+ OafConstants.OAF_SYSTEM + "] = '" + includeSystemContent + "' ";
			if (activeOnly) {
				expression += "and [" + OafConstants.OAF_ACTIVE + "] = '" + activeOnly + "' ";
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
		}

		return fooList;

	}

	public List<EnterpriseContent> searchContent(int pageSize, int pageNumber, String searchTerm,
												 boolean enableSystemContent) throws ContentRepositoryNotAvailableException {
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
					+ OafConstants.OAF_SYSTEM + "] = '" + enableSystemContent + "' " + "and (" + "["
					+ OafConstants.OAF_CONTENT_TYPE + "] = '" + ContentType.HTML.key() + "' " + " or ["
					+ OafConstants.OAF_CONTENT_TYPE + "] = '" + ContentType.NEWS_FEED.key() + "' " + " or ["
					+ OafConstants.OAF_CONTENT_TYPE + "] = '" + ContentType.NOTIFICATION.key() + "' " + ") ";

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
		}

		return fooList;
	}

	protected Node getRootNode() throws ContentRepositoryNotAvailableException {
		try {
			return session.getRootNode();
		} catch (RepositoryException e) {
			throw new ContentRepositoryNotAvailableException();
		}
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

	public Collection<? extends EnterpriseContent> getChildren(String viewId)
			throws ContentRepositoryNotAvailableException, ElementNotFoundException {

		Node parent = getNodeByViewId(viewId, true);

		List<EnterpriseContent> results = new ArrayList<>();

		try {
			for (Node node : JcrUtils.getChildNodes(parent)) {
				try {

					loopNodes(results, node);

				} catch (RepositoryException e) {
					log.error("Error getting content children", e);
				}
			}
		} catch (PathNotFoundException e) {
			throw new ElementNotFoundException(e.getMessage());
		} catch (RepositoryException e) {
			throw new ContentRepositoryNotAvailableException();
		}

		return results;
	}

	private void loopNodes(List<EnterpriseContent> results, Node node) throws RepositoryException {

		if (node.getName().equals(JcrConstants.JCR_SYSTEM)) {
			return;
		}

		if (!node.getName().equalsIgnoreCase(JcrConstants.JCR_CONTENT)) {
			EnterpriseContent doc = contentFactory.convertJCRNode(node);
			results.add(doc);
		}

		NodeIterator nodes = node.getNodes();
		while (nodes.hasNext()) {
			loopNodes(results, nodes.nextNode());
		}
	}

	@Override
	public OAFAccess getOAF() {
		return oaf;
	}

}
