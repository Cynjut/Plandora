package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.helper.HtmlUtil;

/**
 * This decorator formats a grid cell with a edit html image.<br>
 * It uses the information from table to assembly the a javascript call.<br>
 * 
 * Use 'property' and 'tag' attributes to set the fields that will be used by decorator such as argument. 
 * In this case the decorator will be return a image representing an 'edit' feature: <i>javascript:edit(id, tag);</i><br>  
 * Ex.: &lt;display:column property="id" tag="myOperation" title="&nbsp;" decorator="com.pandora.gui.taglib.decorator.GridEditDecorator"/&gt; </li>
 * 
 * <br>Anyway, make sure that the image 'edit.gif' must be placed into /web/images
 */
public class GridEditDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
        return decorate(columnValue, "edit");
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */
    public String decorate(Object columnValue, String tag) {
		String image = "";
		String altValue = this.getBundleMessage("label.grid.edit");
		image ="<a href=\"javascript:edit('" + columnValue + "'," + tag + ");\" border=\"0\"> \n";
		image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/edit.gif\" />";
		image += "</a>"; 				
		return image;
    }
    
    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#contentToSearching(java.lang.Object)
     */
    public String contentToSearching(Object columnValue) {
    	return columnValue+"";
    }
    

}
