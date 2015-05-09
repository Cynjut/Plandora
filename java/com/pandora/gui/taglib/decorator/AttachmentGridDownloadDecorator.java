package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.AttachmentTO;

public class AttachmentGridDownloadDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
		String buff = "??";
		AttachmentTO ato = (AttachmentTO)this.getObject();
		if (ato!=null) {
			buff = "<a href=\"javascript:download_" + ato.getId() + "();\" border=\"0\"> \n";
			buff += ato.getName();
			buff += "</a>\n"; 
			
			buff += "<script language=\"JavaScript\">\n";
			buff += "   function download_" + ato.getId() + "() {\n";
			buff += "		downloadAttachment('" + ato.getId() + "');\n";
			buff += "   }\n";
			buff += "</script>\n";			
			
		}
		return buff;        
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
