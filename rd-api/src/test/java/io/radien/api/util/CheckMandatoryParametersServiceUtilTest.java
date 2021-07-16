package io.radien.api.util;

import io.radien.exception.tenantroleuser.TenantRoleUserException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

/**
 * Class that aggregates UnitTest cases for
 * CheckMandatoryParametersServiceUtil
 *
 * @author Rajesh Gavvala
 */
public class CheckMandatoryParametersServiceUtilTest {
    @InjectMocks
    private CheckMandatoryParametersServiceUtil checkMandatoryParametersServiceUtil;

    /**
     * Prepares require objects when requires to invoke
     */
    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test method checkIfMandatoryParametersTenantRoleUser()
     * @throws TenantRoleUserException if found any parameters are null
     */
    @Test
    public void testCheckIfMandatoryParametersTenantRoleUser() throws TenantRoleUserException {
        Long userId = 1L;
        Long tenantId = 1L;
        List<Long> roleIds = new ArrayList<>();
        roleIds.add(1L);
        checkMandatoryParametersServiceUtil.checkIfMandatoryParametersTenantRoleUser(userId, tenantId, roleIds);
    }

    /**
     * Test method checkIfMandatoryParametersTenantRoleUser()
     * Expected an error of TenantRoleUserException
     * @throws TenantRoleUserException if found any parameters are null
     */
    @Test(expected = TenantRoleUserException.class)
    public void testCheckIfMandatoryParametersTenantRoleUserException() throws TenantRoleUserException {
        Long userId = 1L;
        Long tenantId = 1L;
        List<Long> roleIds = new ArrayList<>();
        checkMandatoryParametersServiceUtil.checkIfMandatoryParametersTenantRoleUser(userId, tenantId, roleIds);
    }

}