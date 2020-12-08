/*

	Copyright (c) 2006-present go-open.de & its legal owners. All rights reserved.

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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.radien.api.Model;
import io.radien.api.service.ecm.util.EnterpriseContentDeserializer;

/**
 * Interface that defines a generic CMS content
 *
 * @author Marco Weiland
 */
@JsonDeserialize(using = EnterpriseContentDeserializer.class)
public interface EnterpriseContent extends Model, Comparable<EnterpriseContent> {

    ContentType getContentType();

    void setContentType(ContentType contentType);
    
    String getApp();
    
    void setApp(String app);

    String getLanguage();

    void setLanguage(String language);

    String getViewId();

    void setViewId(String viewId);

    String getName();

    void setName(String name);

    String getJcrPath();

    void setJcrPath(String jcrPath);

    String getParentPath();

    void setParentPath(String parentPath);

    String getHtmlContent();

    void setHtmlContent(String htmlContent);

    String getAuthor();

    void setAuthor(String author);

    boolean isActive();

    void setActive(boolean active);

    byte[] getImage();

    void setImage(byte[] image);

    String getImageName();

    void setImageName(String imageName);

    String getImageMimeType();

    void setImageMimeType(String imageMimeType);

    byte[] getFile();

    void setFile(byte[] file);

    long getFileSize();

    void setFileSize(long fileSize);

    String getMimeType();

    void setMimeType(String mimeType);

    boolean isSystem();

    void setSystem(boolean system);

    List<String> getTags();

    void setTags(List<String> tags);

    String getPermissions();

    void setPermissions(String permissions);

    /**
     * 
     * TODO: why has this been placed in a separate interface
     */
    
    
    boolean isVersionable();

    void setVersionable(boolean versionable);

    String getVersionComment();

    void setVersionComment(String versionComment);

    Date getValidDate();

    void setValidDate(Date validDate);

    SystemContentVersion getVersion();

    void setVersion(SystemContentVersion version);

    String getVersionableName();

    void setVersionableName(String name);

    boolean isUpdateOnLaunch();

    void setUpdateOnLaunch(boolean updateOnLaunch);
}
