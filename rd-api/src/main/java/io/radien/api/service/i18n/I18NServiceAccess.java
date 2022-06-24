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

package io.radien.api.service.i18n;

import io.radien.api.entity.Page;
import io.radien.exception.SystemException;
import java.util.List;

import io.radien.api.model.i18n.SystemI18NProperty;
import io.radien.api.service.ServiceAccess;
import java.util.Map;

public interface I18NServiceAccess extends ServiceAccess {

    String getTranslation(String key, String language, String application) throws SystemException;

    SystemI18NProperty findByKeyAndApplication(String key, String application) throws SystemException;

    List<SystemI18NProperty> findAllByApplication(String application) throws SystemException;

    Map<String, String> findAllByApplicationAndLanguage(String application, String language) throws SystemException;

    void save(SystemI18NProperty property) throws SystemException;

    void deleteProperties(List<SystemI18NProperty> properties) throws SystemException;

    void deleteApplicationProperties(String application) throws SystemException;

    Page<SystemI18NProperty> getAll(String application, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) throws SystemException;
}
