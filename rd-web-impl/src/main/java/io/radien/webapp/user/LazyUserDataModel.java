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

package io.radien.webapp.user;

import io.radien.api.entity.Page;
import io.radien.api.model.user.SystemUser;
import io.radien.api.service.user.UserRESTServiceAccess;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
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

public class LazyUserDataModel extends LazyDataModel<SystemUser> {
    private static final long serialVersionUID = -4406564138942194061L;
    private static final Logger log = LoggerFactory.getLogger(LazyUserDataModel.class);

    private List<? extends SystemUser> datasource;
    private UserRESTServiceAccess userService;
    private String errorMsg;

    public LazyUserDataModel(UserRESTServiceAccess userService) {
        this.userService = userService;
        this.datasource = new ArrayList<>();
    }

    @Override
    public SystemUser getRowData(String rowKey) {
        for (SystemUser user : datasource) {
            if (user.getId() == Integer.parseInt(rowKey)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public String getRowKey(SystemUser user) {
        return String.valueOf(user.getId());
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public List<SystemUser> load(int offset, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        Long rowCount = 0L;
        try {
            Page<? extends SystemUser> page = userService.getAll(null,(offset/pageSize)+1,pageSize,null,true);
            datasource = page.getResults();

            rowCount = (long)page.getTotalResults();
        } catch (Exception e){
            errorMsg = e.getMessage();
            log.error("Error trying to load users", e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error retrieving users",
                            extractErrorMessage(e)));
        }

        setRowCount(Math.toIntExact(rowCount));

        return datasource.stream().collect(Collectors.toList());
    }

    protected String extractErrorMessage(Exception exception) {
        String errorMsg = (exception instanceof EJBException) ?
                exception.getCause().getMessage() : exception.getMessage();
        // Bootsfaces growl has issues to handle special characters
        return errorMsg.replace("\n\t", "");
    }


}
