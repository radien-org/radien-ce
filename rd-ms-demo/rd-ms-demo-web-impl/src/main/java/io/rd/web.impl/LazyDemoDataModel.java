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

package io.rd.web.impl;

import io.rd.api.entity.Page;
import io.rd.api.model.SystemDemo;
import io.rd.api.service.DemoRESTServiceAccess;

import io.rd.exception.SystemException;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 *
 * @author Rajesh Gavvala
 */

public class LazyDemoDataModel extends LazyDataModel<SystemDemo> {
    private static final long serialVersionUID = 6812608123262000036L;
    private static final Logger log = LoggerFactory.getLogger(LazyDemoDataModel.class);

    private List<? extends SystemDemo> datasource;
    private DemoRESTServiceAccess demoService;
    private String errorMsg;

    public LazyDemoDataModel(DemoRESTServiceAccess demoService) {
        this.demoService = demoService;
        this.datasource = new ArrayList<>();
    }

    @Override
    public SystemDemo getRowData(String rowKey) {
        for (SystemDemo demo : datasource) {
            if (demo.getId() == Integer.parseInt(rowKey)) {
                return demo;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(SystemDemo demo) {
        return String.valueOf(demo.getId());
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public List<SystemDemo> load(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        Long rowCount = 0L;
        try {
            Page<? extends SystemDemo> page = demoService.getAll(null,(offset/pageSize)+1,pageSize,null,true);
            datasource = page.getResults();

            rowCount = (long)page.getTotalResults();
        } catch (SystemException e) {
            log.error(e.getMessage(),e);
        }

        setRowCount(Math.toIntExact(rowCount));

        return datasource.stream().collect(Collectors.toList());
    }

}
