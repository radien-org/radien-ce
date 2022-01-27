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
package io.radien.ms.ecm.util;

import com.google.common.io.ByteStreams;
import io.radien.api.service.ecm.exception.NameNotValidException;
import io.radien.api.service.ecm.model.*;
import io.radien.ms.ecm.client.factory.ContentFactory;
import io.radien.ms.ecm.constants.CmsConstants;
import java.net.URLConnection;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.util.Text;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.util.*;

/**
 * Class responsible for creating generic content that can be manipulated in the cms
 *
 * @author Marco Weiland
 * @author jrodrigues
 */
public @RequestScoped class ContentMappingUtils implements Serializable {

	private static final Logger log = LoggerFactory.getLogger(ContentMappingUtils.class);
	private static final long serialVersionUID = -5005556415803181075L;

	public EnterpriseContent convertSeederJSONObject(JSONObject json) throws IOException, ParseException {
		EnterpriseContent content = ContentFactory.convertJSONObject(json);
		getImageResource(json, content);

		if(content.getFileSize() == 0 || content.getFile() == null || content.getFile().length == 0) {
			getFileResource(json, content);
		}
		return content;
	}

	private void getFileResource(JSONObject json, EnterpriseContent content) throws IOException {
		InputStream stream = null;
		try {

			String fileString = (String) json.get("file");

			if(fileString != null && !fileString.isEmpty()) {
				stream = getClass().getClassLoader().getResourceAsStream(fileString);

				if (stream != null) {
					byte[] fileArray = IOUtils.toByteArray(stream);
					log.trace("Read {} bytes from content file with path: {}", fileArray.length, fileString);
					log.trace("Content Read from file {}", fileArray);
					content.setFile(fileArray);
					String mimeType = URLConnection.getFileNameMap().getContentTypeFor(getClass().getClassLoader().getResource(fileString).toURI().toString());
					content.setMimeType(mimeType);

					content.setFileSize(fileArray.length);
				}
			}
		} catch (Exception e) {
			log.warn("Error converting json object", e);
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}

	private void getImageResource(JSONObject json, EnterpriseContent content) throws IOException {
		InputStream stream = null;
		try {
			String image = (String) json.get("image");
			if (StringUtils.isNotBlank(image)) {
				stream = getClass().getClassLoader().getResourceAsStream(image);

				if (stream != null) {
					byte[] imageArray = IOUtils.toByteArray(stream);

					log.debug("Read {} bytes from content image", imageArray.length);

					content.setImage(imageArray);
					content.setImageMimeType("image/png");
					content.setImageName(image);
				}
			}
		} catch (Exception e) {
			log.warn("Error converting json object", e);
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}

	public boolean isRadienNode(Node node) {
		try {
			return node.getPrimaryNodeType().getName().equals(CmsConstants.RADIEN_BASE_NODE_TYPE);
		} catch (RepositoryException e) {
			log.error("Error checking if it is oaf node", e);
			return false;
		}
	}

	public EnterpriseContent convertJCRNode(Node node) throws RepositoryException {
		try {
			EnterpriseContent systemContent = null;

			boolean isVersionableNode = node.hasProperty(CmsConstants.RADIEN_VERSION);
			boolean isMandatoryNode = node.hasProperty(CmsConstants.RADIEN_MANDATORY_VIEW);
			if ((isRadienNode(node) || node.hasProperty(CmsConstants.RADIEN_VIEW_ID))) {
				if(isVersionableNode && isMandatoryNode) {
					systemContent = setupMandatoryVersionableEnterpriseContent(node);
				} else if(isVersionableNode) {
					systemContent = setupVersionableEnterpriseContent(node);
				} else if(isMandatoryNode) {
					systemContent = setupMandatoryEnterpriseContent(node);
				} else {
					systemContent = new GenericEnterpriseContent(Text.unescapeIllegalJcrChars(node.getName()));
				}
				systemContent.setViewId(node.getProperty(CmsConstants.RADIEN_VIEW_ID).getString());
				systemContent.setActive(getPropertyBooleanIfPresent(node, CmsConstants.RADIEN_ACTIVE));
				systemContent.setSystem(getPropertyBooleanIfPresent(node, CmsConstants.RADIEN_SYSTEM));
				systemContent.setJcrPath(node.getPath());
				systemContent.setParentPath(node.getParent().getPath());
				if(isRadienNode(node)) {
					systemContent.setContentType(
							ContentType.getByKey(node.getProperty(CmsConstants.RADIEN_CONTENT_TYPE).getString()));
					systemContent.setLanguage(node.getProperty(CmsConstants.RADIEN_CONTENT_LANG).getString());
					systemContent.setCreateDate(node.getProperty(JcrConstants.JCR_CREATED).getDate().getTime());
					systemContent.setHtmlContent(getPropertyStringIfPresent(node, CmsConstants.RADIEN_HTML_CONTENT));

					setupImageProperties(node, systemContent);
					setupDocumentOnlyProperties(node, systemContent);
				} else if(node.getPrimaryNodeType().getName().equals(JcrConstants.NT_FOLDER)) {
					systemContent.setContentType(ContentType.FOLDER);
				}
			}
			return systemContent;
		} catch (RepositoryException | NameNotValidException e) {
			throw new RepositoryException("Error while converting JCR node!", e);
		}
	}

	private void setupImageProperties(Node node, EnterpriseContent systemContent) {
		InputStream stream = null;
		try {
			Binary bin = null;
			if (node.hasProperty(CmsConstants.RADIEN_IMAGE)) {
				bin = node.getProperty(CmsConstants.RADIEN_IMAGE).getBinary();
			}
			if (bin != null) {
				stream = bin.getStream();
				byte[] imageArray = ByteStreams.toByteArray(stream);
				systemContent.setImage(imageArray);
				systemContent.setImageMimeType(node.getProperty(CmsConstants.RADIEN_IMAGE_MIME_TYPE).getString());
				systemContent.setImageName(node.getProperty(CmsConstants.RADIEN_IMAGE_NAME).getString());
			}
		} catch (Exception e) {
			log.error("Error getting oaf:image from JCR", e);
		}
	}

	private void setupDocumentOnlyProperties(Node node, EnterpriseContent systemContent) {
		try {
			if (systemContent.getContentType() == ContentType.DOCUMENT) {
				systemContent.setFileSize(node.getProperty(CmsConstants.RADIEN_FILE_SIZE).getLong());
				systemContent.setMimeType(getPropertyStringIfPresent(node.getNode(JcrConstants.JCR_CONTENT), JcrConstants.JCR_MIMETYPE));
			}
		} catch (PathNotFoundException e) {
			log.info("Error converting JCR node", e);
		} catch (Exception e) {
			log.error("Error converting JCR node", e);
		}
	}

	private EnterpriseContent setupMandatoryEnterpriseContent(Node node) throws RepositoryException {
		MandatoryEnterpriseContent systemContent = new MandatoryEnterpriseContent();
		systemContent.setName(getPropertyStringIfPresent(node, CmsConstants.RADIEN_NAME));
		systemContent.setMandatoryApproval(node.getProperty(CmsConstants.RADIEN_MANDATORY_APPROVAL).getBoolean());
		systemContent.setMandatoryView(node.getProperty(CmsConstants.RADIEN_MANDATORY_VIEW).getBoolean());
		return systemContent;
	}

	private EnterpriseContent setupVersionableEnterpriseContent(Node node) throws RepositoryException {
		VersionableEnterpriseContent systemContent = new VersionableEnterpriseContent();
		systemContent.setName(getPropertyStringIfPresent(node, CmsConstants.RADIEN_NAME));
		systemContent.setVersion(new ContentVersion(node.getProperty(CmsConstants.RADIEN_VERSION).getString()));
		systemContent.setVersionComment(node.getProperty(CmsConstants.RADIEN_VERSION_COMMENT).getString());
		systemContent.setValidDate(node.getProperty(CmsConstants.RADIEN_VALID_DATE).getDate().getTime());
		systemContent.setVersionable(true);
		return systemContent;
	}

	private EnterpriseContent setupMandatoryVersionableEnterpriseContent(Node node) throws RepositoryException {
		MandatoryVersionableEnterpriseContent systemContent = new MandatoryVersionableEnterpriseContent();
		systemContent.setName(getPropertyStringIfPresent(node, CmsConstants.RADIEN_NAME));
		systemContent.setVersion(new ContentVersion(node.getProperty(CmsConstants.RADIEN_VERSION).getString()));
		systemContent.setVersionComment(node.getProperty(CmsConstants.RADIEN_VERSION_COMMENT).getString());
		systemContent.setValidDate(node.getProperty(CmsConstants.RADIEN_VALID_DATE).getDate().getTime());
		systemContent.setVersionable(true);

		systemContent.setMandatoryApproval(node.getProperty(CmsConstants.RADIEN_MANDATORY_APPROVAL).getBoolean());
		systemContent.setMandatoryView(node.getProperty(CmsConstants.RADIEN_MANDATORY_VIEW).getBoolean());
		return systemContent;
	}

	public byte[] getFile(Session session, String path) throws RepositoryException, IOException {
		Node fileNode = session.getNode(path);
		InputStream stream;
		if (fileNode != null) {
			stream = JcrUtils.readFile(fileNode);
			return ByteStreams.toByteArray(stream);
		}
		return StringUtils.EMPTY.getBytes();
	}

	private Node getContentNode(Node node) {
		try {
			return node.getNode(JcrConstants.JCR_CONTENT);
		} catch (Exception ignored) {
			//Swallow exception
		}
		return null;
	}

	public void syncNode(Node node, EnterpriseContent obj, Session session) throws RepositoryException {
		decorateNewContent(obj);
		Calendar now = Calendar.getInstance();

		node.setProperty(CmsConstants.RADIEN_VIEW_ID, obj.getViewId());
		node.setProperty(CmsConstants.RADIEN_ACTIVE, obj.isActive());
		node.setProperty(CmsConstants.RADIEN_SYSTEM, obj.isSystem());

		if (isRadienNode(node)) {
			node.setProperty(CmsConstants.RADIEN_CONTENT_TYPE, obj.getContentType().key());
			node.setProperty(CmsConstants.RADIEN_CONTENT_LANG, obj.getLanguage());
			node.setProperty(CmsConstants.RADIEN_NAME, obj.getName());

			syncNodeImageProperties(node, obj, session);

			switch (obj.getContentType()) {
				case DOCUMENT:
				case IMAGE:
					syncNodeFileProperties(node, obj, session, now);
					break;
				case HTML:
				case NEWS_FEED:
				case NOTIFICATION:
					node.setProperty(CmsConstants.RADIEN_HTML_CONTENT, obj.getHtmlContent());
					break;
				default:
					break;
			}

			boolean isVersionable = obj instanceof SystemVersionableEnterpriseContent;
			boolean isMandatory = obj instanceof SystemMandatoryEnterpriseContent;

			if(isVersionable && isMandatory) {
				node.setProperty(CmsConstants.RADIEN_VERSION, ((MandatoryVersionableEnterpriseContent)obj).getVersion().getVersion());
				node.setProperty(CmsConstants.RADIEN_VERSION_COMMENT, ((MandatoryVersionableEnterpriseContent)obj).getVersionComment());
				now.setTime(((MandatoryVersionableEnterpriseContent)obj).getValidDate());
				node.setProperty(CmsConstants.RADIEN_VALID_DATE, now);

				node.setProperty(CmsConstants.RADIEN_MANDATORY_APPROVAL, ((MandatoryVersionableEnterpriseContent)obj).isMandatoryApproval());
				node.setProperty(CmsConstants.RADIEN_MANDATORY_VIEW, ((MandatoryVersionableEnterpriseContent)obj).isMandatoryView());
			} else if(isVersionable) {
				node.setProperty(CmsConstants.RADIEN_VERSION, ((VersionableEnterpriseContent)obj).getVersion().getVersion());
				node.setProperty(CmsConstants.RADIEN_VERSION_COMMENT, ((VersionableEnterpriseContent)obj).getVersionComment());
				now.setTime(((VersionableEnterpriseContent)obj).getValidDate());
				node.setProperty(CmsConstants.RADIEN_VALID_DATE, now);
			} else if(isMandatory) {
				node.setProperty(CmsConstants.RADIEN_MANDATORY_APPROVAL, ((MandatoryEnterpriseContent)obj).isMandatoryApproval());
				node.setProperty(CmsConstants.RADIEN_MANDATORY_VIEW, ((MandatoryEnterpriseContent)obj).isMandatoryView());
			}
		}

		try {
			obj.setJcrPath(node.getPath());
			obj.setParentPath(node.getParent().getPath());
		} catch (RepositoryException e) {
			log.error("Could not retrieve JCRPath and or Parent Path");
		}
	}

	private void syncNodeFileProperties(Node node, EnterpriseContent obj, Session session, Calendar now) throws RepositoryException {
		if (obj.getFile() != null) {
			Node contentNode = null;
			contentNode = getContentNode(node);
			if (contentNode == null) {
				contentNode = node.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE);
			}
			InputStream streamFile = new ByteArrayInputStream(obj.getFile());
			Binary file = session.getValueFactory().createBinary(streamFile);
			contentNode.setProperty(JcrConstants.JCR_DATA, file);
			contentNode.setProperty(JcrConstants.JCR_MIMETYPE, obj.getMimeType());
			contentNode.setProperty(JcrConstants.JCR_LASTMODIFIED, now);
			node.setProperty(CmsConstants.RADIEN_FILE_SIZE, file.getSize());
		}
	}

	private void syncNodeImageProperties(Node node, EnterpriseContent obj, Session session) throws RepositoryException {
		if (obj.getImage() != null) {
			Binary binary = null;
			try (InputStream stream = new ByteArrayInputStream(obj.getImage())) {
				binary = session.getValueFactory().createBinary(stream);

				if (binary != null) {
					node.setProperty(CmsConstants.RADIEN_IMAGE, binary);
					node.setProperty(CmsConstants.RADIEN_IMAGE_NAME, obj.getImageName());
					node.setProperty(CmsConstants.RADIEN_IMAGE_MIME_TYPE, obj.getImageMimeType());
				}
			} catch (Exception e) {
				log.error("Error attaching image {} to EnterpriseContent - {}", obj.getImageName(), obj.getName(), e);
			}
			if (binary != null) {
				node.setProperty(CmsConstants.RADIEN_IMAGE, binary);
				node.setProperty(CmsConstants.RADIEN_IMAGE_NAME, obj.getImageName());
				node.setProperty(CmsConstants.RADIEN_IMAGE_MIME_TYPE, obj.getImageMimeType());
			}
		}
	}

	public void decorateNewContent(EnterpriseContent obj) {
		if (obj.getViewId() == null || obj.getViewId().equalsIgnoreCase("")) {
			UUID uuid = UUID.randomUUID();
			String viewId = uuid.toString();
			obj.setViewId(viewId);
		}
		if (obj.getCreateDate() == null) {
			obj.setCreateDate(new Date());
		}

	}

	public EnterpriseContent create(String rootName, String viewId, ContentType contentType) {
		EnterpriseContent content = null;
		try {
			content = new GenericEnterpriseContent(rootName);
			content.setViewId(viewId);
			content.setContentType(contentType);
			decorateNewContent(content);
		} catch (NameNotValidException e) {
			log.error("Error creating node", e);
		}
		return content;
	}

	private String getPropertyStringIfPresent(Node node, String propertyName) throws RepositoryException {
		if(node.hasProperty(propertyName)) {
			return node.getProperty(propertyName).getString();
		}
		return null;
	}

	private boolean getPropertyBooleanIfPresent(Node node, String propertyName) throws RepositoryException {
		if(node.hasProperty(propertyName)) {
			return node.getProperty(propertyName).getBoolean();
		}
		return false;
	}

}