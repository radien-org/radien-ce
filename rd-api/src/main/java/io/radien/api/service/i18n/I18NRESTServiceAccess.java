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

import io.radien.exception.SystemException;
import java.util.List;

import io.radien.api.Appframeable;
import io.radien.api.model.i18n.SystemI18NProperty;
import java.util.Optional;

public interface I18NRESTServiceAccess extends Appframeable {

    String getTranslation(String key, String language, String application) throws SystemException;

    boolean save(SystemI18NProperty property) throws SystemException;

    Optional<SystemI18NProperty> findByKeyAndApplication(String key, String application) throws SystemException;

    List<SystemI18NProperty> findAllByApplication(String application) throws SystemException;
    
}
