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
package io.radien.webapp.tenant;

import io.radien.api.model.permission.SystemAction;
import io.radien.api.model.tenant.SystemTenant;
import io.radien.api.service.permission.ActionRESTServiceAccess;
import io.radien.api.service.tenant.TenantRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.webapp.JSFUtil;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;
import java.text.MessageFormat;

/**
 * @author Newton Carvalho
 */
@Named
@FacesConverter(value = "tenantConverter", managed = true)
public class TenantConverter implements Converter<SystemTenant> {

    @Inject
    private TenantRESTServiceAccess tenantRESTServiceAccess;

    @Override
    public SystemTenant getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                return tenantRESTServiceAccess.getTenantByName(value).stream().findFirst().
                        orElseThrow(() -> new SystemException(MessageFormat.format(JSFUtil.getMessage(
                                "rd_tenant_not_found"), value)));
            } catch (Exception e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Conversion Error", e.getMessage()));
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, SystemTenant tenant) {
        return tenant.getName();
    }
}
