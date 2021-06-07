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
 * LazyDataModel implemented specifically to attend the exhibition of TenantRole associations
 * using Lazy loading fashion.
 *
 * The idea behind a LazyDataModel is to allow working with considerable hugh datasets,
 * taking in consideration pagination parameters like page size, page number and so on,
 * allowing to load only chunks of data that are really required in a specific moment
 *
 * @author Newton Carvalho
 */
public class LazyTenantRoleAssociationDataModel extends LazyDataModel<SystemTenantRole> {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private TenantRoleRESTServiceAccess service;

    private List<? extends SystemTenantRole> datasource;

    /**
     * Main constructor
     * @param service TenantRoleRESTServiceAccess rest client to perform the role of a DataService,
     *                providing data to assembly the datasource
     */
    public LazyTenantRoleAssociationDataModel(TenantRoleRESTServiceAccess service) {
        this.service=service;
        this.datasource = new ArrayList<>();
    }

    /**
     * Retrieves a SystemTenantRole that corresponds to a DataTable row.
     * @param rowKey id or index that refers a row (presented in a data table)
     * @return SystemTenantRole instance that corresponds to the selected datatable row
     */
    @Override
    public SystemTenantRole getRowData(String rowKey) {
        for (SystemTenantRole association : datasource) {
            if (association.getId() == Integer.parseInt(rowKey)) {
                return association;
            }
        }
        return null;
    }

    /**
     * This method retrieves the row identifier (or key) that exists for a given a SystemTenantRole object
     * @param association TenantRole object contained in the grid, for which the row
     *                    id (key) will be retrieved
     * @return String value that corresponds to the row key
     */
    @Override
    public String getRowKey(SystemTenantRole association) {
        return String.valueOf(association.getId());
    }

    /**
     * Core method that performs the page loading taking in consideration the parameters described bellow
     * @param offset parameter that helps to calculate the page number
     * @param pageSize corresponds to the page size.
     * @param sortBy map containing references to properties/columns that may guide the data sort
     * @param filterBy map containing references to properties/columns that may guide a filtering process
     * @return list containing TenantRole associations corresponding to a page
     */
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
                            e.getMessage()));
        }
        setRowCount(Math.toIntExact(rowCount));
        return datasource.stream().collect(Collectors.toList());
    }
}