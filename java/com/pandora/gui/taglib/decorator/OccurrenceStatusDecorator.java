package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.OccurrenceTO;
import com.pandora.delegate.OccurrenceDelegate;
import com.pandora.exception.BusinessException;

/**
 * 
 */
public class OccurrenceStatusDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {	
		OccurrenceTO oto = (OccurrenceTO)this.getObject();
		return this.getBundleMessage(oto.getStatusLabel(), true);
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
        OccurrenceDelegate odel = new OccurrenceDelegate();
        OccurrenceTO oto = null;
        String response = "";
        
        try {
            oto =  odel.getOccurrenceObject(new OccurrenceTO((String)columnValue));    
        } catch(BusinessException e) {
            oto = null;
        }

        if (oto!=null) {
            response = this.getBundleMessage(oto.getStatusLabel(), true);     
        }
		
        return response;        
    }

}
