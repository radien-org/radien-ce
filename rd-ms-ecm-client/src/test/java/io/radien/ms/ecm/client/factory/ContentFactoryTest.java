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

package io.radien.ms.ecm.client.factory;

import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.ecm.model.MandatoryEnterpriseContent;
import io.radien.api.service.ecm.model.MandatoryVersionableEnterpriseContent;
import io.radien.api.service.ecm.model.VersionableEnterpriseContent;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ContentFactoryTest {

    private final static String viewID = "viewId";
    private final static String htmlContent = "htmlContent";
    private final static String name = "contentName";
    private final static String contentTypeDocument = "document";
    private final static String contentTypeHtml = "html";
    private final static String language = "en";
    private final static Boolean active = Boolean.TRUE;
    private final static Boolean system = Boolean.TRUE;
    private final static String parentPath = "/a/parent/path";
    private final static String jcrPath = "/a/parent/path/file";
    private final static String createDate = "2020-10-10";
    private final static Boolean versionable = Boolean.TRUE;
    private final static String versionComment = "versionComment";
    private final static String validDate = "2020-10-10";
    private final static String version = "1.1.0";
    private final static String majorVersion = "1";
    private final static String minorVersion = "1";
    private final static String hotfixVersion = "0";
    private final static Boolean mandatoryView = Boolean.TRUE;
    private final static Boolean mandatoryApprove = Boolean.TRUE;
    private final static Boolean externalPublic = Boolean.TRUE;
    private final static String permissions = null;
    private final static String file = "[101,120,97,109,112,108,101]";
    private final static long fileSize = 7;


    @Test
    public void testConvertJSONObjectNoFileNoVersion() throws IOException, ParseException {
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

        EnterpriseContent content = ContentFactory.convertJSONObject(object);
        validateEnterpriseContent(content, contentTypeHtml);
    }

    @Test
    public void testConvertJSONObjectFileNoVersion() throws IOException, ParseException {
        JSONObject object = new JSONObject();
        object.put("viewId", viewID);
        object.put("htmlContent", htmlContent);
        object.put("name", name);
        object.put("contentType", contentTypeDocument);
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
        object.put("file", file);

        EnterpriseContent content = ContentFactory.convertJSONObject(object);
        validateEnterpriseContent(content, contentTypeDocument);
        assertEquals(file, Arrays.toString(content.getFile()).replaceAll("\\s+", ""));
        assertEquals(fileSize, content.getFileSize());
    }

    @Test
    public void testConvertJSONObjectImageNoVersion() throws IOException, ParseException {
        JSONObject object = new JSONObject();
        object.put("viewId", viewID);
        object.put("htmlContent", htmlContent);
        object.put("name", name);
        object.put("contentType", contentTypeDocument);
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
        object.put("image", file);
        object.put("imageName", "imageName");
        object.put("imageMimeType", "image/png");

        EnterpriseContent content = ContentFactory.convertJSONObject(object);
        validateEnterpriseContent(content, contentTypeDocument);
        assertEquals(file, Arrays.toString(content.getImage()).replaceAll("\\s+", ""));
        assertEquals("imageName", content.getImageName());
        assertEquals("image/png", content.getImageMimeType());
    }

    @Test
    public void testConvertJSONObjectNoFileVersion() throws IOException, ParseException {
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
        object.put("versionable", versionable);
        object.put("versionComment", versionComment);
        object.put("validDate", validDate);
        JSONObject versionObject = new JSONObject();
        versionObject.put("majorVersion", majorVersion);
        versionObject.put("minorVersion", minorVersion);
        versionObject.put("hotfixVersion", hotfixVersion);
        versionObject.put("version", version);
        object.put("version", versionObject);

        EnterpriseContent content = ContentFactory.convertJSONObject(object);
        validateEnterpriseContent(content, contentTypeHtml);
        validateVersionableContent((VersionableEnterpriseContent) content);
    }

    @Test
    public void testConvertJSONObjectNoFilMandatory() throws IOException, ParseException {
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
        object.put("mandatoryApprove", mandatoryApprove);
        object.put("mandatoryView", mandatoryView);

        EnterpriseContent content = ContentFactory.convertJSONObject(object);
        validateEnterpriseContent(content, contentTypeHtml);
        validateEnterpriseMandatoryContent((MandatoryEnterpriseContent) content);
    }

    @Test
    public void testConvertJSONObjectNoFileVersionMandatory() throws IOException, ParseException {
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
        object.put("versionable", versionable);
        object.put("versionComment", versionComment);
        object.put("validDate", validDate);
        JSONObject versionObject = new JSONObject();
        versionObject.put("majorVersion", majorVersion);
        versionObject.put("minorVersion", minorVersion);
        versionObject.put("hotfixVersion", hotfixVersion);
        versionObject.put("version", version);
        object.put("version", versionObject);
        object.put("mandatoryApprove", mandatoryApprove);
        object.put("mandatoryView", mandatoryView);

        EnterpriseContent content = ContentFactory.convertJSONObject(object);
        validateEnterpriseContent(content, contentTypeHtml);
        validateEnterpriseVersionableMandatoryContent((MandatoryVersionableEnterpriseContent) content);

    }

    private void validateEnterpriseVersionableMandatoryContent(MandatoryVersionableEnterpriseContent content) throws ParseException {
        assertEquals(versionable, content.isVersionable());
        assertEquals(versionComment, content.getVersionComment());
        assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse(validDate), content.getValidDate());
        assertEquals(version, content.getVersion().getVersion());
        assertEquals(mandatoryApprove, content.isMandatoryApproval());
        assertEquals(mandatoryView, content.isMandatoryView());
    }

    private void validateEnterpriseMandatoryContent(MandatoryEnterpriseContent content) {
        assertEquals(mandatoryApprove, content.isMandatoryApproval());
        assertEquals(mandatoryView, content.isMandatoryView());
    }

    private void validateVersionableContent(VersionableEnterpriseContent content) throws ParseException {
        assertEquals(versionable, content.isVersionable());
        assertEquals(versionComment, content.getVersionComment());
        assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse(validDate), content.getValidDate());
        assertEquals(version, content.getVersion().getVersion());
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
        assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse(createDate), content.getCreateDate());
    }
}
