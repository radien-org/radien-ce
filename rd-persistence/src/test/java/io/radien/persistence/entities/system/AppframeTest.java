package io.radien.persistence.entities.system;


import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertNotNull;

public class AppframeTest {

    Appframe appframe;
    private final Date date = new Date();


    @Before
    public void setUpEach() {
        appframe = new Appframe();
        appframe.setId(1L);
        appframe.setVersion("version1");
        appframe.setCreateDate(date);
        appframe.setCreateUser(2L);
        appframe.setLastUpdate(date);
        appframe.setLastUpdateUser(3L);
        appframe.setLog("log1");
    }

    @Test
    public void testAppframeConstructor(){
        appframe = new Appframe(1L,"version");
        assertNotNull(appframe.getId());
        assertNotNull(appframe.getVersion());
    }

    /**
     * Test method for
     * {@link Appframe#getId()}
     */
    @Test
    public void testGetId() {assertNotNull(appframe.getId());}

    /**
     * Test method for
     * {@link Appframe#getVersion()} ()}
     */
    @Test
    public void testGetVersion() {assertNotNull(appframe.getVersion());}

    /**
     * Test method for
     * {@link Appframe#getCreateDate()}
     */
    @Test
    public void testGetCreateDate() {assertNotNull(appframe.getCreateDate());}

    /**
     * Test method for
     * {@link Appframe#getCreateUser()}
     */
    @Test
    public void testGetCreateUser() {assertNotNull(appframe.getCreateUser());}

    /**
     * Test method for
     * {@link Appframe#getLastUpdateUser()}
     */
    @Test
    public void testGetLastUpdateUser() {assertNotNull(appframe.getLastUpdateUser());}

    /**
     * Test method for
     * {@link Appframe#getLastUpdate()}
     */
    @Test
    public void testGetLastUpdate() {assertNotNull(appframe.getLastUpdate());}

    /**
     * Test method for
     * {@link Appframe#getLog()}
     */
    @Test
    public void testGetLog() {assertNotNull(appframe.getLog());}
}