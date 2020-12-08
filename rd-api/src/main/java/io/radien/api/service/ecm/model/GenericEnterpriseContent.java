/*
 * Copyright (c) 2006-present openappframe.org & its legal owners. All rights reserved.
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
package io.radien.api.service.ecm.model;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.radien.api.service.ecm.exception.NameNotValidException;

/**
 * @author Marco Weiland
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "viewId", "language", "name", "htmlContent" })
@JsonDeserialize(as = GenericEnterpriseContent.class)
public class GenericEnterpriseContent extends AbstractECMModel implements EnterpriseContent {

    private static final long serialVersionUID = 3198590546640263794L;
    private Long id;
    @JsonProperty("viewId")
    @NotNull
    private String viewId;
    @JsonProperty("language")
    private String language = Locale.getDefault().getLanguage();
    @JsonProperty("name")
    @NotNull
    @Size(min = 3)
    private String name;
    @JsonProperty("htmlContent")
    private String htmlContent;
    @JsonProperty("contentType")
    private ContentType contentType = ContentType.HTML;
    @JsonProperty("author")
    private String author;
    @JsonProperty("active")
    private boolean active = true;
    @JsonProperty("externalPublic")
    private boolean externalPublic = false;
    @JsonProperty("permissions")
    private String permissions;

    @JsonProperty("app")
    private String app;

    @JsonProperty("image")
    private transient byte[] image;
    @JsonProperty("imageName")
    private String imageName;
    @JsonProperty("imageMimeType")
    private String imageMimeType;
    @JsonProperty("fileStream")
    private transient byte[] fileStream;
    @JsonProperty("mimeType")
    private String mimeType;
    @JsonProperty("fileSize")
    private long fileSize;
    @JsonProperty("system")
    private boolean system = true;
    @JsonProperty("tags")
    private List<String> tags;
    @JsonProperty("lastEditDate")
    private Date lastEditDate;

    private String jcrPath;
    private String parentPath;


    public GenericEnterpriseContent() {
        this.viewId = UUID.randomUUID().toString();
        this.name = "";
    }

    public GenericEnterpriseContent(String name) throws NameNotValidException {
        this.viewId = UUID.randomUUID().toString();
        this.name = name;
        if (name == null) {
            throw new NameNotValidException("name is null");
        }
    }

    @Override
    public int compareTo(EnterpriseContent o) {
        return this.getName().compareTo(o.getName());
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String viewId() {
        return viewId;
    }

    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @JsonIgnore
    public String getViewIdLanguage() {
        return getViewId()+"_"+ getLanguage();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }
    
    public String getApp() {
        return app;
    }
    
    public void setApp(String app) {
        this.app = app;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] inputStream) {
        this.image = inputStream;
    }

    public byte[] getFile() {
        return fileStream;
    }

    public void setFile(byte[] fileStream) {
        this.fileStream = fileStream;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public String getPermissions() {
        return permissions;
    }

    @Override
    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    @Override
    public String getImageName() {
        return imageName;
    }

    @Override
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    @Override
    public String getImageMimeType() {
        return imageMimeType;
    }

    @Override
    public void setImageMimeType(String imageMimeType) {
        this.imageMimeType = imageMimeType;
    }

    @Override
    public String getJcrPath() {
        return jcrPath;
    }

    @Override
    public void setJcrPath(String jcrPath) {
        this.jcrPath = jcrPath;
    }

    @Override
    public String getParentPath() {
        return parentPath;
    }

    @Override
    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

}
