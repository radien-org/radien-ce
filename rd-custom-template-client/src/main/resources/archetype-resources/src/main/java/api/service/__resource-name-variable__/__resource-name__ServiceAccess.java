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
package io.radien.api.service.${resource-name-variable};


import io.radien.api.model.${resource-name-variable}.System${resource-name};
import io.radien.api.service.ServiceAccess;

/**
 * @author Rajesh Gavvala
 *
 */
public interface ${resource-name}ServiceAccess extends ServiceAccess {

    public System${resource-name} get(Long ${resource-name-variable}Id);

    public void create(System${resource-name} ${resource-name-variable});

    public void update(System${resource-name} ${resource-name-variable});

    public void delete(Long ${resource-name-variable}Id);

}
