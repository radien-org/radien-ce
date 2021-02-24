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
package io.radien.ms.rolemanagement.services;

import io.radien.api.entity.Page;
import io.radien.api.model.linked.authorization.SystemLinkedAuthorization;
import io.radien.api.model.linked.authorization.SystemLinkedAuthorizationSearchFilter;
import io.radien.api.service.linked.authorization.LinkedAuthorizationServiceAccess;
import io.radien.exception.LinkedAuthorizationNotFoundException;
import io.radien.exception.UniquenessConstraintException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * @author Bruno Gama
 */
public class LinkedAuthorizationsService implements Serializable {


    private static final long serialVersionUID = 933659981003498804L;

    private static final Logger log = LoggerFactory.getLogger(LinkedAuthorizationsService.class);

    @Inject
    private LinkedAuthorizationServiceAccess linkedAuthorizationServiceAccess;

    public Page<? extends SystemLinkedAuthorization> getAllAssociations(int pageNo, int pageSize){
        return linkedAuthorizationServiceAccess.getAll(pageNo, pageSize);
    }

    public SystemLinkedAuthorization get(long id) throws LinkedAuthorizationNotFoundException {
        return linkedAuthorizationServiceAccess.getAssociationById(id);
    }

    public List<? extends SystemLinkedAuthorization> getSpecificAssociation(SystemLinkedAuthorizationSearchFilter filter) {
        return linkedAuthorizationServiceAccess.getSpecificAssociation(filter);
    }

    public void save(SystemLinkedAuthorization association) throws LinkedAuthorizationNotFoundException, UniquenessConstraintException {
        linkedAuthorizationServiceAccess.save(association);
    }

    public void deleteAssociation(Long association) throws LinkedAuthorizationNotFoundException {
        linkedAuthorizationServiceAccess.deleteAssociation(association);
    }

}
