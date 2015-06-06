package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

/**
 * This decorator, replaces the string of color in RGB format (ex.:C1C1C1, AF76DD, etc) 
 * to html square colorized.     
 */
public class GridColorDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
		String column ="&nbsp;";
		
		if (columnValue!=null){
		    String color = (String)columnValue;
		    if (color.length()==6){
		        column = "<center><table border=\"0\" height=\"10\" width=\"15\" cellspacing=\"0\" cellpadding=\"0\">\n" +
		                 "<tr><td class=\"tableCell\" bgcolor=\"#" + color + "\">&nbsp;</td></tr></table></center>\n";
		    }
		} 
		return column;
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
    	return columnValue+"";
    }
    
}
