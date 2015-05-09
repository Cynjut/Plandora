package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.CategoryTO;
import com.pandora.InvoiceHistoryTO;
import com.pandora.InvoiceTO;

public class InvoiceCategoryDecorator extends ColumnDecorator {

	@Override
	public String decorate(Object columnValue) {
		return getCategoryName();
	}

	@Override
	public String decorate(Object columnValue, String tag) {
		return decorate(columnValue);
	}

	@Override
	public String contentToSearching(Object columnValue) {
		return getCategoryName();
	}

	
	private String getCategoryName() {
		String text = "";
		Object obj = this.getObject();
		
		if (obj instanceof InvoiceTO) {
			InvoiceTO to = (InvoiceTO)obj;
			if (to!=null) {
				CategoryTO cto = to.getCategory();
			    text = cto.getName();    		    
			}
			
		} else if (obj instanceof InvoiceHistoryTO) {
			InvoiceHistoryTO to = (InvoiceHistoryTO)obj;
			if (to!=null) {
				CategoryTO cto = to.getCategory();
			    text = cto.getName();    		    
			}
		}
		
		return text;
	}


}
