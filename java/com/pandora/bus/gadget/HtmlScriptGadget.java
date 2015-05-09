package com.pandora.bus.gadget;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.pandora.UserTO;
import com.pandora.exception.BusinessException;
import com.pandora.helper.SessionUtil;

public class HtmlScriptGadget extends HtmlGadget {

	public StringBuffer gadgetToHtml(HttpServletRequest request, int width, int height, String loadingLabel) throws BusinessException{
	    StringBuffer content = new StringBuffer("");
	    UserTO uto = SessionUtil.getCurrentUser(request);
	    Vector ids = super.getSelectedIds(uto);

	    String id = getId();

	    super.setWidth(width);
	    content.append(this.generate(ids));
	    
		content.append("<script type=\"text/javascript\">\n");
		content.append("  function callGadget_" + id + "(c, w, h){\n");
		content.append("     window.location='../do/manageResourceHome?operation=prepareForm';\n");	
		content.append("  }\n");	
		content.append("</script>\n");
	    
	    
		return content;
	}
}
