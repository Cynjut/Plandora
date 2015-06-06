package com.tests.delegate;

import junit.framework.TestCase;

import com.pandora.delegate.ResourceTaskDelegate;

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
