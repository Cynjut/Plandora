package com.tests.delegate;

import com.pandora.delegate.ResourceTaskDelegate;

import junit.framework.TestCase;

public class ResourceTaskDelegateTest extends TestCase {

    ResourceTaskDelegate del = null;
    
    public void changeTaskStatus() {
        try {
            //del.changeTaskStatus(rtto)
            assertTrue(false);
        } catch(Exception e){
            assertTrue(false);
        }
    }

    protected void setUp() throws Exception {
        del = new ResourceTaskDelegate();
    }
}
