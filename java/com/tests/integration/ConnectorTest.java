package com.tests.integration;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.pandora.bus.SystemSingleton;
import com.pandora.integration.Connector;
import com.pandora.integration.Integration;
import com.pandora.integration.IntegrationResponse;
import com.pandora.integration.ResourceTaskAllocIntegration;
import com.pandora.integration.ResourceTaskIntegration;
import com.pandora.integration.exception.IntegrationException;

/**
 */
public class ConnectorTest extends TestCase {

    /** The class tested */
    Connector connector = null;
    
    String target = "://localhost:8080/pandora/do/connectorListener";
    
    
 	public void setUp() {
 	   connector = new Connector();
 	}
 	
 	public void tearDown() {}

 	
 	public void testExecute() throws IntegrationException{
 	 
 		String protoc = SystemSingleton.getInstance().getSystemProtocol();
 		IntegrationResponse resp0 = connector.execute(prepareResourceTaskList2(), protoc + target);
 		assertTrue("Test 0 ", resp0.wasSuccess());
 	  /**
 	   //Test 1 - check if call with empty list works
 	   IntegrationResponse resp1 = connector.execute(null, target);
 	   assertTrue("Test 1 - check if call with empty list works", resp1==null);

 	   //Test 2 - check if updating ResourceTask data works
 	   IntegrationResponse resp2 = connector.execute(this.prepareResourceTaskList(false, false), target);
 	   assertTrue("Test 2 - check if updating ResourceTask data works", resp2.wasSuccess());

 	   //Test 3 - check if test case failed because some transaction field is empty
 	   try {
 	      connector.execute(this.prepareResourceTaskList(false, true), target);
 	      assertTrue(false);
 	   } catch(IntegrationException e) {
 	       assertTrue("Test 3 - " + e.getMessage(), true);    
 	   } 	   
 	   
 	   //check if updating of 'not existed' ResourceTask failed
 	   IntegrationResponse resp4 = connector.execute(this.prepareResourceTaskList(true, false), target);
 	   assertTrue("Test 4 - check if 'not existed' ResourceTask failed", !resp4.wasSuccess());

  	   //check if updating ResourceTaskAlloc data works
 	   IntegrationResponse resp6 = connector.execute(this.prepareResourceTaskAllocList(2), target);
 	   assertTrue(resp6.wasSuccess()); 	   

 	   //check if updating ResourceTaskAlloc data works
 	   IntegrationResponse resp7 = connector.execute(this.prepareResourceTaskAllocList(3), target);
 	   assertTrue(resp7.wasSuccess()); 	   

 	   //check if updating ResourceTaskAlloc data works
 	   IntegrationResponse resp8 = connector.execute(this.prepareResourceTaskAllocList(1), target);
 	   assertTrue(resp8.wasSuccess()); 	   
 	   */
 	}
 	
 	private ArrayList prepareResourceTaskList2(){
 	    ArrayList list = new ArrayList();
  	    
 	    ResourceTaskIntegration rt1 = new ResourceTaskIntegration("root", "123", "ptBR");
 	    rt1.setEstimatedDay("19");
 	    rt1.setEstimatedMonth("12");
 	    rt1.setEstimatedYear("2005");
 	    rt1.setEstimatedTime("8");
 	    rt1.setResourceId("11");
 	    rt1.setTaskId("725");
 	    rt1.setComment("test 1111");
 	    rt1.setTransaction(Integration.TRANSACTION_UPDATE);    
 	    list.add(rt1);

 	    ResourceTaskAllocIntegration rta1 = new ResourceTaskAllocIntegration("root", "123", "ptBR");
	    rta1.setResourceId("11");
	    rta1.setSequence("1");
	    rta1.setTaskId("725");
	    rta1.setValue("3");
	    rta1.setTransaction(Integration.TRANSACTION_UPDATE); 	    
	    list.add(rta1); 	        

	    return list;
 	    
 	}
 	
 	/*
 	private ArrayList prepareResourceTaskList(boolean notExists, boolean transacEmpty){
 	    ArrayList list = new ArrayList();
 	     	    
 	    ResourceTaskIntegration rt1 = new ResourceTaskIntegration("albeper", "123", "ptBR");
 	    rt1.setEstimatedDay("28");
 	    rt1.setEstimatedMonth("10");
		rt1.setEstimatedYear("2005");
	    rt1.setResourceId("1");
	    rt1.setTaskId("7738");
	    rt1.setComment("test 1");
	    rt1.setTransaction(Integration.TRANSACTION_UPDATE);    
 	    list.add(rt1);

 	    ResourceTaskIntegration rt2 = new ResourceTaskIntegration("betofigu", "123", "ptBR");
 	    rt2.setEstimatedDay("25");
 	    rt2.setEstimatedMonth("10");
		rt2.setEstimatedYear("2005");
		rt2.setComment("test 2");
		if (notExists) {
		    rt2.setResourceId("121212");
		    rt2.setTaskId("121212");
		} else {
		    rt2.setResourceId("1");
		    rt2.setTaskId("7739");
		}
		if (!transacEmpty) {
		    rt2.setTransaction(Integration.TRANSACTION_UPDATE);    
		}
 	    list.add(rt2);

 	    ResourceTaskIntegration rt3 = new ResourceTaskIntegration("vitolisb", "123", "ptBR");
 	    rt3.setEstimatedDay("01");
 	    rt3.setEstimatedMonth("11");
		rt3.setEstimatedYear("2005");
	    rt3.setResourceId("1");
	    rt3.setTaskId("7740");
	    rt3.setComment("test 3");
	    rt3.setTransaction(Integration.TRANSACTION_UPDATE);    
 	    list.add(rt3);
 	    
 	    return list;
 	}

 	
 	private ArrayList prepareResourceTaskAllocList(int slotnumber){
 	    ArrayList list = new ArrayList();
 	    
 	    for (int i =0; i<slotnumber; i++){
 	 	    ResourceTaskAllocIntegration rta1 = new ResourceTaskAllocIntegration("albeper", "123", "ptBR");
 	 	    rta1.setResourceId("1");
 	 	    rta1.setSequence("" + (i+1));
 	 	    rta1.setTaskId("7738");
 	 	    rta1.setValue(480 - (slotnumber * 60) + "");
 	 	    rta1.setTransaction(Integration.TRANSACTION_UPDATE); 	    
 	 	    list.add(rta1); 	        
 	    } 	   
 	    return list;
 	}
*/ 	
}
