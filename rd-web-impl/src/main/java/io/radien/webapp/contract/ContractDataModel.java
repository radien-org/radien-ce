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

package io.radien.webapp.contract;

import io.radien.api.model.tenant.SystemContract;
import io.radien.api.service.tenant.ContractRESTServiceAccess;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author Bruno Gama
 */
@Model
@SessionScoped
public class ContractDataModel implements Serializable {


    /**
	 * 
	 */
	private static final long serialVersionUID = 4809846160847227798L;

	private LazyDataModel<? extends SystemContract> lazyModel;

    private SystemContract selectedContract;

    @Inject
    private ContractRESTServiceAccess service;

    @PostConstruct
    public void init() {
        lazyModel = new LazyContractDataModel(service);
    }

    public void onload() {
        init();
    }

    public LazyDataModel<? extends SystemContract> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<? extends SystemContract> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public SystemContract getSelectedContract() {
        return selectedContract;
    }

    public void setSelectedContract(SystemContract selectedContract) {
        this.selectedContract = selectedContract;
    }

    public ContractRESTServiceAccess getService() {
        return service;
    }

    public void setService(ContractRESTServiceAccess service) {
        this.service = service;
    }

    public void onRowSelect(SelectEvent<SystemContract> event) {
        FacesMessage msg = new FacesMessage("Customer Selected", String.valueOf(event.getObject().getId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
