/*
 * Copyright (c) 2006-present radien GmbH & its legal owners. All rights reserved.
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

import io.radien.api.model.AbstractModel;

/**
 * Abstract class for the Enterprise Content Manager
 *
 * @author Marco Weiland
 */
public abstract class AbstractECMModel extends AbstractModel {

	private static final long serialVersionUID = -4165151447731450930L;
	protected String viewId = "";
	protected String language = Locale.getDefault().getLanguage();
	protected String name = "";
	protected String htmlContent = "";
	protected ContentType contentType = ContentType.HTML;
	protected String author;
	private Date lastEditDate;
	protected boolean active = true;
	private String permissions = "";

	private String app;

	protected transient byte[] image;
	protected transient byte[] fileStream;
	protected String mimeType;
	protected long fileSize;
	private List<String> tags;
	protected boolean system = false;
	private String imageMimeType;
	private String imageName;
	private String jcrPath;
	private String parentPath;

	private boolean versionable;
	private String versionComment;
	private Date validDate;
	private SystemContentVersion version;
	private String versionableName;
	private boolean updateOnLaunch;

	/**
	 * ECM View Id getter
	 * @return ecm view id
	 */
	public String viewId() {
		return viewId;
	}

	/**
	 * ECM View Id getter
	 * @return ecm view id
	 */
	public String getViewId() {
		return viewId;
	}

	/**
	 * ECM View id setter
	 * @param viewId to be set
	 */
	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	/**
	 * ECM language getter
	 * @return ecm requested language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * ECM language setter
	 * @param language to be set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * ECM name getter
	 * @return ecm requested name
	 */
	public String getName() {
		return name;
	}

	/**
	 * ECM Name setter
	 * @param name to be set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * ECM HTML content getter
	 * @return ecm hetml content
	 */
	public String getHtmlContent() {
		return htmlContent;
	}

	/**
	 * ECM HTML Content setter
	 * @param htmlContent to be set
	 */
	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	/**
	 * ECM content type getter
	 * @return ecm content type
	 */
	public ContentType getContentType() {
		return contentType;
	}

	/**
	 * ECM content type setter
	 * @param contentType to be set
	 */
	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

	/**
	 * ECM App getter
	 * @return ecm app
	 */
	public String getApp() {
	    return app;
	}

	/**
	 * ECM App setter
	 * @param app to be set
	 */
	public void setApp(String app) {
	    this.app = app;
	}

	/**
	 * ECM Author getter
	 * @return ecm author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * ECM Author setter
	 * @param author to be set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * ECM LastEditDate getter
	 * @return ecm lastEditDate
	 */
	public Date getLastEditDate() {
		return lastEditDate;
	}

	/**
	 * ECM LastEditDate setter
	 * @param lastEditDate to be set
	 */
	public void setLastEditDate(Date lastEditDate) {
		this.lastEditDate = lastEditDate;
	}

	/**
	 * ECM is active getter
	 * @return if ecm requested is active or not
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Sets ECM as active or inactive
	 * @param active to be set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * ECM JCR Path getter
	 * @return ecm jcr path
	 */
	public String getJcrPath() {
		return jcrPath;
	}

	/**
	 * ECM JCR path setter
	 * @param jcrPath to be set
	 */
	public void setJcrPath(String jcrPath) {
		this.jcrPath = jcrPath;
	}

	/**
	 * ECM Parent path getter
	 * @return gets the ecm parent path
	 */
	public String getParentPath() {
		return parentPath;
	}

	/**
	 * ECM parent path setter
	 * @param parentPath to be set
	 */
	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	/**
	 * ECM Image getter
	 * @return ecm image
	 */
	public byte[] getImage() {
		return image;
	}

	/**
	 * ECM image setter
	 * @param inputStream to be set
	 */
	public void setImage(byte[] inputStream) {
		this.image = inputStream;
	}

	/**
	 * ECM image mime type getter
	 * @return ecm image mime type
	 */
	public String getImageMimeType() {
		return imageMimeType;
	}

	/**
	 * ECM image mime type setter
	 * @param imageMimeType to be set
	 */
	public void setImageMimeType(String imageMimeType) {
		this.imageMimeType = imageMimeType;
	}

	/**
	 * ECM image name getter
	 * @return emc image name
	 */
	public String getImageName() {
		return imageName;
	}

	/**
	 * ECM image name setter
	 * @param imageName to be set
	 */
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	/**
	 * ECM path getter
	 * @return ecm path
	 */
	public byte[] getFile() {
		return fileStream;
	}

	/**
	 * ECM file setter
	 * @param fileStream to be set
	 */
	public void setFile(byte[] fileStream) {
		this.fileStream = fileStream;
	}

	/**
	 * ECM mime type getter
	 * @return ecm mime type
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * ECM mime type setter
	 * @param mimeType to be set
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * ECM File size getter
	 * @return ecm file size
	 */
	public long getFileSize() {
		return fileSize;
	}

	/**
	 * ECM file size setter
	 * @param fileSize to be set
	 */
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * ECM is system getter
	 * @return check if ecm is system or not
	 */
	public boolean isSystem() {
		return system;
	}

	/**
	 * Sets the ecm as a system or not
	 * @param system to be set
	 */
	public void setSystem(boolean system) {
		this.system = system;
	}

	/**
	 * Gets all the tags for the requested ecm
	 * @return a list of tags
	 */
	public List<String> getTags() {
		return tags;
	}

	/**
	 * Sets tags for the ecm
	 * @param tags to be set
	 */
	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	/**
	 * ECM permission getter
	 * @return the ecm permissions
	 */
	public String getPermissions() {
		return permissions;
	}

	/**
	 * ECM permissions setter
	 * @param permissions to be set
	 */
	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}

	/**
	 * Checks if the requested ecm is versional or not
	 * @return the versional
	 */
	public boolean isVersionable() {
		return versionable;
	}

	/**
	 * Sets a new version to the active ecm
	 * @param versionable the versional to set
	 */
	public void setVersionable(boolean versionable) {
		this.versionable = versionable;
	}

	/**
	 * Retrieves ecm version comment
	 * @return the versionComment
	 */
	public String getVersionComment() {
		return versionComment;
	}

	/**
	 * Sets the ecm version comment
	 * @param versionComment the versionComment to set
	 */
	public void setVersionComment(String versionComment) {
		this.versionComment = versionComment;
	}

	/**
	 * Ecm valid date getter
	 * @return the validDate
	 */
	public Date getValidDate() {
		return validDate;
	}

	/**
	 * ECM set valid date
	 * @param validDate the validDate to set
	 */
	public void setValidDate(Date validDate) {
		this.validDate = validDate;
	}

	/**
	 * ECM Version getter
	 * @return the version
	 */
	public SystemContentVersion getVersion() {
		return version;
	}

	/**
	 * ECM version setter
	 * @param version the version to set
	 */
	public void setVersion(SystemContentVersion version) {
		this.version = version;
	}

	/**
	 * ECM Version name getter
	 * @return the versional Name
	 */
	public String getVersionableName() {
		return versionableName;
	}

	/**
	 * ECM Version name setter
	 * @param versionableName the versional Name to set
	 */
	public void setVersionableName(String versionableName) {
		this.versionableName = versionableName;
	}

	/**
	 * ECM validation to update the requested ecm on launch or not
	 * @return the updateOnLaunch
	 */
	public boolean isUpdateOnLaunch() {
		return updateOnLaunch;
	}

	/**
	 * Sets the requested value of the ecm on launch update or not
	 * @param updateOnLaunch the updateOnLaunch to set
	 */
	public void setUpdateOnLaunch(boolean updateOnLaunch) {
		this.updateOnLaunch = updateOnLaunch;
	}
}
