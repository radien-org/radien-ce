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

import io.radien.api.service.ecm.exception.ElementNotFoundException;
import io.radien.api.service.ecm.model.*;
import io.radien.ms.ecm.config.ConfigHandler;
import io.radien.ms.ecm.constants.CmsConstants;
import io.radien.ms.ecm.constants.CmsProperties;
import io.radien.ms.ecm.datalayer.JCRRepository;
import io.radien.ms.ecm.domain.ContentDataProvider;
import io.radien.ms.ecm.util.ContentMappingUtils;
import javax.inject.Singleton;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.util.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.jcr.*;
import javax.jcr.nodetype.NodeType;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.version.VersionManager;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Main implementation of the
 *
 * @author Marco Weiland
 */
@Singleton
public class ContentRepository extends JCRRepository {

	private static final Logger log = LoggerFactory.getLogger(ContentRepository.class);
	private static final long serialVersionUID = 3705349362214763287L;

	private static final String FILE_SEPARATOR = "/";

	private static final String JCR_OR = " or [";
	private static final String JCR_AND = " and [";
	public static final String JCR_END_OBJ = "] = '";
	public static final String IS_TRUE = "] = true ";

	@Inject
	private ConfigHandler configHandler;
	@Inject
	private ContentMappingUtils contentMappingUtils;
	@Inject
	private ContentDataProvider dataProvider;

	public void save(String client, EnterpriseContent obj) throws RepositoryException {
		Session session = createSession();
		String viewId = obj.getViewId();
		String language = obj.getLanguage();
		Node content = null;
		boolean isMoveCommand = false;
		String newPath = null;
		String nameEscaped = Text.escapeIllegalJcrChars(obj.getName());

		if (StringUtils.isBlank(viewId)) {
			//TODO: REVIEW - Sets default properties
			contentMappingUtils.decorateNewContent(obj);
		} else {
			try {
				List<Node> nodeList = getNodeByViewIdLanguage(session, viewId, false, language);
				if(nodeList.isEmpty() && obj.getContentType() == ContentType.FOLDER) {
					nodeList = getNodeByViewIdLanguage(session, viewId, false, null);
				}
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
			} catch (RepositoryException e) {
				log.error("Error accessing the JCR repository while trying to find content", e);
			}
		}
		content = getContentIfPathExists(session, obj, content);
		Node parent = getParentIfParentPathExists(session, obj);

		process(session, client, obj, language, content, parent, isMoveCommand, nameEscaped, newPath);
	}

	private void process(Session session, String client, EnterpriseContent obj, String language, Node content,
						 Node parent, boolean isMoveCommand, String nameEscaped, String newPath) throws RepositoryException {
		try {
			boolean isVersionable = obj instanceof SystemVersionableEnterpriseContent && ((SystemVersionableEnterpriseContent)obj).isVersionable();
			if (content == null) {
				switch (obj.getContentType()) {
					case DOCUMENT:
						content = addNodeToParent(session, client, language, parent, nameEscaped, CmsProperties.SYSTEM_CMS_CFG_NODE_DOCS, false);
						break;
					case HTML:
						content = addNodeToParent(session, client, language, parent, nameEscaped, CmsProperties.SYSTEM_CMS_CFG_NODE_HTML, true);
						break;
					case IMAGE:
						content = addNodeToParent(session, client, language, parent, nameEscaped, CmsProperties.SYSTEM_CMS_CFG_NODE_IMAGE, false);
						break;
					case FOLDER:
						content = prepareFolderNode(session, client, language, parent, nameEscaped);
						break;
					case NOTIFICATION:
						content = addNodeToParent(session, client, language, parent, nameEscaped, CmsProperties.SYSTEM_CMS_CFG_NODE_NOTIFICATION, true);
						break;
					case TAG:
						content = prepareTagNode(session, client, language, parent, nameEscaped);
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
				Node movedNode = session.getNode(newPath);
				obj.setParentPath(movedNode.getParent().getPath());
				obj.setJcrPath(movedNode.getPath());
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

	private Node addNodeToParent(Session session, String client, String language, Node parent, String viewID, CmsProperties systemCmsCfgNodeDocs,
								 boolean b) throws RepositoryException {
		Node content = null;
		if(parent == null) {
			parent = getNode(session, client, systemCmsCfgNodeDocs, b, language);
		}
		if(parent != null) {
			content = parent.addNode(viewID, CmsConstants.RADIEN_BASE_NODE_TYPE);
		}
		return content;
	}

	private Node prepareTagNode(Session session, String client, String language, Node parent, String nameEscaped) throws RepositoryException {
		Node content = null;
		if(parent == null) {
			parent = getNode(session, client, CmsProperties.SYSTEM_CMS_CFG_NODE_TAG, false, language);
		}
		if(parent != null) {
			content = parent.addNode(nameEscaped, JcrConstants.NT_FILE);
		}
		return content;
	}

	private Node prepareFolderNode(Session session, String client, String language, Node parent, String nameEscaped) throws RepositoryException {
		Node content = null;
		if(parent == null) {
			parent = getNode(session, client, CmsProperties.SYSTEM_CMS_CFG_NODE_DOCS, false, language);
		}
		if(parent != null) {
			content = parent.addNode(nameEscaped, JcrConstants.NT_FOLDER);
		}
		addSupportedLocalesFolder(client, parent, nameEscaped);
		return content;
	}

	public EnterpriseContent loadFile(String jcrPath) throws RepositoryException, IOException {
		Session session = createSession();
		try {
			EnterpriseContent content = getContentByPath(jcrPath, session);
			content.setFile(contentMappingUtils.getFile(session, jcrPath));
			return content;
		} finally {
			session.logout();
		}
	}

	private EnterpriseContent getContentByPath(String jcrPath, Session session) throws RepositoryException {
		Node result = JcrUtils.getNodeIfExists(jcrPath, session);
		return contentMappingUtils.convertJCRNode(result);
	}

	public void delete(String path) throws RepositoryException {
		Session session = createSession();
		try {
			session.removeItem(path);
			session.save();
		} finally {
			session.logout();
		}
	}

	public Optional<List<EnterpriseContent>> getByViewIdLanguage(String viewId, boolean activeOnly, String language)
			throws RepositoryException {
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
			return Optional.of(ecList);
		} finally {
			session.logout();
		}
	}

	private List<Node> getNodeByViewIdLanguage(Session session, String viewId, boolean activeOnly, String language)
			throws RepositoryException {

		List<Node> results = new ArrayList<>();
		QueryManager queryManager;
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
		return results;
	}

	public void registerCNDNodeTypes(String cndFileName) {
		Session session = createSession();
		NodeType[] nodeTypes;
		try(InputStreamReader streamReader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(cndFileName))) {
			nodeTypes = CndImporter.registerNodeTypes(streamReader, session);
			for (NodeType nt : nodeTypes) {
				log.info("Registered: {}", nt.getName());
			}
		} catch (Exception e) {
			log.error("Error registering node type", e);
		} finally {
			session.logout();
		}
	}

	public void updateFolderSupportedLanguages(String client, String nameEscaped) throws RepositoryException {
		Session session = createSession();

		try {
			Node rootNode = session.getRootNode().getNode("radien");
			addSupportedLocalesFolder(client, rootNode, nameEscaped);
			session.save();
		} finally {
			session.logout();
		}
	}

	private void addSupportedLocalesFolder(String client, Node parent, String nameEscaped) throws RepositoryException {
		if (nameEscaped.equals(configHandler.getHtmlNode(client)) ||
				nameEscaped.equals(configHandler.getNotificationNode(client))) {

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


	public String getRootNodePath() throws RepositoryException{
		Session session = createSession();
		try {
			return session.getRootNode().getPath();
		} finally {
			session.logout();
		}
	}

	private Node getNode(Session session, String client, CmsProperties nodeType, boolean findLanguage, String language) throws RepositoryException {
		Node resultNode = session.getRootNode().getNode(configHandler.getRootNode(client));
		if (resultNode != null) {
			resultNode = resultNode.getNode(configHandler.getProperty(nodeType, Optional.ofNullable(client)));
		}
		if (findLanguage && resultNode != null) {
			resultNode = resultNode.getNode(language);
		}
		return resultNode;
	}

	public Collection<EnterpriseContent> getChildren(String viewId) throws ElementNotFoundException, RepositoryException {

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
		} finally {
			session.logout();
		}

		return results;
	}

	public List<EnterpriseContent> getFolderContents(String path) throws RepositoryException {
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

	public List<EnterpriseContent> getContentVersions(String path) throws RepositoryException {
		List<EnterpriseContent> results = new ArrayList<>();
		Session session = createSession();
		try {
			VersionManager versionManager = session.getWorkspace().getVersionManager();
			VersionHistory history = versionManager.getVersionHistory(path);
			VersionIterator iterator = history.getAllVersions();
			while(iterator.hasNext()) {
				Version version = iterator.nextVersion();
				getNodeContentVersions(results, version.getFrozenNode());
			}
			results.forEach(result -> result.setParentPath(path));
		} finally {
			session.logout();
		}
		return results;
	}

	/**
	 * Deletes a single version from the given absolute path.
	 * If the given version is the current baseline version (most recent) then the previous version is restored as baseline.
	 * VersionIterator always returns the versions in order.
	 * @param path absolute path to the node
	 * @param version version to be deleted
	 * @throws RepositoryException when deletion process fails
	 */
	public int deleteVersion(String path, SystemContentVersion version) throws RepositoryException {
		Session session = createSession();
		int affectedRecords = 0;
		try {
			VersionManager versionManager = session.getWorkspace().getVersionManager();
			VersionHistory history = versionManager.getVersionHistory(path);
			VersionIterator iterator = history.getAllVersions();
			Version previousVersion = null;
			while(iterator.hasNext()) {
				Version nodeVersion = iterator.nextVersion();
				Node frozenNode = nodeVersion.getFrozenNode();
				if(frozenNode.hasProperty(CmsConstants.RADIEN_VERSION)) {
					String versionString = frozenNode.getProperty(CmsConstants.RADIEN_VERSION).getString();
					if(versionString.equalsIgnoreCase(version.getVersion())) {
						if(versionManager.getBaseVersion(path).getName().equalsIgnoreCase(nodeVersion.getName())) {
							versionManager.restore(previousVersion, false);
						}
						history.removeVersion(nodeVersion.getName());
						affectedRecords++;
						log.info("Version {} deleted from {}", version.getVersion(), path);
						return affectedRecords;
					} else {
						previousVersion = nodeVersion;
					}
				}
			}
		} finally {
			session.logout();
		}
		return affectedRecords;
	}

	public String getOrCreateDocumentsPath(String client, String path) throws RepositoryException {
		Session session = createSession();
		try {
			String root = configHandler.getRootNode(client);
			Node docsNode = getNode(session, client, CmsProperties.SYSTEM_CMS_CFG_NODE_DOCS, false, null);
			if (docsNode == null) {
				throw new RepositoryException(String.format("%s not found", CmsProperties.SYSTEM_CMS_CFG_NODE_DOCS.propKey()));
			}
			generateFolderStructure(client, String.format("/%s/%s%s", root, docsNode.getName(), path), session);
			session.save();
		} finally {
			session.logout();
		}
		return path;
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

	private void getNodeContentVersions(List<EnterpriseContent> results, Node versionNode) throws RepositoryException {
		if(versionNode.getName().equals(JcrConstants.JCR_SYSTEM)) {
			return;
		}
		if(!versionNode.getName().equalsIgnoreCase(JcrConstants.JCR_CONTENT)) {
			EnterpriseContent doc = contentMappingUtils.convertJCRNode(versionNode);
			if(doc != null) {
				results.add(doc);
			}
		}

		NodeIterator nodes = versionNode.getNodes();
		while(nodes.hasNext()) {
			getNodeContentVersions(results, nodes.nextNode());
		}
	}

	private String generateFolderStructure(String client, String path, Session session) throws RepositoryException {
		String parent = path.substring(0, 1);
		String[] folderNames = path.substring(1).split("/");
		for(String folderName : folderNames) {
			Node exists = null;
			if(parent.equals("/")) {
				exists = JcrUtils.getNodeIfExists(String.format("%s%s", parent, folderName), session);
			} else {
				exists = JcrUtils.getNodeIfExists(String.format("%s/%s", parent, folderName), session);
			}
			if(exists == null) {
				Folder folder = new Folder(folderName);
				folder.setParentPath(parent);
				folder.setViewId(String.format("%s_%s", folder.getName(), UUID.randomUUID()));
				save(client, folder);
				parent = folder.getJcrPath();
			} else {
				parent = exists.getPath();
			}

		}
		return path;
	}

}
