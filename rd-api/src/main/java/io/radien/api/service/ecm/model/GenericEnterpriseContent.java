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
 * Generic Enterprise Content Manager class
 *
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

    /**
     * ECM Generic content empty constructor
     */
    public GenericEnterpriseContent() {
        this.viewId = UUID.randomUUID().toString();
        this.name = "";
    }

    /**
     * ECM Generic content named information constructor
     * @param name for the generic ecm content
     * @throws NameNotValidException
     */
    public GenericEnterpriseContent(String name) throws NameNotValidException {
        this.viewId = UUID.randomUUID().toString();
        this.name = name;
        if (name == null) {
            throw new NameNotValidException("name is null");
        }
    }

    /**
     * Compares this object with the specified object for order. Returns a negative integer, zero,
     * or a positive integer as this object is less than, equal to, or greater than the specified object.
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(EnterpriseContent o) {
        return this.getName().compareTo(o.getName());
    }

    /**
     * This object (which is already a string!) is itself returned.
     * @return he string itself.
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Generic ECM id getter
     * @return the generic ecm id
     */
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Generic ecm id setter
     * @param id to be set
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Generic ECM view id getter
     * @return the generic ecm view id
     */
    public String viewId() {
        return viewId;
    }

    /**
     * Generic ECM view id getter
     * @return the generic ecm view id
     */
    public String getViewId() {
        return viewId;
    }

    /**
     * Generic ecm view id setter
     * @param viewId to be set
     */
    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    /**
     * Generic ECM language getter
     * @return the generic ecm language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Generic ecm language setter
     * @param language to be set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Generic ECM view id language getter
     * @return the generic ecm view id language
     */
    @JsonIgnore
    public String getViewIdLanguage() {
        return getViewId()+"_"+ getLanguage();
    }

    /**
     * Generic ECM name getter
     * @return the generic ecm name
     */
    public String getName() {
        return name;
    }

    /**
     * Generic ecm name setter
     * @param name to be set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Generic ECM HTML content getter
     * @return the generic ecm html content
     */
    public String getHtmlContent() {
        return htmlContent;
    }

    /**
     * Generic ecm html content setter
     * @param htmlContent to be set
     */
    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    /**
     * Generic ECM content type getter
     * @return the generic ecm content type
     */
    public ContentType getContentType() {
        return contentType;
    }

    /**
     * Generic content type setter
     * @param contentType to be set
     */
    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    /**
     * Generic ECM app getter
     * @return the generic ecm app
     */
    public String getApp() {
        return app;
    }

    /**
     * Generic ecm app setter
     * @param app to be set
     */
    public void setApp(String app) {
        this.app = app;
    }

    /**
     * Generic ECM author getter
     * @return the generic ecm author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Generic ecm author setter
     * @param author to be set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Generic ECM is active getter
     * @return true if generic ecm is active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Generic ecm is active setter
     * @param active to be set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Generic ECM image getter
     * @return the generic ecm image
     */
    public byte[] getImage() {
        return image;
    }

    /**
     * Generic ecm image setter
     * @param inputStream to be set
     */
    public void setImage(byte[] inputStream) {
        this.image = inputStream;
    }

    /**
     * Generic ECM file getter
     * @return the generic ecm file
     */
    public byte[] getFile() {
        return fileStream;
    }

    /**
     * Generic ecm file setter
     * @param fileStream to be set
     */
    public void setFile(byte[] fileStream) {
        this.fileStream = fileStream;
    }

    /**
     * Generic ECM mime type getter
     * @return the generic ecm mime type
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Generic ECM Mime type setter
     * @param mimeType to be set
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Generic ECM file size getter
     * @return the generic ecm file size
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * Generic ecm file size setter
     * @param fileSize to be set
     */
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * Validator of the generic ecm is system
     * @return true if generic ecm is system
     */
    public boolean isSystem() {
        return system;
    }

    /**
     * Generic ecm is system setter
     * @param system to be set
     */
    public void setSystem(boolean system) {
        this.system = system;
    }

    /**
     * Generic ECM tags getter
     * @return the generic ecm tags
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Generic ECM tags setter
     * @param tags to be set
     */
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     * Generic ECM permissions getter
     * @return the generic ecm permissions
     */
    @Override
    public String getPermissions() {
        return permissions;
    }

    /**
     * Generic ECM permission setter
     * @param permissions to be set
     */
    @Override
    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    /**
     * Generic ECM image name getter
     * @return the generic ecm image name
     */
    @Override
    public String getImageName() {
        return imageName;
    }

    /**
     * Generic ECM image name setter
     * @param imageName to be set
     */
    @Override
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    /**
     * Generic ECM image mime type getter
     * @return the generic ecm image mime type
     */
    @Override
    public String getImageMimeType() {
        return imageMimeType;
    }

    /**
     * Generic ecm image mime type setter
     * @param imageMimeType to be set
     */
    @Override
    public void setImageMimeType(String imageMimeType) {
        this.imageMimeType = imageMimeType;
    }

    /**
     * Generic ECM JCR path getter
     * @return the generic ecm JCR path
     */
    @Override
    public String getJcrPath() {
        return jcrPath;
    }

    /**
     * Generic ECM JCR path setter
     * @param jcrPath to be set
     */
    @Override
    public void setJcrPath(String jcrPath) {
        this.jcrPath = jcrPath;
    }

    /**
     * Generic ECM parent path getter
     * @return the generic ecm parent path
     */
    @Override
    public String getParentPath() {
        return parentPath;
    }

    /**
     * Generic parent path setter
     * @param parentPath to be set
     */
    @Override
    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

}
