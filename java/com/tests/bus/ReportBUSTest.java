package com.tests.bus;

import java.util.Vector;

import junit.framework.TestCase;

import com.pandora.ReportFieldTO;
import com.pandora.bus.ReportBUS;

/**
 */
public class ReportBUSTest extends TestCase {

    ReportBUS bus = new ReportBUS();
    Vector response1 = null;
    Vector response2 = null;
    Vector response3 = null;
    Vector response4 = null;
    Vector response5 = null;
    Vector response6 = null;
    Vector response7 = null;
    Vector response8 = null;
    Vector response9 = null;
    Vector response10 = null;
    Vector response11 = null;
    
 	public void testGetReportFields(){
  	   
 	   //normal content
 	   String content1 = "select sum((rt.estimated_time/60) * r.cost_per_hour) as value, r.id " +
 	   		"from resource_task rt, resource r " +
 	   		"where rt.resource_id = r.id " +
 	   		"and rt.project_id = r.project_id " +
 	   		"and rt.id = ?#LISTA_RECURSOS{0|Fulano|1|Sicrano}(1)# " +
 	   		"and rt.project_id=?#PROJECT_ID#";
 	   response1 = bus.getReportFields(content1);
	   assertTrue(response1.size()==2);
	   assertTrue(((ReportFieldTO)response1.get(0)).getId().equals("LISTA_RECURSOS"));
	   assertTrue(((ReportFieldTO)response1.get(0)).getReportFieldType().equals(ReportFieldTO.TYPE_STRING));
	   assertTrue(((ReportFieldTO)response1.get(1)).getId().equals("PROJECT_ID"));
	   assertNull(((ReportFieldTO)response1.get(1)).getReportFieldType());

 	   //normal content with empty braces	   
 	   String content2 = "select sum((rt.estimated_time/60) * r.cost_per_hour) as value, r.id " +
   			"from resource_task rt, resource r " +
   			"where rt.resource_id = r.id " +
   			"and rt.project_id = r.project_id " +
   			"and rt.id = ?#ESCOLHA_RECURSO_1{}# or rt.id = ?#ESCOLHA_RECURSO_2{  }(1)# " +
   			"and rt.project_id=?#PROJECT_ID#" +   			
   			"and rt.id = ?#ESCOLHA_RECURSO_1{0 | Fulano | 1 | Sicrano}(1)# ";
 	   response2 = bus.getReportFields(content2);
 	   assertTrue(response2.size()==4);
 	   assertTrue(((ReportFieldTO)response2.get(0)).getId().equals("ESCOLHA_RECURSO_1"));
 	   assertTrue(((ReportFieldTO)response2.get(0)).getLabel().equals(""));
 	   assertTrue(((ReportFieldTO)response2.get(1)).getLabel().equals(""));
 	   assertTrue(((ReportFieldTO)response2.get(2)).getId().equals("PROJECT_ID"));
 	   assertTrue(((ReportFieldTO)response2.get(3)).getId().equals("ESCOLHA_RECURSO_1")); 	   
	   
 	   //content with invalid fields (open token without closer)
 	   String content3 = "select sum((rt.estimated_time/60) * r.cost_per_hour) as value, r.id " +
			"from resource_task rt, resource r " +
			"and rt.id = ?#ESCOLHA_RECURSO_2{} or rt.id = ?#ESCOLHA_RECURSO_2{  }(1)# ";
 	   response3 = bus.getReportFields(content3);
 	   assertTrue(response3.size()==0);
 	  
 	   //content with invalid fields (double closer token) 	   
 	   String content4 = "select sum((rt.estimated_time/60) * r.cost_per_hour) as value, r.id " +
		"from resource_task rt, resource r " +
		"and rt.id = ?#ESCOLHA_RECURSO_1{}(1)";
 	   response4 = bus.getReportFields(content4); 	   
 	   assertTrue(response4.size()==0);
 	   
 	   //normal content with comboSQL sintax 	   
 	   String content5 = "select sum((rt.estimated_time/60) * r.cost_per_hour) as value, r.id " +
		"from resource_task rt, resource r " +
		"and rt.id = ?#RECURSOS{!select name, id from departmenet!}(1)# rt.id = ?#RECURSOS{}(1)#";
 	   response5 = bus.getReportFields(content5); 	   
 	   assertTrue(response5.size()==2);
 	   assertTrue(((ReportFieldTO)response5.get(0)).getLabel().equals("!select name, id from departmenet!"));
 	   assertTrue(((ReportFieldTO)response5.get(1)).getLabel().equals(""));
 	   assertTrue(((ReportFieldTO)response5.get(0)).getId().equals("RECURSOS"));
 	   
 	   //normal content with comboSQL with inner fild into it
 	   String content6 = "select sum((rt.estimated_time/60) * r.cost_per_hour) as value, r.id " +
		"from resource_task rt, resource r " +
		"and rt.id = ?#RECURSOS{!select name, id from departmenet where project= ?#PROJECT_ID#!}(1)# " +
		"rt.id = ?#ESCOLHA_RECURSO_1{}(1)#";
 	   response6 = bus.getReportFields(content6); 	   
 	   assertTrue(response6.size()==3);
 	   assertTrue(((ReportFieldTO)response6.get(1)).getLabel().equals("!select name, id from departmenet where project= ?#PROJECT_ID#!"));
 	   assertTrue(((ReportFieldTO)response6.get(0)).getId().equals("PROJECT_ID"));
 	   assertTrue(((ReportFieldTO)response6.get(0)).getLabel()==null);
 	   assertTrue(((ReportFieldTO)response6.get(2)).getId().equals("ESCOLHA_RECURSO_1"));

 	   //normal content with comboSQL with inner fild into it
 	   String content7 = "select sum((rt.estimated_time/60) * r.cost_per_hour) as value, r.id " +
		"from resource_task rt, resource r " +
		"and rt.id = ?#RECURSOS{!select id, name from departmenet where project=?#PROJECT_LIST{0|apple|1|orange|2|lemon}(2)# and name is not null!}(1)#";
 	   response7 = bus.getReportFields(content7); 	   
 	   assertTrue(response7.size()==2);
 	   assertTrue(((ReportFieldTO)response7.get(1)).getLabel().equals("!select id, name from departmenet where project=?#PROJECT_LIST{0|apple|1|orange|2|lemon}(2)# and name is not null!"));
 	   assertTrue(((ReportFieldTO)response7.get(0)).getId().equals("PROJECT_LIST"));
 	   assertTrue(((ReportFieldTO)response7.get(0)).getLabel().equals("0|apple|1|orange|2|lemon"));
 	   
 	   
 	   String content11 = "select sum((rt.estimated_time/60)) as value, r.id " +
							"from resource_task rt, resource r " +
							"and rt.id = ?#RECURSOS{!select name, id from departmenet!}(21)# " +
							"and rt.id = ?#resource ID{default value}(7)# " +
							"rt.id = ?#My List{0|huguinho|1|luizinho|2|zezinho}#";
 	   response11 = bus.getReportFields(content11);
 	   assertTrue(response11.size()==3);
 	   assertTrue(((ReportFieldTO)response11.get(0)).getLabel().equals("!select name, id from departmenet!"));
 	   assertNull(((ReportFieldTO)response11.get(0)).getReportFieldType());
 	   assertTrue(((ReportFieldTO)response11.get(1)).getId().equals("resource ID"));
 	   assertTrue(((ReportFieldTO)response11.get(1)).getLabel().equals("default value"));
 	   assertNull(((ReportFieldTO)response11.get(1)).getReportFieldType()); 	   
 	   assertTrue(((ReportFieldTO)response11.get(2)).getId().equals("My List"));
 	   assertTrue(((ReportFieldTO)response11.get(2)).getLabel().equals(""));
 	   assertNull(((ReportFieldTO)response11.get(2)).getReportFieldType()); 	   
	}    
 	
 	/*
 	public void testContainDuplicatedNames(){
 	    this.testGetReportFields();
 	    
 	    assertFalse(bus.containDuplicatedNames(response1));
 	    assertTrue(bus.containDuplicatedNames(response2));
 	    assertFalse(bus.containDuplicatedNames(response3));
 	    assertFalse(bus.containDuplicatedNames(response4));
 	    assertTrue(bus.containDuplicatedNames(response5));
 	   	assertFalse(bus.containDuplicatedNames(response6));
 	  	assertFalse(bus.containDuplicatedNames(response7));
 	 	assertFalse(bus.containDuplicatedNames(response8));
 		assertFalse(bus.containDuplicatedNames(response9));
 		assertTrue(bus.containDuplicatedNames(response10));
 	}
 	*/
}
