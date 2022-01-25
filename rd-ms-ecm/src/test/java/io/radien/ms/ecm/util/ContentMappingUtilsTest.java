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

import io.radien.api.service.ecm.exception.NameNotValidException;
import io.radien.api.service.ecm.model.ContentType;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.ecm.model.GenericEnterpriseContent;
import io.radien.api.service.ecm.model.MandatoryEnterpriseContent;
import io.radien.api.service.ecm.model.MandatoryVersionableEnterpriseContent;
import io.radien.api.service.ecm.model.VersionableEnterpriseContent;
import io.radien.ms.ecm.constants.CmsConstants;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;
import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFactory;
import javax.jcr.nodetype.NodeType;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.jackrabbit.JcrConstants;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContentMappingUtilsTest {
    private final static String viewID = "viewId";
    private final static String htmlContent = "htmlContent";
    private final static String name = "contentName";
    private final static String contentTypeHtml = "html";
    private final static String contentTypeDocument = "document";
    private final static String language = "en";
    private final static Boolean active = Boolean.TRUE;
    private final static Boolean system = Boolean.TRUE;
    private final static String parentPath = "/a/parent/path";
    private final static String jcrPath = "/a/parent/path/file";
    private final static String createDate = "2020-10-10";
    private final static Boolean versionable = Boolean.TRUE;
    private final static String versionComment = "versionComment";
    private final static String version = "1.1.0";
    private final static Boolean mandatoryView = Boolean.TRUE;
    private final static Boolean mandatoryApprove = Boolean.TRUE;
    private final static Boolean externalPublic = Boolean.TRUE;
    private final static String permissions = null;
    private final static String file = "META-INF/beans.xml";
    private final static String fileArray = "101,120,97,109,112,108,101";


    @InjectMocks
    private ContentMappingUtils contentMappingUtils;

    @Before
    public void init() throws IOException {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testConvertSeederJSONObjectNoImageNoFile() throws IOException, ParseException {
        JSONObject object = new JSONObject();
        object.put("viewId", viewID);
        object.put("htmlContent", htmlContent);
        object.put("name", name);
        object.put("contentType", contentTypeHtml);
        object.put("language", language);
        object.put("active", active);
        object.put("system", system);
        object.put("parentPath", parentPath);
        object.put("jcrPath", jcrPath);
        object.put("createDate", createDate);
        object.put("externalPublic", externalPublic);
        object.put("permissions", permissions);
        JSONArray tagArray = new JSONArray();
        object.put("tags", tagArray);

        EnterpriseContent content = contentMappingUtils.convertSeederJSONObject(object);
        validateEnterpriseContent(content, contentTypeHtml);
    }

    @Test
    public void testConvertSeederJSONObjectNoImageFile() throws IOException, ParseException {
        JSONObject object = new JSONObject();
        object.put("viewId", viewID);
        object.put("htmlContent", htmlContent);
        object.put("name", name);
        object.put("contentType", contentTypeHtml);
        object.put("language", language);
        object.put("active", active);
        object.put("system", system);
        object.put("parentPath", parentPath);
        object.put("jcrPath", jcrPath);
        object.put("createDate", createDate);
        object.put("externalPublic", externalPublic);
        object.put("permissions", permissions);
        object.put("file", file);
        JSONArray tagArray = new JSONArray();
        object.put("tags", tagArray);

        EnterpriseContent content = contentMappingUtils.convertSeederJSONObject(object);
        validateEnterpriseContent(content, contentTypeHtml);
        assertNotNull(content.getFile());
        assertEquals(content.getFileSize(), content.getFile().length);
    }

    @Test
    public void testConvertSeederJSONObjectImageNoFile() throws IOException, ParseException {
        JSONObject object = new JSONObject();
        object.put("viewId", viewID);
        object.put("htmlContent", htmlContent);
        object.put("name", name);
        object.put("contentType", contentTypeHtml);
        object.put("language", language);
        object.put("active", active);
        object.put("system", system);
        object.put("parentPath", parentPath);
        object.put("jcrPath", jcrPath);
        object.put("createDate", createDate);
        object.put("externalPublic", externalPublic);
        object.put("permissions", permissions);
        object.put("image", file);
        JSONArray tagArray = new JSONArray();
        object.put("tags", tagArray);

        EnterpriseContent content = contentMappingUtils.convertSeederJSONObject(object);
        validateEnterpriseContent(content, contentTypeHtml);
        assertNotNull(content.getImage());
        assertEquals("image/png", content.getImageMimeType());
        assertEquals(file, content.getImageName());
    }

    @Test
    public void testIsRadienNodeTrue() throws RepositoryException {
        Node mockNode = initMockNode("rd:NodeType");
        assertTrue(contentMappingUtils.isRadienNode(mockNode));
    }

    @Test
    public void testIsRadienNodeFalse() throws RepositoryException {
        Node mockNode = initMockNode("rd:DifferentNodeType");
        assertFalse(contentMappingUtils.isRadienNode(mockNode));
    }

    @Test
    public void testIsRadienNodeException() throws RepositoryException {
        Node mockNode = mock(Node.class);
        NodeType mockNodeType = mock(NodeType.class);
        when(mockNode.getPrimaryNodeType())
                .thenThrow(new RepositoryException());
        assertFalse(contentMappingUtils.isRadienNode(mockNode));
    }

    @Test
    public void convertJCRNodeNotRadien() throws RepositoryException {
        Node mockNode = initMockNode("rd:DifferentNodeType");
        EnterpriseContent result = contentMappingUtils.convertJCRNode(mockNode);
        assertNull(result);
    }

    @Test
    public void convertJCRNodeGenericNoImageHTML() throws RepositoryException, ParseException {
        Calendar calendar = Calendar.getInstance();
        Node mockNode = initGenericMockNode(calendar);

        EnterpriseContent content = contentMappingUtils.convertJCRNode(mockNode);
        validateEnterpriseContent(content, contentTypeHtml);
    }

    @Test
    public void convertJCRNodeGenericImageHTML() throws RepositoryException, ParseException {
        Calendar calendar = Calendar.getInstance();
        Node mockNode = initGenericMockNode(calendar);
        when(mockNode.hasProperty(CmsConstants.RADIEN_IMAGE))
                .thenReturn(true);
        Binary mockBinary = mock(Binary.class);
        when(mockBinary.getStream())
                .thenReturn(new ByteArrayInputStream(fileArray.getBytes()));
        Property mockImageProperty = mock(Property.class);
        when(mockImageProperty.getBinary())
                .thenReturn(mockBinary);
        when(mockNode.getProperty(CmsConstants.RADIEN_IMAGE))
                .thenReturn(mockImageProperty);
        Property mockImageMimeTypeProperty = initMockStringProperty("image/png");

        when(mockNode.hasProperty(CmsConstants.RADIEN_IMAGE_MIME_TYPE))
                .thenReturn(true);
        when(mockNode.getProperty(CmsConstants.RADIEN_IMAGE_MIME_TYPE))
                .thenReturn(mockImageMimeTypeProperty);
        Property mockImageNameProperty = initMockStringProperty("imageName");
        when(mockNode.hasProperty(CmsConstants.RADIEN_IMAGE_NAME))
                .thenReturn(true);
        when(mockNode.getProperty(CmsConstants.RADIEN_IMAGE_NAME))
                .thenReturn(mockImageNameProperty);
        EnterpriseContent content = contentMappingUtils.convertJCRNode(mockNode);
        validateEnterpriseContent(content, contentTypeHtml);
        assertNotNull(content.getImage());
        assertEquals("image/png", content.getImageMimeType());
        assertEquals("imageName", content.getImageName());
    }

    @Test
    public void convertJCRNodeGenericNoImageDocument() throws RepositoryException, ParseException {
        Calendar calendar = Calendar.getInstance();
        Node mockNode = initGenericMockNode(calendar);
        Property mockContentTypeProperty = initMockStringProperty(contentTypeDocument);
        when(mockNode.getProperty(CmsConstants.RADIEN_CONTENT_TYPE))
                .thenReturn(mockContentTypeProperty);
        when(mockNode.hasProperty(CmsConstants.RADIEN_CONTENT_TYPE))
                .thenReturn(true);
        Property mockFileSizeProperty = initMockLongProperty(10);
        when(mockNode.getProperty(CmsConstants.RADIEN_FILE_SIZE))
                .thenReturn(mockFileSizeProperty);
        Node mockContentNode = initMockNode("nt:file");
        Property mockMimeTypeProperty = initMockStringProperty("application/html");
        when(mockContentNode.getProperty(JcrConstants.JCR_MIMETYPE))
                .thenReturn(mockMimeTypeProperty);
        when(mockNode.getNode(JcrConstants.JCR_CONTENT))
                .thenReturn(mockContentNode);

        EnterpriseContent content = contentMappingUtils.convertJCRNode(mockNode);
        validateEnterpriseContent(content, contentTypeDocument);
        assertEquals(10, content.getFileSize());
        assertEquals("application/html", content.getMimeType());
    }

    @Test
    public void convertJCRNodeGenericNoImageDocumentPathNotFound() throws RepositoryException, ParseException {
        Calendar calendar = Calendar.getInstance();
        Node mockNode = initGenericMockNode(calendar);
        Property mockContentTypeProperty = initMockStringProperty(contentTypeDocument);
        when(mockNode.getProperty(CmsConstants.RADIEN_CONTENT_TYPE))
                .thenReturn(mockContentTypeProperty);
        when(mockNode.hasProperty(CmsConstants.RADIEN_CONTENT_TYPE))
                .thenReturn(true);
        Property mockFileSizeProperty = initMockLongProperty(10);
        when(mockNode.getProperty(CmsConstants.RADIEN_FILE_SIZE))
                .thenReturn(mockFileSizeProperty);
        when(mockNode.getNode(JcrConstants.JCR_CONTENT))
                .thenThrow(new PathNotFoundException());

        EnterpriseContent content = contentMappingUtils.convertJCRNode(mockNode);
        validateEnterpriseContent(content, contentTypeDocument);
        assertEquals(10, content.getFileSize());
        assertNull(content.getMimeType());
    }

    @Test
    public void convertJCRNodeGenericNoImageDocumentException() throws RepositoryException, ParseException {
        Calendar calendar = Calendar.getInstance();
        Node mockNode = initGenericMockNode(calendar);
        Property mockContentTypeProperty = initMockStringProperty(contentTypeDocument);
        when(mockNode.getProperty(CmsConstants.RADIEN_CONTENT_TYPE))
                .thenReturn(mockContentTypeProperty);
        when(mockNode.hasProperty(CmsConstants.RADIEN_CONTENT_TYPE))
                .thenReturn(true);
        when(mockNode.getProperty(CmsConstants.RADIEN_FILE_SIZE))
                .thenThrow(new PathNotFoundException());

        EnterpriseContent content = contentMappingUtils.convertJCRNode(mockNode);
        validateEnterpriseContent(content, contentTypeDocument);
        assertEquals(0, content.getFileSize());
        assertNull(content.getMimeType());
    }

    @Test
    public void convertJCRNodeVersionableMandatoryNoImageHTML() throws RepositoryException, ParseException {
        Calendar calendar = Calendar.getInstance();
        Node mockNode = initGenericMockNode(calendar);
        mockNode = initVersionableMockNode(mockNode, calendar);
        mockNode = initMandatoryMockNode(mockNode);

        EnterpriseContent content = contentMappingUtils.convertJCRNode(mockNode);
        validateEnterpriseContent(content, contentTypeHtml);
        validateEnterpriseVersionableMandatoryContent((MandatoryVersionableEnterpriseContent) content);
    }

    @Test
    public void convertJCRNodeMandatoryNoImageHTML() throws RepositoryException, ParseException {
        Calendar calendar = Calendar.getInstance();
        Node mockNode = initGenericMockNode(calendar);
        mockNode = initMandatoryMockNode(mockNode);

        EnterpriseContent content = contentMappingUtils.convertJCRNode(mockNode);
        validateEnterpriseContent(content, contentTypeHtml);
        validateEnterpriseMandatoryContent((MandatoryEnterpriseContent) content);
    }

    @Test
    public void convertJCRNodeVersionableNoImageHTML() throws RepositoryException, ParseException {
        Calendar calendar = Calendar.getInstance();
        Node mockNode = initGenericMockNode(calendar);
        mockNode = initVersionableMockNode(mockNode, calendar);

        EnterpriseContent content = contentMappingUtils.convertJCRNode(mockNode);
        validateEnterpriseContent(content, contentTypeHtml);
        validateVersionableContent((VersionableEnterpriseContent) content);
    }

    @Test
    public void testSyncNodeGenericNoImageHTML() throws NameNotValidException, RepositoryException {
        EnterpriseContent genericContent = new GenericEnterpriseContent(name);
        genericContent.setActive(system);
        genericContent.setSystem(active);
        genericContent.setContentType(ContentType.getByKey(contentTypeHtml));
        genericContent.setLanguage(language);
        genericContent.setHtmlContent(htmlContent);
        Node mockNode = initMockNode("rd:NodeType");
        when(mockNode.getPath())
                .thenReturn(jcrPath);
        Node mockParentNode = initMockNode("rd:NodeType");
        when(mockParentNode.getPath())
                .thenReturn(parentPath);
        when(mockNode.getParent())
                .thenReturn(mockParentNode);

        contentMappingUtils.syncNode(mockNode, genericContent, mock(Session.class));
        assertEquals(jcrPath, mockNode.getPath());
        assertEquals(parentPath, mockNode.getParent().getPath());
    }

    @Test(expected = RepositoryException.class)
    public void testGetFile() throws RepositoryException, IOException {
        Session mockSession = mock(Session.class);
        Node mockNode = initMockNode(JcrConstants.NT_RESOURCE);
        when(mockSession.getNode(anyString()))
                .thenReturn(mockNode);
        contentMappingUtils.getFile(mockSession, "/a/path");
    }

    @Test
    public void testGetFileNoFileNode() throws RepositoryException, IOException {
        Session mockSession = mock(Session.class);
        when(mockSession.getNode(anyString()))
                .thenReturn(null);
        byte[] result = contentMappingUtils.getFile(mockSession, "/a/path");
        assertEquals(0, result.length);
    }

    @Test
    public void testSyncNodeGenericImageHTML() throws NameNotValidException, RepositoryException {
        EnterpriseContent genericContent = new GenericEnterpriseContent(name);
        genericContent.setActive(system);
        genericContent.setSystem(active);
        genericContent.setContentType(ContentType.getByKey(contentTypeHtml));
        genericContent.setLanguage(language);
        genericContent.setHtmlContent(htmlContent);
        genericContent.setImage(ArrayUtils.toPrimitive(Arrays.stream(fileArray.split(","))
                .map(Byte::valueOf).collect(Collectors.toList()).toArray(new Byte[0])));
        genericContent.setImageMimeType("image/png");
        genericContent.setImageName("imageName");
        Node mockNode = initMockNode("rd:NodeType");
        when(mockNode.getPath())
                .thenReturn(jcrPath);
        Node mockParentNode = initMockNode("rd:NodeType");
        when(mockParentNode.getPath())
                .thenReturn(parentPath);
        when(mockNode.getParent())
                .thenReturn(mockParentNode);

        Session mockSession = mock(Session.class);
        ValueFactory mockValueFactory = mock(ValueFactory.class);
        when(mockValueFactory.createBinary(any(InputStream.class)))
                .thenReturn(mock(Binary.class));
        when(mockSession.getValueFactory()).thenReturn(mockValueFactory);
        contentMappingUtils.syncNode(mockNode, genericContent, mockSession);
        assertEquals(jcrPath, mockNode.getPath());
        assertEquals(parentPath, mockNode.getParent().getPath());
    }

    @Test
    public void testSyncNodeGenericNoImageDocument() throws NameNotValidException, RepositoryException {
        EnterpriseContent genericContent = new GenericEnterpriseContent(name);
        genericContent.setActive(system);
        genericContent.setSystem(active);
        genericContent.setContentType(ContentType.getByKey(contentTypeDocument));
        genericContent.setLanguage(language);
        genericContent.setHtmlContent(htmlContent);
        genericContent.setFile(ArrayUtils.toPrimitive(Arrays.stream(fileArray.split(","))
                .map(Byte::valueOf).collect(Collectors.toList()).toArray(new Byte[0])));
        genericContent.setImageMimeType("application/pdf");
        genericContent.setImageName("fileName");
        Node mockNode = initMockNode("rd:NodeType");
        Node mockContentNode = initMockNode(JcrConstants.NT_RESOURCE);
        when(mockNode.getNode(JcrConstants.JCR_CONTENT))
                .thenReturn(mockContentNode);
        when(mockNode.getPath())
                .thenReturn(jcrPath);
        Node mockParentNode = initMockNode("rd:NodeType");
        when(mockParentNode.getPath())
                .thenReturn(parentPath);
        when(mockNode.getParent())
                .thenReturn(mockParentNode);

        Session mockSession = mock(Session.class);
        ValueFactory mockValueFactory = mock(ValueFactory.class);
        when(mockValueFactory.createBinary(any(InputStream.class)))
                .thenReturn(mock(Binary.class));
        when(mockSession.getValueFactory()).thenReturn(mockValueFactory);
        contentMappingUtils.syncNode(mockNode, genericContent, mockSession);
        assertEquals(jcrPath, mockNode.getPath());
        assertEquals(parentPath, mockNode.getParent().getPath());
    }

    @Test
    public void testSyncNodeMandatoryVersionableNoImageHTML() throws NameNotValidException, RepositoryException {
        MandatoryVersionableEnterpriseContent mandatoryVersionableEnterpriseContent = new MandatoryVersionableEnterpriseContent();
        mandatoryVersionableEnterpriseContent.setName(name);
        mandatoryVersionableEnterpriseContent.setActive(system);
        mandatoryVersionableEnterpriseContent.setSystem(active);
        mandatoryVersionableEnterpriseContent.setContentType(ContentType.getByKey(contentTypeHtml));
        mandatoryVersionableEnterpriseContent.setLanguage(language);
        mandatoryVersionableEnterpriseContent.setHtmlContent(htmlContent);
        mandatoryVersionableEnterpriseContent.setVersionable(true);
        mandatoryVersionableEnterpriseContent.setVersionComment(versionComment);
        mandatoryVersionableEnterpriseContent.setValidDate(new Date());
        mandatoryVersionableEnterpriseContent.setMandatoryView(true);
        mandatoryVersionableEnterpriseContent.setMandatoryApproval(true);
        Node mockNode = initMockNode("rd:NodeType");
        when(mockNode.getPath())
                .thenReturn(jcrPath);
        Node mockParentNode = initMockNode("rd:NodeType");
        when(mockParentNode.getPath())
                .thenReturn(parentPath);
        when(mockNode.getParent())
                .thenReturn(mockParentNode);

        contentMappingUtils.syncNode(mockNode, mandatoryVersionableEnterpriseContent, mock(Session.class));
        assertEquals(jcrPath, mockNode.getPath());
        assertEquals(parentPath, mockNode.getParent().getPath());
    }

    @Test
    public void testSyncNodeMandatoryNoImageHTML() throws NameNotValidException, RepositoryException {
        MandatoryEnterpriseContent mandatoryEnterpriseContent = new MandatoryEnterpriseContent();
        mandatoryEnterpriseContent.setName(name);
        mandatoryEnterpriseContent.setActive(system);
        mandatoryEnterpriseContent.setSystem(active);
        mandatoryEnterpriseContent.setContentType(ContentType.getByKey(contentTypeHtml));
        mandatoryEnterpriseContent.setLanguage(language);
        mandatoryEnterpriseContent.setHtmlContent(htmlContent);
        mandatoryEnterpriseContent.setMandatoryView(true);
        mandatoryEnterpriseContent.setMandatoryApproval(true);
        Node mockNode = initMockNode("rd:NodeType");
        when(mockNode.getPath())
                .thenReturn(jcrPath);
        Node mockParentNode = initMockNode("rd:NodeType");
        when(mockParentNode.getPath())
                .thenReturn(parentPath);
        when(mockNode.getParent())
                .thenReturn(mockParentNode);

        contentMappingUtils.syncNode(mockNode, mandatoryEnterpriseContent, mock(Session.class));
        assertEquals(jcrPath, mockNode.getPath());
        assertEquals(parentPath, mockNode.getParent().getPath());
    }

    @Test
    public void testSyncNodeVersionableNoImageHTML() throws NameNotValidException, RepositoryException {
        VersionableEnterpriseContent versionableEnterpriseContent = new VersionableEnterpriseContent();
        versionableEnterpriseContent.setName(name);
        versionableEnterpriseContent.setActive(system);
        versionableEnterpriseContent.setSystem(active);
        versionableEnterpriseContent.setContentType(ContentType.getByKey(contentTypeHtml));
        versionableEnterpriseContent.setLanguage(language);
        versionableEnterpriseContent.setHtmlContent(htmlContent);
        versionableEnterpriseContent.setVersionable(true);
        versionableEnterpriseContent.setVersionComment(versionComment);
        versionableEnterpriseContent.setValidDate(new Date());
        Node mockNode = initMockNode("rd:NodeType");
        when(mockNode.getPath())
                .thenReturn(jcrPath);
        Node mockParentNode = initMockNode("rd:NodeType");
        when(mockParentNode.getPath())
                .thenReturn(parentPath);
        when(mockNode.getParent())
                .thenReturn(mockParentNode);

        contentMappingUtils.syncNode(mockNode, versionableEnterpriseContent, mock(Session.class));
        assertEquals(jcrPath, mockNode.getPath());
        assertEquals(parentPath, mockNode.getParent().getPath());
    }

    @Test
    public void testCreateValid() {
        EnterpriseContent result = contentMappingUtils.create(name, viewID, ContentType.getByKey(contentTypeHtml));
        assertEquals(name, result.getName());
        assertEquals(viewID, result.getViewId());
        assertEquals(contentTypeHtml, result.getContentType().key());
    }

    @Test
    public void testCreateError() {
        EnterpriseContent result = contentMappingUtils.create(null, viewID, ContentType.getByKey(htmlContent));
        assertNull(result);
    }

    private Node initMandatoryMockNode(Node mockNode) throws RepositoryException {
        Property mockMandatoryApproveProperty = initMockBooleanProperty(mandatoryApprove);
        when(mockNode.getProperty(CmsConstants.RADIEN_MANDATORY_APPROVAL))
                .thenReturn(mockMandatoryApproveProperty);
        when(mockNode.hasProperty(CmsConstants.RADIEN_MANDATORY_APPROVAL))
                .thenReturn(true);
        Property mockMandatoryViewProperty = initMockBooleanProperty(mandatoryView);
        when(mockNode.getProperty(CmsConstants.RADIEN_MANDATORY_VIEW))
                .thenReturn(mockMandatoryViewProperty);
        when(mockNode.hasProperty(CmsConstants.RADIEN_MANDATORY_VIEW))
                .thenReturn(true);

        return mockNode;
    }

    private Node initVersionableMockNode(Node mockNode, Calendar calendar) throws RepositoryException {
        when(mockNode.hasProperty(CmsConstants.RADIEN_VERSION))
                .thenReturn(true);
        Property mockVersionProperty = initMockStringProperty(version);
        when(mockNode.getProperty(CmsConstants.RADIEN_VERSION))
                .thenReturn(mockVersionProperty);
        when(mockNode.hasProperty(CmsConstants.RADIEN_VERSION))
                .thenReturn(true);
        Property mockVersionCommentProperty = initMockStringProperty(versionComment);
        when(mockNode.getProperty(CmsConstants.RADIEN_VERSION_COMMENT))
                .thenReturn(mockVersionCommentProperty);
        when(mockNode.hasProperty(CmsConstants.RADIEN_VERSION_COMMENT))
                .thenReturn(true);
        Property mockValidDateProperty = initMockDateProperty(calendar);
        when(mockNode.getProperty(CmsConstants.RADIEN_VALID_DATE))
                .thenReturn(mockValidDateProperty);
        when(mockNode.hasProperty(CmsConstants.RADIEN_VALID_DATE))
                .thenReturn(true);

        return mockNode;
    }

    private Node initGenericMockNode(Calendar calendar) throws RepositoryException {
        Node mockNode = initMockNode("rd:NodeType");
        when(mockNode.hasProperty(CmsConstants.RADIEN_VERSION))
                .thenReturn(false);
        when(mockNode.hasProperty(CmsConstants.RADIEN_MANDATORY_VIEW))
                .thenReturn(false);
        Property mockNameProperty = initMockStringProperty(name);
        when(mockNode.hasProperty(CmsConstants.RADIEN_NAME))
                .thenReturn(true);
        when(mockNode.getProperty(CmsConstants.RADIEN_NAME))
                .thenReturn(mockNameProperty);
        when(mockNode.getName())
                .thenReturn(name);
        Property mockViewIDProperty = initMockStringProperty(viewID);
        when(mockNode.getProperty(CmsConstants.RADIEN_VIEW_ID))
                .thenReturn(mockViewIDProperty);
        when(mockNode.hasProperty(CmsConstants.RADIEN_VIEW_ID))
                .thenReturn(true);
        Property mockActiveProperty = initMockBooleanProperty(active);
        when(mockNode.getProperty(CmsConstants.RADIEN_ACTIVE))
                .thenReturn(mockActiveProperty);
        when(mockNode.hasProperty(CmsConstants.RADIEN_ACTIVE))
                .thenReturn(true);
        Property mockSystemProperty = initMockBooleanProperty(system);
        when(mockNode.getProperty(CmsConstants.RADIEN_SYSTEM))
                .thenReturn(mockSystemProperty);
        when(mockNode.hasProperty(CmsConstants.RADIEN_SYSTEM))
                .thenReturn(true);
        when(mockNode.getPath())
                .thenReturn(jcrPath);
        Node mockParentNode = initMockNode("rd:NodeType");
        when(mockParentNode.getPath())
                .thenReturn(parentPath);
        when(mockNode.getParent())
                .thenReturn(mockParentNode);
        Property mockContentTypeProperty = initMockStringProperty(contentTypeHtml);
        when(mockNode.getProperty(CmsConstants.RADIEN_CONTENT_TYPE))
                .thenReturn(mockContentTypeProperty);
        when(mockNode.hasProperty(CmsConstants.RADIEN_CONTENT_TYPE))
                .thenReturn(true);
        Property mockLanguageProperty = initMockStringProperty(language);
        when(mockNode.getProperty(CmsConstants.RADIEN_CONTENT_LANG))
                .thenReturn(mockLanguageProperty);
        when(mockNode.hasProperty(CmsConstants.RADIEN_CONTENT_LANG))
                .thenReturn(true);
        Property mockCreateProperty = initMockDateProperty(calendar);
        when(mockNode.getProperty(JcrConstants.JCR_CREATED))
                .thenReturn(mockCreateProperty);
        when(mockNode.hasProperty(JcrConstants.JCR_CREATED))
                .thenReturn(true);
        Property mockHtmlContentProperty = initMockStringProperty(htmlContent);
        when(mockNode.getProperty(CmsConstants.RADIEN_HTML_CONTENT))
                .thenReturn(mockHtmlContentProperty);
        when(mockNode.hasProperty(CmsConstants.RADIEN_HTML_CONTENT))
                .thenReturn(true);
        return mockNode;
    }

    private void validateEnterpriseContent(EnterpriseContent content, String contentType) throws ParseException {
        assertEquals(viewID, content.getViewId());
        assertEquals(htmlContent, content.getHtmlContent());
        assertEquals(name, content.getName());
        assertEquals(contentType, content.getContentType().key());
        assertEquals(language, content.getLanguage());
        assertEquals(active, content.isActive());
        assertEquals(system, content.isSystem());
        assertEquals(parentPath, content.getParentPath());
        assertEquals(jcrPath, content.getJcrPath());
    }

    private void validateEnterpriseMandatoryContent(MandatoryEnterpriseContent content) {
        assertEquals(mandatoryApprove, content.isMandatoryApproval());
        assertEquals(mandatoryView, content.isMandatoryView());
    }

    private void validateVersionableContent(VersionableEnterpriseContent content) throws ParseException {
        assertEquals(versionable, content.isVersionable());
        assertEquals(versionComment, content.getVersionComment());
        assertEquals(version, content.getVersion().getVersion());
    }

    private void validateEnterpriseVersionableMandatoryContent(MandatoryVersionableEnterpriseContent content) throws ParseException {
        assertEquals(versionable, content.isVersionable());
        assertEquals(versionComment, content.getVersionComment());
        assertEquals(version, content.getVersion().getVersion());
        assertEquals(mandatoryApprove, content.isMandatoryApproval());
        assertEquals(mandatoryView, content.isMandatoryView());
    }

    private Property initMockStringProperty(String value) throws RepositoryException {
        Property mockProperty = mock(Property.class);
        when(mockProperty.getString()).thenReturn(value);
        return mockProperty;
    }

    private Property initMockBooleanProperty(Boolean value) throws RepositoryException {
        Property mockProperty = mock(Property.class);
        when(mockProperty.getBoolean()).thenReturn(value);
        return mockProperty;
    }

    private Property initMockDateProperty(Calendar calendar) throws RepositoryException {
        Property mockProperty = mock(Property.class);
        when(mockProperty.getDate()).thenReturn(calendar);
        return mockProperty;
    }

    private Property initMockLongProperty(long value) throws RepositoryException {
        Property mockProperty = mock(Property.class);
        when(mockProperty.getLong()).thenReturn(value);
        return mockProperty;
    }

    private Node initMockNode(String t) throws RepositoryException {
        Node mockNode = mock(Node.class);
        NodeType mockNodeType = mock(NodeType.class);
        when(mockNode.getPrimaryNodeType())
                .thenReturn(mockNodeType);
        when(mockNodeType.getName())
                .thenReturn(t);
        return mockNode;
    }
}
