package io.radien.ms.usermanagement.config;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.keycloak.Config;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

/**
 * @author Bruno Gama
 **/
public class ConfigControllerTest extends TestCase {

    @InjectMocks
    ConfigController configController = new ConfigController();

    @InjectMocks
    Config config = new Config();

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetInjectedConfigValue() {
        assertEquals("Config value as Injected by CDI ", configController.getInjectedConfigValue());
    }

    @Test
    public void testGetLookupConfigValue() {
        assertEquals("Config value from ConfigProvider lookup value", configController.getLookupConfigValue());
    }
}