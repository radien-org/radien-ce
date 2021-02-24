/*
 * Copyright (c) 2016-present openappframe.org & its legal owners. All rights reserved.
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
package io.radien.api.service.linked.authorization;

import io.radien.api.Appframeable;
import io.radien.api.model.linked.authorization.SystemLinkedAuthorization;
import io.radien.exception.SystemException;

import java.net.MalformedURLException;
import java.util.List;

/**
 * @author Bruno Gama
 */
public interface LinkedAuthorizationRESTServiceAccess extends Appframeable {

    /**
     * Requests find of linked authorization by role id
     * @param roleId to be found
     * @return a list of system linked authorizations
     * @throws SystemException in case of any issue
     */
    public List<? extends SystemLinkedAuthorization> getLinkedAuthorizationByRoleId(Long roleId) throws SystemException;

    /**
     * Creates given linked authorization
     * @param linkedAuthorization to be created
     * @return true if linked authorization has been created with success or false if not
     * @throws MalformedURLException in case of URL specification
     */
    public boolean create(SystemLinkedAuthorization linkedAuthorization) throws SystemException;
}
