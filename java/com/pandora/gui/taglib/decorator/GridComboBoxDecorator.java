package com.pandora.gui.taglib.decorator;

import java.util.Vector;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.RequirementTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.helper.HtmlUtil;

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
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public String decorate(Object columnValue, String tag) {
		String response = "&nbsp;";

		UserTO user = (UserTO)this.getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);		
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

					String key = "GRID_COMBOBOX_" + tokens[0] + "_" + projectId + "_" + user.getId();
					Vector<TransferObject> options = (Vector<TransferObject>)super.getSession().getAttribute(key);
					if (options==null) {	
						options = HtmlUtil.getQueryData(sql, this.getSession(), projectId, user.getId());
						super.getSession().setAttribute(key, options);
					}
					response = HtmlUtil.getComboBox(name, options, "textBox", ""+columnValue);						
					
				} else if (text.startsWith("$")) {
					Vector list = (Vector)super.getSession().getAttribute(text.substring(1));
					if (list!=null) {
						response = HtmlUtil.getComboBox(name, list, "textbox", ""+columnValue);
					}
				} else {
					response = HtmlUtil.getComboBox(name, text, "textBox", ""+columnValue, this.getSession());		
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
