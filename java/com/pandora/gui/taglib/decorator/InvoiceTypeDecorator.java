package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

public class InvoiceTypeDecorator extends ColumnDecorator {

	@Override
	public String decorate(Object columnValue) {	
		return this.getBundleMessage("label.invoiceForm.itemList.type." + columnValue);
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
