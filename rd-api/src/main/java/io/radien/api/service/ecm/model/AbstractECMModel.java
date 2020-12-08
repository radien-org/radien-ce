/*

	Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

 */
package io.radien.api.service.ecm.model;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.radien.api.AbstractModel;

/**
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
	protected boolean active = true;
	private String permissions = "";

	private String app;

	protected transient byte[] image;
	transient byte[] fileStream;
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

	public String getJcrPath() {
		return jcrPath;
	}

	public void setJcrPath(String jcrPath) {
		this.jcrPath = jcrPath;
	}

	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] inputStream) {
		this.image = inputStream;
	}

	public String getImageMimeType() {
		return imageMimeType;
	}

	public void setImageMimeType(String imageMimeType) {
		this.imageMimeType = imageMimeType;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
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

	public String getPermissions() {
		return permissions;
	}

	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}

	/**
	 * @return the versionable
	 */
	public boolean isVersionable() {
		return versionable;
	}

	/**
	 * @param versionable the versionable to set
	 */
	public void setVersionable(boolean versionable) {
		this.versionable = versionable;
	}

	/**
	 * @return the versionComment
	 */
	public String getVersionComment() {
		return versionComment;
	}

	/**
	 * @param versionComment the versionComment to set
	 */
	public void setVersionComment(String versionComment) {
		this.versionComment = versionComment;
	}

	/**
	 * @return the validDate
	 */
	public Date getValidDate() {
		return validDate;
	}

	/**
	 * @param validDate the validDate to set
	 */
	public void setValidDate(Date validDate) {
		this.validDate = validDate;
	}

	/**
	 * @return the version
	 */
	public SystemContentVersion getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(SystemContentVersion version) {
		this.version = version;
	}

	/**
	 * @return the versionableName
	 */
	public String getVersionableName() {
		return versionableName;
	}

	/**
	 * @param versionableName the versionableName to set
	 */
	public void setVersionableName(String versionableName) {
		this.versionableName = versionableName;
	}

	/**
	 * @return the updateOnLaunch
	 */
	public boolean isUpdateOnLaunch() {
		return updateOnLaunch;
	}

	/**
	 * @param updateOnLaunch the updateOnLaunch to set
	 */
	public void setUpdateOnLaunch(boolean updateOnLaunch) {
		this.updateOnLaunch = updateOnLaunch;
	}
}
