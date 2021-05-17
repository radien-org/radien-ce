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
 * See the License for the specific language governing actions and
 * limitations under the License.
 */
package io.radien.webapp.permission;

import io.radien.api.model.permission.SystemAction;
import io.radien.api.model.permission.SystemPermission;
import io.radien.api.model.permission.SystemResource;
import io.radien.api.service.permission.ActionRESTServiceAccess;
import io.radien.api.service.permission.PermissionRESTServiceAccess;
import io.radien.api.service.permission.ResourceRESTServiceAccess;
import io.radien.exception.SystemException;
import io.radien.ms.permissionmanagement.client.entities.Action;
import io.radien.ms.permissionmanagement.client.entities.Permission;
import io.radien.ms.permissionmanagement.client.entities.Resource;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.JSFUtil;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

/**
 * @author Newton Carvalho
 */
@Model
@RequestScoped
public class PermissionManager extends AbstractManager {

    @Inject
    private PermissionRESTServiceAccess permissionRESTServiceAccess;

    @Inject
    private ActionRESTServiceAccess actionRESTServiceAccess;

    @Inject
    private ResourceRESTServiceAccess resourceRESTServiceAccess;

    protected SystemPermission permission = new Permission();
    protected SystemAction selectedAction = new Action();
    protected SystemResource selectedResource = new Resource();

    public String save(SystemPermission p) {
        try {
            if (p.getName() == null || p.getName().trim().length() == 0) {
                p.setName(this.selectedAction.getName() + " " + this.selectedResource.getName());
            }
            p.setResourceId(this.selectedResource.getId());
            p.setActionId(this.selectedAction.getId());
            this.permissionRESTServiceAccess.create(p);

            handleMessage(FacesMessage.SEVERITY_INFO, JSFUtil.getMessage("rd_save_success"),
                    JSFUtil.getMessage("rd_permission"));
        } catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_save_error"), JSFUtil.getMessage("rd_permission"));
        }
        return "permission";
    }

    public String edit(SystemPermission p) {
        this.setPermission(p);
        try {
            this.selectedAction = this.actionRESTServiceAccess.getActionById(p.getActionId()).
                    orElseThrow(() -> new SystemException(MessageFormat.format(JSFUtil.getMessage(
                            "rd_action_not_found"), p.getActionId())));
            this.selectedResource = this.resourceRESTServiceAccess.getResourceById(p.getResourceId()).
                    orElseThrow(() -> new SystemException(MessageFormat.format(JSFUtil.getMessage(
                            "rd_resource_not_found"), p.getResourceId())));
        }
        catch (Exception e) {
            handleError(e, JSFUtil.getMessage("rd_edit_error"), JSFUtil.getMessage("rd_permission"));
        }
        return "permission";
    }

    public List<? extends SystemResource> filterResourcesByName(String filter) throws SystemException {
        List<? extends SystemResource> page = null;
        try {
            if (filter.trim().length() == 0) {
                page = this.resourceRESTServiceAccess.getAll(
                        null, 1, 5, Arrays.asList("name"), true).getResults();
            } else {
                if (!filter.endsWith("%")) {
                    filter += "%";
                }
                page = this.resourceRESTServiceAccess.getAll(
                        filter, 1, 5, Arrays.asList("name"), true).getResults();
            }
        } catch (SystemException e) {
                handleError(e, JSFUtil.getMessage("rd_edit_error"), JSFUtil.getMessage("rd_permission"));
        }
        return page;
    }

    public List<? extends SystemAction> filterActionsByName(String filter) throws SystemException {
        List<? extends SystemAction> list = null;
        try {
            if (filter.trim().length() == 0) {
                list = this.actionRESTServiceAccess.getAll(
                        null, 1, 10, Arrays.asList("name"), true).getResults();
            }
            else {
                if (!filter.endsWith("%")) {
                    filter += "%";
                }
                list = this.actionRESTServiceAccess.getAll(
                        filter, 1, 10, Arrays.asList("name"), true).getResults();
            }
        } catch (SystemException e) {
            handleError(e, JSFUtil.getMessage("rd_edit_error"), JSFUtil.getMessage("rd_permission"));
        }
        return list;
    }

    public SystemPermission getPermission() {
        return permission;
    }

    public void setPermission(SystemPermission permission) {
        this.permission = permission;
    }

    public SystemAction getSelectedAction() {
        return selectedAction;
    }

    public void setSelectedAction(SystemAction selectedAction) {
        this.selectedAction = selectedAction;
    }

    public SystemResource getSelectedResource() {
        return selectedResource;
    }

    public void setSelectedResource(SystemResource selectedResource) {
        this.selectedResource = selectedResource;
    }
}