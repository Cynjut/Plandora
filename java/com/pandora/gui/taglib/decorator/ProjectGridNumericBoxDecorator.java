package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.UserTO;
import com.pandora.helper.HtmlUtil;

/**
 * This decorator formats a customized numeric textBox into grid cell.
 */
public class ProjectGridNumericBoxDecorator extends ColumnDecorator {

    /** Constant used to specify into html form */
    //public final static String COST_PER_HOUR = "COST_PER_HOUR";

    /** Constant used to specify into html form */
    //public final static String CAPACITY_PER_DAY = "CAPACITY_PER_DAY";
    
    
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
		String response = "";
		//LeaderTO eto = this.getLeaderObject();
		
		//if (eto!=null) {
		//	Locale loc = getCurrentLocale();		    
		//    response = this.getTextBox(eto.getId(), eto.getStringCostPerHour(loc), false, "$", tag);    
		//} else if (tag.equals(CAPACITY_PER_DAY) && eto!=null) {
		//    if (eto.getCapacityPerDay()!=null) {
		//        response = this.getTextBox(eto.getId(), eto.getCapacityPerDay().toString(), false, "", tag);    
		//    } else {
		//        response = this.getTextBox(eto.getId(), "", false, "", tag);
		//    }
		//} else {
		    UserTO uto = (UserTO)this.getObject();
		    response = this.getTextBox(uto.getId(), "", false, "", tag);
		//}

		return response;
    }
    
    /*
    private LeaderTO getLeaderObject(){
        LeaderTO response = null;

        if (this.getObject() instanceof LeaderTO) {
            response = (LeaderTO)this.getObject();    
        } else if (this.getObject() instanceof ResourceTO) {
            response = new LeaderTO((ResourceTO)this.getObject()); 
        }
            
        return response;
    }
    */
    
    /**
     * Return a CheckBox html.
     */
    private String getTextBox(String id, String value, boolean disable, String label, String type){
        String name = "tx_" + id + "_" + type + "\"";
        String js = " onClick=\"javaScript:checkNumericValue('" + id + "');\"";
        
        String content = "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><tr><td  class=\"formBody\" nowrap>";
        content = content + label + HtmlUtil.getTextBox(name, value, disable, js);
        content = content + "</td></tr></table>";
        
        if (disable){
            content = content + " <input type=\"hidden\" name=\"" + name + "\" />"; 
        }

        return content;
    }
    
    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#contentToSearching(java.lang.Object)
     */
    public String contentToSearching(Object columnValue) {
    	return columnValue+"";
    }

}
