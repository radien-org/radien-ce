package io.radien.webapp.activeTenant;

import io.radien.webapp.DataModelEnum;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;

@Interceptor
@ActiveTenantMandatory
public class ActiveTenantInterceptor implements Serializable {

    @Inject
    private ActiveTenantDataModelManager activeTenantDataModelManager;

    @AroundInvoke
    public Object manage(InvocationContext ctx) throws Exception {
        try {
            if (!activeTenantDataModelManager.isTenantActive()) {
                ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
                context.redirect(context.getRequestContextPath() + DataModelEnum.PUBLIC_INDEX_PATH.getValue());
//                return DataModelEnum.PUBLIC_INDEX_PATH.getValue();
            }
            return ctx.proceed();
        }
        catch (Exception e) {
            throw new Exception("Error checking active tenants", e);
        }
    }
}
