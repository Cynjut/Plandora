package com.pandora.gui.taglib.decorator;

import java.util.Locale;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.InvoiceHistoryTO;
import com.pandora.InvoiceTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.helper.StringUtil;

public class InvoiceTotalDecorator extends ColumnDecorator {

	@Override
	public String decorate(Object columnValue) {
		return getTotal();
	}

	@Override
	public String decorate(Object columnValue, String tag) {
		return decorate(columnValue);
	}

	@Override
	public String contentToSearching(Object columnValue) {
		return getTotal();
	}

	
	private String getTotal() {
		UserDelegate udel = new UserDelegate();
		String text = "";
		
		try {		
			Object obj = this.getObject();
			Locale currLoc = udel.getCurrencyLocale();
			
			if (obj instanceof InvoiceTO) {
				InvoiceTO to = (InvoiceTO)obj;
				if (to!=null) {
			        Integer t = to.getTotal();
				    text = StringUtil.getCurrencyValue((t.floatValue()/100), currLoc);
				}			
				
			} else if (obj instanceof InvoiceHistoryTO) {
				InvoiceHistoryTO to = (InvoiceHistoryTO)obj;
				if (to!=null) {
			        Integer t = to.getTotalPrice();
				    text = StringUtil.getCurrencyValue((t.floatValue()/100), currLoc);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return text;
	}

}
