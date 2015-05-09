package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.InvoiceHistoryTO;
import com.pandora.InvoiceStatusTO;
import com.pandora.InvoiceTO;

public class InvoiceStatusDecorator extends ColumnDecorator {

	@Override
	public String decorate(Object columnValue) {
		return getCategoryStatus();
	}

	@Override
	public String decorate(Object columnValue, String tag) {
        return decorate(columnValue);
	}

	
	@Override
	public String contentToSearching(Object columnValue) {
		return getCategoryStatus();
	}
	
	
	private String getCategoryStatus() {
		String text = "";
		Object obj = this.getObject();
		
		if (obj instanceof InvoiceTO) {
			InvoiceTO to = (InvoiceTO)obj;
			if (to!=null) {
				InvoiceStatusTO ito = to.getInvoiceStatus();
			    text = ito.getName();    		    
			}
			
		} else if (obj instanceof InvoiceHistoryTO) {
			InvoiceHistoryTO to = (InvoiceHistoryTO)obj;
			if (to!=null) {
				InvoiceStatusTO ito = to.getInvoiceStatus();
			    text = ito.getName();    		    
			}
		}
	    
		return text;
	}
	
}
