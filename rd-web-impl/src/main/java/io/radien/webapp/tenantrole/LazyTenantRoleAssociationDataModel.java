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
package io.radien.webapp.tenantrole;

import io.radien.api.entity.Page;
import io.radien.api.model.tenantrole.SystemTenantRole;
import io.radien.api.service.tenantrole.TenantRoleRESTServiceAccess;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Newton Carvalho
 */
public class LazyTenantRoleAssociationDataModel extends LazyDataModel<SystemTenantRole> {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private TenantRoleRESTServiceAccess service;

    private List<? extends SystemTenantRole> datasource;

    public LazyTenantRoleAssociationDataModel(TenantRoleRESTServiceAccess service) {
        this.service=service;
        this.datasource = new ArrayList<>();
    }

    @Override
    public SystemTenantRole getRowData(String rowKey) {
        for (SystemTenantRole association : datasource) {
            if (association.getId() == Integer.parseInt(rowKey)) {
                return association;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(SystemTenantRole association) {
        return String.valueOf(association.getId());
    }

    @Override
    public List<SystemTenantRole> load(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        Long rowCount = 0L;
        try {
            Page<? extends SystemTenantRole> pagedInformation =
                    service.getAll((offset/pageSize)+1, pageSize);
            datasource = pagedInformation.getResults();
            rowCount = (long)pagedInformation.getTotalResults();
        } catch (Exception e){
            log.error("Error trying to load associations", e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error retrieving associations",
                            extractErrorMessage(e)));
        }
        setRowCount(Math.toIntExact(rowCount));
        return datasource.stream().collect(Collectors.toList());
    }

    protected String extractErrorMessage(Exception exception) {
        String errorMsg = (exception instanceof EJBException) ?
                exception.getCause().getMessage() : exception.getMessage();
        return errorMsg.replace("\n\t", "");
    }
}