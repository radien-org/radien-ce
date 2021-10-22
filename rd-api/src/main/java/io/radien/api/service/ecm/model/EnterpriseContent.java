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

    /**
     * ECM content type getter
     * @return ecm content type
     */
    ContentType getContentType();

    /**
     * ECM content type setter
     * @param contentType to be set
     */
    void setContentType(ContentType contentType);

    /**
     * ECM App getter
     * @return the ecm app
     */
    String getApp();

    /**
     * ECM App setter
     * @param app to be set
     */
    void setApp(String app);

    /**
     * ECM Language getter
     * @return the ecm language
     */
    String getLanguage();

    /**
     * ECM language setter
     * @param language to be set
     */
    void setLanguage(String language);

    /**
     * ECM View id getter
     * @return the ecm view id
     */
    String getViewId();

    /**
     * ECM View ID setter
     * @param viewId to be set
     */
    void setViewId(String viewId);

    /**
     * ECM Name getter
     * @return the ecm name
     */
    String getName();

    /**
     * ECm Name setter
     * @param name to be set
     */
    void setName(String name);

    /**
     * ECM JCR Path getter
     * @return the ecm jcr path
     */
    String getJcrPath();

    /**
     * ECM JCR Path setter
     * @param jcrPath to be set
     */
    void setJcrPath(String jcrPath);

    /**
     * ECM Parent path getter
     * @return the ecm parent path
     */
    String getParentPath();

    /**
     * ECM Parent path setter
     * @param parentPath to be set
     */
    void setParentPath(String parentPath);

    /**
     * ECM html content getter
     * @return the ecm html content
     */
    String getHtmlContent();

    /**
     * ECM HTML Content setter
     * @param htmlContent to be set
     */
    void setHtmlContent(String htmlContent);

    /**
     * ECM Author getter
     * @return the ecm author
     */
    String getAuthor();

    /**
     * ECM Author setter
     * @param author to be set
     */
    void setAuthor(String author);

    /**
     * Checks if ECM is still active
     * @return true in case ecm is still active
     */
    boolean isActive();

    /**
     * ECM is active setter
     * @param active to be set
     */
    void setActive(boolean active);

    /**
     * ECM Image getter
     * @return the ecm image
     */
    byte[] getImage();

    /**
     * ECM image setter
     * @param image to be set
     */
    void setImage(byte[] image);

    /**
     * ECM image name getter
     * @return the ecm image name
     */
    String getImageName();

    /**
     * ECM image name setter
     * @param imageName to be set
     */
    void setImageName(String imageName);

    /**
     * ECM image mime type getter
     * @return the ecm image mime type
     */
    String getImageMimeType();

    /**
     * ECM image mime type setter
     * @param imageMimeType to be set
     */
    void setImageMimeType(String imageMimeType);

    /**
     * ECM file getter
     * @return the ecm file
     */
    byte[] getFile();

    /**
     * ECM file setter
     * @param file to be set
     */
    void setFile(byte[] file);

    /**
     * ECM file size getter
     * @return the ecm file size
     */
    long getFileSize();

    /**
     * ECM file size setter
     * @param fileSize to be set
     */
    void setFileSize(long fileSize);

    /**
     * ECM mime type getter
     * @return the ecm mime type
     */
    String getMimeType();

    /**
     * ECM mime type setter
     * @param mimeType to be set
     */
    void setMimeType(String mimeType);

    /**
     * Checks if ecm belongs into the system or not
     * @return true in case ecm is system
     */
    boolean isSystem();

    /**
     * ECM system setter
     * @param system to be set
     */
    void setSystem(boolean system);

    /**
     * ECM tags getter
     * @return the ecm tags into a list
     */
    List<String> getTags();

    /**
     * ECM tage setter
     * @param tags to be set
     */
    void setTags(List<String> tags);

    /**
     * ECM permission getter
     * @return the ecm permissions
     */
    String getPermissions();

    /**
     * ECM permissions setter
     * @param permissions to be set
     */
    void setPermissions(String permissions);

    /**
     * 
     * TODO: why has this been placed in a separate interface
     */

    /**
     * Checks if ECM is versional
     * @return true if ecm is verional
     */
    boolean isVersionable();

    /**
     * ECM versional setter
     * @param versionable to be set
     */
    void setVersionable(boolean versionable);

    /**
     * ECM version comment getter
     * @return the ecm version comment
     */
    String getVersionComment();

    /**
     * ECM version comment setter
     * @param versionComment to be set
     */
    void setVersionComment(String versionComment);

    /**
     * ECM valid date getter
     * @return the ecm valid date
     */
    Date getValidDate();

    /**
     * ECM valid date setter
     * @param validDate to be set
     */
    void setValidDate(Date validDate);

    /**
     * ECM version getter
     * @return the ecm version
     */
    SystemContentVersion getVersion();

    /**
     * ECM version setter
     * @param version to be set
     */
    void setVersion(SystemContentVersion version);

    /**
     * ECM versional name getter
     * @return the ecm versional name
     */
    String getVersionableName();

    /**
     * ECM versional name setter
     * @param name to be set
     */
    void setVersionableName(String name);

    /**
     * Is ECM to be update on launch getter
     * @return true if ecm is to be updated on launch
     */
    boolean isUpdateOnLaunch();

    /**
     * ECM update on launch setter
     * @param updateOnLaunch to be set
     */
    void setUpdateOnLaunch(boolean updateOnLaunch);
}
