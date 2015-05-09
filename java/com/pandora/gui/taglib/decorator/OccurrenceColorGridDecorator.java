package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.OccurrenceTO;
import com.pandora.bus.occurrence.Occurrence;
import com.pandora.helper.HtmlUtil;

public class OccurrenceColorGridDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
		String content = "", img = "empty.gif";
		
		OccurrenceTO oto = (OccurrenceTO)this.getObject();
		img = this.getOccurrenceBallon(oto);
		String label = this.getBundleMessage(oto.getStatusLabel(), true);
		content = "<img border=\"0\" " + HtmlUtil.getHint(label) + " src=\"../images/" + img + "\" >";
		return content;
    }

	public String getOccurrenceBallon(OccurrenceTO oto) {
		String img;
		if (oto.getStatus().equals(Occurrence.STATE_START)) {
		    img = "redballon.gif";
		} else if (oto.getStatus().equals(Occurrence.STATE_FINAL_1) ||
				oto.getStatus().equals(Occurrence.STATE_FINAL_2) ||
		        oto.getStatus().equals(Occurrence.STATE_FINAL_3)) {
		    img = "greenballon.gif";
		} else {
		    img = "yellowballon.gif";
		}
		return img;
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
