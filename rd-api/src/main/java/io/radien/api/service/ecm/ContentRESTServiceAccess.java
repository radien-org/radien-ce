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

package io.radien.api.service.ecm;

import io.radien.api.Appframeable;
import io.radien.api.service.ecm.model.EnterpriseContent;
import io.radien.api.service.ecm.model.SystemContentVersion;
import io.radien.exception.SystemException;
import java.util.List;

public interface ContentRESTServiceAccess extends Appframeable {

    EnterpriseContent getByViewIdAndLanguage(String viewId, String language) throws SystemException;

    EnterpriseContent getFileContent(String jcrAbsolutePath) throws SystemException;

    List<EnterpriseContent> getFolderContents(String jcrAbsolutePath) throws SystemException;

    String getOrCreateDocumentsPath(String jcrRelativePath) throws SystemException;

    List<EnterpriseContent> getContentVersions(String jcrAbsolutePath) throws SystemException;

    boolean deleteAllVersions(String absoluteJcrPath) throws SystemException;

    boolean deleteVersion(String absoluteJcrPath, SystemContentVersion contentVersion) throws SystemException;

    boolean saveContent(EnterpriseContent enterpriseContent) throws SystemException;

    boolean deleteContentByPath(String absolutePath) throws SystemException;

    boolean deleteContentByViewIDLanguage(String viewId, String language) throws SystemException;
}
