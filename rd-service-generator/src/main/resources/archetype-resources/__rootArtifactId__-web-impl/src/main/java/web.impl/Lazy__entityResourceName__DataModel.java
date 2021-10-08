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

package ${package}.web.impl;

import ${package}.api.entity.Page;
import ${package}.api.model.System${entityResourceName};
import ${package}.api.service.${entityResourceName}RESTServiceAccess;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 *
 * @author Rajesh Gavvala
 */

public class Lazy${entityResourceName}DataModel extends LazyDataModel<System${entityResourceName}> {
    private static final long serialVersionUID = 6812608123262000036L;
    private static final Logger log = LoggerFactory.getLogger(Lazy${entityResourceName}DataModel.class);

    private List<? extends System${entityResourceName}> datasource;
    private ${entityResourceName}RESTServiceAccess ${entityResourceName.toLowerCase()}Service;
    private String errorMsg;

    public Lazy${entityResourceName}DataModel(${entityResourceName}RESTServiceAccess ${entityResourceName.toLowerCase()}Service) {
        this.${entityResourceName.toLowerCase()}Service = ${entityResourceName.toLowerCase()}Service;
        this.datasource = new ArrayList<>();
    }

    @Override
    public System${entityResourceName} getRowData(String rowKey) {
        for (System${entityResourceName} ${entityResourceName.toLowerCase()} : datasource) {
            if (${entityResourceName.toLowerCase()}.getId() == Integer.parseInt(rowKey)) {
                return ${entityResourceName.toLowerCase()};
            }
        }
        return null;
    }

    @Override
    public String getRowKey(System${entityResourceName} ${entityResourceName.toLowerCase()}) {
        return String.valueOf(${entityResourceName.toLowerCase()}.getId());
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public List<System${entityResourceName}> load(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        Long rowCount = 0L;
        try {
            Page<? extends System${entityResourceName}> page = ${entityResourceName.toLowerCase()}Service.getAll(null,(offset/pageSize)+1,pageSize,null,true);
            datasource = page.getResults();

            rowCount = (long)page.getTotalResults();
        } catch (MalformedURLException  e) {
            e.printStackTrace();
        } catch (WebApplicationException e){
            errorMsg = e.getMessage();
        }

        setRowCount(Math.toIntExact(rowCount));

        return datasource.stream().collect(Collectors.toList());
    }

}
