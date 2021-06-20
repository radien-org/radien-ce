package io.radien.ms.config.lib;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

/**
 * @author Bruno Gama
 **/
public class MSOAFTest extends TestCase {

    @InjectMocks
    MSOAF oaf;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetVersion() {
        assertNull(oaf.getVersion());
    }

    @Test
    public void testGetSystemAdminUserId() {
        assertEquals((Long) 0L, oaf.getSystemAdminUserId());
    }
}
