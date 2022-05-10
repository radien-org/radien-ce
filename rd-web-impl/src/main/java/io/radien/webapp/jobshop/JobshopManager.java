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

package io.radien.webapp.jobshop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.radien.api.model.jobshop.SystemJobshopItem;
import io.radien.api.model.jobshop.SystemStudentIdentity;
import io.radien.api.service.user.JobShopRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.usermanagement.client.entities.JobshopItem;
import io.radien.ms.usermanagement.client.entities.StudentIdentity;
import java.io.Serializable;
import java.util.List;
import java.util.StringJoiner;
import javax.annotation.PostConstruct;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.inject.Inject;

import io.radien.webapp.JSFUtil;
import javax.faces.context.FacesContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Model
@SessionScoped
public class JobshopManager implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(JobshopManager.class);

    private List<? extends SystemJobshopItem> itemsList;
    private SystemStudentIdentity studentIdentity = new StudentIdentity();
    private int resultIndex;
    private String resultName;

    private boolean java;
    private boolean kotlin;
    private boolean aws;
    private boolean sql;
    private boolean python;
    private boolean azure;
    private boolean jsf;
    private boolean php;
    private boolean businessSide;
    private boolean hr;
    private boolean financial;
    private boolean automotive;
    private String others;

    @Inject
    private JobShopRESTServiceAccess jobShop;

    @PostConstruct
    public void init() throws SystemException, JsonProcessingException {
        reset();
    }

    public void reset() throws SystemException, JsonProcessingException {
        getItems();
        java = false;
        kotlin = false;
        aws = false;
        sql = false;
        python = false;
        azure = false;
        jsf = false;
        php = false;
        businessSide = false;
        hr = false;
        financial = false;
        automotive = false;
        others = "";
        studentIdentity = new StudentIdentity();
        resultIndex = -1;
        resultName = null;
    }

    public String getItems() throws JsonProcessingException, SystemException {
        itemsList = jobShop.getAllItem();
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(itemsList);
    }

    public void checkPrize() {
        String param1 = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("wheelIndex");
        log.info("result " + itemsList.get(Integer.parseInt(param1)).getLabel() + " available quantity: " + itemsList.get(Integer.parseInt(param1)).getWeight());
    }

    public void restartStuff() throws SystemException, JsonProcessingException {
        getItems();
        JobshopItem selectedItem = (JobshopItem) itemsList.get(resultIndex);
        if(!selectedItem.getLabel().equals(resultName)) {
            JSFUtil.addWarningMessage("Item no longer available! Please try again!");
            return;
        }
        selectedItem.setWeight(selectedItem.getWeight()-1);
        jobShop.create(selectedItem);
        JSFUtil.redirect("/module/jobshop/studentForm?faces-redirect=true");
    }

    public SystemStudentIdentity getStudentIdentity() {
        return studentIdentity;
    }
    public void setStudentIdentity(SystemStudentIdentity studentIdentity) {
        this.studentIdentity = studentIdentity;
    }
    public int getResultIndex() {
        return resultIndex;
    }
    public void setResultIndex(int resultIndex) {
        this.resultIndex = resultIndex;
    }
    public String getResultName() {
        return resultName;
    }
    public void setResultName(String resultName) {
        this.resultName = resultName;
    }
    public List<? extends SystemJobshopItem> getItemsList() {
        return itemsList;
    }
    public void setItemsList(List<? extends SystemJobshopItem> itemsList) {
        this.itemsList = itemsList;
    }

    public void goNextStep() throws SystemException {
        storeInterests();

        jobShop.create(studentIdentity);
        JSFUtil.redirect("/module/jobshop/wheel?faces-redirect=true");
    }

    public void storeInterests() {
        StringJoiner s = new StringJoiner(", ");

        if(java) {
            s.add("java");
        } if (kotlin) {
            s.add("kotlin");
        } if(aws) {
            s.add("aws");
        } if(sql) {
            s.add("sql");
        } if (python) {
            s.add("python");
        } if(azure) {
            s.add("azure");
        } if(jsf) {
            s.add("jsf");
        } if(php) {
            s.add("php");
        } if(businessSide) {
            s.add("businessSide");
        } if(hr) {
            s.add("hr");
        } if(financial) {
            s.add("financial");
        } if(automotive) {
            s.add("automotive");
        } if(!others.isEmpty()) {
            s.add(others);
        }

        studentIdentity.setInterests(s.toString());
    }

    public boolean isJava() {
        return java;
    }

    public void setJava(boolean java) {
        this.java = java;
    }

    public boolean isKotlin() {
        return kotlin;
    }

    public void setKotlin(boolean kotlin) {
        this.kotlin = kotlin;
    }

    public boolean isAws() {
        return aws;
    }

    public void setAws(boolean aws) {
        this.aws = aws;
    }

    public boolean isSql() {
        return sql;
    }

    public void setSql(boolean sql) {
        this.sql = sql;
    }

    public boolean isPython() {
        return python;
    }

    public void setPython(boolean python) {
        this.python = python;
    }

    public boolean isAzure() {
        return azure;
    }

    public void setAzure(boolean azure) {
        this.azure = azure;
    }

    public boolean isJsf() {
        return jsf;
    }

    public void setJsf(boolean jsf) {
        this.jsf = jsf;
    }

    public boolean isPhp() {
        return php;
    }

    public void setPhp(boolean php) {
        this.php = php;
    }

    public boolean isBusinessSide() {
        return businessSide;
    }

    public void setBusinessSide(boolean businessSide) {
        this.businessSide = businessSide;
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others;
    }

    public boolean isHr() {
        return hr;
    }

    public void setHr(boolean hr) {
        this.hr = hr;
    }

    public boolean isFinancial() {
        return financial;
    }

    public void setFinancial(boolean financial) {
        this.financial = financial;
    }

    public boolean isAutomotive() {
        return automotive;
    }

    public void setAutomotive(boolean automotive) {
        this.automotive = automotive;
    }
}

