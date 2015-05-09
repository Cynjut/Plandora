package com.pandora.bus.gadget;

import java.io.PrintWriter;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pandora.exception.BusinessException;

public class HtmlGadget extends Gadget {

	public void process(HttpServletRequest request,
			HttpServletResponse response, Vector selectedFields)
			throws BusinessException {
		try {
            response.setContentType("text/html");  
            response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);            
            PrintWriter out = response.getWriter();
            String content = "";
			try {
				content = generate(selectedFields);	
			} catch(Exception e) {
				e.printStackTrace();
				content = e.getMessage();
			}
		    out.println(content);
		    
		} catch(Exception e) {
			throw new  BusinessException(e);
		}
	}

	
	public int getWidth(){
		return super.getWidth();
	}
	
	
	public int getHeight(){
		return 150;
	}	
	
	protected String generate(Vector selectedFields) throws BusinessException {
		throw new  BusinessException("This method must be implemented by sub-class.");
	}

	
	public StringBuffer gadgetToHtml(HttpServletRequest request, int width, int height, String loadingLabel) throws BusinessException{
	    StringBuffer content = new StringBuffer("");
	    
	    String id = getId();
	    String name = this.getClass().getName();
	    
		content.append("<div id=\""+ id + "\" />\n");
		
		content.append("<script type=\"text/javascript\">\n");

		content.append("  function callGadget_" + id + "(c, w, h){\n");
		content.append("      document.getElementById('"+ id + "').innerHTML = \"<center><img src='../images/indicator.gif' border='0'></center>\";\n");	
		content.append("      with(document.forms['resourceHomeForm']){\n");		
		content.append("         operation.value = 'renderGadget';\n");	
		content.append("         gagclass.value = '" + name + "';\n");
		content.append("      }\n");	
		content.append("      ajaxProcess(document.forms[\"resourceHomeForm\"], callGadget_" + id + "_callBack, c);\n");
		content.append("      document.getElementById('ajaxResponse').innerHTML = '';\n");  		
		content.append("  }\n");
		
		content.append("  function callGadget_" + id + "_callBack(c){\n");	
		content.append("      if(isAjax()){ \n");
		content.append("         document.getElementById('ajaxResponse').innerHTML = '';\n");  
		content.append("         var content = objRequest.responseText;\n");	
		content.append("         document.getElementById(c).innerHTML = content;\n");
		content.append("      }\n");
		content.append("  }\n");
		content.append("  document.getElementById('"+ id + "').innerHTML = \"<img src='../images/indicator.gif' border='0'>\";\n");		
		content.append("  window.setTimeout(\"callGadget_" + id + "('" + id + "','" + width + "','" + height + "')\", 1000);");
	
		content.append("</script>\n");

		return content;
	}
	
		
}
