package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.OccurrenceTO;
import com.pandora.bus.occurrence.Occurrence;
import com.pandora.delegate.OccurrenceDelegate;

/**
 */
public class ProjectGridOccurrenceDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
		OccurrenceDelegate odel = new OccurrenceDelegate();        
		String response = "&nbsp;";
		OccurrenceTO oto = (OccurrenceTO)this.getObject();
		
		if (oto.getSource()!=null){
		    try {
			    Occurrence occ = odel.getKbClass(oto.getSource());
			    response = this.getBundleMessage(occ.getUniqueName(), true);		        
		    } catch(Exception e){
		        response = "err!";
		    }
		}
		return response;        
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
        String content = "";
        try {
            OccurrenceTO oto = odel.getOccurrenceObject(new OccurrenceTO((String)columnValue));
		    Occurrence occ = odel.getKbClass(oto.getSource());
            content = this.getBundleMessage(occ.getUniqueName(), true);
        } catch(Exception e){
            content = "";
        }
        return content;
    }
    
}
