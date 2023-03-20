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

import io.radien.api.service.ecm.model.Content;
import io.radien.api.service.ecm.model.ContentType;
import io.radien.api.service.ecm.model.ContentVersion;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.ecm.model.MandatoryEnterpriseContent;
import io.radien.api.service.ecm.model.MandatoryVersionableEnterpriseContent;
import io.radien.api.service.ecm.model.VersionableEnterpriseContent;
import io.radien.ms.ecm.client.util.TextUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentFactory {
    private static final Logger log = LoggerFactory.getLogger(ContentFactory.class);

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String VERSION_JSON_KEY = "version";

    private ContentFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static EnterpriseContent convertJSONObject(JSONObject json) throws ParseException {
        String viewId = tryGetJsonProperty(json, "viewId");
        String htmlContent = tryGetJsonProperty(json, "htmlContent");
        String name = tryGetJsonProperty(json, "name");
        String contentType = tryGetJsonProperty(json, "contentType");
        String language = tryGetJsonProperty(json, "language");
        String active = tryGetJsonProperty(json, "active");
        String system = tryGetJsonProperty(json, "system");
        String parentPath = tryGetJsonProperty(json, "parentPath");

        String mimeType = tryGetJsonProperty(json, "mimeType");
        String fileSize = tryGetJsonProperty(json, "fileSize");
        String jcrPath = tryGetJsonProperty(json, "jcrPath");
        String createDate = tryGetJsonProperty(json, "createDate");
        String externalPublic =  tryGetJsonProperty(json, "externalPublic");
        //Versionable Properties
        String versionable = tryGetJsonProperty(json, "versionable");
        String versionComment = tryGetJsonProperty(json, "versionComment");
        String validDate = tryGetJsonProperty(json, "validDate");
        String version = null;
        //Mandatory Properties
        String mandatoryApprove = tryGetJsonProperty(json, "mandatoryApprove");
        String mandatoryView = tryGetJsonProperty(json, "mandatoryView");

        if(Boolean.parseBoolean(versionable) && json.containsKey(VERSION_JSON_KEY)) {
            Object versionValue = json.get(VERSION_JSON_KEY);
            if(versionValue instanceof JSONObject) {
                version = ((JSONObject) json.get(VERSION_JSON_KEY)).get(VERSION_JSON_KEY).toString();
            } else {
                version = json.get(VERSION_JSON_KEY).toString();
            }
        }

        String permissions =  tryGetJsonProperty(json, "permissions");
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

        EnterpriseContent content = createEnterpriseContent(htmlContent, name, versionable,
                versionComment, validDate, version, mandatoryApprove, mandatoryView);
        content.setViewId(viewId);
        content.setContentType(ContentType.getByKey(contentType));
        content.setJcrPath(jcrPath);
        content.setActive(Boolean.parseBoolean(active));
        content.setSystem(Boolean.parseBoolean(system));
        content.setLanguage(language);
        content.setTags(tags);
        content.setMimeType(mimeType);
        content.setFileSize(fileSize != null ? Long.parseLong(fileSize) : 0L);
        content.setParentPath(parentPath);
        content.setExternalPublic(Boolean.parseBoolean(externalPublic));
        content.setPermissions(permissions);
        content.setCreateDate(createDate == null ? null :
                new SimpleDateFormat(DATE_FORMAT).parse(createDate));

        if (contentType != null && contentType.equalsIgnoreCase(ContentType.DOCUMENT.key())) {
            getFileResource(json, content);
        }
        getImageResource(json, content);
        return content;
    }

    private static EnterpriseContent createEnterpriseContent(String htmlContent, String name, String versionable,
                                                             String versionComment, String validDate, String version,
                                                             String mandatoryApprove, String mandatoryView) throws ParseException {
        EnterpriseContent content = null;
        if(versionable != null && mandatoryApprove != null) {
            content = createVersionableMandatoryContent(htmlContent, name, versionable, versionComment, validDate,
                    version, mandatoryApprove, mandatoryView);
        } else if(versionable != null) {
            content = createVersionableContent(htmlContent, name, versionable, versionComment, validDate, version);
        } else if(mandatoryApprove != null) {
            content = createMandatoryContent(htmlContent, name, mandatoryApprove, mandatoryView);
        } else {
            content = new Content(TextUtil.escapeIllegalJcrChars(name), htmlContent);
        }
        return content;
    }

    private static EnterpriseContent createMandatoryContent(String htmlContent, String name, String mandatoryApprove,
                                                            String mandatoryView) {
        EnterpriseContent content;
        content = new MandatoryEnterpriseContent();
        content.setName(name);
        content.setHtmlContent(htmlContent);
        ((MandatoryEnterpriseContent)content).setMandatoryView(Boolean.parseBoolean(mandatoryView));
        ((MandatoryEnterpriseContent)content).setMandatoryApproval(Boolean.parseBoolean(mandatoryApprove));
        return content;
    }

    private static EnterpriseContent createVersionableContent(String htmlContent, String name, String versionable,
                                                              String versionComment, String validDate, String version) throws ParseException {
        EnterpriseContent content;
        content = new VersionableEnterpriseContent();
        content.setName(name);
        content.setHtmlContent(htmlContent);
        ((VersionableEnterpriseContent)content).setVersionable(Boolean.parseBoolean(versionable));
        ((VersionableEnterpriseContent)content).setVersionComment(versionComment);
        ((VersionableEnterpriseContent)content).setValidDate(new SimpleDateFormat(DATE_FORMAT).parse(validDate));
        if(version != null) {
            ((VersionableEnterpriseContent)content).setVersion(new ContentVersion(version));
        }
        return content;
    }

    private static EnterpriseContent createVersionableMandatoryContent(String htmlContent, String name, String versionable,
                                                                       String versionComment, String validDate, String version,
                                                                       String mandatoryApprove, String mandatoryView) throws ParseException {
        EnterpriseContent content;
        content = new MandatoryVersionableEnterpriseContent();
        content.setName(name);
        content.setHtmlContent(htmlContent);
        ((MandatoryVersionableEnterpriseContent)content).setVersionable(Boolean.parseBoolean(versionable));
        ((MandatoryVersionableEnterpriseContent)content).setVersionComment(versionComment);
        ((MandatoryVersionableEnterpriseContent)content).setValidDate(new SimpleDateFormat(DATE_FORMAT).parse(validDate));
        ((MandatoryVersionableEnterpriseContent)content).setMandatoryView(Boolean.parseBoolean(mandatoryView));
        ((MandatoryVersionableEnterpriseContent)content).setMandatoryApproval(Boolean.parseBoolean(mandatoryApprove));
        if(version != null) {
            ((MandatoryVersionableEnterpriseContent)content).setVersion(new ContentVersion(version));
        }
        return content;
    }

    private static void getFileResource(JSONObject json, EnterpriseContent content) {
        try {
            String fileString =  json.get("file") == null ? null : json.get("file").toString();

            if(fileString != null && fileString.contains(",")) {
                String file = fileString.substring(1, fileString.length() - 1);
                List<Byte> listaDeString = Arrays.stream(file.split(",")).map(Byte::valueOf).collect(Collectors.toList());
                Byte[] bytes = listaDeString.toArray(new Byte[listaDeString.size()]);
                byte[] bArray = ArrayUtils.toPrimitive(bytes);
                content.setFileSize(bArray.length);
                content.setFile(bArray);
            }
        } catch (Exception e) {
            log.warn("Error converting json object", e);
        }
    }

    private static void getImageResource(JSONObject json, EnterpriseContent content) {
        try {
            String imageString =  json.get("image") == null ? null : json.get("image").toString();

            if(imageString != null && imageString.contains(",")) {
                String file = imageString.substring(1, imageString.length() - 1);
                List<Byte> stringList = Arrays.stream(file.split(",")).map(Byte::valueOf).collect(Collectors.toList());
                Byte[] bytes = stringList.toArray(new Byte[stringList.size()]);
                byte[] bArray = ArrayUtils.toPrimitive(bytes);
                content.setImageMimeType(tryGetJsonProperty(json, "imageMimeType"));
                content.setImageName(tryGetJsonProperty(json, "imageName"));
                content.setImage(bArray);
            }
        } catch (Exception e) {
            log.warn("Error converting json object", e);
        }
    }

    private static String tryGetJsonProperty(JSONObject object, String key) {
        Object value = object.get(key);
        return value != null ? value.toString() : null;
    }
}
