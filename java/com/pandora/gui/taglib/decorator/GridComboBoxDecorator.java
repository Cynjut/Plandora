package com.pandora.gui.taglib.decorator;

import java.util.Vector;

import org.apache.taglibs.display.ColumnDecorator;
import com.pandora.TransferObject;
import com.pandora.RequirementTO;
import com.pandora.UserTO;
import com.pandora.helper.HtmlUtil;
import com.pandora.delegate.UserDelegate;

public class GridComboBoxDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
        return decorate(columnValue, "");
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */
    public String decorate(Object columnValue, String tag) {
		String response = "&nbsp;";
		//Vector values = new Vector();
		TransferObject to = (TransferObject)this.getObject();
		
		if (tag!=null) {
			String name = "cb_" + to.getId();
			String[] tokens = tag.split(";");
			if (tokens!=null && tokens.length==2) {
				
				name = name + "_" + tokens[0];
				String text = tokens[1];
				
				if (text.startsWith("!")) {
					String sql = text.substring(1, text.length()-1);
					
					String projectId = "";
					if (to instanceof RequirementTO) {
						RequirementTO rto = (RequirementTO) to;
						projectId = rto.getProject().getId();
					}
					UserTO user = (UserTO)this.getPageContext().getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);				
					Vector<TransferObject> options = HtmlUtil.getQueryData(sql, this.getPageContext(), projectId, user.getId());
					response = HtmlUtil.getComboBox(name, options, "textBox", ""+columnValue);
				} else {
					response = HtmlUtil.getComboBox(name, text, "textBox", ""+columnValue, this.getPageContext());		
				}
			} else {
				response = ""+columnValue;
			}
		} else {
			response = ""+columnValue;
		}
		
		return response;
    }
    
    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#contentToSearching(java.lang.Object)
     */
    public String contentToSearching(Object columnValue) {
    	return columnValue+"";
    }    
   
	
}
