/*
 * Copyright (c) 2021-present radien GmbH & its legal owners. All rights reserved.
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
package ${package}.api.service;

import java.util.List;

import ${package}.api.entity.Page;
import ${package}.api.model.System${entityResourceName};
import ${package}.api.service.ServiceAccess;
import ${package}.exception.${entityResourceName}NotFoundException;

/**
 * @author Rajesh Gavvala
 * @author Marco Weiland <m.weiland@radien.io>
 */
public interface ${entityResourceName}ServiceAccess extends ServiceAccess {

    public void save(System${entityResourceName} ${entityResourceName.toLowerCase()}) throws ${entityResourceName}NotFoundException;

    public System${entityResourceName} get(Long ${entityResourceName.toLowerCase()}Id) throws ${entityResourceName}NotFoundException;

    public void delete(Long ${entityResourceName.toLowerCase()}Id);

    public Page<System${entityResourceName}> getAll(String search, int pageNo, int pageSize, List<String> sortBy, boolean isAscending);

}
