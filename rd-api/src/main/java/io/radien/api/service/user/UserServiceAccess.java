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

import java.util.Collection;
import java.util.List;

import io.radien.api.model.user.SystemUser;
import io.radien.api.service.ServiceAccess;
import io.radien.exception.UserNotFoundException;

/**
 * @author Marco Weiland <m.weiland@radien.io>
 *
 */
public interface UserServiceAccess extends ServiceAccess {

    public SystemUser get(Long userId) throws UserNotFoundException;

    public List<SystemUser> get(List<Long> userId);

    public List<SystemUser> getAll(String search,  int pageNo, int pageSize, List<String> sortBy, boolean isAscending, boolean isConjunction);

    public void save(SystemUser user);

    public void delete(Long userId);

    public void delete(Collection<Long> userIds);

}