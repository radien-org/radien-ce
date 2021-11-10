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
package io.radien.webapp.contract;

import io.radien.api.model.tenant.SystemContract;
import io.radien.api.service.tenant.ContractRESTServiceAccess;
import io.radien.exception.ProcessingException;
import io.radien.ms.tenantmanagement.client.entities.Contract;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.JSFUtil;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author Newton Carvalho
 */
@Model
@RequestScoped
public class ContractManager extends AbstractManager {

    @Inject
    private ContractRESTServiceAccess contractRESTServiceAccess;

    protected SystemContract contract = new Contract();

    protected Date contractStartDate;
    protected Date contractEndDate;

    public String save(SystemContract c) {
        try {
            this.checkAndConvertDates();
            if (c.getId() == null) {
                this.contractRESTServiceAccess.create(c);
            } else {
                this.contractRESTServiceAccess.update(c.getId(), c);
            }
            handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage("rd_save_success"),
                    JSFUtil.getMessage("rd_contract"));
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_save_error"), JSFUtil.getMessage("rd_contract"));
        }
        return "contract";
    }

    protected void checkAndConvertDates() throws ProcessingException{

        if (this.contractEndDate.before(this.contractStartDate)) {
            throw new ProcessingException(JSFUtil.getMessage("rd_contract_dates_invalid"));
        }

        LocalDateTime endDate = this.contractEndDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        LocalDateTime startDate = this.contractStartDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        this.contract.setStart(startDate);
        this.contract.setEnd(endDate);
    }

    protected void obtainDates() {
        this.contractStartDate = Date.from(this.contract.getStart().atZone(
                ZoneId.systemDefault()).toInstant());
        this.contractEndDate = Date.from(this.contract.getEnd().atZone(
                ZoneId.systemDefault()).toInstant());
    }

    public String edit(SystemContract c) {
        this.setContract(c);
        this.obtainDates();
        return "contract";
    }

    public SystemContract getContract() {
        return contract;
    }

    public void setContract(SystemContract contract) {
        this.contract = contract;
    }

    public Date getContractStartDate() {
        return contractStartDate;
    }

    public void setContractStartDate(Date contractStartDate) {
        this.contractStartDate = contractStartDate;
    }

    public Date getContractEndDate() {
        return contractEndDate;
    }

    public void setContractEndDate(Date contractEndDate) {
        this.contractEndDate = contractEndDate;
    }
}