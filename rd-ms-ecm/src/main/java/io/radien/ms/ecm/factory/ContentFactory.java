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
package io.radien.ms.ecm.factory;

import com.google.common.io.ByteStreams;
import io.radien.api.service.ecm.exception.NameNotValidException;
import io.radien.api.service.ecm.model.Content;
import io.radien.api.service.ecm.model.GenericEnterpriseContent;
import io.radien.api.service.ecm.model.ContentType;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.ms.ecm.constants.CmsConstants;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.util.Text;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Class responsible for creating generic content that can be manipulated in the cms
 *
 * @author Marco Weiland
 * @author jrodrigues
 */
public @RequestScoped class ContentFactory implements Serializable {

	private static final Logger log = LoggerFactory.getLogger(ContentFactory.class);
	private static final long serialVersionUID = -5005556415803181075L;

	public EnterpriseContent convertJSONObject(JSONObject json) throws IOException, ParseException {
		String viewId = (String) json.get("viewId");
		String htmlContent = (String) json.get("htmlContent");
		String name = (String) json.get("name");
		String contentType = (String) json.get("contentType");
		String language = (String) json.get("language");
		String active = (String) json.get("active");
		String system = (String) json.get("system");
		String parentPath = (String) json.get("parentPath");
		String versionable = null;
		String versionComment = null;
		String validDate = null;
		String updateOnLaunch = null;
		if(json.containsKey("versionable")) {
			versionable = (String) json.get("versionable");
			versionComment = (String) json.get("versionComment");
			validDate = (String) json.get("validDate");
			updateOnLaunch = (String) json.get("updateOnLaunch");
		}
		String externalPublic = (String) json.get("externalPublic");
		String permissions = (String) json.get("permissions");
		if(permissions == null) {
			permissions = "NONE";
		}
		JSONArray jsonArray = (JSONArray) json.get("tags");
		List<String> tags = new ArrayList<>();
		if (jsonArray != null) {
			for (Object obj : jsonArray) {
				tags.add((String) obj);
			}
		}
		EnterpriseContent content = new Content(Text.escapeIllegalJcrChars(name), htmlContent);
		content.setViewId(viewId);
		content.setContentType(ContentType.getByKey(contentType));
		content.setActive(Boolean.parseBoolean(active));
		content.setSystem(Boolean.parseBoolean(system));
		content.setLanguage(language);
		content.setTags(tags);
		content.setParentPath(parentPath);
		content.setExternalPublic(Boolean.parseBoolean(externalPublic));
		content.setPermissions(permissions);

		getImageResource(json, content);

		if (contentType.equalsIgnoreCase(ContentType.DOCUMENT.key())) {
			getFileResource(json, content);
		}
		return content;
	}

	public static EnterpriseContent convertJsonObject(JsonObject json) throws IOException, ParseException {
		String viewId = json.getString("viewId");
		String htmlContent = json.getString("htmlContent");
		String name = json.getString("name");
		String contentType = json.getString("contentType");
		String language = json.getString("language");
		String active = json.getString("active");
		String system = json.getString("system");
		String parentPath = json.containsKey("parentPath") ? json.getString("parentPath") : null;
		String versionable = null;
		String versionComment = null;
		String validDate = null;
		String updateOnLaunch = null;
		if(json.containsKey("versionable")) {
			versionable = (String) json.getString("versionable");
			versionComment = (String) json.getString("versionComment");
			validDate = (String) json.getString("validDate");
			updateOnLaunch = (String) json.getString("updateOnLaunch");
		}
		String externalPublic = json.containsKey("externalPublic") ? json.getString("externalPublic") : "true";
		String permissions = json.containsKey("permissions") ? json.getString("permissions") : null;
		if(permissions == null) {
			permissions = "NONE";
		}
		JsonArray jsonArray = json.getJsonArray("tags");
		List<String> tags = new ArrayList<>();
		if (jsonArray != null) {
			jsonArray.forEach(obj -> tags.add(obj.toString()));
		}
		EnterpriseContent content = new Content(Text.escapeIllegalJcrChars(name), htmlContent);
		content.setViewId(viewId);
		content.setContentType(ContentType.getByKey(contentType));
		content.setActive(Boolean.parseBoolean(active));
		content.setSystem(Boolean.parseBoolean(system));
		content.setLanguage(language);
		content.setTags(tags);
		content.setParentPath(parentPath);
		content.setExternalPublic(Boolean.parseBoolean(externalPublic));
		content.setPermissions(permissions);

		getImageResourceJson(json, content);

		if (contentType.equalsIgnoreCase(ContentType.DOCUMENT.key())) {
			getFileResourceJson(json, content);
		}
		return content;
	}

	private static void getImageResourceJson(JsonObject json, EnterpriseContent content) throws IOException {
		InputStream stream = null;
		try {
			String image = json.getString("image");
			if (StringUtils.isNotBlank(image)) {
				stream = ContentFactory.class.getClassLoader().getResourceAsStream(image);

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

	private static void getFileResourceJson(JsonObject json, EnterpriseContent content) throws IOException {
		InputStream stream = null;
		try {
			String file = json.getString("file");
			if (StringUtils.isNotBlank(file)) {
				stream = ContentFactory.class.getClassLoader().getResourceAsStream(file);

				if (stream != null) {
					byte[] fileArray = IOUtils.toByteArray(stream);
					log.trace("Read {} bytes from content file with path: {}", fileArray.length, file);
					log.trace("Content Read from file {}", fileArray);
					content.setFile(fileArray);

					String filePath = ContentFactory.class.getClassLoader().getResource(file).getPath();
					String mimeType = Files.probeContentType(new File(filePath).toPath());
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

	private void getFileResource(JSONObject json, EnterpriseContent content) throws IOException {
		InputStream stream = null;
		try {
			String file = (String) json.get("file");
			if (StringUtils.isNotBlank(file)) {
				stream = getClass().getClassLoader().getResourceAsStream(file);

				if (stream != null) {
					byte[] fileArray = IOUtils.toByteArray(stream);
					log.trace("Read {} bytes from content file with path: {}", fileArray.length, file);
					log.trace("Content Read from file {}", fileArray);
					content.setFile(fileArray);

					String filePath = getClass().getClassLoader().getResource(file).getPath();
					String mimeType = Files.probeContentType(new File(filePath).toPath());
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

	public boolean isOafNode(Node node) {
		NodeType[] mixinNodeTypes;
		boolean isOAFNode = false;
		try {
			mixinNodeTypes = node.getMixinNodeTypes();
			for (NodeType type : mixinNodeTypes) {
				if (!isOAFNode) {
					isOAFNode = type.getName().equalsIgnoreCase(CmsConstants.RADIEN_MIXIN_NODE_PROPS);
				}
			}
		} catch (RepositoryException e) {
			log.error("Error checking if it is oaf node", e);
		}
		return isOAFNode;
	}

	public EnterpriseContent convertJCRNode(Node node) throws RepositoryException {

		try {
			EnterpriseContent systemContent = null;
			try {
				systemContent = new GenericEnterpriseContent(Text.unescapeIllegalJcrChars(node.getName()));
				systemContent.setJcrPath(node.getPath());
				systemContent.setParentPath(node.getParent().getPath());
			} catch (NameNotValidException e1) {
				log.info("Error converting JCR node", e1);
			}

			if (isOafNode(node) && systemContent != null) {
				try {
					systemContent.setViewId(node.getProperty(CmsConstants.RADIEN_VIEW_ID).getString());
					systemContent.setHtmlContent(node.getProperty(CmsConstants.RADIEN_HTML_CONTENT).getString());
					systemContent.setContentType(
							ContentType.getByKey(node.getProperty(CmsConstants.RADIEN_CONTENT_TYPE).getString()));
					systemContent.setActive(node.getProperty(CmsConstants.RADIEN_ACTIVE).getBoolean());
					systemContent.setSystem(node.getProperty(CmsConstants.RADIEN_SYSTEM).getBoolean());
					systemContent.setLanguage(node.getProperty(CmsConstants.RADIEN_CONTENT_LANG).getString());
					systemContent.setCreateDate(node.getProperty(JcrConstants.JCR_CREATED).getDate().getTime());

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

					try {
						if (systemContent.getContentType() == ContentType.DOCUMENT) {
							systemContent.setFileSize(node.getProperty(CmsConstants.RADIEN_FILE_SIZE).getLong());
						}

					} catch (PathNotFoundException e) {
						log.info("Error converting JCR node", e);
					} catch (Exception e) {
						log.error("Error converting JCR node", e);
					}

					try {
						switch (systemContent.getContentType()) {

							case DOCUMENT:
							case HTML:
							case NEWS_FEED:
							case NOTIFICATION:
								systemContent.setMimeType(node.getNode(JcrConstants.JCR_CONTENT)
										.getProperty(JcrConstants.JCR_MIMETYPE).getString());
								break;

							default:
								break;
						}

					} catch (PathNotFoundException e) {
						log.info("Error converting JCR node", e);
					} catch (Exception e) {
						log.error("Error converting JCR node", e);
					}

				} catch (Exception e) {
					log.info("Error converting JCR node", e);
				}
			}

			return systemContent;
		} catch (RepositoryException e) {
			log.error("Error while converting JCR node!");
			throw new RepositoryException(e.getMessage());
		}

	}

	public byte[] getFile(Session session, String path) throws RepositoryException, IOException {

		Node fileNode = session.getNode(path);
		InputStream stream;
		if (fileNode != null) {
			stream = JcrUtils.readFile(fileNode);
			byte[] fileArray = ByteStreams.toByteArray(stream);
			log.trace("Read {} bytes from content file with path: {}", fileArray.length, path);
			log.trace("Content Read from file {}", fileArray);
			return fileArray;
		}
		return StringUtils.EMPTY.getBytes();
	}

	private Node getNode(Node node, Node contentNode) {
		try {
			contentNode = node.getNode(JcrConstants.JCR_CONTENT);
		} catch (Exception ignored) {
			//Swallow exception
		}
		return contentNode;
	}

	public void syncNode(Node node, EnterpriseContent obj, Session session) {

		decorateNewContent(obj);

		Calendar now = Calendar.getInstance();

		try {

			node.setProperty(CmsConstants.RADIEN_VIEW_ID, obj.getViewId());
			node.setProperty(CmsConstants.RADIEN_CONTENT_TYPE, obj.getContentType().key());
			node.setProperty(CmsConstants.RADIEN_ACTIVE, obj.isActive());
			node.setProperty(CmsConstants.RADIEN_HTML_CONTENT, obj.getHtmlContent());
			node.setProperty(CmsConstants.RADIEN_SYSTEM, obj.isSystem());
			node.setProperty(CmsConstants.RADIEN_CONTENT_LANG, obj.getLanguage());

			obj.setJcrPath(node.getPath());
			obj.setParentPath(node.getParent().getPath());
		} catch (Exception e) {
			log.error("Error syncing node", e);
		}

		try {
			Binary binary = null;
			if (obj.getImage() != null) {
				try(InputStream stream = new ByteArrayInputStream(obj.getImage())) {
					binary = session.getValueFactory().createBinary(stream);

					if (binary != null) {
						node.setProperty(CmsConstants.RADIEN_IMAGE, binary);
						node.setProperty(CmsConstants.RADIEN_IMAGE_NAME, obj.getImageName());
						node.setProperty(CmsConstants.RADIEN_IMAGE_MIME_TYPE, obj.getImageMimeType());
					}
				} catch (Exception e){
					log.error("Error attaching image {} to EnterpriseContent - {}", obj.getImageName(), obj.getName(), e);
				}
			}

			if (binary != null) {
				node.setProperty(CmsConstants.RADIEN_IMAGE, binary);
				node.setProperty(CmsConstants.RADIEN_IMAGE_NAME, obj.getImageName());
				node.setProperty(CmsConstants.RADIEN_IMAGE_MIME_TYPE, obj.getImageMimeType());
			}

			switch (obj.getContentType()) {
				case DOCUMENT:
				case IMAGE:
					if (obj.getFile() != null) {
						Node contentNode = null;
						contentNode = getNode(node, contentNode);

						if (contentNode == null) {
							contentNode = node.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE);
						}

						InputStream streamFile = new ByteArrayInputStream(obj.getFile());
						Binary file  = session.getValueFactory().createBinary(streamFile);
						contentNode.setProperty(JcrConstants.JCR_DATA, file);
						contentNode.setProperty(JcrConstants.JCR_MIMETYPE, obj.getMimeType());
						contentNode.setProperty(JcrConstants.JCR_LASTMODIFIED, now);
						node.setProperty(CmsConstants.RADIEN_FILE_SIZE, file.getSize());

					}
					break;
				case HTML:
				case NEWS_FEED:
				case NOTIFICATION:
					Node contentNode = null;
					try {
						contentNode = node.getNode(JcrConstants.JCR_CONTENT);
					} catch (Exception e) {
						log.info("Initializing jcr node: " + JcrConstants.JCR_CONTENT);
					}

					if (contentNode == null) {
						contentNode = node.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE);
					}

					contentNode.setProperty(JcrConstants.JCR_DATA, obj.getHtmlContent());
					contentNode.setProperty(JcrConstants.JCR_MIMETYPE, "text/html");
					contentNode.setProperty(JcrConstants.JCR_ENCODING, "UTF-8");
					contentNode.setProperty(JcrConstants.JCR_LASTMODIFIED, now);
					break;

				case FOLDER:

					break;
			}
		} catch (PathNotFoundException e) {
			log.error("no image attached");
		} catch (Exception e) {
			log.error("Error syncing node", e);
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

}