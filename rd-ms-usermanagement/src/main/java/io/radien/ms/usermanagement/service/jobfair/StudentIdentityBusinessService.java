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

package io.radien.ms.usermanagement.service.jobfair;

import io.radien.api.entity.Page;
import io.radien.api.model.jobshop.SystemStudentIdentity;
import io.radien.api.service.jobshop.StudentIdentityServiceAccess;
import java.io.Serializable;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class StudentIdentityBusinessService implements Serializable {
    private static final long serialVersionUID = -633790942204494649L;

    @Inject
    private StudentIdentityServiceAccess serviceAccess;

    public Page<? extends SystemStudentIdentity> getAll(String nameFiler,
                                                        int pageNo, int pageSize) {
        return serviceAccess.getAll(nameFiler, pageNo, pageSize);
    }

    public void save(SystemStudentIdentity identity) {
        serviceAccess.save(identity);
    }

}
