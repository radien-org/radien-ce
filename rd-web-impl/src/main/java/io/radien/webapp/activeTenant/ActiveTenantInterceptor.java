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
 *
 */

package io.radien.webapp.activeTenant;

import io.radien.exception.GenericErrorCodeMessage;
import io.radien.webapp.AbstractManager;
import io.radien.webapp.DataModelEnum;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;

/**
 * Interceptor class that when annotation is added above the method we will validate if a given user has or does not
 * have a active tenant selected
 * @author Bruno Gama
 * @author Newton Carvalho
 */
@Interceptor
@ActiveTenantMandatory
public class ActiveTenantInterceptor extends AbstractManager implements Serializable {

    @Inject
    private ActiveTenantDataModelManager activeTenantDataModelManager;

    @AroundInvoke
    public Object manage(InvocationContext ctx) throws Exception {
        try {
            if (!activeTenantDataModelManager.isTenantActive()) {
                if (ctx.getMethod().getReturnType().equals(void.class)) {
                    redirectToHomePage();
                    return null;
                }
                else {
                    return DataModelEnum.PUBLIC_INDEX_PATH.getValue();
                }
            }
            return ctx.proceed();
        }
        catch (Exception e) {
            throw new Exception(GenericErrorCodeMessage.ACTIVE_TENANT_ERROR_VALIDATING.toString(), e);
        }
    }
}
