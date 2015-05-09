package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.AttachmentTO;

/**
 * 
 */
public class AttachmentGridTypeDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
		String image = "&nbsp;";

		AttachmentTO ato = (AttachmentTO)this.getObject();
		if (ato.getType()!=null){
		    String file = ato.getType();
		    file = file.replaceAll("\\/", "-");
			image = "<img border=\"0\" src=\"../images/" + file + ".png\" >";
		}
		
		return image;        
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
