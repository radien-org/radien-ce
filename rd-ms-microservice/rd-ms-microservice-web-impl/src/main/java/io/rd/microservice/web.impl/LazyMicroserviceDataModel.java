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

package io.rd.microservice.web.impl;

import io.rd.microservice.api.entity.Page;
import io.rd.microservice.api.model.SystemMicroservice;
import io.rd.microservice.api.service.MicroserviceRESTServiceAccess;

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

public class LazyMicroserviceDataModel extends LazyDataModel<SystemMicroservice> {
    private static final long serialVersionUID = 6812608123262000036L;
    private static final Logger log = LoggerFactory.getLogger(LazyMicroserviceDataModel.class);

    private List<? extends SystemMicroservice> datasource;
    private MicroserviceRESTServiceAccess microserviceService;
    private String errorMsg;

    public LazyMicroserviceDataModel(MicroserviceRESTServiceAccess microserviceService) {
        this.microserviceService = microserviceService;
        this.datasource = new ArrayList<>();
    }

    @Override
    public SystemMicroservice getRowData(String rowKey) {
        for (SystemMicroservice microservice : datasource) {
            if (microservice.getId() == Integer.parseInt(rowKey)) {
                return microservice;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(SystemMicroservice microservice) {
        return String.valueOf(microservice.getId());
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public List<SystemMicroservice> load(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        Long rowCount = 0L;
        try {
            Page<? extends SystemMicroservice> page = microserviceService.getAll(null,(offset/pageSize)+1,pageSize,null,true);
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
