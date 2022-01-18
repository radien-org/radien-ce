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
import java.io.IOException;
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

    public static EnterpriseContent convertJSONObject(JSONObject json) throws IOException, ParseException {
        String viewId = json.get("viewId") == null ? null : json.get("viewId").toString();
        String htmlContent =  json.get("htmlContent") == null ? null : json.get("htmlContent").toString();
        String name =  json.get("name") == null ? null : json.get("name").toString();
        String contentType =  json.get("contentType") == null ? null : json.get("contentType").toString();
        String language =  json.get("language") == null ? null : json.get("language").toString();
        String active =  json.get("active") == null ? null : json.get("active").toString();
        String system =  json.get("system") == null ? null : json.get("system").toString();
        String parentPath =  json.get("parentPath") == null ? null : json.get("parentPath").toString();

        String jcrPath = null;
        String createDate = null;
        //Versionable Properties
        String versionable = null;
        String versionComment = null;
        String validDate = null;
        String version = null;

        //Mandatory Properties
        String mandatoryApprove = null;
        String mandatoryView = null;

        if(json.containsKey("jcrPath")) {
            jcrPath = json.get("jcrPath").toString();
        }
        if(json.containsKey("createDate")) {
            createDate = json.get("createDate").toString();
        }
        if(json.containsKey("versionable")) {
            versionable =  json.get("versionable").toString();
            versionComment =  json.get("versionComment").toString();
            validDate =  json.get("validDate").toString();
            if(json.containsKey("version")) {
                if(json.get("version") instanceof JSONObject) {
                    version = ((JSONObject) json.get("version")).get("version").toString();
                } else {
                    version = json.get("version").toString();
                }
            }
        }
        if(json.containsKey("mandatoryApprove")) {
            mandatoryApprove =  json.get("mandatoryApprove").toString();
            mandatoryView =  json.get("mandatoryView").toString();
        }
        String externalPublic =  json.get("externalPublic") == null ? null : json.get("externalPublic").toString();
        String permissions =  (String) json.get("permissions");
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
        if(versionable != null && mandatoryApprove != null) {
            content = new MandatoryVersionableEnterpriseContent();
            content.setName(name);
            content.setHtmlContent(htmlContent);
            ((MandatoryVersionableEnterpriseContent)content).setVersionable(Boolean.parseBoolean(versionable));
            ((MandatoryVersionableEnterpriseContent)content).setVersionComment(versionComment);
            ((MandatoryVersionableEnterpriseContent)content).setValidDate(new SimpleDateFormat("yyyy-MM-dd").parse(validDate));
            ((MandatoryVersionableEnterpriseContent)content).setMandatoryView(Boolean.parseBoolean(mandatoryView));
            ((MandatoryVersionableEnterpriseContent)content).setMandatoryApproval(Boolean.parseBoolean(mandatoryApprove));
            if(version != null) {
                ((MandatoryVersionableEnterpriseContent)content).setVersion(new ContentVersion(version));
            }
        } else if(versionable != null) {
            content = new VersionableEnterpriseContent();
            content.setName(name);
            content.setHtmlContent(htmlContent);
            ((VersionableEnterpriseContent)content).setVersionable(Boolean.parseBoolean(versionable));
            ((VersionableEnterpriseContent)content).setVersionComment(versionComment);
            ((VersionableEnterpriseContent)content).setValidDate(new SimpleDateFormat("yyyy-MM-dd").parse(validDate));
            if(version != null) {
                ((VersionableEnterpriseContent)content).setVersion(new ContentVersion(version));
            }
        } else if(mandatoryApprove != null) {
            content = new MandatoryEnterpriseContent();
            content.setName(name);
            content.setHtmlContent(htmlContent);
            ((MandatoryEnterpriseContent)content).setMandatoryView(Boolean.parseBoolean(mandatoryView));
            ((MandatoryEnterpriseContent)content).setMandatoryApproval(Boolean.parseBoolean(mandatoryApprove));
        } else {
            content = new Content(TextUtil.escapeIllegalJcrChars(name), htmlContent);
        }
        content.setViewId(viewId);
        content.setContentType(ContentType.getByKey(contentType));
        content.setJcrPath(jcrPath);
        content.setActive(Boolean.parseBoolean(active));
        content.setSystem(Boolean.parseBoolean(system));
        content.setLanguage(language);
        content.setTags(tags);
        content.setParentPath(parentPath);
        content.setExternalPublic(Boolean.parseBoolean(externalPublic));
        content.setPermissions(permissions);
        content.setCreateDate(createDate == null ? null :
                new SimpleDateFormat("yyyy-MM-dd").parse(createDate));

        if (contentType.equalsIgnoreCase(ContentType.DOCUMENT.key())) {
            getFileResource(json, content);
        }
        return content;
    }

    private static void getFileResource(JSONObject json, EnterpriseContent content) throws IOException {
        try {
            String fileString =  json.get("file") == null ? null : json.get("file").toString();

            if(fileString != null && !fileString.isEmpty()) {
                if(fileString.contains(",")) {
                    String file = fileString.substring(1, fileString.length() - 1);
                    List<Byte> listaDeString = Arrays.stream(file.split(",")).map(Byte::valueOf).collect(Collectors.toList());
                    Byte[] bytes = listaDeString.toArray(new Byte[listaDeString.size()]);
                    byte[] bArray = ArrayUtils.toPrimitive(bytes);
                    content.setFileSize(bArray.length);
                    content.setFile(bArray);
                }
            }
        } catch (Exception e) {
            log.warn("Error converting json object", e);
        }
    }
}
