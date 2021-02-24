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
package io.radien.api.service.${resource-name-variable};

import java.net.MalformedURLException;
import java.util.Optional;

import io.radien.api.Appframeable;
import io.radien.api.model.${resource-name-variable}.System${resource-name};
import io.radien.exception.SystemException;

/**
 * @author Rajesh Gavvala
 *
 */
public interface ${resource-name}RESTServiceAccess extends Appframeable{

    public boolean create(System${resource-name} ${resource-name-variable}) throws SystemException;

    public boolean update(System${resource-name} ${resource-name-variable}) throws SystemException;

    public boolean delete(long ${resource-name-variable}Id) throws SystemException;
}
