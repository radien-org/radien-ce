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
package io.radien.ms.ecm.legacy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.nodetype.NodeType;
import javax.jcr.version.VersionManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.util.Text;
import org.apache.tika.Tika;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;

import io.radien.api.service.ecm.exception.NameNotValidException;
import io.radien.api.service.ecm.model.Content;
import io.radien.api.service.ecm.model.ContentType;
import io.radien.api.service.ecm.model.ContentVersion;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.ecm.model.GenericEnterpriseContent;

/**
 *
 * @author Marco Weiland <m.weiland@radien.io>
 */
@RequestScoped
public class ContentFactory implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(ContentFactory.class);
    private static final long serialVersionUID = -5005556415803181075L;

    @Inject
    private S3FileUtil s3FileUtil;

    /**
     * Method used to convert a JSONObject into a {@link EnterpriseContent} object
     * @param o the object to be transformed
     * @return an {@link EnterpriseContent} object based on the 'o' properties
     * @throws IOException Exception thrown if the object image reference does not exists on the File System
     */
    public EnterpriseContent convertJSONObject(Object o) throws IOException, ParseException {
        JSONObject json = (JSONObject) o;
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
        String version = null;
        if(json.containsKey("versionable")) {
            versionable = (String) json.get("versionable");
            versionComment = (String) json.get("versionComment");
            validDate = (String) json.get("validDate");
            updateOnLaunch = (String) json.get("updateOnLaunch");
            version = (String) json.get("version");
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
        EnterpriseContent content = null;
        if(versionable != null) {
           content = new GenericEnterpriseContent();
           content.setName(name);
           content.setHtmlContent(htmlContent);
           content.setVersionable(Boolean.parseBoolean(versionable));
           content.setVersionComment(versionComment);
           content.setUpdateOnLaunch(Boolean.parseBoolean(updateOnLaunch));
           content.setValidDate(new SimpleDateFormat("yyyy-MM-dd").parse(validDate));
           content.setVersion(new ContentVersion(version));


        } else {
            content = new Content(Text.escapeIllegalJcrChars(name), htmlContent);
        }
        content.setViewId(viewId);
        content.setContentType(ContentType.getByKey(contentType));
        content.setActive(Boolean.parseBoolean(active));
        content.setSystem(Boolean.parseBoolean(system));
        content.setLanguage(language);
        content.setTags(tags);
        content.setParentPath(parentPath);
        content.setPermissions(permissions);

        getImageResource(json, content);

        if (contentType.equalsIgnoreCase(ContentType.DOCUMENT.key())) {
            getFileResource(json, content);
        }

        return content;
    }

    /**
     * Loads a local image referenced from the local json files into a given {@link EnterpriseContent} object
     *
     * @param json the json object with the image location property
     * @param content the content that the image will attach to
     * @throws IOException Exception thrown if the local image referenced from the json is not present on the filesystem
     */
    private void getImageResource(JSONObject json, EnterpriseContent content) throws IOException {
        InputStream stream = null;
        try {
            String image = (String) json.get("image");
            if (StringUtils.isNotBlank(image)) {
                if(s3FileUtil.isLoadLocalFiles()) {
                    stream = getClass().getClassLoader().getResourceAsStream(image);
                } else {
                    s3FileUtil.getS3FileWithName(image);
                    stream = s3FileUtil.getClassLoaderForBucketFiles().getResourceAsStream(image);
                }

                if (stream != null) {
                    byte[] imageArray = ByteStreams.toByteArray(stream);

                    log.info("Read {} bytes from content image", imageArray.length);

                    content.setImage(imageArray);
                    content.setImageMimeType("image/png");
                    content.setImageName(image);
                }

            }

        } catch (Exception e) {
            log.info("Error converting json object", e);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    /**
     * Loads a local fo;e referenced from the local json files into a given {@link EnterpriseContent} object
     *
     * @param json the json object with the file location property
     * @param content the content that the file will attach to
     * @throws IOException Exception thrown if the local file referenced from the json is not present on the filesystem
     */
    private void getFileResource(JSONObject json, EnterpriseContent content) throws IOException {
        InputStream stream = null;
        try {
            String file = (String) json.get("file");
            if (StringUtils.isNotBlank(file)) {
                if(s3FileUtil.isLoadLocalFiles()) {
                    stream = getClass().getClassLoader().getResourceAsStream(file);
                } else {
                    Path f = Paths.get(file);
                    s3FileUtil.getS3FileWithName(f.getFileName().toString());
                    stream = s3FileUtil.getClassLoaderForBucketFiles().getResourceAsStream(f.getFileName().toString());
                }

                if (stream != null) {
                    Tika tika = new Tika();
                    byte[] fileArray = ByteStreams.toByteArray(stream);
                    log.trace("Read {} bytes from content file with path: {}", fileArray.length, file);
                    log.trace("Content Read from file {}", fileArray);
                    content.setFile(fileArray);
                    content.setMimeType(tika.detect(file));
                    content.setFileSize(fileArray.length);
                }
            }
        } catch (Exception e) {
            log.info("Error converting json object", e);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    /**
     * Checks if the given node is from oaf, this is useful to skip some of the JCR tree and increase performance
     * @param node the node to be checked
     * @return true if the node is an oaf node, false otherwise
     */
    private boolean isOafNode(Node node) {
        NodeType[] mixinNodeTypes;
        boolean result = false;
        try {
            mixinNodeTypes = node.getMixinNodeTypes();
            for (NodeType type : mixinNodeTypes) {
                if (CmsConstants.RD_MIXIN_NODE_PROPS.equalsIgnoreCase(type.getName())) {
                    result = true;
                }
            }
        } catch (RepositoryException e) {
            log.error("Error checking if it is oaf node", e);
        }
        return result;
    }

    /**
     * Converts a JCR {@link Node} into a {@link EnterpriseContent} content object
     * @param node the JCR node to transform
     * @return an {@link EnterpriseContent} containing the node properties
     * @throws RepositoryException exception thrown in case of an unavailable repository
     */
    public EnterpriseContent convertJCRNode(Node node) throws RepositoryException {

        try {
            EnterpriseContent systemContent = getEnterpriseContent(node);

            if (isOafNode(node) && systemContent != null) {
                decorateContent(node, systemContent);
            }

            return systemContent;
        } catch (RepositoryException e) {
            log.error("Error while converting JCR node!");
            throw new RepositoryException(e.getMessage());
        }

    }

    /**
     * Decorates a {@link EnterpriseContent} based on a JCR {@link Node} properties
     * @param node the node with the properties to set on the systemContent
     * @param systemContent the object that gets decorated with the node properties
     */
    private void decorateContent(Node node, EnterpriseContent systemContent) {
        try {
            systemContent.setViewId(node.getProperty(CmsConstants.RD_VIEW_ID).getString());
            if(node.hasProperty(CmsConstants.RD_NAME)) {
                systemContent.setName(node.getProperty(CmsConstants.RD_NAME).getString());
            }
            if (node.hasProperty(CmsConstants.RD_HTML_CONTENT)) {
                systemContent.setHtmlContent(node.getProperty(CmsConstants.RD_HTML_CONTENT).getString());
            }
            systemContent.setContentType(ContentType.getByKey(node.getProperty(CmsConstants.RD_CONTENT_TYPE).getString()));
            systemContent.setActive(node.getProperty(CmsConstants.RD_ACTIVE).getBoolean());
            systemContent.setSystem(node.getProperty(CmsConstants.RD_SYSTEM).getBoolean());
            systemContent.setLanguage(node.getProperty(CmsConstants.RD_CONTENT_LANG).getString());
//            systemContent.setLastEditDate(node.getProperty(CmsConstants.RD_LAST_EDIT_DATE).getDate().getTime());
            if(node.hasProperty(CmsConstants.RD_CREATED)) {
                systemContent.setCreateDate(node.getProperty(CmsConstants.RD_CREATED).getDate().getTime());
            } else {
                systemContent.setCreateDate(node.getProperty(CmsConstants.JCR_CREATED).getDate().getTime());
            }



            if(node.hasProperty(CmsConstants.RD_CONTENT_PERMISSIONS)) {
                systemContent.setPermissions(node.getProperty(CmsConstants.RD_CONTENT_PERMISSIONS).getString());
            }
            if (node.hasProperty(CmsConstants.RD_APP)) {
                systemContent.setApp(node.getProperty(CmsConstants.RD_APP).getString());
            }
            if(node.hasProperty(CmsConstants.RD_CONTENT_AUTHOR)) {
                systemContent.setAuthor(node.getProperty(CmsConstants.RD_CONTENT_AUTHOR).getString());
            }

            List<String> tags = new ArrayList<>();
            if (node.hasProperty(CmsConstants.RD_TAGS)) {
                for (Value value : node.getProperty(CmsConstants.RD_TAGS).getValues()) {
                    tags.add(value.getString());
                }
            }
            if(node.hasProperty(CmsConstants.RD_VERSION_COMMENT)) {
                systemContent.setVersionable(true);
                systemContent.setVersionComment(node.getProperty(CmsConstants.RD_VERSION_COMMENT).getString());
            }
            if(node.hasProperty(CmsConstants.RD_VALID_DATE)) {
                systemContent.setValidDate(node.getProperty(CmsConstants.RD_VALID_DATE).getDate().getTime());
            }

            systemContent.setTags(tags);

            setBinaryImage(node, systemContent);

            setDocumentFileSize(node, systemContent);

            setMIMEType(node, systemContent);

        } catch (Exception e) {
            log.info("Error decorating JCR node", e);
        }
    }

    /**
     * Sets the content MIME type based the node JCR_MIMETYPE constant
     * @param node the JCR node containing the JCR_MIMETYPE property
     * @param systemContent the returned node with the corresponding MIME type
     */
    private void setMIMEType(Node node, EnterpriseContent systemContent) {
        try {
            switch (systemContent.getContentType()) {

                case DOCUMENT:
                case HTML:
                case NEWS_FEED:
                
                case NOTIFICATION:
                
                    systemContent.setMimeType(
                            node.getNode(JcrConstants.JCR_CONTENT).getProperty(JcrConstants.JCR_MIMETYPE).getString());
                    break;

                default:
                    break;
            }

        } catch (Exception e) {
            log.error("Error setting JCR node MIME type", e);
        }
    }

    /**
     * Sets the file size property of given systemContent if it is of type 'DOCUMENT'
     * @see ContentType
     * @param node the JCR Node with the size property
     * @param systemContent the final systemContent with the file size if its of type 'DOCUMENT'
     */
    private void setDocumentFileSize(Node node, EnterpriseContent systemContent) {
        try {
            if (systemContent.getContentType() == ContentType.DOCUMENT) {
                systemContent.setFileSize(node.getProperty(CmsConstants.RD_FILE_SIZE).getLong());
            }

        } catch (Exception e) {
            log.error("Error converting JCR node", e);
        }
    }

    private void setBinaryImage(Node node, EnterpriseContent systemContent) {
    	InputStream stream = null;
    	try {
            Binary bin = null;
            if (node.hasProperty(CmsConstants.RD_IMAGE)) {
                bin = node.getProperty(CmsConstants.RD_IMAGE).getBinary();
            }
            if (bin != null) {
                stream = bin.getStream();
                byte[] imageArray = ByteStreams.toByteArray(stream);
                systemContent.setImage(imageArray);
                systemContent.setImageMimeType(node.getProperty(CmsConstants.RD_IMAGE_MIME_TYPE).getString());
                systemContent.setImageName(node.getProperty(CmsConstants.RD_IMAGE_NAME).getString());
            }
        } catch (Exception e) {
            log.error("Error getting oaf:image from JCR", e);
        }finally {
        	if (stream != null ) {
        		try {
					stream.close();
				} catch (IOException e) {
					 log.warn("image binary stream could not be closed.", e);
				}
        	}
        }
    }

    private EnterpriseContent getEnterpriseContent(Node node)
            throws RepositoryException {
        EnterpriseContent enterpriseContent = null;
        try {
            if(node.hasProperty(CmsConstants.RD_VERSION_COMMENT)) {
                enterpriseContent = new GenericEnterpriseContent();
                enterpriseContent.setName(Text.unescapeIllegalJcrChars(node.getName()));

            } else {
                enterpriseContent = new GenericEnterpriseContent(Text.unescapeIllegalJcrChars(node.getName()));
            }
            enterpriseContent.setJcrPath(node.getPath());
            enterpriseContent.setParentPath(node.getParent().getPath());
        } catch (NameNotValidException e1) {
            log.info("Error converting JCR node", e1);
        }
        return enterpriseContent;
    }

    /**
     * Returns a byte array from a file present on the given path
     *
     * @param session the current JCR session
     * @param path the file path
     * @return the byte array with the file contents
     * @throws RepositoryException if the JCR is not available
     * @throws IOException if the file with the given path is not present on the filesystem
     */
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

    public void syncNode(Node node, EnterpriseContent obj, Session session) throws RepositoryException {
        boolean isVersionable = obj instanceof EnterpriseContent;
        decorateNewContent(obj);

        Calendar now = Calendar.getInstance();
        if(isVersionable && !node.isCheckedOut()) {
            VersionManager versionManager = session.getWorkspace().getVersionManager();
            versionManager.checkout(node.getPath());
        }
        setGlobalProperties(node, obj);

        if(obj.getImage() != null) {
            setImageProperty(node, obj, session);
        }

        try {
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
                        node.setProperty(CmsConstants.RD_FILE_SIZE, file.getSize());

                    }
                    break;
                case HTML:
                case NEWS_FEED:
                
                case NOTIFICATION:
                
                    Node contentNode = null;
                    contentNode = getNode(node, contentNode);

                    if (contentNode == null) {
                        contentNode = node.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE);
                    }

                    contentNode.setProperty(JcrConstants.JCR_DATA, obj.getHtmlContent());
                    contentNode.setProperty(JcrConstants.JCR_MIMETYPE, "text/html");
                    contentNode.setProperty(JcrConstants.JCR_ENCODING, "UTF-8");
                    contentNode.setProperty(JcrConstants.JCR_LASTMODIFIED, now);
                    break;
                case TAG:
                    Node contNode = null;
                    contNode = getNode(node, contNode);

                    if (contNode == null) {
                        node.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_UNSTRUCTURED);
                    }
                    break;
                case FOLDER:
                    break;
            }
        } catch (PathNotFoundException e) {
            log.error("No image attached");
        } catch (Exception e) {
            log.error("Error syncing node", e);
        }

    }

    private void setImageProperty(Node node, EnterpriseContent obj, Session session) {
        try(InputStream stream = new ByteArrayInputStream(obj.getImage())) {
            Binary binary = session.getValueFactory().createBinary(stream);

            if (binary != null) {
                node.setProperty(CmsConstants.RD_IMAGE, binary);
                node.setProperty(CmsConstants.RD_IMAGE_NAME, obj.getImageName());
                node.setProperty(CmsConstants.RD_IMAGE_MIME_TYPE, obj.getImageMimeType());
            }
        } catch (Exception e){
            log.error("Error attaching image {} to EnterpriseContent - {}", obj.getImageName(), obj.getName());
        }
    }

    /**
     * Sets the global properties of an oaf JCR node based on a given {@link EnterpriseContent} obj
     * @param node the target JCR node to be populated with the object properties
     * @param obj the source of the properties that are set in the JCR node
     */
    private void setGlobalProperties(Node node, EnterpriseContent obj) {
        try {
            node.setProperty(CmsConstants.RD_VIEW_ID, obj.getViewId());
            node.setProperty(CmsConstants.RD_NAME, obj.getName());
            node.setProperty(CmsConstants.RD_CONTENT_TYPE, obj.getContentType().key());
            node.setProperty(CmsConstants.RD_ACTIVE, obj.isActive());
            node.setProperty(CmsConstants.RD_HTML_CONTENT, obj.getHtmlContent());
            node.setProperty(CmsConstants.RD_SYSTEM, obj.isSystem());
            
            node.setProperty(CmsConstants.RD_CONTENT_LANG, obj.getLanguage());
            node.setProperty(CmsConstants.RD_CONTENT_AUTHOR, obj.getAuthor());
            
            node.setProperty(CmsConstants.RD_CONTENT_PERMISSIONS, obj.getPermissions());

//            Date lastEditDate = obj.getLastEditDate();
//            if(lastEditDate == null) {
//                lastEditDate =  new Date();
//            }
            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(lastEditDate);
//            node.setProperty(CmsConstants.RD_LAST_EDIT_DATE, calendar);

            String app = obj.getApp();
            if(app!= null) {
            	node.setProperty(CmsConstants.RD_APP, app);
            }

            List<String> contentTags = obj.getTags();
            if(contentTags != null) {
                String[] tags = new String[contentTags.size()];
                tags = contentTags.toArray(tags);
                node.setProperty(CmsConstants.RD_TAGS, tags);
            }

            if(obj instanceof EnterpriseContent) {
                GenericEnterpriseContent versionableObj = (GenericEnterpriseContent) obj;
                node.setProperty(CmsConstants.RD_VERSION_COMMENT, versionableObj.getVersionComment());
                calendar.setTime(versionableObj.getValidDate());
                node.setProperty(CmsConstants.RD_VALID_DATE, calendar);

                Date createdDate = versionableObj.getCreateDate();
                if (createdDate == null) {
                    createdDate = new Date();
                }
                calendar.setTime(createdDate);
                node.setProperty(CmsConstants.RD_CREATED, calendar);
                node.setProperty(CmsConstants.RD_VERSION, versionableObj.getVersion().getVersion());
            }
            



            obj.setJcrPath(node.getPath());
            obj.setParentPath(node.getParent().getPath());
        } catch (Exception e) {
            log.error("Error setting node properties", e);
        }
    }

    /**
     * JCR compliant method to obtain a Node's content
     *
     * @param node the node to retrieve the content
     * @param contentNode the final node with content to return
     * @return The node with te content property
     */
    private Node getNode(Node node, Node contentNode) {
        try {
            contentNode = node.getNode(JcrConstants.JCR_CONTENT);
        } catch (Exception ignored) {
            //Swallow exception
        }
        return contentNode;
    }

    /**
     * Decorates a new content object with a default set of properties
     *
     * @param obj the content object to be decorated
     */
    public void decorateNewContent(EnterpriseContent obj) {
        if (StringUtils.isBlank(obj.getViewId()) ||
                StringUtils.isBlank(obj.getLanguage())) {
            UUID uuid = UUID.randomUUID();
            String viewId = uuid.toString();
            obj.setViewId(viewId);
        }
        if (obj.getCreateDate() == null) {
            obj.setCreateDate(new Date());
        }

    }

    /**
     * Creates a default content with given name, viewId and type
     * @param rootName the name of the content
     * @param viewId the identifier of this content
     * @param contentType the content Type
     * @return a new {@link EnterpriseContent} with the values given, and default properties set
     * @see ContentFactory#decorateContent(Node, EnterpriseContent)
     */
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

    public GenericEnterpriseContent convertJCRVersionNode(Node versionNode) throws RepositoryException {
        try {
            EnterpriseContent systemContent = getVersionedEnterpriseContent(versionNode);
            GenericEnterpriseContent resultDTO = (GenericEnterpriseContent) systemContent;
            if(resultDTO != null) {
                if(versionNode.hasProperty(CmsConstants.RD_VERSION)) {
                    resultDTO.setVersion(new ContentVersion(versionNode.getProperty(CmsConstants.RD_VERSION).getString()));
                } else {
                    resultDTO.setVersion(new ContentVersion(versionNode.getParent().getName()));
                }
                resultDTO.setJcrPath(versionNode.getPath());
            }
            if (systemContent != null) {
                decorateContent(versionNode, resultDTO);
            } else {
                return null;
            }

            return resultDTO;
        } catch (RepositoryException e) {
            log.error("Error while converting JCR node!");
            throw new RepositoryException(e.getMessage());
        }
    }

    private EnterpriseContent getVersionedEnterpriseContent(Node versionNode) throws RepositoryException {
        EnterpriseContent enterpriseContent = null;
        if(versionNode.hasProperty(CmsConstants.RD_VIEW_ID)) {
            enterpriseContent = new GenericEnterpriseContent();
            enterpriseContent.setName(Text.unescapeIllegalJcrChars(
                   versionNode.getProperty(CmsConstants.RD_VIEW_ID).getString()));
            enterpriseContent.setJcrPath(versionNode.getPath());
            enterpriseContent.setParentPath(versionNode.getParent().getPath());
        }
        return enterpriseContent;
    }
}
