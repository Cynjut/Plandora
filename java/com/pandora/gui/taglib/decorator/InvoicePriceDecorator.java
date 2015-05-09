package com.pandora.gui.taglib.decorator;

import java.util.Locale;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.InvoiceItemTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.helper.StringUtil;

public class InvoicePriceDecorator extends ColumnDecorator {

	@Override
	public String decorate(Object columnValue) {
		UserDelegate udel = new UserDelegate();
        String response = "err!";
        String t1 = "", t2 = "";
    	try {
            if (columnValue!=null){
            	
            	InvoiceItemTO ito = (InvoiceItemTO)getObject();
            	Locale loc = udel.getCurrencyLocale();
				
	            Integer val = (Integer)columnValue; 
	            float f = val.floatValue();
	            f = (f / 100) * ito.getTypeIndex().intValue();
	            
	            if (ito.getTypeIndex().intValue()<0){
	            	t1 = "<font color=\"#BB0000\">";
	                t2 = "</font>";
	            }
	            
	            response = t1 + StringUtil.getCurrencyValue(f, loc) + t2;
            }
			
		} catch (BusinessException e) {
			e.printStackTrace();
		}
        return response;
	}

	
	@Override
	public String decorate(Object columnValue, String tag) {
		return decorate(columnValue);
	}
	

	@Override
	public String contentToSearching(Object columnValue) {
    	return columnValue+"";
	}
	
}
