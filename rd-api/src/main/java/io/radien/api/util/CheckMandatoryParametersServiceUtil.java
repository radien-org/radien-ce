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
package io.radien.api.util;

import io.radien.exception.GenericErrorCodeMessage;
import io.radien.exception.TenantRoleUserException;

import java.io.Serializable;
import java.util.Collection;

import javax.enterprise.context.RequestScoped;

/**
 * This class serves as helper class to check any parameters that
 * Those are Mandatory to checkable in any service of the application
 *
 * @author Rajesh Gavvala
 */
@RequestScoped
public class CheckMandatoryParametersServiceUtil implements Serializable {
    private static final long serialVersionUID = 2657613283703586737L;

    /**
     * Checks Mandatory parameters of TenantRoleUser
     * @param params TenantRoleUser objects
     * @throws TenantRoleUserException if object found null
     */
    public void checkIfMandatoryParametersTenantRoleUser(Object...params) throws TenantRoleUserException {
        for (Object object:params) {
            if (isInstanceOfCollection(object)){
                throw new TenantRoleUserException(GenericErrorCodeMessage.HAVE_NULL_PARAMS_TENANT_USER_ROLES.toString());
            }
        }
    }

    /**
     * Check Object instance of Collection
     * @param object Object of TenantRoleUser parameter
     * @return boolean value true
     * If the object is instance of Collection else false
     */
    private boolean isInstanceOfCollection(Object object){
        return object instanceof Collection ?  ((Collection<?>) object).isEmpty() : object == null;
    }

}
