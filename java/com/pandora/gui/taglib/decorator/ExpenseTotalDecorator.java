package com.pandora.gui.taglib.decorator;

import java.util.Locale;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.CostTO;
import com.pandora.ExpenseTO;
import com.pandora.delegate.UserDelegate;
import com.pandora.helper.StringUtil;

public class ExpenseTotalDecorator extends ColumnDecorator {

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
			Long t = null;
			
			if (obj instanceof ExpenseTO) {
				ExpenseTO to = (ExpenseTO)obj;
				t = to.getTotal();
				
			} else if (obj instanceof CostTO) {
				CostTO to = (CostTO)obj;
				t = to.getTotalValue();
			}
	        
			if (t!=null) {
				text = StringUtil.getCurrencyValue((t.floatValue()/100), currLoc);	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return text;
	}

}
