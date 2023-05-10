package io.radien.exception;

import junit.framework.TestCase;
import org.junit.Test;

public class ProcessingLockedExceptionTest extends TestCase {

    @Test
    public void testProcessingLockedException(){
        ProcessingLockedException processingLockedException = new ProcessingLockedException();
        assertEquals(412,processingLockedException.getStatus().getStatusCode());
    }

}