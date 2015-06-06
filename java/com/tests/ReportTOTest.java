package com.tests;

import junit.framework.TestCase;

import com.pandora.ReportTO;

/**
 */
public class ReportTOTest extends TestCase {

    ReportTO rto = new ReportTO();
    
    public void testGetSqlWithoutDomain(){

 	   rto.setSqlStement("select r.id " +
						"from resource_task rt, resource r " +
						"where rt.resource_id = r.id " +
						"and rt.id = ?#LISTA_RECURSOS{0|Fulano|1|Sicrano}(1)# " +
						"and rt.project_id=?#PROJECT_ID#");
 	   String response1 = rto.getSqlWithoutDomain();
 	   assertTrue(response1.equals("select r.id " +
 	    						"from resource_task rt, resource r " +
 	    						"where rt.resource_id = r.id " +
 	    						"and rt.id = ?#LISTA_RECURSOS# " +
 	     						"and rt.project_id=?#PROJECT_ID#"));
 	   
 	   rto.setSqlStement("select r.id " +
 	          			"from resource_task rt, resource r " +
 	          			"where rt.resource_id = r.id " +
 	          			"and rt.id = ?#ESCOLHA_RECURSO_1{}(1)# or rt.id = ?#ESCOLHA_RECURSO_2{  }(2)# " +
 	          			"and rt.project_id=?#PROJECT_ID#" +   			
 	          			"and rt.id = ?#ESCOLHA_RECURSO_1{0 | Fulano | 1 | Sicrano}(1)#");
 	   String response2 = rto.getSqlWithoutDomain();
 	   assertTrue(response2.equals("select r.id " +
      			"from resource_task rt, resource r " +
      			"where rt.resource_id = r.id " +
      			"and rt.id = ?#ESCOLHA_RECURSO_1# or rt.id = ?#ESCOLHA_RECURSO_2# " +
      			"and rt.project_id=?#PROJECT_ID#" +   			
      			"and rt.id = ?#ESCOLHA_RECURSO_1#"));
 	   

 	   rto.setSqlStement("select r.id " +
      			"from resource_task rt, resource r " +
      			"and rt.id = ?#ESCOLHA_RECURSO_2{}(1) or rt.id = ?#ESCOLHA_RECURSO_2{  }(1)#");
	   String response3a = rto.getSqlWithoutDomain();
	   assertTrue(response3a.equals("select r.id " +
					"from resource_task rt, resource r " +
					"and rt.id = ?#ESCOLHA_RECURSO_2 or rt.id = ?#ESCOLHA_RECURSO_2#"));
	   

 	   
 	   rto.setSqlStement("select r.id " +
 	           			"from resource_task rt, resource r " +
 	           			"and rt.id = ?#ESCOLHA_RECURSO_2{} or rt.id = ?#ESCOLHA_RECURSO_2{  }#");
 	   String response3 = rto.getSqlWithoutDomain();
 	   assertTrue(response3.equals("select r.id " +
       					"from resource_task rt, resource r " +
       					"and rt.id = ?#ESCOLHA_RECURSO_2 or rt.id = ?#ESCOLHA_RECURSO_2{  }#"));

 	   
 	   
 	   rto.setSqlStement("select r.id " +
 	           			"from resource_task rt, resource r " +
 	           			"and rt.id = ?#ESCOLHA_RECURSO_1{}");
 	   String response4 = rto.getSqlWithoutDomain();
 	   assertTrue(response4.equals("select r.id " +
       								"from resource_task rt, resource r " +
       								"and rt.id = ?#ESCOLHA_RECURSO_1"));
 	   
 	 
 	   rto.setSqlStement("select r.id " +
 	           			"from resource_task rt, resource r " +
						"and rt.id = ?#RECURSOS{!select id, name from departmenet where project=?#PROJECT_LIST{0|apple|1|orange|2|lemon}(1)# and name is not null!}#");
 	   String response5 = rto.getSqlWithoutDomain();
 	   assertTrue(response5.equals("select r.id " +
 	           			"from resource_task rt, resource r " +
						"and rt.id = ?#RECURSOS#"));
 	  
    }

}
