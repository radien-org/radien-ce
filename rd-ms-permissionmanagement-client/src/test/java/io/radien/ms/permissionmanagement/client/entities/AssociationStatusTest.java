package io.radien.ms.permissionmanagement.client.entities;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class AssociationStatusTest {

    @Test
    public void testConstructor() {
        AssociationStatus associationStatus = new AssociationStatus();
        assertTrue(associationStatus.isOK());
        assertNotNull(associationStatus.getMessage());
        assertEquals(associationStatus.getMessage(), "");

        associationStatus = new AssociationStatus(false, "fail");
        assertFalse(associationStatus.isOK());
        assertNotNull(associationStatus.getMessage());
        assertEquals(associationStatus.getMessage(), "fail");

        associationStatus = new AssociationStatus(false, null);
        assertFalse(associationStatus.isOK());
        assertNull(associationStatus.getMessage());
    }
}
