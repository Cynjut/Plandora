package com.pandora.gui.taglib.decorator;

import java.util.Iterator;
import java.util.Vector;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.CustomerFunctionTO;
import com.pandora.CustomerTO;
import com.pandora.helper.HtmlUtil;

public class CustomerRoleDecorator extends ColumnDecorator {


	public String decorate(Object columnValue, String tag) {
		String image = "&nbsp;";
		if (this.getObject() instanceof CustomerTO) {
			CustomerTO cto = (CustomerTO) this.getObject();
			String altValue = this.getBundleMessage("label.grid.project.role");
			String key = cto.getId() + "_" + cto.getProject().getId();
			
			String floatPanelContent = this.getRoles(cto, key);
			
			if (floatPanelContent!=null) {
				image = "<a href=\"javascript:void(0);\" onclick=\"openFloatPanel(getContent_" + key + "());\">" +
				"<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/roles.gif\" >" +
				"</a>\n" +
				"<script language=\"JavaScript\">\n" +
				"   function getContent_" + key + "() { \n" +
				"      return \"" + floatPanelContent + "\";\n" +
				"	}\n" +
				"   function checkvalue_" + key + "(opt) { \n" +
				"      var checkBox = document.getElementById(opt); \n" + 
				"      var param = (checkBox.checked?'on':'off');\n" +          		
				"      window.location='../do/manageProject?operation=changeRole&customer=" + cto.getId() + "&proj=" + cto.getProject().getId() + "&func=' + opt + '&param=' + param;\n" +            		
				"	}\n" + 
				"</script>\n";				
			}
		}
		
		return image;
	}

	
	public String decorate(Object columnValue) {
		return decorate(columnValue, "");
	}

	
	public String contentToSearching(Object columnValue) {
		return null;
	}

	
    private String getRoles(CustomerTO cto, String key){
    	String response = null;
    	
    	Vector list = cto.getRoles();
    	
    	if (list!=null) {
    		response = "";
        	Iterator i = list.iterator();
        	while(i.hasNext()) {
        		CustomerFunctionTO cfto = (CustomerFunctionTO)i.next();
        		String title = cfto.getFunct().getName();
        		response = response + "<input type='checkbox' onclick='checkvalue_" + key + "(&quot;" + cfto.getFunct().getId() + "&quot;);' id='" + cfto.getFunct().getId() + "' name='" + title + "'";
        		if (cfto.getCustomer()!=null) {
        			response = response + " checked";
        		}
        		response = response + " value='on'>" + title + "<br/>";    		
        	}    		
    	}
                
        return response;
    }
	
}
