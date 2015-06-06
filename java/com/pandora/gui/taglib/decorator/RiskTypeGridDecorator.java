package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.RiskHistoryTO;
import com.pandora.RiskTO;
import com.pandora.TransferObject;

public class RiskTypeGridDecorator extends ColumnDecorator {

	@Override
	public String decorate(Object columnValue) {
		return decorate(columnValue, null);  
	}

	@Override
	public String decorate(Object columnValue, String tag) {
		return this.contentToSearching(columnValue);
	}

	@Override
	public String contentToSearching(Object columnValue) {
		String response = "&nbsp;";
		
		TransferObject to = (TransferObject)this.getObject();
		response = this.getBundleMessage("label.formRisk.type." + this.getRiskType(to));
		
		return response;
	}
	
	private Integer getRiskType(TransferObject to){
    	Integer response = null;
    	if (to instanceof RiskHistoryTO) {
    		response = ((RiskHistoryTO)to).getRiskType();
    	} else if (to instanceof RiskTO) {
    		response = ((RiskTO)to).getRiskType();
    	}
    	return response;    	
    }

}
