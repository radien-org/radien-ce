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
package io.radien.api.service.user;

import io.radien.api.Appframeable;
import io.radien.api.entity.Page;
import io.radien.api.model.jobshop.SystemJobshopItem;
import io.radien.api.model.jobshop.SystemStudentIdentity;
import io.radien.exception.SystemException;

import java.util.List;

/**
 * User REST Service Access interface for future requests
 *
 * @author Marco Weiland
 */
public interface JobShopRESTServiceAccess extends Appframeable{

    public Page<? extends SystemStudentIdentity> getAllStudent(String nameFilter, int pageNo, int pageSize) throws SystemException;

    public List<? extends SystemJobshopItem> getAllItem() throws SystemException;

    public boolean create(SystemStudentIdentity student) throws SystemException;

    public boolean create(SystemJobshopItem item) throws SystemException;
}
