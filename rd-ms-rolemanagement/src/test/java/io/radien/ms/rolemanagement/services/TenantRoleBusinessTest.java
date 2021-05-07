package io.radien.ms.rolemanagement.services;

import io.radien.api.service.tenantrole.TenantRolePermissionServiceAccess;
import io.radien.api.service.tenantrole.TenantRoleServiceAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.Properties;

/**
 * @author Newton Carvalho
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TenantRoleBusinessTest {

    Properties p;
    TenantRoleBusinessService tenantRoleBusinessService;

    Long basePermissionId = 111L;
    Long baseTenantRoleId = 222L;

    public TenantRoleBusinessTest() throws NamingException {
        p = new Properties();
        p.put("appframeDatabase", "new://Resource?type=DataSource");
        p.put("appframeDatabase.JdbcDriver", "org.hsqldb.jdbcDriver");
        p.put("appframeDatabase.JdbcUrl", "jdbc:hsqldb:mem:radienTest");
        p.put("appframeDatabase.userName", "sa");
        p.put("appframeDatabase.password", "");
        p.put("openejb.deployments.classpath.include",".*");
        p.put("openejb.deployments.classpath.exclude",".*rd-ms-usermanagement-client.*");


        final Context context = EJBContainer.createEJBContainer(p).getContext();

        String lookupString = "java:global/rd-ms-rolemanagement//TenantRoleBusinessService";
        tenantRoleBusinessService = (TenantRoleBusinessService) context.lookup(lookupString);
    }

    @Test
    public void test1() {
        Assertions.assertNotNull(tenantRoleBusinessService);
    }

}
