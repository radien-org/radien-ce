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
package io.radien.webapp.role;

import io.radien.api.model.role.SystemRole;
import io.radien.api.service.role.RoleRESTServiceAccess;
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
@FacesConverter(value = "roleConverter", managed = true)
public class RoleConverter implements Converter<SystemRole> {

    @Inject
    private RoleRESTServiceAccess roleRESTServiceAccess;

    @Override
    public SystemRole getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                return roleRESTServiceAccess.getRoleByName(value).
                        orElseThrow(() -> new SystemException(MessageFormat.format(JSFUtil.getMessage(
                                "rd_role_not_found"), value)));
            } catch (Exception e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Conversion Error", e.getMessage()));
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, SystemRole role) {
        return role.getName();
    }
}
