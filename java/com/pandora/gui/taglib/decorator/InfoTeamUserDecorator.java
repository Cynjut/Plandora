package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.TeamInfoTO;
import com.pandora.helper.HtmlUtil;

public class InfoTeamUserDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
		String userId ="" + columnValue;
		String urlImg = "../images/emptypic.png";
		if (columnValue!=null){
			urlImg = "../do/login?operation=getUserPic&id=" + userId;
		}

		Object info = getObject();
		String alt = "";
		if (info instanceof TeamInfoTO) {
			alt = HtmlUtil.getHint(((TeamInfoTO)info).getUser().getName());
		}

		return "<center><img width=\"32\" height=\"40\" " + alt + " border=\"0\" src=\"" + urlImg + "\"></center>";
    }
    

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */
    public String decorate(Object columnValue, String tag) {
        return decorate(columnValue);        
    }
    

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#contentToSearching(java.lang.Object)
     */
    public String contentToSearching(Object columnValue) {
    	String content = ""+columnValue;
		Object info = getObject();
		if (info instanceof TeamInfoTO) {
			content = ((TeamInfoTO)info).getUser().getName();
		}
    	return content;
    }

}
