/*
 * Copyright (c) 2016-present radien.io & its legal owners. All rights reserved.
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
package io.radien.api.service.user;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

import io.radien.api.Appframeable;
import io.radien.api.model.user.SystemUser;
import io.radien.exception.SystemException;

/**
 * @author Marco Weiland <m.weiland@radien.io>
 *
 */
public interface UserRESTServiceAccess extends Appframeable{
	public Optional<SystemUser> getUserBySub(String sub) throws SystemException ;

    /**
     * Creates given user
     * @param user to be created
     * @return true if user has been created with success or false if not
     * @throws MalformedURLException in case of URL specification
     */
    public boolean create(SystemUser user,boolean skipKeycloak) throws SystemException;

    public List<? extends SystemUser> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending) throws MalformedURLException;

    public List<? extends SystemUser> getUserList();

    public boolean setInitiateResetPassword(long id);

    public boolean deleteUser(long id);

    public boolean updateUser(SystemUser user);

    public Long getTotalRecordsCount() throws SystemException;
}
